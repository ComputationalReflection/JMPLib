package es.uniovi.jmplib.testing.reflection;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassNotChanged;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.IntrospectionUtils;
import jmplib.reflect.Introspector;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ClassStructureNoEvolutionTest {
    /**
     * Ensure that method and field numbers are right if no versioning is done.
     */
    @Test
    public void testMethodCount() {
        DummyClassNotChanged d = new DummyClassNotChanged();
        // java reflection libraries will detect load-time instrumentation added field
        // and methods. This is unavoidable.
        java.lang.reflect.Method[] javaMethods = IntrospectionUtils.filterJMPLibMethods(d.getClass().getMethods());
        java.lang.reflect.Field[] javaFields = IntrospectionUtils.filterJMPLibFields(d.getClass().getFields());

        jmplib.reflect.Method[] jmpLibMethods = Introspector.getClass(d).getMethods();
        jmplib.reflect.Field[] jmpLibFields = Introspector.getClass(d).getFields();

        assertTrue(javaMethods.length == jmpLibMethods.length);
        assertTrue(javaFields.length == jmpLibFields.length);
    }

    /**
     * Ensure that method and field names are right if no versioning is done.
     */
    @Test
    public void testMethodNames() {
        DummyClassNotChanged d = new DummyClassNotChanged();
        // java reflection libraries will detect load-time instrumentation added field
        // and methods. This is unavoidable.
        java.lang.reflect.Method[] javaMethods = IntrospectionUtils.filterJMPLibMethods(d.getClass().getMethods());
        java.lang.reflect.Field[] javaFields = IntrospectionUtils.filterJMPLibFields(d.getClass().getFields());

        jmplib.reflect.Method[] jmpLibMethods = Introspector.getClass(d).getMethods();
        jmplib.reflect.Field[] jmpLibFields = Introspector.getClass(d).getFields();

        for (java.lang.reflect.Method m : javaMethods) {
            if (!StructuralChanges.findMethodByName(m.getName(), jmpLibMethods))
                fail();
        }
        for (java.lang.reflect.Field m : javaFields) {
            if (!StructuralChanges.findFieldByName(m.getName(), jmpLibFields))
                fail();
        }
    }

    /**
     * Ensure that the class name is not modified.
     */
    @Test
    public void testClassName() {
        DummyClassNotChanged d = new DummyClassNotChanged();

        assertTrue(DummyClassNotChanged.class.getName() == Introspector.getClass(d).getName());
    }

    /**
     * Ensure that the declaring class of the members is not changed.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDeclaringClassMembers() {
        DummyClassNotChanged d = new DummyClassNotChanged();

        jmplib.reflect.Class clazz = Introspector.getClass(d);

        try {
            jmplib.reflect.Method mget = clazz.getMethod("getDummyProperty");
            jmplib.reflect.Method mset = clazz.getMethod("setDummyProperty", int.class);
            jmplib.reflect.Field name = clazz.getField("dummyProperty");

            assertTrue(mget.getDeclaringClass() == d.getClass());
            assertTrue(mset.getDeclaringClass() == d.getClass());
            assertTrue(name.getDeclaringClass() == d.getClass());
        } catch (Exception ex) {
            fail();
        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDeclaringClassMembersUsage() {
        DummyClassNotChanged d = new DummyClassNotChanged();

        jmplib.reflect.Class clazz = Introspector.getClass(d);

        try {
            jmplib.reflect.Method mget = clazz.getMethod("getDummyProperty");
            jmplib.reflect.Method mset = clazz.getMethod("setDummyProperty", int.class);
            jmplib.reflect.Field field = clazz.getField("dummyProperty");

            mset.invoke(d, 10);
            int val = (int) mget.invoke(d);
            assertTrue(val == 10);
            int nameVal2 = (int) field.get(d);
            assertTrue(nameVal2 == 10);
            field.set(d, 20);
            int nameVal3 = (int) field.get(d);
            assertTrue(nameVal3 == 20);
            nameVal3 = (int) mget.invoke(d);
            assertTrue(nameVal3 == 20);
        } catch (Exception ex) {
            fail();
        }

    }
}
