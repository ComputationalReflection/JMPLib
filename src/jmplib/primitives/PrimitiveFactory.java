package jmplib.primitives;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.primitives.impl.AddAnnotationToClassPrimitive;
import jmplib.primitives.impl.AddAnnotationToMethodPrimitive;
import jmplib.primitives.impl.AddFieldPrimitive;
import jmplib.primitives.impl.AddGenericTypeToClassPrimitive;
import jmplib.primitives.impl.AddGenericTypeToMethodPrimitive;
import jmplib.primitives.impl.AddImportPrimitive;
import jmplib.primitives.impl.AddInterfacePrimitive;
import jmplib.primitives.impl.AddMethodPrimitive;
import jmplib.primitives.impl.DeleteFieldPrimitive;
import jmplib.primitives.impl.DeleteMethodPrimitive;
import jmplib.primitives.impl.GetImportPrimitive;
import jmplib.primitives.impl.RemoveInterfacePrimitive;
import jmplib.primitives.impl.RemoveSuperClassPrimitive;
import jmplib.primitives.impl.ReplaceFieldPrimitive;
import jmplib.primitives.impl.ReplaceImplementationPrimitive;
import jmplib.primitives.impl.ReplaceMethodPrimitive;
import jmplib.primitives.impl.SetAnnotationToClassPrimitive;
import jmplib.primitives.impl.SetAnnotationToMethodPrimitive;
import jmplib.primitives.impl.SetGenericTypeToClassPrimitive;
import jmplib.primitives.impl.SetGenericTypeToMethodPrimitive;
import jmplib.primitives.impl.SetImportPrimitive;
import jmplib.primitives.impl.SetSuperClassPrimitive;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.MemberFinder;

/**
 * Factory for the creation of the intercession primitives
 * 
 * @author Ignacio Lagartos, Jose Manuel Redondo
 * @version 1.1 (Redondo) Added new primitives (import, annotations...)
 */
@ExcludeFromJMPLib
public class PrimitiveFactory {

	private static final SourceCodeCache sourceCodeCache = SourceCodeCache.getInstance();

