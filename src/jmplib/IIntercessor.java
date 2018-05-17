package jmplib;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Map;

import jmplib.exceptions.StructuralIntercessionException;

/**
 * This is the interface of operations that every intercessor implements
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
public interface IIntercessor {
	/***************************************
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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param fields
	 *            Set of jmplib.reflect.Field or java.lang.reflect.Field to add
	 * @throws StructuralIntercessionException
	 */
	public void addField(Type clazz, Member... fields) throws StructuralIntercessionException;

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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param fields
	 *            Set of jmplib.reflect.Field or java.lang.reflect.Field to replace.
	 *            The field name is used to identify the field to replace. The rest
	 *            of the information is taken from the Field class.
	 * @throws StructuralIntercessionException
	 */
	public void replaceField(Type clazz, Member... fields) throws StructuralIntercessionException;

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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param fields
	 *            Set of jmplib.reflect.Field or java.lang.reflect.Field to remove.
	 *            The field name is used to identify the field to replace.
	 * @throws StructuralIntercessionException
	 */
	public void removeField(Type clazz, Member... fields) throws StructuralIntercessionException;

	/***************************************
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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param methods
	 *            Set of jmplib.reflect.Method or java.lang.reflect.Method to add.
	 * @throws StructuralIntercessionException
	 */
	public void addMethod(Type clazz, AccessibleObject... methods) throws StructuralIntercessionException;

	/**
	 * Replaces a method of the specified class
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param method
	 *            jmplib.reflect.Method or java.lang.reflect.Method that identifies
	 *            the method to be replaced
	 * @param newMethod
	 *            jmplib.reflect.Method or java.lang.reflect.Method that identifies
	 *            the new method to replace with
	 * @throws StructuralIntercessionException
	 */
	public void replaceMethod(Type clazz, AccessibleObject method, AccessibleObject newMethod)
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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param methods
	 *            Set of jmplib.reflect.Method or java.lang.reflect.Method to
	 *            replace. The method name and type is used to identify the method
	 *            to replace. The rest of the information is taken from the Method
	 *            class.
	 * @throws StructuralIntercessionException
	 */
	public void replaceMethod(Type clazz, Map<AccessibleObject, AccessibleObject> methods)
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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param methods
	 *            Set of jmplib.reflect.Method or java.lang.reflect.Method to
	 *            replace its implementation. The method name and type is used to
	 *            identify the method to replace. The rest of the information is
	 *            taken from the Method class.
	 * @throws StructuralIntercessionException
	 */
	public void replaceImplementation(Type clazz, AccessibleObject... methods) throws StructuralIntercessionException;

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
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param methods
	 *            Set of jmplib.reflect.Method or java.lang.reflect.Method to
	 *            remove. The method name and type is used to identify the method to
	 *            remove. The rest of the information is taken from the Method
	 *            class.
	 * @throws StructuralIntercessionException
	 */
	public void removeMethod(Type clazz, AccessibleObject... methods) throws StructuralIntercessionException;

	/***************************************
	 * CLASS MANIPULATION
	 **************************************/

	/**
	 * Adds all the members of the public interface of the specified class to
	 * another class
	 * 
	 * @param origin
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param destination
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @throws StructuralIntercessionException
	 */
	public void addPublicInterfaceOf(Type origin, Type destination) throws StructuralIntercessionException;

	/**
	 * Creates a class clone of the specified class with the specified name and
	 * belonging to the provided package
	 * 
	 * @param packageName
	 *            Package name of the new class to be created
	 * @param className
	 *            Name of the newly created class
	 * @param origin
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @return
	 * @throws StructuralIntercessionException
	 */
	public jmplib.reflect.Class<?> createClassClone(String packageName, String className, Type origin)
			throws StructuralIntercessionException;

	/***************************************
	 * DYNAMIC INHERITANCE
	 **************************************/

	/**
	 * Adds an interface to the provided class. The class must already provide
	 * implementations for all the interface methods
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param interf
	 *            A java.lang.Class or a jmplib.reflect.Class representing an
	 *            interface to add
	 * @param typeParameters
	 *            Types of the generic parameters of the interface to be added. This
	 *            is an optional parameter. However, if specified, it must comply
	 *            with the expected type parameter number of the passed interface.
	 * 
	 *            Example:
	 * 
	 *            To add the Comparable interface to the Dog object, the call should
	 *            be:
	 * 
	 *            Intercessor.addInterface(Dog.class, Comparable.class);
	 * 
	 *            The call will fail if the Dog class do not have a
	 *            compareTo(Object) method already added.
	 * 
	 *            If we need to add the Comparable<Dog> interface instead, then the
	 *            call should be:
	 * 
	 *            Intercessor.addInterface(Dog.class, Comparable.class, Dog.class);
	 * 
	 *            The call will fail if the Dog class do not have a compareTo(Dog)
	 *            method already added.
	 * @throws StructuralIntercessionException
	 */
	public void addInterface(Type clazz, Type interf, Type... typeParameters) throws StructuralIntercessionException;

	/**
	 * Version of the previous method able to add multiple interfaces.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param interfs
	 *            A collection of objects representing interfaces and their type
	 *            parameters
	 * @throws StructuralIntercessionException
	 */
	public void addInterface(Type clazz, Map<Type, Type[]> interfs) throws StructuralIntercessionException;

	/**
	 * Removes an interface from a class
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param interf
	 *            A java.lang.Class or a jmplib.reflect.Class representing an
	 *            interface
	 * @throws StructuralIntercessionException
	 */
	public void removeInterface(Type clazz, Type interf) throws StructuralIntercessionException;

	/**
	 * Version of the addInterface method that also allow to add a series of method
	 * that belong to the interface to be implemented. This allows the
	 * implementation of an interface and its messages over a class in a single
	 * primitive
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param interf
	 *            A java.lang.Class or a jmplib.reflect.Class representing an
	 *            interface
	 * @param methods
	 *            Method of the interface
	 * @param typeParameters
	 *            Types of the generic parameters of the interface to be added. This
	 *            is an optional parameter. However, if specified, it must comply
	 *            with the expected type parameter number of the passed interface.
	 * 
	 *            Example:
	 * 
	 *            To add the Comparable interface to the Dog object, the call should
	 *            be:
	 * 
	 *            Intercessor.addInterface(Dog.class, Comparable.class);
	 * 
	 *            The call will fail if the Dog class do not have a
	 *            compareTo(Object) method already added.
	 * 
	 *            If we need to add the Comparable<Dog> interface instead, then the
	 *            call should be:
	 * 
	 *            Intercessor.addInterface(Dog.class, Comparable.class, Dog.class);
	 * 
	 *            The call will fail if the Dog class do not have a compareTo(Dog)
	 *            method already added.
	 * @throws StructuralIntercessionException
	 */
	public void implementInterface(Type clazz, Type interf, AccessibleObject[] methods, Class<?>... typeParameters)
			throws StructuralIntercessionException;

	/**
	 * Changes the superclass of an existing class, modifying member visibilities
	 * accordingly.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param superclazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param typeParameters
	 * @throws StructuralIntercessionException
	 */
	public void setSuperclass(Type clazz, Type superclazz, Type... typeParameters)
			throws StructuralIntercessionException;

	/**
	 * Removes the superclass of an existing class, setting it to Object.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @throws StructuralIntercessionException
	 */
	public void removeSuperclass(Type clazz) throws StructuralIntercessionException;

	/***************************************
	 * IMPORTS
	 **************************************/

	/**
	 * Add the provided imports to the source file that defines the specified class.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param imports
	 * @throws StructuralIntercessionException
	 */
	public void addImport(Type clazz, AnnotatedElement... imports) throws StructuralIntercessionException;

	/**
	 * Get the imports of the source file that defines the specified class.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @return
	 * @throws StructuralIntercessionException
	 */
	public AnnotatedElement[] getImports(Type clazz) throws StructuralIntercessionException;

	/**
	 * Replace the imports of the source file that defines the specified class with
	 * the provided ones.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param imports
	 * @throws StructuralIntercessionException
	 */
	public void setImports(Type clazz, AnnotatedElement... imports) throws StructuralIntercessionException;

	/***************************************
	 * ANNOTATIONS (https://beginnersbook.com/2014/09/java-annotations/)
	 **************************************/

	/**
	 * Add the provided annotations to the specified class.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param annotations
	 *            Set of annotations to be used to add to the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void addAnnotation(Type clazz, Type... annotations) throws StructuralIntercessionException;

	/**
	 * Replace the annotations of the specified class with the provided ones.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param annotations
	 *            Set of annotations to be used to replace the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void setAnnotation(Type clazz, Type... annotations) throws StructuralIntercessionException;

	/**
	 * Add the provided annotations to the specified method.
	 * 
	 * @param met
	 *            A java.lang.reflect.Method or a jmplib.reflect.Method instance
	 * @param annotations
	 *            Set of annotations to be used to add to the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void addAnnotation(AccessibleObject met, Type... annotations) throws StructuralIntercessionException;

	/**
	 * Replace the annotations of a method with the provided ones.
	 * 
	 * @param met
	 * @param annotations
	 *            Set of annotations to be used to replace the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void setAnnotation(AccessibleObject met, Type... annotations) throws StructuralIntercessionException;

	/***************************************
	 * GENERIC TYPES
	 **************************************/

	/**
	 * Add the provided generic type to the specified class.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param types
	 *            Set of generic types to be used to add to the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void addGenericType(Type clazz, jmplib.reflect.TypeVariable<?>... types)
			throws StructuralIntercessionException;

	/**
	 * Replace the generic types of the specified class with the provided ones.
	 * 
	 * @param clazz
	 *            A java.lang.Class or a jmplib.reflect.Class
	 * @param types
	 *            Set of generic types to be used to replace the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void setGenericType(Type clazz, jmplib.reflect.TypeVariable<?>... types)
			throws StructuralIntercessionException;

	/**
	 * Add the provided generic types to the specified method.
	 * 
	 * @param met
	 *            A java.lang.reflect.Method or a jmplib.reflect.Method instance
	 * @param types
	 *            Set of annotations to be used to add to the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void addGenericType(AccessibleObject met, jmplib.reflect.TypeVariable<?>... types)
			throws StructuralIntercessionException;

	/**
	 * Replace the generic types of a method with the provided ones.
	 * 
	 * @param met
	 * @param types
	 *            Set of annotations to be used to replace the existing ones.
	 * @throws StructuralIntercessionException
	 */
	public void setGenericType(AccessibleObject met, jmplib.reflect.TypeVariable<?>... types)
			throws StructuralIntercessionException;

	/**************************************
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
	 * @throws StructuralIntercessionException
	 */
	public default void commit() throws StructuralIntercessionException {
	}

}
