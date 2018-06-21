package es.uniovi.jmplib.testing.annotations;

import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Introspector;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ExcludeFromJMPLib
public class AnnotationTest {
    private static IIntercessor Intercessor = SimpleIntercessor.getInstance();

    @Test
    public void testAddAnnotationToClass() {
        jmplib.reflect.Class<?> cl = Introspector.decorateClass(AnnotationTestClass.class);
        Annotation[] annots = cl.getAnnotations();

        assertTrue(annots.length == 0);
        try {
            Intercessor.addAnnotation(cl, SampleAnnotation.class);
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            fail();
        }
        annots = cl.getAnnotations();
        assertTrue(annots.length == 1);
        assertTrue(annots[0].annotationType() == SampleAnnotation.class);
    }

    @Test
    public void testAddAnnotationToMethod() {
        Annotation[] annots = null;
        try {
            annots = Introspector.decorateClass(AnnotationTestClass.class).getMethod("testMethod").getAnnotations();
        } catch (NoSuchMethodException e1) {
            fail();
        }

        assertTrue(annots.length == 0);
        try {
            Intercessor.addAnnotation(Introspector.decorateClass(AnnotationTestClass.class).getMethod("testMethod"),
                    Deprecated.class);
        } catch (StructuralIntercessionException | NoSuchMethodException | SecurityException e) {
            fail();
        }
        try {
            annots = Introspector.decorateClass(AnnotationTestClass.class).getMethod("testMethod").getAnnotations();
        } catch (NoSuchMethodException e) {
            fail();
        }
        assertTrue(annots.length == 1);
        assertTrue(annots[0].annotationType() == Deprecated.class);
    }

    @Test
    public void testAddMethodAnnotationToClass() {
        Annotation[] annots = Introspector.decorateClass(AnnotationTestClass.class).getAnnotations();

        assertTrue(annots.length == 0);
        try {
            Intercessor.addAnnotation(AnnotationTestClass.class, Override.class);
            fail();
        } catch (StructuralIntercessionException e) {
        }
    }

    @Ignore
    @Test
    public void testAddClassAnnotationToMethod() {
        // TODO - Este test da problemas, se borran todas las anotaciones de la clase al hacer el undo de la primitiva y fallan test posteriores
        Annotation[] annots = null;
        try {
            Intercessor.addAnnotation(Introspector.decorateClass(AnnotationTestClass.class).getMethod("testMethod"),
                    SampleAnnotation.class);
            fail();
        } catch (StructuralIntercessionException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddMultipleAnnotationsToClass() {
        Annotation[] annots = Introspector.decorateClass(AnnotationTestClass2.class).getAnnotations();

        assertTrue(annots.length == 0);
        try {
            Intercessor.setAnnotation(AnnotationTestClass2.class, Deprecated.class, SampleAnnotation.class);
        } catch (StructuralIntercessionException e) {
            fail();
        }
        annots = Introspector.decorateClass(AnnotationTestClass2.class).getAnnotations();
        assertTrue(annots.length == 2);
        assertTrue((annots[0].annotationType() == Deprecated.class)
                || (annots[0].annotationType() == SampleAnnotation.class));
        assertTrue((annots[1].annotationType() == Deprecated.class)
                || (annots[1].annotationType() == SampleAnnotation.class));
    }

    @Test
    public void testAddMultipleAnnotationsToMethod() {
        Annotation[] annots = null;
        try {
            annots = Introspector.decorateClass(AnnotationTestClass2.class).getMethod("testMethod").getAnnotations();
        } catch (NoSuchMethodException e1) {
            fail();
        }

        assertTrue(annots.length == 0);
        try {
            Intercessor.setAnnotation(Introspector.decorateClass(AnnotationTestClass2.class).getMethod("testMethod"),
                    Deprecated.class, SampleAnnotationMethod.class);
        } catch (StructuralIntercessionException | NoSuchMethodException | SecurityException e) {
            fail();
        }
        try {
            annots = Introspector.decorateClass(AnnotationTestClass2.class).getMethod("testMethod").getAnnotations();
        } catch (NoSuchMethodException e) {
            fail();
        }
        assertTrue(annots.length == 2);
        assertTrue((annots[0].annotationType() == Deprecated.class)
                || (annots[0].annotationType() == SampleAnnotationMethod.class));
        assertTrue((annots[1].annotationType() == Deprecated.class)
                || (annots[1].annotationType() == SampleAnnotationMethod.class));
    }
}
