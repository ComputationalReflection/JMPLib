package es.uniovi.jmplib.testing.reflection.sourcecode;

public class GenericSourceCodeClass<T> {
    public String testMethod(T cont) {
        String temp = cont.toString();
        int i = 0;
        while (i < 10)
            temp += temp;
        return temp;
    }
}
