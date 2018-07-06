package es.uniovi.jmplib.testing.times.montecarlo;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class MontecarloTest {

    @Test
    public void montecarlo() {
        BenchMark mc = new MonteCarloBenchMark(new MonteCarlo());
        mc.ITERATIONS = 20000000;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("Montecarlo time: " + time / 1000000 + "ms");
    }
}
