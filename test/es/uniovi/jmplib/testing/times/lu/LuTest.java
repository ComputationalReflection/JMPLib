package es.uniovi.jmplib.testing.times.lu;

import org.junit.Test;

public class LuTest {

    @Test
    public void lutest() {
        BenchMark mc = new LUBenchMark(null);
        mc.ITERATIONS = 10000;
        mc.DIN = 100;
        mc.prepare();
        int numIts = 5;
        int time;

        System.out.println("LU time; ");
        for (int i = 0;i<numIts;i++) {
            time = mc.RunStartup();
            System.out.print(time / 1000000.0 + ";");
        }
        System.out.println("");
    }
}
