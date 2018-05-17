package jmplib.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import jmplib.classversions.VersionTables;

public class IntrospectionUtils {

	private static final List<String> jmpLibMethodNames = Arrays.asList("_transferState", "get_OldVersion",
			"set_OldVersion", "get_ObjCreated", "get_NewVersion", "set_NewVersion", "get_CurrentInstanceVersion",
			"set_CurrentInstanceVersion");
	private static final List<String> jmpLibMethodPostFix = Arrays.asList("_invoker", "_creator", "_fieldGetter",
			"_fieldSetter", "_unary", "Static_getter", "Static_setter");

	private static final List<String> jmpLibFieldNames = Arrays.asList("_oldVersion", "_newVersion",
			"_currentInstanceVersion", "_currentClassVersion", "_objCreated");

	private static final List<String> jmpLibInterfaceNames = Arrays.asList("jmplib.classversions.VersionClass");

	private static final List<String> jmpLibAnnotationNames = Arrays.asList("jmplib.annotations.NoRedirect",
			"jmplib.annotations.AuxiliaryMethod");

	/**
	 * Determines if this annotation is an annotation added by JMPLib
	 * 
	 * @param m
	 * @return
	 */
	public static final boolean isJmpLibAddedAnnotation(Annotation n) {
		return jmpLibAnnotationNames.contains(n.annotationType().getName());
	}

	/**
	 * Determines if this method is a method added by JMPLib
	 * 
	 * @param m
	 * @return
	 */
	public static final boolean isJmpLibAddedMethod(Method m) {
		return jmpLibMethodNames.contains(m.getName());
	}

	/**
	 * Determines if this method is a method added by JMPLib
	 * 
	 * @param m
	 * @return
	 */
	public static final boolean isJmpLibAddedInterface(Class<?> c) {
		return jmpLibInterfaceNames.contains(c.getName());
	}

	public static final boolean isJmpLibAddedMethod(java.lang.reflect.Method m) {
		return jmpLibMethodNames.contains(m.getName());
	}

	/**
	 * Determines if this field is a field added by JMPLib
	 * 
	 * @param m
	 * @return
	 */
	public static final boolean isJmpLibAddedField(Field m) {
		return jmpLibFieldNames.contains(m.getName());
	}

	public static final boolean isJmpLibAddedField(java.lang.reflect.Field m) {
		return jmpLibFieldNames.contains(m.getName());
	}

	/**
	 * Finds a method in a method list using its name
	 * 
	 * @param name
	 * @param methodList
	 * @return
	 */
	private static final boolean findMethodByName(String name, Method[] methodList) {
		for (Method m : methodList) {
			if (name.equals(m.getName()))
				return true;
		}
		return false;
	}

	private static final boolean findMethodByName(String name, java.lang.reflect.Method[] methodList) {
		for (java.lang.reflect.Method m : methodList) {
			if (name.equals(m.getName()))
				return true;
		}
		return false;
	}

