package es.uniovi.jmplib.testing.times.mandelbrot;
public class MandelbrotTest_NewVersion_1 extends Test_NewVersion_1 implements jmplib.classversions.VersionClass {
    @Override
    public void test() { int N = BenchMark._ITERATIONS_getter();
                         Crb = (new double[N + 7]);
                         Cib = (new double[N + 7]);
                         double invN = 2.0 / N;
                         for (int i = 0; i < N; i++) { Cib[i] = i * invN -
                                                                  1.0;
                                                       Crb[i] = i * invN -
                                                                  1.5; }
                         yCt = new java.util.concurrent.atomic.AtomicInteger(
                                 );
                         out = (new byte[N][(N + 7) / 8]);
                         int y;
                         while ((y = yCt.getAndIncrement()) < out.length)
                             putLine(
                               y,
                               out[y]); }
    @jmplib.annotations.AuxiliaryMethod
    public static void _test_invoker(MandelbrotTest o) { try { ((MandelbrotTest_NewVersion_1)
                                                                  o.
                                                                  get_NewVersion(
                                                                    )).
                                                                 test(
                                                                   );
                                                         }
                                                         catch (NullPointerException|ClassCastException e) {
                                                             _creator(
                                                               o);
                                                             ((MandelbrotTest_NewVersion_1)
                                                                o.
                                                                get_NewVersion(
                                                                  )).
                                                               test(
                                                                 );
                                                         }
    }
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(MandelbrotTest o) {
        MandelbrotTest_NewVersion_1 ov =
          null;
        try {
            ov =
              (MandelbrotTest_NewVersion_1)
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
    public MandelbrotTest _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public MandelbrotTest get_OldVersion() {
        return _oldVersion;
    }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) {
        _oldVersion =
          (MandelbrotTest)
            newValue;
    }
    static byte[][] out;
    @jmplib.annotations.AuxiliaryMethod
    static byte[][] _out_getter() { return out;
    }
    @jmplib.annotations.AuxiliaryMethod
    static void _out_setter(byte[][] newValue) {
        out =
          newValue;
    }
    static double[] Crb;
    @jmplib.annotations.AuxiliaryMethod
    static double[] _Crb_getter() { return Crb;
    }
    @jmplib.annotations.AuxiliaryMethod
    static void _Crb_setter(double[] newValue) {
        Crb =
          newValue;
    }
    static double[] Cib;
    @jmplib.annotations.AuxiliaryMethod
    static double[] _Cib_getter() { return Cib;
    }
    @jmplib.annotations.AuxiliaryMethod
    static void _Cib_setter(double[] newValue) {
        Cib =
          newValue;
    }
    static java.util.concurrent.atomic.AtomicInteger
      yCt;
    @jmplib.annotations.AuxiliaryMethod
    static java.util.concurrent.atomic.AtomicInteger _yCt_getter() {
        return yCt;
    }
    @jmplib.annotations.AuxiliaryMethod
    static void _yCt_setter(java.util.concurrent.atomic.AtomicInteger newValue) {
        yCt =
          newValue;
    }
    static int getByte(int x, int y) { int res =
                                         0;
                                       for (int i =
                                              0;
                                            i <
                                              8;
                                            i +=
                                              2) {
                                           double Zr1 =
                                             Crb[x +
                                                   i];
                                           double Zi1 =
                                             Cib[y];
                                           double Zr2 =
                                             Crb[x +
                                                   i +
                                                   1];
                                           double Zi2 =
                                             Cib[y];
                                           int b =
                                             0;
                                           int j =
                                             49;
                                           do  {
                                               double nZr1 =
                                                 Zr1 *
                                                 Zr1 -
                                                 Zi1 *
                                                 Zi1 +
                                                 Crb[x +
                                                       i];
                                               double nZi1 =
                                                 Zr1 *
                                                 Zi1 +
                                                 Zr1 *
                                                 Zi1 +
                                                 Cib[y];
                                               Zr1 =
                                                 nZr1;
                                               Zi1 =
                                                 nZi1;
                                               double nZr2 =
                                                 Zr2 *
                                                 Zr2 -
                                                 Zi2 *
                                                 Zi2 +
                                                 Crb[x +
                                                       i +
                                                       1];
                                               double nZi2 =
                                                 Zr2 *
                                                 Zi2 +
                                                 Zr2 *
                                                 Zi2 +
                                                 Cib[y];
                                               Zr2 =
                                                 nZr2;
                                               Zi2 =
                                                 nZi2;
                                               if (Zr1 *
                                                     Zr1 +
                                                     Zi1 *
                                                     Zi1 >
                                                     4) {
                                                   b |=
                                                     2;
                                                   if (b ==
                                                         3)
                                                       break;
                                               }
                                               if (Zr2 *
                                                     Zr2 +
                                                     Zi2 *
                                                     Zi2 >
                                                     4) {
                                                   b |=
                                                     1;
                                                   if (b ==
                                                         3)
                                                       break;
                                               }
                                           }while(--j >
                                                    0); 
                                           res =
                                             (res <<
                                                2) +
                                               b;
                                       }
                                       return res ^
                                         -1;
    }
    static void putLine(int y, byte[] line) {
        for (int xb =
               0;
             xb <
               line.
                 length;
             xb++)
            line[xb] =
              (byte)
                getByte(
                  xb *
                    8,
                  y);
    }
    public MandelbrotTest_NewVersion_1() {
        super(
          );
    }
}
