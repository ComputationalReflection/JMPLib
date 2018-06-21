package es.uniovi.jmplib.testing.state;

public class Counter {

    public static int COUNTERS = 10;
    public int counter;

    public void increment() {
        counter++;
    }

    public void decrement() {
        counter--;
    }

    public double getCounter() {
        return counter;
    }

}