	/**
	 * Tests if a method is a JMPLib invoker or creator
	 * 
	 * @param m
	 * @param methodList
	 * @return
	 */
	public static final boolean isJmpLibInvokerOrCreator(Method m, Method[] methodList) {
		for (String post : jmpLibMethodPostFix) {
			if (m.getName().endsWith(post)) {
				// This avoids reporting as a JMPLib method those ones that originally end with
				// _invoker or other jmplib postfixes
				if (findMethodByName(m.getName() + post, methodList)) {
					// System.out.println("FOUND: " + m.getName() + post);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public static final boolean isJmpLibInvokerOrCreator(java.lang.reflect.Method m,
			java.lang.reflect.Method[] methodList) {
		for (String post : jmpLibMethodPostFix) {
			if (m.getName().endsWith(post)) {
				// This avoids reporting as a JMPLib method those ones that originally end with
				// _invoker or other jmplib postfixes
				if (findMethodByName(m.getName() + post, methodList)) {
					// System.out.println("FOUND: " + m.getName() + post);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param m
	 * @param methodList
	 * @return
	 */
	public static final boolean isJmpLibMethod(Method m, java.lang.reflect.Method[] methodList) {
		return isJmpLibMethod(m, IntrospectionUtils.decorateMethodList(methodList));
	}

	/**
	 * 
	 * @param m
	 * @param methodList
	 * @return
	 */
	public static final boolean isJmpLibMethod(Method m, Method[] methodList) {
		return isJmpLibAddedMethod(m) || isJmpLibInvokerOrCreator(m, methodList);
	}

	public static final boolean isJmpLibMethod(java.lang.reflect.Method m, java.lang.reflect.Method[] methodList) {
		return isJmpLibAddedMethod(m) || isJmpLibInvokerOrCreator(m, methodList);
	}

	/**
	 * Removes from the passed method list those methods that are added by JMPLib,
	 * to avoid reflection users to see the inner workings of our library.
	 * 
	 * @param methods
	 * @return
	 */
	public static final Method[] filterJMPLibMethods(Method[] methods) {
		Object[] step1 = Arrays.stream(methods).filter(met -> !isJmpLibMethod(met, methods)).toArray();

		return Arrays.copyOf(step1, step1.length, Method[].class);
	}

	public static final java.lang.reflect.Method[] filterJMPLibMethods(java.lang.reflect.Method[] methods) {
		Object[] step1 = Arrays.stream(methods).filter(met -> !isJmpLibMethod(met, methods)).toArray();

		return Arrays.copyOf(step1, step1.length, java.lang.reflect.Method[].class);
	}

	/**
	 * Removes from the passed field list those fields that are added by JMPLib, to
	 * avoid reflection users to see the inner workings of our library.
	 * 
	 * @param methods
	 * @return
	 */
	public static final Field[] filterJMPLibFields(Field[] fields) {
		Object[] step1 = Arrays.stream(fields).filter(f -> !isJmpLibAddedField(f)).toArray();

		return Arrays.copyOf(step1, step1.length, Field[].class);
	}

	public static final java.lang.reflect.Field[] filterJMPLibFields(java.lang.reflect.Field[] fields) {
		Object[] step1 = Arrays.stream(fields).filter(f -> !isJmpLibAddedField(f)).toArray();

		return Arrays.copyOf(step1, step1.length, java.lang.reflect.Field[].class);
	}

	/**
	 * Removes from the passed interface list those interfaces that are added by
	 * JMPLib, to avoid reflection users to see the inner workings of our library.
	 * 
	 * @param interfs
	 * @return
	 */
	public static final Annotation[] filterJMPLibAnnotations(Annotation[] interfs) {
		Object[] step1 = Arrays.stream(interfs).filter(in -> !isJmpLibAddedAnnotation(in)).toArray();

		return Arrays.copyOf(step1, step1.length, Annotation[].class);
	}

	/**
	 * Removes from the passed annotation list those ones that are added by JMPLib,
	 * to avoid reflection users to see the inner workings of our library.
	 * 
	 * @param interfs
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final Class[] filterJMPLibInterfaces(Class[] interfs) {
		Object[] step1 = Arrays.stream(interfs).filter(in -> !isJmpLibAddedInterface(in)).toArray();

		return Arrays.copyOf(step1, step1.length, Class[].class);
	}

	/**
	 * Decorates a list of java.lang.reflect.Constructor<T> objects.
	 * 
	 * @param methodList
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Constructor<?>[] decorateConstructorList(java.lang.reflect.Constructor<?>[] methodList) {
		Object[] objs = Arrays.stream(methodList).map(met -> new Constructor(met)).toArray();
		return Arrays.copyOf(objs, objs.length, Constructor[].class);
	}

	/**
	 * Decorates a list of java.lang.reflect.Method objects.
	 * 
	 * @param methodList
	 * @return
	 */
	public static final Method[] decorateMethodList(java.lang.reflect.Method[] methodList) {
		Object[] objs = Arrays.stream(methodList).map(met -> new Method(met)).toArray();
		return Arrays.copyOf(objs, objs.length, Method[].class);
	}

	/**
	 * Decorates a list of java.lang.reflect.Field objects.
	 * 
	 * @param fieldList
	 * @return
	 */
	public static final Field[] decorateFieldList(java.lang.reflect.Field[] fieldList) {
		Object[] objs = Arrays.stream(fieldList).map(fld -> new Field(fld)).toArray();
		return Arrays.copyOf(objs, objs.length, Field[].class);
	}

	/**
	 * Decorates a list of java.lang.Class objects.
	 * 
	 * @param classList
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Class<?>[] decorateClassList(java.lang.Class<?>[] classList) {
		Object[] objs = Arrays.stream(classList).map(cl -> new Class(cl)).toArray();
		return Arrays.copyOf(objs, objs.length, Class[].class);
	}

	/**
	 * Eliminates the decorator of list of jmplib.reflect.Class objects.
	 * 
	 * @param classList
	 * @return
	 */
	public static final java.lang.Class<?>[] undecorateClassList(Class<?>[] classList) {
		Object[] objs = Arrays.stream(classList).map(cl -> cl.getDecoratedClass()).toArray();
		return Arrays.copyOf(objs, objs.length, java.lang.Class[].class);
	}

	/**
	 * Pretty prints argument types
	 * 
	 * @param argTypes
	 * @return
	 */
	public static final String argumentTypesToString(java.lang.Class<?>[] argTypes) {
		StringBuilder buf = new StringBuilder();
		buf.append("(");

		if (argTypes != null) {
			for (int i = 0; i < argTypes.length; i++) {
				if (i > 0) {
					buf.append(", ");
				}

				java.lang.Class<?> c = argTypes[i];
				buf.append((c == null) ? "null" : c.getName());
			}
		}

		buf.append(")");
		return buf.toString();
	}

	/**
	 * Helper method to get an instance of the latest class version of a certain
	 * Object.
	 * 
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public static final Object getLatestObjectVersion(Object obj)
			throws NoSuchMethodException, InvocationTargetException {
		java.lang.Class<?> objClass = obj.getClass();

		try {
			java.lang.reflect.Method getCurrentInstanceVersionMethod = objClass.getMethod("get_CurrentInstanceVersion");
			getCurrentInstanceVersionMethod.setAccessible(true);
			int currentInstanceVersion = (int) getCurrentInstanceVersionMethod.invoke(obj);

			java.lang.reflect.Field getCurrentClassVersionField = objClass.getField("_currentClassVersion");
			getCurrentClassVersionField.setAccessible(true);
			int currentClassVersion = (int) getCurrentClassVersionField.get(objClass);

			// Force a version update if necessary
			if (currentInstanceVersion != currentClassVersion) {
				java.lang.Class<?> lastVersionClass = VersionTables.getNewVersion(objClass);
				java.lang.reflect.Method creatorMethod = lastVersionClass.getDeclaredMethod("_creator", objClass);
				creatorMethod.setAccessible(true);
				creatorMethod.invoke(null, obj);
			}
			java.lang.reflect.Method getNewVersionMethod = objClass.getMethod("get_NewVersion");

			Object newVersion = getNewVersionMethod.invoke(obj);
			if (newVersion == null)
				return obj;
			return newVersion;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	/**
	 * Adds the type variable generic declaration (i. e. <T, K> to the passed StringBuffer
	 * @param sb
	 * @param tp
	 */
	public static final void addGenericTypes(StringBuffer sb, TypeVariable<?> [] tp) {
		if (tp == null)
			return;
		if (tp.length == 0)
			return;
		sb.append("<");
		int cont = 0;
		for (TypeVariable<?> t: tp) {
			sb.append(t.getName());
			if (cont != tp.length - 1)
				sb.append(", ");
		}
		sb.append(">");
	}
}
