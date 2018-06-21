package es.uniovi.jmplib.testing.primitives;

import com.github.javaparser.ParseException;
import es.uniovi.jmplib.testing.common.*;
import jmplib.DefaultEvaluator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.ClassNotEditableException;
import jmplib.exceptions.CompilationFailedException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExcludeFromJMPLib
public class MethodTest {
    private static IIntercessor Intercessor = SimpleIntercessor.getInstance();
    private static IEvaluator Evaluator = DefaultEvaluator.getInstance();

    @Test
    public void _01_addMultipleMethods() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();
        MethodDescriptor md2 = new MethodDescriptor();
        MethodDescriptor md3 = new MethodDescriptor();

        try {
            md.methodName = "method1";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];
            md.code = "return \"method1\";";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }
        try {
            md2.methodName = "method2";
            md2.methodSignature = Func_String_ErrorTargetClassParent.class;
            md2.methodType = MethodType.methodType(String.class);
            md2.parameterNames = new String[0];
            md2.code = "return \"method2\";";
            ErrorTargetClassParent.addMethod(md2, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }
        try {
            md3.methodName = "method3";
            md3.methodSignature = Func_String_ErrorTargetClassParent.class;
            md3.methodType = MethodType.methodType(String.class);
            md3.parameterNames = new String[0];
            md3.code = "return \"method3\";";
            ErrorTargetClassParent.addMethod(md3, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }

        Func_String_ErrorTargetClassParent invoker;

        try {

            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild.getInvokerInstanceMethod(md);

            String result = invoker.invoke(child);
            assertTrue(result.equals("method1"));
            result = invoker.invoke(childPoly);
            assertTrue(result.equals("method1"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        try {

            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent.getInvokerInstanceMethod(md);

            String result = invoker.invoke(parent);
            assertTrue(result.equals("method1"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        try {
            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild.getInvokerInstanceMethod(md2);

            String result = invoker.invoke(child);
            assertTrue(result.equals("method2"));
            result = invoker.invoke(childPoly);
            assertTrue(result.equals("method2"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        try {
            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent.getInvokerInstanceMethod(md2);

            String result = invoker.invoke(parent);
            assertTrue(result.equals("method2"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        try {
            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild.getInvokerInstanceMethod(md3);

            String result = invoker.invoke(child);
            assertTrue(result.equals("method3"));
            result = invoker.invoke(childPoly);
            assertTrue(result.equals("method3"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        try {
            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent.getInvokerInstanceMethod(md3);

            String result = invoker.invoke(parent);
            assertTrue(result.equals("method3"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }
    }

    // Add a new method that is in the original class code
    @Test
    public void _02_addExistingMethod() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();

        try {
            md.methodName = "duplicateTxt";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];
            md.code = "return \"dynamic method\";";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        Func_String_ErrorTargetClassParent invoker;

        // Call the existing one, as the add operation was not possible
        try {
            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent.getInvokerInstanceMethod(md);

            String result = invoker.invoke(child);
            assertTrue(result.equals("hihi"));
            result = invoker.invoke(parent);
            assertTrue(result.equals("hihi"));
            result = invoker.invoke(childPoly);
            assertTrue(result.equals("hihi"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }
    }

    // Add a method that was already added dynamically
    @Test
    public void _03_addDynamicMethod() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();

        try {
            md.methodName = "dupMethod";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];
            md.code = "return \"method to add two times\";";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }

        // Add again: ERROR
        try {
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        Func_String_ErrorTargetClassParent invoker;

        // Call the existing one, the second add operation was not possible
        try {
            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent.getInvokerInstanceMethod(md);

            String result = invoker.invoke(parent);
            assertTrue(result.equals("method to add two times"));

            result = invoker.invoke(child);
            assertTrue(result.equals("method to add two times"));

            result = invoker.invoke(childPoly);
            assertTrue(result.equals("method to add two times"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }
    }

    // Add a method to the parent class and use it from a child class
    @Test
    public void _04_addMethodToParent() {
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();

        try {
            md.methodName = "dupMethod2";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];
            md.code = "return \"method to add two times\";";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }
        Func_String_ErrorTargetClassParent invoker;
        if (true) {
            // Access the added method from a child instance
            try {
                invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild.getInvokerInstanceMethod(md);

                String result = invoker.invoke(child);
                assertTrue(result.equals("method to add two times"));

                result = invoker.invoke(childPoly);
                assertTrue(result.equals("method to add two times"));

            } catch (Exception e) {
                fail("MODEL ERROR: Error accessing dynamic parent method from child: " + e);
            }
        }

        // Access the added method from a child instance
        try {
            md.methodName = "duplicateTxt";

            invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild.getInvokerInstanceMethod(md);

            String result = invoker.invoke(child);
            assertTrue(result.equals("hihi"));
            result = invoker.invoke(childPoly);
            assertTrue(result.equals("hihi"));
        } catch (Exception e) {
            fail("MODEL ERROR: Error accessing existing parent method from child: " + e);
        }
    }

    // Add a method to an interface
    @Test
    public void _05_addMethodToInterface() {
        MethodDescriptor descriptor = new MethodDescriptor();
        try {
            descriptor.methodName = "interfaceMethod";
            descriptor.methodSignature = Func_String_ErrorTargetInterface.class;
            descriptor.methodType = MethodType.methodType(String.class);
            descriptor.parameterNames = new String[0];
            descriptor.code = "";
            MethodType type = MethodType.methodType(String.class);
            Intercessor.addMethod(Func_String_ErrorTargetInterface.class, new jmplib.reflect.Method(
                    descriptor.methodName, type, descriptor.code, Modifier.PUBLIC, descriptor.parameterNames));
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Wrongly overload a method
    @Test
    public void _06_overloadMethod() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();

        // Overload an existing method with only a different return type: ERROR
        try {
            md.methodName = "duplicateTxt";
            md.methodSignature = Func_int_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(int.class);
            md.parameterNames = new String[0];
            md.code = "return 15;";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Add a dynamic method
        md = new MethodDescriptor();

        try {
            md.methodName = "dynamicMethodToOverload";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];
            md.code = "return \"hi\";";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }

        md = new MethodDescriptor();

        // Overload an existing dynamic method with only a different return
        // type: ERROR
        try {
            md.methodName = "dynamicMethodToOverload";
            md.methodSignature = Func_int_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(int.class);
            md.parameterNames = new String[0];
            md.code = "return 15;";
            ErrorTargetClassParent.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Call the existing one, as the add operation was not possible
        try {
            md.methodName = "duplicateTxt";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);

            String result = invoker.invoke(parent);
            assertTrue(result.equals("hihi"));
            result = invoker.invoke(child);
            assertTrue(result.equals("hihi"));
            result = invoker.invoke(childPoly);
            assertTrue(result.equals("hihi"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        // Call the existing one, as the add operation was not possible
        try {
            md.methodName = "dynamicMethodToOverload";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);

            String result = invoker.invoke(parent);
            assertTrue(result.equals("hi"));

            result = invoker.invoke(child);
            assertTrue(result.equals("hi"));

            result = invoker.invoke(childPoly);
            assertTrue(result.equals("hi"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }
    }

    // Wrongly overload a method in a subclass
    @Test
    public void _07_subclassOverload() {
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();

        // Overload an existing method with only a different return type: ERROR
        try {
            md.methodName = "duplicateTxt";
            md.methodSignature = Func_int_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(int.class);
            md.parameterNames = new String[0];
            md.code = "return 15;";
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Add a dynamic method
        md = new MethodDescriptor();

        try {
            md.methodName = "dynamicMethodToOverload";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];
            md.code = "return \"hi\";";
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            fail("Error adding method: " + e);
        }

        md = new MethodDescriptor();

        // Overload an existing dynamic method with only a different return
        // type: ERROR
        try {
            md.methodName = "dynamicMethodToOverload";
            md.methodSignature = Func_int_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(int.class);
            md.parameterNames = new String[0];
            md.code = "return 15;";
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Call the existing one, as the add operation was not possible
        try {
            md.methodName = "duplicateTxt";
            md.methodSignature = Func_String_ErrorTargetClassChild.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];

            Func_String_ErrorTargetClassChild invoker = (Func_String_ErrorTargetClassChild) ErrorTargetClassChild
                    .getInvokerInstanceMethod(md);

            String result = invoker.invoke(child);
            assertTrue(result.equals("hihi"));
            result = invoker.invoke((ErrorTargetClassChild) childPoly);
            assertTrue(result.equals("hihi"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }

        // Call the existing one, as the add operation was not possible
        try {
            md.methodName = "dynamicMethodToOverload";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(String.class);
            md.parameterNames = new String[0];

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild
                    .getInvokerInstanceMethod(md);

            String result = invoker.invoke(child);
            assertTrue(result.equals("hi"));

            result = invoker.invoke((ErrorTargetClassChild) childPoly);
            assertTrue(result.equals("hi"));

        } catch (Exception e) {
            fail("Errors obtaining invokers");
        }
    }

    // Add a syntactically erroneous method
    @Test
    public void _08_addSyntacticallyErroneousMethod() {
        MethodDescriptor md = new MethodDescriptor();

        // Overload an existing method with only a different return type: ERROR
        try {
            md.methodName = "wrongMethod";
            md.methodSignature = Func_int_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(int.class);
            md.parameterNames = new String[0];
            md.code = "return 15"; // Lacks semicolon
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Add a method that throws an Exception
    @Test
    public void _09_addExceptionThrowingMethod() {
        MethodDescriptor md = new MethodDescriptor();

        // Overload an existing method with only a different return type: ERROR
        try {
            md.methodName = "exceptMethod";
            md.methodSignature = Func_int_ErrorTargetClassParent.class;
            md.methodType = MethodType.methodType(int.class);
            md.parameterNames = new String[0];
            md.code = "return 1/0"; // Runtime exception
            ErrorTargetClassChild.addMethod(md, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ParseException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Replace a method using incompatible types
    @Test
    public void _10_replaceMethod() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        MethodDescriptor md = new MethodDescriptor();

        // Use the method
        try {
            md.methodName = "echo";
            md.methodSignature = Func_String_ErrorTargetClassParent_String.class;

            Func_String_ErrorTargetClassParent_String invoker = (Func_String_ErrorTargetClassParent_String) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent, "hi").equals("hi"));

        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }

        MethodDescriptor md2 = new MethodDescriptor();

        // Overload an unexisting method
        try {
            md2.methodName = "echo";
            md2.methodSignature = Func_String_ErrorTargetClassParent_int.class;
            md2.methodType = MethodType.methodType(String.class);
            md2.parameterNames = new String[]{"num"};
            md2.code = "return new Integer(num).toString();"; // Runtime
            // exception
            ErrorTargetClassChild.replaceMethod(md2, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // We can still use the method
        try {
            md.methodName = "echo";
            md.methodSignature = Func_String_ErrorTargetClassParent_String.class;

            Func_String_ErrorTargetClassParent_String invoker = (Func_String_ErrorTargetClassParent_String) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent, "hi").equals("hi"));

        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }
    }

    // Replace a unexisting method
    @Test
    public void _11_replaceUnexistingMethod() {
        MethodDescriptor md2 = new MethodDescriptor();

        // Overload an existing method with only a different return type: ERROR
        try {
            md2.methodName = "unexisting";
            md2.methodSignature = Func_String_ErrorTargetClassParent_int.class;
            md2.methodType = MethodType.methodType(String.class, int.class);
            md2.parameterNames = new String[]{"num"};
            md2.code = "return new Integer(num).toString();"; // Runtime
            // exception
            ErrorTargetClassChild.replaceMethod(md2, Modifier.PUBLIC);
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete method, try to use it
    @Test
    public void _12_deleteMethod() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        MethodDescriptor md = new MethodDescriptor();

        // Use the existing method
        try {
            md.methodName = "echo";
            md.methodSignature = Func_String_ErrorTargetClassParent_String.class;

            Func_String_ErrorTargetClassParent_String invoker = (Func_String_ErrorTargetClassParent_String) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent, "hi").equals("hi"));

        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }

        // Use the previously added method
        try {
            md.methodName = "method1";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent).equals("method1"));

        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }

        // Delete both methods
        try {
            Intercessor.removeMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method("echo"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Fail deleting");
        }
        try {
            Intercessor.removeMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method("method1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Fail deleting");
        }

        try {
            md.methodName = "echo";
            md.methodSignature = Func_String_ErrorTargetClassParent_String.class;

            ErrorTargetClassParent.getInvokerInstanceMethod(md);
            fail("Fail deleting");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Use the previously added method
        try {
            md.methodName = "method1";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;

            ErrorTargetClassParent.getInvokerInstanceMethod(md);
            fail("Fail deleting");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete a unexisting method
    @Test
    public void _13_deleteUnexistingMethod() {
        try {
            Intercessor.removeMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method("unexisting"));
            fail("Must throw an exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete a inherited method
    @Test
    public void _14_deleteInheritedMethod() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        MethodDescriptor md = new MethodDescriptor();

        // Use the existing method
        try {
            md.methodName = "echoV2";
            md.methodSignature = Func_String_ErrorTargetClassParent_String.class;

            Func_String_ErrorTargetClassParent_String invoker = (Func_String_ErrorTargetClassParent_String) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent, "hi").equals("hi"));

        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }

        // Use the previously added method
        try {
            md.methodName = "method2";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent).equals("method2"));

        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }

        // Delete both methods
        try {
            Intercessor.removeMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method("echoV2"));
        } catch (Exception e) {
            fail("Error deleting method: " + e);
        }
        try {
            Intercessor.removeMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method("method2"));
        } catch (Exception e) {
            fail("Error deleting method: " + e);
        }

        // Use the methods again
        // Use the existing method
        try {
            md.methodName = "echoV2";
            md.methodSignature = Func_String_ErrorTargetClassParent_String.class;

            Func_String_ErrorTargetClassParent_String invoker = (Func_String_ErrorTargetClassParent_String) ErrorTargetClassChild
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent, "hi").equals("hi"));
            fail("Access invalid");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }

        // Use the previously added method
        try {
            md.methodName = "method2";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassChild
                    .getInvokerInstanceMethod(md);
            assertTrue(invoker.invoke(parent).equals("method2"));
            fail("Access invalid");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete a used method
    @Test
    public void _15_deleteUsedMethod() {
        try {
            Intercessor.removeMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method("used"));
            fail("Used method cannot be deleted");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete a method from an interface
    @Test
    public void _16_deleteMethodFromAnInterface() {
        try {
            Intercessor.removeMethod(ErrorTargetInterface.class, new jmplib.reflect.Method("foo"));
            fail("Must throw exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(ClassNotEditableException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Delete an abstract method
    @Test
    public void _17_deleteAbstractMethod() {
        try {
            Intercessor.removeMethod(ErrorTargetAbstractClass.class, new jmplib.reflect.Method("foo"));
        } catch (Exception e) {
            fail("Error deleting method: " + e);
        }
    }

    // Invoke an unexisting method
    @Test
    @SuppressWarnings("unused")
    public void _18_invokeUnexistingMethod() {
        MethodDescriptor md = new MethodDescriptor();
        try {
            md.methodName = "donotexist";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;

            Func_String_ErrorTargetClassParent invoker = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);
            fail("Must throw exception");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Invoke a method with wrong parameters (bad class)
    @Test
    @SuppressWarnings("unused")
    public void _19_invokeMethod() {
        MethodDescriptor md = new MethodDescriptor();
        try {
            md.methodName = "myOwnMethod";
            md.methodSignature = Func_String_ErrorTargetClassChild.class;

            Func_String_ErrorTargetClassChild invoker = (Func_String_ErrorTargetClassChild) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);

        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertThat(e.getCause(), instanceOf(NoSuchMethodException.class));
            assertNotNull(e.getMessage());
            assertNotNull(e.getCause().getMessage());
        }
    }

    // Invoke a child method over a polymorphic parent reference
    @Test
    public void _20_invokeMethodWrongPolymorphism() {
        ErrorTargetClassParent parent = new ErrorTargetClassParent();
        ErrorTargetClassChild child = new ErrorTargetClassChild();
        ErrorTargetClassParent childPoly = new ErrorTargetClassChild();
        MethodDescriptor md = new MethodDescriptor();
        try {
            md.methodName = "fromMethod";
            md.methodSignature = Func_String_ErrorTargetClassParent.class;

            Func_String_ErrorTargetClassParent invokerParent = (Func_String_ErrorTargetClassParent) ErrorTargetClassParent
                    .getInvokerInstanceMethod(md);

            assertTrue(invokerParent.invoke(parent).equals("from the parent"));
            assertTrue(invokerParent.invoke(childPoly).equals("from the child"));

            md.methodSignature = Func_String_ErrorTargetClassChild.class;

            Func_String_ErrorTargetClassChild invokerChild = (Func_String_ErrorTargetClassChild) ErrorTargetClassChild
                    .getInvokerInstanceMethod(md);
            assertTrue(invokerChild.invoke(child).equals("from the child"));
        } catch (Exception e) {
            fail("Error obtaining method: " + e);
        }
    }

}
