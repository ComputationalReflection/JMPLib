package es.uniovi.jmplib.testing.reflection.sourcecode;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

import es.uniovi.jmplib.testing.reflection.classes.DestinationClass;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk2;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk3;
import es.uniovi.jmplib.testing.reflection.classes.DestinationClassBulk4;
import es.uniovi.jmplib.testing.reflection.classes.SourceClass;
import es.uniovi.jmplib.testing.reflection.classes.SourceClassBulk;
import es.uniovi.jmplib.testing.reflection.classes.SourceClassBulk2;
import jmplib.DefaultEvaluator;
import jmplib.DefaultIntercessor;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Class;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;

/**
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class ReflectionSourceCodeTest {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();
	private static IEvaluator Evaluator = DefaultEvaluator.getInstance();

	private boolean compareSource(String src1, String src2) {
		String[] lines1 = src1.split("[\\r\\n]+");
		String[] lines2 = src2.split("[\\r\\n]+");

		// System.out.println(lines1.length + " : " + lines2.length);
		if (lines1.length != lines2.length)
			return false;
		int i = 0;
		while (i < lines1.length) {
			// System.out.println(lines1[i] + " : " + lines2[i]);
			if (!lines1[i].trim().equals(lines2[i].trim()))
				return false;
			i++;
		}
		return true;
	}

	@Test
	public void testGetMethodCode() {
		Class<?> cl = Introspector.decorateClass(SourceCodeClass.class);
		Method m = null;
		try {
			m = cl.getMethod("testMethod");
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			String source = m.getSourceCode();
			assertTrue(compareSource(source.trim(),
					"int cont = 1;\n" + "int i = 0;\n" + "while (i < 10) cont *= 2;\n" + "return cont;\n"));
		} catch (IllegalAccessException e) {
			fail();
		}
		try {
			assertTrue(m.getParameterString().equals("()"));
		} catch (IllegalAccessException e) {
			fail();
		}
	}

	@Test
	public void testGetAddedMethodCode() {
		Class<?> cl = Introspector.decorateClass(SourceCodeClass.class);
		Method m = null;

		try {
			m = cl.getMethod("testMethod");
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			Intercessor.addMethod(SourceCodeClass.class,
					new Method("addedMethod", MethodType.methodType(int.class), m.getSourceCode()));
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		try {
			m = cl.getMethod("addedMethod");
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			String source = m.getSourceCode();
			assertTrue(compareSource(source.trim(), m.getSourceCode().trim()));
		} catch (IllegalAccessException e) {
			fail();
		}
		try {
			assertTrue(m.getParameterString().equals("()"));
		} catch (IllegalAccessException e) {
			fail();
		}
	}

	@Test
	public void testGetGenericMethodCode() {
		Class<?> cl = Introspector.decorateClass(GenericSourceCodeClass.class);
		Method m = null;
		try {
			m = cl.getMethod("testMethod", Object.class);
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			String source = m.getSourceCode();
			assertTrue(compareSource(source.trim(), "String temp = cont.toString();\n" + "int i = 0;\n"
					+ "while (i < 10) temp += temp;\n" + "return temp;\n"));
		} catch (Exception e) {
			fail();
		}
		try {
			assertTrue(m.getParameterString().equals("(T cont)"));
		} catch (IllegalAccessException e) {
			fail();
		}
	}

	@Test
	public void testAddedGenericMethodCode() {
		Class<?> cl = Introspector.decorateClass(GenericSourceCodeClass.class);
		Method m = null;
		try {
			m = cl.getMethod("testMethod", Object.class);
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			Intercessor.addMethod(GenericSourceCodeClass.class,
					new Method(null, "addedMethod", MethodType.genericMethodType(1), m.getSourceCode(),
							Modifier.PUBLIC, new Class<?>[0], m.getGenericParameterTypes(), m.getGenericReturnType(),
							null, "cont"));
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		try {
			m = cl.getMethod("addedMethod", Object.class);
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			String source = m.getSourceCode();
			assertTrue(compareSource(source.trim(), "String temp = cont.toString();\n" + "int i = 0;\n"
					+ "while (i < 10) temp += temp;\n" + "return temp;\n"));
		} catch (Exception e) {
			fail();
		}
		try {
			assertTrue(m.getParameterString().equals("(T cont)"));
		} catch (IllegalAccessException e) {
			fail();
		}
	}

	@Test
	public void testAddedGenericMethodCodeGenericMethod() {
		Class<?> cl = Introspector.decorateClass(GenericSourceCodeClass.class);
		Method m = null;
		try {
			m = cl.getMethod("testMethod", Object.class);
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			Type[] ptypes = m.getGenericParameterTypes();
			Intercessor.addMethod(GenericSourceCodeClass.class, new Method(null, "addedMethodG",
					MethodType.genericMethodType(1), m.getSourceCode(), Modifier.PUBLIC, new Class<?>[0], ptypes,
					m.getGenericReturnType(),
					new jmplib.reflect.TypeVariable<?>[] { new jmplib.reflect.TypeVariable(ptypes[0].getTypeName()) },
					"cont"));
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		try {
			m = cl.getMethod("addedMethodG", Object.class);
		} catch (NoSuchMethodException e) {
			fail();
		}
		try {
			String source = m.getSourceCode();
			assertTrue(compareSource(source.trim(), "String temp = cont.toString();\n" + "int i = 0;\n"
					+ "while (i < 10) temp += temp;\n" + "return temp;\n"));
		} catch (Exception e) {
			fail();
		}
		try {
			assertTrue(m.getParameterString().equals("(T cont)"));
		} catch (IllegalAccessException e) {
			fail();
		}
	}

	@Test
	public void testGetClassCode() {
		Class<?> cl = Introspector.decorateClass(SourceCodeClass2.class);
		try {
			String source = cl.getSourceCode();
			assertTrue(compareSource(source,
					"public class SourceCodeClass2 {\n \npublic int testMethod()\n{\nint cont = 1;\n" + "int i = 0;\n"
							+ "while (i < 10) cont *= 2;\n" + "return cont;\n\n}\n}"));
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetGenericClassCode() {
		Class<?> cl = Introspector.decorateClass(GenericSourceCodeClass2.class);
		try {
			String source = cl.getSourceCode();
			assertTrue(compareSource(source,
					"public class GenericSourceCodeClass2<T> {\n \npublic String testMethod(T cont)\n{\nString temp = cont.toString();\n"
							+ "int i = 0;\n" + "while (i < 10) temp += temp;\n" + "return temp;\n\n}\n}"));
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCodeWithAnnotations() {
		Class<?> cl = Introspector.decorateClass(SourceCodeClassAnnotated.class);
		try {
			String source = cl.getSourceCode();
			assertTrue(compareSource(source,
					"@Deprecated\npublic class SourceCodeClassAnnotated {\n@Deprecated\npublic int testMethod()\n{\nint cont = 1;\n" + "int i = 0;\n"
							+ "while (i < 10) cont *= 2;\n" + "return cont;\n\n}\n}"));
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetEvolvedClassCode() {
			Class<?> cl = Introspector.decorateClass(SourceCodeClass3.class);
			Method m = null;

			try {
				m = cl.getMethod("testMethod");
			} catch (NoSuchMethodException e) {
				fail();
			}
			try {
				Intercessor.addMethod(SourceCodeClass3.class,
						new Method("addedMethod", MethodType.methodType(int.class), m.getSourceCode()));
			} catch (Exception e1) {
				e1.printStackTrace();
				fail();
			}
			try {
				m = cl.getMethod("addedMethod");
			} catch (NoSuchMethodException e) {
				fail();
			}
			try {
				String source = m.getSourceCode();
				assertTrue(compareSource(source.trim(), m.getSourceCode().trim()));
			} catch (IllegalAccessException e) {
				fail();
			}
			try {
				assertTrue(m.getParameterString().equals("()"));
			} catch (IllegalAccessException e) {
				fail();
			}
			try {
				String sourceClass = cl.getSourceCode();
				assertTrue(sourceClass.contains("SourceCodeClass3"));
				assertTrue(sourceClass.contains("public int testMethod()"));
				assertTrue(sourceClass.contains("public int addedMethod()"));
			}  catch (StructuralIntercessionException e) {
				fail();
			}
	}

}
