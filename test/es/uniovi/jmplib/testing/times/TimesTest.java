package es.uniovi.jmplib.testing.times;

import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesTest;
import es.uniovi.jmplib.testing.times.raytracer.RayTracerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.Serializable;

@RunWith(Suite.class)
@SuiteClasses({RayTracerTest.class, BinaryTreesTest.class})
public class TimesTest implements Serializable {

    private static final long serialVersionUID = 1L;

}
