package es.uniovi.jmplib.testing.common;

import jmplib.SimpleEvaluator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;

public class ErrorTargetClassChild extends ErrorTargetClassParent {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    public static String staticMethod() {
        return "static";
    }

    public static void addMethod(MethodDescriptor descriptor, int modifier) throws StructuralIntercessionException {
        Intercessor.addMethod(ErrorTargetClassChild.class, new jmplib.reflect.Method(descriptor.methodName,
                descriptor.methodType, descriptor.code, modifier, descriptor.parameterNames));
    }

    public static Object getInvokerInstanceMethod(MethodDescriptor descriptor) throws StructuralIntercessionException {
        return Evaluator.getMethodInvoker(ErrorTargetClassChild.class, descriptor.methodName,
                new MemberInvokerData(descriptor.methodSignature));
    }

    public static void replaceMethod(MethodDescriptor descriptor, int modifier) throws StructuralIntercessionException {
        Intercessor.replaceImplementation(ErrorTargetClassChild.class,
                new jmplib.reflect.Method(descriptor.methodName, descriptor.methodType, descriptor.code));
    }

    public String myOwnMethod() {
        return "Child's own method";
    }

    public String fromMethod() {
        return "from the child";
    }
}
