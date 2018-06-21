package jmplib;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Map;

import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.IntrospectionUtils;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import jmplib.reflect.TypeVariable;

/**
 * This is the interface of operations that every intercessor implements
 *
 * @author Jose Manuel Redondo Lopez
 */
public interface IIntercessor {
    /* **************************************
     * FIELDS
     **************************************/

    /**
     * Adds fields to the specified class
     *
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * <code>Intercessor.addField(Person.class, new jmplib.reflect.Field(String.class, "lastName"));</code>
     * </pre>
     *
     * @param clazz  A java.lang.Class or a jmplib.reflect.Class
     * @param fields Set of jmplib.reflect.Field or java.lang.reflect.Field to add
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void addField(Type clazz, java.lang.reflect.Field... fields) throws StructuralIntercessionException {
        addField(clazz, IntrospectionUtils.decorateFieldList(fields));
    }

    /**
     * @see IIntercessor#addField(Type, java.lang.reflect.Field...)
     */
    void addField(Type clazz, jmplib.reflect.Field... fields) throws StructuralIntercessionException;

    /**
     * Replaces fields in the specified class
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * <code>Intercessor.replaceField(Calculator.class, new jmplib.reflect.Field("lastResult", double.class));</code>
     * </pre>
     *
     * @param clazz  A java.lang.Class or a jmplib.reflect.Class
     * @param fields Set of jmplib.reflect.Field or java.lang.reflect.Field to replace.
     *               The field name is used to identify the field to replace. The rest
     *               of the information is taken from the Field class.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void replaceField(Type clazz, java.lang.reflect.Field... fields)
            throws StructuralIntercessionException {
        replaceField(clazz, IntrospectionUtils.decorateFieldList(fields));
    }

    /**
     * @see IIntercessor#replaceField(Type, java.lang.reflect.Field...)
     */
    void replaceField(Type clazz, jmplib.reflect.Field... fields) throws StructuralIntercessionException;

    /**
     * Removes fields in the specified class
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * <code>Intercessor.deleteField(Person.class, new jmplib.reflect.Field("lastName"));</code>
     * </pre>
     *
     * @param clazz  A java.lang.Class or a jmplib.reflect.Class
     * @param fields Set of jmplib.reflect.Field or java.lang.reflect.Field to remove.
     *               The field name is used to identify the field to replace.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void removeField(Type clazz, java.lang.reflect.Field... fields)
            throws StructuralIntercessionException {
        removeField(clazz, IntrospectionUtils.decorateFieldList(fields));
    }

    /**
     * @see IIntercessor#removeField(Type, java.lang.reflect.Field...)
     */
    void removeField(Type clazz, jmplib.reflect.Field... fields) throws StructuralIntercessionException;

    /* **************************************
     * METHODS
     **************************************/

    /**
     * Adds methods to the specified class
     *
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * <code>// Declaring MethodType
     *  MethodType mt = MethodType.methodType(int.class, int.class);
     *
     *  // Adding method to Counter
     *  Intercessor.addMethod(Counter.class, new jmplib.reflect.Method("sum", mt,
     *  	"return this.counter += value;", "value")); </code>
     * </pre>
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param methods Set of jmplib.reflect.Method or java.lang.reflect.Method to add.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void addMethod(Type clazz, java.lang.reflect.Method... methods)
            throws StructuralIntercessionException {
        addMethod(clazz, IntrospectionUtils.decorateMethodList(methods));
    }

    /**
     * @see IIntercessor#addMethod(Type, java.lang.reflect.Method...)
     */
    void addMethod(Type clazz, jmplib.reflect.Method... methods) throws StructuralIntercessionException;

    /**
     * Replaces a method of the specified class
     *
     * @param clazz     A java.lang.Class or a jmplib.reflect.Class
     * @param method    jmplib.reflect.Method or java.lang.reflect.Method that identifies
     *                  the method to be replaced
     * @param newMethod jmplib.reflect.Method or java.lang.reflect.Method that identifies
     *                  the new method to replace with
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void replaceMethod(Type clazz, java.lang.reflect.Method method, java.lang.reflect.Method newMethod)
            throws StructuralIntercessionException {
        try {
            replaceMethod(clazz, Introspector.decorateMethod(method), Introspector.decorateMethod(newMethod));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new StructuralIntercessionException(e.getMessage());
        }
    }

    /**
     * @see IIntercessor#replaceMethod(Type, java.lang.reflect.Method, java.lang.reflect.Method)
     */
    void replaceMethod(Type clazz, jmplib.reflect.Method method, jmplib.reflect.Method newMethod)
            throws StructuralIntercessionException;

