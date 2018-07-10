package es.uniovi.jmplib.testing.primitives.addmethod;

import com.github.javaparser.ParseException;
import jmplib.*;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class AddMethodTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();

    @Test
    public void testAddMethod_InstanceMethod() throws StructuralIntercessionException {
        MethodType methodType = MethodType.methodType(void.class);
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("mInstance");
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method("mInstance", methodType,
                "System.out.println(\"mInstance\");", Modifier.PUBLIC));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("mInstance");
            assertFalse(Modifier.isStatic(m.getModifiers()));
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("mInstance2");
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.addMethod(Dummy.class, new jmplib.reflect.Method("mInstance2", methodType,
                "System.out.println(\"mInstance2\");", Modifier.PUBLIC));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("mInstance2");
            assertFalse(Modifier.isStatic(m.getModifiers()));
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
    }

    @Test
    public void testAddMethod_StaticMethod() throws StructuralIntercessionException {
        MethodType methodType = MethodType.methodType(void.class);
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("mStatic");
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method("mStatic", methodType,
                "System.out.println(\"mStatic\");", Modifier.PUBLIC | Modifier.STATIC));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("mStatic");
            assertTrue(Modifier.isStatic(m.getModifiers()));
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("mStatic2");
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.addMethod(Dummy.class, new jmplib.reflect.Method("mStatic2", methodType,
                "System.out.println(\"mStatic2\");", Modifier.PUBLIC | Modifier.STATIC));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("mStatic2");
            assertTrue(Modifier.isStatic(m.getModifiers()));
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
    }

    @Test
    public void testAddMethod_Overload() throws StructuralIntercessionException {
        MethodType methodType = MethodType.methodType(void.class, double.class);
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("overload", int.class);
        } catch (NoSuchMethodException | SecurityException e) {
            fail("Method to overload missing");
        }
        try {
            clazz.getMethod("overload", double.class);
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method("overload", methodType,
                "System.out.println(\"overload\" + a);", Modifier.PUBLIC, "a"));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("overload", double.class);
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
        methodType = MethodType.methodType(void.class, String.class);
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("overload", String.class);
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.addMethod(Dummy.class, new jmplib.reflect.Method("overload", methodType,
                "System.out.println(\"overload\" + a);", Modifier.PUBLIC, "a"));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("overload", String.class);
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
    }

    @Test
    public void testAddMethod_duplicate() {
        MethodType methodType = MethodType.methodType(void.class, int.class);
        try {
            Intercessor.addMethod(Dummy.class,
                    new jmplib.reflect.Method("overload", methodType, "System.out.println(\"overload\" + a);", "a"));
            fail("The method is duplicated and have to raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getCause().getMessage());
            assertNotNull(((CompilationFailedException) e.getCause()).getCompilationError());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(Dummy.class,
                    new jmplib.reflect.Method("overload", methodType, "System.out.println(\"overload\" + a);", "a"));
            transaction.commit();
            fail("The method is duplicated and have to raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getCause().getMessage());
            assertNotNull(((CompilationFailedException) e.getCause()).getCompilationError());
        }
    }

    @Test
    public void testAddMethod_DeclaringExceptions() throws StructuralIntercessionException {
        MethodType methodType = MethodType.methodType(void.class);
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("mException");
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method(null, "mException", methodType,
                "throw new Exception();", Modifier.PUBLIC, new Class[]{Exception.class}));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("mException");
            assertFalse(Modifier.isStatic(m.getModifiers()));
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
        IIntercessor transaction = new TransactionalIntercessor();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("mException2");
            fail("The method doesn't exist yet");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        transaction.addMethod(Dummy.class, new jmplib.reflect.Method(null, "mException2", methodType,
                "throw new Exception();", Modifier.PUBLIC, new Class[]{Exception.class}));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            Method m = clazz.getMethod("mException2");
            assertFalse(Modifier.isStatic(m.getModifiers()));
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method was not added");
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_NullClass() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        String name = "sum", body = "return a + b;";
        String[] parameterNames = {"a", "b"};
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addMethod(null, new jmplib.reflect.Method(name, type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(null, new jmplib.reflect.Method(name, type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_NullName() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String body = "return a + b;";
        String[] parameterNames = {"a", "b"};
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addMethod(clazz, new jmplib.reflect.Method(null, type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(clazz, new jmplib.reflect.Method(null, type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_EmptyName() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String body = "return a + b;";
        String[] parameterNames = {"a", "b"};
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addMethod(clazz, new jmplib.reflect.Method("", type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(clazz, new jmplib.reflect.Method("", type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_NullMethodType() {
        MethodType type = null;
        Class<?> clazz = Dummy.class;
        String name = "sum", body = "return a + b;";
        String[] parameterNames = {"a", "b"};
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addMethod(clazz, new jmplib.reflect.Method(name, type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(clazz, new jmplib.reflect.Method(name, type, body, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_IncorrectNumberOfParameterNames() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String name = "sum", body = "return a + b;";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addMethod(clazz, new jmplib.reflect.Method(name, type, body, modifiers));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The number of parameter" + " names must match with the number of parameters"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(clazz, new jmplib.reflect.Method(name, type, body, modifiers));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The number of parameter" + " names must match with the number of parameters"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_NullBody() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String name = "sum";
        String[] parameterNames = {"a", "b"};
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addMethod(clazz, new jmplib.reflect.Method(name, type, null, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(clazz, new jmplib.reflect.Method(name, type, null, modifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The body parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_MethodType_IllegalArgumentException_WrongModifer() {
        MethodType type = MethodType.methodType(double.class, double.class, double.class);
        Class<?> clazz = Dummy.class;
        String name = "sum", body = "return a + b;";
        String[] parameterNames = {"a", "b"};
        int badModifiers = Modifier.PRIVATE | Modifier.INTERFACE;
        try {
            Intercessor.addMethod(clazz, new jmplib.reflect.Method(name, type, body, badModifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The modifier combination is incorrect for a method"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(clazz, new jmplib.reflect.Method(name, type, body, badModifiers, parameterNames));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The modifier combination is incorrect for a method"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddMethod_CompilationFailedException() {
        MethodType methodType = MethodType.methodType(void.class);
        try {
            Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method("mCompilationFail", methodType,
                    "System.out.println(\"m1\" + a);", Modifier.PUBLIC));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getCause().getMessage());
            assertNotNull(((CompilationFailedException) e.getCause()).getCompilationError());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(Dummy.class, new jmplib.reflect.Method("mCompilationFail", methodType,
                    "System.out.println(\"m1\" + a);", Modifier.PUBLIC));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getCause().getMessage());
            assertNotNull(((CompilationFailedException) e.getCause()).getCompilationError());
        }
    }

    @Test
    public void testAddMethod_ParseException() {
        MethodType methodType = MethodType.methodType(void.class);
        try {
            Intercessor.addMethod(Dummy.class, new jmplib.reflect.Method("mParseFail", methodType,
                    "System.out.println(\"m1\"); ()", Modifier.PUBLIC));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
            assertNotNull(e.getCause().getMessage());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(Dummy.class, new jmplib.reflect.Method("mParseFail", methodType,
                    "System.out.println(\"m1\"); ()", Modifier.PUBLIC));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
            assertNotNull(e.getCause().getMessage());
        }
    }

    @Test
    public void testAddMethod_ClassNotEditableException() {
        MethodType methodType = MethodType.methodType(void.class);
        try {
            Intercessor.addMethod(Object.class,
                    new jmplib.reflect.Method("m1", methodType, "System.out.println(\"m1\");", Modifier.PUBLIC));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accessible"));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addMethod(Object.class,
                    new jmplib.reflect.Method("m1", methodType, "System.out.println(\"m1\");", Modifier.PUBLIC));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accessible"));
        }
    }

    @Test
    public void testAddMethod_OwnClass() {
        Dummy2 d = new Dummy2();

        d.addPrintMethod();
        IEvaluator ev = new SimpleEvaluator().createEvaluator();
        BiFunction<Dummy2, Integer, Integer> m = null;
        try {
            m = ev.getMethodInvoker(Dummy2.class,
                    "print", new MemberInvokerData<>(BiFunction.class, Modifier.PUBLIC, new Class[]{
                            Dummy2.class, int.class, int.class}));
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            fail();
        }
        int x = m.apply(d, 4);
        assertEquals(4, x);
    }
}
