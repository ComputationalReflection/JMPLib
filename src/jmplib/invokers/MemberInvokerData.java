package jmplib.invokers;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import jmplib.annotations.ExcludeFromJMPLib;

/**
 * Class to provide necessary data when obtaining invokers of dynamically added members
 *
 * @param <T>
 * @author redon
 */
@ExcludeFromJMPLib
public class MemberInvokerData<T> extends InvokerData<T> {
    private boolean isStatic;
    private int modifiers;

    /**
     * @inheritDoc
     */
    public MemberInvokerData(Class<T> functionalInterface) {
        this(functionalInterface, false, 0, (Type[]) null);
    }

    /**
     * @param functionalInterface Description of the wrapped member
     * @param isStatic            Determines if the wrapped member is static
     */
    public MemberInvokerData(Class<T> functionalInterface, boolean isStatic) {
        this(functionalInterface, isStatic, 0, (Type[]) null);
    }

    /**
     * @param functionalInterface Description of the wrapped member
     * @param isStatic            Determines if the wrapped member is static
     * @param modifiers           Access modifiers of the memeber
     */
    public MemberInvokerData(Class<T> functionalInterface, boolean isStatic, int modifiers) {
        this(functionalInterface, isStatic, modifiers, (Type[]) null);
    }

    /**
     * @param functionalInterface    Description of the wrapped member
     * @param parametrizationClasses Generic types of the wrapped member
     */
    public MemberInvokerData(Class<T> functionalInterface, Type... parametrizationClasses) {
        this(functionalInterface, false, Modifier.PUBLIC, parametrizationClasses);
    }

    /**
     * @param functionalInterface    Description of the wrapped member
     * @param modifiers              Access modifiers of the memeber
     * @param parametrizationClasses Generic types of the wrapped member
     */
    public MemberInvokerData(Class<T> functionalInterface, int modifiers, Type... parametrizationClasses) {
        this(functionalInterface, false, modifiers, parametrizationClasses);
    }

    /**
     * @param functionalInterface    Description of the wrapped member
     * @param isStatic               Determines if the wrapped member is static
     * @param modifiers              Access modifiers of the memeber
     * @param parametrizationClasses Generic types of the wrapped member
     */
    public MemberInvokerData(Class<T> functionalInterface, boolean isStatic, int modifiers, Type... parametrizationClasses) {
        super(functionalInterface, parametrizationClasses);
        this.isStatic = isStatic;
        this.modifiers = modifiers;
    }

    /**
     * @return the isStatic
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * @return the modifiers
     */
    public int getModifiers() {
        return modifiers;
    }

}
