package es.uniovi.jmplib.testing.dynamicinheritance;

import es.uniovi.jmplib.testing.dynamicinheritance.classes.*;
import es.uniovi.jmplib.testing.reflection.StructuralChanges;
import jmplib.SimpleEvaluator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import jmplib.util.intercessor.IntercessorUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class DynamicInheritanceSuperClassTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    /**
     * This is necessary due to the code generation problems that the first stages
     * of phase two have. If the library source code cache is not cleared after each
     * test, incorrectly generated code may produce crashes in further tests that
     * have not problems.
     */

    @After
    public void init() {
        IntercessorUtils.resetLibraryState();
    }

    /**
     * Helper method to the tests, creates a dog list
     *
     * @return
     */
    private List<DogDHSetSuper> getDogList() {
        DogDHSetSuper d1 = new DogDHSetSuper("Rufus");
        DogDHSetSuper d2 = new DogDHSetSuper("Laika");
        DogDHSetSuper d3 = new DogDHSetSuper("Lassie");
        List<DogDHSetSuper> testList = new ArrayList<DogDHSetSuper>();
        testList.add(d1);
        testList.add(d2);
        testList.add(d3);
        return testList;
    }

    /**
     * Change superclass test from the ENASE paper
     */
    @Ignore
    @SuppressWarnings({"unchecked", "unused"})
    @Test
    public void changeSuperClassTest() {
        // Obtain a Dog list
        List<DogDHSetSuper> dogs = getDogList();
        int previousSize = dogs.size();

        //TO DO

        // Sort as a Comparable (by dog name)
		/*
		try {
			Evaluator.generateEvalInvoker(
					"java.util.Collections.sort((java.util.List<es.uniovi.jmplib.testing.dynamicinheritance.classes.DogDHSetSuper>)dogs);",
					new EvalInvokerData<>(Consumer.class, new String[] { "dogs" }, List.class)).accept(dogs);
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
			fail();
		}*/

        // Change the superclass of Dog
        try {
            Intercessor.setSuperclass(DogDHSetSuper.class, Pet.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
            fail();
        }

        // * WARNING * Lambda usage is not supported due to Polyglot not supporting 1.8
        // features.
        // Lambda expression to sort using pet age
        // Comparator<Pet> sortCriteria = (pet1, pet2) -> pet1.getAge() - pet2.getAge();
        //
        // // Sort using Pet attributes, since now is our parent class
        // try {
        // Evaluator
        // .generateEvalInvoker("Collections.sort(pets, criteria);", BiConsumer.class,
        // new String[] { "pets", "criteria" }, List.class, Comparator.class)
        // .accept(dogs, sortCriteria);
        // } catch (StructuralIntercessionException e) {
        // e.printStackTrace();
        // fail();
        // }
        //
        // assertTrue(previousSize == dogs.size());
        fail(); //Unfinished
    }

    /**
     * Changes the superclass of a class from Animal to Per and try to cast to it
     * using jmplib.reflect, access to its old methods, new methods and own methods
     */
    @SuppressWarnings("unused")
    @Test
    public void setNonGenericSuperClassTest() {
        DogDHSetSuper2 dog = new DogDHSetSuper2("Rufus");

        jmplib.reflect.Class<?> dogClass = Introspector.getClass(dog);

        // Access parent method
        try {
            Method mget = dogClass.getMethod("getSpecies");
            Method mset = dogClass.getMethod("setSpecies", String.class);
            mset.invoke(dog, "Dinosaur");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Dinosaur"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the parent
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Animal a = (Animal) animalClass.cast(dog);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Change the superclass of Dog
        try {
            Intercessor.setSuperclass(DogDHSetSuper2.class, Pet.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
            fail();
        }

        // Old parent method are no longer accessible
        try {
            Method mget = dogClass.getMethod("getSpecies");
            fail();
        } catch (NoSuchMethodException e) {
        }

        // New parent methods are now available
        try {
            Method mget = dogClass.getMethod("getAge");
            Method mset = dogClass.getMethod("setAge", int.class);
            mset.invoke(dog, 23);
            int age = (int) mget.invoke(dog);
            assertTrue(age == 23);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Own methods are still usable
        try {
            Method mget = dogClass.getMethod("getName");
            Method mset = dogClass.getMethod("setName", String.class);
            mset.invoke(dog, "Laika");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Laika"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the old parent fails
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Animal a = (Animal) animalClass.cast(dog);
            fail();
        } catch (ClassCastException e) {
        }
        // Casting to the new parent is possible
        try {
            jmplib.reflect.Class<?> petClass = Introspector.decorateClass(Pet.class);

            Pet a = (Pet) petClass.cast(dog);
        } catch (ClassCastException e) {
            fail();
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    @Test
    public void setGenericSuperClassTest() {
        DogDHSetSuper4 dog = new DogDHSetSuper4("Rufus");

        jmplib.reflect.Class<?> dogClass = Introspector.getClass(dog);

        // Access parent method
        try {
            Method mget = dogClass.getMethod("getSpecies");
            Method mset = dogClass.getMethod("setSpecies", String.class);
            mset.invoke(dog, "Dinosaur");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Dinosaur"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the parent
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Animal a = (Animal) animalClass.cast(dog);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Change the superclass of Dog
        try {
            Intercessor.setSuperclass(DogDHSetSuper4.class, GenericPet.class, Float.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
            fail();
        }

        // Old parent method are no longer accessible
        try {
            Method mget = dogClass.getMethod("getSpecies");
            fail();
        } catch (NoSuchMethodException e) {
        }

        // New parent methods are now available
        try {
            Method mget = dogClass.getMethod("getGenericProperty");
            Method mset = dogClass.getMethod("setGenericProperty", Object.class);
            mset.invoke(dog, 43.0f);
            float genval = (float) mget.invoke(dog);
            assertTrue(genval == 43.0f);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Own methods are still usable
        try {
            Method mget = dogClass.getMethod("getName");
            Method mset = dogClass.getMethod("setName", String.class);
            mset.invoke(dog, "Laika");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Laika"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the old parent fails
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Animal a = (Animal) animalClass.cast(dog);
            fail();
        } catch (ClassCastException e) {
        }

        // Casting to the new parent is possible
        try {
            jmplib.reflect.Class<?> petClass = Introspector.decorateClass(GenericPet.class);

            GenericPet<Float> a = (GenericPet<Float>) petClass.cast(dog);
        } catch (ClassCastException e) {
            fail();
        }
    }

    /**
     * Try to assign a class superclass to itself.
     */
    @Test
    public void setOwnSuperClassTest() {
        jmplib.reflect.Class<?> dogClass = Introspector.decorateClass(DogDHSetSuper2.class);
        jmplib.reflect.Class<?> oldParent = dogClass.getSuperclass();

        // Change the superclass of Dog to itself
        try {
            Intercessor.setSuperclass(DogDHSetSuper2.class, DogDHSetSuper2.class);
            fail();
        } catch (StructuralIntercessionException e1) {
            fail();
        } catch (IllegalArgumentException e2) {
        }
        jmplib.reflect.Class<?> newParent = dogClass.getSuperclass();
        assertTrue(oldParent.equals(newParent));
    }

    /**
     * Set a class superclass to one final class
     */
    @Test
    public void setFinalSuperClassTest() {
        jmplib.reflect.Class<?> dogClass = Introspector.decorateClass(DogDHSetSuper2.class);
        jmplib.reflect.Class<?> oldParent = dogClass.getSuperclass();

        // Change the superclass to a final class
        try {
            Intercessor.setSuperclass(DogDHSetSuper2.class, FinalAnimal.class);
            fail();
        } catch (StructuralIntercessionException e1) {
            fail();
        } catch (IllegalArgumentException e2) {
        }
        jmplib.reflect.Class<?> newParent = dogClass.getSuperclass();
        assertTrue(oldParent.equals(newParent));
    }

    /**
     * Get the new superclass of a class once changed dynamically through
     * jmplib.reflect
     */
    @Test
    public void getSuperClassTest() {
        jmplib.reflect.Class<?> dogClass = Introspector.decorateClass(DogDHSetSuper3.class);
        jmplib.reflect.Class<?> oldParent = dogClass.getSuperclass();

        // Change the superclass of Dog to itself
        try {
            Intercessor.setSuperclass(DogDHSetSuper3.class, Pet.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
            fail();
        }
        jmplib.reflect.Class<?> newParent = dogClass.getSuperclass();
        assertTrue(!oldParent.equals(newParent));
        assertTrue(newParent.equals(Pet.class));
    }

    /**
     * Delete the superclass of a class
     */
    @SuppressWarnings("unused")
    @Test
    public void removeSuperClassTest() {
        DogDHSetSuper5 dog = new DogDHSetSuper5("Rufus");

        jmplib.reflect.Class<?> dogClass = Introspector.getClass(dog);

        // Access parent method
        try {
            Method mget = dogClass.getMethod("getSpecies");
            Method mset = dogClass.getMethod("setSpecies", String.class);
            mset.invoke(dog, "Dinosaur");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Dinosaur"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the parent
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Object a = animalClass.cast(dog);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Change the superclass of Dog
        try {
            Intercessor.removeSuperclass(DogDHSetSuper5.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
            fail();
        }

        // Old parent method are no longer accessible
        try {
            Method mget = dogClass.getMethod("getSpecies");
            fail();
        } catch (NoSuchMethodException e) {
        }

        // Own methods are still usable
        try {
            Method mget = dogClass.getMethod("getName");
            Method mset = dogClass.getMethod("setName", String.class);
            mset.invoke(dog, "Laika");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Laika"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the old parent fails
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Animal a = (Animal) animalClass.cast(dog);
            fail();
        } catch (ClassCastException e) {
        }
    }

    /**
     * Get the superclass of a class using jmplib.reflect after it has been removed.
     */
    @Test
    public void getSuperClassAfterRemovalTest() {
        jmplib.reflect.Class<?> dogClass = Introspector.decorateClass(DogDHSetSuper6.class);
        jmplib.reflect.Class<?> oldParent = dogClass.getSuperclass();

        // Change the superclass of Dog to itself
        try {
            Intercessor.removeSuperclass(DogDHSetSuper6.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
        }
        jmplib.reflect.Class<?> newParent = dogClass.getSuperclass();
        assertTrue(!oldParent.equals(newParent));
        assertTrue(newParent.equals(Object.class));
    }

    /**
     * Change the superclass of a class to a class that has been versioned
     * dynamically.
     */
    // @Test
    @SuppressWarnings("unused")
    public void setNonGenericSuperClassParentEvolutionTest() {
        DogDHSetSuper7 dog = new DogDHSetSuper7("Rufus");

        jmplib.reflect.Class<?> dogClass = Introspector.getClass(dog);
        // Evolve new parent class
        StructuralChanges.addFieldAndGetterSetter(Pet.class, int.class, "height", Modifier.PUBLIC);

        // Change the superclass of Dog
        try {
            Intercessor.setSuperclass(DogDHSetSuper7.class, Pet.class);
        } catch (StructuralIntercessionException e1) {
            e1.printStackTrace();
            fail();
        }

        // Old parent method are no longer accessible
        try {
            dogClass.getMethod("getSpecies");
            fail();
        } catch (NoSuchMethodException e) {
        }

        // New parent methods are now available
        try {
            Method mget = dogClass.getMethod("getAge");
            Method mset = dogClass.getMethod("setAge", int.class);
            mset.invoke(dog, 23);
            int age = (int) mget.invoke(dog);
            assertTrue(age == 23);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Parent added methods are usable
        try {
            Method mget = dogClass.getMethod("getHeight");
            Method mset = dogClass.getMethod("setHeight", int.class);
            mset.invoke(dog, 56);
            int s = (int) mget.invoke(dog);
            assertTrue(s == 56);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Own methods are still usable
        try {
            Method mget = dogClass.getMethod("getName");
            Method mset = dogClass.getMethod("setName", String.class);
            mset.invoke(dog, "Laika");
            String s = (String) mget.invoke(dog);
            assertTrue(s.equals("Laika"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        // Casting to the old parent fails
        try {
            jmplib.reflect.Class<?> animalClass = Introspector.decorateClass(Animal.class);

            Animal a = (Animal) animalClass.cast(dog);
            fail();
        } catch (ClassCastException e) {
        }
        // Casting to the new parent is possible
        try {
            jmplib.reflect.Class<?> petClass = Introspector.decorateClass(Pet.class);

            Pet a = (Pet) petClass.cast(dog);
        } catch (ClassCastException e) {
            fail();
        }
    }
}
