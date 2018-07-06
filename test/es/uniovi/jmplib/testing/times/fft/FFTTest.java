package es.uniovi.jmplib.testing.times.fft;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTrees;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesBenchMark;
import org.junit.Test;

public class FFTTest {

    @Test
    public void fftTest() {
        BenchMark mc = new FFTBenchMark(new FFT());
        mc.ITERATIONS = 400;
        mc.DIN = 8192;
        mc.prepare();
        int time = mc.RunStartup();
        System.out.println("FFT time: " + time / 1000000 + "ms");
    }
}
