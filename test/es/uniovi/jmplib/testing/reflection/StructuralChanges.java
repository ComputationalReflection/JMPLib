package es.uniovi.jmplib.testing.reflection;

import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.List;

import jmplib.DefaultIntercessor;
import jmplib.IIntercessor;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Field;
import jmplib.reflect.Method;

public class StructuralChanges {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();

	public static List<String> jmpLibInstrumentMethodNames = Arrays.asList("get_CurrentInstanceVersion",
			"set_CurrentInstanceVersion", "get_NewVersion", "_transferState", "get_ObjCreated", "set_NewVersion");

	public static List<String> jmpLibInstrumentFieldNames = Arrays.asList("_newVersion", "_currentInstanceVersion",
			"_currentClassVersion");

	public static boolean findMethodByName(String name, Method[] methodList) {
		for (Method m : methodList) {
			if (name.equals(m.getName()))
				return true;
		}
		return false;
	}

	public static boolean findFieldByName(String name, Field[] fieldList) {
		for (Field m : fieldList) {
			if (name.equals(m.getName()))
				return true;
		}
		return false;
	}

	public static void addFieldAndGetterSetter(Class<?> clazz, Class<?> fieldType, String fieldName, int modifier) {
		try {
			Intercessor.addField(clazz, new Field(modifier, fieldType, fieldName.toLowerCase()));

			Intercessor.addMethod(clazz, new Method("get" + fieldName, MethodType.methodType(fieldType),
					"return " + fieldName.toLowerCase() + ";"));

			String paramName = "new" + fieldName;
			Intercessor.addMethod(clazz,
					new Method("set" + fieldName, MethodType.methodType(void.class, fieldType),
							fieldName.toLowerCase() + " = " + paramName + ";", paramName));

		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
		}
	}

	public static void deleteField(Class<?> clazz, String fieldName) {
		try {
			Intercessor.removeField(clazz, new Field(fieldName));

		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
		}
	}

	public static void deleteMethod(Class<?> clazz, String methodName) {
		try {
			Intercessor.removeMethod(clazz, new Method(methodName));

		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
		}
	}
}
