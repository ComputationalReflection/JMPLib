package es.uniovi.jmplib.testing.times.nbody;
public class Body_NewVersion_1 implements jmplib.classversions.VersionClass {
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(Body o) { Body_NewVersion_1 ov = null;
                                           try { ov = (Body_NewVersion_1)
                                                        o.
                                                        _createInstance(
                                                          ); }
                                           catch (Exception e) { e.printStackTrace(
                                                                     );
                                           }
                                           Object oldVersion = o.
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
                                           ov.set_OldVersion(o);
                                           o.set_NewVersion(ov);
                                           o.set_CurrentInstanceVersion(
                                               o.
                                                 _currentClassVersion);
    }
    public Body _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public Body get_OldVersion() { return _oldVersion; }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) { _oldVersion = (Body)
                                                                  newValue;
    }
    static final double PI = 3.141592653589793;
    @jmplib.annotations.AuxiliaryMethod
    static double _PI_getter() { return PI; }
    static final double SOLAR_MASS = 4 * PI * PI;
    @jmplib.annotations.AuxiliaryMethod
    static double _SOLAR_MASS_getter() { return SOLAR_MASS; }
    static final double DAYS_PER_YEAR = 365.24;
    @jmplib.annotations.AuxiliaryMethod
    static double _DAYS_PER_YEAR_getter() { return DAYS_PER_YEAR;
    }
    public double x;
    @jmplib.annotations.AuxiliaryMethod
    public static double _x_fieldGetter(Body o) { try { return ((Body_NewVersion_1)
                                                                  o.
                                                                  get_NewVersion(
                                                                    )).
                                                                 x;
                                                  }
                                                  catch (NullPointerException|ClassCastException e) {
                                                      _creator(
                                                        o);
                                                      return ((Body_NewVersion_1)
                                                                o.
                                                                get_NewVersion(
                                                                  )).
                                                               x;
                                                  }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _x_fieldSetter(Body o,
                                      double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              x =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              x =
              value;
        }
    }
    public double y;
    @jmplib.annotations.AuxiliaryMethod
    public static double _y_fieldGetter(Body o) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     y;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     y;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _y_fieldSetter(Body o,
                                      double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              y =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              y =
              value;
        }
    }
    public double z;
    @jmplib.annotations.AuxiliaryMethod
    public static double _z_fieldGetter(Body o) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     z;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     z;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _z_fieldSetter(Body o,
                                      double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              z =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              z =
              value;
        }
    }
    public double vx;
    @jmplib.annotations.AuxiliaryMethod
    public static double _vx_fieldGetter(Body o) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     vx;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     vx;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _vx_fieldSetter(Body o,
                                       double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              vx =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              vx =
              value;
        }
    }
    public double vy;
    @jmplib.annotations.AuxiliaryMethod
    public static double _vy_fieldGetter(Body o) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     vy;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     vy;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _vy_fieldSetter(Body o,
                                       double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              vy =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              vy =
              value;
        }
    }
    public double vz;
    @jmplib.annotations.AuxiliaryMethod
    public static double _vz_fieldGetter(Body o) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     vz;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     vz;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _vz_fieldSetter(Body o,
                                       double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              vz =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              vz =
              value;
        }
    }
    public double mass;
    @jmplib.annotations.AuxiliaryMethod
    public static double _mass_fieldGetter(Body o) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     mass;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
                     mass;
        }
    }
    @jmplib.annotations.AuxiliaryMethod
    public static void _mass_fieldSetter(Body o,
                                         double value) {
        try {
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              mass =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((Body_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              mass =
              value;
        }
    }
    static Body jupiter() { Body p = new Body(
                              );
                            Body_NewVersion_1.
                              _x_fieldSetter(
                                p,
                                4.841431442464721);
                            Body_NewVersion_1.
                              _y_fieldSetter(
                                p,
                                -1.1603200440274284);
                            Body_NewVersion_1.
                              _z_fieldSetter(
                                p,
                                -0.10362204447112311);
                            Body_NewVersion_1.
                              _vx_fieldSetter(
                                p,
                                0.001660076642744037 *
                                  DAYS_PER_YEAR);
                            Body_NewVersion_1.
                              _vy_fieldSetter(
                                p,
                                0.007699011184197404 *
                                  DAYS_PER_YEAR);
                            Body_NewVersion_1.
                              _vz_fieldSetter(
                                p,
                                -6.90460016972063E-5 *
                                  DAYS_PER_YEAR);
                            Body_NewVersion_1.
                              _mass_fieldSetter(
                                p,
                                9.547919384243266E-4 *
                                  SOLAR_MASS);
                            return p; }
    static Body saturn() { Body p = new Body(
                             );
                           Body_NewVersion_1.
                             _x_fieldSetter(
                               p,
                               8.34336671824458);
                           Body_NewVersion_1.
                             _y_fieldSetter(
                               p,
                               4.124798564124305);
                           Body_NewVersion_1.
                             _z_fieldSetter(
                               p,
                               -0.4035234171143214);
                           Body_NewVersion_1.
                             _vx_fieldSetter(
                               p,
                               -0.002767425107268624 *
                                 DAYS_PER_YEAR);
                           Body_NewVersion_1.
                             _vy_fieldSetter(
                               p,
                               0.004998528012349172 *
                                 DAYS_PER_YEAR);
                           Body_NewVersion_1.
                             _vz_fieldSetter(
                               p,
                               2.3041729757376393E-5 *
                                 DAYS_PER_YEAR);
                           Body_NewVersion_1.
                             _mass_fieldSetter(
                               p,
                               2.858859806661308E-4 *
                                 SOLAR_MASS);
                           return p; }
    static Body uranus() { Body p = new Body(
                             );
                           Body_NewVersion_1.
                             _x_fieldSetter(
                               p,
                               12.894369562139131);
                           Body_NewVersion_1.
                             _y_fieldSetter(
                               p,
                               -15.111151401698631);
                           Body_NewVersion_1.
                             _z_fieldSetter(
                               p,
                               -0.22330757889265573);
                           Body_NewVersion_1.
                             _vx_fieldSetter(
                               p,
                               0.002964601375647616 *
                                 DAYS_PER_YEAR);
                           Body_NewVersion_1.
                             _vy_fieldSetter(
                               p,
                               0.0023784717395948095 *
                                 DAYS_PER_YEAR);
                           Body_NewVersion_1.
                             _vz_fieldSetter(
                               p,
                               -2.9658956854023756E-5 *
                                 DAYS_PER_YEAR);
                           Body_NewVersion_1.
                             _mass_fieldSetter(
                               p,
                               4.366244043351563E-5 *
                                 SOLAR_MASS);
                           return p; }
    static Body neptune() { Body p = new Body(
                              );
                            Body_NewVersion_1.
                              _x_fieldSetter(
                                p,
                                15.379697114850917);
                            Body_NewVersion_1.
                              _y_fieldSetter(
                                p,
                                -25.919314609987964);
                            Body_NewVersion_1.
                              _z_fieldSetter(
                                p,
                                0.17925877295037118);
                            Body_NewVersion_1.
                              _vx_fieldSetter(
                                p,
                                0.0026806777249038932 *
                                  DAYS_PER_YEAR);
                            Body_NewVersion_1.
                              _vy_fieldSetter(
                                p,
                                0.001628241700382423 *
                                  DAYS_PER_YEAR);
                            Body_NewVersion_1.
                              _vz_fieldSetter(
                                p,
                                -9.515922545197159E-5 *
                                  DAYS_PER_YEAR);
                            Body_NewVersion_1.
                              _mass_fieldSetter(
                                p,
                                5.1513890204661145E-5 *
                                  SOLAR_MASS);
                            return p; }
    static Body sun() { Body p = new Body(
                          );
                        Body_NewVersion_1.
                          _mass_fieldSetter(
                            p,
                            SOLAR_MASS);
                        return p; }
    public Body offsetMomentum(double px,
                               double py,
                               double pz) {
        vx =
          -px /
            SOLAR_MASS;
        vy =
          -py /
            SOLAR_MASS;
        vz =
          -pz /
            SOLAR_MASS;
        return this.
          get_OldVersion(
            );
    }
    @jmplib.annotations.AuxiliaryMethod
    public static Body _offsetMomentum_invoker(Body o,
                                               double param0,
                                               double param1,
                                               double param2) {
        try {
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
              offsetMomentum(
                param0,
                param1,
                param2);
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((Body_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
              offsetMomentum(
                param0,
                param1,
                param2);
        }
    }
    public Body_NewVersion_1() { super();
    }
}
