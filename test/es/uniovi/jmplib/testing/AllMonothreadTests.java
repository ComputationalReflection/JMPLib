package es.uniovi.jmplib.testing;

import es.uniovi.jmplib.testing.annotations.AnnotationTest;
import es.uniovi.jmplib.testing.dynamiccode.DynamicCodeTest;
import es.uniovi.jmplib.testing.dynamicinheritance.DynamicInheritanceInterfacesTest;
import es.uniovi.jmplib.testing.dynamicinheritance.DynamicInheritanceSuperClassTest;
import es.uniovi.jmplib.testing.generics.GenericTest;
import es.uniovi.jmplib.testing.imports.ImportTest;
import es.uniovi.jmplib.testing.inheritance.InheritanceStaticTest;
import es.uniovi.jmplib.testing.instrumentation.InstrumentationTest;
import es.uniovi.jmplib.testing.invokers.InvokersTest;
import es.uniovi.jmplib.testing.modifiers.ModifierStaticTest;
import es.uniovi.jmplib.testing.parser_integration.JavaParserIntegrationTest;
import es.uniovi.jmplib.testing.primitives.FieldTest;
import es.uniovi.jmplib.testing.primitives.MethodTest;
import es.uniovi.jmplib.testing.primitives.PrimitivesTest;
import es.uniovi.jmplib.testing.reflection.*;
import es.uniovi.jmplib.testing.reflection.sourcecode.ReflectionSourceCodeTest;
import es.uniovi.jmplib.testing.state.StateTest;
import es.uniovi.jmplib.testing.times.TimesTest;
import es.uniovi.jmplib.testing.visibility.ExternalFieldAccesTest;
import jmplib.annotations.ExcludeFromJMPLib;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.Serializable;

@ExcludeFromJMPLib
@RunWith(Suite.class)
@SuiteClasses({DynamicInheritanceSuperClassTest.class, ModifierStaticTest.class,
        StateTest.class, DynamicCodeTest.class, InstrumentationTest.class,
        PrimitivesTest.class, InvokersTest.class, TimesTest.class,
        FieldTest.class, MethodTest.class, ClassStructureAccessTest.class,
        ClassStructureDeleteTest.class, ClassStructureEvolutionTest.class,
        ClassStructureNoEvolutionTest.class, InheritanceStaticTest.class,
        DynamicInheritanceInterfacesTest.class, AnnotationTest.class,
        GenericTest.class, ImportTest.class, AddMembersWithReflectionTest.class,
        ExtendedReflectionTest.class, ReflectionSourceCodeTest.class,
        ExternalFieldAccesTest.class, JavaParserIntegrationTest.class})
public class AllMonothreadTests implements Serializable {

    private static final long serialVersionUID = 1L;

}
