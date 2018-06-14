package es.uniovi.jmplib.testing.dynamicinheritance.classes;

public class DogDHSetSuper { //implements Comparable<DogDHSetSuper> {
	public String name = "foo";
	
	public DogDHSetSuper(String name) {
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
	/*
	@Override
	public int compareTo(DogDHSetSuper o) {
		return this.name.compareTo(((DogDHSetSuper)o).name);
	}*/
}