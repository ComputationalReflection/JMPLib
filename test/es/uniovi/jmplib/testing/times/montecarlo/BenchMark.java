package es.uniovi.jmplib.testing.times.montecarlo;


public abstract class BenchMark {
    public static int DIN = 50; // original value
    public static int ITERATIONS = 1000;
    protected int microSeconds;

    protected Test test;

    public BenchMark(Test test) {
        this.test = test;
    }

    // startup
    public int RunStartup() {
        return this.runOneIteration();
    }

    public int runOneIteration() {
        Chronometer chronometer = new Chronometer();
        chronometer.start();
        test.test();
        chronometer.stop();
        this.microSeconds = chronometer.GetMicroSeconds();
        return this.microSeconds;
    }

    public abstract void prepare();

}
