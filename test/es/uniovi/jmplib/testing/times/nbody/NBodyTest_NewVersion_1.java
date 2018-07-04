package es.uniovi.jmplib.testing.times.nbody;
public class NBodyTest_NewVersion_1 extends Test_NewVersion_1 implements jmplib.classversions.VersionClass {
    @Override
    public void test() { NBodySystem bodies = new NBodySystem();
                         NBodySystem_NewVersion_1._initialize_invoker(bodies);
                         for (int i = 0; i < BenchMark._ITERATIONS_getter(
                                                         ); ++i) NBodySystem_NewVersion_1.
                                                                   _advance_invoker(
                                                                     bodies,
                                                                     0.01);
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _test_invoker(NBodyTest o) { try {
                                                        ((NBodyTest_NewVersion_1)
                                                           o.
                                                           get_NewVersion(
                                                             )).
                                                          test(
                                                            );
                                                    }
                                                    catch (NullPointerException|ClassCastException e) {
                                                        _creator(
                                                          o);
                                                        ((NBodyTest_NewVersion_1)
                                                           o.
                                                           get_NewVersion(
                                                             )).
                                                          test(
                                                            );
                                                    }
    }
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(NBodyTest o) {
        NBodyTest_NewVersion_1 ov =
          null;
        try {
            ov =
              (NBodyTest_NewVersion_1)
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
    public NBodyTest _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public NBodyTest get_OldVersion() { return _oldVersion;
    }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) {
        _oldVersion =
          (NBodyTest)
            newValue;
    }
    public NBodyTest_NewVersion_1() { super(
                                        );
    }
}
