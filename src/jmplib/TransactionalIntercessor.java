package jmplib;

import jmplib.config.JMPlibConfig;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.primitives.*;
import jmplib.reflect.Class;
import jmplib.reflect.IntrospectionUtils;
import jmplib.reflect.Introspector;
import jmplib.reflect.TypeVariable;
import jmplib.util.intercessor.IntercessorTypeConversion;
import jmplib.util.intercessor.IntercessorValidators;

import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * An intercessor of classes. This class is the fa�ade of JMPlib and provides
 * all the primitive support (add method, replace method, delete method...) and
 * the methods that allow to create invokers for the new members. Contrary to
 * {@link SimpleIntercessor}, this class supports the execution of several primitives
 * simultaneously as a transaction.
 *
 * @author Ignacio Lagartos, Jose Manuel Redondo
 * @version 1.3 (Redondo) Duplicate code with Intercessor moved to
 * IntercessorUtils. Added many new services, integrating the
 * jmplib.reflect classes and also new primitives (import and
 * annotation manipulation, instance "type change"). Once commited,
 * this intercessor is able to process a new set of primitives.
 */
public class TransactionalIntercessor implements IIntercessor {

    protected final Queue<Primitive> primitives;

    /* **************************************
     * INSTANCE CREATION
     **************************************/

