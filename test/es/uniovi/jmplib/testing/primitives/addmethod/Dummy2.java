package es.uniovi.jmplib.testing.primitives.addmethod;

import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import static org.junit.Assert.fail;

public class Dummy2 {

    public void addPrintMethod() {
        MethodType methodType = MethodType.methodType(int.class, int.class);
        IIntercessor intt = new SimpleIntercessor().createIntercessor();
        try {
            intt.addMethod(this.getClass(),
                    new jmplib.reflect.Method("print", methodType, "System.out.println(n); return n;", Modifier.PUBLIC, "n"));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
