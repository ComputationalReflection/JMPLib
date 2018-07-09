package es.uniovi.jmplib.testing.times.fannkuchredux;
import org.junit.Test;

/* The Computer Language Benchmarks Game
 * http://benchmarksgame.alioth.debian.org/
 *
 * contributed by Jarkko Miettinen
 * modified by Daryl Griffith
 */

public class FannkuchreduxTest {

	@Test
	public void testFannkuchRedux() {
		BenchMark mc = new FannkuchreduxBenchMark(new Fannkuchredux());
		BenchMark.ITERATIONS = 11;
		mc.prepare();

		int numIts = 5;
		int time;

		System.out.println("Fannkuch Redux time; ");
		for (int i = 0;i<numIts;i++) {
			time = mc.RunStartup();
			System.out.print(time / 1000000.0 + ";");
		}
		System.out.println("");
	}
}
