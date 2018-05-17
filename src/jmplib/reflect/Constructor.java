package jmplib.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import jmplib.classversions.VersionTables;
import sun.reflect.CallerSensitive;

public class Constructor<T> extends Executable {
	java.lang.reflect.Constructor<T> decoratedConstructor;
	Class<?> declaringClass;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Constructor(java.lang.reflect.Constructor<T> originalMethod) {
		this.decoratedConstructor = originalMethod;
		java.lang.Class<?> originalDeclaringClass = originalMethod.getDeclaringClass();

		java.lang.Class<?> version = VersionTables.isVersionOf(originalDeclaringClass);
		if (version == null)
			version = originalDeclaringClass;

		this.declaringClass = new Class(version);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public java.lang.Class<?> getDeclaringClass() {
        return declaringClass.getDecoratedClass();
    }

    /**
     * Returns the name of this constructor, as a string.  This is
     * the binary name of the constructor's declaring class.
     */
    @Override
    public String getName() {
        return getDeclaringClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return decoratedConstructor.getModifiers();
    }

    /**
     * {@inheritDoc}
     * @throws GenericSignatureFormatError {@inheritDoc}
     * @since 1.5
     */
    @Override
    public TypeVariable<java.lang.reflect.Constructor<T>>[] getTypeParameters() {
    	return decoratedConstructor.getTypeParameters();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public java.lang.Class<?>[] getParameterTypes() {
    	return decoratedConstructor.getParameterTypes();
    }

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    public int getParameterCount() { 
    	return decoratedConstructor.getParameterCount();
    }

    /**
     * {@inheritDoc}
     * @throws GenericSignatureFormatError {@inheritDoc}
     * @throws TypeNotPresentException {@inheritDoc}
     * @throws MalformedParameterizedTypeException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public Type[] getGenericParameterTypes() {
    	return decoratedConstructor.getTypeParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.lang.Class<?>[] getExceptionTypes() {
    	return decoratedConstructor.getExceptionTypes();
    }


    /**
     * {@inheritDoc}
     * @throws GenericSignatureFormatError {@inheritDoc}
     * @throws TypeNotPresentException {@inheritDoc}
     * @throws MalformedParameterizedTypeException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public Type[] getGenericExceptionTypes() {
    	return decoratedConstructor.getTypeParameters();
    }

    /**
     * Compares this {@code Constructor} against the specified object.
     * Returns true if the objects are the same.  Two {@code Constructor} objects are
     * the same if they were declared by the same class and have the
     * same formal parameter types.
     */
    @SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
    	if (obj != null && obj instanceof Constructor) {
			return this.decoratedConstructor.equals(((Constructor) obj).decoratedConstructor);
		} else {
			if (obj != null && obj instanceof java.lang.reflect.Constructor) {
				return this.decoratedConstructor.equals((java.lang.reflect.Constructor) obj);
			}
		}
		return false;
    }

    /**
     * Returns a hashcode for this {@code Constructor}. The hashcode is
     * the same as the hashcode for the underlying constructor's
     * declaring class name.
     */
    public int hashCode() {
    	return decoratedConstructor.hashCode();
    }

    /**
     * Returns a string describing this {@code Constructor}.  The string is
     * formatted as the constructor access modifiers, if any,
     * followed by the fully-qualified name of the declaring class,
     * followed by a parenthesized, comma-separated list of the
     * constructor's formal parameter types.  For example:
     * <pre>
     *    public java.util.Hashtable(int,float)
     * </pre>
     *
     * <p>The only possible modifiers for constructors are the access
     * modifiers {@code public}, {@code protected} or
     * {@code private}.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     *
     * @return a string describing this {@code Constructor}
     * @jls 8.8.3. Constructor Modifiers
     */
    public String toString() {
        return sharedToString(Modifier.constructorModifiers(),
                              false,
                              getParameterTypes(),
                              getExceptionTypes());
    }

    @Override
    void specificToStringHeader(StringBuilder sb) {
        sb.append(getDeclaringClass().getTypeName());
    }

    /**
     * Returns a string describing this {@code Constructor},
     * including type parameters.  The string is formatted as the
     * constructor access modifiers, if any, followed by an
     * angle-bracketed comma separated list of the constructor's type
     * parameters, if any, followed by the fully-qualified name of the
     * declaring class, followed by a parenthesized, comma-separated
     * list of the constructor's generic formal parameter types.
     *
     * If this constructor was declared to take a variable number of
     * arguments, instead of denoting the last parameter as
     * "<tt><i>Type</i>[]</tt>", it is denoted as
     * "<tt><i>Type</i>...</tt>".
     *
     * A space is used to separate access modifiers from one another
     * and from the type parameters or return type.  If there are no
     * type parameters, the type parameter list is elided; if the type
     * parameter list is present, a space separates the list from the
     * class name.  If the constructor is declared to throw
     * exceptions, the parameter list is followed by a space, followed
     * by the word "{@code throws}" followed by a
     * comma-separated list of the thrown exception types.
     *
     * <p>The only possible modifiers for constructors are the access
     * modifiers {@code public}, {@code protected} or
     * {@code private}.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     *
     * @return a string describing this {@code Constructor},
     * include type parameters
     *
     * @since 1.5
     * @jls 8.8.3. Constructor Modifiers
     */
    @Override
    public String toGenericString() {
        return decoratedConstructor.toGenericString();
    }

    @Override
    void specificToGenericStringHeader(StringBuilder sb) {
        specificToStringHeader(sb);
    }

    /**
     * Uses the constructor represented by this {@code Constructor} object to
     * create and initialize a new instance of the constructor's
     * declaring class, with the specified initialization parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as necessary.
     *
     * <p>If the number of formal parameters required by the underlying constructor
     * is 0, the supplied {@code initargs} array may be of length 0 or null.
     *
     * <p>If the constructor's declaring class is an inner class in a
     * non-static context, the first argument to the constructor needs
     * to be the enclosing instance; see section 15.9.3 of
     * <cite>The Java&trade; Language Specification</cite>.
     *
     * <p>If the required access and argument checks succeed and the
     * instantiation will proceed, the constructor's declaring class
     * is initialized if it has not already been initialized.
     *
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     *
     * @param initargs array of objects to be passed as arguments to
     * the constructor call; values of primitive types are wrapped in
     * a wrapper object of the appropriate type (e.g. a {@code float}
     * in a {@link java.lang.Float Float})
     *
     * @return a new object created by calling the constructor
     * this object represents
     *
     * @exception IllegalAccessException    if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @exception IllegalArgumentException  if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion; if
     *              this constructor pertains to an enum type.
     * @exception InstantiationException    if the class that declares the
     *              underlying constructor represents an abstract class.
     * @exception InvocationTargetException if the underlying constructor
     *              throws an exception.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    @CallerSensitive
    public T newInstance(Object ... initargs)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
    	return decoratedConstructor.newInstance(initargs);
    }

    /**
     * {@inheritDoc}
     * @since 1.5
     */
    @Override
    public boolean isVarArgs() {
    	return decoratedConstructor.isVarArgs();
    }

    /**
     * {@inheritDoc}
     * @jls 13.1 The Form of a Binary
     * @since 1.5
     */
    @Override
    public boolean isSynthetic() {
    	return decoratedConstructor.isSynthetic();
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     * @since 1.5
     */
    @SuppressWarnings("hiding")
	public <T extends Annotation> T getAnnotation(java.lang.Class<T> annotationClass) {
    	return decoratedConstructor.getAnnotation(annotationClass);
    }

    /**
     * {@inheritDoc}
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations()  {
    	return decoratedConstructor.getDeclaredAnnotations();
    }

    /**
     * {@inheritDoc}
     * @since 1.5
     */
    @Override
    public Annotation[][] getParameterAnnotations() {
    	return decoratedConstructor.getParameterAnnotations();
    }

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    @Override
    public AnnotatedType getAnnotatedReturnType() {
    	return decoratedConstructor.getAnnotatedReturnType();
    }

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    @Override
    public AnnotatedType getAnnotatedReceiverType() {
    	return decoratedConstructor.getAnnotatedReceiverType();
    }

	@Override
	public Parameter[] getParameters() {
		return decoratedConstructor.getParameters();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(java.lang.Class<T> annotationClass) {
		return decoratedConstructor.getAnnotationsByType(annotationClass);
	}

	@Override
	public AnnotatedType[] getAnnotatedParameterTypes() {
		return decoratedConstructor.getAnnotatedParameterTypes();
	}

	@Override
	public AnnotatedType[] getAnnotatedExceptionTypes() {
		return decoratedConstructor.getAnnotatedExceptionTypes();
	}
}
