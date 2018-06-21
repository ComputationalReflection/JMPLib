package es.uniovi.jmplib.testing.reflection;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassAccess;
import es.uniovi.jmplib.testing.reflection.classes.ITest;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.Introspector;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ExtendedReflectionTest {

    @SuppressWarnings({"unused", "unchecked", "rawtypes"})
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

    @Ignore
    @Test
    public void testGetInterfaceSourceCode() {
        try {
            jmplib.reflect.Class cl3 = Introspector.decorateClass(ITest.class);
            System.out.println(cl3.getSourceCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
