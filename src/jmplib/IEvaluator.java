package jmplib;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.InvokerData;
import jmplib.invokers.MemberInvokerData;
import jmplib.util.EnvironmentSetUp;

@SuppressWarnings("unused")
public interface IEvaluator {
	/***************************************
	 * INVOKERS
	 **************************************/

	/**
	 * <p>
	 * Generates an instance of the specified interface to invoke one method. The
	 * first parameter of the interface must be the class owner type, meaning, the
	 * class where the method is. This parameter is going to be the instance over
	 * the invokations are called.
	 * </p>
	 * 
	 * @param clazz
	 *            The class where the method is
	 * @param name
	 *            The name of the method
	 * @param info
	 *            The invoker data needed to obtain the invoker of the specified
	 *            method
	 * @return The populated interface with the invoker
	 * @throws StructuralIntercessionException
	 *             If a problem was encountered before finishing
	 */
	public <T> T getMethodInvoker(Type clazz, String name, MemberInvokerData<T> info) throws StructuralIntercessionException;

	/**
	 * <p>
	 * Generates an instance of the specified interface to provide one field getter.
	 * The first parameter of the interface must be the class owner type, meaning,
	 * the class where the field is.
	 * </p>
	 * 
	 * @param clazz
	 *            The class where the field is
	 * @param name
	 *            The name of the field
	 * @param info
	 *            The invoker data needed to obtain the invoker of the specified
	 *            field
	 * @return The populated interface with the invoker
	 * @throws StructuralIntercessionException
	 *             If a problem was encountered before finishing
	 */
	public <T> T getFieldInvoker(Type clazz, String name, MemberInvokerData<T> info) throws StructuralIntercessionException;

	/**
	 * <p>
	 * Generates an instance of the specified interface to provide one field setter.
	 * The first parameter of the interface must be the class owner type, meaning,
	 * the class where the field is.
	 * </p>
	 * 
	 * @param clazz
	 *            The class where the field is
	 * @param name
	 *            The name of the field
	 * @param info
	 *            The invoker data needed to obtain the invoker of the specified
	 *            field
	 * @return The populated interface with the invoker
	 * @throws StructuralIntercessionException
	 *             If a problem was encountered before finishing
	 */
	public <T> T setFieldInvoker(Type clazz, String name, MemberInvokerData<T> info) throws StructuralIntercessionException;

	/***************************************
	 * DYNAMIC CODE EXECUTION: EXEC, EVAL
	 **************************************/

	/**
	 * This method is a front-end of the exec method specially created to easily
	 * return empty classes with a concrete name and package. Coupled with the new
	 * functionalities that allow to incorporate arrays of fields and methods from
	 * other classes, it can be a powerful way of creating brand new classes made
	 * from parts of other classes.
	 * 
	 * @param packageName
	 *            Package name of the class to be created
	 * @param className
	 *            Name of the class to be created
	 * @param imports
	 *            Import clauses to be included in the new file.
	 * @return
	 */
	public Class<?> createEmptyClass(String packageName, String className, AnnotatedElement... imports)
			throws StructuralIntercessionException;

	/**
	 * <p>
	 * Exec method allows the addition of new classes at runtime from its source
	 * code to the application. These classes are incorporated to the specified
	 * package as they were there before the application start running, and can be
	 * used by other classes as normal.
	 * </p>
	 * <p>
	 * The following example shows how does it works:
	 * </p>
	 * 
	 * <pre>
	 * <code>Class<?> clazz = Evaluator.exec("package pack.example; public class Foo {}");</code>
	 * </pre>
	 * <p>
	 * This sentence adds a new class called {@code Foo} to the package
	 * {@code package.example} of the application. This new class is compatible with
	 * JMPlib modifications because a copy of its source code is located inside the
	 * source code folder.
	 * 
	 * <p>
	 * <b>IMPORTANT:</b> If you stop the application make sure you delete the added
	 * classes before launching it again.
	 * </p>
	 * 
	 * @param classSource
	 *            source code of the class
	 * @return The class reference
	 * @throws StructuralIntercessionException
	 */
	public Class<?> exec(String classSource) throws StructuralIntercessionException;

	/**
	 * <p>
	 * This method generates classes that implements generic functional interfaces,
	 * that have to extend {@link EnvironmentSetUp} interface. It allows to enclose
	 * the code given and invoke it by the interface instance returned.
	 * </p>
	 * <p>
	 * Additionally, this method define the global variables of the generated class
	 * using the map {@code environment} . All this variables are common for each
	 * invokation and have to be initialized by the method
	 * {@code EnvironmentSetUp.setEnvironment}.
	 * </p>
	 * 
	 * @param code
	 *            the code to enclose in the interface method
	 * @param invokerData
	 *            Data needed to perform the code evaluation
	 * @throws StructuralIntercessionException
	 */
	public <T> T generateEvalInvoker(String code, EvalInvokerData<T> invokerData)
			throws StructuralIntercessionException;
}
