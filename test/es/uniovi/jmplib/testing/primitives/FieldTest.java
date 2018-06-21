package es.uniovi.jmplib.testing.primitives;

import es.uniovi.jmplib.testing.common.*;
import jmplib.SimpleEvaluator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.CompilationFailedException;
import jmplib.invokers.MemberInvokerData;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

//import jmplib.Intercessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExcludeFromJMPLib
public class FieldTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    @Test
    public void _01_addExistingAttribute() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();

        try {
            // Ok
            Intercessor.addField(ErrorTargetClassChild.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "dynamicAttribute"));
            Intercessor.addField(ErrorTargetClassParent.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "dynamicAttributeParent"));

            // This field exist in the parent class: ERROR
            Intercessor.addField(ErrorTargetClassParent.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "existingAttribute"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Attribute Access checks

        try {
            // Dynamic field
            Func_int_ErrorTargetClassChild getter = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "dynamicAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            assertTrue(getter.invoke(child) == 0);
            assertTrue(getter.invoke((ErrorTargetClassChild) childPoly) == 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail("fail getting attribute");
        }

        try {
            // Field in parent class
            Func_int_ErrorTargetClassParent getter2 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassParent>(Func_int_ErrorTargetClassParent.class));

            assertTrue(getter2.invoke(parent) == 5);
            assertTrue(getter2.invoke(child) == 5);
            assertTrue(getter2.invoke(childPoly) == 5);
        } catch (Exception e) {
            fail("fail getting attribute");
        }

        // Existing field inherited from parent class in child class
        try {
            Func_int_ErrorTargetClassChild getter3 = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            boolean result = (getter3.invoke(child) == 5);
            if (!result)
                throw new Exception();

            result = (getter3.invoke((ErrorTargetClassChild) childPoly) == 5);
            if (!result)
                throw new Exception();
        } catch (Exception e) {
            fail("MODEL ERROR: Accessing a public inherited field from a child class should not be an error");
        }

        // Dynamic field inherited from parent class in child class
        try {
            Func_int_ErrorTargetClassChild getter3 = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "dynamicAttributeParent",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            assertTrue(getter3.invoke(child) == 0);
            assertTrue(getter3.invoke((ErrorTargetClassChild) childPoly) == 0);
        } catch (Exception e) {
            fail("MODEL ERROR: Accessing a public inherited field from a child class should not be an error");
        }
    }

    @Test
    public void _02_addExistingDynamicAttribute() {
        try {
            Intercessor.addField(ErrorTargetClassChild.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "dynamicAttribute"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    @Test
    public void _03_hideExistingAttribute() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        try {
            // Hides the parent attribute, although it is the same signature
            Intercessor.addField(ErrorTargetClassChild.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "existingAttribute"));
        } catch (Exception e) {
            fail("MODEL ERROR: Adding this field should hide the parent field, not fail");
        }

        // Attribute Access checks
        try {
            // Dynamic field
            Func_int_ErrorTargetClassChild getter = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "dynamicAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            assertTrue(getter.invoke(child) == 0);
            assertTrue(getter.invoke((ErrorTargetClassChild) childPoly) == 0);
        } catch (Exception e) {
            fail("Failed obtaining attribute");
        }

        // Field in parent class
        try {
            Func_int_ErrorTargetClassParent getter2 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassParent>(Func_int_ErrorTargetClassParent.class));

            assertTrue(getter2.invoke(parent) == 5);
            assertTrue(getter2.invoke(child) == 5);
            assertTrue(getter2.invoke(childPoly) == 5);
        } catch (Exception e) {
            fail("Failed obtaining attribute");
        }

        // Existing field inherited from parent class in child class
        try {
            Func_int_ErrorTargetClassParent getter3 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassParent>(Func_int_ErrorTargetClassParent.class));

            boolean result = (getter3.invoke(child) == 5);
            if (!result)
                throw new Exception();

            result = (getter3.invoke((ErrorTargetClassChild) childPoly) == 5);
            if (!result)
                throw new Exception();
        } catch (Exception e) {
            fail("MODEL ERROR: Accessing a public inherited field from a child class should not be an error");
        }

        // Dynamic field inherited from parent class in child class
        try {
            Func_int_ErrorTargetClassChild getter3 = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "dynamicAttributeParent",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            assertTrue(getter3.invoke(child) == 0);
            assertTrue(getter3.invoke((ErrorTargetClassChild) childPoly) == 0);
        } catch (Exception e) {
            fail("MODEL ERROR: Accessing a public inherited field from a child class should not be an error");
        }
    }

    @Test
    public void _04_hideDynamicAttribute() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        try {
            // Hides the parent attribute. This time we try with a different
            // type
            Intercessor.addField(ErrorTargetClassChild.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, String.class, "dynamicAttributeParent"));
        } catch (Exception e) {
            fail("MODEL ERROR: Adding this field should hide the parent field, not fail");
            System.err.flush();
        }

        // Attribute Access checks

        // Field obtained from parent class
        try {
            Func_int_ErrorTargetClassParent getter2 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "dynamicAttributeParent",
                    new MemberInvokerData<Func_int_ErrorTargetClassParent>(Func_int_ErrorTargetClassParent.class));

            assertTrue(getter2.invoke(parent) == 0);
            assertTrue(getter2.invoke(child) == 0);
            assertTrue(getter2.invoke(childPoly) == 0);
        } catch (Exception e) {
            fail("Error obtaining fields");
        }

        // Dynamic field inherited from parent class in child class
        try {
            // My hidden Dynamic field
            Func_String_ErrorTargetClassChild getter = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "dynamicAttributeParent",
                    new MemberInvokerData<Func_String_ErrorTargetClassChild>(Func_String_ErrorTargetClassChild.class));

            boolean result = (getter.invoke(child) == null);
            if (!result)
                throw new RuntimeException();
            result = (getter.invoke((ErrorTargetClassChild) childPoly) == null);
            if (!result)
                throw new RuntimeException();
        } catch (Exception e) {
            fail("MODEL ERROR: Accessing a public dynamic inherited field from a child class should not be an error");
        }
    }

    // Add an attribute to an interface
    @Test
    public void _05_addAttributeToInterface() {
        try {
            Intercessor.addField(ErrorTargetInterface.class,
                    new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "dynamicAttribute"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

    }

    // Replace a used attribute with an incompatible type
    @Test
    public void _06_replaceIncompatibleAttribute() {
        try {
            // Added in the previous function: ERROR
            Intercessor.replaceField(ErrorTargetClassParent.class, new jmplib.reflect.Field(int.class, "txt"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Replace a unexisting attribute
    @Test
    public void _07_replaceUnexistingAttribute() {
        try {
            // Added in the previous function: ERROR
            Intercessor.replaceField(ErrorTargetClassParent.class, new jmplib.reflect.Field(int.class, "unexisting"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Replace a field from an interface
    @Test
    public void _08_replaceFieldFromInterface() {
        try {
            // Added in the previous function: ERROR
            Intercessor.replaceField(ErrorTargetInterface.class, new jmplib.reflect.Field(int.class, "txt"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete a unexisting attribute (include multiple deletion of the same
    // attribute)
    @Test
    public void _09_deleteUnexistingAttribute() {
        try {
            // Added in the previous function: ERROR
            Intercessor.removeField(ErrorTargetClassParent.class, new jmplib.reflect.Field("unexisting"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete a inherited attribute
    @Test
    public void _10_deleteInheritedAttribute() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        try {
            // Delete in the child an attribute that already has the parent =>
            // OK
            Intercessor.removeField(ErrorTargetClassChild.class, new jmplib.reflect.Field("existingAttribute"));

            // Delete in the child an attribute defined in the parent => ERROR
            Intercessor.removeField(ErrorTargetClassChild.class, new jmplib.reflect.Field("foo"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        try {
            // Field in parent class is not deleted
            Func_int_ErrorTargetClassParent getter = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassParent>(Func_int_ErrorTargetClassParent.class));

            assertTrue(getter.invoke(parent) == 5);
            assertTrue(getter.invoke(child) == 5);
            assertTrue(getter.invoke(childPoly) == 5);
        } catch (Exception e) {
            fail("Error obtaining attributes");
        }

        // Existing field inherited from parent class in child class
        try {
            Func_int_ErrorTargetClassChild getter2 = Evaluator.getFieldInvoker(ErrorTargetClassChild.class, "foo",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            assertTrue(getter2.invoke(child) == 1);
            assertTrue(getter2.invoke((ErrorTargetClassChild) childPoly) == 1);

        } catch (Exception e) {
            fail("Error obtaining attributes");
        }
    }

    // Delete a used attribute
    @Test
    public void _11_deleteUsedAttribute() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        // Static
        try {
            // Used attribute: ERROR
            Intercessor.removeField(ErrorTargetClassChild.class, new jmplib.reflect.Field("txt"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        try {
            // Field in parent class
            Func_String_ErrorTargetClassParent getter = Evaluator.getFieldInvoker(ErrorTargetClassParent.class, "txt",
                    new MemberInvokerData<Func_String_ErrorTargetClassParent>(
                            Func_String_ErrorTargetClassParent.class));

            // Still present
            assertTrue(getter.invoke(parent).equals("hi"));
            assertTrue(getter.invoke(child).equals("hi"));
            assertTrue(getter.invoke(childPoly).equals("hi"));
        } catch (Exception e) {
            fail("Errors obtainign the fields");
        }

        // Existing field inherited from parent class in child class
        try {
            Func_String_ErrorTargetClassChild getter2 = Evaluator.getFieldInvoker(ErrorTargetClassChild.class, "txt",
                    new MemberInvokerData<Func_String_ErrorTargetClassChild>(Func_String_ErrorTargetClassChild.class));

            assertTrue(getter2.invoke(child).equals("hi"));
            assertTrue(getter2.invoke((ErrorTargetClassChild) childPoly).equals("hi"));
        } catch (Exception e) {
            fail("Errors obtaining the fields");
        }

        // Dynamic

        // Add a method to make this field as an used one
        MethodDescriptor md = new MethodDescriptor();
        md.methodName = "getDynamicAttribute";
        md.methodSignature = Func_String_ErrorTargetClassChild.class;
        md.methodType = MethodType.methodType(String.class);
        md.parameterNames = new String[0];
        md.code = "return dynamicAttributeParent;";

        try {
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }

        try {
            // Used attribute: ERROR
            Intercessor.removeField(ErrorTargetClassChild.class, new jmplib.reflect.Field("dynamicAttributeParent"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        try {
            // Field in parent class
            Func_int_ErrorTargetClassParent getter3 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassParent>(Func_int_ErrorTargetClassParent.class));

            // Still present
            assertTrue(getter3.invoke(parent) == 5);
            assertTrue(getter3.invoke(child) == 5);
            assertTrue(getter3.invoke(childPoly) == 5);
        } catch (Exception e) {
            fail("Errors obtaining fields");
        }

        // Existing field inherited from parent class in child class

        try {
            Func_int_ErrorTargetClassChild getter4 = Evaluator.getFieldInvoker(ErrorTargetClassChild.class,
                    "existingAttribute",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));

            assertTrue(getter4.invoke(child) == 5);
            assertTrue(getter4.invoke((ErrorTargetClassChild) childPoly) == 5);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Errors obtaining fields");
        }
    }

    // Delete an attribute from an interface
    @Test
    public void _12_deleteAttributeFromInterface() {
        try {
            // Added in the previous function: ERROR
            Intercessor.removeField(ErrorTargetInterface.class, new jmplib.reflect.Field("existingAttribute"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Get an unexisting attribute
    @Test
    public void _13_getUnexistingAttribute() {
        try {
            Evaluator.getFieldInvoker(ErrorTargetClassChild.class, "unexisting",
                    new MemberInvokerData<Func_int_ErrorTargetClassChild>(Func_int_ErrorTargetClassChild.class));
            fail("The method must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

}
