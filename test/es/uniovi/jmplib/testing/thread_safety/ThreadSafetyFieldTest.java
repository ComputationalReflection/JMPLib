package es.uniovi.jmplib.testing.thread_safety;

import jmplib.*;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

@ExcludeFromJMPLib
public class ThreadSafetyFieldTest {
    @Test
    public void concurrentClassModificationFieldTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();
        IIntercessor interc2 = new SimpleIntercessor().createIntercessor();

        ContainerFieldClass obj = new ContainerFieldClass();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
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
                    interc2.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "fooB" + i, "10"));
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
            Function<ContainerFieldClass, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            Function<ContainerFieldClass, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass.class, "fooB" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass.class, int.class));
            assertEquals(10, (int) newSize2.apply(obj));
            i++;
        }

    }

    @Test
    public void concurrentClassModificationFieldTransactionTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass2.class);

        IIntercessor interc1 = new TransactionalIntercessor().createIntercessor();
        IIntercessor interc2 = new TransactionalIntercessor().createIntercessor();

        ContainerFieldClass2 obj = new ContainerFieldClass2();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
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
                    interc2.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "fooB" + i, "10"));
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
            Function<ContainerFieldClass2, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass2.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass2.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            Function<ContainerFieldClass2, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass2.class, "fooB" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass2.class, int.class));
            assertEquals(10, (int) newSize2.apply(obj));
            i++;
        }

    }

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Test
    public void concurrentClassInvokerWhileCreatorFieldTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass3.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerFieldClass3 obj = new ContainerFieldClass3();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
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
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!running.get()) {
                    Function<ContainerFieldClass3, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass3.class, "baz",
                            new MemberInvokerData<>(Function.class, ContainerFieldClass3.class, int.class));
                    assertEquals(5, (int) newSize2.apply(obj));
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
            Function<ContainerFieldClass3, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass3.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass3.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }

    @Test
    public void concurrentClassDirectInvokationWhileCreatorFieldTest() throws StructuralIntercessionException, InterruptedException {
        running.set(false);
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass4.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerFieldClass4 obj = new ContainerFieldClass4();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
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
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!running.get()) {
                    obj.baz = 20;
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
            Function<ContainerFieldClass4, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass4.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass4.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }


    private final AtomicBoolean running2 = new AtomicBoolean(false);
    private final AtomicBoolean running3 = new AtomicBoolean(false);

    @Test
    public void concurrentClassInvokerWhile2CreatorFieldTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass5.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();
        IIntercessor interc2 = new SimpleIntercessor().createIntercessor();

        ContainerFieldClass5 obj = new ContainerFieldClass5();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
                    i++;
                }
                running2.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        Runnable taskB = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc2.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "fooB" + i, "10"));
                    i++;
                }
                running3.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        Runnable taskC = () -> {
            try {
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!(running2.get() && running3.get())) {
                    Function<ContainerFieldClass5, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass5.class, "baz",
                            new MemberInvokerData<>(Function.class, ContainerFieldClass5.class, int.class));
                    assertEquals(5, (int) newSize2.apply(obj));
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
        Thread t3 = new Thread(taskC);

        t.start();
        t2.start();
        t3.start();
        t.join();
        t2.join();
        t3.join();

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        int i = 0;
        while (i < limit) {
            Function<ContainerFieldClass5, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass5.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass5.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            Function<ContainerFieldClass5, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass5.class, "fooB" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass5.class, int.class));
            assertEquals(10, (int) newSize2.apply(obj));
            i++;
        }

    }


    //Transaction 1
    private final AtomicBoolean running4 = new AtomicBoolean(false);

    @Test
    public void concurrentClassInvokerWhileCreatorFieldTransactionTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass6.class);

        IIntercessor interc1 = new TransactionalIntercessor().createIntercessor();

        ContainerFieldClass6 obj = new ContainerFieldClass6();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
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
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!running4.get()) {
                    Function<ContainerFieldClass6, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass6.class, "baz",
                            new MemberInvokerData<>(Function.class, ContainerFieldClass6.class, int.class));
                    assertEquals(5, (int) newSize2.apply(obj));
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
            Function<ContainerFieldClass6, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass6.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass6.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }

    @Test
    public void concurrentClassDirectInvokationWhileCreatorFieldTransactionTest() throws StructuralIntercessionException, InterruptedException {
        running.set(false);
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass7.class);

        IIntercessor interc1 = new TransactionalIntercessor().createIntercessor();

        ContainerFieldClass7 obj = new ContainerFieldClass7();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
                    i++;
                }
                interc1.commit();
                running.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        Runnable taskB = () -> {
            try {
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (!running.get()) {
                    obj.baz = 20;
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
            Function<ContainerFieldClass7, Integer> newSize = evaluator.getFieldInvoker(ContainerFieldClass7.class, "foo" + i,
                    new MemberInvokerData<>(Function.class, ContainerFieldClass7.class, int.class));
            assertEquals(10, (int) newSize.apply(obj));
            i++;
        }

    }


    //Multiple invoker test
    @Test
    public void concurrentInvokersFieldTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass8.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerFieldClass8 obj = new ContainerFieldClass8();
        int limit = 20;

        try {
            int i = 0;
            while (i < limit) {
                interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
                i++;
            }
            running.set(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }


        Runnable taskA = () -> {
            try {
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (i < limit) {
                    Function<ContainerFieldClass8, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass8.class, "foo" + i,
                            new MemberInvokerData<>(Function.class, ContainerFieldClass8.class, int.class));
                    assertEquals(10, (int) newSize2.apply(obj));
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
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (i < limit) {
                    Function<ContainerFieldClass8, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass8.class, "foo" + i,
                            new MemberInvokerData<>(Function.class, ContainerFieldClass8.class, int.class));
                    assertEquals(10, (int) newSize2.apply(obj));
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

    private final AtomicBoolean running5 = new AtomicBoolean(false);

    @Test
    public void concurrentInvokerToAddeFieldTest() throws StructuralIntercessionException, InterruptedException {
        assertTrue(JMPlibConfig.getInstance().getConfigureAsThreadSafe());
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(ContainerFieldClass9.class);

        IIntercessor interc1 = new SimpleIntercessor().createIntercessor();

        ContainerFieldClass9 obj = new ContainerFieldClass9();
        int limit = 20;

        Runnable taskA = () -> {
            try {
                int i = 0;
                while (i < limit) {
                    interc1.addField(cl, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "foo" + i, "=10"));
                    i++;
                }
                running5.set(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };

        Runnable taskB = () -> {
            try {
                int i = 0;
                IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
                while (i < limit) {
                    Function<ContainerFieldClass9, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass9.class, "foo" + i,
                            new MemberInvokerData<>(Function.class, ContainerFieldClass9.class, int.class));
                    assertEquals(10, (int) newSize2.apply(obj));
                    i++;
                }
                while (!running5.get())
                    Thread.sleep(100);
                try {
                    Function<ContainerFieldClass9, Integer> newSize2 = evaluator.getFieldInvoker(ContainerFieldClass9.class, "foo" + (limit - 1),
                            new MemberInvokerData<>(Function.class, ContainerFieldClass9.class, int.class));
                    assertEquals(10, (int) newSize2.apply(obj));
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                    fail();
                }
            } catch (Exception ex) {
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
