package es.uniovi.jmplib.testing.primitives.replacemethod;

import jmplib.*;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import org.junit.Test;

import java.lang.invoke.MethodType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class ReplaceMethodTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    @Test
    public void testReplaceMethod_ExistingMethod_Params() throws StructuralIntercessionException {
        Calculator calculator = new Calculator();
        // making operation
        double actual = calculator.divide(6, 3);
        // the result isn't stored in the attribute lastResult
        assertTrue(calculator.getLastResult() == 0.0);
        assertTrue(actual != calculator.getLastResult());
        Func_int_Calculator_double_double divide = null;

        // modifying method to store the last result on the attribute
        Intercessor.replaceMethod(Calculator.class,
                new jmplib.reflect.Method("divide", MethodType.methodType(double.class, int.class, int.class)),
                new jmplib.reflect.Method("divide", MethodType.methodType(int.class, double.class, double.class),
                        "lastResult = (int) (a / b);" + "return (int) lastResult;"));
        divide = Evaluator.getMethodInvoker(Calculator.class, "divide",
                new MemberInvokerData<Func_int_Calculator_double_double>(Func_int_Calculator_double_double.class));

        assertNotNull(divide);
        // checking that results are stored in this attribute
        int actualInt = divide.invoke(calculator, 7.65, 5.2);
        assertTrue(actualInt == ((int) (7.65 / 5.2)));
        actual = calculator.divide(432, 7);
        assertTrue(actual == ((double) (int) (432 / 7)));
        // Transaction
        calculator = new Calculator();
        // making operation
        actual = calculator.divide2(6, 3);
        // the result isn't stored in the attribute lastResult
        assertTrue(calculator.getLastResult() == 0.0);
        assertTrue(actual != calculator.getLastResult());
        Func_int_Calculator_double_double divide2 = null;

        // modifying method to store the last result on the attribute
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.replaceMethod(Calculator.class,
                new jmplib.reflect.Method("divide2", MethodType.methodType(double.class, int.class, int.class)),
                new jmplib.reflect.Method("divide2", MethodType.methodType(int.class, double.class, double.class),
                        "lastResult = (int) (a / b);" + "return (int) lastResult;"));
        transaction.commit();
        divide2 = Evaluator.getMethodInvoker(Calculator.class, "divide2",
                new MemberInvokerData<Func_int_Calculator_double_double>(Func_int_Calculator_double_double.class));
        assertNotNull(divide2);
        // checking that results are stored in this attribute
        actualInt = divide2.invoke(calculator, 7.65, 5.2);
        assertTrue(actualInt == ((int) (7.65 / 5.2)));
        actual = calculator.divide2(432, 7);
        assertTrue(actual == ((double) (int) (432 / 7)));

    }

    @Test
    public void testReplaceMethod_nothing_methodType_IllegalArgumentException_NullClass() {
        MethodType methodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "divide", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(null,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(null,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_nothing_methodType_IllegalArgumentException_NullName() {
        MethodType methodType = MethodType.methodType(int.class, double.class, double.class);
        String name = null, body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_nothing_methodType_IllegalArgumentException_EmptyName() {
        MethodType methodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_nothing_methodType_IllegalArgumentException_NullMethodType() {
        MethodType methodType = null;
        String name = "divide", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_nothing_methodType_IllegalArgumentException_NullBody() {
        MethodType methodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "divide", body = null;
        try {
            Intercessor.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class,
                    new jmplib.reflect.Method(name, MethodType.methodType(int.class, int.class, int.class)),
                    new jmplib.reflect.Method(name, methodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_methodType_methodType_IllegalArgumentException_NullClass() {
        MethodType methodType = MethodType.methodType(double.class, int.class, int.class),
                newMethodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "divide", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(null, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(null, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_methodType_methodType_IllegalArgumentException_NullName() {
        MethodType methodType = MethodType.methodType(double.class, int.class, int.class),
                newMethodType = MethodType.methodType(int.class, double.class, double.class);
        String name = null, body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_methodType_methodType_IllegalArgumentException_EmptyName() {
        MethodType methodType = MethodType.methodType(double.class, int.class, int.class),
                newMethodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_methodType_methodType_IllegalArgumentException_NullMethodType() {
        MethodType methodType = null, newMethodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "divide", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_methodType_methodType_IllegalArgumentException_NullNewMethodType() {
        MethodType methodType = MethodType.methodType(double.class, int.class, int.class), newMethodType = null;
        String name = "divide", body = "return (int) (a / b);";
        try {
            Intercessor.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceMethod_methodType_methodType_IllegalArgumentException_NullBody() {
        MethodType methodType = MethodType.methodType(double.class, int.class, int.class),
                newMethodType = MethodType.methodType(int.class, double.class, double.class);
        String name = "divide", body = null;
        try {
            Intercessor.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceMethod(Calculator.class, new jmplib.reflect.Method(name, methodType),
                    new jmplib.reflect.Method(name, newMethodType, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

}
