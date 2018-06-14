package es.uniovi.jmplib.testing.common;

import java.lang.invoke.MethodType;

public class MethodDescriptor {
	public String methodName;
	public Class<?> methodSignature;
	public MethodType methodType;
	public String[] parameterNames;
	public String code;
}
