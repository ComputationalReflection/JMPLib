package jmplib.compiler;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.CompilationFailedException;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class is used for compile java files at runtime. The class implements a
 * singleton pattern so all the constructors are private. It is possible to
 * create an instance through the {@link ClassCompiler#getInstance()} method.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public class ClassCompiler {

    private static final String JAVA_HOME = "java.home";
    private static final ClassCompiler _instance = new ClassCompiler();
    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    public static final String compilerOptions = "-g";//"-g:none";

    static {
        Optional<String> javaHome = JMPlibConfig.getInstance().getJavaHome();
        javaHome.ifPresent(value -> System.setProperty(JAVA_HOME, value));
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                    Collections.singletonList(new File(JMPlibConfig.getInstance().getOriginalClassPath())));
        }
        catch (IOException ioex) {
            throw new RuntimeException("An error ocurred when initializing JMPLib version generation system: " + ioex.getMessage(), ioex.getCause());
        }
    }
    private ClassCompiler() {
    }

    /**
     * This method compiles multiple java files at runtime. If anyone of those
     * has compilation errors, no one would be compiled.
     *
     * @param classPath The classpath of the application
     * @param files     The java files to be compiled
     * @throws IOException                If the file cannot be accessed
     * @throws CompilationFailedException It's thrown when the file have source code errors.
     */
    public void compile(List<File> classPath, JavaFileObject... files)
            throws CompilationFailedException, IOException {
//        Optional<String> javaHome = JMPlibConfig.getInstance().getJavaHome();
//        javaHome.ifPresent(value -> System.setProperty(JAVA_HOME, value));
//
        Writer errors = new StringWriter();
//        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
//                Collections.singletonList(new File(JMPlibConfig.getInstance().getOriginalClassPath())));
        fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
        // Compile the file
        boolean compiled = compiler.getTask(errors, fileManager, null,
                Collections.singletonList(compilerOptions), null, Arrays.asList(files)).call();
        fileManager.close();
        if (!compiled) {
            throw new CompilationFailedException("The compilation of the classes failed.\n".concat(errors.toString()),
                    errors.toString());
        }
    }

    /**
     * Obtains the instance of the ClassCompiler
     *
     * @return {@link ClassCompiler}
     */
    public static ClassCompiler getInstance() {
        return _instance;
    }

}
