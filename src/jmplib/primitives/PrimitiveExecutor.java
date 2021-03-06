package jmplib.primitives;

import jmplib.agent.UpdaterAgent;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.compiler.ClassCompiler;
import jmplib.compiler.PolyglotAdapter;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.ClassContentSerializer;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.ClassPathUtil;
import jmplib.util.JavaSourceFromString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;

/**
 * Class to run all the primitives it enqueues.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public class PrimitiveExecutor {

    protected Queue<Primitive> primitives = null;
    Deque<Primitive> executedPrimitives = new ArrayDeque<>();
    Set<ClassContent> classContents = new HashSet<>();
    boolean safeChange = true;

    public PrimitiveExecutor(Primitive primitive) {
        if (primitive == null) {
            throw new RuntimeException("The primitive cannot be null");
        }
        primitives = new LinkedList<Primitive>();
        primitives.add(primitive);
    }

    public PrimitiveExecutor(Queue<Primitive> primitives) {
        if (primitives.isEmpty()) {
            throw new RuntimeException("The primitive list cannot be empty");
        }
        this.primitives = primitives;
    }

    /**
     * Run all stored primitives
     *
     * @throws StructuralIntercessionException If an error occurs when executing the primitives.
     */
    void runAllPrimitives() throws StructuralIntercessionException {
        // Execute each primitive
        while (!primitives.isEmpty()) {
            Primitive primitive = primitives.poll();
            // Store the affected ClassContents
            classContents.addAll(primitive.execute());
            // Store the primitive in the stack of executed
            executedPrimitives.push(primitive);
            safeChange &= primitive.isSafe();
            // setClassContentsUpdated();
        }
        makeChangesEffective();
    }

    /**
     * Executes all primitives in order. If an error happens all primitives are
     * undone in inverse order. The new versions are compiled and the last
     * versions are redirected to the new version.
     *
     * @throws StructuralIntercessionException If an error occurs when executing the primitives.
     */
    public void executePrimitives()
            throws StructuralIntercessionException {
        try {
            runAllPrimitives();
        } catch (StructuralIntercessionException e) {
            // If the primitive fails, undo the changes of all primitive
            // executed previously
            e.printStackTrace();
            undoChanges();
            throw e;
        }
    }

    /**
     * Serializes the classes in the file system for Polyglot instrumentation.
     * When Polyglot finishes, the Java Compiler compile all the files and all
     * classes are loaded, instrumented and old version classes are redirected
     * to the new ones.
     *
     * @throws StructuralIntercessionException
     */
    void makeChangesEffective() throws StructuralIntercessionException {
        try {
            // Serialize the ClassContents to files
            File[] files = null;
            if (safeChange)
                files = ClassContentSerializer.serialize(classContents);
            else
                files = ClassContentSerializer.serialize(SourceCodeCache
                        .getInstance().getAll());

            //System.out.println(files.length);
            // Instrument with Polyglot
            // files = PolyglotAdapter.instrument(files);
            JavaSourceFromString[] instrumented = PolyglotAdapter
                    .instrument(files);
            if (!safeChange)
                instrumented = filterInstrumented(instrumented);

            //instrumented = filterNewfiles(instrumented);

            // Compile the new java files
            ClassCompiler.getInstance().compile(
                    ClassPathUtil.getApplicationClassPath(), instrumented);
            // Update the VersionTable with the new Classes
            updateVersionTable();
            // Update original class references
            updateReferences();
        } catch (IOException e) {
            throw new StructuralIntercessionException("An compilation error occurred when evolving the classes. \n" +
                    "Did you forget to run the jmplib agent? (-javaagent:./lib/jmplib.jar)\n" + e.getMessage(), e);
        } catch (CompilationFailedException e) {
            throw new StructuralIntercessionException("An compilation error occurred when evolving the classes. \n" +
                    "Did you forget to run the jmplib agent? (-javaagent:./lib/jmplib.jar)\n" + e.getCompilationError(), e);
        }
    }

    static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception ex) {
        }
    }

    private String generateContentHash(String content) {
        if (digest == null) return content;

        byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private JavaSourceFromString[] filterNewfiles(JavaSourceFromString[] instrumented) {
        List<JavaSourceFromString> filtered = new ArrayList<>();
        File ftemp;
        String ftxt1;
        String ftxt2 = null;

        for (JavaSourceFromString file : instrumented) {
            ftemp = file.getFileData();
            Path path = ftemp.toPath();

            if (ftemp.exists()) {
                ftxt1 = generateContentHash(file.getCode());
                try {
                    ftxt2 = Files.getAttribute(path, "contentHash").toString();
                } catch (Exception ex) {
                    try {
                        Files.setAttribute(path, "contentHash", generateContentHash(file.getCode()));
                    } catch (Exception ex2) {
                    }
                }
                if (ftxt1.equals(ftxt2)) {
                    System.out.println("Skipped generation of: " + ftxt1);
                    continue;
                }
            } else {
                try {
                    System.out.println("Write metadata to: " + file.getClassName());
                    Files.setAttribute(path, "contentHash", generateContentHash(file.getCode()));
                } catch (Exception ex) {
                }
            }
            filtered.add(file);
        }
        return filtered.toArray(new JavaSourceFromString[0]);
    }

    /**
     * Filter the instrumented java files to compile only the new versions
     *
     * @param instrumented The instrumented files
     * @return Filtered files
     */
    private JavaSourceFromString[] filterInstrumented(
            JavaSourceFromString[] instrumented) {
        List<JavaSourceFromString> filtered = new ArrayList<>();
        for (JavaSourceFromString file : instrumented) {
            for (ClassContent classContent : classContents) {
                if (classContent.getPath().hashCode() == file.getIdentifier()) {
                    filtered.add(file);
                }
            }
        }
        return filtered.toArray(new JavaSourceFromString[0]);
    }

    /**
     * Retransforms all version and original class to point the new version.
     */
    private void updateReferences() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (ClassContent classContent : classContents) {
            Class<?> clazz = classContent.getClazz();
            classes.add(classContent.getClazz());
            classes.addAll(VersionTables.getVersions(classContent.getClazz()));
            classes.remove(VersionTables.getNewVersion(classContent.getClazz()));
            try {
                clazz.getField("_currentClassVersion").setInt(null,
                        classContent.getVersion());
            } catch (IllegalArgumentException | IllegalAccessException
                    | NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(
                        "Errors setting class version attribute: Did you load the JMPLib Agent \n " +
                                "when starting the application (-javaagent:./lib/jmplib.jar)?", e);
            }
        }
        UpdaterAgent.updateClass(classes.toArray(new Class<?>[0]));
    }

    /**
     * Updates version tables to add the new versions of each modified class
     *
     * @throws StructuralIntercessionException
     */
    private void updateVersionTable() throws StructuralIntercessionException {
        for (ClassContent classContent : classContents) {
            // Obtain the new class
            Class<?> newClazz;
            try {
                newClazz = Class.forName(classContent.getClazz().getPackage()
                        .getName()
                        + "."
                        + classContent.getClazz().getSimpleName()
                        + "_NewVersion_" + classContent.getVersion());
            } catch (ClassNotFoundException e) {
                throw new StructuralIntercessionException(
                        "The new version cannot be found", e);
            }
            // Set the new class version
            VersionTables.addNewVersion(classContent.getClazz(), newClazz);
            classContent.setUpdated(false);
        }
    }

    /**
     * All executed primitives are undone by inverse order
     *
     * @throws StructuralIntercessionException
     */
    private void undoChanges() throws StructuralIntercessionException {
        while (!executedPrimitives.isEmpty()) {
            Primitive primitive = executedPrimitives.pop();
            primitive.undo();
        }
    }

}
