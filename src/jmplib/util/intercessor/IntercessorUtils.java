package jmplib.util.intercessor;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.compiler.ClassCompiler;
import jmplib.compiler.PolyglotAdapter;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.ClassPathUtil;
import jmplib.util.MemberFinder;

/**
 * This class holds all the utility methods of both intercessors, mostly related
 * to checking parameters. It was created as a major refactoring and code
 * optimization process.
 * 
 * @author Jose Manuel Redondo
 *
 */
@ExcludeFromJMPLib
public class IntercessorUtils {
	/**
	 * Compiles a class file and return an instance of the compiled class. Each
	 * class compiled is instrumented by Polyglot. The name and the package of the
	 * class are needed to load the {@link Class} instance.
	 * 
	 * @param file
	 *            The file of the class
	 * @param name
	 *            The name of the class
	 * @param packageName
	 *            The package of the class
	 * @return The instance of the compiled class
	 * @throws CompilationFailedException
	 *             If the class have compilation errors
	 * @throws StructuralIntercessionException
	 *             If some errors ocurrs
	 * @throws RuntimeException
	 *             If there are errors accesing the files, obtaining the class or
	 *             creating the instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T compileFile(File file, String name, String packageName)
			throws CompilationFailedException, StructuralIntercessionException {
		try {
			ClassCompiler.getInstance().compile(ClassPathUtil.getApplicationClassPath(),
					PolyglotAdapter.instrument(file));
		} catch (IOException e) {
			throw new RuntimeException("Errors compiling the code: " + e.getMessage(), e);
		}
		Class<?> invokerClass;
		try {
			invokerClass = Class.forName(packageName + "." + name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Errors obtaining the class: " + e.getMessage(), e);
		}
		T invoker;
		try {
			invoker = (T) invokerClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Errors instantiating the class: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Errors accesing the class: " + e.getMessage(), e);
		}
		return invoker;
	}

	/**
	 * Checks if any method matches the provided functional interface method in the
	 * last version of the class.
	 * 
	 * @param clazz
	 *            The original class
	 * @param name
	 *            The name of the method
	 * @param methodInterface
	 *            The functional interface
	 * @param modifiers
	 *            The modifiers of the method
	 * @param parametrizationClasses
	 *            The classes that parametrize the interface
	 * @throws NoSuchMethodException
	 *             If no method matches the functional interface method in the last
	 *             version of the class
	 */
	public static void checkVisibility(Class<?> clazz, String name, Class<?> methodInterface, int modifiers,
			Class<?>[] parametrizationClasses) throws NoSuchMethodException {
		Class<?> lastVersion = VersionTables.getNewVersion(clazz);
		Method m = null, interfaceMethod = MemberFinder.getMethod(methodInterface);
		Class<?>[] parameters = interfaceMethod.getParameterTypes();
		parameters = MemberFinder.resolveGenericParametersToClass(interfaceMethod, parametrizationClasses);
		if (!Modifier.isStatic(modifiers)) {
			parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
		}
		try {
			m = lastVersion.getMethod(name, parameters);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodException("The method " + name + " "
					+ MethodType.methodType(interfaceMethod.getReturnType(), parameters).toString()
					+ " does not exist in the class " + clazz.getName());
		}
		if (!Modifier.isPublic(m.getModifiers())) {
			throw new IllegalArgumentException("The method " + m.toString() + " is not visible");
		}
	}

	/**
	 * Checks if there is any attribute in the last version of the class that
	 * matches with the data provided and its visibility is public.
	 * 
	 * @param clazz
	 *            The original class
	 * @param name
	 *            The name of the field
	 * @throws NoSuchFieldException
	 *             If the field isn't public or doesn't exist
	 */
	public static void checkVisibility(Class<?> clazz, String name) throws NoSuchFieldException {
		Class<?> lastVersion = VersionTables.getNewVersion(clazz);
		Field f = null;
		try {
			f = lastVersion.getField(name);
		} catch (NoSuchFieldException e) {
			throw new NoSuchFieldException("The field " + name + " does not exist in the class " + clazz.getName());
		}
		if (!Modifier.isPublic(f.getModifiers())) {
			throw new IllegalArgumentException("The field " + f.toString() + " is not visible");
		}
	}

	/**
	 * This method clears the source code cache, so previously modified classes are
	 * not taken into account when performing "unsafe" operations (those that may
	 * produce errors with external classes that call the modified classes, such as
	 * deleting methods or modifying the hierarchy of a class). This is basically
	 * used in unit testing, when tests are independent between them and there is no
	 * reason to recompile previously compiled classes.
	 */
	public static void resetLibraryState() {
		SourceCodeCache.getInstance().clear();
	}

}
