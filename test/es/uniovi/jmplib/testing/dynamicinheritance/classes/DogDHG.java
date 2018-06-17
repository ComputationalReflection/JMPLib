package es.uniovi.jmplib.testing.dynamicinheritance.classes;

public class DogDHG {
	public String name = "foo";
	
	public DogDHG(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void bark() {
		System.out.println("Woof!!");
	}

	public void shake() {
		System.out.println("Shakes");
	}
}