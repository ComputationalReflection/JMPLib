package es.uniovi.jmplib.testing.primitives.replacemethod;

public class Calculator {

    @SuppressWarnings("unused")
    private double PI = 3.1415;
    private double aggregate;
    private double memory;
    private double lastResult = 0.0;

    public double multiply(double a, double b) {
        lastResult = a * b;
        return lastResult;
    }

    public double divide(int a, int b) {
        return a / b;
    }

    public double divide2(int a, int b) {
        return a / b;
    }

    public double pow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public double abs(double a) {
        return Math.abs(a);
    }

    public double sin(double a) {
        return Math.sin(a);
    }

    public double getMemory() {
        return memory;
    }

    public double getLastResult() {
        return lastResult;
    }

    public double getAggregate() {
        return aggregate;
    }

}
