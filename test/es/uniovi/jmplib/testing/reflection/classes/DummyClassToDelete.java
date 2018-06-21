package es.uniovi.jmplib.testing.reflection.classes;

public class DummyClassToDelete {
    public int dummyProperty;
    public int dummyPropertyToDelete;

    public int getDummyProperty() {
        return dummyProperty;
    }

    public int setDummyProperty(int dummyProperty) {
        return this.dummyProperty = dummyProperty;
    }
}
