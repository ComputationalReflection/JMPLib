package jmplib.invokers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.util.intercessor.IntercessorValidators;

/**
 * Base class to all the data passed to any of the methods that obtain invokers
 * to dynamically added members
 *
 * @param <T>
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class InvokerData<T> {
    private Class<T> functionalInterface;

    private Type[] parametrizationClasses;
    private static boolean enforceFunctionalInterface = false;

    /**
     * @return the enforceFunctionalInterface
     */
    public static boolean isEnforceFunctionalInterface() {
        return enforceFunctionalInterface;
    }

    /**
     * @param enforceFunctionalInterface the enforceFunctionalInterface to set
     */
    public static void setEnforceFunctionalInterface(boolean enforceFunctionalInterface) {
        InvokerData.enforceFunctionalInterface = enforceFunctionalInterface;
    }

    /**
     * @param functionalInterface Description of the wrapped member
     */
    public InvokerData(Class<T> functionalInterface) {
        IntercessorValidators.ensureParameterNotNull("functionalInterface", functionalInterface);

        // Check that the parameter is really a functional interface
        Annotation[] annotations = functionalInterface.getAnnotations();
        if (InvokerData.enforceFunctionalInterface) {
            boolean isFunctionalInterface = false;
            for (Annotation a : annotations) {

                if (a.annotationType() == FunctionalInterface.class) {
                    isFunctionalInterface = true;
                    break;
                }
            }
            if (!isFunctionalInterface)
                throw new IllegalArgumentException("The first param must be a functional interface");
        }
        this.functionalInterface = functionalInterface;
        this.parametrizationClasses = new Class[0];
    }

    /**
     * @param functionalInterface Description of the wrapped member
     * @param parametrizationClasses Generic types of the wrapped member
     */
    InvokerData(Class<T> functionalInterface, Type... parametrizationClasses) {
        this(functionalInterface);
        if (parametrizationClasses == null)
            parametrizationClasses = new Class[0];
        this.parametrizationClasses = parametrizationClasses;
    }

    /**
     * @return the functionalInterface
     */
    public Class<T> getFunctionalInterface() {
        return functionalInterface;
    }

    /**
     * @return the parametrizationClasses
     */
    public Type[] getParametrizationClasses() {
        return parametrizationClasses;
    }
}