    /**
     * Replaces a series of methods from the specified class
     * <p>
     * For example, modifying the method to acept a more generic type (Dog ->
     * Animal):
     * </p>
     *
     * <pre>
     * <code>// Creating MethodType
     * MethodType newType = MethodType.methodType(void.class, Pet.class);
     *
     * // Modifying the method
     * Intercessor.replaceMethod(Owner.class, new jmplib.reflect.Method("addPet", newType,
     * 		"this.pet = dog;"));</code>
     * </pre>
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param methods Set of jmplib.reflect.Method or java.lang.reflect.Method to
     *                replace. The method name and type is used to identify the method
     *                to replace. The rest of the information is taken from the Method
     *                class.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void replaceMethod(Type clazz, Map<jmplib.reflect.Method, jmplib.reflect.Method> methods)
            throws StructuralIntercessionException;

    /**
     * Replace the implementation of a series of methods of the specified class
     * <p>
     * For example, modifying the method to use a new field called lastResult:
     * </p>
     *
     * <pre>
     * <code>// Modify a the method code
     * Intercessor.replaceImplementation(Calculator.class, new jmplib.reflect.Method("sum",
     * 		"this.lastResult = a + b;"
     * 		+ "return this.lastResult;"));</code>
     * </pre>
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param methods Set of jmplib.reflect.Method or java.lang.reflect.Method to
     *                replace its implementation. The method name and type is used to
     *                identify the method to replace. The rest of the information is
     *                taken from the Method class.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void replaceImplementation(Type clazz, java.lang.reflect.Method... methods)
            throws StructuralIntercessionException {
        replaceImplementation(clazz, IntrospectionUtils.decorateMethodList(methods));
    }

    /**
     * @see IIntercessor#replaceImplementation(Type, java.lang.reflect.Method...)
     */
    void replaceImplementation(Type clazz, jmplib.reflect.Method... methods)
            throws StructuralIntercessionException;

    /**
     * Removes a series of methods from the specified class
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * <code>// Deleting method
     * Intercessor.deleteMethod(Dog.class, new jmplib.reflect.Method("bark"));
     *
     * Dog dog = new Dog();
     * dog.bark(); // Throws RuntimeException</code>
     * </pre>
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param methods Set of jmplib.reflect.Method or java.lang.reflect.Method to
     *                remove. The method name and type is used to identify the method to
     *                remove. The rest of the information is taken from the Method
     *                class.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void removeMethod(Type clazz, java.lang.reflect.Method... methods)
            throws StructuralIntercessionException {
        removeMethod(clazz, IntrospectionUtils.decorateMethodList(methods));
    }

    /**
     * @see IIntercessor#removeMethod(Type, java.lang.reflect.Method...)
     */
    void removeMethod(Type clazz, jmplib.reflect.Method... methods) throws StructuralIntercessionException;

    /* **************************************
     * CLASS MANIPULATION
     **************************************/

    /**
     * Adds all the members of the public interface of the specified class to
     * another class
     *
     * @param origin      A java.lang.Class or a jmplib.reflect.Class
     * @param destination A java.lang.Class or a jmplib.reflect.Class
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void addPublicInterfaceOf(Type origin, Type destination) throws StructuralIntercessionException;

    /**
     * Creates a class clone of the specified class with the specified name and
     * belonging to the provided package
     *
     * @param packageName Package name of the new class to be created
     * @param className   Name of the newly created class
     * @param origin      A java.lang.Class or a jmplib.reflect.Class
     * @return A new class copying the class structure of the provided one
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    jmplib.reflect.Class<?> createClassClone(String packageName, String className, Type origin)
            throws StructuralIntercessionException;

    /* **************************************
     * DYNAMIC INHERITANCE
     **************************************/

