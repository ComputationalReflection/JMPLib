package es.uniovi.jmplib.testing.generics;

import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Class;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ExcludeFromJMPLib
public class GenericTest {
    private static IIntercessor Intercessor = SimpleIntercessor.getInstance();

    @SuppressWarnings("rawtypes")
    @Test
    public void testAddGenericTypeToClass() {
        TypeVariable<?>[] tvs = Introspector.decorateClass(GenericTestClass.class).getTypeParameters();

        assertTrue(tvs.length == 0);
        try {
            Intercessor.addGenericType(GenericTestClass.class, new jmplib.reflect.TypeVariable("T"));
        } catch (StructuralIntercessionException e) {
            fail();
        }
        tvs = Introspector.decorateClass(GenericTestClass.class).getTypeParameters();
        assertTrue(tvs.length == 1);
        assertTrue(tvs[0].getTypeName().equals("T"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testAddGenericTypeToMethod() {
        TypeVariable<?>[] tvs = null;
        try {
            tvs = Introspector.decorateClass(GenericTestClass.class).getMethod("testMethod").getTypeParameters();
        } catch (NoSuchMethodException e1) {
            fail();
        }

        assertTrue(tvs.length == 0);
        try {
            Intercessor.addGenericType(Introspector.decorateClass(GenericTestClass.class).getMethod("testMethod"),
                    new jmplib.reflect.TypeVariable("T"));
        } catch (StructuralIntercessionException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            fail();
        }
        try {
            tvs = Introspector.decorateClass(GenericTestClass.class).getMethod("testMethod").getTypeParameters();
        } catch (NoSuchMethodException e) {
            fail();
        }
        assertTrue(tvs.length == 1);
        assertTrue(tvs[0].getTypeName().equals("T"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testAddGenericTypeToClassAndAMethodUsingIt() {
        Class<?> cl = Introspector.decorateClass(GenericTestClass2.class);
        Method m = null;
        try {
            Type[] ptypes = new Type[1];
            ptypes[0] = new jmplib.reflect.TypeVariable("T");
            Intercessor.addMethod(cl,
                    new Method(null, "addedMethodG", MethodType.genericMethodType(1), "return param;",
                            Modifier.PUBLIC, new Class<?>[0], ptypes, new jmplib.reflect.TypeVariable("T"), null,
                            "param"));
            fail();
        } catch (Exception e1) {

        }

        try {
            Intercessor.addGenericType(cl, new jmplib.reflect.TypeVariable("T"));
        } catch (StructuralIntercessionException e) {
            fail();
        }
        try {
            Type[] ptypes = new Type[1];
            ptypes[0] = new jmplib.reflect.TypeVariable("T");
            Intercessor.addMethod(cl,
                    new Method(null, "addedMethodG", MethodType.genericMethodType(1), "return param;",
                            Modifier.PUBLIC, new Class<?>[0], ptypes, new jmplib.reflect.TypeVariable("T"), null,
                            "param"));
        } catch (Exception e1) {
            e1.printStackTrace();
            fail();
        }
        try {
            m = cl.getMethod("addedMethodG", Object.class);
        } catch (NoSuchMethodException e) {
            fail();
        }
        try {
            assertTrue(m.getParameterString().equals("(T param)"));
        } catch (IllegalAccessException e) {
            fail();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRemoveUsedGenericType() {
        Class<?> cl = Introspector.decorateClass(GenericTestClass3.class);
        try {
            Intercessor.setGenericType(cl, new jmplib.reflect.TypeVariable("Q"));
            fail();
        } catch (StructuralIntercessionException e) {

        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRemoveUsedGenericTypeAndMethods() {
        Class<?> cl = Introspector.decorateClass(GenericTestClass4.class);
        try {
            Intercessor.removeMethod(cl, cl.getMethod("testMethod", Object.class));
            Intercessor.setGenericType(cl, new jmplib.reflect.TypeVariable("Q"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testAddMultipleGenericTypes() {
        TypeVariable<?>[] tvs = Introspector.decorateClass(GenericTestClass5.class).getTypeParameters();

        assertTrue(tvs.length == 0);
        try {
            Intercessor.addGenericType(GenericTestClass5.class, new jmplib.reflect.TypeVariable("T"),
                    new jmplib.reflect.TypeVariable("Q"), new jmplib.reflect.TypeVariable("R"));
        } catch (StructuralIntercessionException e) {
            fail();
        }
        tvs = Introspector.decorateClass(GenericTestClass5.class).getTypeParameters();
        assertTrue(tvs.length == 3);
        assertTrue(tvs[0].getTypeName().equals("T") || tvs[0].getTypeName().equals("Q") || tvs[0].getTypeName().equals("R"));
        assertTrue(tvs[1].getTypeName().equals("T") || tvs[1].getTypeName().equals("Q") || tvs[1].getTypeName().equals("R"));
        assertTrue(tvs[2].getTypeName().equals("T") || tvs[2].getTypeName().equals("Q") || tvs[2].getTypeName().equals("R"));

        tvs = null;
        try {
            tvs = Introspector.decorateClass(GenericTestClass5.class).getMethod("testMethod").getTypeParameters();
        } catch (NoSuchMethodException e1) {
            fail();
        }

        assertTrue(tvs.length == 0);
        try {
            Intercessor.addGenericType(Introspector.decorateClass(GenericTestClass5.class).getMethod("testMethod"),
                    new jmplib.reflect.TypeVariable("T2"), new jmplib.reflect.TypeVariable("Q2"),
                    new jmplib.reflect.TypeVariable("R2"));
        } catch (StructuralIntercessionException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            fail();
        }
        try {
            tvs = Introspector.decorateClass(GenericTestClass5.class).getMethod("testMethod").getTypeParameters();
        } catch (NoSuchMethodException e) {
            fail();
        }
        assertTrue(tvs.length == 3);
        assertTrue(tvs[0].getTypeName().equals("T2") || tvs[0].getTypeName().equals("Q2") || tvs[0].getTypeName().equals("R2"));
        assertTrue(tvs[1].getTypeName().equals("T2") || tvs[1].getTypeName().equals("Q2") || tvs[1].getTypeName().equals("R2"));
        assertTrue(tvs[2].getTypeName().equals("T2") || tvs[2].getTypeName().equals("Q2") || tvs[2].getTypeName().equals("R2"));
    }
}