	/**
	 * Creates {@link AddMethodPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param type
	 *            The type of the method
	 * @param paramNames
	 *            The parameter names
	 * @param body
	 *            The body of the method
	 * @param modifiers
	 *            The modifiers of the method
	 * @param exceptions
	 *            The exceptions of the method
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddMethodPrimitive(Class<?> clazz, String name, MethodType type, String[] paramNames,
			String body, int modifiers, Class<?>... exceptions) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		if (paramNames.length != type.parameterCount()) {
			throw new IllegalArgumentException(
					"The number of parameter" + " names must match with the number of parameters");
		}
		Primitive primitive = new AddMethodPrimitive(classContent, name, type.returnType(), type.parameterArray(),
				exceptions, paramNames, body, modifiers);
		return primitive;
	}

	/**
	 * Creates {@link AddMethodPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param type
	 *            The type of the method
	 * @param paramNames
	 *            The parameter names
	 * @param body
	 *            The body of the method
	 * @param modifiers
	 *            The modifiers of the method
	 * @param exceptions
	 *            The exceptions of the method
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddMethodPrimitive(Class<?> clazz, String name, MethodType type, String[] paramNames,
			String body, int modifiers, Type[] genericParamTypes, Type genericReturnType,
			jmplib.reflect.TypeVariable<?>[] methodTypeParameters, Class<?>... exceptions)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		if (paramNames.length != type.parameterCount()) {
			throw new IllegalArgumentException(
					"The number of parameter" + " names must match with the number of parameters");
		}
		Primitive primitive = new AddMethodPrimitive(classContent, name, type.returnType(), type.parameterArray(),
				exceptions, paramNames, body, modifiers, genericParamTypes, genericReturnType, methodTypeParameters);
		return primitive;
	}

	/**
	 * Creates {@link DeleteMethodPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createDeleteMethodPrimitive(Class<?> clazz, String name)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Method m;
		try {
			m = MemberFinder.findMethod(clazz, name);
		} catch (NoSuchMethodException e) {
			throw new StructuralIntercessionException(e.getMessage(), e);
		}
		Primitive primitive = new DeleteMethodPrimitive(classContent, name, m.getReturnType(), m.getParameterTypes());
		return primitive;
	}

	/**
	 * Creates {@link DeleteMethodPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param type
	 *            The type of the method
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createDeleteMethodPrimitive(Class<?> clazz, String name, MethodType type)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Primitive primitive = new DeleteMethodPrimitive(classContent, name, type.returnType(), type.parameterArray());
		return primitive;
	}

	/**
	 * Creates {@link ReplaceImplementationPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param body
	 *            The new body
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createReplaceImplementationPrimitive(Class<?> clazz, String name, String body)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Method m;
		try {
			m = MemberFinder.findMethod(clazz, name);
		} catch (NoSuchMethodException e) {
			throw new StructuralIntercessionException(e.getMessage(), e);
		}
		Primitive primitive = new ReplaceImplementationPrimitive(classContent, name, body, m.getReturnType(),
				m.getParameterTypes());
		return primitive;
	}

	/**
	 * Creates {@link ReplaceImplementationPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param type
	 *            The type of the method
	 * @param body
	 *            The new body
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createReplaceImplementationPrimitive(Class<?> clazz, String name, MethodType type,
			String body) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Primitive primitive = new ReplaceImplementationPrimitive(classContent, name, body, type.returnType(),
				type.parameterArray());
		return primitive;
	}

	/**
	 * Creates {@link ReplaceMethodPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param newMethodType
	 *            The new method type
	 * @param body
	 *            The new body
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createReplaceMethodPrimitive(Class<?> clazz, String name, MethodType newMethodType,
			String body) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Method original;
		try {
			original = MemberFinder.findMethod(clazz, name);
		} catch (NoSuchMethodException e) {
			throw new StructuralIntercessionException(e.getMessage(), e);
		}
		Primitive primitive = new ReplaceMethodPrimitive(classContent, name, body, original.getReturnType(),
				original.getParameterTypes(), newMethodType.returnType(), newMethodType.parameterArray());
		return primitive;
	}

	/**
	 * Creates {@link ReplaceMethodPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the method
	 * @param methodType
	 *            The method type
	 * @param newMethodType
	 *            The new method type
	 * @param body
	 *            The new body
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createReplaceMethodPrimitive(Class<?> clazz, String name, MethodType methodType,
			MethodType newMethodType, String body) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Primitive primitive = new ReplaceMethodPrimitive(classContent, name, body, methodType.returnType(),
				methodType.parameterArray(), newMethodType.returnType(), newMethodType.parameterArray());
		return primitive;
	}

	/**
	 * Creates {@link AddFieldPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param modifiers
	 *            The modifiers of the field
	 * @param type
	 *            The type of the field
	 * @param name
	 *            The name of the field
	 * @param init
	 *            The initialization sequence of the field
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddFieldPrimitive(Class<?> clazz, int modifiers, Class<?> type, String name,
			String init) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Primitive primitive = new AddFieldPrimitive(classContent, modifiers, type, name, init);
		return primitive;
	}

	/**
	 * Creates {@link DeleteFieldPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the field
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createDeleteFieldPrimitive(Class<?> clazz, String name)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Field f;
		try {
			f = MemberFinder.findField(clazz, name);
		} catch (NoSuchFieldException e) {
			String error = String.format("The field \"%s\" does not exist in the class %s", e.getMessage(),
					clazz.getName());
			throw new StructuralIntercessionException(error, e);
		}
		Primitive primitive = new DeleteFieldPrimitive(classContent, name, f.getType());
		return primitive;
	}

	/**
	 * Creates {@link ReplaceFieldPrimitive}
	 * 
	 * @param clazz
	 *            The class to modify
	 * @param name
	 *            The name of the field
	 * @param newType
	 *            The new type
	 * @param newInit
	 *            The new initialization sequence
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createReplaceFieldPrimitive(Class<?> clazz, String name, Class<?> newType, String newInit)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);
		Primitive primitive = new ReplaceFieldPrimitive(classContent, name, newType, newInit);
		return primitive;
	}

	/**************************************
	 * DYNAMIC INHERITANCE
	 **************************************/

