package es.uniovi.jmplib.testing.times.sor;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class SORTest {

    @Test
    public void sor() {
        BenchMark mc = new SORBenchMark(null);
        mc.ITERATIONS = 10000;
        mc.DIN = 100;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("SOR time: " + time / 1000000 + "ms");
    }
}
