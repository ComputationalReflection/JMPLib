package es.uniovi.jmplib.testing.primitives.deletefield;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import jmplib.DefaultIntercessor;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.StructuralIntercessionException;

@ExcludeFromJMPLib
public class DeleteFieldTest {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();

	@Test
	public void testDeleteField() throws StructuralIntercessionException {
		Class<?> clazz = VersionTables.getNewVersion(Dummy.class);
		try {
			clazz.getField("f2");
		} catch (NoSuchFieldException | SecurityException e) {
			fail("The field exist");
		}
		Intercessor.removeField(Dummy.class, new jmplib.reflect.Field("f2"));
		clazz = VersionTables.getNewVersion(Dummy.class);
		try {
			clazz.getField("f2");
			fail("The field was not deleted");
		} catch (NoSuchFieldException | SecurityException e) {
		}
		clazz = VersionTables.getNewVersion(Dummy.class);
		try {
			clazz.getField("f3");
		} catch (NoSuchFieldException | SecurityException e) {
			fail("The field exist");
		}
		IIntercessor transaction = new TransactionalIntercessor();
		transaction.removeField(Dummy.class, new jmplib.reflect.Field("f3"));
		transaction.commit();
		clazz = VersionTables.getNewVersion(Dummy.class);
		try {
			clazz.getField("f3");
			fail("The field was not deleted");
		} catch (NoSuchFieldException | SecurityException e) {
		}
	}

	@Test
	public void testDeleteField_ClassNotEditableExceptions() {
		String name = "f1";
		try {
			Intercessor.removeField(Object.class, new jmplib.reflect.Field(name));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
			assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
					+ "be modified because the source file is not accesible"));
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.removeField(Object.class, new jmplib.reflect.Field(name));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
			assertThat(e.getCause().getMessage(), equalTo("The class " + Object.class.getName() + " cannot "
					+ "be modified because the source file is not accesible"));
		}
	}

	@Test
	public void testDeleteField_NoSuchFieldException() {
		String badName = "f";
		try {
			Intercessor.removeField(Dummy.class, new jmplib.reflect.Field(badName));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
			assertThat(e.getMessage(),
					equalTo("deleteField could not be executed " + "due to the following reasons: The field \""
							+ badName + "\" does not exist in the class " + Dummy.class.getName()));
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.removeField(Dummy.class, new jmplib.reflect.Field(badName));
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
			assertThat(e.getMessage(),
					equalTo("deleteField could not be executed due to the following reasons: The field \"" + badName + "\" does not exist in the class " + Dummy.class.getName()));
		}
	}

	@Test
	public void testDeleteField_CompilationFailedException() {
		Dummy dummy = new Dummy();
		String f1 = dummy.f1;
		assertThat(f1, equalTo("f1"));
		String name = "f1";
		try {
			Intercessor.removeField(Dummy.class, new jmplib.reflect.Field(name));
			// fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
			fail("This method should not rise an exception");
			/*
			 * assertNotNull(e.getCause()); assertThat(e.getCause(),
			 * instanceOf(CompilationFailedException.class)); assertNotNull(e.getCause());
			 * assertNotNull(e.getCause().getMessage());
			 */
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.removeField(Dummy.class, new jmplib.reflect.Field(name));
			transaction.commit();
			fail("This method should rise an exception");
		} catch (StructuralIntercessionException e) {
			assertNotNull(e.getCause());
			assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
			assertNotNull(e.getCause());
			assertNotNull(e.getCause().getMessage());
		}
	}

	@Test
	public void testDeleteField_IllegalArgumentException_NullClass() {
		String name = "f1";
		try {
			Intercessor.removeField(null, new jmplib.reflect.Field(name));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.removeField(null, new jmplib.reflect.Field(name));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The class parameter cannot be null"));
			assertNull(e.getCause());
		}
	}

	@Test
	public void testDeleteField_IllegalArgumentException_NullName() {
		try {
			Intercessor.removeField(Dummy.class, new jmplib.reflect.Field(null));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.removeField(Dummy.class, new jmplib.reflect.Field(null));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be null"));
			assertNull(e.getCause());
		}
	}

	@Test
	public void testDeleteField_IllegalArgumentException_EmptyName() {
		try {
			Intercessor.removeField(Dummy.class, new jmplib.reflect.Field(""));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
			assertNull(e.getCause());
		}
		IIntercessor transaction = new TransactionalIntercessor();
		try {
			transaction.removeField(Dummy.class, new jmplib.reflect.Field(""));
			fail("This method should rise an exception");
		} catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo("The name parameter cannot be empty"));
			assertNull(e.getCause());
		}
	}

}
