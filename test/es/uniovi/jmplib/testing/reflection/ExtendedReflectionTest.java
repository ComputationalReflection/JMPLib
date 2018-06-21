package es.uniovi.jmplib.testing.reflection;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassAccess;
import es.uniovi.jmplib.testing.reflection.classes.ITest;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Field;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
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

    @Test
    public void testGetInterfaceSourceCode() throws StructuralIntercessionException {
        jmplib.reflect.Class cl3 = Introspector.decorateClass(ITest.class);
        String source = cl3.getSourceCode();
        assertTrue(source.contains("public interface ITest"));
        assertTrue(source.contains("public void method(int param);"));
    }

    @Test
    public void testGetJavaClassSourceCode() throws StructuralIntercessionException {
        jmplib.reflect.Class cl = Introspector.decorateClass(java.util.Vector.class);
        String source = cl.getSourceCode();
        assertTrue(source.contains("public class Vector<E>"));
        assertNotNull(cl.getClassDeclaration());
    }

    @Test
    public void testGetJavaMethodSourceCode() throws StructuralIntercessionException {
        jmplib.reflect.Class cl = Introspector.decorateClass(java.util.Vector.class);
        try {
            Method m = cl.getMethod("capacity");
            String source = m.getSourceCode();
            assertTrue(source.contains("return elementData.length;"));
            assertNotNull(m.getMethodDeclaration());
        }
        catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testGetJavaFieldSourceCode() throws StructuralIntercessionException {
        jmplib.reflect.Class cl = Introspector.decorateClass(java.lang.Math.class);
        try {

            Field f = cl.getField("PI");
            String source = f.getSourceCode();
            assertTrue(source.contains("double PI;"));
            assertNotNull(f.getFieldDeclaration());
        }
        catch (Exception ex) {
            fail();
        }
    }
}
