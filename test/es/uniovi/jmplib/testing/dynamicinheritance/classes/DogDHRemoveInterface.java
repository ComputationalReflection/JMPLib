package es.uniovi.jmplib.testing.dynamicinheritance.classes;

public class DogDHRemoveInterface {
    public String name = "foo";

    public DogDHRemoveInterface(String name) {
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