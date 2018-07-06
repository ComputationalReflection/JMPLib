package es.uniovi.jmplib.testing.times.fannkuchredux;
import es.uniovi.jmplib.testing.times.BenchMark;
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
		mc.ITERATIONS = 10;
		mc.prepare();
		int time = mc.RunStartup();
		System.out.println("BinaryTrees time: " + time / 1000000 + "ms");
	}
}
