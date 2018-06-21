package es.uniovi.jmplib.testing.instrumentation;

import jmplib.SimpleEvaluator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@ExcludeFromJMPLib
public class InstrumentationTest {
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    @BeforeClass
    public static void initialize() throws StructuralIntercessionException {
        Intercessor.addField(HolderToModify.class, new jmplib.reflect.Field(long.class, "_aux"));
    }

    @Test
    public void testAssign() {
        Holder h = new Holder();
        h.f1 = "hi";
        assertThat(h.f1, is("hi"));
        h.f2 = 4;
        assertThat(h.f2, is(4));
        h.f1 += "!";
        assertThat(h.f1, is("hi!"));
        h.f2 += 4;
        assertThat(h.f2, is(8));
        h.f2 -= 2;
        assertThat(h.f2, is(6));
        h.f2 *= 3;
        assertThat(h.f2, is(18));
        h.f2 /= 9;
        assertThat(h.f2, is(2));
    }

    @Test
    public void testIncrement() {
        Holder h = new Holder();
        h.f2++;
        assertThat(h.f2, is(1));
        h.f2--;
        assertThat(h.f2, is(0));
        assertThat(++h.f2, is(1));
        assertThat(h.f2++, is(1));
        assertThat(h.f2, is(2));
        assertThat(--h.f2, is(1));
        assertThat(h.f2--, is(1));
        assertThat(h.f2, is(0));
    }

    @Test
    public void testAssignVersions() {
        HolderToModify h = new HolderToModify();
        h.f1 = "hi";
        assertThat(h.f1, is("hi"));
        h.f2 = 4;
        assertThat(h.f2, is(4));
        h.f1 += "!";
        assertThat(h.f1, is("hi!"));
        h.f2 += 4;
        assertThat(h.f2, is(8));
        h.f2 -= 2;
        assertThat(h.f2, is(6));
        h.f2 *= 3;
        assertThat(h.f2, is(18));
        h.f2 /= 9;
        assertThat(h.f2, is(2));
    }

    @Test
    public void testIncrementVersions() {
        HolderToModify h = new HolderToModify();
        h.f2++;
        assertThat(h.f2, is(1));
        h.f2--;
        assertThat(h.f2, is(0));
        assertThat(++h.f2, is(1));
        assertThat(h.f2++, is(1));
        assertThat(h.f2, is(2));
        assertThat(--h.f2, is(1));
        assertThat(h.f2--, is(1));
        assertThat(h.f2, is(0));
    }

    @Test
    public void testAssignFromVersions() throws StructuralIntercessionException {
        Intercessor.addMethod(HolderToModify.class,
                new jmplib.reflect.Method("checkAssign", MethodType.methodType(void.class), "Holder h = new Holder();"
                        + "h.f1 = \"hi\";" + "org.junit.Assert.assertThat(h.f1, org.hamcrest.CoreMatchers.is(\"hi\"));"
                        + "h.f2 = 4;" + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(4));"
                        + "h.f1 += \"!\";" + "org.junit.Assert.assertThat(h.f1, org.hamcrest.CoreMatchers.is(\"hi!\"));"
                        + "h.f2 += 4;" + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(8));"
                        + "h.f2 -= 2;" + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(6));"
                        + "h.f2 *= 3;" + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(18));"
                        + "h.f2 /= 9;" + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(2));",
                        Modifier.PUBLIC));
        Func_Holder_void invoker = Evaluator.getMethodInvoker(HolderToModify.class, "checkAssign",
                new MemberInvokerData<>(Func_Holder_void.class));
        invoker.invoke(new HolderToModify());
    }

    @Test
    public void testIncrementFromVersions() throws StructuralIntercessionException {
        Intercessor.addMethod(HolderToModify.class,
                new jmplib.reflect.Method("checkIncrement", MethodType.methodType(void.class),
                        "Holder h = new Holder();" + "h.f2++;"
                                + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(1));" + "h.f2--;"
                                + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(0));"
                                + "org.junit.Assert.assertThat(++h.f2, org.hamcrest.CoreMatchers.is(1));"
                                + "org.junit.Assert.assertThat(h.f2++, org.hamcrest.CoreMatchers.is(1));"
                                + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(2));"
                                + "org.junit.Assert.assertThat(--h.f2, org.hamcrest.CoreMatchers.is(1));"
                                + "org.junit.Assert.assertThat(h.f2--, org.hamcrest.CoreMatchers.is(1));"
                                + "org.junit.Assert.assertThat(h.f2, org.hamcrest.CoreMatchers.is(0));",
                        Modifier.PUBLIC));
        Func_Holder_void invoker = Evaluator.getMethodInvoker(HolderToModify.class, "checkIncrement",
                new MemberInvokerData<>(Func_Holder_void.class));
        invoker.invoke(new HolderToModify());
    }
}
