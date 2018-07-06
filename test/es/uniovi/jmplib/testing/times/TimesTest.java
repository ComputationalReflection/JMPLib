package es.uniovi.jmplib.testing.times;

import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesTest;
import es.uniovi.jmplib.testing.times.fannkuchredux.FannkuchreduxTest;
import es.uniovi.jmplib.testing.times.fft.FFTTest;
import es.uniovi.jmplib.testing.times.lu.LuTest;
import es.uniovi.jmplib.testing.times.mandelbrot.MandelbrotTest;
import es.uniovi.jmplib.testing.times.montecarlo.MontecarloTest;
import es.uniovi.jmplib.testing.times.nbody.NBodyTest;
import es.uniovi.jmplib.testing.times.raytracer.RayTracerTest;
import es.uniovi.jmplib.testing.times.sor.SORTest;
import es.uniovi.jmplib.testing.times.spectralnorm.SpectralNormTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.Serializable;

@RunWith(Suite.class)
@SuiteClasses({RayTracerTest.class, BinaryTreesTest.class, FannkuchreduxTest.class,
        FFTTest.class, LuTest.class, MandelbrotTest.class, MontecarloTest.class,
        NBodyTest.class, SORTest.class, SpectralNormTest.class})
public class TimesTest implements Serializable {

    private static final long serialVersionUID = 1L;

}
