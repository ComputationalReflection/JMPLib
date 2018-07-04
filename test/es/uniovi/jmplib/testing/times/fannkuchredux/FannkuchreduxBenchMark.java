package es.uniovi.jmplib.testing.times.fannkuchredux;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class FannkuchreduxBenchMark extends BenchMark {

    private Test test = null;

    public FannkuchreduxBenchMark(Test test) {
        super();
        this.test = test;
    }

    @Override
    public int runOneIteration() {
        Chronometer chronometer = new Chronometer();
        chronometer.start();
        test.test();
        chronometer.stop();
        this.microSeconds = chronometer.GetMicroSeconds();
        return this.microSeconds;
    }

    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            transaction
                    .addMethod(FannkuchreduxTest.class, new jmplib.reflect.Method("fannkuch",
                            MethodType.methodType(int.class, int.class),
                            "int[] perm = new int[n];"
                                    + "int[] perm1 = new int[n];"
                                    + "int[] count = new int[n];"
                                    + "int maxFlipsCount = 0;"
                                    + "int permCount = 0;"
                                    + "int checksum = 0;"
                                    + "for(int i=0; i<n; i++) perm1[i] = i;"
                                    + "int r = n;"
                                    + "while (true) {"
                                    + "while (r != 1){ count[r-1] = r; r--; }"
                                    + "for(int i=0; i<n; i++) perm[i] = perm1[i];"
                                    + "int flipsCount = 0;"
                                    + "int k;"
                                    + "while ( !((k=perm[0]) == 0) ) {"
                                    + "int k2 = (k+1) >> 1;"
                                    + "for(int i=0; i<k2; i++) {"
                                    + "int temp = perm[i]; perm[i] = perm[k-i]; perm[k-i] = temp;"
                                    + "}"
                                    + "flipsCount++;"
                                    + "}"
                                    + "maxFlipsCount = Math.max(maxFlipsCount, flipsCount);"
                                    + "checksum += permCount%2 == 0 ? flipsCount : -flipsCount;"
                                    + "while (true) {"
                                    + "if (r == n) {"
                                    + "return maxFlipsCount;"
                                    + "}"
                                    + "int perm0 = perm1[0];"
                                    + "int i = 0;"
                                    + "while (i < r) {"
                                    + "int j = i + 1;"
                                    + "perm1[i] = perm1[j];"
                                    + "i = j;"
                                    + "}"
                                    + "perm1[r] = perm0;"
                                    + "count[r] = count[r] - 1;"
                                    + "if (count[r] > 0) break;"
                                    + "r++;"
                                    + "}"
                                    + "permCount++;"
                                    + "}",
                            Modifier.STATIC | Modifier.PUBLIC,
                            new String[]{"n"}));
            transaction.replaceImplementation(FannkuchreduxTest.class, new jmplib.reflect.Method("test",
                    "fannkuch(BenchMark.ITERATIONS);"));

            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
