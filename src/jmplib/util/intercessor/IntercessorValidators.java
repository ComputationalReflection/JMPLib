package jmplib.util.intercessor;

import jmplib.annotations.ExcludeFromJMPLib;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * This class concentrates all the method to validate the parameters passed to
 * many of the JMPLib primitives
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class IntercessorValidators {
    /**
     * Throws an exception if the passed parameter value is null
     *
     * @param parameterName Name of the parameter to include in the exception message
     * @param obj           Value of the parameter
     */
    public static final void ensureParameterNotNull(String parameterName, Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("The " + parameterName + " parameter cannot be null");
    }

    /**
     * Check if a class can represent a generic type
     */
    public static final boolean canRepresentGenericType(Type clazz) {
        return clazz.getClass() == jmplib.reflect.TypeVariable.class;
    }

    /**
     * Checks if a name can be a valid identifier
     */
    public static final void checkValidIdentifier(String name) {
        try {
            Integer.parseInt(name.substring(0, 1));
            throw new IllegalArgumentException("Invalid identifier name: identifier names cannot start by a number");
        } catch (Exception ex) {

        }
    }

    /**
     * Check field parameters
     */
    public static final void checkFieldParams(int modifiers, String name) {
        if (name == null)
            throw new IllegalArgumentException("The name parameter cannot be null");
        if ("".equals(name))
            throw new IllegalArgumentException("The name parameter cannot be empty");

        checkValidIdentifier(name);
        int mask = Modifier.fieldModifiers();
        if ((modifiers | mask) != mask)
            throw new IllegalArgumentException("The modifier combination is incorrect for a field");
    }

    /**
     * Check field parameters
     */
    public static final void checkFieldParams(int modifiers, Type type, String name, String init) {
        checkFieldParams(modifiers, name);
        if (type == null)
            throw new IllegalArgumentException("The type parameter cannot be null");
        if (init != null && init == "")
            throw new IllegalArgumentException("The init parameter cannot be empty");
    }

    /**
     * Check method parameters
     */
    public static final void checkMethodParams(String name, int modifiers) throws IllegalArgumentException {
        if (name == null)
            throw new IllegalArgumentException("The name parameter cannot be null");
        if (name.length() == 0)
            throw new IllegalArgumentException("The name parameter cannot be empty");
        checkValidIdentifier(name);
        int mask = Modifier.methodModifiers();
        if ((modifiers | mask) != mask)
            throw new IllegalArgumentException("The modifier combination is incorrect for a method");
    }

    /**
     * Check method parameters
     */
    public static final void checkMethodParams(String name, String body, int modifiers)
            throws IllegalArgumentException {
        checkMethodParams(name, modifiers);
        if (body == null)
            throw new IllegalArgumentException("The body parameter cannot be null");
    }

    /**
     * Check method parameters
     */
    public static final void checkMethodParams(String name, MethodType methodType, int modifiers)
            throws IllegalArgumentException {
        checkMethodParams(name, modifiers);
        if (methodType == null)
            throw new IllegalArgumentException("The methodType parameter cannot be null");
        if (methodType.returnType() == null)
            throw new IllegalArgumentException("The returnType of the methodType parameter cannot be null");
        if (methodType.parameterArray() == null)
            throw new IllegalArgumentException("The parameterArray of the methodType parameter cannot be null");
    }

    /**
     * Check method parameters
     */
    public static final void checkMethodParams(String name, MethodType methodType, String body, int modifiers)
            throws IllegalArgumentException {
        checkMethodParams(name, body, modifiers);
        if (methodType == null)
            throw new IllegalArgumentException("The methodType parameter cannot be null");
        if (methodType.returnType() == null)
            throw new IllegalArgumentException("The returnType of the methodType parameter cannot be null");
        if (methodType.parameterArray() == null)
            throw new IllegalArgumentException("The parameterArray of the methodType parameter cannot be null");
    }

    /**
     * Check method parameters
     */
    public static final void checkMethodParams(String name, MethodType methodType, String body, int modifiers,
                                               Type[] exceptions) throws IllegalArgumentException {
        checkMethodParams(name, methodType, body, modifiers);
        java.lang.Class<?> temp;
        for (Type t : exceptions) {
            temp = IntercessorTypeConversion.type2JavaClass(t);
            if (!Exception.class.isAssignableFrom(temp))
                throw new IllegalArgumentException(
                        "The thrown exceptions of the method must be subclassses of the exception class");
        }
    }

    /**
     * Check parameters
     */
    public static void checkInvokerParams(Class<?> clazz, String name, Class<?> methodInterface, int modifiers)
            throws IllegalArgumentException {
        if (clazz == null)
            throw new IllegalArgumentException("The class parameter cannot be null");
        if (name == null)
            throw new IllegalArgumentException("The name parameter cannot be null");
        if (name.length() == 0)
            throw new IllegalArgumentException("The name parameter cannot be empty");
        checkValidIdentifier(name);
        if (methodInterface == null)
            throw new IllegalArgumentException("The methodInterface parameter cannot be null");
        int mask = Modifier.methodModifiers();
        if ((modifiers | mask) != mask)
            throw new IllegalArgumentException("The modifier combination is incorrect for a method");
    }

    /**
     * Check that the imports do not begin with "import" (removed) and do not end
     * with ";" (removed) to ensure proper code generation
     *
     * @param imports Imports to process
     */
    public static void checkAddImportParameters(String[] imports) {
        int counter = 0;
        for (String str : imports) {
            str = str.trim();
            if (str.startsWith("import")) {
                str = str.substring("import".length());
            }
            if (str.endsWith(";")) {
                str = str.substring(0, str.length() - 1);
            }
            imports[counter++] = str;
        }
    }

    /**
     * Check parameters of the addInterface/removeInterface primitives
     */
    @SuppressWarnings("rawtypes")
    public static void checkAddInterfaceParams(Class<?> clazz, Class<?> interf, Class<?>... typeParameters)
            throws IllegalArgumentException {
        if (clazz == null)
            throw new IllegalArgumentException("The class parameter cannot be null");
        if (interf == null)
            throw new IllegalArgumentException("The interf parameter cannot be null");

        if (!interf.isInterface())
            throw new IllegalArgumentException("The interf parameter must be an interface");

        TypeVariable[] tv = interf.getTypeParameters();

        if (typeParameters == null) {
            return;
        }
        if ((tv.length != typeParameters.length) && (typeParameters.length > 0)) {
            throw new IllegalArgumentException(
                    "Type parameters of the passed interface do not match with the expected type parameters");
        }
    }

    /**
     * Check parameters of the setSuperclass primitive
     */
    @SuppressWarnings("rawtypes")
    public static void checkSetSuperClassParams(Class<?> clazz, Class<?> superc, Type... typeParameters)
            throws IllegalArgumentException {
        if (clazz == null)
            throw new IllegalArgumentException("The class parameter cannot be null");
        if (superc == null)
            throw new IllegalArgumentException("The superclass parameter cannot be null");

        if (superc.isInterface())
            throw new IllegalArgumentException("The superclass parameter must not be an interface");

        if (superc.equals(clazz))
            throw new IllegalArgumentException("The superclass parameter cannot be the own class");

        Class<?> superClass = superc.getSuperclass();
        while (superClass != null) {
            if (superClass.equals(clazz))
                throw new IllegalArgumentException(
                        "Cannot change the inheritance tree: Class inheritance circularity detected.");
            superClass = superClass.getSuperclass();
        }

        if (Modifier.isFinal(superc.getModifiers()))
            throw new IllegalArgumentException("The superclass parameter cannot be final");

        TypeVariable[] tv = superc.getTypeParameters();

        if (typeParameters == null) {
            return;
        }
        if ((tv.length != typeParameters.length) && (typeParameters.length > 0)) {
            throw new IllegalArgumentException(
                    "Type parameters of the passed superclass do not match with the expected type parameters");
        }
    }

    /**
     * Check parameters of the removeSuperclass primitive
     */
    // @SuppressWarnings("rawtypes")
    public static void checkRemoveSuperClassParams(Class<?> clazz) throws IllegalArgumentException {
        if (clazz == null)
            throw new IllegalArgumentException("The class parameter cannot be null");
        if (clazz.equals(Object.class))
            throw new IllegalArgumentException("Cannot remove the superclass  of the Object class");
        if (clazz.getSuperclass().equals(Object.class))
            throw new IllegalArgumentException("The class parameter cannot derive from the Object class");
    }

    /**
     * Checks that the passed class to add or remove annotations are really
     * annotations.
     *
     * @param annotations
     */
    public static void checkAddAnnotationToClassParameters(Class<?>[] annotations) {
        for (Class<?> cl : annotations) {
            if (cl == null)
                throw new IllegalArgumentException("The class parameter cannot be null");
            if (!cl.isAnnotation())
                throw new IllegalArgumentException("The class parameter must inherit from the Annotation class");
        }

    }

    /**
     * Checks that the passed type variables are valid
     *
     * @param tvs
     */
    public static void checkAddGenericTypeParameters(jmplib.reflect.TypeVariable<?>[] tvs) {
        for (jmplib.reflect.TypeVariable<?> cl : tvs) {
            if (cl == null)
                throw new IllegalArgumentException("The passed type variables cannot be null");
            if (cl.getName().equals(""))
                throw new IllegalArgumentException("The type variable name cannot be empty");
        }

    }
}
