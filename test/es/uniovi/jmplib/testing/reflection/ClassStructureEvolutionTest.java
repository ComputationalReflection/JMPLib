package es.uniovi.jmplib.testing.reflection;

import es.uniovi.jmplib.testing.reflection.classes.DummyClass;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.reflect.IntrospectionUtils;
import jmplib.reflect.Introspector;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ClassStructureEvolutionTest {
    static int numMethodsBefore = 0;
    static int numFieldsBefore = 0;
    static java.lang.reflect.Method[] beforeMethods;
    static java.lang.reflect.Field[] beforeFields;

    /**
     * Prepare the class DummyClass for the test, adding an attribute and its
     * corresponding getter and setter. This means that the class will have 3
     * additional dynamically added members (1 attribute and 2 methods).
     */
    @BeforeClass
    public static void setup() {
        DummyClass d = new DummyClass();
        beforeMethods = IntrospectionUtils.filterJMPLibMethods(d.getClass().getMethods());
        // Number of methods of the class before changes = class methods - jmplib
        // instrumentation methods
        numMethodsBefore = beforeMethods.length;

        beforeFields = d.getClass().getFields();
        numFieldsBefore = beforeFields.length - StructuralChanges.jmpLibInstrumentFieldNames.size();

        StructuralChanges.addFieldAndGetterSetter(d.getClass(), String.class, "Name", Modifier.PUBLIC);
    }

    /**
     * Ensure that method and field numbers are right after versioning.
     */
    @Test
    public void testMethodCountAfterVersioning() {
        DummyClass d = new DummyClass();
        jmplib.reflect.Method[] afterMethods = Introspector.getClass(d).getMethods();
        jmplib.reflect.Field[] afterFields = Introspector.getClass(d).getFields();

        /*for (jmplib.reflect.Field f: afterFields)
            System.out.println(f.getName());

        System.out.println(numFieldsBefore - 1 + ", " + afterFields.length);*/

        assertTrue(numMethodsBefore + 2 == afterMethods.length);
        assertTrue(numFieldsBefore + 1 == afterFields.length);
    }

    /**
     * Ensure that method and field names are right after versioning.
     */
    @Test
    public void testMethodNamesAfterVersioning() {
        DummyClass d = new DummyClass();
        jmplib.reflect.Method[] afterMethods = Introspector.getClass(d).getMethods();
        jmplib.reflect.Field[] afterFields = Introspector.getClass(d).getFields();

        for (java.lang.reflect.Method m : beforeMethods) {
            if (StructuralChanges.jmpLibInstrumentMethodNames.contains(m.getName()))
                continue;

            if (!StructuralChanges.findMethodByName(m.getName(), afterMethods))
                fail();
        }
        for (java.lang.reflect.Field m : beforeFields) {
            if (StructuralChanges.jmpLibInstrumentFieldNames.contains(m.getName()))
                continue;
            if (!StructuralChanges.findFieldByName(m.getName(), afterFields))
                fail();
        }

        assertTrue(StructuralChanges.findMethodByName("getName", afterMethods));
        assertTrue(StructuralChanges.findMethodByName("setName", afterMethods));
        assertTrue(StructuralChanges.findFieldByName("name", afterFields));
    }

    /**
     * Test that the class name is consistent even if now we have versions.
     */
    @Test
    public void testClassNameAfterVersioning() {
        DummyClass d = new DummyClass();

        assertTrue(DummyClass.class.getName() == Introspector.getClass(d).getName());
    }

    /**
     * Access to the added member declaring class.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDeclaringClassAddedMembersAfterVersioning() {
        DummyClass d = new DummyClass();

        jmplib.reflect.Class clazz = Introspector.getClass(d);

        try {
            jmplib.reflect.Method mget = clazz.getMethod("getName");
            jmplib.reflect.Method mset = clazz.getMethod("setName", String.class);
            jmplib.reflect.Field name = clazz.getField("name");

            assertTrue(mget.getDeclaringClass() == d.getClass());
            assertTrue(mset.getDeclaringClass() == d.getClass());
            assertTrue(name.getDeclaringClass() == d.getClass());
        } catch (Exception ex) {
            fail();
        }

    }

    /**
     * Use added members through its corresponding reflection objects
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDeclaringClassAddedMembersAfterVersioningUsage() {
        DummyClass d = new DummyClass();

        jmplib.reflect.Class clazz = Introspector.getClass(d);

        try {
            jmplib.reflect.Method mget = clazz.getMethod("getName");
            jmplib.reflect.Method mset = clazz.getMethod("setName", String.class);
            jmplib.reflect.Field name = clazz.getField("name");

            mset.invoke(d, "Albert");
            String nameVal = (String) mget.invoke(d);
            assertTrue(nameVal.equals("Albert"));
            String nameVal2 = (String) name.get(d);
            assertTrue(nameVal2.equals("Albert"));
            name.set(d, "Cosworth");
            String nameVal3 = (String) name.get(d);
            assertTrue(nameVal3.equals("Cosworth"));
        } catch (Exception ex) {
            fail();
        }

    }
}
