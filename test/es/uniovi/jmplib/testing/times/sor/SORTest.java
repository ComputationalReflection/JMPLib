package es.uniovi.jmplib.testing.times.sor;

import org.junit.Test;

public class SORTest {

    @Test
    public void sor() {
        BenchMark mc = new SORBenchMark(null);
        mc.ITERATIONS = 20000;
        mc.DIN = 100;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("SOR time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
