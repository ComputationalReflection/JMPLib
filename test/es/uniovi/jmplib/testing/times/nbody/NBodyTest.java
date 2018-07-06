package es.uniovi.jmplib.testing.times.nbody;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class NBodyTest {

    @Test
    public void nbody() {
        BenchMark mc = new NBodyBenchMark(new NBody());
        mc.ITERATIONS = 2300000;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("NBody time: " + time / 1000000 + "ms");
    }
}