	/**
	 * Creates {@link AddInterfacePrimitive}
	 * 
	 * @param clazz
	 *            Class to add interface to
	 * @param interf
	 *            Interface to add to the class
	 * @param typeParameters
	 *            Type of the generic parameters of the interface to add (optional)
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddInterfacePrimitive(Class<?> clazz, Class<?> interf, Class<?>... typeParameters)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new AddInterfacePrimitive(classContent, interf, typeParameters);
		return primitive;
	}

	/**
	 * Creates {@link RemoveInterfacePrimitive}
	 * 
	 * @param clazz
	 *            Class to add interface to
	 * @param interf
	 *            Interface to add to the class
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createRemoveInterfacePrimitive(Class<?> clazz, Class<?> interf)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new RemoveInterfacePrimitive(classContent, interf);
		return primitive;
	}

	/**
	 * Creates {@link SetSuperClassPrimitive}
	 * 
	 * @param clazz
	 *            Class to add interface to
	 * @param interf
	 *            Interface to add to the class
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createSetSuperClassPrimitive(Class<?> clazz, Class<?> superclazz,
			Class<?>... typeParameters) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new SetSuperClassPrimitive(classContent, superclazz, typeParameters);
		return primitive;
	}

	/**
	 * Creates {@link RemoveSuperClassPrimitive}
	 * 
	 * @param clazz
	 *            Class to add interface to
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createRemoveSuperClassPrimitive(Class<?> clazz) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new RemoveSuperClassPrimitive(classContent);
		return primitive;
	}

	/**
	 * Creates {@link AddImportPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddImportPrimitive(Class<?> clazz, String[] imports)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new AddImportPrimitive(classContent, imports);
		return primitive;
	}

	/**
	 * Creates {@link SetImportPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createSetImportPrimitive(Class<?> clazz, String[] imports)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new SetImportPrimitive(classContent, imports);
		return primitive;
	}

	/**
	 * Creates {@link GetImportPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createGetImportPrimitive(Class<?> clazz) throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new GetImportPrimitive(classContent);
		return primitive;
	}

	/**
	 * Creates {@link createAddAnnotationToClassPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddAnnotationToClassPrimitive(Class<?> clazz, Class<?>[] annotations)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new AddAnnotationToClassPrimitive(clazz.getName(), classContent, annotations);
		return primitive;
	}

	/**
	 * Creates {@link createSetAnnotationToClassPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createSetAnnotationToClassPrimitive(Class<?> clazz, Class<?>[] annotations)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new SetAnnotationToClassPrimitive(clazz.getName(), classContent, annotations);
		return primitive;
	}

	/**
	 * Creates {@link createAddAnnotationToMethodPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddAnnotationToMethodPrimitive(Method met, Class<?>[] annotations)
			throws StructuralIntercessionException {
		String className = sourceCodeCache.getOriginalClassNameFromVersion(met.getDeclaringClass().getName());
		Class<?> declaringClass = null;
		try {
			declaringClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new StructuralIntercessionException(e.getMessage(), e.getCause());
		}
		ClassContent classContent = sourceCodeCache.getClassContent(declaringClass);

		Primitive primitive = new AddAnnotationToMethodPrimitive(met, classContent, annotations);
		return primitive;
	}

	/**
	 * Creates {@link createSetAnnotationToMethodPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createSetAnnotationToMethodPrimitive(Method met, Class<?>[] annotations)
			throws StructuralIntercessionException {
		String className = sourceCodeCache.getOriginalClassNameFromVersion(met.getDeclaringClass().getName());
		Class<?> declaringClass = null;
		try {
			declaringClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new StructuralIntercessionException(e.getMessage(), e.getCause());
		}
		ClassContent classContent = sourceCodeCache.getClassContent(declaringClass);

		Primitive primitive = new SetAnnotationToMethodPrimitive(met, classContent, annotations);
		return primitive;
	}

	/**
	 * Creates {@link createAddGenericTypeToClassPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddGenericTypeToClassPrimitive(Class<?> clazz, jmplib.reflect.TypeVariable<?>[] tvs)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new AddGenericTypeToClassPrimitive(clazz.getName(), classContent, tvs);
		return primitive;
	}

	/**
	 * Creates {@link createSetGenericTypeToClassPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createSetGenericTypeToClassPrimitive(Class<?> clazz, jmplib.reflect.TypeVariable<?>[] tvs)
			throws StructuralIntercessionException {
		ClassContent classContent = sourceCodeCache.getClassContent(clazz);

		Primitive primitive = new SetGenericTypeToClassPrimitive(clazz.getName(), classContent, tvs);
		return primitive;
	}

	/**
	 * Creates {@link createAddGenericTypeToMethodPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createAddGenericTypeToMethodPrimitive(Method met, jmplib.reflect.TypeVariable<?>[] tvs)
			throws StructuralIntercessionException {
		String className = sourceCodeCache.getOriginalClassNameFromVersion(met.getDeclaringClass().getName());
		Class<?> declaringClass = null;
		try {
			declaringClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new StructuralIntercessionException(e.getMessage(), e.getCause());
		}
		ClassContent classContent = sourceCodeCache.getClassContent(declaringClass);

		Primitive primitive = new AddGenericTypeToMethodPrimitive(met, classContent, tvs);
		return primitive;
	}

	/**
	 * Creates {@link createSetGenericTypeToMethodPrimitive}
	 * 
	 * @param clazz
	 * 
	 * @return The {@link Primitive} ready to be executed
	 * @throws StructuralIntercessionException
	 */
	public static Primitive createSetGenericTypeToMethodPrimitive(Method met, jmplib.reflect.TypeVariable<?>[] tvs)
			throws StructuralIntercessionException {
		String className = sourceCodeCache.getOriginalClassNameFromVersion(met.getDeclaringClass().getName());
		Class<?> declaringClass = null;
		try {
			declaringClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new StructuralIntercessionException(e.getMessage(), e.getCause());
		}
		ClassContent classContent = sourceCodeCache.getClassContent(declaringClass);

		Primitive primitive = new SetGenericTypeToMethodPrimitive(met, classContent, tvs);
		return primitive;
	}
}
