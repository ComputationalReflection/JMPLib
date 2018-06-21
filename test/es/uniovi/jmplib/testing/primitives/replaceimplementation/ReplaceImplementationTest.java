package es.uniovi.jmplib.testing.primitives.replaceimplementation;

import com.github.javaparser.ParseException;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ReplaceImplementationTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();

    @Test
    public void testReplaceImplementation_ExistingMethod() throws StructuralIntercessionException {
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("m1");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("m1"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
        Intercessor.replaceImplementation(Dummy.class, new jmplib.reflect.Method("m1", "return \"mReplaced\";"));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("m1");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("mReplaced"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.replaceImplementation(Dummy.class, new jmplib.reflect.Method("m1", "return \"mReplaced2\";"));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("m1");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("mReplaced2"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
    }

    @Test
    public void testReplaceImplementation_MethodType_ExistingMethod() throws StructuralIntercessionException {
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("m2");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("m2"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
        MethodType type = MethodType.methodType(String.class);
        Intercessor.replaceImplementation(Dummy.class, new jmplib.reflect.Method("m2", type, "return \"mReplaced\";"));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("m2");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("mReplaced"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.replaceImplementation(Dummy.class, new jmplib.reflect.Method("m2", type, "return \"mReplaced2\";"));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("m2");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("mReplaced2"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
    }

    @Test
    public void testReplaceImplementation_AddedMethod() throws StructuralIntercessionException {
        MethodType type = MethodType.methodType(String.class);
        Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method("added", type, "return \"added\";"));
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("added");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("added"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
        Intercessor.replaceImplementation(Dummy.class,
                new jmplib.reflect.Method("added", type, "return \"mReplaced\";"));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("added");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("mReplaced"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.replaceImplementation(Dummy.class,
                new jmplib.reflect.Method("added", type, "return \"mReplaced2\";"));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("added");
            String result = (String) m.invoke(clazz.newInstance());
            assertThat(result, equalTo("mReplaced2"));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            fail("The method exist");
        }
    }

    @Test
    public void testReplaceImplementation_MethodType_IllegalArgumentException_NullClass() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        String name = "multiply", body = "return a * a;";
        try {
            Intercessor.replaceImplementation(null, new jmplib.reflect.Method(name, type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(null, new jmplib.reflect.Method(name, type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_MethodType_IllegalArgumentException_NullName() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String body = "return a * a;";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(null, type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(null, type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_MethodType_IllegalArgumentException_EmptyName() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String body = "return a * a;";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method("", type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method("", type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_MethodType_IllegalArgumentException_NullMethodType() {
        MethodType type = null;
        Class<?> clazz = Dummy.class;
        String name = "multiply", body = "return a * a;";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(name, type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(name, type, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_MethodType_IllegalArgumentException_NullBody() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String name = "multiply";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(name, type, null));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(name, type, null));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_IllegalArgumentException_NullClass() {
        String name = "multiply", body = "return a * a;";
        try {
            Intercessor.replaceImplementation(null, new jmplib.reflect.Method(name, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(null, new jmplib.reflect.Method(name, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_IllegalArgumentException_NullName() {
        Class<?> clazz = Dummy.class;
        String body = "return a * a;";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method((String) null, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method((String) null, body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_IllegalArgumentException_EmptyName() {
        Class<?> clazz = Dummy.class;
        String body = "return a * a;";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method("", body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method("", body));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_IllegalArgumentException_NullBody() {
        Class<?> clazz = Dummy.class;
        String name = "multiply";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(name, (String) null));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(name, (String) null));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testReplaceImplementation_NoSuchMethodException_BadName() {
        Class<?> clazz = Dummy.class;
        String badName = "m", body = "return \"mReplaced\";";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(badName, body));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertThat(e.getCause().getMessage(),
                    equalTo("The method " + badName + " does not exist in the class " + clazz.getName()));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(badName, body));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertThat(e.getCause().getMessage(),
                    equalTo("The method " + badName + " does not exist in the class " + clazz.getName()));
        }
    }

    @Test
    public void testReplaceImplementation_CompilationFailedException() {
        Class<?> clazz = Dummy.class;
        String name = "m3", badBody = "return \"m3\" + a;";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(name, badBody));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getCause().getMessage());
            assertNotNull(((CompilationFailedException) e.getCause()).getCompilationError());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(name, badBody));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getCause().getMessage());
            assertNotNull(((CompilationFailedException) e.getCause()).getCompilationError());
        }
    }

    @Test
    public void testReplaceImplementation_ParseException() {
        Class<?> clazz = Dummy.class;
        String name = "m3";
        String badBodyParsing = "return \"mReplaced\"; ()";
        try {
            Intercessor.replaceImplementation(clazz, new jmplib.reflect.Method(name, badBodyParsing));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(clazz, new jmplib.reflect.Method(name, badBodyParsing));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
        }
    }

    @Test
    public void testReplaceImplementation_ClassnotEditableException() {
        String name = "m3", body = "return \"mReplaced\"";
        try {
            Intercessor.replaceImplementation(Object.class, new jmplib.reflect.Method(name, body));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accessible"));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.replaceImplementation(Object.class, new jmplib.reflect.Method(name, body));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accessible"));
        }
    }

}
