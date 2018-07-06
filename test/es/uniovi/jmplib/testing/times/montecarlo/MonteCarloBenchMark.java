package es.uniovi.jmplib.testing.times.montecarlo;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.Test;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class MonteCarloBenchMark extends BenchMark {
    public MonteCarloBenchMark(Test test) {
        super(test);
    }


    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            transaction.addField(MonteCarlo.class, new jmplib.reflect.Field(Modifier.STATIC
                    | Modifier.FINAL, int.class, "SEED", "113"));
            transaction
                    .addMethod(
                            MonteCarlo.class,
                            new jmplib.reflect.Method("integrate",
                                    MethodType.methodType(double.class, int.class),
                                    "Random R = new Random(SEED);"
                                            + "int under_curve = 0;"
                                            + "for (int count=0; count<Num_samples; count++){"
                                            + "double x= R.nextDouble();"
                                            + "double y= R.nextDouble();"
                                            + "if ( x*x + y*y <= 1.0)"
                                            + "under_curve ++;"
                                            + "}"
                                            + "return ((double) under_curve / Num_samples) * 4.0;",
                                    Modifier.FINAL | Modifier.STATIC | Modifier.PUBLIC,
                                    new String[]{"Num_samples"}));
            transaction.replaceImplementation(MonteCarlo.class, new jmplib.reflect.Method("test",
                    "integrate(BenchMark.ITERATIONS);"));
            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
