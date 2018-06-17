package es.uniovi.jmplib.testing.times.raytracer;


public abstract class BenchMark {
	protected int microSeconds;
	public static int ITERATIONS = 1000;

	// startup
	public void RunStartup() {
		BenchMark self = this;
		self.runOneIteration();
	}

	public abstract void runOneIteration();
	public abstract void prepare();

}
