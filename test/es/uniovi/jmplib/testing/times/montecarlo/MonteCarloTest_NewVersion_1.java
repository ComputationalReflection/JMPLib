package es.uniovi.jmplib.testing.times.montecarlo;
public class MonteCarloTest_NewVersion_1 extends Test_NewVersion_1 implements jmplib.classversions.VersionClass {
    @Override
    public void test() { integrate(BenchMark._ITERATIONS_getter()); }
    @jmplib.annotations.AuxiliaryMethod
    public static void _test_invoker(MonteCarloTest o) { try { ((MonteCarloTest_NewVersion_1)
                                                                  o.
                                                                  get_NewVersion(
                                                                    )).
                                                                 test(
                                                                   );
                                                         }
                                                         catch (NullPointerException|ClassCastException e) {
                                                             _creator(
                                                               o);
                                                             ((MonteCarloTest_NewVersion_1)
                                                                o.
                                                                get_NewVersion(
                                                                  )).
                                                               test(
                                                                 );
                                                         }
    }
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(MonteCarloTest o) {
        MonteCarloTest_NewVersion_1 ov =
          null;
        try {
            ov =
              (MonteCarloTest_NewVersion_1)
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
    public MonteCarloTest _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public MonteCarloTest get_OldVersion() {
        return _oldVersion;
    }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) {
        _oldVersion =
          (MonteCarloTest)
            newValue;
    }
    static final int SEED = 113;
    @jmplib.annotations.AuxiliaryMethod
    static int _SEED_getter() { return SEED;
    }
    public static final double integrate(int Num_samples) {
        Random R =
          new Random(
          SEED);
        int under_curve =
          0;
        for (int count =
               0;
             count <
               Num_samples;
             count++) {
            double x =
              R.
              nextDouble(
                );
            double y =
              R.
              nextDouble(
                );
            if (x *
                  x +
                  y *
                  y <=
                  1.0)
                under_curve++;
        }
        return (double)
                 under_curve /
          Num_samples *
          4.0;
    }
    public MonteCarloTest_NewVersion_1() {
        super(
          );
    }
}