    /**
     * Adds an interface to the provided class. The class must already provide
     * implementations for all the interface methods
     *
     * @param clazz          A java.lang.Class or a jmplib.reflect.Class
     * @param interf         A java.lang.Class or a jmplib.reflect.Class representing an
     *                       interface to add
     * @param typeParameters Types of the generic parameters of the interface to be added. This
     *                       is an optional parameter. However, if specified, it must comply
     *                       with the expected type parameter number of the passed interface.
     *                       <p>
     *                       Example:
     *                       <p>
     *                       To add the Comparable interface to the Dog object, the call should
     *                       be:
     *                       <p>
     *                       Intercessor.addInterface(Dog.class, Comparable.class);
     *                       <p>
     *                       The call will fail if the Dog class do not have a
     *                       compareTo(Object) method already added.
     *                       <p>
     *                       If we need to add the Comparable<Dog> interface instead, then the
     *                       call should be:
     *                       <p>
     *                       Intercessor.addInterface(Dog.class, Comparable.class, Dog.class);
     *                       <p>
     *                       The call will fail if the Dog class do not have a compareTo(Dog)
     *                       method already added.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void addInterface(Type clazz, Type interf, Type... typeParameters) throws StructuralIntercessionException;

    /**
     * Version of the previous method able to add multiple interfaces.
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param interfs A collection of objects representing interfaces and their type
     *                parameters
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void addInterface(Type clazz, Map<Type, Type[]> interfs) throws StructuralIntercessionException;

    /**
     * Removes an interface from a class
     *
     * @param clazz  A java.lang.Class or a jmplib.reflect.Class
     * @param interf A java.lang.Class or a jmplib.reflect.Class representing an
     *               interface
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void removeInterface(Type clazz, Type interf) throws StructuralIntercessionException;

    /**
     * Version of the addInterface method that also allow to add a series of method
     * that belong to the interface to be implemented. This allows the
     * implementation of an interface and its messages over a class in a single
     * primitive
     *
     * @param clazz          A java.lang.Class or a jmplib.reflect.Class
     * @param interf         A java.lang.Class or a jmplib.reflect.Class representing an
     *                       interface
     * @param methods        Method of the interface
     * @param typeParameters Types of the generic parameters of the interface to be added. This
     *                       is an optional parameter. However, if specified, it must comply
     *                       with the expected type parameter number of the passed interface.
     *                       <p>
     *                       Example:
     *                       <p>
     *                       To add the Comparable interface to the Dog object, the call should
     *                       be:
     *                       <p>
     *                       Intercessor.addInterface(Dog.class, Comparable.class);
     *                       <p>
     *                       The call will fail if the Dog class do not have a
     *                       compareTo(Object) method already added.
     *                       <p>
     *                       If we need to add the Comparable<Dog> interface instead, then the
     *                       call should be:
     *                       <p>
     *                       Intercessor.addInterface(Dog.class, Comparable.class, Dog.class);
     *                       <p>
     *                       The call will fail if the Dog class do not have a compareTo(Dog)
     *                       method already added.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void implementInterface(Type clazz, Type interf, java.lang.reflect.Method[] methods,
                                    Class<?>... typeParameters) throws StructuralIntercessionException {
        implementInterface(clazz, interf, IntrospectionUtils.decorateMethodList(methods), typeParameters);
    }

    /**
     * @see IIntercessor#implementInterface(Type, Type, java.lang.reflect.Method[],
     * Class...)
     */
    void implementInterface(Type clazz, Type interf, jmplib.reflect.Method[] methods, Class<?>... typeParameters)
            throws StructuralIntercessionException;

    /**
     * Changes the superclass of an existing class, modifying member visibilities
     * accordingly.
     *
     * @param clazz          A java.lang.Class or a jmplib.reflect.Class
     * @param superclazz     A java.lang.Class or a jmplib.reflect.Class
     * @param typeParameters Type parameters of the new superclass
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void setSuperclass(Type clazz, Type superclazz, Type... typeParameters)
            throws StructuralIntercessionException;

    /**
     * Removes the superclass of an existing class, setting it to Object.
     *
     * @param clazz A java.lang.Class or a jmplib.reflect.Class
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void removeSuperclass(Type clazz) throws StructuralIntercessionException;

    /* **************************************
     * IMPORTS
     **************************************/

    /**
     * Add the provided imports to the source file that defines the specified class.
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param imports Classes to add as imports to the source file of the class
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void addImport(Type clazz, java.lang.Class<?>... imports) throws StructuralIntercessionException {
        addImport(clazz, IntrospectionUtils.decorateClassList(imports));
    }

    /**
     * Add the provided imports to the source file that defines the specified class.
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param imports Packages to add as imports to the source file of the class
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void addImport(Type clazz, java.lang.Package... imports) throws StructuralIntercessionException;

    /**
     * @see IIntercessor#addImport(Type, Class...)
     */
    void addImport(Type clazz, jmplib.reflect.Class<?>... imports) throws StructuralIntercessionException;

    /**
     * Get the imports of the source file that defines the specified class.
     *
     * @param clazz A java.lang.Class or a jmplib.reflect.Class
     * @return The import set of the file that defines the provided class
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    AnnotatedElement[] getImports(Type clazz) throws StructuralIntercessionException;

    /**
     * Replace the imports of the source file that defines the specified class with
     * the provided ones.
     *
     * @param clazz   A java.lang.Class or a jmplib.reflect.Class
     * @param imports Modifies the import set of the file that defines the provided class
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void setImports(Type clazz, AnnotatedElement... imports) throws StructuralIntercessionException;

    /* **************************************
     * ANNOTATIONS (https://beginnersbook.com/2014/09/java-annotations/)
     **************************************/

