package es.uniovi.jmplib.testing.imports;

import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ExcludeFromJMPLib
public class ImportTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();

    @Test
    public void testAddImport() {
        try {
            Intercessor.addImport(ImportTestClass.class, Package.getPackage("java.util"));
        } catch (StructuralIntercessionException e) {
            fail();
        }

        try {
            AnnotatedElement[] packages = Intercessor.getImports(ImportTestClass.class);
            assertTrue(packages.length == 1);

            assertTrue(packages[0] == Package.getPackage("java.util"));
        } catch (StructuralIntercessionException e) {
            fail();
        }
    }

    @Test
    public void testAddUnnecessaryImport() {
        try {
            Intercessor.addImport(ImportTestClass.class, Class.forName("java.awt.Window"));
        } catch (StructuralIntercessionException e) {
            fail();
        } catch (ClassNotFoundException e) {
            fail();
        }

        try {
            AnnotatedElement[] packages = Intercessor.getImports(ImportTestClass.class);
            boolean correct = false;
            for (AnnotatedElement e : packages)
                if (e == Class.forName("java.awt.Window")) {
                    correct = true;
                    break;
                }

            if (!correct)
                fail();
        } catch (StructuralIntercessionException e) {
            fail();
        } catch (ClassNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testAddImportSolvingError() {
        try {
            Intercessor.addMethod(ImportTestClass2.class,
                    new jmplib.reflect.Method("failedMethod", MethodType.methodType(void.class), "new Vector();"));
            fail();
        } catch (StructuralIntercessionException e) {

        }

        try {
            Intercessor.addImport(ImportTestClass2.class, Package.getPackage("java.util"));
            Intercessor.addMethod(ImportTestClass2.class,
                    new jmplib.reflect.Method("failedMethod", MethodType.methodType(void.class), "new Vector();"));
        } catch (StructuralIntercessionException e) {
            fail();
        }
    }

    @Test
    public void testReplaceNecessaryImport() {
        try {
            Intercessor.setImports(ImportTestClass2.class, null);
            fail();
        } catch (StructuralIntercessionException e) {
        }
    }

    @Test
    public void testReplaceImportSolvingError() {
        try {
            Intercessor.addMethod(ImportTestClass3.class,
                    new jmplib.reflect.Method("failedMethod2", MethodType.methodType(void.class), "new Window(null);"));
            fail();
        } catch (StructuralIntercessionException e) {

        }

        try {
            Intercessor.setImports(ImportTestClass3.class, Class.forName("java.awt.Window"));
            Intercessor.addMethod(ImportTestClass3.class,
                    new jmplib.reflect.Method("failedMethod2", MethodType.methodType(void.class), "new Window(null);"));
        } catch (StructuralIntercessionException | ClassNotFoundException e) {
            fail();
        }
    }
}
