package es.uniovi.jmplib.testing.times.lu;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class LuTest {

    @Test
    public void binaryTrees() {
        BenchMark mc = new LUBenchMark(null);
        mc.ITERATIONS = 5000;
        mc.DIN = 100;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("BinaryTrees time: " + time / 1000000 + "ms");
    }
}
