package es.uniovi.jmplib.testing.dynamiccode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javaparser.ParseException;

import jmplib.DefaultEvaluator;
import jmplib.DefaultIntercessor;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.MemberInvokerData;

@ExcludeFromJMPLib
public class DynamicCodeTest {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();
	private static IEvaluator Evaluator = DefaultEvaluator.getInstance();
	
	@BeforeClass
	public static void initialize() throws StructuralIntercessionException {
		IIntercessor transaction = new TransactionalIntercessor();
		transaction.addField(Dummy.class, new jmplib.reflect.Field(Modifier.PUBLIC, String.class,
				"text", "\"Eval\""));
		MethodType methodType = MethodType.methodType(String.class);
		transaction.addMethod(Dummy.class, new jmplib.reflect.Method("getText", methodType,
				"return text;", Modifier.PUBLIC, new String[0]));
		transaction.commit();
	}

	@AfterClass
	public static void tidyUp() throws StructuralIntercessionException {
		File src = new File(
				"src/es/uniovi/jmplib/testing/dynamiccode/Dummy2.java");
		src.delete();
		File bin = new File(
				"bin/es/uniovi/jmplib/testing/dynamiccode/Dummy2.class");
		bin.delete();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void atributeEvalTest() throws StructuralIntercessionException {
		Function<Dummy, String> getText = Evaluator.generateEvalInvoker(
				"return d.text;", // Code
				new EvalInvokerData<>(Function.class, // Functional interface
				new String[] { "d" }, // Parameter names
				new Class<?>[] { Dummy.class, String.class })); // Parametrization
																// classes
		String text = getText.apply(new Dummy());
		assertThat(text, equalTo("Eval"));

		BiFunction<Dummy, String, String> setText = Evaluator
				.generateEvalInvoker("d.text = value; return d.text;", // Code
						new EvalInvokerData<>(BiFunction.class, // Functional interface
						new String[] { "d", "value" }, // Parameter names
						new Class<?>[] { Dummy.class, String.class,
								String.class }));
		Dummy d = new Dummy();
		text = setText.apply(d, "NewEval");
		assertThat(text, equalTo("NewEval"));
		text = getText.apply(d);
		assertThat(text, equalTo("NewEval"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void methodEvalTest() throws StructuralIntercessionException {
		Function<Dummy, String> getText = Evaluator.generateEvalInvoker(
				"return d.getText();", // Code
				new EvalInvokerData<>(Function.class, // Functional interface
				new String[] { "d" }, // Parameter names
				new Class<?>[] { Dummy.class, String.class })); // Parametrization
																// classes
		String text = getText.apply(new Dummy());
		assertThat(text, equalTo("Eval"));

		BiFunction<Dummy, String, String> setText = Evaluator
				.generateEvalInvoker("d.text = value; return d.getText();", // Code
						new EvalInvokerData<>(BiFunction.class, // Functional interface
						new String[] { "d", "value" }, // Parameter names
						new Class<?>[] { Dummy.class, String.class,
								String.class }));
		Dummy d = new Dummy();
		text = setText.apply(d, "NewEval");
		assertThat(text, equalTo("NewEval"));
		text = getText.apply(d);
		assertThat(text, equalTo("NewEval"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void genericEvalTest() throws StructuralIntercessionException {
		Func_two_parameters<Integer, Integer, Integer> evaluator = Evaluator
				.generateEvalInvoker("a + b", new EvalInvokerData<>(Func_two_parameters.class,
						new String[] { "a", "b" }, new Class<?>[] {
								Integer.class, Integer.class, Integer.class }));
		assertNotNull(evaluator);
		assertTrue(1 + 1 == evaluator.calculate(1, 1));
		Func_two_parameters<String, String, String> evaluator2 = Evaluator
				.generateEvalInvoker("t1.concat(t2)",
						new EvalInvokerData<>(Func_two_parameters.class, new String[] { "t1", "t2" },
						new Class<?>[] { String.class, String.class,
								String.class }));
		assertNotNull(evaluator2);
		assertThat("This works!!", equalTo(evaluator2.calculate("This ", "works!!")));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void functionalInterfaceEvalTest()
			throws StructuralIntercessionException {

		Function<Integer, Integer> evaluator = Evaluator.generateEvalInvoker(
				"number * number", // Code
				new EvalInvokerData<>(Function.class, // Functional interface
				new String[] { "number" }, // Parameter names
				new Class<?>[] { Integer.class, Integer.class })); // Parametrization
																	// classes
		assertNotNull(evaluator);
		assertTrue(1 == evaluator.apply(1));
		assertTrue(16 == evaluator.apply(4));

		BiFunction<String, String, String> concat = Evaluator
				.generateEvalInvoker("t1.concat(t2)", new EvalInvokerData<>(BiFunction.class,
						new String[] { "t1", "t2" }, new Class<?>[] {
								String.class, String.class, String.class }));
		assertNotNull(concat);
		assertThat("Hola mundo", equalTo(concat.apply("Hola ", "mundo")));
		assertThat("this works", equalTo(concat.apply("this ", "works")));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void execTest() throws StructuralIntercessionException,
			InstantiationException, IllegalAccessException {
		Class<?> clazz = Evaluator
				.exec("package es.uniovi.jmplib.testing.dynamiccode; "
						+ "public class Dummy2 {"
						+ "public String text = \"Exec\";" + "}");
		assertNotNull(clazz);
		assertThat(clazz.getName(),
				equalTo("es.uniovi.jmplib.testing.dynamiccode.Dummy2"));

		Function<Object, String> getText = Evaluator.generateEvalInvoker(
				"return d.text;", // Code
				new EvalInvokerData<>(Function.class, // Functional interface
				new String[] { "d" }, // Parameter names
				new Class<?>[] { clazz, String.class })); // Parametrization
															// classes
		String text = getText.apply(clazz.newInstance());
		assertThat(text, equalTo("Exec"));

		BiFunction<Object, String, String> setText = Evaluator
				.generateEvalInvoker("d.text = value; return d.text;", // Code
						new EvalInvokerData<>(BiFunction.class, // Functional interface
						new String[] { "d", "value" }, // Parameter names
						new Class<?>[] { clazz, String.class, String.class }));
		Object d = clazz.newInstance();
		text = setText.apply(d, "NewEval");
		assertThat(text, equalTo("NewEval"));
		text = getText.apply(d);
		assertThat(text, equalTo("NewEval"));

		MethodType methodType = MethodType.methodType(String.class);
		Intercessor.addMethod(clazz, new jmplib.reflect.Method("getText", methodType, "return text;",
				Modifier.PUBLIC, new String[0]));
		@SuppressWarnings("rawtypes")
		Function<Object, String> getText2 = Evaluator.getMethodInvoker(clazz,
				"getText", new MemberInvokerData<Function>(Function.class, Modifier.PUBLIC, new Class[] {
						clazz, String.class }));
		text = getText2.apply(d);
		assertThat(text, equalTo("NewEval"));
	}
	
	@Test
	public void evalExecCodeWithSyntaxErrors() {
		try {
			// Missing operator
			Func_Double invoker = Evaluator.generateEvalInvoker(
					"return 3.0 4.0", new EvalInvokerData<>(Func_Double.class));
			invoker.invoke();
			fail("MODEL ERROR: Syntactically invalid code executed without reporting errors");
		} catch (Exception ex) {
			assertNotNull(ex.getCause());
			assertThat(ex.getCause(),
					instanceOf(ParseException.class));
			assertNotNull(ex.getMessage());
			assertNotNull(ex.getCause().getMessage());
		}
	}
	
	@Test
	public void evalExecExceptionThrowingCode() {
		try {
			Func_Double invoker = Evaluator
					.generateEvalInvoker("throw new Exception(\"Dynamic exception\");",
							new EvalInvokerData<>(Func_Double.class));
			invoker.invoke();
			fail("MODEL ERROR: Exception code executed without reporting errors");
		} catch (Exception ex) {
			assertNotNull(ex.getCause());
			assertThat(ex.getCause(),
					instanceOf(CompilationFailedException.class));
			assertNotNull(ex.getMessage());
			assertNotNull(ex.getCause().getMessage());			
		}
		try {
			Func_Double__Exception invoker = Evaluator
					.generateEvalInvoker("throw new Exception(\"Dynamic exception\");",
							new EvalInvokerData<>(Func_Double__Exception.class));
			invoker.invoke();
			fail("MODEL ERROR: Exception code executed without reporting errors");
		} catch (Exception ex) {
			assertNull(ex.getCause());
			assertNotNull(ex.getMessage());
			assertThat(ex.getMessage(), equalTo("Dynamic exception"));
			
		}
	}

}
