package jmplib;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Map;

import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Class;
import jmplib.reflect.TypeVariable;

/**
 * An intercessor of classes. This class is the main façade of JMPlib and
 * provides all the primitive support (add method, replace method, delete
 * method...) and the methods that allow to create invokers for the new members.
 * 
 * @author Ignacio Lagartos, Jose Manuel Redondo
 * @version 1.3 (Redondo) Duplicate code with IntercessorTransaction moved to
 *          IntercessorUtils. Added many new primitives to manipulate imports,
 *          annotations and generic types. This intercessor and the
 *          transactional one now implement the same interface. This intercessor
 *          uses the transactional one to perform their operations, so the code
 *          is no longer duplicated.
 */
public class DefaultIntercessor implements IIntercessor {

	private static IIntercessor _instance = new DefaultIntercessor();

	private IIntercessor transactionalint = TransactionalIntercessor.getInstance();

	/***************************************
	 * INSTANCE CREATION
	 **************************************/

	/**
	 * Creates an instance of this Intercessor. This intercessor is a singleton
	 * 
	 * @return
	 */
	public static IIntercessor getInstance() {
		return _instance;
	}

	/***************************************
	 * FIELDS
	 **************************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addField(Type clazz, Member... fields) throws StructuralIntercessionException {
		transactionalint.addField(clazz, fields);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceField(Type clazz, Member... fields) throws StructuralIntercessionException {
		transactionalint.replaceField(clazz, fields);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeField(Type clazz, Member... fields) throws StructuralIntercessionException {
		transactionalint.removeField(clazz, fields);
		transactionalint.commit();
	}

	/***************************************
	 * METHODS
	 **************************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMethod(Type clazz, AccessibleObject... methods) throws StructuralIntercessionException {
		transactionalint.addMethod(clazz, methods);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceMethod(Type clazz, AccessibleObject method, AccessibleObject newMethod)
			throws StructuralIntercessionException {
		transactionalint.replaceMethod(clazz, method, newMethod);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceMethod(Type clazz, Map<AccessibleObject, AccessibleObject> methods)
			throws StructuralIntercessionException {
		transactionalint.replaceMethod(clazz, methods);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceImplementation(Type clazz, AccessibleObject... methods) throws StructuralIntercessionException {
		transactionalint.replaceImplementation(clazz, methods);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMethod(Type clazz, AccessibleObject... methods) throws StructuralIntercessionException {
		transactionalint.removeMethod(clazz, methods);
		transactionalint.commit();
	}

	/***************************************
	 * CLASS
	 **************************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPublicInterfaceOf(Type origin, Type destination) throws StructuralIntercessionException {
		transactionalint.addPublicInterfaceOf(origin, destination);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> createClassClone(String packageName, String className, Type origin)
			throws StructuralIntercessionException {

		return transactionalint.createClassClone(packageName, className, origin);
	}

	/***************************************
	 * DYNAMIC INHERITANCE
	 **************************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInterface(Type clazz, Type interf, Type... typeParameters) throws StructuralIntercessionException {
		transactionalint.addInterface(clazz, interf, typeParameters);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInterface(Type clazz, Map<Type, Type[]> interfs) throws StructuralIntercessionException {
		transactionalint.addInterface(clazz, interfs);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeInterface(Type clazz, Type interf) throws StructuralIntercessionException {
		transactionalint.removeInterface(clazz, interf);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void implementInterface(Type clazz, Type interf, AccessibleObject[] methods,
			java.lang.Class<?>... typeParameters) throws StructuralIntercessionException {
		transactionalint.implementInterface(clazz, interf, methods, typeParameters);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSuperclass(Type clazz, Type superclazz, Type... typeParameters)
			throws StructuralIntercessionException {
		transactionalint.setSuperclass(clazz, superclazz, typeParameters);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSuperclass(Type clazz) throws StructuralIntercessionException {
		transactionalint.removeSuperclass(clazz);
		transactionalint.commit();
	}

	/***************************************
	 * IMPORTS
	 **************************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImport(Type clazz, AnnotatedElement... imports) throws StructuralIntercessionException {
		transactionalint.addImport(clazz, imports);
		transactionalint.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnnotatedElement[] getImports(Type clazz) throws StructuralIntercessionException {
		return transactionalint.getImports(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setImports(Type clazz, AnnotatedElement... imports) throws StructuralIntercessionException {
		transactionalint.setImports(clazz, imports);
		transactionalint.commit();
	}

	/***************************************
	 * ANNOTATIONS
	 **************************************/

	@Override
	public void addAnnotation(Type clazz, Type... annotations) throws StructuralIntercessionException {
		transactionalint.addAnnotation(clazz, annotations);
		transactionalint.commit();

	}

	@Override
	public void setAnnotation(Type clazz, Type... annotations) throws StructuralIntercessionException {
		transactionalint.setAnnotation(clazz, annotations);
		transactionalint.commit();

	}

	@Override
	public void addAnnotation(AccessibleObject met, Type... annotations) throws StructuralIntercessionException {
		transactionalint.addAnnotation(met, annotations);
		transactionalint.commit();

	}

	@Override
	public void setAnnotation(AccessibleObject met, Type... annotations) throws StructuralIntercessionException {
		transactionalint.addAnnotation(met, annotations);
		transactionalint.commit();

	}

	@Override
	public void addGenericType(Type clazz, TypeVariable<?>... types) throws StructuralIntercessionException {
		transactionalint.addGenericType(clazz, types);
		transactionalint.commit();
	}

	@Override
	public void setGenericType(Type clazz, TypeVariable<?>... types) throws StructuralIntercessionException {
		transactionalint.setGenericType(clazz, types);
		transactionalint.commit();
	}

	@Override
	public void addGenericType(AccessibleObject met, TypeVariable<?>... types) throws StructuralIntercessionException {
		transactionalint.addGenericType(met, types);
		transactionalint.commit();
	}

	@Override
	public void setGenericType(AccessibleObject met, TypeVariable<?>... types) throws StructuralIntercessionException {
		transactionalint.setGenericType(met, types);
		transactionalint.commit();
	}

}
