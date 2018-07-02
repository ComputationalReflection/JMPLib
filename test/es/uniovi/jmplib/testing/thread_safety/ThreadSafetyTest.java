package es.uniovi.jmplib.testing.thread_safety;

import jmplib.*;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

@ExcludeFromJMPLib
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
        assertEquals(10, (int) newSize.apply(obj));

    }

    @Test
    public void concurrentClassModificationTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass2.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();
        IIntercessor interc2 = new SimpleIntercessor().createIntercessor();

        ContainerClass2 obj = new ContainerClass2();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
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
                    interc2.addMethod(cl, new jmplib.reflect.Method("fooB" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerClass2, Integer> newSize = evaluator.getMethodInvoker(ContainerClass2.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass2.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            Function<ContainerClass2, Integer> newSize2 = evaluator.getMethodInvoker(ContainerClass2.class, "fooB" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass2.class, int.class));
            assertEquals(10, (int) newSize2.apply(obj));
            i++;
        }

    }

    @Test
    public void concurrentClassModificationTestTransaction() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass3.class);

        IIntercessor interc1 = new TransactionalIntercessor().createIntercessor();
        IIntercessor interc2 = new TransactionalIntercessor().createIntercessor();

        ContainerClass3 obj = new ContainerClass3();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                interc1.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        Runnable taskB = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc2.addMethod(cl, new jmplib.reflect.Method("fooB" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                interc2.commit();
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerClass3, Integer> newSize = evaluator.getMethodInvoker(ContainerClass3.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass3.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            Function<ContainerClass3, Integer> newSize2 = evaluator.getMethodInvoker(ContainerClass3.class, "fooB" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass3.class, int.class));
            assertEquals(10, (int) newSize2.apply(obj));
            i++;
        }

    }

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Test
    public void concurrentClassInvokerWhileCreatorTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass4.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerClass4 obj = new ContainerClass4();
        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                running.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!running.get()) {
                    Function<ContainerClass4, Integer> newSize2 = evaluator.getMethodInvoker(ContainerClass4.class, "test",
                            new MemberInvokerData<>(Function.class, ContainerClass4.class, int.class));
                    assertEquals(20, (int) newSize2.apply(obj));
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerClass4, Integer> newSize = evaluator.getMethodInvoker(ContainerClass4.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass4.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }

    //Same as above but with direct calls.
    @Test
    public void concurrentClassDirectInvokationWhileCreatorTest() throws StructuralIntercessionException, InterruptedException {
        running.set(false);
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass6.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerClass6 obj = new ContainerClass6();
        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                running.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                while (!running.get()) {
                    obj.test();
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerClass6, Integer> newSize = evaluator.getMethodInvoker(ContainerClass6.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass6.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }

    @Test
    public void concurrentClassDirectInvokationWhileCreatorTestParameter() throws StructuralIntercessionException, InterruptedException {
        running.set(false);
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass7.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerClass7 obj = new ContainerClass7();
        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(double.class, double.class),
                            "return d;", Modifier.PUBLIC, "d"));
                    i++;
                }
                running.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                while (!running.get()) {
                    obj.test(13.2);
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            BiFunction<ContainerClass7, Double, Double> newSize = evaluator.getMethodInvoker(ContainerClass7.class, "foo" + i,
                    new MemberInvokerData<>(BiFunction.class, ContainerClass7.class, double.class, double.class));
            assertEquals(14.5, (double) newSize.apply(obj, 14.5), 0.01);
            i++;
        }

    }

    private final AtomicBoolean running2 = new AtomicBoolean(false);
    private final AtomicBoolean running3 = new AtomicBoolean(false);

    @Test
    public void concurrentClassInvokerWhile2CreatorTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass5.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerClass5 obj = new ContainerClass5();
        int limit = 20;


        Runnable creatorA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                running2.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable creatorB = () -> {
            try {
                int x = 0;
                while (x < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + (x + limit), MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    x++;
                }
                running3.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable invoker = () -> {
            try {
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!(running2.get() && (running3.get()))) {
                    Function<ContainerClass5, Integer> newSize2 = evaluator.getMethodInvoker(ContainerClass5.class, "test",
                            new MemberInvokerData<>(Function.class, ContainerClass5.class, int.class));
                    assertEquals(20, (int) newSize2.apply(obj));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        // start the thread
        Thread t = new Thread(creatorA);
        Thread t2 = new Thread(creatorB);
        Thread t3 = new Thread(invoker);

        t.start();
        t2.start();
        t3.start();
        t.join();
        t2.join();
        t3.join();

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit * 2) {
            Function<ContainerClass5, Integer> newSize = evaluator.getMethodInvoker(ContainerClass5.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass5.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }

    //Transaction 1
    private final AtomicBoolean running4 = new AtomicBoolean(false);

    @Test
    public void concurrentClassInvokerWhileCreatorTestTransaction() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass8.class);

        IIntercessor interc1 = new TransactionalIntercessor().createIntercessor();

        ContainerClass8 obj = new ContainerClass8();
        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                interc1.commit();
                running4.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!running4.get()) {
                    Function<ContainerClass8, Integer> newSize2 = evaluator.getMethodInvoker(ContainerClass8.class, "test",
                            new MemberInvokerData<>(Function.class, ContainerClass8.class, int.class));
                    assertEquals(20, (int) newSize2.apply(obj));
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerClass8, Integer> newSize = evaluator.getMethodInvoker(ContainerClass8.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass8.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }

    //Transaction 2
    //Same as above but with direct calls.
    @Test
    public void concurrentClassDirectInvokationWhileCreatorTestTransaction() throws StructuralIntercessionException, InterruptedException {
        running4.set(false);
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerClass9.class);

        IIntercessor interc1 = new TransactionalIntercessor().createIntercessor();

        ContainerClass9 obj = new ContainerClass9();
        int limit = 20;


        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addMethod(cl, new jmplib.reflect.Method("foo" + i, MethodType.methodType(int.class),
                            "return 10;", Modifier.PUBLIC));
                    i++;
                }
                interc1.commit();
                running4.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }

        };

        Runnable taskB = () -> {
            try {
                while (!running4.get()) {
                    obj.test();
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

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerClass9, Integer> newSize = evaluator.getMethodInvoker(ContainerClass9.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerClass9.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }


    //Concurrent eval
    @Test
    public void concurrentEval() throws StructuralIntercessionException, InterruptedException {
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
    public void concurrentExec() throws StructuralIntercessionException, InterruptedException {
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
