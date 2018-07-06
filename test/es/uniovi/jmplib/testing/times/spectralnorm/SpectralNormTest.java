package es.uniovi.jmplib.testing.times.spectralnorm;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class SpectralNormTest {

    @Test
    public void binaryTrees() {
        BenchMark mc = new SpectralNormBenchMark(new SpectralNorm());
        mc.ITERATIONS = 2500;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("Spectral norm time: " + time / 1000000 + "ms");
    }
}
