package jmplib.invokers;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

import jmplib.annotations.ExcludeFromJMPLib;

/**
 * Class to provide necessary data when obtaining invokers of dynamically
 * evaluated code
 * 
 * @author redon
 *
 * @param <T>
 */
@ExcludeFromJMPLib
public class EvalInvokerData<T> extends InvokerData<T> {
	private String[] paramNames;
	private AnnotatedElement[] imports;
	private Map<String, Class<?>> environment;

	/**
	 * 
	 * @param code
	 * @param functionalInterface
	 * @param paramNames
	 */
	public EvalInvokerData(Class<T> functionalInterface, String... paramNames) {
		this(functionalInterface, null, paramNames);
	}

	/**
	 * 
	 * @param code
	 * @param functionalInterface
	 * @param paramNames
	 * @param parametrizationClasses
	 */
	public EvalInvokerData(Class<T> functionalInterface, String[] paramNames,
			Class<?>... parametrizationClasses) {
		this(functionalInterface, paramNames, null, parametrizationClasses);
	}

	/**
	 * 
	 * @param code
	 * @param functionalInterface
	 * @param paramNames
	 * @param imports
	 * @param parametrizationClasses
	 */
	public EvalInvokerData(Class<T> functionalInterface, String[] paramNames, AnnotatedElement[] imports,
			Class<?>... parametrizationClasses) {
		super(functionalInterface, parametrizationClasses);
		this.paramNames = paramNames;
		this.imports = imports;
	}

	/**
	 * 
	 * @param code
	 * @param functionalInterface
	 * @param environment
	 * @param paramNames
	 */
	public EvalInvokerData(Class<T> functionalInterface, Map<String, Class<?>> environment,
			String... paramNames) {
		super(functionalInterface);
		this.paramNames = paramNames;
		this.environment = environment;
	}

	/**
	 * @return the paramNames
	 */
	public String[] getParamNames() {
		return paramNames;
	}

	/**
	 * @return the imports
	 */
	public AnnotatedElement[] getImports() {
		return imports;
	}

	/**
	 * @return the environment
	 */
	public Map<String, Class<?>> getEnvironment() {
		return environment;
	}
}
