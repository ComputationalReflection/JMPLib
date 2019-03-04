package es.uniovi.jmplib.testing.isinstance_cast;

import es.uniovi.jmplib.testing.isinstance_cast.classes.*;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Introspector;
import org.junit.Test;

import java.util.TreeSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Checking instanceof and cast replacement on Java API and user code
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class IsInstanceCastInterfacesTest {

    /**
     * Method to check the Introspector instanceOf primitive
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void IsInstancePrimitiveTest() {
        Foo3 f = new Foo3();

        assertFalse(Introspector.instanceOf(f, Comparable.class));

        IIntercessor it = new TransactionalIntercessor();

        //Add interface dynamically
        try {
            it.addInterface(Foo3.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            fail();
        }
        assertTrue(Introspector.instanceOf(f, Comparable.class));
    }

    /**
     * Method to check the Introspector cast primitive
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void CastPrimitiveTest() {
        Foo4 f = new Foo4();

        try {
            Introspector.cast(Comparable.class, f);
            fail();
        } catch (ClassCastException ex) {

        }
        IIntercessor it = new TransactionalIntercessor();

        //Add interface dynamically
        try {
            it.addInterface(Foo4.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            fail();
        }
        try {
            Introspector.cast(Comparable.class, f);
        } catch (ClassCastException ex) {
            fail();
        }
    }

    /**
     * Method to check the Introspector instanceOf primitive on user code. This code return a different kind of list
     * depending on the interfaces implemented by the passed object class: non-Comparable objects returns a LinkedList.
     * Otherwise a PriorityQueue is returned.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void IsInstanceUserCodeTest() {
        Foo f = new Foo();
        QueueCreator q = new QueueCreator();

        assertEquals(q.newQueue(f).getClass().getName(), "java.util.LinkedList");

        IIntercessor it = new TransactionalIntercessor();

        //Add interface dynamically
        try {
            it.addInterface(Foo.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }
        assertEquals(q.newQueue(f).getClass().getName(), "java.util.PriorityQueue");
    }

    /**
     * Make a class temporally implement and interface, check that once removed is no longer an instance of the interface
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void TemporalIsInstanceUserCodeTest() {
        Foo5 f = new Foo5();
        QueueCreator q = new QueueCreator();

        assertEquals(q.newQueue(f).getClass().getName(), "java.util.LinkedList");

        IIntercessor it = new TransactionalIntercessor();

        //Add interface dynamically
        try {
            it.addInterface(Foo5.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }
        assertEquals(q.newQueue(f).getClass().getName(), "java.util.PriorityQueue");

        //Add interface dynamically
        try {
            it.removeInterface(Foo5.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }
        assertEquals(q.newQueue(f).getClass().getName(), "java.util.LinkedList");
    }

    /**
     * Method to check the Introspector cast primitive on user code. This code return a different kind of list
     * depending on the interfaces implemented by the passed object class: non-Comparable objects returns a LinkedList.
     * Otherwhise a PriorityQueue is returned. This method uses a plain cast to try to determine if the object's class
     * implements Comparable. If not, the resulting exception is caught and a LinkedList is returned. In case the
     * cast is correct, a PriorityQueue is returned.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void CastUserCodeTest() {
        Foo1 f = new Foo1();
        QueueCreator q = new QueueCreator();

        assertEquals(q.newQueueCast(f).getClass().getName(), "java.util.LinkedList");

        IIntercessor it = new TransactionalIntercessor();

        //Add interface dynamically
        try {
            it.addInterface(Foo1.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }
        assertEquals(q.newQueueCast(f).getClass().getName(), "java.util.PriorityQueue");

    }

    /**
     * Make a class temporally implement and interface, check that once removed can no longer cast to the interface
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void TemporalCastUserCodeTest() {
        Foo6 f = new Foo6();
        QueueCreator q = new QueueCreator();

        assertEquals(q.newQueueCast(f).getClass().getName(), "java.util.LinkedList");

        IIntercessor it = new TransactionalIntercessor();

        //Add interface dynamically
        try {
            it.addInterface(Foo6.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }
        assertEquals(q.newQueueCast(f).getClass().getName(), "java.util.PriorityQueue");

        //Add interface dynamically
        try {
            it.removeInterface(Foo6.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }
        assertEquals(q.newQueueCast(f).getClass().getName(), "java.util.LinkedList");
    }

    /**
     * This method checks the Java API code interacting with our runtime modifications. To check that we use the
     * TreeSet class that throw an exception if trying to add an object that does not implement Comparable. We add
     * the interface dynamically to the class and try to add the instance again. The add is successful as now the
     * class implements this interface.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void IsInstanceCastJavaAPIest() {
        Foo2 f = new Foo2();

        TreeSet t = new TreeSet();

        try {
            t.add(f);
            fail("Foo is not comparable, it shouldn't be added!");
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        IIntercessor it = new TransactionalIntercessor();
        //Add interface dynamically
        try {
            it.addInterface(Foo2.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }

        try {
            t.add(f);
            //System.out.println("Correct!!");
        } catch (Exception ex) {
            //ex.printStackTrace();
            fail();
        }

    }

    /**
     * Same method as before, but the class implements the interface just temporally.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void TemporalIsInstanceCastJavaAPIest() {
        Foo7 f = new Foo7();

        TreeSet t = new TreeSet();

        try {
            t.add(f);
            fail("Foo is not comparable, it shouldn't be added!");
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        IIntercessor it = new TransactionalIntercessor();
        //Add interface dynamically
        try {
            it.addInterface(Foo7.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }

        try {
            t.add(f);
            //System.out.println("Correct!!");
        } catch (Exception ex) {
            //ex.printStackTrace();
            fail();
        }
        //Remove interface dynamically
        try {
            it.removeInterface(Foo7.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            //e.printStackTrace();
            fail();
        }

        try {
            t.add(f);
            fail("Foo is not comparable, it shouldn't be added!");
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }
}
