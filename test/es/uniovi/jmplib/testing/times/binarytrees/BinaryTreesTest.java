package es.uniovi.jmplib.testing.times.binarytrees;

import org.junit.Test;

public class BinaryTreesTest {

    @Test
    public void binaryTrees() {
        BenchMark mc = new BinaryTreesBenchMark(new BinaryTrees());
        BenchMark.ITERATIONS = 10;
        mc.prepare();
        long start = System.nanoTime();
        mc.RunStartup();
        long end = System.nanoTime();
        double total = (end - start) / 1000000; // milliseconds
//        System.out.println("BinaryTrees: " + total + "ms");
        /*if(total > 500) { // more than 500 milliseconds
        	fail("Too slow: " + total + "ms");
        }*/
    }
}
