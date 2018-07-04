package es.uniovi.jmplib.testing.times.spectralnorm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BenchMark {
	protected int microSeconds;
	public static int DIN = 50; // original value
	public static int ITERATIONS = 1000;

	// startup
	public int RunStartup() {
		BenchMark self = this;
		self.runOneIteration();
		return this.microSeconds;
	}

	// steady
	public int RunSteady(int maxIterations, int k, double CoV) {
		List<Integer> executionTimes = new ArrayList<Integer>();
		for (int i = 1; i <= maxIterations; i++) {
			int time = this.runOneIteration();
			executionTimes.add(time);
			if (areWeDone(executionTimes, k, CoV))
				break;
		}
		return (int) (getMean(executionTimes, k));
	}

	public static void PrintHelp(PrintStream outStream, int argumentsCount) {
		outStream.printf("Invalid number of arguments {0}. Usage:",
				argumentsCount);
		outStream.printf("'{0} it' for Startup", Program.FRIENDLY_NAME); // (single
																			// bench
																			// iteration)
		outStream.printf("'{0} it n k Cov' for Steady", Program.FRIENDLY_NAME); // (n=maxIterations,
																				// k=measurementsPerInvocation,
																				// CoV=coefficientOfVariation)
	}

	private static boolean areWeDone(List<Integer> executionTimes, int k,
			double CoV) {
		if (executionTimes.size() < k)
			return false;
		double summation = 0;
		double mean = getMean(executionTimes, k);
		for (int i = executionTimes.size() - k; i < executionTimes.size(); i++)
			summation += Math.pow(executionTimes.get(i) - mean, 2);
		double stdDeviation = Math.sqrt(summation / k);
		return stdDeviation / mean < CoV;
	}

	private static double getMean(List<Integer> executionTimes, int k) {
		double summation = 0;
		for (int i = executionTimes.size() - k; i < executionTimes.size(); i++)
			summation += executionTimes.get(i);
		return summation / k;
	}

	public abstract int runOneIteration();

	public abstract void prepare();

}
