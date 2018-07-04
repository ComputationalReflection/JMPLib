package es.uniovi.jmplib.testing.times.mandelbrot;
public abstract class Test_NewVersion_1 implements jmplib.classversions.VersionClass {
    public abstract void test();
    @jmplib.annotations.AuxiliaryMethod
    public static void _test_invoker(Test o) { try { ((Test_NewVersion_1)
                                                        o.
                                                        get_NewVersion(
                                                          )).test(); }
                                               catch (NullPointerException|ClassCastException e) {
                                                   _creator(
                                                     o);
                                                   ((Test_NewVersion_1)
                                                      o.
                                                      get_NewVersion(
                                                        )).
                                                     test(
                                                       );
                                               } }
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(Test o) { Test_NewVersion_1 ov =
                                             null;
                                           try { ov = (Test_NewVersion_1)
                                                        o.
                                                        _createInstance(
                                                          ); }
                                           catch (Exception e) { e.
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
                                           ov.set_OldVersion(
                                                o);
                                           o.set_NewVersion(
                                               ov);
                                           o.set_CurrentInstanceVersion(
                                               o.
                                                 _currentClassVersion);
    }
    public Test _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public Test get_OldVersion() { return _oldVersion; }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) { _oldVersion =
                                                    (Test)
                                                      newValue;
    }
    public Test_NewVersion_1() { super(); }
}
