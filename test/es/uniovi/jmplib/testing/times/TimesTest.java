package es.uniovi.jmplib.testing.times;

import java.io.Serializable;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uniovi.jmplib.testing.times.raytracer.RayTracerTest;
import es.uniovi.jmplib.testing.times.binarytrees.BinaryTreesTest;

@RunWith(Suite.class)
@SuiteClasses({RayTracerTest.class, BinaryTreesTest.class})
public class TimesTest implements Serializable {

	private static final long serialVersionUID = 1L;

}
