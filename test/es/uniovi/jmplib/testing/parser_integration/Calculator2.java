package es.uniovi.jmplib.testing.parser_integration;

public class Calculator2 {
    public String name = "Calculator instance";

    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double sin(double a) {
        return Math.sin(a);
    }

    public double cos(double a) {
        return Math.cos(a);
    }

    public <T> T getGeneric(T param) {
        return param;
    }
}