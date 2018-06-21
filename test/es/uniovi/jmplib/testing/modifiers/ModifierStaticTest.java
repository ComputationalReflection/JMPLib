package es.uniovi.jmplib.testing.modifiers;

import es.uniovi.jmplib.testing.interfaces.Func_String;
import es.uniovi.jmplib.testing.interfaces.Func_double_double_double;
import jmplib.DefaultEvaluator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class ModifierStaticTest {
    private static IIntercessor Intercessor = SimpleIntercessor.getInstance();
    private static IEvaluator Evaluator = DefaultEvaluator.getInstance();

    @Test
    public void addPublicMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("publicSum", MethodType.methodType(double.class, double.class, double.class),
                        "return a + b;", Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "publicSum",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addProtectedMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("protectedSum",
                        MethodType.methodType(double.class, double.class, double.class), "return a + b;",
                        Modifier.PROTECTED, "a", "b"));
        try {
            sum = Evaluator.getMethodInvoker(Dummie.class, "protectedSum",
                    new MemberInvokerData<>(Func_double_Dummie_double_double.class));
            fail("The method is not visible");
        } catch (StructuralIntercessionException e) {
        }
        assertNull(sum);
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("protectedSumAccess",
                        MethodType.methodType(double.class, double.class, double.class), "return protectedSum(a, b);",
                        Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "protectedSumAccess",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addPrivateMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("privateSum", MethodType.methodType(double.class, double.class, double.class),
                        "return a + b;", Modifier.PRIVATE, "a", "b"));
        try {
            sum = Evaluator.getMethodInvoker(Dummie.class, "privateSum",
                    new MemberInvokerData<>(Func_double_Dummie_double_double.class));
            fail("The method is not visible");
        } catch (StructuralIntercessionException e) {
        }
        assertNull(sum);
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("privateSumAccess",
                        MethodType.methodType(double.class, double.class, double.class), "return privateSum(a, b);",
                        Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "privateSumAccess",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addFinalMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("finalSum", MethodType.methodType(double.class, double.class, double.class),
                        "return a + b;", Modifier.FINAL | Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "finalSum",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addSynchronizedMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("synchronizedSum",
                        MethodType.methodType(double.class, double.class, double.class), "return a + b;",
                        Modifier.SYNCHRONIZED | Modifier.PUBLIC, new String[]{"a", "b"}));
        sum = Evaluator.getMethodInvoker(Dummie.class, "synchronizedSum",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addStrictMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("strictSum", MethodType.methodType(double.class, double.class, double.class),
                        "return a + b;", Modifier.STRICT | Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "strictSum",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addAbstractMethod() throws StructuralIntercessionException {
        Dummie dummie = new DummieImpl();
        Func_double_Dummie_double_double sum = null;
        Intercessor.addMethod(DummieImpl.class,
                new jmplib.reflect.Method("abstractSum",
                        MethodType.methodType(double.class, double.class, double.class), "return a + b;",
                        Modifier.PUBLIC, "a", "b"));
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("abstractSum",
                        MethodType.methodType(double.class, double.class, double.class), "return a + b;",
                        Modifier.ABSTRACT | Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "abstractSum",
                new MemberInvokerData<>(Func_double_Dummie_double_double.class));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(dummie, a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addStaticMethod() throws StructuralIntercessionException {
        Func_double_double_double sum = null;
        Intercessor.addMethod(Dummie.class,
                new jmplib.reflect.Method("staticSum", MethodType.methodType(double.class, double.class, double.class),
                        "return a + b;", Modifier.STATIC | Modifier.PUBLIC, "a", "b"));
        sum = Evaluator.getMethodInvoker(Dummie.class, "staticSum",
                new MemberInvokerData<>(Func_double_double_double.class, Modifier.STATIC));
        assertNotNull(sum);
        int a = 654654, b = 32;
        double expected = a + b;
        double actual = sum.invoke(a, b);
        assertTrue(actual == expected);
    }

    @Test
    public void addPublicField()
            throws StructuralIntercessionException, InstantiationException, IllegalAccessException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.PUBLIC, String.class, "publicField", "\"public\""));
        Intercessor.addMethod(Dummie.class, new jmplib.reflect.Method("getPublicField",
                MethodType.methodType(String.class), "return publicField;", Modifier.PUBLIC));
        Func_String_Dummie getter = Evaluator.getMethodInvoker(Dummie.class, "getPublicField",
                new MemberInvokerData<>(Func_String_Dummie.class));
        String result = getter.invoke(new DummieImpl());
        assertThat(result, equalTo("public"));

    }

    @Test
    public void addProtectedField() throws StructuralIntercessionException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.PROTECTED, String.class, "protectedField", "\"protected\""));
        Intercessor.addMethod(Dummie.class, new jmplib.reflect.Method("getProtectedField",
                MethodType.methodType(String.class), "return protectedField;", Modifier.PUBLIC));
        Func_String_Dummie getter = Evaluator.getMethodInvoker(Dummie.class, "getProtectedField",
                new MemberInvokerData<>(Func_String_Dummie.class));
        String result = getter.invoke(new DummieImpl());
        assertThat(result, equalTo("protected"));

    }

    @Test
    public void addPrivateField() throws StructuralIntercessionException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.PRIVATE, String.class, "privateField", "\"private\""));
        Intercessor.addMethod(Dummie.class, new jmplib.reflect.Method("getPrivateField",
                MethodType.methodType(String.class), "return privateField;", Modifier.PUBLIC));
        Func_String_Dummie getter = Evaluator.getMethodInvoker(Dummie.class, "getPrivateField",
                new MemberInvokerData<>(Func_String_Dummie.class));
        String result = getter.invoke(new DummieImpl());
        assertThat(result, equalTo("private"));

    }

    @Test
    public void addStaticField()
            throws StructuralIntercessionException, InstantiationException, IllegalAccessException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.STATIC | Modifier.PUBLIC, String.class, "staticField", "\"static\""));
        Func_String getter = Evaluator.getFieldInvoker(Dummie.class, "staticField",
                new MemberInvokerData<>(Func_String.class, Modifier.STATIC));
        String result = getter.invoke();
        assertThat(result, equalTo("static"));
    }

    @Test
    public void addFinalField() throws StructuralIntercessionException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.FINAL, String.class, "finalField", "\"final\""));
        Intercessor.addMethod(Dummie.class, new jmplib.reflect.Method("getFinalField",
                MethodType.methodType(String.class), "return finalField;", Modifier.PUBLIC));
        Func_String_Dummie getter = Evaluator.getMethodInvoker(Dummie.class, "getFinalField",
                new MemberInvokerData<>(Func_String_Dummie.class));
        String result = getter.invoke(new DummieImpl());
        assertThat(result, equalTo("final"));

    }

    @Test
    public void addTransientField() throws StructuralIntercessionException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.TRANSIENT, String.class, "transientField", "\"transient\""));
        Intercessor.addMethod(Dummie.class, new jmplib.reflect.Method("getTransientField",
                MethodType.methodType(String.class), "return transientField;", Modifier.PUBLIC));
        Func_String_Dummie getter = Evaluator.getMethodInvoker(Dummie.class, "getTransientField",
                new MemberInvokerData<>(Func_String_Dummie.class));
        String result = getter.invoke(new DummieImpl());
        assertThat(result, equalTo("transient"));

    }

    @Test
    public void addVolatileField() throws StructuralIntercessionException {
        Intercessor.addField(Dummie.class,
                new jmplib.reflect.Field(Modifier.VOLATILE, String.class, "volatileField", "\"volatile\""));
        Intercessor.addMethod(Dummie.class, new jmplib.reflect.Method("getVolatileField",
                MethodType.methodType(String.class), "return volatileField;", Modifier.PUBLIC));
        Func_String_Dummie getter = Evaluator.getMethodInvoker(Dummie.class, "getVolatileField",
                new MemberInvokerData<>(Func_String_Dummie.class));
        String result = getter.invoke(new DummieImpl());
        assertThat(result, equalTo("volatile"));
    }

}
