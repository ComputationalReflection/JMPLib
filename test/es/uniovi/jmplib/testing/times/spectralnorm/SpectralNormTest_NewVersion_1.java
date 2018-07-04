package es.uniovi.jmplib.testing.times.spectralnorm;
public class SpectralNormTest_NewVersion_1 extends Test_NewVersion_1 implements jmplib.classversions.VersionClass {
    @Override
    public void test() { spectralnorm(BenchMark._ITERATIONS_getter()); }
    @jmplib.annotations.AuxiliaryMethod
    public static void _test_invoker(SpectralNormTest o) { if (o.get_CurrentInstanceVersion(
                                                                   ) !=
                                                                 SpectralNormTest.
                                                                   _currentClassVersion) {
                                                               _creator(
                                                                 o);
                                                           }
                                                           ((SpectralNormTest_NewVersion_1)
                                                              o.
                                                              get_NewVersion(
                                                                )).
                                                             test(
                                                               );
    }
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(SpectralNormTest o) {
        SpectralNormTest_NewVersion_1 ov =
          null;
        try {
            ov =
              (SpectralNormTest_NewVersion_1)
                o.
                _createInstance(
                  );
        }
        catch (Exception e) {
            e.
              printStackTrace(
                );
        }
        Object oldVersion =
          o.
          get_NewVersion(
            ) ==
          null
          ? o
          : o.
          get_NewVersion(
            );
        jmplib.util.TransferState.
          transferState(
            oldVersion,
            ov);
        ov.
          set_OldVersion(
            o);
        o.
          set_NewVersion(
            ov);
        o.
          set_CurrentInstanceVersion(
            o.
              _currentClassVersion);
    }
    public SpectralNormTest _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public SpectralNormTest get_OldVersion() {
        return _oldVersion;
    }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) {
        _oldVersion =
          (SpectralNormTest)
            newValue;
    }
    private static final double spectralnorm(int n) {
        double[] u =
          new double[n];
        double[] v =
          new double[n];
        double[] w =
          new double[n];
        double vv =
          0;
        double vBv =
          0;
        for (int i =
               0;
             i <
               n;
             i++) {
            u[i] =
              1.0;
            v[i] =
              0.0;
            w[i] =
              0.0;
        }
        for (int i =
               0;
             i <
               10;
             i++) {
            AtAu(
              u,
              v,
              w);
            AtAu(
              v,
              u,
              w);
        }
        for (int i =
               0;
             i <
               n;
             i++) {
            vBv +=
              u[i] *
                v[i];
            vv +=
              v[i] *
                v[i];
        }
        return Math.
          sqrt(
            vBv /
              vv);
    }
    private static final double A(int i, int j) {
        int div =
          ((i +
              j) *
             (i +
                j +
                1) >>>
             1) +
          i +
          1;
        return 1.0 /
          div;
    }
    private static final void Au(double[] u,
                                 double[] v) {
        for (int i =
               0;
             i <
               u.
                 length;
             i++) {
            double sum =
              0;
            for (int j =
                   0;
                 j <
                   u.
                     length;
                 j++) {
                sum +=
                  A(
                    i,
                    j) *
                    u[j];
            }
            v[i] =
              sum;
        }
    }
    private static final void Atu(double[] u,
                                  double[] v) {
        for (int i =
               0;
             i <
               u.
                 length;
             i++) {
            double sum =
              0;
            for (int j =
                   0;
                 j <
                   u.
                     length;
                 j++) {
                sum +=
                  A(
                    j,
                    i) *
                    u[j];
            }
            v[i] =
              sum;
        }
    }
    private static final void AtAu(double[] u,
                                   double[] v,
                                   double[] w) {
        Au(
          u,
          w);
        Atu(
          w,
          v);
    }
    public SpectralNormTest_NewVersion_1() {
        super(
          );
    }
}
