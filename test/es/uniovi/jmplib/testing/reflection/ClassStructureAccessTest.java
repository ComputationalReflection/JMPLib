package es.uniovi.jmplib.testing.reflection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassAccess;
import es.uniovi.jmplib.testing.reflection.classes.DummyClassNotChanged;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.Introspector;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ClassStructureAccessTest {

	/**
	 * Check access to compiled members using our jmplib.reflect library
	 */
	@Test
	public void testCompiledMemberAccess() {
		DummyClassAccess d = new DummyClassAccess();
		DummyClassAccess d2 = new DummyClassAccess();
		try {
			jmplib.reflect.Field f = Introspector.getClass(d).getField("dummyProperty");
			jmplib.reflect.Method m = Introspector.getClass(d).getMethod("dummyMethod");
			f.setInt(d, 10);
			assertTrue((int) m.invoke(d) == 10);
			assertTrue((int) m.invoke(d2) == 0);
		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * Check access to static compiled members using our jmplib.reflect library
	 */
	@Test
	public void testCompiledMemberAccessStatic() {
		DummyClassAccess d = new DummyClassAccess();
		DummyClassAccess d2 = new DummyClassAccess();
		try {
			jmplib.reflect.Field f = Introspector.getClass(d).getField("dummyPropertyStatic");
			jmplib.reflect.Method m = Introspector.getClass(d).getMethod("dummyMethodStatic");
			assertTrue(f.getInt(d) == 0);
			assertTrue((int) m.invoke(d) == 0);
			f.setInt(d, 10);
			assertTrue((int) m.invoke(d) == 10);
			assertTrue((int) m.invoke(d2) == 10);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Check access to non existing members using our jmplib.reflect library
	 */
	@SuppressWarnings("unused")
	@Test
	public void testNonExistingMembersAccess() {
		DummyClassAccess d = new DummyClassAccess();
		try {
			jmplib.reflect.Field f = Introspector.getClass(d).getField("dummyPropertyNA");
			fail();
		} catch (NoSuchFieldException e) {

		}
		try {
			jmplib.reflect.Method m = Introspector.getClass(d).getMethod("dummyMethodNA");
			fail();
		} catch (NoSuchMethodException e) {

		}
	}

	/**
	 * Check access to members added by JMPLib at load time to the classes using our
	 * jmplib.reflect library. These members should be invisible to the users.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testJmpLibMemberAccess() {
		DummyClassNotChanged d = new DummyClassNotChanged();

		try {
			jmplib.reflect.Field f = Introspector.getClass(d).getField("_oldVersion");
			fail();
		} catch (NoSuchFieldException e) {

		}
		try {
			jmplib.reflect.Method m = Introspector.getClass(d).getMethod("get_OldVersion");
			fail();
		} catch (NoSuchMethodException e) {

		}

	}
}
