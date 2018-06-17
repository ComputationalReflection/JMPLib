package es.uniovi.jmplib.testing.reflection.classes;

import java.io.Serializable;

public class DummyClassAccess implements Serializable{
	public static int dummyPropertyStatic;
	public int dummyProperty;
	private int dummyPropertyPrivate;
	
	public int dummyMethod() {
		return dummyProperty;
	}
	private int dummyMethodPrivate() {
		return dummyPropertyPrivate;
	}
	public static int dummyMethodStatic() {
		return dummyPropertyStatic;
	}
}
