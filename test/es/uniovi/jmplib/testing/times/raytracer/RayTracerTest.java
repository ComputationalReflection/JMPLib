package es.uniovi.jmplib.testing.times.raytracer;

import org.junit.Test;

public class RayTracerTest {

    @Test
    public void rayTracer() {
        BenchMark mc = new RayTracerBenchMark(new JGFRayTracerBench());
        BenchMark.ITERATIONS = 0;
        mc.prepare();
        int numIts = 1;
        int time;

        System.out.println("Raytracer time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }

}
