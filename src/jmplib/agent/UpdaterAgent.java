package jmplib.agent;

import jmplib.agent.impl.*;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.annotations.NoCompatible;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.FileUtils;
import jmplib.util.InheritanceTables;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * UpdateAgent is used to initiate the instrumentation of the application code.
 * This class is referenced to act as Java Agent on premain and agentmain.
 * Through this class, the code of the updated classes is updated to reference
 * the new functionalities.
 *
 * @author Ignacio Lagartos
 */
public class UpdaterAgent {

    /**
     * This attribute allows to transform the bytecode of the classes.
     */
    static private Instrumentation inst = null;

    /**
     * The classes that are instrumentable
     */
    public static Map<Integer, String> instrumentables = new HashMap<>();

    private static List<Class<?>> toRetransform = new ArrayList<>();

    /**
     * This method is call before main method, when the agent is pointed to the JVM
     **/
    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            run("premain", agentArgs, inst);
        } catch (StructuralIntercessionException e) {
            throw new RuntimeException("Intrumentation error", e);
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            run("agentmain", agentArgs, inst);
        } catch (StructuralIntercessionException e) {
            throw new RuntimeException("Intrumentation error", e);
        }
    }

    /**
     * The function of this method is initialize the transformers that will process
     * the classes to update them. This transformers are added to the
     * instrumentation object.
     *
     * @param sourceMethodName Unused in this version
     * @param agentArgs        Unused in this version
     * @param inst             Instrumentation data
     * @throws StructuralIntercessionException If a problem occurs initializing the Agent
     */
    public static void run(String sourceMethodName, String agentArgs, Instrumentation inst)
            throws StructuralIntercessionException {
        // Save a reference to the instrumentor
        UpdaterAgent.inst = inst;
        // Create the transformers
        newTransformerInicialization();
        // Load Data
        loadClasses();
    }

    /**
     * Creates all transformers and initialize the classpaths creating the
     * generated_bin folder
     *
     * @throws StructuralIntercessionException If some problems occur creating the transformers
     */
    private static void newTransformerInicialization() throws StructuralIntercessionException {
        initializeClassPaths();
        OriginalClassLoadTimeTransformer originalLoadTransformer = new OriginalClassLoadTimeTransformer();
        VersionClassLoadTimeTransformer versionLoadTransformer = new VersionClassLoadTimeTransformer();
        ClassFileTransformer externalFieldAccessTransformer = new ExternalFieldAccessTransformer();
        DefaultMethodTransformer defaultMethodTransformer = new DefaultMethodTransformer();
        ChangeWriterTransformer writer = new ChangeWriterTransformer();
        RedirectMethodTransformer redirect = new RedirectMethodTransformer();

        inst.addTransformer(originalLoadTransformer);
        inst.addTransformer(versionLoadTransformer);
        inst.addTransformer(defaultMethodTransformer);
        inst.addTransformer(writer);
        inst.addTransformer(redirect, true);
        inst.addTransformer(externalFieldAccessTransformer, true);
        inst.addTransformer(writer);
    }

    /**
     * Creates the generated_bin folder to allocate the compiled classes and store
     * the modified ones
     *
     * @throws StructuralIntercessionException If some problem occur when initializing the classpaths
     */
    private static void initializeClassPaths() throws StructuralIntercessionException {
        JMPlibConfig config = JMPlibConfig.getInstance();
        File bin = new File(config.getOriginalClassPath());
        File file = new File(config.getModifiedClassPath());
        if (file.exists()) {
            FileUtils.deleteFile(file);
        }
        FileUtils.copyDirectory(bin, file);
    }

    /**
     * Load all classes in the src folder. At the end, all classes are instrumented
     * and ready to use the library functionalities
     */
    private static void loadClasses() {
        String src = JMPlibConfig.getInstance().getOriginalSrcPath();
        Path srcPath = Paths.get(src);
        try (Stream<Path> stream = Files.find(srcPath, 500, (path, attr) -> String.valueOf(path).endsWith(".java"))) {
            stream.forEach(path -> cacheClass(path, srcPath));
        } catch (IOException e) {
            throw new RuntimeException("Error caching classes inside the source path");
        }
        updateClass(toRetransform.toArray(new Class<?>[0]));
    }

    /**
     * Load one class, instrument it, store the source code inside the source code
     * cache and add it to the inheritance tree
     *
     * @param path    The path of the class
     * @param srcPath The path of the src folder
     */
    private static void cacheClass(Path path, Path srcPath) {
        String className = srcPath.relativize(path).toString().replace(File.separatorChar, '.');
        if (className.endsWith(".java"))
            className = className.substring(0, className.length() - ".java".length());

        if (className.endsWith("package-info")) {
            return;
        }
        try {
            Class<?> clazz = Class.forName(className, false, UpdaterAgent.class.getClassLoader());
            if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum()) {
                return;
            }
            if (clazz.isAnnotationPresent(ExcludeFromJMPLib.class)) {
                return;
            }
            if (clazz.isAnnotationPresent(NoCompatible.class)) {
                toRetransform.add(clazz);
                return;
            }
            SourceCodeCache.getInstance().getClassContent(clazz);
            InheritanceTables.put(clazz.getSuperclass(), clazz);
            toRetransform.add(clazz);
        } catch (ClassNotFoundException | StructuralIntercessionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error caching classes inside the source path");
        }
    }

    /**
     * This method dispatches the class received to the instrumentor in order to
     * re-transform it in run time.
     *
     * @param clazz The classes to re-transform
     */
    public static void updateClass(Class<?>... clazz) {
        try {
            inst.retransformClasses(clazz);
        } catch (Throwable t) {
            throw new RuntimeException("Error retransforming classes: " + t.getMessage(), t);
        }
    }

}
