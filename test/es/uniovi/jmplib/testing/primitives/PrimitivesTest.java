package es.uniovi.jmplib.testing.primitives;

import es.uniovi.jmplib.testing.primitives.addfield.AddFieldTest;
import es.uniovi.jmplib.testing.primitives.addmethod.AddMethodTest;
import es.uniovi.jmplib.testing.primitives.deletefield.DeleteFieldTest;
import es.uniovi.jmplib.testing.primitives.deletemethod.DeleteMethodTest;
import es.uniovi.jmplib.testing.primitives.replacefield.ReplaceFieldTest;
import es.uniovi.jmplib.testing.primitives.replaceimplementation.ReplaceImplementationTest;
import es.uniovi.jmplib.testing.primitives.replacemethod.ReplaceMethodTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.Serializable;

@RunWith(Suite.class)
@SuiteClasses({AddFieldTest.class, DeleteFieldTest.class,
        ReplaceFieldTest.class, AddMethodTest.class, DeleteMethodTest.class,
        ReplaceImplementationTest.class, ReplaceMethodTest.class})
public class PrimitivesTest implements Serializable {

    private static final long serialVersionUID = 1L;

}
