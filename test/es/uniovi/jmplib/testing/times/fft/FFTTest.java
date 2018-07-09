package es.uniovi.jmplib.testing.times.fft;

import org.junit.Test;

public class FFTTest {

    @Test
    public void fftTest() {
        BenchMark mc = new FFTBenchMark(new FFT());
        mc.ITERATIONS = 800;
        mc.DIN = 8192;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("FFT time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
