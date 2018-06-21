package es.uniovi.jmplib.testing.visibility;

import es.uniovi.jmplib.testing.common.*;
import jmplib.SimpleEvaluator;
import jmplib.IEvaluator;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.CompilationFailedException;
import jmplib.invokers.MemberInvokerData;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@ExcludeFromJMPLib
public class ExternalFieldAccesTest {
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    @Test
    public void testPublicVisibility() {
        FieldHolder a = new FieldHolder();
        assertTrue(a.publicField == 1);
    }

    @Test
    public void testProtectedVisibility() {
        FieldHolder a = new FieldHolder();
        assertTrue(a.protectedField == 2);
    }

    @Test
    public void testPackageVisibility() {
        FieldHolder a = new FieldHolder();
        assertTrue(a.packageField == 3);
    }

    @Test
    public void testPrivateVisibility() {
        FieldHolder a = new FieldHolder();
        assertTrue(a.getPrivateField() == 4);
    }

    @Test
    public void checkAttributeVisibilityPrivate() {
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        try {
            Func_int_ErrorTargetClassParent getter3 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "thisIsPrivate", new MemberInvokerData<>(Func_int_ErrorTargetClassParent.class));

            getter3.invoke(child);
            fail("MODEL ERROR: Accessing a private field from the outside");
        } catch (Exception ex) {
            assertNotNull(ex.getCause());
            assertThat(ex.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(ex.getMessage());
            assertNotNull(ex.getCause().getMessage());
        }

        // Add a method to make this field as an used one
        MethodDescriptor md = new MethodDescriptor();
        md.methodName = "getPrivateAttribute";
        md.methodType = MethodType.methodType(int.class);
        md.methodSignature = Func_int_ErrorTargetClassChild.class;
        md.parameterNames = new String[0];
        md.code = "return thisIsPrivate;";

        try {
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
            fail("MODEL ERROR: Can access to a private field from a child method");
        } catch (Exception ex) {
            assertNotNull(ex.getCause());
            assertThat(ex.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(ex.getMessage());
            assertNotNull(ex.getCause().getMessage());
        }
    }

    @Test
    public void checkAttributeVisibilityProtected() {
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        try {
            Func_int_ErrorTargetClassParent getter3 = Evaluator.getFieldInvoker(ErrorTargetClassParent.class,
                    "thisIsProtected", new MemberInvokerData<>(Func_int_ErrorTargetClassParent.class));

            int result = getter3.invoke(child);
            //System.out.println(result);

            fail("MODEL ERROR: Accessing a protected field from the outside");
        } catch (Exception ex) {
            assertNotNull(ex.getCause());
            assertThat(ex.getCause(), instanceOf(NoSuchFieldException.class));
            assertNotNull(ex.getMessage());
            assertNotNull(ex.getCause().getMessage());
        }

        // Add a method in the child class
        MethodDescriptor md = new MethodDescriptor();
        md.methodName = "getProtectedAttribute";
        md.methodSignature = Func_int_ErrorTargetClassChild.class;
        md.methodType = MethodType.methodType(int.class);
        md.parameterNames = new String[0];
        md.code = "return thisIsProtected;";

        try {
            // If the private test is not executed, this does not fail. Problems
            // adding multiple methods?
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
        } catch (Exception ex) {
            fail("Error adding method");
        }

        try {
            Func_int_ErrorTargetClassChild invoker;
            invoker = (Func_int_ErrorTargetClassChild) ErrorTargetClassChild.getInvokerInstanceMethod(md);

            int result = invoker.invoke(child);
            assertTrue(result == 20);
        } catch (Exception e) {
            fail("MODEL ERROR: Cannot access to a protected field from a child method");
        }
    }

}
