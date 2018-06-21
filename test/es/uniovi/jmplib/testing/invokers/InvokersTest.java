package es.uniovi.jmplib.testing.invokers;

import jmplib.SimpleEvaluator;
import jmplib.IEvaluator;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class InvokersTest {
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    @Test
    public void testGetInvoker_OriginalMethod() throws StructuralIntercessionException {
        Func_double_Calculator_double_double invoker = null;
        invoker = Evaluator.getMethodInvoker(Calculator.class, "pow",
                new MemberInvokerData<>(Func_double_Calculator_double_double.class));
        assertNotNull(invoker);
        double actual = invoker.invoke(new Calculator(), 2, 3);
        double expected = 8;
        assertTrue(actual == expected);
    }

    @Test
    public void testGetInvoker_IllegalArgumentException_NullClass() throws StructuralIntercessionException {
        try {
            Evaluator.getMethodInvoker(null, "multiply", new MemberInvokerData<>(Func_double_Calculator_double_double.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testGetInvoker_IllegalArgumentException_NullName() throws StructuralIntercessionException {
        try {
            Evaluator.getMethodInvoker(Calculator.class, null,
                    new MemberInvokerData<>(Func_double_Calculator_double_double.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testGetInvoker_IllegalArgumentException_EmptyName() throws StructuralIntercessionException {
        try {
            Evaluator.getMethodInvoker(Calculator.class, "",
                    new MemberInvokerData<>(Func_double_Calculator_double_double.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testGetInvoker_IllegalArgumentException_NullInterface() throws StructuralIntercessionException {
        try {
            Evaluator.getMethodInvoker(Calculator.class, "multiply", new MemberInvokerData<>(null));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The functionalInterface parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAttributeInvoker_OriginalField() throws StructuralIntercessionException {
        Func_int_Counter getter = Evaluator.getFieldInvoker(Counter.class, "counter",
                new MemberInvokerData<>(Func_int_Counter.class));
        assertNotNull(getter);
        Counter counter = new Counter();
        assertTrue(getter.invoke(counter) == counter.counter);
        counter.counter = 15;
        assertTrue(getter.invoke(counter) == counter.counter);
        Function<Counter, Integer> getter2 = Evaluator.getFieldInvoker(Counter.class, "counter",
                new MemberInvokerData<>(Function.class, Counter.class, int.class));
        assertNotNull(getter2);
        counter = new Counter();
        assertTrue(getter2.apply(counter) == counter.counter);
        counter.counter = 15;
        assertTrue(getter2.apply(counter) == counter.counter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAttributeInvoker_OriginalStaticField() throws StructuralIntercessionException {
        Func_int getter = Evaluator.getFieldInvoker(Counter.class, "COUNTERS",
                new MemberInvokerData<>(Func_int.class, Modifier.STATIC));
        assertNotNull(getter);
        assertTrue(getter.invoke() == Counter.COUNTERS);
        Counter.COUNTERS = 15;
        assertTrue(getter.invoke() == Counter.COUNTERS);
        Supplier<Integer> getter2 = Evaluator.getFieldInvoker(Counter.class, "COUNTERS",
                new MemberInvokerData<>(Supplier.class, Modifier.STATIC, int.class));
        assertNotNull(getter2);
        assertTrue(getter2.get() == Counter.COUNTERS);
        Counter.COUNTERS = 15;
        assertTrue(getter2.get() == Counter.COUNTERS);
    }

    @Test
    public void testGetAttributeInvoker_IllegalArgumentException_NullClass() throws StructuralIntercessionException {
        try {
            Evaluator.getFieldInvoker(null, "memory", new MemberInvokerData<>(Func_double_Calculator.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testGetAttributeInvoker_IllegalArgumentException_NullName() throws StructuralIntercessionException {
        try {
            Evaluator.getFieldInvoker(Calculator.class, null, new MemberInvokerData<>(Func_double_Calculator.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testGetAttributeInvoker_IllegalArgumentException_EmptyName() throws StructuralIntercessionException {
        try {
            Evaluator.getFieldInvoker(Calculator.class, "", new MemberInvokerData<>(Func_double_Calculator.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testGetAttributeInvoker_IllegalArgumentException_NullInterface()
            throws StructuralIntercessionException {
        try {
            Evaluator.getFieldInvoker(Calculator.class, "memory", new MemberInvokerData<>(null));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The functionalInterface parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetAttributeInvoker_OriginalField() throws StructuralIntercessionException {
        Func_void_Counter_int setter = Evaluator.setFieldInvoker(Counter.class, "counter",
                new MemberInvokerData<>(Func_void_Counter_int.class));
        assertNotNull(setter);
        Counter counter = new Counter();
        int value = 15;
        setter.invoke(counter, value);
        assertTrue(counter.counter == value);
        BiConsumer<Counter, Integer> setter2 = Evaluator.setFieldInvoker(Counter.class, "counter",
                new MemberInvokerData<>(BiConsumer.class, Counter.class, int.class));
        assertNotNull(setter2);
        counter = new Counter();
        setter2.accept(counter, value);
        assertTrue(counter.counter == value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testSetAttributeInvoker_OriginalStaticField() throws StructuralIntercessionException {
        Func_int_void setter = Evaluator.setFieldInvoker(Counter.class, "COUNTERS",
                new MemberInvokerData<>(Func_int_void.class, Modifier.STATIC));
        assertNotNull(setter);
        int value = 15;
        setter.invoke(value);
        assertTrue(Counter.COUNTERS == value);
        Consumer setter2 = Evaluator.setFieldInvoker(Counter.class, "COUNTERS",
                new MemberInvokerData<>(Consumer.class, Modifier.STATIC, int.class));
        assertNotNull(setter2);
        setter2.accept(value);
        assertTrue(Counter.COUNTERS == value);
    }

    @Test
    public void testSetAttributeInvoker_IllegalArgumentException_NullClass() throws StructuralIntercessionException {
        try {
            Evaluator.setFieldInvoker(null, "counter", new MemberInvokerData<>(Func_void_Counter_double.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testSetAttributeInvoker_IllegalArgumentException_NullName() throws StructuralIntercessionException {
        try {
            Evaluator.setFieldInvoker(Counter.class, null, new MemberInvokerData<>(Func_void_Counter_double.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testSetAttributeInvoker_IllegalArgumentException_EmptyName() throws StructuralIntercessionException {
        try {
            Evaluator.setFieldInvoker(Counter.class, "", new MemberInvokerData<>(Func_void_Counter_double.class));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testSetAttributeInvoker_IllegalArgumentException_NullInterface()
            throws StructuralIntercessionException {
        try {
            Evaluator.setFieldInvoker(Calculator.class, "memory", new MemberInvokerData<>(null));
            fail("The method had to raise an exception");
        } catch (IllegalArgumentException e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The functionalInterface parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

}
