package es.uniovi.jmplib.testing.common;

import jmplib.DefaultEvaluator;
import jmplib.DefaultIntercessor;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;

public class ErrorTargetClassParent {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();
	private static IEvaluator Evaluator = DefaultEvaluator.getInstance();
	
	public int existingAttribute = 5;
	
	public int foo = 1;
	
	public String txt = "hi";
	
	@SuppressWarnings("unused")
	private int thisIsPrivate = 10;
	
	protected int thisIsProtected = 20;
	
	
	public String duplicateTxt() {
		return txt.concat(txt);
	}
	
	public String echo(String s) {
		return s;
	}
	
	public String echoV2(String s) {
		return s;
	}
	
	public String used() {
		return "used";
	}
	
	public String user() {
		return used();
	}
	
	public String fromMethod () {
		return "from the parent";
	}
	
	//Helpers
	
	public static void addMethod(MethodDescriptor descriptor, int modifier) throws StructuralIntercessionException {
		Intercessor.addMethod(ErrorTargetClassParent.class, new jmplib.reflect.Method(descriptor.methodName,
				descriptor.methodType, descriptor.code, modifier,
				descriptor.parameterNames));
	}
	
	public static Object getInvokerInstanceMethod(MethodDescriptor descriptor) throws StructuralIntercessionException {
		return Evaluator.getMethodInvoker(ErrorTargetClassParent.class, descriptor.methodName, new MemberInvokerData(
				descriptor.methodSignature));
	}
	
	public static void replaceMethod(MethodDescriptor descriptor, int modifier) throws StructuralIntercessionException {
		Intercessor.replaceImplementation(ErrorTargetClassParent.class, new jmplib.reflect.Method(descriptor.methodName,
				descriptor.methodType, descriptor.code));
	}
}
