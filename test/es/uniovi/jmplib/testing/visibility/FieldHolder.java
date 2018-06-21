package es.uniovi.jmplib.testing.visibility;

public class FieldHolder {

    public int publicField = 1;
    protected int protectedField = 2;
    int packageField = 3;
    private int privateField = 4;

    public int getPrivateField() {
        return privateField;
    }

}
