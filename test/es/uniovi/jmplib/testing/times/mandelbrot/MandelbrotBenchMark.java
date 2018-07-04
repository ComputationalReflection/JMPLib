package es.uniovi.jmplib.testing.times.mandelbrot;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

public class MandelbrotBenchMark extends BenchMark {

    private Test test = null;

    public MandelbrotBenchMark(Test test) {
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
            transaction.addField(MandelbrotTest.class, new jmplib.reflect.Field(Modifier.STATIC,
                    byte[][].class, "out"));
            transaction.addField(MandelbrotTest.class, new jmplib.reflect.Field(Modifier.STATIC,
                    double[].class, "Crb"));
            transaction.addField(MandelbrotTest.class, new jmplib.reflect.Field(Modifier.STATIC,
                    double[].class, "Cib"));
            transaction.addField(MandelbrotTest.class, new jmplib.reflect.Field(Modifier.STATIC,
                    AtomicInteger.class, "yCt"));

            transaction.addMethod(MandelbrotTest.class, new jmplib.reflect.Method("getByte",
                    MethodType.methodType(int.class, int.class, int.class),
                    "int res=0;"
                            + "for(int i=0;i<8;i+=2){"
                            + "double Zr1=Crb[x+i];"
                            + "double Zi1=Cib[y];"
                            + "double Zr2=Crb[x+i+1];"
                            + "double Zi2=Cib[y];"
                            + "int b=0;"
                            + "int j=49;"
                            + "do{"
                            + "double nZr1=Zr1*Zr1-Zi1*Zi1+Crb[x+i];"
                            + "double nZi1=Zr1*Zi1+Zr1*Zi1+Cib[y];"
                            + "Zr1=nZr1;Zi1=nZi1;"
                            + "double nZr2=Zr2*Zr2-Zi2*Zi2+Crb[x+i+1];"
                            + "double nZi2=Zr2*Zi2+Zr2*Zi2+Cib[y];"
                            + "Zr2=nZr2;Zi2=nZi2;"
                            + "if(Zr1*Zr1+Zi1*Zi1>4){b|=2;if(b==3)break;}"
                            + "if(Zr2*Zr2+Zi2*Zi2>4){b|=1;if(b==3)break;}"
                            + "}while(--j>0);"
                            + "res=(res<<2)+b;"
                            + "}"
                            + "return res^-1;",
                    Modifier.STATIC,
                    "x", "y"));

            transaction.addMethod(MandelbrotTest.class, new jmplib.reflect.Method("putLine",
                    MethodType.methodType(void.class, int.class, byte[].class),
                    "for (int xb=0; xb<line.length; xb++) "
                            + "line[xb]=(byte)getByte(xb*8,y);",
                    Modifier.STATIC,
                    "y", "line"));

            transaction.replaceImplementation(MandelbrotTest.class, new jmplib.reflect.Method("test",
                    "int N = BenchMark.ITERATIONS;"
                            + "Crb=new double[N+7]; Cib=new double[N+7];"
                            + "double invN=2.0/N; for(int i=0;i<N;i++){ Cib[i]=i*invN-1.0; Crb[i]=i*invN-1.5; }"
                            + "yCt=new java.util.concurrent.atomic.AtomicInteger();"
                            + "out=new byte[N][(N+7)/8];"
                            + "int y; while((y=yCt.getAndIncrement())<out.length) putLine(y,out[y]);"));

            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
