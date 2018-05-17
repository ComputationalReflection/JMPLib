package jmplib.util.intercessor;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.Introspector;

/**
 * Class that contain method that converts between types. It is used to deal
 * with methods that both accept objects from the Java reflection library and
 * the JMPLib reflection library, as the JMPLib primitives are created to use a
 * supertype of both so any of them can be accepted in any call.
 * 
 * Every method throw an IllegalArgumentException if the conversion is not
 * possible.
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
@ExcludeFromJMPLib
public class IntercessorTypeConversion {
	/**
	 * Converts from a Type to a jmplib.reflect.Class
	 * 
	 * @param t
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final jmplib.reflect.Class<?> type2Class(Type t) {
		if (t == null)
			return null;
		Class clazz = t.getClass();
		if (clazz == jmplib.reflect.Class.class)
			return (jmplib.reflect.Class) t;
		if (clazz == Class.class)
			return Introspector.decorateClass((Class) t);
		throw new IllegalArgumentException(
				"Only java.lang.Class and jmplib.reflect.Class instances are accepted as Type instances.");
	}

	/**
	 * Converts from a Type to a java Class
	 * 
	 * @param t
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final java.lang.Class<?> type2JavaClass(Type t) {
		if (t == null)
			return null;
		Class clazz = t.getClass();
		if (clazz == jmplib.reflect.Class.class)
			return ((jmplib.reflect.Class) t).getDecoratedClass();
		if (clazz == Class.class)
			return (Class) t;
		throw new IllegalArgumentException(
				"Only java.lang.Class and jmplib.reflect.Class instances are accepted as Type instances.");
	}

	/**
	 * Converts from a Type array to a java Class array
	 * 
	 * @param t
	 * @return
	 */
	public static final java.lang.Class<?>[] type2JavaClass(Type[] types) {
		if (types == null)
			return null;
		java.lang.Class<?>[] ret = new java.lang.Class<?>[types.length];
		for (int i = 0; i < types.length; i++)
			ret[i] = type2JavaClass(types[i]);

		return ret;
	}

	/**
	 * Converts from a Member to a jmplib.reflect.Field
	 * 
	 * @param t
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final jmplib.reflect.Field member2Field(Member t) {
		if (t == null)
			return null;
		Class clazz = t.getClass();
		if (clazz == jmplib.reflect.Field.class)
			return (jmplib.reflect.Field) t;
		if (clazz == Field.class)
			try {
				return Introspector.decorateField((Field) t);
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException("The passed Field instance cannot be found in its declaring class.");
			}
		throw new IllegalArgumentException(
				"Only java.lang.reflect.Field and jmplib.reflect.Field instances are accepted as Member instances.");
	}

	/**
	 * Converts from an AccessibleObject to a jmplib.reflectMethod
	 * 
	 * @param t
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final jmplib.reflect.Method accesibleObject2Method(AccessibleObject t) {
		if (t == null)
			return null;
		Class clazz = t.getClass();
		if (clazz == jmplib.reflect.Method.class)
			return (jmplib.reflect.Method) t;
		if (clazz == java.lang.reflect.Method.class)
			try {
				return Introspector.decorateMethod((java.lang.reflect.Method) t);
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(
						"The passed Method instance cannot be found in its declaring class.");
			}
		throw new IllegalArgumentException(
				"Only java.lang.reflect.Method and jmplib.reflect.Method instances are accepted as AccessibleObject instances.");
	}

	/**
	 * Converts from an array of Package, a jmplib.reflect.Class or a
	 * java.lang.Class to its corresponding import string.
	 * 
	 * @param imports
	 * @return
	 */
	public static final String[] getImportString(AnnotatedElement... imports) {
		if (imports == null)
			return new String[0];
		String[] ret = new String[imports.length];

		for (int i = 0; i < imports.length; i++) {
			if (imports[i].getClass() == Package.class) {
				ret[i] = ((Package) imports[i]).getName() + ".*";
			} else {
				jmplib.reflect.Class<?> cl = type2Class((Type) imports[i]);
				ret[i] = cl.getName();
			}
		}
		return ret;
	}

	/**
	 * Converts from an array of Package, a jmplib.reflect.Class or a
	 * java.lang.Class to its corresponding import string.
	 * 
	 * @param imports
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static final AnnotatedElement[] getImportAnnotatedElementsFromString(String... imports)
			throws ClassNotFoundException {
		AnnotatedElement[] ret = new AnnotatedElement[imports.length];
		String impStr;

		for (int i = 0; i < imports.length; i++) {
			impStr = imports[i].trim().replace("import ", "");
			impStr = impStr.replace(";", "");
			System.out.println(impStr);
			if (impStr.endsWith(".*")) {
				ret[i] = Package.getPackage(impStr.substring(0, impStr.length() - 2));
			} else {
				ret[i] = Class.forName(impStr);
			}
		}
		return ret;
	}
}
