package es.uniovi.jmplib.testing.java_api;

import es.uniovi.jmplib.testing.combined.Calculator;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleEvaluator;
import jmplib.SimpleIntercessor;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.Vector;
import java.util.function.Function;

public class JavaApiTest {

    @Ignore
    @Test
    public void vectorTest() throws StructuralIntercessionException {
/*        jmplib.reflect.Class<?> vec = Introspector.decorateClass(Math.class);
        IIntercessor interc = new SimpleIntercessor().createIntercessor();

        interc.addMethod(vec, new jmplib.reflect.Method("myCos", MethodType.methodType(double.class, double.class),
                "return Math.cos(a);", Modifier.PUBLIC | Modifier.STATIC, "a"));

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        Function<Vector, Integer> newCos = evaluator.getMethodInvoker(Math.class, "myCos",
                new MemberInvokerData<>(Function.class, Vector.class, int.class));
        System.out.println(newCos.apply(null));*/

        jmplib.reflect.Class<?> vec = Introspector.decorateClass(Vector.class);
        IIntercessor interc = new SimpleIntercessor().createIntercessor();

        Vector v = new Vector();

        v.size();

        interc.addMethod(vec, new jmplib.reflect.Method("mySize", MethodType.methodType(int.class),
                "return this.size();", Modifier.PUBLIC));

        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        Function<Vector, Integer> newSize = evaluator.getMethodInvoker(Vector.class, "mySize",
                new MemberInvokerData<>(Function.class, Vector.class, int.class));
        System.out.println(newSize.apply(v));

    }
}
