package es.uniovi.jmplib.testing.thread_safety;

import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleEvaluator;
import jmplib.SimpleIntercessor;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class ThreadSafetyTest {
    @Test
    public void basicThreadSafeTest() throws StructuralIntercessionException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass.class);

        IIntercessor interc = new SimpleIntercessor().createIntercessor();
        ContainerClass obj = new ContainerClass();

        interc.addMethod(cl, new jmplib.reflect.Method("foo", MethodType.methodType(int.class),
                "return 10;", Modifier.PUBLIC));

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        Function<ContainerClass, Integer> newSize = evaluator.getMethodInvoker(ContainerClass.class, "foo",
                new MemberInvokerData<>(Function.class, ContainerClass.class, int.class));
        assertEquals(10, (int)newSize.apply(obj));

    }
}
