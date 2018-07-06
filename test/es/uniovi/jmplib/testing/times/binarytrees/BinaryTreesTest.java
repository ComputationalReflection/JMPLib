package es.uniovi.jmplib.testing.times.binarytrees;

import es.uniovi.jmplib.testing.times.BenchMark;
import org.junit.Test;

public class BinaryTreesTest {

    @Test
    public void binaryTrees() {
        BenchMark mc = new BinaryTreesBenchMark(new BinaryTrees());
        mc.ITERATIONS = 18;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("BinaryTrees time: " + time / 1000000 + "ms");
    }
}
