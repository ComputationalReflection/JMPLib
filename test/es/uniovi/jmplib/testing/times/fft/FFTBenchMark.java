package es.uniovi.jmplib.testing.times.fft;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.Chronometer;
import es.uniovi.jmplib.testing.times.Test;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class FFTBenchMark extends BenchMark {

    public FFTBenchMark(Test test) {
        super(test);
    }

    @Override
    public int runOneIteration() {
        Chronometer chronometer = new Chronometer();
        FFT test = new FFT();
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
            transaction.addMethod(FFT.class, new jmplib.reflect.Method("transform",
                    MethodType.methodType(void.class, double[].class),
                    "transform_internal(data, -1);", Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"data"}));
            transaction.addMethod(FFT.class, new jmplib.reflect.Method("log2",
                    MethodType.methodType(int.class, int.class),
                    "int log = 0;"
                            + "for (int k = 1; k < n; k *= 2, log++)"
                            + ";"
                            + "if (n != (1 << log))"
                            + "throw new Error(\"FFT: Data length is not a power of 2!: \" + n);"
                            + "return log;",
                    Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"n"}));
            transaction.addMethod(FFT.class, new jmplib.reflect.Method("inverse",
                    MethodType.methodType(void.class, double[].class),
                    "transform_internal(data, +1);"
                            + "int nd=data.length;"
                            + "int n =nd/2;"
                            + "double norm=1/((double) n);"
                            + "for(int i=0; i<nd; i++)"
                            + "data[i] *= norm;",
                    Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"data"}));
            transaction.addMethod(FFT.class, new jmplib.reflect.Method("transform_internal",
                    MethodType.methodType(void.class, double[].class, int.class),
                    "if (data.length == 0)"
                            + "return;"
                            + "int n = data.length / 2;"
                            + "if (n == 1)"
                            + "return;"
                            + "int logn = log2(n);"
                            + "bitreverse(data);"
                            + "for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {"
                            + "double w_real = 1.0;"
                            + "double w_imag = 0.0;"
                            + "double theta = 2.0 * direction * Math.PI / (2.0 * (double) dual);"
                            + "double s = Math.sin(theta);"
                            + "double t = Math.sin(theta / 2.0);"
                            + "double s2 = 2.0 * t * t;"
                            + "for (int b = 0; b < n; b += 2 * dual) {"
                            + "int i = 2 * b;"
                            + "int j = 2 * (b + dual);"
                            + "double wd_real = data[j];"
                            + "double wd_imag = data[j + 1];"
                            + "data[j] = data[i] - wd_real;"
                            + "data[j + 1] = data[i + 1] - wd_imag;"
                            + "data[i] += wd_real;"
                            + "data[i + 1] += wd_imag;"
                            + "}"
                            + "for (int a = 1; a < dual; a++) {"
                            + "double tmp_real = w_real - s * w_imag - s2 * w_real;"
                            + "double tmp_imag = w_imag + s * w_real - s2 * w_imag;"
                            + "w_real = tmp_real;"
                            + "w_imag = tmp_imag;"
                            + "for (int b = 0; b < n; b += 2 * dual) {"
                            + "int i = 2 * (b + a);"
                            + "int j = 2 * (b + a + dual);"
                            + "double z1_real = data[j];"
                            + "double z1_imag = data[j + 1];"
                            + "double wd_real = w_real * z1_real - w_imag * z1_imag;"
                            + "double wd_imag = w_real * z1_imag + w_imag * z1_real;"
                            + "data[j] = data[i] - wd_real;"
                            + "data[j + 1] = data[i + 1] - wd_imag;"
                            + "data[i] += wd_real;"
                            + "data[i + 1] += wd_imag;"
                            + "}"
                            + "}"
                            + "}",
                    Modifier.PROTECTED | Modifier.STATIC,
                    new String[]{"data", "direction"}));
            transaction.addMethod(FFT.class, new jmplib.reflect.Method("bitreverse",
                    MethodType.methodType(void.class, double[].class),
                    "int n = data.length / 2;"
                            + "int nm1 = n - 1;"
                            + "int i = 0;"
                            + "int j = 0;"
                            + "for (; i < nm1; i++) {"
                            + "int ii = i << 1;"
                            + "int jj = j << 1;"
                            + "int k = n >> 1;"
                            + "if (i < j) {"
                            + "double tmp_real = data[ii];"
                            + "double tmp_imag = data[ii + 1];"
                            + "data[ii] = data[jj];"
                            + "data[ii + 1] = data[jj + 1];"
                            + "data[jj] = tmp_real;"
                            + "data[jj + 1] = tmp_imag;"
                            + "}"
                            + "while (k <= j) {"
                            + "j -= k;"
                            + "k >>= 1;"
                            + "}"
                            + "j += k;"
                            + "}",
                    Modifier.PROTECTED | Modifier.STATIC,
                    new String[]{"data"}));
            transaction.addMethod(FFT.class, new jmplib.reflect.Method("randomVector",
                    MethodType.methodType(double[].class, int.class),
                    "Random r = new Random();"
                            + "double A[] = new double[N];"
                            + "for (int i = 0; i < N; i++)"
                            + "A[i] = r.nextDouble();"
                            + "return A;",
                    Modifier.PRIVATE | Modifier.STATIC,
                    new String[]{"N"}));
            transaction.replaceImplementation(FFT.class, new jmplib.reflect.Method("test",
                    "double x[] = randomVector(2 * BenchMark.DIN);"
                            + "for (int i = 0; i < BenchMark.ITERATIONS; i++) {"
                            + "FFT.transform(x);"
                            + "FFT.inverse(x);"
                            + "}"));
            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
