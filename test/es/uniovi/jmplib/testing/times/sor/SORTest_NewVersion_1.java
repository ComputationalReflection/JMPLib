package es.uniovi.jmplib.testing.times.sor;
public class SORTest_NewVersion_1 extends Test_NewVersion_1 implements jmplib.classversions.VersionClass {
    private double[][] matrix = null;
    public SORTest_NewVersion_1(double[][] matrix) { super();
                                                     this.matrix = matrix;
    }
    @Override
    public void test() { execute(1.25, matrix, BenchMark._ITERATIONS_getter(
                                                           )); }
    @jmplib.annotations.AuxiliaryMethod
    private static double[][] _matrix_fieldGetter(SORTest o) { try { return ((SORTest_NewVersion_1)
                                                                               o.
                                                                               get_NewVersion(
                                                                                 )).
                                                                              matrix;
                                                               }
                                                               catch (NullPointerException|ClassCastException e) {
                                                                   _creator(
                                                                     o);
                                                                   return ((SORTest_NewVersion_1)
                                                                             o.
                                                                             get_NewVersion(
                                                                               )).
                                                                            matrix;
                                                               }
    }
    @jmplib.annotations.AuxiliaryMethod
    private static void _matrix_fieldSetter(SORTest o,
                                            double[][] value) {
        try {
            ((SORTest_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              matrix =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((SORTest_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              matrix =
              value;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _test_invoker(SORTest o) {
        try {
            ((SORTest_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              test(
                );
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((SORTest_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              test(
                );
        }
    }
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(SORTest o) {
        SORTest_NewVersion_1 ov =
          null;
        try {
            ov =
              (SORTest_NewVersion_1)
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
    public SORTest_NewVersion_1() { super(
                                      ); }
    public SORTest _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public SORTest get_OldVersion() { return _oldVersion;
    }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) {
        _oldVersion =
          (SORTest)
            newValue;
    }
    public static final double num_flops(int N,
                                         int M,
                                         int num_iterations) {
        double Md =
          (double)
            M;
        double Nd =
          (double)
            N;
        double num_iterD =
          (double)
            num_iterations;
        return (Md -
                  1) *
          (Nd -
             1) *
          num_iterD *
          6.0;
    }
    public static final void execute(double omega,
                                     double[][] G,
                                     int num_iterations) {
        int M =
          G.
            length;
        int N =
          G[0].
            length;
        double omega_over_four =
          omega *
          0.25;
        double one_minus_omega =
          1.0 -
          omega;
        int Mm1 =
          M -
          1;
        int Nm1 =
          N -
          1;
        for (int p =
               0;
             p <
               num_iterations;
             p++) {
            for (int i =
                   1;
                 i <
                   Mm1;
                 i++) {
                double[] Gi =
                  G[i];
                double[] Gim1 =
                  G[i -
                      1];
                double[] Gip1 =
                  G[i +
                      1];
                for (int j =
                       1;
                     j <
                       Nm1;
                     j++) {
                    Gi[j] =
                      omega_over_four *
                        (Gim1[j] +
                           Gip1[j] +
                           Gi[j -
                                1] +
                           Gi[j +
                                1]) +
                        one_minus_omega *
                        Gi[j];
                }
            }
        }
    }
}
