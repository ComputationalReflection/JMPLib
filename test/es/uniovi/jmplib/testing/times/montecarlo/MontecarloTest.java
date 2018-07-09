package es.uniovi.jmplib.testing.times.montecarlo;

import org.junit.Test;

public class MontecarloTest {

    @Test
    public void montecarlo() {
        BenchMark mc = new MonteCarloBenchMark(new MonteCarlo());
        mc.ITERATIONS = 60000000;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("Montecarlo time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
