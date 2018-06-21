package es.uniovi.jmplib.testing.primitives.addfield;

import com.github.javaparser.ParseException;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class AddFieldTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();

    @Test
    public void testAddField() throws StructuralIntercessionException {
        String init = "new Double(1);";
        int modifiers = Modifier.PUBLIC;
        Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getField("f2");
            fail("The field already exist");
        } catch (NoSuchFieldException | SecurityException e) {
        }
        Intercessor.addField(Dummy.class, new jmplib.reflect.Field(modifiers, Double.class, "f2", init));
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getField("f2");
        } catch (NoSuchFieldException | SecurityException e) {
            fail("The field was not added");
        }
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getField("f3");
            fail("The field already exist");
        } catch (NoSuchFieldException | SecurityException e) {
        }
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.addField(Dummy.class, new jmplib.reflect.Field(modifiers, Double.class, "f3", init));
        transaction.commit();
        clazz = VersionTables.getNewVersion(Dummy.class);
        try {
            clazz.getField("f3");
        } catch (NoSuchFieldException | SecurityException e) {
            fail("The field was not added");
        }
    }

    @Test
    public void testAddField_IllegalArgumentException_NullClass() {
        String name = "f1";
        String init = "new Double(1);";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(null, new jmplib.reflect.Field(modifiers, Double.class, name, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(null, new jmplib.reflect.Field(modifiers, Double.class, name, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddField_IllegalArgumentException_NullName() {
        Class<?> clazz = Dummy.class;
        String init = "new Double(1);";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, null, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, null, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddField_IllegalArgumentException_EmptyName() {
        Class<?> clazz = Dummy.class;
        String init = "new Double(1);";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, "", init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, "", init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddField_IllegalArgumentException_NullType() {
        Class<?> clazz = Dummy.class;
        String name = "f1";
        String init = "new Double(1);";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, null, name, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The type parameter cannot be null"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, null, name, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The type parameter cannot be null"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddField_IllegalArgumentException_WrongModifier() {
        Class<?> clazz = Dummy.class;
        String name = "f1";
        String init = "new Double(1);";
        int badModifiers = Modifier.ABSTRACT;
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(badModifiers, Double.class, name, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The modifier combination is incorrect for a field"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(badModifiers, Double.class, name, init));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The modifier combination is incorrect for a field"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddField_IllegalArgumentException_EmptyInitialize() {
        Class<?> clazz = Dummy.class;
        String name = "f1";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, name, ""));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The init parameter cannot be empty"));
            assertNull(e.getCause());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, name, ""));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(), equalTo("The init parameter cannot be empty"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void testAddField_ClassNotEditableException() {
        String name = "f1";
        String init = "new Double(1);";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(Object.class, new jmplib.reflect.Field(modifiers, Double.class, name, init));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accessible"));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(Object.class, new jmplib.reflect.Field(modifiers, Double.class, name, init));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
                    + "be modified because the source file is not accessible"));
        }
    }

    @Test
    public void testAddField_ParseException() {
        Class<?> clazz = Dummy.class;
        String name = "f1";
        String badInitParsing = " = new ();";
        int modifiers = Modifier.PUBLIC;
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, name, badInitParsing));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, name, badInitParsing));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
        }
    }

    @Test
    public void testAddField_CompilationFailedException_Duplicate() {
        Class<?> clazz = Dummy.class;
        String duplicateName = "fOriginal";
        int modifiers = Modifier.PUBLIC;
        String init = "new Double(1);";
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, duplicateName, init));
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, duplicateName, init));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (StructuralIntercessionException e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    @Test
    public void testAddField_CompilationFailedException_BadInit() {
        Class<?> clazz = Dummy.class;
        String name = "f1";
        int modifiers = Modifier.PUBLIC;
        String badInitCompiling = "new String();";
        try {
            Intercessor.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, name, badInitCompiling));
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
        IIntercessor transaction = new TransactionalIntercessor();
        try {
            transaction.addField(clazz, new jmplib.reflect.Field(modifiers, Double.class, name, badInitCompiling));
            transaction.commit();
            fail("The method should raise an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

}
