package es.uniovi.jmplib.testing.times.spectralnorm;

import org.junit.Test;

public class SpectralNormTest {

    @Test
    public void spectralnorm() {
        BenchMark mc = new SpectralNormBenchMark(new SpectralNorm());
        mc.ITERATIONS = 4000;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("Spectral norm time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
