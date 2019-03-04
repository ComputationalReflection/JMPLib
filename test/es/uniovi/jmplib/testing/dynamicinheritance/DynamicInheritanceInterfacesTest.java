package es.uniovi.jmplib.testing.dynamicinheritance;

import es.uniovi.jmplib.testing.dynamicinheritance.classes.*;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class DynamicInheritanceInterfacesTest {
    /**
     * Add non-generic interface to class
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void addInterfaceTest() {
        DogDH d = new DogDH("Rufus");
        DogDH d2 = new DogDH("Laika");

        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addMethod(DogDH.class, new Method("compareTo", MethodType.methodType(int.class, Object.class),
                    "return this.name.compareTo(((DogDH)otherDog).name);", "otherDog"));
            it.addInterface(DogDH.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            fail();
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(Comparable.class);
        try {
            Method m = cl.getMethod("compareTo", Object.class);
            assertTrue(m != null);
            assertTrue((int) m.invoke(d, d2) != 0);
        } catch (NoSuchMethodException | SecurityException e) {
            fail();
        } catch (IllegalAccessException e) {
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }

        Comparable compdog = (Comparable) cl.cast(d);
        assertTrue(compdog.compareTo(d2) != 0);

        cl = Introspector.decorateClass(DogDH.class);

        jmplib.reflect.Class<?>[] interfs = cl.getInterfaces();
        boolean found = false;
        for (jmplib.reflect.Class<?> i : interfs) {
            if (i.equals(Comparable.class)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /**
     * Add generic interface to class
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addGenericInterfaceTest() {
        DogDHG d = new DogDHG("Rufus");
        DogDHG d2 = new DogDHG("Laika");

        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addMethod(DogDHG.class, new Method("compareTo", MethodType.methodType(int.class, DogDHG.class),
                    "return this.name.compareTo(otherDog.name);", "otherDog"));
            it.addInterface(DogDHG.class, Comparable.class, DogDHG.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            fail();
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(DogDHG.class);
        try {
            Method m = cl.getMethod("compareTo", DogDHG.class);
            assertTrue(m != null);
            assertTrue((int) m.invoke(d, d2) != 0);
        } catch (NoSuchMethodException | SecurityException e) {
            fail();
        } catch (IllegalAccessException e) {
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }

        cl = Introspector.decorateClass(Comparable.class);
        Comparable<DogDHG> compdog = (Comparable<DogDHG>) cl.cast(d);
        assertTrue(compdog.compareTo(d2) != 0);

        cl = Introspector.decorateClass(DogDHG.class);

        jmplib.reflect.Class<?>[] interfs = cl.getInterfaces();
        boolean found = false;
        for (jmplib.reflect.Class<?> i : interfs) {
            if (i.equals(Comparable.class)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /**
     * Add interface to class without implementing its methods
     */
    @Test
    public void addInterfaceNoMethodTest() {
        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addInterface(DogDHG.class, PrinterCustomInterface.class);
            it.commit();
            fail();
        } catch (StructuralIntercessionException e) {
            return;
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(DogDHG.class);

        jmplib.reflect.Class<?>[] interfs = cl.getInterfaces();
        boolean found = false;
        for (jmplib.reflect.Class<?> i : interfs) {
            if (i.equals(Comparable.class)) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    /**
     * Add custom interface to class
     */
    @Test
    public void addCustomInterfaceTest() {
        DogDH d = new DogDH("Rufus");
        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addMethod(DogDH.class, new Method("printInfo", MethodType.methodType(void.class), "System.out.println(this.name);"));
            it.addInterface(DogDH.class, PrinterCustomInterface.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            fail();
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(DogDH.class);
        try {
            Method m = cl.getMethod("printInfo");
            assertTrue(m != null);
            assertTrue(m.invoke(d) == null);
        } catch (NoSuchMethodException | SecurityException e) {
            fail();
        } catch (IllegalAccessException e) {
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }
        cl = Introspector.decorateClass(PrinterCustomInterface.class);
        PrinterCustomInterface pdog = (PrinterCustomInterface) cl.cast(d);
        pdog.printInfo();
    }

    /**
     * Add non-generic interface to class two times
     */
    @Test
    public void addRepeatedInterfaceTest() {
        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addMethod(DogDHRepeated.class, new Method("compareTo", MethodType.methodType(int.class, Object.class),
                    "return this.name.compareTo(((DogDHRepeated)otherDog).name);", "otherDog"));
            it.addInterface(DogDHRepeated.class, Comparable.class);
            it.addInterface(DogDHRepeated.class, Comparable.class);
            it.commit();
            fail();
        } catch (StructuralIntercessionException e) {
            // e.printStackTrace();
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(DogDHRepeated.class);
        try {
            cl.getMethod("compareTo", Object.class);
            fail();
        } catch (NoSuchMethodException | SecurityException e) {
            return;
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    /**
     * Add generic interface to class two times
     */
    @Test
    public void addRepeatedGenericInterfaceTest() {
        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addMethod(DogDHRepeated.class, new Method("compareTo", MethodType.methodType(int.class, DogDHRepeated.class),
                    "return this.name.compareTo(((DogDHRepeated)otherDog).name);", "otherDog"));
            it.addMethod(DogDHRepeated.class, new Method("compareTo", MethodType.methodType(int.class, DogDH.class),
                    "return this.name.compareTo(((DogDH)otherDog).name);", "otherDog"));
            it.addInterface(DogDHRepeated.class, Comparable.class, DogDH.class);
            it.addInterface(DogDHRepeated.class, Comparable.class, DogDHRepeated.class);
            it.commit();
            fail();
        } catch (StructuralIntercessionException e) {
            // e.printStackTrace();
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(DogDHRepeated.class);
        try {
            cl.getMethod("compareTo", Object.class);
            fail();
        } catch (NoSuchMethodException | SecurityException e) {
            return;
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    /**
     * Remove generic interface to class
     */
    @SuppressWarnings("unchecked")
    @Test
    public void removeGenericInterfaceTest() {
        DogDHRemoveInterface d = new DogDHRemoveInterface("Rufus");
        DogDHRemoveInterface d2 = new DogDHRemoveInterface("Laika");

        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        // Adds a compareTo() method compatible with the Comparable<Dog> interface
        try {
            it.addMethod(DogDHRemoveInterface.class, new Method("compareTo",
                    MethodType.methodType(int.class, DogDHRemoveInterface.class),
                    "return this.name.compareTo(otherDog.name);", "otherDog"));
            it.addInterface(DogDHRemoveInterface.class, Comparable.class, DogDHRemoveInterface.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            fail();
        }

        jmplib.reflect.Class<?> cl = Introspector.decorateClass(DogDHRemoveInterface.class);
        try {
            Method m = cl.getMethod("compareTo", DogDHRemoveInterface.class);
            assertTrue(m != null);
            assertTrue((int) m.invoke(d, d2) != 0);
        } catch (NoSuchMethodException | SecurityException e) {
            fail();
        } catch (IllegalAccessException e) {
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }

        cl = Introspector.decorateClass(Comparable.class);
        Comparable<DogDHRemoveInterface> compdog = (Comparable<DogDHRemoveInterface>) cl.cast(d);
        assertTrue(compdog.compareTo(d2) != 0);

        // Create transaction
        it = new TransactionalIntercessor();

        // Removes the interface, but not the method
        try {
            it.removeInterface(DogDHRemoveInterface.class, Comparable.class);
            it.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            fail();
        }

        // Method call should be still possible
        cl = Introspector.decorateClass(DogDHRemoveInterface.class);
        try {
            Method m = cl.getMethod("compareTo", DogDHRemoveInterface.class);
            assertTrue(m != null);
            assertTrue((int) m.invoke(d, d2) != 0);
        } catch (NoSuchMethodException | SecurityException e) {
            fail();
        } catch (IllegalAccessException e) {
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvocationTargetException e) {
            fail();
        }

        // Casting will no longer be possible
        cl = Introspector.decorateClass(Comparable.class);
        try {
            compdog = (Comparable<DogDHRemoveInterface>) cl.cast(d);
            fail();
        } catch (ClassCastException ex) {
            return;
        }

        cl = Introspector.decorateClass(DogDHRemoveInterface.class);

        jmplib.reflect.Class<?>[] interfs = cl.getInterfaces();
        boolean found = false;
        for (jmplib.reflect.Class<?> i : interfs) {
            if (i.equals(Comparable.class)) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    /**
     * Remove interface to a class that do not implement it
     */
    @Test
    public void removeNonExistingInterfaceTest() {
        // Create transaction
        IIntercessor it = new TransactionalIntercessor();

        try {
            it.removeInterface(DogDHRemoveInterface.class, PrinterCustomInterface.class);
            it.commit();
            fail();
        } catch (StructuralIntercessionException e) {
            return;
        }
    }
}
