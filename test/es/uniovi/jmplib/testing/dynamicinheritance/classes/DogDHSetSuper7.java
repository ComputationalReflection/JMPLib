package es.uniovi.jmplib.testing.dynamicinheritance.classes;

public class DogDHSetSuper7 extends Animal implements Comparable {
	public String name = "foo";
	
	public DogDHSetSuper7(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void bark() {
		System.out.println("Woof!!");
	}

	public void shake() {
		System.out.println("Shakes");
	}
	@Override
	public int compareTo(Object o) {
		return this.name.compareTo(((DogDHSetSuper7)o).name);
	}
}