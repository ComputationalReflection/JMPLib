package es.uniovi.jmplib.testing.reflection;

import es.uniovi.jmplib.testing.reflection.classes.DummyClassToDelete;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.IntrospectionUtils;
import jmplib.reflect.Introspector;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ClassStructureDeleteTest {
    static int numMethodsBefore = 0;
    static int numFieldsBefore = 0;
    static java.lang.reflect.Method[] beforeMethods;
    static java.lang.reflect.Field[] beforeFields;

    /**
     * Prepare the tests storing the original number of field and methods of the class DummyClassToDelete,
     * deleting one attribute and two methods after that.
     */
    @BeforeClass
    public static void setup() {
        beforeMethods = IntrospectionUtils.filterJMPLibMethods(DummyClassToDelete.class.getMethods());
        // Number of methods of the class before changes = class methods - jmplib
        // instrumentation methods
        numMethodsBefore = beforeMethods.length;

        beforeFields = IntrospectionUtils.filterJMPLibFields(DummyClassToDelete.class.getFields());
        numFieldsBefore = beforeFields.length;

        StructuralChanges.deleteField(DummyClassToDelete.class, "dummyPropertyToDelete");
        StructuralChanges.deleteMethod(DummyClassToDelete.class, "getDummyProperty");
        StructuralChanges.deleteMethod(DummyClassToDelete.class, "setDummyProperty");
    }

    /**
     * Ensure that method and field numbers are right after versioning.
     */
    @Test
    public void testMethodCountAfterVersioning() {
        DummyClassToDelete d = new DummyClassToDelete();
        jmplib.reflect.Method[] afterMethods = IntrospectionUtils.filterJMPLibMethods(Introspector.getClass(d).getMethods());
        jmplib.reflect.Field[] afterFields = IntrospectionUtils.filterJMPLibFields(Introspector.getClass(d).getFields());

        assertTrue(numMethodsBefore - 2 == afterMethods.length);
        assertTrue(numFieldsBefore - 1 == afterFields.length);
    }

    /**
     * Ensure that method and field names are right after versioning.
     */
    @Test
    public void testMethodNamesAfterVersioning() {
        DummyClassToDelete d = new DummyClassToDelete();
        jmplib.reflect.Method[] afterMethods = IntrospectionUtils.filterJMPLibMethods(Introspector.getClass(d).getMethods());
        jmplib.reflect.Field[] afterFields = IntrospectionUtils.filterJMPLibFields(Introspector.getClass(d).getFields());


        for (java.lang.reflect.Method m : beforeMethods) {
            if (m.getName().equals("getDummyProperty") || m.getName().equals("setDummyProperty"))
                continue;

            if (!StructuralChanges.findMethodByName(m.getName(), afterMethods))
                fail();
        }
        for (java.lang.reflect.Field m : beforeFields) {
            if (m.getName().equals("dummyPropertyToDelete"))
                continue;
            if (!StructuralChanges.findFieldByName(m.getName(), afterFields)) {
                fail();
            }
        }

        assertTrue(StructuralChanges.findMethodByName("getDummyProperty", afterMethods) == false);
        assertTrue(StructuralChanges.findMethodByName("setDummyProperty", afterMethods) == false);
        assertTrue(StructuralChanges.findFieldByName("dummyPropertyToDelete", afterFields) == false);
    }

    /**
     * Try to access the previously deleted methods directly.
     */
    @SuppressWarnings({"rawtypes", "unused", "unchecked"})
    @Test
    public void testDeclaringClassAddedMembersAfterVersioningUsage() {
        DummyClassToDelete d = new DummyClassToDelete();

        jmplib.reflect.Class clazz = Introspector.getClass(d);

        try {
            jmplib.reflect.Method mget = clazz.getMethod("getDummyProperty");
            fail();
        } catch (NoSuchMethodException ex) {
        }
        try {
            jmplib.reflect.Method mset = clazz.getMethod("setDummyProperty", int.class);
            fail();
        } catch (NoSuchMethodException ex) {
        }
        try {
            jmplib.reflect.Field name = clazz.getField("dummyPropertyToDelete");
            fail();
        } catch (NoSuchFieldException ex) {
        }
    }
}
