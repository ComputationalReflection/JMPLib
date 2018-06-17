package jmplib.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;

/**
 * This class manages the classpath of the application adding the generated_bin
 * folder to it. This allows the Java Compiler to use the new auxiliary members
 * in the instrumented classes.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public final class ClassPathUtil {

	private static final String JAVA_HOME = "java.home";

	private static List<File> classPath = null;

	private ClassPathUtil() {
	}

	/**
	 * Provides the modified classpath with generated_bin folder included
	 *
	 * @return The classpath of the application
	 */
	public static List<File> getApplicationClassPath() {
		return classPath;
	}

	static {
		JMPlibConfig config = JMPlibConfig.getInstance();
		Optional<String> javaHome = config.getJavaHome();
		javaHome.ifPresent(path -> System.setProperty(JAVA_HOME, path));
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		File originalClassPathFolder = new File(config.getOriginalClassPath());
		if (classPath == null) {
			Iterable<? extends File> iterable = fileManager.getLocation(StandardLocation.CLASS_PATH);
			@SuppressWarnings("unchecked")
			Iterator<File> iterator = (Iterator<File>) iterable.iterator();
			classPath = new ArrayList<File>();
			while (iterator.hasNext()) {
				File file = (File) iterator.next();
				if (file.getAbsolutePath().equals(originalClassPathFolder.getAbsolutePath())) {
					file = replaceFile(file, config.getModifiedClassPath());
					classPath.add(0, file);
					continue;
				}
				classPath.add(file);
			}
		}
	}

	private static File replaceFile(File file, String newFile) {
		String parent = file.getParent();
		if(parent != null) {
			return new File(parent.concat("/").concat(newFile));
		} else {
			return new File(newFile);
		}
	}

}