    /**
     * Add the provided annotations to the specified class.
     *
     * @param clazz       A java.lang.Class or a jmplib.reflect.Class
     * @param annotations Set of annotations to be used to add to the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void addAnnotation(Type clazz, Type... annotations) throws StructuralIntercessionException;

    /**
     * Replace the annotations of the specified class with the provided ones.
     *
     * @param clazz       A java.lang.Class or a jmplib.reflect.Class
     * @param annotations Set of annotations to be used to replace the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void setAnnotation(Type clazz, Type... annotations) throws StructuralIntercessionException;

    /**
     * Add the provided annotations to the specified method.
     *
     * @param met         A java.lang.reflect.Method or a jmplib.reflect.Method instance
     * @param annotations Set of annotations to be used to add to the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void addAnnotation(java.lang.reflect.Method met, Type... annotations)
            throws StructuralIntercessionException {
        try {
            addAnnotation(Introspector.decorateMethod(met), annotations);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new StructuralIntercessionException(e.getMessage());
        }
    }

    void addAnnotation(jmplib.reflect.Method met, Type... annotations) throws StructuralIntercessionException;

    /**
     * Replace the annotations of a method with the provided ones.
     *
     * @param met         Method to add annotations to
     * @param annotations Set of annotations to be used to replace the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void setAnnotation(java.lang.reflect.Method met, Type... annotations)
            throws StructuralIntercessionException {
        try {
            setAnnotation(Introspector.decorateMethod(met), annotations);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new StructuralIntercessionException(e.getMessage());
        }
    }

    void setAnnotation(jmplib.reflect.Method met, Type... annotations) throws StructuralIntercessionException;

    /* **************************************
     * GENERIC TYPES
     **************************************/

    /**
     * Add the provided generic type to the specified class.
     *
     * @param clazz A java.lang.Class or a jmplib.reflect.Class
     * @param types Set of generic types to be used to add to the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void addGenericType(Type clazz, jmplib.reflect.TypeVariable<?>... types)
            throws StructuralIntercessionException;

    /**
     * Replace the generic types of the specified class with the provided ones.
     *
     * @param clazz A java.lang.Class or a jmplib.reflect.Class
     * @param types Set of generic types to be used to replace the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    void setGenericType(Type clazz, jmplib.reflect.TypeVariable<?>... types)
            throws StructuralIntercessionException;

    /**
     * Add the provided generic types to the specified method.
     *
     * @param met   A java.lang.reflect.Method or a jmplib.reflect.Method instance
     * @param types Set of annotations to be used to add to the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void addGenericType(java.lang.reflect.Method met, jmplib.reflect.TypeVariable<?>... types)
            throws StructuralIntercessionException {
        try {
            addGenericType(Introspector.decorateMethod(met), types);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new StructuralIntercessionException(e.getMessage());
        }
    }

    void addGenericType(jmplib.reflect.Method met, jmplib.reflect.TypeVariable<?>... types)
            throws StructuralIntercessionException;

    /**
     * Replace the generic types of a method with the provided ones.
     *
     * @param met   Method to change the generic types to
     * @param types Set of annotations to be used to replace the existing ones.
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void setGenericType(java.lang.reflect.Method met, jmplib.reflect.TypeVariable<?>... types)
            throws StructuralIntercessionException {
        try {
            setGenericType(Introspector.decorateMethod(met), types);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new StructuralIntercessionException(e.getMessage());
        }
    }

    /**
     * @see IIntercessor#setGenericType(Method, TypeVariable[])
     */
    void setGenericType(jmplib.reflect.Method met, jmplib.reflect.TypeVariable<?>... types)
            throws StructuralIntercessionException;

    /* *************************************
     * COMMIT
     **************************************/

    /**
     * Performs the operations indicated to the intercessor, if the intercessor has
     * the ability to store the operations until they are executed at the same time.
     * This enables the creation of a single class version with all the specified
     * operations included, instead of a new version per operation. Some
     * intercessors may not have an implementation for this operation. Therefore, is
     * implemented as empty by default.
     *
     * @throws StructuralIntercessionException If problems with the metaprogramming primitives are detected (exception
     *                                         message indicates the concrete problem, as it wraps the inner exception)
     */
    default void commit() throws StructuralIntercessionException {
    }

}
