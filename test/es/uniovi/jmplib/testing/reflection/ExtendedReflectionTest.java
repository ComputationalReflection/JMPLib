package es.uniovi.jmplib.testing.reflection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassAccess;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.Introspector;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ExtendedReflectionTest {

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	@Test
	public void testGetMethodSourceCode() {
		DummyClassAccess d = new DummyClassAccess();

		Class javaClass = d.getClass();
		jmplib.reflect.Class jmpLibClass = Introspector.getClass(d);

		try {
			jmplib.reflect.Method method = jmpLibClass.getMethod("dummyMethod");
			String source = method.getSourceCode();
			assertTrue(source.contains("return dummyProperty;"));

		} catch (NoSuchMethodException e) {
			fail();
		} catch (SecurityException e) {
			fail();
		} catch (IllegalAccessException e) {
			fail();
		}

	}
}
