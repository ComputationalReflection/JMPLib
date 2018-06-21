package es.uniovi.jmplib.testing.primitives.deletemethod;

import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.StructuralIntercessionException;
import org.junit.Test;

import java.lang.invoke.MethodType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class DeleteMethodTest {
    private static IIntercessor Intercessor = SimpleIntercessor.getInstance();

    @Test
    public void testDeleteMethod() throws StructuralIntercessionException {
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("m2");
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method exist");
        }
        Intercessor.removeMethod(Dummy.class, new jmplib.reflect.Method("m2"));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("m2");
            fail("The method was not deleted");
        } catch (NoSuchMethodException | SecurityException e) {
        }
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("m3");
        } catch (NoSuchMethodException | SecurityException e) {
            fail("The method exist");
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.removeMethod(Dummy.class, new jmplib.reflect.Method("m3"));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getMethod("m3");
            fail("The method was not deleted");
        } catch (NoSuchMethodException | SecurityException e) {
        }
    }

    @Test
    public void testDeleteMethod_CompilationFailedException() throws StructuralIntercessionException {
        Dummy dummy = new Dummy();
        String actual = dummy.m1();
        assertThat(actual, equalTo("1"));
        try {
            Intercessor.removeMethod(Dummy.class, new jmplib.reflect.Method("m1"));
            //fail("The method should rise an exception");
        } catch (Exception e) {
            fail("The method should not rise an exception");
			/*assertThat(e, instanceOf(StructuralIntercessionException.class));
			assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
			actual = dummy.m1();
			assertThat(actual, equalTo("1"));*/
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(Dummy.class, new jmplib.reflect.Method("m1"));
            transaction.commit();
            fail("The method should rise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(StructuralIntercessionException.class));
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            actual = dummy.m1();
            assertThat(actual, equalTo("1"));
        }
    }

    @Test
    public void testDeleteMethod_MethodType_IllegalArgumentException_NullClass() {
        MethodType type = MethodType.methodType(double.class, double.class,
                double.class);
        String name = "m1";
        try {
            Intercessor.removeMethod(null, new jmplib.reflect.Method(name, type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(null, new jmplib.reflect.Method(name, type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_MethodType_IllegalArgumentException_NullName() {
        MethodType type = MethodType.methodType(double.class, double.class,
                double.class);
        Class<?> clazz = Dummy.class;
        try {
            Intercessor.removeMethod(clazz, new jmplib.reflect.Method(null, type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(clazz, new jmplib.reflect.Method(null, type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_MethodType_IllegalArgumentException_EmptyName() {
        MethodType type = MethodType.methodType(double.class, double.class,
                double.class);
        Class<?> clazz = Dummy.class;
        try {
            Intercessor.removeMethod(clazz, new jmplib.reflect.Method("", type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(clazz, new jmplib.reflect.Method("", type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_MethodType_IllegalArgumentException_NullMethodType() {
        MethodType type = null;
        Class<?> clazz = Dummy.class;
        String name = "m1";
        try {
            Intercessor.removeMethod(clazz, new jmplib.reflect.Method(name, type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(clazz, new jmplib.reflect.Method(name, type));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The methodType parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_IllegalArgumentException_NullClass() {
        String name = "m1";
        try {
            Intercessor.removeMethod(null, new jmplib.reflect.Method(name));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(null, new jmplib.reflect.Method(name));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_IllegalArgumentException_NullName() {
        Class<?> clazz = Dummy.class;
        try {
            Intercessor.removeMethod(clazz, new jmplib.reflect.Method((String) null));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(clazz, new jmplib.reflect.Method((String) null));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_IllegalArgumentException_EmptyName() {
        Class<?> clazz = Dummy.class;
        try {
            Intercessor.removeMethod(clazz, new jmplib.reflect.Method(""));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(clazz, new jmplib.reflect.Method(""));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                    equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testDeleteMethod_NoSuchMethodException_WrongName() {
        Class<?> clazz = Dummy.class;
        String badName = "m";
        try {
            Intercessor.removeMethod(clazz, new jmplib.reflect.Method(badName));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertThat(
                    e.getCause().getMessage(),
                    equalTo("The method " + badName
                            + " does not exist in the class " + clazz.getName()));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(clazz, new jmplib.reflect.Method(badName));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertThat(
                    e.getCause().getMessage(),
                    equalTo("The method " + badName
                            + " does not exist in the class " + clazz.getName()));
        }
    }

    @Test
    public void testDeleteMethod_ClassNotEditableException() {
        String name = "m1";
        try {
            Intercessor.removeMethod(Object.class, new jmplib.reflect.Method(name));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(),
                    instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class "
                    + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accesible"));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.removeMethod(Object.class, new jmplib.reflect.Method(name));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(),
                    instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class "
                    + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accesible"));
        }
    }
}
