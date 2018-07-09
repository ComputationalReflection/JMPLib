package es.uniovi.jmplib.testing.times.nbody;

import org.junit.Test;

public class NBodyTest {

    @Test
    public void nbody() {
        BenchMark mc = new NBodyBenchMark(new NBody());
        mc.ITERATIONS = 4600000;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("NBody time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
