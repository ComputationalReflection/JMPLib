package es.uniovi.jmplib.testing.dynamicinheritance.classes;

public class GenericPet<T> {
    private String ownerName;
    private T genericProperty;
    private int age;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public T getGenericProperty() {
        return genericProperty;
    }

    public void setGenericProperty(T genericProperty) {
        this.genericProperty = genericProperty;
    }
}