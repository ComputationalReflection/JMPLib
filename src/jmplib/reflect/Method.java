package jmplib.reflect;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import jmplib.classversions.VersionTables;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.intercessor.IntercessorTypeConversion;
import jmplib.util.intercessor.IntercessorValidators;
import sun.reflect.CallerSensitive;

public class Method extends Executable {
	/**
	 * Decorated java.lang.reflect.Method
	 */
	private java.lang.reflect.Method decoratedMethod;
	/**
	 * Class that originally declared the decorated field.
	 */
	private Class<?> declaringClass;

	/**
	 * Parameter types of the decorated method.
	 */
	java.lang.Class<?>[] parameterTypes;

	/**
	 * JMPLib postfix to methods introduced to invoke methods
	 */
	private static String invokerPostfix = "_invoker";

	/**
	 * The user has supplied method data manually and this Method do not correspond
	 * to a java.lang.reflect.Method
	 */
	private boolean customMethod = false;

	private String customName;
	private MethodType customMethodType;
	private String customBody;
	private int customModifiers;
	private Type[] customExceptions;
	private String[] customParameterNames;

	private Type[] customTypeParameters = new TypeVariable[0];
	private Type customGenericReturnType;
	jmplib.reflect.TypeVariable<?>[] methodTypeParameters;

	private void checkParameters() {
		if (customMethod) {
			if ((customMethodType == null) && (customParameterNames == null))
				return;
			if ((customMethodType.parameterList().size() == customParameterNames.length))
				return;
			throw new IllegalArgumentException(
					"The number of parameter names must match with the number of parameters");
		}
	}

