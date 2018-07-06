package es.uniovi.jmplib.testing.times.spectralnorm;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.Test;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class SpectralNormBenchMark extends BenchMark {

    public SpectralNormBenchMark(Test test) {
        super(test);
    }


    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            // SpectralNorm
            transaction.replaceImplementation(SpectralNorm.class, new jmplib.reflect.Method("test",
                    "spectralnorm(BenchMark.ITERATIONS);"));
            transaction.addMethod(SpectralNorm.class, new jmplib.reflect.Method("spectralnorm",
                    MethodType.methodType(double.class, int.class),
                    "double[] u = new double[n];"
                            + "double[] v = new double[n];"
                            + "double[] w = new double[n];"
                            + "double vv = 0;"
                            + "double vBv = 0;"
                            + "for (int i = 0; i < n; i++) {"
                            + "u[i] = 1.0; v[i] = 0.0; w[i] = 0.0;"
                            + "}"
                            + "for (int i = 0; i < 10; i++) {"
                            + " AtAu(u,v,w);"
                            + " AtAu(v,u,w);"
                            + "}"
                            + "for (int i = 0; i < n; i++) {"
                            + "vBv += u[i]*v[i];"
                            + "vv  += v[i]*v[i];"
                            + "}"
                            + "return Math.sqrt(vBv/vv);",
                    Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE, "n"));
            transaction.addMethod(SpectralNorm.class, new jmplib.reflect.Method("A",
                    MethodType.methodType(double.class, int.class, int.class),
                    "int div = ( ((i+j) * (i+j+1) >>> 1) +i+1 );"
                            + "return 1.0 / div;",
                    Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE, "i", "j"));
            transaction.addMethod(SpectralNorm.class, new jmplib.reflect.Method("Au",
                    MethodType.methodType(void.class, double[].class, double[].class),
                    "for (int i = 0; i < u.length; i++) {"
                            + "double sum = 0;"
                            + "for (int j = 0; j < u.length; j++) { sum += A(i, j) * u[j]; }"
                            + "v[i] = sum;"
                            + "}",
                    Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE, "u", "v"));
            transaction.addMethod(SpectralNorm.class, new jmplib.reflect.Method("Atu",
                    MethodType.methodType(void.class, double[].class, double[].class),
                    "for (int i = 0; i < u.length; i++) {"
                            + "double sum = 0;"
                            + "for (int j = 0; j < u.length; j++) { sum += A(j, i) * u[j]; }"
                            + "v[i] = sum;"
                            + "}",
                    Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE, "u", "v"));
            transaction.addMethod(SpectralNorm.class, new jmplib.reflect.Method("AtAu",
                    MethodType.methodType(void.class, double[].class, double[].class, double[].class),
                    "Au(u,w);"
                            + "Atu(w,v);",
                    Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE, "u", "v", "w"));


            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