    public TransactionalIntercessor() {
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            primitives = new ConcurrentLinkedQueue<>();
        } else primitives = new LinkedList<>();
    }

    /**
     * {@inheritDoc}
     */
    public IIntercessor createIntercessor() {
        return new TransactionalIntercessor();
    }


    /* **************************************
     * FIELDS
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void addField(Type clazz, jmplib.reflect.Field... fields) throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        try {
            java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
            Primitive primitive;

            for (jmplib.reflect.Field ftemp : fields) {
                // Creating the primitive
                primitive = PrimitiveFactory.createAddFieldPrimitive(cl, ftemp.getModifiers(), ftemp.getType(),
                        ftemp.getName(), ftemp.getInit());
                primitives.add(primitive);
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "addField could not be executed due to the following reasons: " + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceField(Type clazz, jmplib.reflect.Field... fields) throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        try {
            java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
            Primitive primitive;

            for (jmplib.reflect.Field ftemp : fields) {
                // Creating the primitive
                primitive = PrimitiveFactory.createReplaceFieldPrimitive(cl, ftemp.getName(), ftemp.getType(),
                        ftemp.getInit());
                primitives.add(primitive);
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "replaceField could not be executed due to the following reasons: " + ex.getMessage(),
                    ex.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeField(Type clazz, jmplib.reflect.Field... fields) throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        try {
            java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
            Primitive primitive;

            for (jmplib.reflect.Field ftemp : fields) {
                // Creating the primitive
                primitive = PrimitiveFactory.createDeleteFieldPrimitive(cl, ftemp.getName());
                primitives.add(primitive);
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "deleteField could not be executed due to the following reasons: " + ex.getMessage(),
                    ex.getCause());
        }
    }

    /* **************************************
     * METHODS
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMethod(Type clazz, jmplib.reflect.Method... methods) throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
        Primitive primitive;

        try {
            for (jmplib.reflect.Method mtemp : methods) {
                String[] params;

                if (mtemp.getParameterNames() == null)
                    params = new String[0];
                else
                    params = mtemp.getParameterNames();

                if (!mtemp.isGeneric()) {
                    // Creating the primitive
                    primitive = PrimitiveFactory.createAddMethodPrimitive(cl, mtemp.getName(), mtemp.getMethodType(),
                            params, mtemp.getSourceCode(), mtemp.getModifiers(), mtemp.getExceptionTypes());
                } else {
                    // Creating the primitive
                    primitive = PrimitiveFactory.createAddMethodPrimitive(cl, mtemp.getName(), mtemp.getMethodType(),
                            params, mtemp.getSourceCode(), mtemp.getModifiers(), mtemp.getGenericParameterTypes(),
                            mtemp.getGenericReturnType(), mtemp.getMethodTypeParameters(), mtemp.getExceptionTypes());
                }

                primitives.add(primitive);
                //addPrimitive(primitive);
/*                if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
                    synchronized (primitives) {

                        primitives.add(primitive);
                    }
                }
                else primitives.add(primitive);*/
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "addMethod could not be executed due to the following reasons: " + ex.getMessage(), ex.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceMethod(Type clazz, jmplib.reflect.Method method, jmplib.reflect.Method newMethod)
            throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
        Primitive primitive;

        try {
            // Creating the primitive
            primitive = PrimitiveFactory.createReplaceMethodPrimitive(cl, method.getName(), method.getMethodType(),
                    newMethod.getMethodType(), newMethod.getSourceCode());
            primitives.add(primitive);
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "replaceMethod could not be executed due to the following reasons: " + ex.getMessage(),
                    ex.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceMethod(Type clazz, Map<jmplib.reflect.Method, jmplib.reflect.Method> methods)
            throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);

        try {
            for (jmplib.reflect.Method ao : methods.keySet()) {
                replaceMethod(clazz, ao, methods.get(ao));
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "replaceMethod could not be executed due to the following reasons: " + ex.getMessage(),
                    ex.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceImplementation(Type clazz, jmplib.reflect.Method... methods)
            throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
        Primitive primitive;

        try {
            for (jmplib.reflect.Method mtemp : methods) {
                // Creating the primitive
                if (mtemp.getMethodType() == null)
                    primitive = PrimitiveFactory.createReplaceImplementationPrimitive(cl, mtemp.getName(),
                            mtemp.getSourceCode());
                else
                    primitive = PrimitiveFactory.createReplaceImplementationPrimitive(cl, mtemp.getName(),
                            mtemp.getMethodType(), mtemp.getSourceCode());
                primitives.add(primitive);
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "replaceImplementation could not be executed due to the following reasons: " + ex.getMessage(),
                    ex.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMethod(Type clazz, jmplib.reflect.Method... methods) throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("class", clazz);
        java.lang.Class<?> cl = IntercessorTypeConversion.type2JavaClass(clazz);
        Primitive primitive;
        MethodType mt;

        try {
            for (jmplib.reflect.Method mtemp : methods) {
                mt = mtemp.getMethodType();
                // Creating the primitive
                if (mt == null)
                    primitive = PrimitiveFactory.createDeleteMethodPrimitive(cl, mtemp.getName());
                else
                    primitive = PrimitiveFactory.createDeleteMethodPrimitive(cl, mtemp.getName(), mt);
                primitives.add(primitive);
            }
        } catch (Exception ex) {
            throw new StructuralIntercessionException(
                    "removeMethod could not be executed due to the following reasons: " + ex.getMessage(),
                    ex.getCause());
        }
    }

    /* **************************************
     * CLASS
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPublicInterfaceOf(Type origin, Type destination) throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("origin", origin);
        IntercessorValidators.ensureParameterNotNull("destination", destination);
        Class<?> tempOrigin = IntercessorTypeConversion.type2Class(origin);

        jmplib.reflect.Field[] fields = IntrospectionUtils.filterJMPLibFields(tempOrigin.getFields());
        jmplib.reflect.Method[] methods = IntrospectionUtils.filterJMPLibMethods(tempOrigin.getMethods());

        Stream<jmplib.reflect.Method> matchedMets = Arrays.stream(methods)
                .filter(m -> m.getDeclaringClass() != Object.class);

        Stream<jmplib.reflect.Method> matchedMets2 = matchedMets.filter(m -> !m.getName().equals("toString")
                && !m.getName().equals("equals") && !m.getName().equals("hashCode"));

        jmplib.reflect.Method[] finalMatchedMets = matchedMets2.filter(m -> m.getModifiers() == Modifier.PUBLIC)
                .toArray(size -> new jmplib.reflect.Method[size]);

        addField(destination, fields);
        addMethod(destination, finalMatchedMets);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This operation works independently from the current transaction and does not
     * commit it or modify its pending operations.
     */
    @Override
    public Class<?> createClassClone(String packageName, String className, Type origin)
            throws StructuralIntercessionException {
        IntercessorValidators.ensureParameterNotNull("origin", origin);

        IntercessorValidators.checkValidIdentifier(packageName);
        IntercessorValidators.checkValidIdentifier(className);
        java.lang.Class<?> newClass = new SimpleEvaluator().createEvaluator().createEmptyClass(packageName, className,
                getImports(origin));

        TransactionalIntercessor tempti = new TransactionalIntercessor();
        tempti.addPublicInterfaceOf(origin, newClass);
        tempti.commit();
        return Introspector.decorateClass(newClass);
    }

    /* **************************************
     * DYNAMIC INHERITANCE
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInterface(Type clazz, Type interf, Type... typeParameters) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            IntercessorValidators.ensureParameterNotNull("interf", interf);
            java.lang.Class<?> tempClazz = IntercessorTypeConversion.type2JavaClass(clazz);
            java.lang.Class<?> tempInterf = IntercessorTypeConversion.type2JavaClass(interf);
            java.lang.Class<?>[] clTypeParams = IntercessorTypeConversion.type2JavaClass(typeParameters);

            // Checking params
            IntercessorValidators.checkAddInterfaceParams(tempClazz, tempInterf, clTypeParams);
            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createAddInterfacePrimitive(tempClazz, tempInterf, clTypeParams);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "addInterface could not be executed due to the following reasons: " + e.getMessage(), e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInterface(Type clazz, Map<Type, Type[]> interfs) throws StructuralIntercessionException {
        for (Type t : interfs.keySet()) {
            addInterface(clazz, t, interfs.get(t));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeInterface(Type clazz, Type interf) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            IntercessorValidators.ensureParameterNotNull("interf", interf);
            java.lang.Class<?> tempClazz = IntercessorTypeConversion.type2JavaClass(clazz);
            java.lang.Class<?> tempInterf = IntercessorTypeConversion.type2JavaClass(interf);

            // Checking params
            IntercessorValidators.checkAddInterfaceParams(tempClazz, tempInterf);
            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createRemoveInterfacePrimitive(tempClazz, tempInterf);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "removeInterface could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void implementInterface(Type clazz, Type interf, jmplib.reflect.Method[] methods,
                                   java.lang.Class<?>... typeParameters) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            IntercessorValidators.ensureParameterNotNull("interf", interf);
            java.lang.Class<?> tempClazz = IntercessorTypeConversion.type2JavaClass(clazz);
            java.lang.Class<?> tempInterf = IntercessorTypeConversion.type2JavaClass(interf);
            java.lang.Class<?>[] clTypeParams = IntercessorTypeConversion.type2JavaClass(typeParameters);

            // Checking params
            IntercessorValidators.checkAddInterfaceParams(tempClazz, tempInterf, clTypeParams);

            addMethod(clazz, methods);
            addInterface(clazz, interf, typeParameters);

        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "implementInterface could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuperclass(Type clazz, Type superclazz, Type... typeParameters)
            throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            IntercessorValidators.ensureParameterNotNull("superclass", superclazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            java.lang.Class<?> jsuperclazz = IntercessorTypeConversion.type2JavaClass(superclazz);
            java.lang.Class<?>[] jtypeparams = IntercessorTypeConversion.type2JavaClass(typeParameters);
            // Checking params
            IntercessorValidators.checkSetSuperClassParams(jclazz, jsuperclazz, typeParameters);
            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createSetSuperClassPrimitive(jclazz, jsuperclazz, jtypeparams);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setSuperclass could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSuperclass(Type clazz) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);

            // Checking params
            IntercessorValidators.checkRemoveSuperClassParams(jclazz);
            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createRemoveSuperClassPrimitive(jclazz);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "removeSuperclass could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /* **************************************
     * IMPORTS
     **************************************/

    /**
     * {@inheritDoc}
     */
    private void __addImport(Type clazz, AnnotatedElement... importObjects) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            String[] imports = IntercessorTypeConversion.getImportString(importObjects);
            IntercessorValidators.checkAddImportParameters(imports);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createAddImportPrimitive(jclazz, imports);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "addImport could not be executed due to the following reasons: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addImport(Type clazz, jmplib.reflect.Class<?>... importObjects) throws StructuralIntercessionException {
        __addImport(clazz, importObjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addImport(Type clazz, java.lang.Package... importObjects) throws StructuralIntercessionException {
        __addImport(clazz, importObjects);
    }

    /**
     * {@inheritDoc} This operation works independently from the current transaction
     * and does not commit it or modify its pending operations.
     */
    @SuppressWarnings("unchecked")
    @Override
    public AnnotatedElement[] getImports(Type clazz) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);

            // Creating the primitive
            AbstractReadPrimitive<String[]> primitive = (AbstractReadPrimitive<String[]>) PrimitiveFactory
                    .createGetImportPrimitive(jclazz);
            // Executing the primitive
            PrimitiveExecutor executor = new PrimitiveExecutor(primitive);
            executor.executePrimitives();
            return IntercessorTypeConversion.getImportAnnotatedElementsFromString(primitive.getReadValue());
        } catch (StructuralIntercessionException | ClassNotFoundException e) {
            throw new StructuralIntercessionException(
                    "getImport could not be executed due to the following reasons: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImports(Type clazz, AnnotatedElement... importObjects)
            throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            String[] imports = IntercessorTypeConversion.getImportString(importObjects);

            IntercessorValidators.checkAddImportParameters(imports);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createSetImportPrimitive(jclazz, imports);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setImport could not be executed due to the following reasons: " + e.getMessage(), e.getCause());
        }

    }

    /* **************************************
     * ANNOTATIONS
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnnotation(Type clazz, Type... annotationObjects) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            java.lang.Class<?>[] annotations = IntercessorTypeConversion.type2JavaClass(annotationObjects);
            IntercessorValidators.checkAddAnnotationToClassParameters(annotations);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createAddAnnotationToClassPrimitive(jclazz, annotations);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "addAnnotation(Class) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnnotation(Type clazz, Type... annotationObjects) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            java.lang.Class<?>[] annotations = IntercessorTypeConversion.type2JavaClass(annotationObjects);
            IntercessorValidators.checkAddAnnotationToClassParameters(annotations);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createSetAnnotationToClassPrimitive(jclazz, annotations);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setAnnotation(Class) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnnotation(jmplib.reflect.Method method, Type... annotationObjects)
            throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("method", method);
            java.lang.Class<?>[] annotations = IntercessorTypeConversion.type2JavaClass(annotationObjects);
            IntercessorValidators.checkAddAnnotationToClassParameters(annotations);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createAddAnnotationToMethodPrimitive(method.getDecoratedMethod(),
                    annotations);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "addAnnotation(Method) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnnotation(jmplib.reflect.Method method, Type... annotationObjects)
            throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("method", method);
            java.lang.Class<?>[] annotations = IntercessorTypeConversion.type2JavaClass(annotationObjects);
            IntercessorValidators.checkAddAnnotationToClassParameters(annotations);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createSetAnnotationToMethodPrimitive(method.getDecoratedMethod(),
                    annotations);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setAnnotation(Method) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }
    }

    /* *************************************
     * GENERIC TYPES
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void addGenericType(Type clazz, TypeVariable<?>... types) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            IntercessorValidators.checkAddGenericTypeParameters(types);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createAddGenericTypeToClassPrimitive(jclazz, types);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "addGenericType(Class) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGenericType(Type clazz, TypeVariable<?>... types) throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("class", clazz);
            java.lang.Class<?> jclazz = IntercessorTypeConversion.type2JavaClass(clazz);
            IntercessorValidators.checkAddGenericTypeParameters(types);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createSetGenericTypeToClassPrimitive(jclazz, types);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setGenericType(Class) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addGenericType(jmplib.reflect.Method method, TypeVariable<?>... types)
            throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("method", method);
            IntercessorValidators.checkAddGenericTypeParameters(types);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createAddGenericTypeToMethodPrimitive(method.getDecoratedMethod(),
                    types);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "addGenericType(Method) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGenericType(jmplib.reflect.Method method, TypeVariable<?>... types)
            throws StructuralIntercessionException {
        try {
            IntercessorValidators.ensureParameterNotNull("method", method);
            IntercessorValidators.checkAddGenericTypeParameters(types);

            // Creating the primitive
            Primitive primitive = PrimitiveFactory.createSetGenericTypeToMethodPrimitive(method.getDecoratedMethod(),
                    types);
            primitives.add(primitive);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setGenericType(Method) could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }

    }

    /* *************************************
     * COMMIT
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws StructuralIntercessionException {
        try {
            PrimitiveExecutor executor;
            if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
                synchronized (primitives) {
                    if (primitives.size() == 0)
                        return;
                    executor = new ThreadSafePrimitiveExecutor(primitives);
                    executor.executePrimitives();
                }
            } else {
                executor = new PrimitiveExecutor(primitives);
                executor.executePrimitives();
            }

        } finally {
            primitives.clear();
        }
    }
}
