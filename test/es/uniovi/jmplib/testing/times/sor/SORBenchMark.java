package es.uniovi.jmplib.testing.times.sor;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class SORBenchMark extends BenchMark {
    public SORBenchMark(Test test) {
        super(test);
    }

    Random r = new Random();

    @Override
    public int runOneIteration() {
        double[][] matrix = randomMatrix(DIN, DIN);
        Chronometer chronometer = new Chronometer();
        Test test = new SOR(matrix);
        chronometer.start();
        test.test();
        chronometer.stop();
        return chronometer.GetMicroSeconds();
    }

    private double[][] randomMatrix(int M, int N) {
        double A[][] = new double[M][N];

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                A[i][j] = r.nextDouble();
        return A;
    }

    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            transaction.addMethod(SOR.class, new jmplib.reflect.Method("num_flops", MethodType
                    .methodType(double.class, int.class, int.class, int.class),
                    "double Md = (double) M;" + "double Nd = (double) N;"
                            + "double num_iterD = (double) num_iterations;"
                            + "return (Md-1)*(Nd-1)*num_iterD*6.0;",
                    Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL,
                    new String[]{"N", "M", "num_iterations"}));
            transaction
                    .addMethod(
                            SOR.class,
                            new jmplib.reflect.Method("execute",
                                    MethodType.methodType(void.class, double.class,
                                            double[][].class, int.class),
                                    "int M = G.length;"
                                            + "int N = G[0].length;"
                                            + "double omega_over_four = omega * 0.25;"
                                            + "double one_minus_omega = 1.0 - omega;"
                                            + "int Mm1 = M-1;"
                                            + "int Nm1 = N-1;"
                                            + "for (int p=0; p<num_iterations; p++){"
                                            + "for (int i=1; i<Mm1; i++){"
                                            + "double[] Gi = G[i];"
                                            + "double[] Gim1 = G[i-1];"
                                            + "double[] Gip1 = G[i+1];"
                                            + "for (int j=1; j<Nm1; j++){"
                                            + "Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j-1]"
                                            + "+ Gi[j+1]) + one_minus_omega * Gi[j];"
                                            + "}" + "}" + "}",
                                    Modifier.PUBLIC
                                            | Modifier.STATIC | Modifier.FINAL, new String[]{"omega", "G", "num_iterations"}));
            transaction.replaceImplementation(SOR.class, new jmplib.reflect.Method("test",
                    "execute(1.25, matrix, BenchMark.ITERATIONS);"));
            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
