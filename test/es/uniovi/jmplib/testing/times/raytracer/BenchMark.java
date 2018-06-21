package es.uniovi.jmplib.testing.times.raytracer;


public abstract class BenchMark {
    public static int ITERATIONS = 1000;
    protected int microSeconds;

    // startup
    public void RunStartup() {
        BenchMark self = this;
        self.runOneIteration();
    }

    public abstract void runOneIteration();

    public abstract void prepare();

}
