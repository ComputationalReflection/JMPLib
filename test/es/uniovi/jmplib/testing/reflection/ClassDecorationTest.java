package es.uniovi.jmplib.testing.reflection;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassAccess;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.IntrospectionUtils;
import jmplib.reflect.Introspector;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ClassDecorationTest {

	/**
	 * Tests that the methods of the decorator class of the jmplib.reflect library
	 * for classes has an equivalent output to the corresponding ones in the Java
	 * Class class.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@Test
	public void testJMPLibClassDecoration()
			throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException {
		DummyClassAccess d = new DummyClassAccess();

		Class javaClass = d.getClass();
		jmplib.reflect.Class jmpLibClass = Introspector.getClass(d);

		assertTrue(jmpLibClass.equals(javaClass));
		assertTrue(javaClass.toString().equals(jmpLibClass.toString()));
		assertTrue(javaClass.hashCode() == jmpLibClass.hashCode());
		assertTrue(javaClass.getName().equals(jmpLibClass.getName()));
		assertTrue(javaClass.getDeclaredAnnotations() == jmpLibClass.getDeclaredAnnotations());
		assertTrue(javaClass.isSynthetic() == jmpLibClass.isSynthetic());
		assertTrue(javaClass.toGenericString().equals(jmpLibClass.toGenericString()));
		assertTrue(jmpLibClass.forName("java.lang.String").equals(javaClass.forName("java.lang.String")));
		assertTrue(javaClass.getProtectionDomain() == jmpLibClass.getProtectionDomain());
		assertTrue(javaClass.isAssignableFrom(DummyClassAccess.class) == jmpLibClass
				.isAssignableFrom(DummyClassAccess.class));
		assertTrue(javaClass.isInstance(d) == jmpLibClass.isInstance(d));
		assertTrue(javaClass.getModifiers() == jmpLibClass.getModifiers());
		assertTrue(javaClass.isInterface() == jmpLibClass.isInterface());
		assertTrue(javaClass.isArray() == jmpLibClass.isArray());
		assertTrue(javaClass.isPrimitive() == jmpLibClass.isPrimitive());
		assertTrue(jmpLibClass.getSuperclass().equals(javaClass.getSuperclass()));
		assertTrue(jmpLibClass.asSubclass(Object.class).equals(javaClass.asSubclass(Object.class)));
		assertTrue(jmpLibClass.cast(d).equals(javaClass.cast(d)));
		assertTrue(javaClass.desiredAssertionStatus() == jmpLibClass.desiredAssertionStatus());
		assertTrue(javaClass.getAnnotatedInterfaces().length == jmpLibClass.getAnnotatedInterfaces().length);
		assertTrue(javaClass.getAnnotations().length == jmpLibClass.getAnnotations().length);
		assertTrue(javaClass.getCanonicalName().equals(jmpLibClass.getCanonicalName()));
		assertTrue(javaClass.getClassLoader() == jmpLibClass.getClassLoader());
		// assertTrue(javaClass.getClasses().length == jmpLibClass.getClasses().length);
		assertTrue(jmpLibClass.getConstructor(new Class[0])
				.equals(javaClass.getConstructor(new Class[0])));
		assertTrue(javaClass.getConstructors().length == jmpLibClass.getConstructors().length);
		// assertTrue(javaClass.getDeclaredClasses().length ==
		// jmpLibClass.getDeclaredClasses().length);
		assertTrue(jmpLibClass.getDeclaredConstructor(new Class[0])
				.equals(javaClass.getDeclaredConstructor(new Class[0])));
		assertTrue(javaClass.getDeclaredConstructors().length == jmpLibClass.getDeclaredConstructors().length);
		assertTrue(jmpLibClass.getDeclaredField("dummyProperty").equals(javaClass.getDeclaredField("dummyProperty")));
		int c1 =  IntrospectionUtils
				.filterJMPLibFields(javaClass.getDeclaredFields()).length;
		int c2 = jmpLibClass.getDeclaredFields().length;
		assertTrue(String.format("{%d} != {%d}", c1, c2), c1 == c2);
		assertTrue(jmpLibClass.getDeclaredMethod("dummyMethod", new Class[0])
				.equals(javaClass.getDeclaredMethod("dummyMethod", new Class[0])));
		assertTrue(IntrospectionUtils
				.filterJMPLibMethods(javaClass.getDeclaredMethods()).length == jmpLibClass.getDeclaredMethods().length);

		if (javaClass.getDeclaringClass() == null)
			assertTrue(jmpLibClass.getDeclaringClass() == null);
		else
			assertTrue(javaClass.getDeclaringClass().equals(jmpLibClass.getDeclaringClass()));

		if (javaClass.getEnclosingClass() == null)
			assertTrue(jmpLibClass.getEnclosingClass() == null);
		else
			assertTrue(javaClass.getEnclosingClass().equals(jmpLibClass.getEnclosingClass()));

		if (javaClass.getEnclosingConstructor() == null)
			assertTrue(jmpLibClass.getEnclosingConstructor() == null);
		else
			assertTrue(javaClass.getEnclosingConstructor().equals(jmpLibClass.getEnclosingConstructor()));

		if (javaClass.getEnclosingMethod() == null)
			assertTrue(jmpLibClass.getEnclosingMethod() == null);
		else
			assertTrue(javaClass.getEnclosingMethod().equals(jmpLibClass.getEnclosingMethod()));

		if (javaClass.getEnumConstants() == null)
			assertTrue(jmpLibClass.getEnumConstants() == null);
		else
			assertTrue(javaClass.getEnumConstants().equals(jmpLibClass.getEnumConstants()));

		assertTrue(jmpLibClass.getField("dummyProperty").equals(javaClass.getField("dummyProperty")));
		assertTrue(
				IntrospectionUtils.filterJMPLibFields(javaClass.getFields()).length == jmpLibClass.getFields().length);
		assertTrue(javaClass.getGenericInterfaces().length == jmpLibClass.getGenericInterfaces().length);
		assertTrue(jmpLibClass.getGenericSuperclass().equals(javaClass.getGenericSuperclass()));
		// assertTrue(javaClass.getInterfaces().length ==
		// jmpLibClass.getInterfaces().length);
		assertTrue(jmpLibClass.getMethod("dummyMethod").equals(javaClass.getMethod("dummyMethod")));
		assertTrue(IntrospectionUtils.filterJMPLibMethods(javaClass.getMethods()).length == jmpLibClass
				.getMethods().length);
		assertTrue(javaClass.getPackage() == jmpLibClass.getPackage());

		if (javaClass.getSigners() == null)
			assertTrue(jmpLibClass.getSigners() == null);
		else
			assertTrue(javaClass.getSigners().equals(jmpLibClass.getSigners()));

		assertTrue(javaClass.getSimpleName().equals(jmpLibClass.getSimpleName()));
		assertTrue(javaClass.getTypeName() == jmpLibClass.getTypeName());
		assertTrue(javaClass.getTypeParameters().length == jmpLibClass.getTypeParameters().length);
		assertTrue(javaClass.isAnnotation() == jmpLibClass.isAnnotation());
		assertTrue(javaClass.isAnonymousClass() == jmpLibClass.isAnonymousClass());
		assertTrue(javaClass.isEnum() == jmpLibClass.isEnum());
		assertTrue(javaClass.isLocalClass() == jmpLibClass.isLocalClass());
		assertTrue(javaClass.isMemberClass() == jmpLibClass.isMemberClass());
		assertTrue(jmpLibClass.newInstance().getClass().equals(javaClass.newInstance().getClass()));
		assertTrue(jmpLibClass.getDecoratedClass().equals(javaClass));
	}

	/**
	 * Tests that the methods of the decorator class of the jmplib.reflect library
	 * for fields has an equivalent output to the corresponding ones in the java
	 * Field class.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testJMPLibFieldDecoration() throws SecurityException, NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DummyClassAccess d = new DummyClassAccess();

		java.lang.reflect.Field javaField = d.getClass().getField("dummyProperty");
		jmplib.reflect.Field jmpLibField = Introspector.getClass(d).getField("dummyProperty");

		assertTrue(javaField.get(d).equals(jmpLibField.get(d)));
		assertTrue(jmpLibField.equals(javaField));
		assertTrue(javaField.toString().equals(jmpLibField.toString()));
		assertTrue(javaField.hashCode() == jmpLibField.hashCode());
		assertTrue(javaField.getModifiers() == jmpLibField.getModifiers());
		assertTrue(javaField.getInt(d) == jmpLibField.getInt(d));
		assertTrue(javaField.getName().equals(jmpLibField.getName()));
		assertTrue(javaField.getDeclaredAnnotations() == jmpLibField.getDeclaredAnnotations());
		assertTrue(javaField.getDeclaringClass() == jmpLibField.getDeclaringClass());
		assertTrue(javaField.isSynthetic() == jmpLibField.isSynthetic());
		assertTrue(javaField.toGenericString().equals(jmpLibField.toGenericString()));
		assertTrue(javaField.getGenericType().equals(jmpLibField.getGenericType()));
		assertTrue(javaField.getType().equals(jmpLibField.getType()));
		assertTrue(javaField.isEnumConstant() == jmpLibField.isEnumConstant());
		assertTrue(javaField.isAccessible() == jmpLibField.isAccessible());
	}

	/**
	 * Tests that the methods of the decorator class of the jmplib.reflect library
	 * for methods has an equivalent output to the corresponding ones in the java
	 * Method class.
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testJMPLibMethodDecoration() throws SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DummyClassAccess d = new DummyClassAccess();

		java.lang.reflect.Method javaMethod = d.getClass().getMethod("dummyMethod");
		jmplib.reflect.Method jmpLibMethod = Introspector.getClass(d).getMethod("dummyMethod");

		assertTrue(javaMethod.invoke(d).equals(jmpLibMethod.invoke(d)));
		assertTrue(jmpLibMethod.equals(javaMethod));
		assertTrue(javaMethod.toString().equals(jmpLibMethod.toString()));
		assertTrue(javaMethod.hashCode() == jmpLibMethod.hashCode());
		assertTrue(javaMethod.getModifiers() == jmpLibMethod.getModifiers());
		assertTrue(javaMethod.getName().equals(jmpLibMethod.getName()));
		assertTrue(javaMethod.getDeclaringClass() == jmpLibMethod.getDeclaringClass());
		assertTrue(javaMethod.getParameterTypes().length == jmpLibMethod.getParameterTypes().length);
		assertTrue(javaMethod.getReturnType().equals(jmpLibMethod.getReturnType()));
		assertTrue(javaMethod.getTypeParameters().length == jmpLibMethod.getTypeParameters().length);
		assertTrue(javaMethod.isDefault() == jmpLibMethod.isDefault());
		assertTrue(javaMethod.isSynthetic() == jmpLibMethod.isSynthetic());
		assertTrue(javaMethod.toGenericString().equals(jmpLibMethod.toGenericString()));
		assertTrue(javaMethod.getParameterCount() == jmpLibMethod.getParameterCount());
		assertTrue(javaMethod.isVarArgs() == jmpLibMethod.isVarArgs());
		assertTrue(javaMethod.getDefaultValue() == jmpLibMethod.getDefaultValue());
		assertTrue(javaMethod.getExceptionTypes().length == jmpLibMethod.getExceptionTypes().length);
		assertTrue(javaMethod.getGenericExceptionTypes().length == jmpLibMethod.getGenericExceptionTypes().length);
		assertTrue(javaMethod.getGenericParameterTypes().length == jmpLibMethod.getGenericParameterTypes().length);
		assertTrue(javaMethod.getGenericReturnType() == jmpLibMethod.getGenericReturnType());
		assertTrue(javaMethod.isBridge() == jmpLibMethod.isBridge());
		assertTrue(javaMethod.getParameters().length == jmpLibMethod.getParameters().length);
		assertTrue(javaMethod.isAccessible() == jmpLibMethod.isAccessible());
	}
}
