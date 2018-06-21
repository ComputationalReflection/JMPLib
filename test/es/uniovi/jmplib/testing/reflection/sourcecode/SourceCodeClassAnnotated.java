package es.uniovi.jmplib.testing.reflection.sourcecode;

@Deprecated
public class SourceCodeClassAnnotated {
    @Deprecated
    public int testMethod() {
        int cont = 1;
        int i = 0;
        while (i < 10)
            cont *= 2;
        return cont;
    }
}