	public Method(Type clazz, String name, MethodType methodType, String body, String... parameterNames)
			throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, Modifier.PUBLIC);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = Modifier.PUBLIC;
		this.customParameterNames = parameterNames;
		checkParameters();
	}

	public Method(String name, MethodType methodType, String body) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, Modifier.PUBLIC);
		this.customMethod = true;
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = Modifier.PUBLIC;
	}

	public Method(String name, MethodType methodType, String body, String... parameterNames)
			throws StructuralIntercessionException {
		this(null, name, methodType, body, parameterNames);
	}

	public Method(Type clazz, String name, MethodType methodType, String body, int modifiers, String... parameterNames)
			throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, modifiers);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = modifiers;
		this.customParameterNames = parameterNames;
		checkParameters();
	}

	public Method(String name, MethodType methodType, String body, int modifiers, String... parameterNames)
			throws StructuralIntercessionException {
		this(null, name, methodType, body, modifiers, parameterNames);
	}

	public Method(String name, MethodType methodType) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, Modifier.PUBLIC);
		this.customMethod = true;
		this.customName = name;
		this.customModifiers = Modifier.PUBLIC;
		this.customMethodType = methodType;
	}

	public Method(String name, String body) throws StructuralIntercessionException {
		this(null, name, body);
	}

	public Method(Type clazz, String name, String body) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, body, Modifier.PUBLIC);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customBody = body;
		this.customModifiers = Modifier.PUBLIC;
	}

	public Method(Type clazz, String name, MethodType methodType, String body) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, Modifier.PUBLIC);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = Modifier.PUBLIC;
	}

	public Method(Type clazz, String name) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, Modifier.PUBLIC);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customModifiers = Modifier.PUBLIC;
	}

	public Method(String name) throws StructuralIntercessionException {
		this((Type) null, name);
	}

	public Method(Type clazz, String name, MethodType methodType) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, "", Modifier.PUBLIC);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customModifiers = Modifier.PUBLIC;
	}

	public Method(Type clazz, String name, MethodType methodType, String body, int modifiers, Type[] exceptions)
			throws StructuralIntercessionException {
		this(clazz, name, methodType, body, modifiers, exceptions, new String[0]);
	}

	public Method(Type clazz, String name, MethodType methodType, String body, int modifiers, Type[] exceptions,
			String... parameterNames) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, modifiers, exceptions);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = modifiers;
		this.customExceptions = exceptions;
		this.customParameterNames = parameterNames;
		checkParameters();
	}

	public Method(Type clazz, String name, MethodType methodType, String body, int modifiers, Type[] exceptions,
			Type[] typeParameters, jmplib.reflect.TypeVariable<?>[] methodTypeParameters, String... parameterNames)
			throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, modifiers, exceptions);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = modifiers;
		this.customExceptions = exceptions;
		this.customParameterNames = parameterNames;
		this.customTypeParameters = typeParameters;
		this.methodTypeParameters = methodTypeParameters;
		checkParameters();
	}

	public Method(Type clazz, String name, MethodType methodType, String body, int modifiers, Class<?>[] exceptions,
			Type[] typeParameters, Type genericReturnType, jmplib.reflect.TypeVariable<?>[] methodTypeParameters,
			String... parameterNames) throws StructuralIntercessionException {
		IntercessorValidators.checkMethodParams(name, methodType, body, modifiers);
		this.customMethod = true;
		this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
		this.customName = name;
		this.customMethodType = methodType;
		this.customBody = body;
		this.customModifiers = modifiers;
		this.customExceptions = exceptions;
		this.customParameterNames = parameterNames;
		this.customTypeParameters = typeParameters;
		this.customGenericReturnType = genericReturnType;
		this.methodTypeParameters = methodTypeParameters;
		checkParameters();
	}

	/**
	 * Method decorator constructor
	 * 
	 * @param originalMethod
	 */
	protected Method(java.lang.reflect.Method originalMethod) {
		this.decoratedMethod = originalMethod;
		java.lang.Class<?> originalDeclaringClass = originalMethod.getDeclaringClass();

		java.lang.Class<?> version = VersionTables.isVersionOf(originalDeclaringClass);
		if (version == null)
			version = originalDeclaringClass;

		this.declaringClass = new Class<>(version);
		this.parameterTypes = originalMethod.getParameterTypes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Class<?> getDeclaringClass() {
		if (declaringClass == null)
			return null;
		return declaringClass.getDecoratedClass();
	}

	/**
	 * Returns the name of the method represented by this {@code Method} object, as
	 * a {@code String}.
	 */
	@Override
	public String getName() {
		if (customMethod)
			return customName;
		return decoratedMethod.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getModifiers() {
		if (customMethod)
			return customModifiers;
		return decoratedMethod.getModifiers();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GenericSignatureFormatError
	 *             {@inheritDoc}
	 * @since 1.5
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TypeVariable<java.lang.reflect.Method>[] getTypeParameters() {
		if (customMethod)
			return (TypeVariable<java.lang.reflect.Method>[]) this.customTypeParameters;
		return decoratedMethod.getTypeParameters();
	}

	/**
	 * Returns a {@code Class} object that represents the formal return type of the
	 * method represented by this {@code Method} object.
	 *
	 * @return the return type for the method this object represents
	 */
	public java.lang.Class<?> getReturnType() {
		if (customMethod) {
			if (this.customMethodType != null)
				return this.customMethodType.returnType();
			throw new IllegalStateException("Cannot obtain the return type of a partially specified method");
		}
		return decoratedMethod.getReturnType();
	}

	/**
	 * Returns a {@code Type} object that represents the formal return type of the
	 * method represented by this {@code Method} object.
	 *
	 * <p>
	 * If the return type is a parameterized type, the {@code Type} object returned
	 * must accurately reflect the actual type parameters used in the source code.
	 *
	 * <p>
	 * If the return type is a type variable or a parameterized type, it is created.
	 * Otherwise, it is resolved.
	 *
	 * @return a {@code Type} object that represents the formal return
	 * 
	 *         type of the underlying method
	 * 
	 * @throws GenericSignatureFormatError
	 * 
	 *             if the generic method signature does not conform to the format
	 * 
	 *             specified in
	 * 
	 *             <cite>The Java&trade; Virtual Machine Specification</cite>
	 * 
	 * @throws TypeNotPresentException
	 *             if the underlying method's
	 * 
	 *             return type refers to a non-existent type declaration
	 * 
	 * @throws MalformedParameterizedTypeException
	 *             if the
	 * 
	 *             underlying method's return typed refers to a parameterized
	 * 
	 *             type that cannot be instantiated for any reason
	 * 
	 * @since 1.5
	 * 
	 */
	public Type getGenericReturnType() {
		if (customMethod) {
			if (this.customGenericReturnType != null)
				return this.customGenericReturnType;
			return this.getReturnType();
		}
		return decoratedMethod.getGenericReturnType();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public java.lang.Class<?>[] getParameterTypes() {
		if (customMethod) {
			if (this.customMethodType != null)
				return this.customMethodType.parameterArray();
			throw new IllegalStateException("Cannot obtain the parameter types of a partially specified method");
		}
		return decoratedMethod.getParameterTypes();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	public int getParameterCount() {
		if (customMethod) {
			if (this.customMethodType != null)
				return this.customMethodType.parameterArray().length;
			throw new IllegalStateException("Cannot obtain the parameter types of a partially specified method");
		}
		return decoratedMethod.getParameterCount();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws GenericSignatureFormatError
	 *             {@inheritDoc}
	 * 
	 * @throws TypeNotPresentException
	 *             {@inheritDoc}
	 * 
	 * @throws MalformedParameterizedTypeException
	 *             {@inheritDoc}
	 * 
	 * @since 1.5
	 * 
	 */
	@Override
	public Type[] getGenericParameterTypes() {
		if (customMethod) {
			if (this.customTypeParameters != null)
				return this.customTypeParameters;
			throw new IllegalStateException("Cannot obtain the parameter types of a partially specified method");
		}
		return decoratedMethod.getGenericParameterTypes();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public java.lang.Class<?>[] getExceptionTypes() {
		if (customMethod) {
			return IntercessorTypeConversion.type2JavaClass(this.customExceptions);
		}
		return decoratedMethod.getExceptionTypes();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws GenericSignatureFormatError
	 *             {@inheritDoc}
	 * 
	 * @throws TypeNotPresentException
	 *             {@inheritDoc}
	 * 
	 * @throws MalformedParameterizedTypeException
	 *             {@inheritDoc}
	 * 
	 * @since 1.5
	 * 
	 */

	@Override

	public Type[] getGenericExceptionTypes() {
		if (customMethod) {
			java.lang.Class<?>[] classes = IntercessorTypeConversion.type2JavaClass(this.customExceptions);
			if (classes == null)
				return classes;
			Type[] ret = new Type[classes.length];
			for (int i = 0; i < classes.length; i++)
				ret[i] = classes[i].getClass();
		}
		return decoratedMethod.getGenericExceptionTypes();
	}

	/**
	 * 
	 * Compares this {@code Method} against the specified object. Returns
	 * 
	 * true if the objects are the same. Two {@code Methods} are the same if
	 * 
	 * they were declared by the same class and have the same name
	 * 
	 * and formal parameter types and return type.
	 * 
	 */

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Method) {
			if (customMethod) {
				Method ftemp = (Method) obj;
				if (!customName.equals(ftemp.getName()))
					return false;
				if ((customMethodType != null) && (customMethodType != MethodType.methodType(ftemp.getReturnType(),
						ftemp.getParameterTypes())))
					return false;
				if (customModifiers != ftemp.getModifiers())
					return false;
				return true;
			}
			return this.decoratedMethod.equals(((Method) obj).decoratedMethod);
		} else {
			if (obj != null && obj instanceof java.lang.reflect.Method) {
				if (customMethod) {
					java.lang.reflect.Method ftemp = (java.lang.reflect.Method) obj;
					if (!customName.equals(ftemp.getName()))
						return false;
					if ((customMethodType != null) && (customMethodType != MethodType.methodType(ftemp.getReturnType(),
							ftemp.getParameterTypes())))
						return false;
					if (customModifiers != ftemp.getModifiers())
						return false;
					return true;
				}
				return this.decoratedMethod.equals((java.lang.reflect.Method) obj);
			}
		}
		return false;
	}

	/**
	 * 
	 * Returns a hashcode for this {@code Method}. The hashcode is computed
	 * 
	 * as the exclusive-or of the hashcodes for the underlying
	 * 
	 * method's declaring class name and the method's name.
	 * 
	 */
	public int hashCode() {
		if (customMethod) {
			if (customMethodType != null)
				return customName.hashCode() ^ customMethodType.hashCode();
			return customName.hashCode();
		}
		return decoratedMethod.hashCode();
	}

	/**
	 * 
	 * Returns a string describing this {@code Method}. The string is
	 * 
	 * formatted as the method access modifiers, if any, followed by
	 * 
	 * the method return type, followed by a space, followed by the
	 * 
	 * class declaring the method, followed by a period, followed by
	 * 
	 * the method name, followed by a parenthesized, comma-separated
	 * 
	 * list of the method's formal parameter types. If the method
	 * 
	 * throws checked exceptions, the parameter list is followed by a
	 * 
	 * space, followed by the word throws followed by a
	 * 
	 * comma-separated list of the thrown exception types.
	 * 
	 * For example:
	 * 
	 * <pre>
	 *    public boolean java.lang.Object.equals(java.lang.Object)
	 * 
	 * </pre>
	 *
	 * 
	 * 
	 * <p>
	 * The access modifiers are placed in canonical order as
	 * 
	 * specified by "The Java Language Specification". This is
	 * 
	 * {@code public}, {@code protected} or {@code private} first,
	 * 
	 * and then other modifiers in the following order:
	 * 
	 * {@code abstract}, {@code default}, {@code static}, {@code final},
	 * 
	 * {@code synchronized}, {@code native}, {@code strictfp}.
	 *
	 * 
	 * 
	 * @return a string describing this {@code Method}
	 *
	 * 
	 * 
	 * @jls 8.4.3 Method Modifiers
	 * 
	 */
	public String toString() {
		return sharedToString(Modifier.methodModifiers(), isDefault(), getParameterTypes(), getExceptionTypes());
	}

	/**
	 * 
	 * Returns a string describing this {@code Method}, including
	 * 
	 * type parameters. The string is formatted as the method access
	 * 
	 * modifiers, if any, followed by an angle-bracketed
	 * 
	 * comma-separated list of the method's type parameters, if any,
	 * 
	 * followed by the method's generic return type, followed by a
	 * 
	 * space, followed by the class declaring the method, followed by
	 * 
	 * a period, followed by the method name, followed by a
	 * 
	 * parenthesized, comma-separated list of the method's generic
	 * 
	 * formal parameter types.
	 *
	 * 
	 * 
	 * If this method was declared to take a variable number of
	 * 
	 * arguments, instead of denoting the last parameter as
	 * 
	 * "<tt><i>Type</i>[]</tt>", it is denoted as
	 * 
	 * "<tt><i>Type</i>...</tt>".
	 *
	 * 
	 * 
	 * A space is used to separate access modifiers from one another
	 * 
	 * and from the type parameters or return type. If there are no
	 * 
	 * type parameters, the type parameter list is elided; if the type
	 * 
	 * parameter list is present, a space separates the list from the
	 * 
	 * class name. If the method is declared to throw exceptions, the
	 * 
	 * parameter list is followed by a space, followed by the word
	 * 
	 * throws followed by a comma-separated list of the generic thrown
	 * 
	 * exception types.
	 *
	 * 
	 * 
	 * <p>
	 * The access modifiers are placed in canonical order as
	 * 
	 * specified by "The Java Language Specification". This is
	 * 
	 * {@code public}, {@code protected} or {@code private} first,
	 * 
	 * and then other modifiers in the following order:
	 * 
	 * {@code abstract}, {@code default}, {@code static}, {@code final},
	 * 
	 * {@code synchronized}, {@code native}, {@code strictfp}.
	 *
	 * 
	 * 
	 * @return a string describing this {@code Method},
	 * 
	 *         include type parameters
	 *
	 * 
	 * 
	 * @since 1.5
	 *
	 * 
	 * 
	 * @jls 8.4.3 Method Modifiers
	 * 
	 */

	@Override
	public String toGenericString() {
		return sharedToGenericString(Modifier.methodModifiers(), isDefault());
	}

	/**
	 * 
	 * Invokes the underlying method represented by this {@code Method}
	 * 
	 * object, on the specified object with the specified parameters.
	 * 
	 * Individual parameters are automatically unwrapped to match
	 * 
	 * primitive formal parameters, and both primitive and reference
	 * 
	 * parameters are subject to method invocation conversions as
	 * 
	 * necessary.
	 *
	 * 
	 * 
	 * <p>
	 * If the underlying method is static, then the specified {@code obj}
	 * 
	 * argument is ignored. It may be null.
	 *
	 * 
	 * 
	 * <p>
	 * If the number of formal parameters required by the underlying method is
	 * 
	 * 0, the supplied {@code args} array may be of length 0 or null.
	 *
	 * 
	 * 
	 * <p>
	 * If the underlying method is an instance method, it is invoked
	 * 
	 * using dynamic method lookup as documented in The Java Language
	 * 
	 * Specification, Second Edition, section 15.12.4.4; in particular,
	 * 
	 * overriding based on the runtime type of the target object will occur.
	 *
	 * 
	 * 
	 * <p>
	 * If the underlying method is static, the class that declared
	 * 
	 * the method is initialized if it has not already been initialized.
	 *
	 * 
	 * 
	 * <p>
	 * If the method completes normally, the value it returns is
	 * 
	 * returned to the caller of invoke; if the value has a primitive
	 * 
	 * type, it is first appropriately wrapped in an object. However,
	 * 
	 * if the value has the type of an array of a primitive type, the
	 * 
	 * elements of the array are <i>not</i> wrapped in objects; in
	 * 
	 * other words, an array of primitive type is returned. If the
	 * 
	 * underlying method return type is void, the invocation returns
	 * 
	 * null.
	 *
	 * 
	 * 
	 * @param obj
	 *            the object the underlying method is invoked from
	 * 
	 * @param args
	 *            the arguments used for the method call
	 * 
	 * @return the result of dispatching the method represented by
	 * 
	 *         this object on {@code obj} with parameters
	 * 
	 *         {@code args}
	 *
	 * 
	 * 
	 * @exception IllegalAccessException
	 *                if this {@code Method} object
	 * 
	 *                is enforcing Java language access control and the underlying
	 * 
	 *                method is inaccessible.
	 * 
	 * @exception IllegalArgumentException
	 *                if the method is an
	 * 
	 *                instance method and the specified object argument
	 * 
	 *                is not an instance of the class or interface
	 * 
	 *                declaring the underlying method (or of a subclass
	 * 
	 *                or implementor thereof); if the number of actual
	 * 
	 *                and formal parameters differ; if an unwrapping
	 * 
	 *                conversion for primitive arguments fails; or if,
	 * 
	 *                after possible unwrapping, a parameter value
	 * 
	 *                cannot be converted to the corresponding formal
	 * 
	 *                parameter type by a method invocation conversion.
	 * 
	 * @exception InvocationTargetException
	 *                if the underlying method
	 * 
	 *                throws an exception.
	 * 
	 * @exception NullPointerException
	 *                if the specified object is null
	 * 
	 *                and the method is an instance method.
	 * 
	 * @exception ExceptionInInitializerError
	 *                if the initialization
	 * 
	 *                provoked by this method fails.
	 * 
	 */
	@CallerSensitive
	public Object invoke(Object obj, Object... args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		try {
			// Obtain the most recent version of the object.
			Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
			if ((currentVer == null) || (currentVer == obj))
				return this.decoratedMethod.invoke(obj, args);

			// Build parameters and arguments for corresponding invoker lookup and call
			Object[] parameters = new Object[args.length + 1];
			java.lang.Class<?>[] arguments = new java.lang.Class<?>[args.length + 1];

			// Add current version type
			parameters[0] = obj;
			arguments[0] = obj.getClass();
			// Copy parameter and arguments to perform method search and invokation
			System.arraycopy(args, 0, parameters, 1, args.length);
			System.arraycopy(this.parameterTypes, 0, arguments, 1, args.length);

			// Obtain the corresponding method in the latest version of the object
			java.lang.reflect.Method currentMethod = currentVer.getClass()
					.getMethod("_" + this.decoratedMethod.getName() + Method.invokerPostfix, arguments);

			// Invoke it through Java reflection
			return currentMethod.invoke(currentVer, parameters);
		} catch (NoSuchMethodException e) {
			throw new InvocationTargetException(e, "The method " + this.decoratedMethod.getName()
					+ " was not found in class " + obj.getClass().getName());
		} catch (SecurityException e) {
			throw new IllegalAccessException(e.getMessage());
		}
	}

	/**
	 * 
	 * Returns {@code true} if this method is a bridge
	 * 
	 * method; returns {@code false} otherwise.
	 *
	 * 
	 * 
	 * @return true if and only if this method is a bridge
	 * 
	 *         method as defined by the Java Language Specification.
	 * 
	 * @since 1.5
	 * 
	 */
	public boolean isBridge() {
		return decoratedMethod.isBridge();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @since 1.5
	 * 
	 */
	@Override
	public boolean isVarArgs() {
		if (customMethod)
			return false;
		return decoratedMethod.isVarArgs();

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @jls 13.1 The Form of a Binary
	 * 
	 * @since 1.5
	 * 
	 */
	@Override
	public boolean isSynthetic() {
		if (customMethod)
			return false;
		return decoratedMethod.isSynthetic();
	}

	/**
	 * 
	 * Returns {@code true} if this method is a default
	 * 
	 * method; returns {@code false} otherwise.
	 *
	 * 
	 * 
	 * A default method is a public non-abstract instance method, that
	 * 
	 * is, a non-static method with a body, declared in an interface
	 * 
	 * type.
	 *
	 * 
	 * 
	 * @return true if and only if this method is a default
	 * 
	 *         method as defined by the Java Language Specification.
	 * 
	 * @since 1.8
	 * 
	 */

	public boolean isDefault() {
		if (customMethod)
			return false;
		// Default methods are public non-abstract instance methods
		// declared in an interface.
		return decoratedMethod.isDefault();
	}

	/**
	 * 
	 * Returns the default value for the annotation member represented by
	 * 
	 * this {@code Method} instance. If the member is of a primitive type,
	 * 
	 * an instance of the corresponding wrapper type is returned. Returns
	 * 
	 * null if no default is associated with the member, or if the method
	 * 
	 * instance does not represent a declared member of an annotation type.
	 *
	 * 
	 * 
	 * @return the default value for the annotation member represented
	 * 
	 *         by this {@code Method} instance.
	 * 
	 * @throws TypeNotPresentException
	 *             if the annotation is of type
	 * 
	 *             {@link Class} and no definition can be found for the
	 * 
	 *             default class value.
	 * 
	 * @since 1.5
	 * 
	 */
	public Object getDefaultValue() {
		if (customMethod)
			return null;
		return decoratedMethod.getDefaultValue();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * 
	 * @since 1.5
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(java.lang.Class<T> annotationClass) {
		if (customMethod)
			return null;
		Annotation an = decoratedMethod.getAnnotation(annotationClass);
		if (IntrospectionUtils.isJmpLibAddedAnnotation(an))
			return null;
		return (T) an;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.5
	 */
	public Annotation[] getDeclaredAnnotations() {
		if (customMethod)
			return null;
		return IntrospectionUtils.filterJMPLibAnnotations(decoratedMethod.getDeclaredAnnotations());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.5
	 */
	@Override
	public Annotation[][] getParameterAnnotations() {
		if (customMethod)
			return null;
		return decoratedMethod.getParameterAnnotations();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.8
	 */
	@Override
	public AnnotatedType getAnnotatedReturnType() {
		if (customMethod)
			return null;
		return decoratedMethod.getAnnotatedReturnType();
	}

	@Override
	public Parameter[] getParameters() {
		if (customMethod)
			return null;
		return decoratedMethod.getParameters();
	}

	@Override
	public AnnotatedType getAnnotatedReceiverType() {
		if (customMethod)
			return null;
		return decoratedMethod.getAnnotatedReceiverType();
	}

	@Override
	public AnnotatedType[] getAnnotatedParameterTypes() {
		if (customMethod)
			return null;
		return decoratedMethod.getAnnotatedParameterTypes();
	}

	@Override
	public AnnotatedType[] getAnnotatedExceptionTypes() {
		if (customMethod)
			return null;
		return decoratedMethod.getAnnotatedExceptionTypes();
	}

	@Override

	void specificToGenericStringHeader(StringBuilder sb) {
		Type genRetType = getGenericReturnType();
		sb.append(genRetType.getTypeName()).append(' ');
		sb.append(getDeclaringClass().getTypeName()).append('.');
		sb.append(getName());

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(java.lang.Class<T> annotationClass) {
		if (customMethod)
			return null;
		Annotation[] ans = decoratedMethod.getAnnotationsByType(annotationClass);
		return (T[]) IntrospectionUtils.filterJMPLibAnnotations(ans);
	}

	@Override
	void specificToStringHeader(StringBuilder sb) {
		if (customMethod) {
			String rtStr;
			if (customMethodType == null)
				rtStr = "<unespecified return type>";
			else
				rtStr = getReturnType().getTypeName();
			sb.append(rtStr).append(' ');
			String dcStr;
			if (declaringClass == null)
				dcStr = "<unbound method>";
			else
				dcStr = getDeclaringClass().getTypeName();
			sb.append(dcStr).append('.');
		} else {
			sb.append(getReturnType().getTypeName()).append(' ');
			sb.append(getDeclaringClass().getTypeName()).append('.');
		}
		sb.append(getName());
	}

	/********************************************
	 * EXTRA FUNCTIONALITY INCORPORATED BY JMPLIB
	 ********************************************/

	/**
	 * Get the source code of the method in a single string
	 * 
	 * @return Source code of the method (it carries no enclosing {})
	 * @throws IllegalAccessException
	 *             If for some reason the method source code cannot be obtained.
	 */
	public String getSourceCode() throws IllegalAccessException {
		if (customMethod)
			return customBody;

		java.lang.Class<?> declaringClass = this.getDeclaringClass();
		// Class content
		ClassContent classContent;
		// Method declaration
		MethodDeclaration md;

		try {
			classContent = SourceCodeCache.getInstance().getClassContent(declaringClass);
			CompilationUnit unit = JavaParserUtils.parse(classContent.getContent());
			md = JavaParserUtils.searchMethod(unit, declaringClass, this.getName(), this.getParameterTypes(),
					this.getReturnType());

		} catch (Exception e1) {
			throw new IllegalAccessException("The method " + getName() + " source code cannot be obtained.");
		}

		// Get and parse method body
		String body = md.getBody().toString();
		if (body.startsWith("{"))
			body = body.substring(1);
		if (body.endsWith("}"))
			body = body.substring(0, body.length() - 1);
		return body;
	}

	/**
	 * Obtain the declared method parameter names in the source code
	 * 
	 * @return An array with the parameter names, in the same declaration order
	 * @throws IllegalAccessException
	 *             If for some reason the parameter names cannot be obtained.
	 */
	public String[] getParameterNames() throws IllegalAccessException {
		if (customMethod) {
			return customParameterNames;
		}
		java.lang.Class<?> declaringClass = this.getDeclaringClass();
		// Class content
		ClassContent classContent;
		// Method declaration
		MethodDeclaration md;

		try {
			classContent = SourceCodeCache.getInstance().getClassContent(declaringClass);
			CompilationUnit unit = JavaParserUtils.parse(classContent.getContent());
			java.lang.Class<?> sourceClass = declaringClass;
			while (true) {
				try {
					md = JavaParserUtils.searchMethod(unit, sourceClass, this.getName(), this.getParameterTypes(),
							this.getReturnType());
					break;
				} catch (NoSuchMethodException ex) {
					sourceClass = sourceClass.getSuperclass();
					if (sourceClass == null)
						throw new IllegalAccessException("The method parameter names cannot be obtained.");
					classContent = SourceCodeCache.getInstance().getClassContent(sourceClass);
					unit = JavaParserUtils.parse(classContent.getContent());
				}
			}
			// Get parameter names
			List<com.github.javaparser.ast.body.Parameter> parameters = md.getParameters();
			String[] parameterNames = new String[parameters.size()];
			int counter = 0;
			for (com.github.javaparser.ast.body.Parameter p : parameters)
				parameterNames[counter++] = p.getId().toString();
			return parameterNames;
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new IllegalAccessException("The method parameter names cannot be obtained.");
		}
	}

	/**
	 * Obtains a string representation of the declared parameters of this method
	 * (Ex.: (double a, double b))
	 * 
	 * @return
	 * @throws IllegalAccessException
	 */
	public String getParameterString() throws IllegalAccessException {
		String[] parameterNames = this.getParameterNames();
		java.lang.Class<?>[] parameterTypes = this.getParameterTypes();
		Type [] genericParameterTypes = this.getGenericParameterTypes();

		StringBuffer sb = new StringBuffer("(");

		for (int i = 0; i < parameterNames.length; i++) {
			if (!parameterTypes[i].toString().equals(genericParameterTypes[i].toString()))
				sb.append(genericParameterTypes[i].getTypeName());
			else sb.append(parameterTypes[i].getSimpleName());
			IntrospectionUtils.addGenericTypes(sb, parameterTypes[i].getTypeParameters());
			sb.append(" ");
			sb.append(parameterNames[i]);
			if (i < parameterNames.length - 1)
				sb.append(", ");
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Returns the java.lang.reflect.Method represented by this Decorator
	 * 
	 * @return
	 */
	public java.lang.reflect.Method getDecoratedMethod() {
		return this.decoratedMethod;
	}

	/**
	 * Obtains the MethodType associated with this method.
	 * 
	 * @return
	 */
	public MethodType getMethodType() {
		if (customMethod)
			return customMethodType;
		return MethodType.methodType(this.getReturnType(), this.getParameterTypes());
	}

	/**
	 * Determines if this method overrides other from a parent method.
	 * 
	 * @return True if the method overrides a parent one.
	 */
	public boolean isOverride() {
		java.lang.Class<?> cl = this.getDeclaringClass().getSuperclass();

		while (cl != null) {
			try {
				// Method is located in a superclass, so it is overriden
				cl.getMethod(this.getName(), this.getParameterTypes());
				return true;
			} catch (NoSuchMethodException e) {
				cl = cl.getSuperclass();
			}
		}
		return false;
	}

	/**
	 * Determines if this method is generics.
	 * 
	 * @return True if the method overrides a parent one.
	 */
	public boolean isGeneric() {
		if (customMethod) {
			return this.customGenericReturnType != null || this.customTypeParameters != null;
		} else {
			java.lang.Class<?>[] ng = this.getParameterTypes();
			Type[] g = this.getGenericParameterTypes();
			if (ng.length != g.length)
				return false;
			for (int i = 0; i < ng.length; i++) {
				if (!ng[i].toString().equals(g.toString()))
					return true;
			}
			return (!this.getReturnType().toString().equals(this.getGenericReturnType().toString()));
		}
	}

	/**
	 * @return the methodTypeParameters
	 */
	public jmplib.reflect.TypeVariable<?>[] getMethodTypeParameters() {
		return methodTypeParameters;
	}
}
