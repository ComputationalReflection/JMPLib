package es.uniovi.jmplib.testing.thread_safety;

import jmplib.IEvaluator;
import jmplib.SimpleEvaluator;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.reflect.Introspector;
import org.junit.Test;

import java.util.function.Function;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

@ExcludeFromJMPLib
public class ThreadSafetyDynamicCodeTest {


    //Concurrent eval
    @Test
    public void concurrentEval() throws  InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass9.class);

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();

        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    Function<Double, Double> code = evaluator.generateEvalInvoker(
                            "(a+a)*(a+a)",
                            new EvalInvokerData<Function>(Function.class, new String[]{"a"}, double.class, double.class)
                    );
                    assertEquals((i + i) * (i + i), code.apply((double) i), 0.1);
                    i++;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    Function<Double, Double> code = evaluator.generateEvalInvoker(
                            "(b*b)+(b*b)",
                            new EvalInvokerData<Function>(Function.class, new String[]{"b"}, double.class, double.class)
                    );
                    assertEquals((i * i) + (i * i), code.apply((double) i), 0.1);
                    i++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        // start the thread
        Thread t = new Thread(taskA);
        Thread t2 = new Thread(taskB);

        t.start();
        t2.start();
        t.join();
        t2.join();
    }

    //Concurrent exec
    @Test
    public void concurrentExec() throws InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass9.class);

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();

        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    Class dynclass = evaluator.exec("package es.uniovi.jmplib.testing.thread_safety;\npublic class DynClass" + i + "{}");
                    assertEquals("DynClass" + i, dynclass.getSimpleName());
                    i++;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    Class dynclass = evaluator.exec("package es.uniovi.jmplib.testing.thread_safety;\npublic class DynClassB" + i + "{}");
                    assertEquals("DynClassB" + i, dynclass.getSimpleName());
                    i++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        // start the thread
        Thread t = new Thread(taskA);
        Thread t2 = new Thread(taskB);

        t.start();
        t2.start();
        t.join();
        t2.join();
    }
}
