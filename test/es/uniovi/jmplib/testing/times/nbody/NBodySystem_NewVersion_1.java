package es.uniovi.jmplib.testing.times.nbody;
public class NBodySystem_NewVersion_1 implements jmplib.classversions.VersionClass {
    @jmplib.annotations.NoRedirect
    @jmplib.annotations.AuxiliaryMethod
    private static void _creator(NBodySystem o) { NBodySystem_NewVersion_1 ov =
                                                    null;
                                                  try { ov = (NBodySystem_NewVersion_1)
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
                                                  ov.set_OldVersion(
                                                       o);
                                                  o.set_NewVersion(
                                                      ov);
                                                  o.set_CurrentInstanceVersion(
                                                      o.
                                                        _currentClassVersion);
    }
    public NBodySystem _oldVersion;
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public NBodySystem get_OldVersion() { return _oldVersion; }
    @jmplib.annotations.AuxiliaryMethod
    @jmplib.annotations.NoRedirect
    public void set_OldVersion(Object newValue) { _oldVersion = (NBodySystem)
                                                                  newValue;
    }
    Body[] bodies;
    @jmplib.annotations.AuxiliaryMethod
    static Body[] _bodies_fieldGetter(NBodySystem o) { try { return ((NBodySystem_NewVersion_1)
                                                                       o.
                                                                       get_NewVersion(
                                                                         )).
                                                                      bodies;
                                                       }
                                                       catch (NullPointerException|ClassCastException e) {
                                                           _creator(
                                                             o);
                                                           return ((NBodySystem_NewVersion_1)
                                                                     o.
                                                                     get_NewVersion(
                                                                       )).
                                                                    bodies;
                                                       }
    }
    @jmplib.annotations.AuxiliaryMethod
    static void _bodies_fieldSetter(NBodySystem o,
                                    Body[] value) {
        try {
            ((NBodySystem_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              bodies =
              value;
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((NBodySystem_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              bodies =
              value;
        }
    }
    public void initialize() { bodies = (new Body[] { Body_NewVersion_1.
                                           sun(
                                             ),
                                         Body_NewVersion_1.
                                           jupiter(
                                             ),
                                         Body_NewVersion_1.
                                           saturn(
                                             ),
                                         Body_NewVersion_1.
                                           uranus(
                                             ),
                                         Body_NewVersion_1.
                                           neptune(
                                             ) });
                               double px =
                                 0.0;
                               double py =
                                 0.0;
                               double pz =
                                 0.0;
                               for (int i =
                                      0; i <
                                           bodies.
                                             length;
                                    ++i) {
                                   px +=
                                     Body_NewVersion_1.
                                       _vx_fieldGetter(
                                         bodies[i]) *
                                       Body_NewVersion_1.
                                       _mass_fieldGetter(
                                         bodies[i]);
                                   py +=
                                     Body_NewVersion_1.
                                       _vy_fieldGetter(
                                         bodies[i]) *
                                       Body_NewVersion_1.
                                       _mass_fieldGetter(
                                         bodies[i]);
                                   pz +=
                                     Body_NewVersion_1.
                                       _vz_fieldGetter(
                                         bodies[i]) *
                                       Body_NewVersion_1.
                                       _mass_fieldGetter(
                                         bodies[i]);
                               }
                               Body_NewVersion_1.
                                 _offsetMomentum_invoker(
                                   bodies[0],
                                   px,
                                   py,
                                   pz); }
    @jmplib.annotations.AuxiliaryMethod
    public static void _initialize_invoker(NBodySystem o) {
        try {
            ((NBodySystem_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              initialize(
                );
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((NBodySystem_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              initialize(
                );
        }
    }
    public void advance(double dt) { for (int i =
                                            0;
                                          i <
                                            bodies.
                                              length;
                                          ++i) {
                                         Body iBody =
                                           bodies[i];
                                         for (int j =
                                                i +
                                                1;
                                              j <
                                                bodies.
                                                  length;
                                              ++j) {
                                             double dx =
                                               Body_NewVersion_1.
                                               _x_fieldGetter(
                                                 iBody) -
                                               Body_NewVersion_1.
                                               _x_fieldGetter(
                                                 bodies[j]);
                                             double dy =
                                               Body_NewVersion_1.
                                               _y_fieldGetter(
                                                 iBody) -
                                               Body_NewVersion_1.
                                               _y_fieldGetter(
                                                 bodies[j]);
                                             double dz =
                                               Body_NewVersion_1.
                                               _z_fieldGetter(
                                                 iBody) -
                                               Body_NewVersion_1.
                                               _z_fieldGetter(
                                                 bodies[j]);
                                             double dSquared =
                                               dx *
                                               dx +
                                               dy *
                                               dy +
                                               dz *
                                               dz;
                                             double distance =
                                               Math.
                                               sqrt(
                                                 dSquared);
                                             double mag =
                                               dt /
                                               (dSquared *
                                                  distance);
                                             Body_NewVersion_1.
                                               _vx_fieldSetter(
                                                 iBody,
                                                 Body_NewVersion_1.
                                                   _vx_fieldGetter(
                                                     iBody) -
                                                   dx *
                                                   Body_NewVersion_1.
                                                   _mass_fieldGetter(
                                                     bodies[j]) *
                                                   mag);
                                             Body_NewVersion_1.
                                               _vy_fieldSetter(
                                                 iBody,
                                                 Body_NewVersion_1.
                                                   _vy_fieldGetter(
                                                     iBody) -
                                                   dy *
                                                   Body_NewVersion_1.
                                                   _mass_fieldGetter(
                                                     bodies[j]) *
                                                   mag);
                                             Body_NewVersion_1.
                                               _vz_fieldSetter(
                                                 iBody,
                                                 Body_NewVersion_1.
                                                   _vz_fieldGetter(
                                                     iBody) -
                                                   dz *
                                                   Body_NewVersion_1.
                                                   _mass_fieldGetter(
                                                     bodies[j]) *
                                                   mag);
                                             Body_NewVersion_1.
                                               _vx_fieldSetter(
                                                 bodies[j],
                                                 Body_NewVersion_1.
                                                   _vx_fieldGetter(
                                                     bodies[j]) +
                                                   dx *
                                                   Body_NewVersion_1.
                                                   _mass_fieldGetter(
                                                     iBody) *
                                                   mag);
                                             Body_NewVersion_1.
                                               _vy_fieldSetter(
                                                 bodies[j],
                                                 Body_NewVersion_1.
                                                   _vy_fieldGetter(
                                                     bodies[j]) +
                                                   dy *
                                                   Body_NewVersion_1.
                                                   _mass_fieldGetter(
                                                     iBody) *
                                                   mag);
                                             Body_NewVersion_1.
                                               _vz_fieldSetter(
                                                 bodies[j],
                                                 Body_NewVersion_1.
                                                   _vz_fieldGetter(
                                                     bodies[j]) +
                                                   dz *
                                                   Body_NewVersion_1.
                                                   _mass_fieldGetter(
                                                     iBody) *
                                                   mag);
                                         }
                                     }
                                     for (Body body
                                           :
                                           bodies) {
                                         Body_NewVersion_1.
                                           _x_fieldSetter(
                                             body,
                                             Body_NewVersion_1.
                                               _x_fieldGetter(
                                                 body) +
                                               dt *
                                               Body_NewVersion_1.
                                               _vx_fieldGetter(
                                                 body));
                                         Body_NewVersion_1.
                                           _y_fieldSetter(
                                             body,
                                             Body_NewVersion_1.
                                               _y_fieldGetter(
                                                 body) +
                                               dt *
                                               Body_NewVersion_1.
                                               _vy_fieldGetter(
                                                 body));
                                         Body_NewVersion_1.
                                           _z_fieldSetter(
                                             body,
                                             Body_NewVersion_1.
                                               _z_fieldGetter(
                                                 body) +
                                               dt *
                                               Body_NewVersion_1.
                                               _vz_fieldGetter(
                                                 body));
                                     } }
    @jmplib.annotations.AuxiliaryMethod
    public static void _advance_invoker(NBodySystem o,
                                        double param0) {
        try {
            ((NBodySystem_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              advance(
                param0);
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            ((NBodySystem_NewVersion_1)
               o.
               get_NewVersion(
                 )).
              advance(
                param0);
        }
    }
    public double energy() { double dx;
                             double dy;
                             double dz;
                             double distance;
                             double e = 0.0;
                             for (int i =
                                    0; i <
                                         bodies.
                                           length;
                                  ++i) { Body iBody =
                                           bodies[i];
                                         e +=
                                           0.5 *
                                             Body_NewVersion_1.
                                             _mass_fieldGetter(
                                               iBody) *
                                             (Body_NewVersion_1.
                                                _vx_fieldGetter(
                                                  iBody) *
                                                Body_NewVersion_1.
                                                _vx_fieldGetter(
                                                  iBody) +
                                                Body_NewVersion_1.
                                                _vy_fieldGetter(
                                                  iBody) *
                                                Body_NewVersion_1.
                                                _vy_fieldGetter(
                                                  iBody) +
                                                Body_NewVersion_1.
                                                _vz_fieldGetter(
                                                  iBody) *
                                                Body_NewVersion_1.
                                                _vz_fieldGetter(
                                                  iBody));
                                         for (int j =
                                                i +
                                                1;
                                              j <
                                                bodies.
                                                  length;
                                              ++j) {
                                             Body jBody =
                                               bodies[j];
                                             dx =
                                               Body_NewVersion_1.
                                                 _x_fieldGetter(
                                                   iBody) -
                                                 Body_NewVersion_1.
                                                 _x_fieldGetter(
                                                   jBody);
                                             dy =
                                               Body_NewVersion_1.
                                                 _y_fieldGetter(
                                                   iBody) -
                                                 Body_NewVersion_1.
                                                 _y_fieldGetter(
                                                   jBody);
                                             dz =
                                               Body_NewVersion_1.
                                                 _z_fieldGetter(
                                                   iBody) -
                                                 Body_NewVersion_1.
                                                 _z_fieldGetter(
                                                   jBody);
                                             distance =
                                               Math.
                                                 sqrt(
                                                   dx *
                                                     dx +
                                                     dy *
                                                     dy +
                                                     dz *
                                                     dz);
                                             e -=
                                               Body_NewVersion_1.
                                                 _mass_fieldGetter(
                                                   iBody) *
                                                 Body_NewVersion_1.
                                                 _mass_fieldGetter(
                                                   jBody) /
                                                 distance;
                                         }
                             }
                             return e; }
    @jmplib.annotations.AuxiliaryMethod
    public static double _energy_invoker(NBodySystem o) {
        try {
            return ((NBodySystem_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
              energy(
                );
        }
        catch (NullPointerException|ClassCastException e) {
            _creator(
              o);
            return ((NBodySystem_NewVersion_1)
                      o.
                      get_NewVersion(
                        )).
              energy(
                );
        }
    }
    public NBodySystem_NewVersion_1() { super(
                                          );
    }
}
