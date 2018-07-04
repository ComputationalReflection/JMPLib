package jmplib.compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.CompilationFailedException;
import jmplib.util.ClassPathUtil;
import jmplib.util.FileUtils;
import jmplib.util.JavaSourceFromString;
import polyglot.main.Main.TerminationException;
import polyglot.util.ErrorInfo;
import polyglot.util.SilentErrorQueue;

/**
 * This class fits the functionality of Polyglot inside the library.
 *
 * @author Ignacio Lagartos, Jose Manuel Redondo
 * @version 1.1 (Redondo) Major refactoring and performance boost with
 * StringBuffer. Fixed polygloth arguments are now constant, and not
 * recreated each instrument call. This should decrease memory usage
 * and improve performance. Added assertion support flag to Polygloth,
 * so code with the assert keyword can be now processed. Added mandatory
 * buffers to file writer operations.
 */
@ExcludeFromJMPLib
public class PolyglotAdapter {
    private static boolean DEBUG = true;
    private final static List<File> classPath = ClassPathUtil.getApplicationClassPath();
    private final static String[] fixedPolyglothArgs = {"-c", "-extclass", "polyglot.ext.jl7.JL7ExtensionInfo",
            "-simpleoutput", "-classpath"};//, "-assert" };

    // Single polygloth compiler instance running all the time: Unfeasible as this instance do not seem to support reentries.
    private final static polyglot.main.Main polyglothCompilerInstance = new polyglot.main.Main();

    private static String[] createPolyglotArguments(File[] files) {
        StringBuilder argPathSB = new StringBuilder();
        for (File file : classPath) {
            argPathSB.append(file.getAbsolutePath());
            argPathSB.append(JMPlibConfig.getInstance().getPathSeparator());
        }
        String argPath = argPathSB.substring(0, argPathSB.length() - 1);

        int fixedArgsLength = fixedPolyglothArgs.length;
        // Make arguments for polyglot
        String[] args = new String[files.length + fixedArgsLength + 1];
        // Efficient copy of fixed arguments
        System.arraycopy(fixedPolyglothArgs, 0, args, 0, fixedArgsLength);
        args[fixedArgsLength] = argPath;

        for (int i = 0; i < files.length; i++) {
            args[i + fixedArgsLength + 1] = files[i].getAbsolutePath();
        }
        return args;
    }

    /**
     * Instrument with Polyglot the files provided
     *
     * @param files The source files of the new classes
     * @return The files instrumented
     * @throws CompilationFailedException If any errors in the files
     */
    public static JavaSourceFromString[] instrument(File... files) throws CompilationFailedException {
        String[] args = createPolyglotArguments(files);

        SilentErrorQueue errorQueue = new SilentErrorQueue(100, "errors");
        JavaSourceFromString[] sources;
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            synchronized (polyglothCompilerInstance) {
                try {
                    //System.out.println(ClassPathUtil.getApplicationClassPath());
                    //sources = new polyglot.main.Main().start(args, errorQueue).toArray(new JavaSourceFromString[0]);
                    sources = polyglothCompilerInstance.start(args, errorQueue).toArray(new JavaSourceFromString[0]);
                } catch (TerminationException e) {
                    String error = getError(errorQueue);
                    throw new CompilationFailedException("The compilation of the classes failed.\n" + error, error);
                }
            }
        }
        else {
            try {
                //System.out.println(ClassPathUtil.getApplicationClassPath());
                //sources = new polyglot.main.Main().start(args, errorQueue).toArray(new JavaSourceFromString[0]);
                sources = polyglothCompilerInstance.start(args, errorQueue).toArray(new JavaSourceFromString[0]);
            } catch (TerminationException e) {
                String error = getError(errorQueue);
                throw new CompilationFailedException("The compilation of the classes failed.\n" + error, error);
            }
        }
        for (int i = 0; i < sources.length; i++) {
            String name = files[i].getName().replaceAll("\\.java", "");
            sources[i] = new JavaSourceFromString(name, sources[i].getCode(), files[i].getAbsolutePath().hashCode());
            if (DEBUG) {
                try {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(files[i])));
                    //FileWriter writer = new FileWriter(files[i]);
                    writer.write(sources[i].getCode());
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
        return sources;
    }

    /**
     * Build the compilation errors
     *
     * @param errorQueue Errors from Polyglot
     * @return Compilation error message
     */
    private static String getError(SilentErrorQueue errorQueue) {
        StringBuilder error = new StringBuilder();
        for (ErrorInfo errorInfo : errorQueue)
            error.append(parseErrorInfo(errorInfo));

        return error.toString();
    }

    /**
     * Extracts data from Polyglot error
     *
     * @param errorInfo Polyglot error
     * @return line with the error data
     */
    private static String parseErrorInfo(ErrorInfo errorInfo) {
        String message = errorInfo.getMessage();
        int line = errorInfo.getPosition().line();
        int startColumn = errorInfo.getPosition().column();
        int endColumn = errorInfo.getPosition().endColumn();
        String file = errorInfo.getPosition().file();
        String errorFormat = "\n- %s (%s: line %s, columns %s-%s)\n\t\t%s";
        return String.format(errorFormat, message, file.substring(file.lastIndexOf("\\") + 1), line,
                startColumn, endColumn, file);
    }

    static {
        File polyglotFolder = new File(JMPlibConfig.getInstance().getPolyglotPath());
        if (polyglotFolder.exists())
            FileUtils.deleteFile(polyglotFolder);
    }

}
