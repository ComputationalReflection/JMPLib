package es.uniovi.jmplib.testing.primitives.replacefield;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import jmplib.DefaultIntercessor;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;

import org.junit.Test;

@ExcludeFromJMPLib
public class ReplaceFieldTest {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();

	@Test
	public void testReplaceField() throws StructuralIntercessionException {
		Intercessor.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, "PI", "3.1415f"));
		Class<?> lastVersion = VersionTables.getNewVersion(Dummy.class);
		try {
			Field field = lastVersion.getDeclaredField("PI");
			assertThat(field.getType().getName(), equalTo(float.class.getName()));
		} catch (NoSuchFieldException | SecurityException e) {
			fail("The field exists");
		}
		IIntercessor transaction = new TransactionalIntercessor();
		transaction.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, "PI2", "3.1415f"));
		transaction.commit();
		lastVersion = VersionTables.getNewVersion(Dummy.class);
		try {
			Field field = lastVersion.getDeclaredField("PI2");
			assertThat(field.getType().getName(), equalTo(float.class.getName()));
		} catch (NoSuchFieldException | SecurityException e) {
			fail("The field exists");
		}
	}

	@Test
	public void testReplaceField_ClassNotEditableExceptions() {
		String name = "PI";
		try {
			Intercessor.replaceField(Object.class, new jmplib.reflect.Field(float.class, name, "3.1415"));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
			assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
					+ "be modified because the source file is not accesible"));
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(Object.class, new jmplib.reflect.Field(float.class, name, "3.1415"));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
			assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
					+ "be modified because the source file is not accesible"));
		}
	}

	@Test
	public void testReplaceField_NoSuchFieldException() {
		String badName = "P";
		try {
			Intercessor.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, badName, "3.1415"));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
			assertThat(e.getCause().getMessage(),
					equalTo("The field \"" + badName + "\" does not exist in the class " + Dummy.class.getName()));
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, badName, "3.1415"));
			transaction.commit();
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
			assertThat(e.getCause().getMessage(),
					equalTo("The field \"" + badName + "\" does not exist in the class " + Dummy.class.getName()));
		}
	}

	@Test
	public void testReplaceField_CompilationFailedException() {
		String name = "PI";
		try {
			Intercessor.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, name, "new String()"));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
			assertNotNull(e.getMessage());
			assertNotNull(e.getCause().getMessage());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, name, "new String()"));
			transaction.commit();
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
			assertNotNull(e.getMessage());
			assertNotNull(e.getCause().getMessage());
		}
	}

	@Test
	public void testReplaceField_IllegalArgumentException_NullClass() {
		String name = "PI";
		try {
			Intercessor.replaceField(null, new jmplib.reflect.Field(float.class, name, "3.1415"));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(null, new jmplib.reflect.Field(float.class, name, "3.1415"));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
			assertNull(e.getCause());
		}
	}

	@Test
	public void testReplaceField_IllegalArgumentException_NullName() {
		try {
			Intercessor.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, null));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, null));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
			assertNull(e.getCause());
		}
	}

	@Test
	public void testReplaceField_IllegalArgumentException_EmptyName() {
		try {
			Intercessor.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, "", "3.1415"));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(Dummy.class, new jmplib.reflect.Field(float.class, "", "3.1415"));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
			assertNull(e.getCause());
		}
	}

	@Test
	public void testReplaceField_IllegalArgumentException_NullNewType() {
		try {
			Intercessor.replaceField(Dummy.class, new jmplib.reflect.Field(null, "PI", "3.1415"));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The type parameter cannot be null"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.replaceField(Dummy.class, new jmplib.reflect.Field(null, "PI", "3.1415"));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The type parameter cannot be null"));
			assertNull(e.getCause());
		}
	}

}
