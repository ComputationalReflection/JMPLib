package es.uniovi.jmplib.testing.times.binarytrees;

import org.junit.Test;

public class BinaryTreesTest {

    @Test
    public void binaryTrees() {

        BenchMark mc = new BinaryTreesBenchMark(new BinaryTrees());
        BenchMark.ITERATIONS = 12;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("BinaryTrees time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
