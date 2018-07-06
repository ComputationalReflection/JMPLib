package es.uniovi.jmplib.testing.times.mandelbrot;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class MandelbrotTest {

    @Test
    public void mandelbrot() {
        BenchMark mc = new MandelbrotBenchMark(new Mandelbrot());
        mc.ITERATIONS = 4000;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("Mandelbrot time: " + time / 1000000 + "ms");
    }
}
