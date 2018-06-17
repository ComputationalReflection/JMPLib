package es.uniovi.jmplib.testing.times.raytracer;

import static org.junit.Assert.fail;

import org.junit.Test;

public class RayTracerTest {

	@Test
	public void rayTracer() {
		BenchMark mc = new RayTracerBenchMark();
		BenchMark.ITERATIONS = 0;
		mc.prepare();
		//long start = System.nanoTime();
		mc.RunStartup();
		//long end = System.nanoTime();
		//double total = (end - start) / 1000000000; // seconds
		//if (total > 2) { // more than 2 seconds
		//	fail("Too slow: " + total + "s");
		//}
	}

}
