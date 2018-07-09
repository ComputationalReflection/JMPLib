package es.uniovi.jmplib.testing.times.mandelbrot;

import org.junit.Test;

public class MandelbrotTest {

    @Test
    public void mandelbrot() {
        BenchMark mc = new MandelbrotBenchMark(new Mandelbrot());
        mc.ITERATIONS = 6000;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("Mandelbrot time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
