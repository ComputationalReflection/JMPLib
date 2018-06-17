package es.uniovi.jmplib.testing.reflection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;

import es.uniovi.jmplib.testing.reflection.classes.DestinationClass;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk2;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk3;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk4;
import es.uniovi.jmplib.testing.reflection.classes.SourceClass;
import es.uniovi.jmplib.testing.reflection.classes.SourceClassBulk;
import es.uniovi.jmplib.testing.reflection.classes.SourceClassBulk2;
import jmplib.DefaultEvaluator;
import jmplib.DefaultIntercessor;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class AddMembersWithReflectionTest {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();
	private static IEvaluator Evaluator = DefaultEvaluator.getInstance();

	/**
	 * Adds a method to a class using the information of the method extracted from
	 * another class, using java reflection classes only.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAddMethodWithJava() {
		Class cl = SourceClass.class;
		DestinationClass dc = new DestinationClass();
		java.lang.reflect.Method m = null;
		try {
			m = cl.getMethod("getName");
		} catch (Exception e) {
			fail();
		}

		try {
			Intercessor.addMethod(DestinationClass.class, m);

			Function<DestinationClass, String> getName = Evaluator.getMethodInvoker(DestinationClass.class, "getName",
					new MemberInvokerData<>(Function.class, DestinationClass.class, String.class));
			String output = getName.apply(dc);
			assertTrue(output.equals("Destination class"));
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Adds a method to a class using the information of the method extracted from
	 * another class, using jmplib extended reflection classes, so we can also add
	 * dynamically added methods.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAddMethodWithJMPLib() {
		jmplib.reflect.Class<?> cl = Introspector.decorateClass(SourceClass.class);
		jmplib.reflect.Method m = null;
		String output;

		try {
			m = cl.getMethod("getName2");
			// Add a compile-time method
			Intercessor.addMethod(DestinationClass.class, m);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		SourceClass sc = new SourceClass();
		try {
			// Add a method to the source
			Intercessor.addMethod(SourceClass.class, new jmplib.reflect.Method("getNameUpperCase",
					MethodType.methodType(String.class), "return this.name.toUpperCase();"));
			Function<SourceClass, String> getNameUpper = Evaluator.getMethodInvoker(SourceClass.class,
					"getNameUpperCase", new MemberInvokerData<>(Function.class, SourceClass.class, String.class));
			output = getNameUpper.apply(sc);
			assertTrue(output.equals("SOURCE CLASS"));
		} catch (Exception e) {
			fail();
		}

		try {
			m = cl.getMethod("getNameUpperCase");
			// Add a dynamic method to the destination using its reflection info
			Intercessor.addMethod(DestinationClass.class, m);
			jmplib.reflect.Class clD2 = Introspector.decorateClass(DestinationClass.class);

			m = clD2.getMethod("getNameUpperCase");
		} catch (Exception e) {
			fail();
		}

		DestinationClass ddc = new DestinationClass();

		try {
			Function<DestinationClass, String> getNameUpper2 = Evaluator.getMethodInvoker(DestinationClass.class,
					"getNameUpperCase", new MemberInvokerData<>(Function.class, DestinationClass.class, String.class));

			String output2 = getNameUpper2.apply(ddc);

			String output3 = (String) m.invoke(ddc);
			assertTrue(output2.equals(output3));
			assertTrue(output2.equals("DESTINATION CLASS"));
		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * Adds a method to a class using the information of the method extracted from
	 * another class, using java reflection classes only.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAddFieldWithJava() {
		Class cl = SourceClass.class;
		DestinationClass dc = new DestinationClass();
		java.lang.reflect.Field f = null;
		try {
			f = cl.getField("field1");
		} catch (Exception e) {
			fail();
		}

		try {
			jmplib.reflect.Field jf = Introspector.decorateField(f);
			jf.setCustomInit("\"newValue\"");
			Intercessor.addField(DestinationClass.class, jf);

			Function<DestinationClass, String> field = Evaluator.getFieldInvoker(DestinationClass.class, "field1",
					new MemberInvokerData<>(Function.class, DestinationClass.class, String.class));
			String output = field.apply(dc);
			assertTrue(output.equals("newValue"));
		} catch (StructuralIntercessionException | NoSuchFieldException e) {
			fail();
		}
	}

	/**
	 * Adds a method to a class using the information of the method extracted from
	 * another class, using jmplib extended reflection classes, so we can also add
	 * dynamically added methods.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAddFieldWithJMPLib() {
		jmplib.reflect.Class<?> cl = Introspector.decorateClass(SourceClass.class);
		jmplib.reflect.Field f = null;

		try {
			f = cl.getField("field2");
			// Add a compile-time field
			Intercessor.addField(DestinationClass.class, f);
		} catch (Exception e) {
			fail();
		}

		SourceClass sc = new SourceClass();
		try {
			// Add a method to the source
			Intercessor.addField(SourceClass.class,
					new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "dynamicField", "10"));
			Function<SourceClass, Integer> dfield = Evaluator.getFieldInvoker(SourceClass.class, "dynamicField",
					new MemberInvokerData<>(Function.class, SourceClass.class, int.class));
			int outInt = dfield.apply(sc);
			assertTrue(outInt == 10);
		} catch (Exception e) {
			fail();
		}

		try {
			f = cl.getField("dynamicField");
			// Add a dynamic method to the destination using its reflection info
			Intercessor.addField(DestinationClass.class, f);
			jmplib.reflect.Class clD2 = Introspector.decorateClass(DestinationClass.class);

			f = clD2.getField("dynamicField");
		} catch (Exception e) {
			fail();
		}

		DestinationClass ddc = new DestinationClass();

		try {
			BiConsumer<DestinationClass, Integer> dfield2set = Evaluator.setFieldInvoker(DestinationClass.class,
					"dynamicField", new MemberInvokerData<>(BiConsumer.class, DestinationClass.class, int.class));

			dfield2set.accept(ddc, 100);

			Function<DestinationClass, Integer> dfield2get = Evaluator.getFieldInvoker(DestinationClass.class,
					"dynamicField", new MemberInvokerData<>(Function.class, DestinationClass.class, int.class));

			int output2 = dfield2get.apply(ddc);

			int output3 = f.getInt(ddc);
			assertTrue(output2 == output3);
			assertTrue(output2 == 100);
		} catch (Exception e) {
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddFieldArray() {
		jmplib.reflect.Class<?> cl = Introspector.decorateClass(SourceClassBulk2.class);
		jmplib.reflect.Field[] f = null;

		try {
			f = cl.getFields();
			Intercessor.addField(DestinationClassBulk.class, f);
		} catch (Exception e) {
			fail();
		}
		DestinationClassBulk sc = new DestinationClassBulk();
		try {
			for (int i = 0; i < 4; i++) {
				BiConsumer<DestinationClassBulk, String> dfield = Evaluator.setFieldInvoker(
						Introspector.decorateClass(DestinationClassBulk.class), "field" + (i + 1),
						new MemberInvokerData<>(BiConsumer.class, DestinationClassBulk.class, String.class));
				dfield.accept(sc, "field" + (i + 1));
			}

			for (int i = 0; i < 4; i++) {
				Function<DestinationClassBulk, String> dfield = Evaluator.getFieldInvoker(
						Introspector.decorateClass(DestinationClassBulk.class), "field" + (i + 1),
						new MemberInvokerData<>(Function.class, DestinationClassBulk.class, String.class));
				String out = dfield.apply(sc);
				assertTrue(out.equals("field" + (i + 1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddMethodArray() {
		jmplib.reflect.Class<?> cl = Introspector.decorateClass(SourceClassBulk.class);
		jmplib.reflect.Method[] f = null;

		try {
			f = cl.getMethods("get\\d{1}");
			Intercessor.addMethod(DestinationClassBulk2.class, f);
		} catch (Exception e) {
			fail();
		}
		DestinationClassBulk2 sc = new DestinationClassBulk2();
		try {
			for (int i = 0; i < f.length; i++) {
				Function<DestinationClassBulk2, String> dmethod = Evaluator.getMethodInvoker(
						DestinationClassBulk2.class, "get" + (i + 1),
						new MemberInvokerData<>(Function.class, DestinationClassBulk2.class, String.class));
				String out = dmethod.apply(sc);
				assertTrue(out.equals("This is the destination name"));
			}
		} catch (Exception e) {
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddFieldAndMethodArray() {
		jmplib.reflect.Class<?> cl = Introspector.decorateClass(SourceClassBulk2.class);
		jmplib.reflect.Field[] f = null;
		jmplib.reflect.Method[] m = null;

		try {
			m = cl.getMethods("getField\\d{1}");
			Intercessor.addMethod(DestinationClassBulk3.class, m);
			fail(); // Cannot compile as there are no fields
		} catch (Exception e) {

		}
		try {
			f = cl.getFields("field\\d{1}");
			Intercessor.addField(DestinationClassBulk3.class, f);
			Intercessor.addMethod(DestinationClassBulk3.class, m);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		DestinationClassBulk3 sc = new DestinationClassBulk3();
		try {
			for (int i = 0; i < f.length; i++) {
				BiConsumer<DestinationClassBulk3, String> smethod = Evaluator.setFieldInvoker(
						DestinationClassBulk3.class, "field" + (i + 1),
						new MemberInvokerData<>(BiConsumer.class, DestinationClassBulk3.class, String.class));
				smethod.accept(sc, "field" + (i + 1));
			}

			for (int i = 0; i < f.length; i++) {
				Function<DestinationClassBulk3, String> dmethod = Evaluator.getMethodInvoker(
						DestinationClassBulk3.class, "getField" + (i + 1),
						new MemberInvokerData<>(Function.class, DestinationClassBulk3.class, String.class));
				String out = dmethod.apply(sc);
				assertTrue(out.equals("field" + (i + 1)));
			}
		} catch (Exception e) {
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReplaceFieldArray() {
		jmplib.reflect.Field f1 = new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "otherField1", "10.0");
		jmplib.reflect.Field f2 = new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "otherField2", "20.0");
		jmplib.reflect.Field f3 = new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "otherField3", "30.0");

		try {
			Intercessor.replaceField(DestinationClassBulk4.class, f1, f2, f3);
		} catch (Exception e) {
			fail();
		}
		DestinationClassBulk4 obj = new DestinationClassBulk4();

		for (int i = 0; i < 3; i++) {
			Function<DestinationClassBulk4, Double> dfield;
			try {
				dfield = Evaluator.getFieldInvoker(DestinationClassBulk4.class, "otherField" + (i + 1),
						new MemberInvokerData<>(Function.class, DestinationClassBulk4.class, Double.class));
				double out = dfield.apply(obj);
				assertTrue(out == (10.0 * (i + 1)));
			} catch (StructuralIntercessionException e) {
				fail();
			}

		}
	}

	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void testReplaceMethodArray() {
		jmplib.reflect.Field f1 = new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "field1", "10.0");
		jmplib.reflect.Field f2 = new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "field2", "20.0");
		jmplib.reflect.Field f3 = new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "field3", "30.0");

		jmplib.reflect.Method m1 = null;
		jmplib.reflect.Method m2 = null;
		jmplib.reflect.Method m3 = null;

		jmplib.reflect.Method mr1 = null;
		jmplib.reflect.Method mr2 = null;
		jmplib.reflect.Method mr3 = null;

		try {
			m1 = new jmplib.reflect.Method("getField1", MethodType.methodType(int.class));
			m2 = new jmplib.reflect.Method("getField2", MethodType.methodType(int.class));
			m3 = new jmplib.reflect.Method("getField3", MethodType.methodType(int.class));

			mr1 = new jmplib.reflect.Method("getField1", MethodType.methodType(double.class), "return (double)field1;");
			mr2 = new jmplib.reflect.Method("getField2", MethodType.methodType(double.class), "return (double)field2;");
			mr3 = new jmplib.reflect.Method("getField3", MethodType.methodType(double.class), "return (double)field3;");
		} catch (StructuralIntercessionException e1) {
			fail();
		}

		HashMap<AccessibleObject, AccessibleObject> map = new HashMap<>();

		map.put(m1, mr1);
		map.put(m2, mr2);
		map.put(m3, mr3);

		try {
			Intercessor.replaceMethod(DestinationClassBulk4.class, map);
			Intercessor.replaceField(DestinationClassBulk4.class, f1, f2, f3);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		DestinationClassBulk4 obj = new DestinationClassBulk4();

		for (int i = 0; i < 3; i++) {
			Function<DestinationClassBulk4, Double> dfield;
			try {
				dfield = Evaluator.getMethodInvoker(DestinationClassBulk4.class, "getField" + (i + 1),
						new MemberInvokerData<>(Function.class, DestinationClassBulk4.class, Double.class));
				double out = dfield.apply(obj);
				assertTrue(out == (10.0 * (i + 1)));
			} catch (StructuralIntercessionException e) {
				fail();
			}

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void cloneClass() {
		IEvaluator ev = DefaultEvaluator.getInstance();
		Class<?> cl = null;

		try {
			cl = ev.createEmptyClass(this.getClass().getPackage().getName(), "Clone");
		} catch (StructuralIntercessionException e) {
			fail();
		}
		jmplib.reflect.Class<?> sourceClass = Introspector.decorateClass(DestinationClassBulk4.class);
		try {
			Intercessor.addPublicInterfaceOf(sourceClass, cl);
		} catch (StructuralIntercessionException e) {
			fail();
		}

		Object obj = null;
		try {
			obj = cl.newInstance();
		} catch (Exception e1) {
			fail();
		}

		for (int i = 0; i < 3; i++) {
			BiConsumer dfield;
			try {
				dfield = Evaluator.setFieldInvoker(obj.getClass(), "field" + (i + 1),
						new MemberInvokerData<>(BiConsumer.class, obj.getClass(), Integer.class));
				dfield.accept(obj, (10 * (i + 1)));
			} catch (StructuralIntercessionException e) {
				fail();
			}
		}
		for (int i = 0; i < 3; i++) {
			Function dMethod;
			try {
				dMethod = Evaluator.getMethodInvoker(obj.getClass(), "getField" + (i + 1),
						new MemberInvokerData<>(Function.class, obj.getClass(), Integer.class));
				int out = (Integer) dMethod.apply(obj);
				assertTrue(out == (10 * (i + 1)));
			} catch (StructuralIntercessionException e) {
				fail();
			}

		}
	}

	@Test
	public void createEmptyClass() {
		IEvaluator ev = DefaultEvaluator.getInstance();
		Class<?> cl = null;

		try {
			cl = ev.createEmptyClass(this.getClass().getPackage().getName(), "Empty");
		} catch (StructuralIntercessionException e) {
			fail();
		}
		assertTrue(cl.getSimpleName().equals("Empty"));
	}

}
