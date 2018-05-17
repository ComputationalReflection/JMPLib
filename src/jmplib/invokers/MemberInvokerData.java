package jmplib.invokers;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import jmplib.annotations.ExcludeFromJMPLib;

/**
 * Class to provide necessary data when obtaining invokers of dynamically added members
 * @author redon
 *
 * @param <T>
 */
@ExcludeFromJMPLib
public class MemberInvokerData<T> extends InvokerData<T> {
	private boolean isStatic;
	private int modifiers;

	/**
	 * 
	 * @param functionalInterface
	 */
	public MemberInvokerData(Class<T> functionalInterface) {
		this(functionalInterface, false, 0, (Type[]) null);
	}

	/**
	 * 
	 * @param functionalInterface
	 * @param isStatic
	 */
	public MemberInvokerData(Class<T> functionalInterface, boolean isStatic) {
		this(functionalInterface, isStatic, 0, (Type[]) null);
	}

	/**
	 * 
	 * @param functionalInterface
	 * @param isStatic
	 * @param modifiers
	 */
	public MemberInvokerData(Class<T> functionalInterface, boolean isStatic, int modifiers) {
		this(functionalInterface, isStatic, modifiers, (Type[]) null);
	}

	/**
	 * 
	 * @param functionalInterface
	 * @param parametrizationClasses
	 */
	public MemberInvokerData(Class<T> functionalInterface, Type... parametrizationClasses) {
		this(functionalInterface, false, Modifier.PUBLIC, parametrizationClasses);
	}

	/**
	 * 
	 * @param functionalInterface
	 * @param modifiers
	 * @param parametrizationClasses
	 */
	public MemberInvokerData(Class<T> functionalInterface, int modifiers, Type... parametrizationClasses) {
		this(functionalInterface, false, modifiers, parametrizationClasses);
	}

	/**
	 * 
	 * @param functionalInterface
	 * @param isStatic
	 * @param modifiers
	 * @param parametrizationClasses
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
