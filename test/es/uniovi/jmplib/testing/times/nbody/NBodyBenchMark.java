package es.uniovi.jmplib.testing.times.nbody;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class NBodyBenchMark extends BenchMark {

    public NBodyBenchMark(Test test) {
        super(test);
    }


    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            // NBody
            transaction.replaceImplementation(NBody.class, new jmplib.reflect.Method("test",
                    "NBodySystem bodies = new NBodySystem();"
                            + "bodies.initialize();"
                            + "for (int i = 0; i < BenchMark.ITERATIONS; ++i)"
                            + "bodies.advance(0.01);"));

            // Body
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.FINAL | Modifier.STATIC,
                    double.class, "PI", "3.141592653589793"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.FINAL | Modifier.STATIC,
                    double.class, "SOLAR_MASS", "4 * PI * PI"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.FINAL | Modifier.STATIC,
                    double.class, "DAYS_PER_YEAR", "365.24"));

            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "x"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "y"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "z"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "vx"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "vy"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "vz"));
            transaction.addField(Body.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "mass"));

            transaction.addMethod(Body.class, new jmplib.reflect.Method("jupiter", MethodType.methodType(Body.class),
                    "Body p = new Body();"
                            + "p.x = 4.84143144246472090e+00;"
                            + "p.y = -1.16032004402742839e+00;"
                            + "p.z = -1.03622044471123109e-01;"
                            + "p.vx = 1.66007664274403694e-03 * DAYS_PER_YEAR;"
                            + "p.vy = 7.69901118419740425e-03 * DAYS_PER_YEAR;"
                            + "p.vz = -6.90460016972063023e-05 * DAYS_PER_YEAR;"
                            + "p.mass = 9.54791938424326609e-04 * SOLAR_MASS;"
                            + "return p;",
                    Modifier.STATIC));
            transaction.addMethod(Body.class, new jmplib.reflect.Method("saturn", MethodType.methodType(Body.class),
                    "Body p = new Body();"
                            + "p.x = 8.34336671824457987e+00;"
                            + "p.y = 4.12479856412430479e+00;"
                            + "p.z = -4.03523417114321381e-01;"
                            + "p.vx = -2.76742510726862411e-03 * DAYS_PER_YEAR;"
                            + "p.vy = 4.99852801234917238e-03 * DAYS_PER_YEAR;"
                            + "p.vz = 2.30417297573763929e-05 * DAYS_PER_YEAR;"
                            + "p.mass = 2.85885980666130812e-04 * SOLAR_MASS;"
                            + "return p;",
                    Modifier.STATIC));
            transaction.addMethod(Body.class, new jmplib.reflect.Method("uranus", MethodType.methodType(Body.class),
                    "Body p = new Body();"
                            + "p.x = 1.28943695621391310e+01;"
                            + "p.y = -1.51111514016986312e+01;"
                            + "p.z = -2.23307578892655734e-01;"
                            + "p.vx = 2.96460137564761618e-03 * DAYS_PER_YEAR;"
                            + "p.vy = 2.37847173959480950e-03 * DAYS_PER_YEAR;"
                            + "p.vz = -2.96589568540237556e-05 * DAYS_PER_YEAR;"
                            + "p.mass = 4.36624404335156298e-05 * SOLAR_MASS;"
                            + "return p;",
                    Modifier.STATIC));
            transaction.addMethod(Body.class, new jmplib.reflect.Method("neptune", MethodType.methodType(Body.class),
                    "Body p = new Body();"
                            + "p.x = 1.53796971148509165e+01;"
                            + "p.y = -2.59193146099879641e+01;"
                            + "p.z = 1.79258772950371181e-01;"
                            + "p.vx = 2.68067772490389322e-03 * DAYS_PER_YEAR;"
                            + "p.vy = 1.62824170038242295e-03 * DAYS_PER_YEAR;"
                            + "p.vz = -9.51592254519715870e-05 * DAYS_PER_YEAR;"
                            + "p.mass = 5.15138902046611451e-05 * SOLAR_MASS;"
                            + "return p;",
                    Modifier.STATIC));
            transaction.addMethod(Body.class, new jmplib.reflect.Method("sun", MethodType.methodType(Body.class),
                    "Body p = new Body();"
                            + "p.mass = SOLAR_MASS;"
                            + "return p;",
                    Modifier.STATIC));
            transaction.addMethod(Body.class, new jmplib.reflect.Method("offsetMomentum",
                    MethodType.methodType(Body.class, double.class, double.class, double.class),
                    "vx = -px / SOLAR_MASS;"
                            + "vy = -py / SOLAR_MASS;"
                            + "vz = -pz / SOLAR_MASS;"
                            + "return this;",
                    new String[]{"px", "py", "pz"}));

            //NBodySystem
            transaction.addField(NBodySystem.class, new jmplib.reflect.Field(Body[].class, "bodies"));
            transaction.addMethod(NBodySystem.class, new jmplib.reflect.Method("initialize", MethodType.methodType(void.class),
                    "bodies = new Body[] { Body.sun(), Body.jupiter(), Body.saturn(),"
                            + "Body.uranus(), Body.neptune() };"
                            + "double px = 0.0;"
                            + "double py = 0.0;"
                            + "double pz = 0.0;"
                            + "for (int i = 0; i < bodies.length; ++i) {"
                            + "	px += bodies[i].vx * bodies[i].mass;"
                            + "	py += bodies[i].vy * bodies[i].mass;"
                            + "	pz += bodies[i].vz * bodies[i].mass;"
                            + "}"
                            + "bodies[0].offsetMomentum(px, py, pz);"));
            transaction.addMethod(NBodySystem.class, new jmplib.reflect.Method("advance",
                    MethodType.methodType(void.class, double.class),
                    "for (int i = 0; i < bodies.length; ++i) {"
                            + "Body iBody = bodies[i];"
                            + "for (int j = i + 1; j < bodies.length; ++j) {"
                            + "double dx = iBody.x - bodies[j].x;"
                            + "double dy = iBody.y - bodies[j].y;"
                            + "double dz = iBody.z - bodies[j].z;"
                            + "double dSquared = dx * dx + dy * dy + dz * dz;"
                            + "double distance = Math.sqrt(dSquared);"
                            + "double mag = dt / (dSquared * distance);"
                            + "iBody.vx -= dx * bodies[j].mass * mag;"
                            + "iBody.vy -= dy * bodies[j].mass * mag;"
                            + "iBody.vz -= dz * bodies[j].mass * mag;"
                            + "	bodies[j].vx += dx * iBody.mass * mag;"
                            + "	bodies[j].vy += dy * iBody.mass * mag;"
                            + "bodies[j].vz += dz * iBody.mass * mag;"
                            + "	}"
                            + "	}"
                            + "	for (Body body : bodies) {"
                            + "		body.x += dt * body.vx;"
                            + "	body.y += dt * body.vy;"
                            + "	body.z += dt * body.vz;"
                            + "}",
                    new String[]{"dt"}));
            transaction.addMethod(NBodySystem.class, new jmplib.reflect.Method("energy", MethodType.methodType(double.class),
                    "double dx, dy, dz, distance;"
                            + "double e = 0.0;"
                            + "	for (int i = 0; i < bodies.length; ++i) {"
                            + "	Body iBody = bodies[i];"
                            + "	e += 0.5"
                            + "	* iBody.mass"
                            + "	* (iBody.vx * iBody.vx + iBody.vy * iBody.vy + iBody.vz"
                            + "	* iBody.vz);"
                            + "	for (int j = i + 1; j < bodies.length; ++j) {"
                            + "	Body jBody = bodies[j];"
                            + "	dx = iBody.x - jBody.x;"
                            + "	dy = iBody.y - jBody.y;"
                            + "	dz = iBody.z - jBody.z;"
                            + "	distance = Math.sqrt(dx * dx + dy * dy + dz * dz);"
                            + "	e -= (iBody.mass * jBody.mass) / distance;"
                            + "}"
                            + "}"
                            + "return e;"));


            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
