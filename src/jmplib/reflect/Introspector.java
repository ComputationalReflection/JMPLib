package jmplib.reflect;

import java.util.HashMap;

/**
 * The introspector is a class that implement decorators of the java reflection
 * system to enable users to see added members through introspection primitives
 * together with those that were added at compile time.
 *
 * @author redon
 */
public class Introspector {
    /**
     * Avoid create class object more than one time, reflecting the behavior of the
     * java.lang.Class class.
     */
    private static HashMap<java.lang.Class<?>, jmplib.reflect.Class<?>> cache = new HashMap<java.lang.Class<?>, jmplib.reflect.Class<?>>();

    /**
     * Obtain a class decorator of the class of the passed object.
     *
     * @param obj
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static jmplib.reflect.Class<?> getClass(Object obj) {
        java.lang.Class<?> clazz = obj.getClass();
        jmplib.reflect.Class<?> cached = cache.get(clazz);
        if (cached == null) {
            jmplib.reflect.Class decorator = new jmplib.reflect.Class(obj.getClass());
            cache.put(clazz, decorator);
            return decorator;
        }
        return cached;
    }

    /**
     * Puts a decorator to a java.lang.Class instance
     *
     * @param clazz A java.lang.Class object
     * @return The decorated java.lang.reflect.Class
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static jmplib.reflect.Class<?> decorateClass(java.lang.Class clazz) {
        jmplib.reflect.Class<?> cached = cache.get(clazz);
        if (cached == null) {
            jmplib.reflect.Class decorator = new jmplib.reflect.Class(clazz);
            cache.put(clazz, decorator);
            return decorator;
        }
        return cached;
    }

    /**
     * Puts a decorator to a java.lang.reflect.Method instance
     *
     * @param method A java.lang.Method object
     * @return The decorated java.lang.reflect.Method
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static jmplib.reflect.Method decorateMethod(java.lang.reflect.Method method)
            throws NoSuchMethodException, SecurityException {
        jmplib.reflect.Class<?> clazz = decorateClass(method.getDeclaringClass());
        return clazz.getMethod(method.getName(), method.getParameterTypes());
    }

    /**
     * Puts a decorator to a java.lang.reflect.Method array
     *
     * @param methods A java.lang.Method array
     * @return The decorated java.lang.reflect.Method array
     */
    public static jmplib.reflect.Method[] decorateMethods(java.lang.reflect.Method[] methods) {
        return IntrospectionUtils.decorateMethodList(methods);
    }

    /**
     * Puts a decorator to a java.lang.reflect.Field instance
     *
     * @param field A java.lang.reflect.Field object
     * @return The decorated java.lang.reflect.Field
     * @throws NoSuchFieldException
     */
    public static jmplib.reflect.Field decorateField(java.lang.reflect.Field field) throws NoSuchFieldException {
        jmplib.reflect.Class<?> clazz = decorateClass(field.getDeclaringClass());
        return clazz.getField(field.getName());
    }

    /**
     * Puts a decorator to a java.lang.reflect.Field array
     *
     * @param fields A java.lang.reflect.Field array
     * @return The decorated java.lang.reflect.Field array
     */
    public static jmplib.reflect.Field[] decorateFields(java.lang.reflect.Field[] fields) throws NoSuchFieldException {
        return IntrospectionUtils.decorateFieldList(fields);
    }

    /**
     * Method equivalent to perform an instanceof
     * @param obj object to check
     * @param clazz Class to compare the object with
     * @return If the object is an instance of the class or not
     */
    public static boolean instanceOf(Object obj, java.lang.Class clazz) {
        return decorateClass(clazz).isAssignableFrom(obj.getClass());
    }

    /**
     * Cast obj to castTarget
     * @param castTarget Class to cast to
     * @param obj Object to cast
     * @return Casted object
     */
    public static Object cast (java.lang.Class castTarget, Object obj) {
        return decorateClass(castTarget).cast(obj);
    }
}
