package es.uniovi.jmplib.testing.times.raytracer;

import es.uniovi.jmplib.testing.times.BenchMark;
import org.junit.Test;

public class RayTracerTest {

    @Test
    public void rayTracer() {
        BenchMark mc = new RayTracerBenchMark(new RayTracer());
        mc.ITERATIONS = 0;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("RayTracer time: " + time / 1000000 + "ms");
    }

}
