package es.uniovi.jmplib.testing.times.raytracer;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class RayTracerBenchMark extends BenchMark {

    public RayTracerBenchMark(Test test) {
        super(test);
    }

    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            List<jmplib.reflect.Field> fields = new ArrayList<jmplib.reflect.Field>();
            List<jmplib.reflect.Method> methods = new ArrayList<jmplib.reflect.Method>();

            // Isect
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "enter"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "t"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Primitive.class, "prim"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Surface.class, "surf"));

            transaction.addField(Isect.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Isect.class, methods.toArray(new jmplib.reflect.Method[0]));

            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();

            // Primitive
            fields.add(new jmplib.reflect.Field(Primitive.class, Modifier.PUBLIC, Surface.class, "surf",
                    "new Surface();"));
            methods.add(new jmplib.reflect.Method("setColor",
                    MethodType.methodType(void.class, double.class, double.class, double.class),
                    "surf.color = new Vec(r, g, b);", Modifier.PUBLIC, new String[]{"r", "g", "b"}));
            methods.add(new jmplib.reflect.Method("normal", MethodType.methodType(Vec.class, Vec.class), "",
                    Modifier.PUBLIC | Modifier.ABSTRACT, new String[]{"pnt"}));
            methods.add(new jmplib.reflect.Method("intersect", MethodType.methodType(Isect.class, Ray.class), "",
                    Modifier.PUBLIC | Modifier.ABSTRACT, new String[]{"ry"}));
            methods.add(new jmplib.reflect.Method("toString", MethodType.methodType(String.class), "",
                    Modifier.PUBLIC | Modifier.ABSTRACT, new String[0]));
            methods.add(new jmplib.reflect.Method("getCenter", MethodType.methodType(Vec.class), "",
                    Modifier.PUBLIC | Modifier.ABSTRACT, new String[0]));
            methods.add(new jmplib.reflect.Method("setCenter", MethodType.methodType(void.class, Vec.class), "",
                    Modifier.PUBLIC | Modifier.ABSTRACT, new String[]{"c"}));

            transaction.addField(Primitive.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Primitive.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();
            // Ray
            methods.add(new jmplib.reflect.Method("point", MethodType.methodType(Vec.class, double.class),
                    "return new Vec(P.x + D.x * t, P.y + D.y * t, P.z + D.z * t);", Modifier.PUBLIC,
                    new String[]{"t"}));
            methods.add(new jmplib.reflect.Method("toString", MethodType.methodType(String.class),
                    "return \"{\" + P.toString() + \" -> \" + D.toString() + \"}\";", Modifier.PUBLIC, new String[0]));

            transaction.addField(Ray.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Ray.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();

            // JGFRayTracerBench
            methods.add(new jmplib.reflect.Method("JGFsetsize", MethodType.methodType(void.class, int.class),
                    "this.size = size;", Modifier.PUBLIC, new String[]{"size"}));
            methods.add(new jmplib.reflect.Method(
                    "JGFinitialise", MethodType.methodType(void.class), "width = height = datasizes[size];"
                    + "scene = createScene();" + "setScene(scene);" + "numobjects = scene.getObjects();",
                    Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("JGFapplication", MethodType.methodType(void.class),
                    "Interval interval = new Interval(0, width, height, 0, height, 1);" + "render(interval);",
                    Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method(
                    "JGFvalidate", MethodType.methodType(void.class), "long[] refval = { 2676692, 29827635 };"
                    + "long dev = checksum - refval[size];" + "if (dev != 0) {" + "}",
                    Modifier.PUBLIC, new String[0]));
            methods.add(
                    new jmplib.reflect.Method(
                            "JGFtidyup", MethodType.methodType(void.class), "scene = null;" + "lights = null;"
                            + "prim = null;" + "tRay = null;" + "inter = null;" + "/*System.gc()*/;",
                            Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("JGFrun", MethodType.methodType(void.class), "JGFrun1(0);",
                    Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("JGFrun1", MethodType.methodType(void.class, int.class),
                    "JGFsetsize(size);"
                            + "JGFinitialise();"
                            + "JGFapplication();"
                            + "JGFvalidate();"
                            + "JGFtidyup();",
                    Modifier.PUBLIC, new String[]{"size"}));

            transaction.addField(JGFRayTracerBench.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(JGFRayTracerBench.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();

            transaction.replaceImplementation(JGFRayTracerBench.class,
                    new jmplib.reflect.Method("test", "JGFrun1(BenchMark.ITERATIONS);"));

            // RayTracer
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Scene.class, "scene"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Light[].class, "lights"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Primitive[].class, "prim"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, View.class, "view"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Ray.class, "tRay", "new Ray()"));
            fields.add(new jmplib.reflect.Field(Modifier.STATIC, int.class, "alpha", "255 << 24"));
            fields.add(new jmplib.reflect.Field(0, Vec.class, "voidVec", "new Vec()"));
            fields.add(new jmplib.reflect.Field(0, Vec.class, "L", "new Vec()"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Isect.class, "inter", "new Isect()"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "height"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "width"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, int[].class, "datasizes", "new int[]{150,500}"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, long.class, "checksum"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "size"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "numobjects"));
            methods.add(new jmplib.reflect.Method("createScene", MethodType.methodType(Scene.class),
                    "int x = 0;" + "int y = 0;" + "Scene scene = new Scene();" + "Primitive p;" + "int nx = 4;"
                            + "int ny = 4;" + "int nz = 4;" + "for (int i = 0; i < nx; i++) {"
                            + "for (int j = 0; j < ny; j++) {" + "for (int k = 0; k < nz; k++) {"
                            + "double xx = 20.0 / (nx - 1) * i - 10.0;" + "double yy = 20.0 / (ny - 1) * j - 10.0;"
                            + "double zz = 20.0 / (nz - 1) * k - 10.0;" + "p = new Sphere(new Vec(xx, yy, zz), 3);"
                            + "p.setColor(0, 0, (i + j) / (double) (nx + ny - 2));" + "p.surf.shine = 15.0;"
                            + "p.surf.ks = 1.5 - 1.0;" + "p.surf.kt = 1.5 - 1.0;" + "scene.addObject(p);" + "}" + "}"
                            + "}" + "scene.addLight(new Light(100, 100, -50, 1.0));"
                            + "scene.addLight(new Light(-100, 100, -50, 1.0));"
                            + "scene.addLight(new Light(100, -100, -50, 1.0));"
                            + "scene.addLight(new Light(-100, -100, -50, 1.0));"
                            + "scene.addLight(new Light(200, 200, 0, 1.0));"
                            + "View v = new View(new Vec(x, 20, -30), new Vec(x, y, 0), new Vec(0, 1,"
                            + "0), 1.0, 35.0 * 3.14159265 / 180.0, 1.0);" + "scene.setView(v);" + "return scene;",
                    Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("setScene", MethodType.methodType(void.class, Scene.class),
                    "int nLights = scene.getLights();" + "int nObjects = scene.getObjects();"
                            + "lights = new Light[nLights];" + "prim = new Primitive[nObjects];"
                            + "for (int l = 0; l < nLights; l++) {" + "lights[l] = scene.getLight(l);" + "}"
                            + "for (int o = 0; o < nObjects; o++) {" + "prim[o] = scene.getObject(o);" + "}"
                            + "view = scene.getView();",
                    Modifier.PUBLIC, new String[]{"scene"}));
            methods.add(new jmplib.reflect.Method("render", MethodType.methodType(void.class, Interval.class),
                    "int[] row = new int[interval.width * (interval.yto - interval.yfrom)];" + "int pixCounter = 0;"
                            + "int x, y, red, green, blue;" + "double xlen, ylen;" + "Vec viewVec;"
                            + "viewVec = Vec.sub(view.at, view.from);" + "viewVec.normalize();"
                            + "Vec tmpVec = new Vec(viewVec);" + "tmpVec.scale(Vec.dot(view.up, viewVec));"
                            + "Vec upVec = Vec.sub(view.up, tmpVec);" + "upVec.normalize();"
                            + "Vec leftVec = Vec.cross(view.up, viewVec);" + "leftVec.normalize();"
                            + "double frustrumwidth = view.dist * Math.tan(view.angle);"
                            + "upVec.scale(-frustrumwidth);" + "leftVec.scale(view.aspect * frustrumwidth);"
                            + "Ray r = new Ray(view.from, voidVec);" + "Vec col = new Vec();"
                            + "for (y = interval.yfrom; y < interval.yto; y++) {"
                            + "ylen = (double) (2.0 * y) / (double) interval.width - 1.0;"
                            + "for (x = 0; x < interval.width; x++) {"
                            + "xlen = (double) (2.0 * x) / (double) interval.width - 1.0;"
                            + "Vec D = Vec.comb(xlen, leftVec, ylen, upVec);" + "D.add(viewVec);" + "D.normalize();"
                            + "r.D = D;" + "col = trace(0, 1.0, r);" + "red = (int) (col.x * 255.0);" + "if (red > 255)"
                            + "red = 255;" + "green = (int) (col.y * 255.0);" + "if (green > 255)" + "green = 255;"
                            + "blue = (int) (col.z * 255.0);" + "if (blue > 255)" + "blue = 255;" + "checksum += red;"
                            + "checksum += green;" + "checksum += blue;"
                            + "row[pixCounter++] = alpha | (red << 16) | (green << 8) | (blue);" + "}" + "}",
                    Modifier.PUBLIC, new String[]{"interval"}));
            methods.add(new jmplib.reflect.Method("intersect",
                    MethodType.methodType(boolean.class, Ray.class, double.class),
                    "Isect tp;" + "int i, nhits;" + "nhits = 0;" + "inter.t = 1e9;"
                            + "for (i = 0; i < prim.length; i++) {" + "tp = prim[i].intersect(r);"
                            + "if (tp != null && tp.t < inter.t) {" + "inter.t = tp.t;" + "inter.prim = tp.prim;"
                            + "inter.surf = tp.surf;" + "inter.enter = tp.enter;" + "nhits++;" + "}" + "}"
                            + "return nhits > 0 ? true : false;",
                    0, new String[]{"r", "maxt"}));
            methods.add(new jmplib.reflect.Method("Shadow", MethodType.methodType(int.class, Ray.class, double.class),
                    "if (intersect(r, tmax))" + "return 0;" + "return 1;", 0, new String[]{"r", "tmax"}));
            methods.add(new jmplib.reflect.Method("SpecularDirection",
                    MethodType.methodType(Vec.class, Vec.class, Vec.class),
                    "Vec r;" + "r = Vec.comb(1.0 / Math.abs(Vec.dot(I, N)), I, 2.0, N);" + "r.normalize();"
                            + "return r;",
                    0, new String[]{"I", "N"}));
            methods.add(new jmplib.reflect.Method("TransDir",
                    MethodType.methodType(Vec.class, Surface.class, Surface.class, Vec.class, Vec.class),
                    "double n1, n2, eta, c1, cs2;" + "Vec r;" + "n1 = m1 == null ? 1.0 : m1.ior;"
                            + "n2 = m2 == null ? 1.0 : m2.ior;" + "eta = n1 / n2;" + "c1 = -Vec.dot(I, N);"
                            + "cs2 = 1.0 - eta * eta * (1.0 - c1 * c1);" + "if (cs2 < 0.0)" + "return null;"
                            + "r = Vec.comb(eta, I, eta * c1 - Math.sqrt(cs2), N);" + "r.normalize();" + "return r;",
                    0, new String[]{"m1", "m2", "I", "N"}));
            methods.add(new jmplib.reflect.Method("shade",
                    MethodType.methodType(Vec.class, int.class, double.class, Vec.class, Vec.class, Vec.class,
                            Isect.class),
                    "double n1, n2, eta, c1, cs2;" + "Vec r;" + "Vec tcol;" + "Vec R;" + "double t, diff, spec;"
                            + "Surface surf;" + "Vec col;" + "int l;" + "col = new Vec();" + "surf = hit.surf;"
                            + "R = new Vec();" + "if (surf.shine > 1e-6) {" + "R = SpecularDirection(I, N);" + "}"
                            + "for (l = 0; l < lights.length; l++) {" + "L.sub2(lights[l].pos, P);"
                            + "if (Vec.dot(N, L) >= 0.0) {" + "t = L.normalize();" + "tRay.P = P;" + "tRay.D = L;"
                            + "if (Shadow(tRay, t) > 0) {" + "diff = Vec.dot(N, L) * surf.kd * lights[l].brightness;"
                            + "col.adds(diff, surf.color);" + "if (surf.shine > 1e-6) {" + "spec = Vec.dot(R, L);"
                            + "if (spec > 1e-6) {" + "spec = Math.pow(spec, surf.shine);" + "col.x += spec;"
                            + "col.y += spec;" + "col.z += spec;" + "}" + "}" + "}" + "}" + "}" + "tRay.P = P;"
                            + "if (surf.ks * weight > 1e-3) {" + "tRay.D = SpecularDirection(I, N);"
                            + "tcol = trace(level + 1, surf.ks * weight, tRay);" + "col.adds(surf.ks, tcol);" + "}"
                            + "if (surf.kt * weight > 1e-3) {" + "if (hit.enter > 0)"
                            + "tRay.D = TransDir(null, surf, I, N);" + "else " + "tRay.D = TransDir(surf, null, I, N);"
                            + "tcol = trace(level + 1, surf.kt * weight, tRay);" + "col.adds(surf.kt, tcol);" + "}"
                            + "tcol = null;" + "surf = null;" + "return col;",
                    0, new String[]{"level", "weight", "P", "N", "I", "hit"}));
            methods.add(new jmplib.reflect.Method("trace",
                    MethodType.methodType(Vec.class, int.class, double.class, Ray.class),
                    "Vec P, N;" + "boolean hit;" + "if (level > 6) {" + "return new Vec();" + "}"
                            + "hit = intersect(r, 1e6);" + "if (hit) {" + "P = r.point(inter.t);"
                            + "N = inter.prim.normal(P);" + "if (Vec.dot(r.D, N) >= 0.0) {" + "N.negate();" + "}"
                            + "return shade(level, weight, P, N, r.D, inter);" + "}" + "return voidVec;",
                    0, new String[]{"level", "weight", "r"}));

            transaction.addField(RayTracer.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(RayTracer.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();

            // Scene
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, ArrayList.class, "lights", "new java.util.ArrayList()"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, ArrayList.class, "objects", "new java.util.ArrayList()"));
            fields.add(new jmplib.reflect.Field(Modifier.PRIVATE, View.class, "view"));
            methods.add(new jmplib.reflect.Method("addLight", MethodType.methodType(void.class, Light.class),
                    "this.lights.add(l);", Modifier.PUBLIC, new String[]{"l"}));
            methods.add(new jmplib.reflect.Method("addObject", MethodType.methodType(void.class, Primitive.class),
                    "this.objects.add(myobject);", Modifier.PUBLIC, new String[]{"myobject"}));
            methods.add(new jmplib.reflect.Method("setView", MethodType.methodType(void.class, View.class),
                    "this.view = view;", Modifier.PUBLIC, new String[]{"view"}));
            methods.add(new jmplib.reflect.Method("getView", MethodType.methodType(View.class), "return this.view;",
                    Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("getLight", MethodType.methodType(Light.class, int.class),
                    "return (Light) this.lights.get(number);", Modifier.PUBLIC, new String[]{"number"}));
            methods.add(new jmplib.reflect.Method("getObject", MethodType.methodType(Primitive.class, int.class),
                    "return (Primitive) objects.get(number);", Modifier.PUBLIC, new String[]{"number"}));
            methods.add(new jmplib.reflect.Method("getLights", MethodType.methodType(int.class),
                    "return this.lights.size();", Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("getObjects", MethodType.methodType(int.class),
                    "return this.objects.size();", Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("setObject",
                    MethodType.methodType(void.class, Primitive.class, int.class), "this.objects.set(pos, myobject);",
                    Modifier.PUBLIC, new String[]{"myobject", "pos"}));

            transaction.addField(Scene.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Scene.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();
            // Sphere
            fields.add(new jmplib.reflect.Field(0, Vec.class, "v", "new Vec()"));
            fields.add(new jmplib.reflect.Field(0, Vec.class, "b", "new Vec()"));

            methods.add(new jmplib.reflect.Method("intersect", MethodType.methodType(Isect.class, Ray.class),
                    "double b, disc, t;" + "Isect ip;" + "v.sub2(c, ry.P);" + "b = Vec.dot(v, ry.D);"
                            + "disc = b * b - Vec.dot(v, v) + r2;" + "if (disc < 0.0) {" + "return null;" + "}"
                            + "disc = Math.sqrt(disc);" + "t = (b - disc < 1e-6) ? b + disc : b - disc;"
                            + "if (t < 1e-6) {" + "return null;" + "}" + "ip = new Isect();" + "ip.t = t;"
                            + "ip.enter = Vec.dot(v, v) > r2 + 1e-6 ? 1 : 0;" + "ip.prim = this;" + "ip.surf = surf;"
                            + "return ip;",
                    Modifier.PUBLIC, new String[]{"ry"}));
            methods.add(new jmplib.reflect.Method("normal", MethodType.methodType(Vec.class, Vec.class),
                    "Vec r;" + "r = Vec.sub(p, c);" + "r.normalize();" + "return r;", Modifier.PUBLIC,
                    new String[]{"p"}));
            methods.add(new jmplib.reflect.Method("toString", MethodType.methodType(String.class),
                    "return \"Sphere {\" + c.toString() + \",\" + r + \"}\";", Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("getCenter", MethodType.methodType(Vec.class), "return c;",
                    Modifier.PUBLIC, new String[0]));
            methods.add(new jmplib.reflect.Method("setCenter", MethodType.methodType(void.class, Vec.class),
                    "this.c = c;", Modifier.PUBLIC, new String[]{"c"}));

            transaction.addField(Sphere.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Sphere.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();
            // Surface
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, Vec.class, "color", "new Vec(1, 0, 0)"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "kd", "1.0"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "ks", "0.0"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "shine", "0.0"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "kt", "0.0"));
            fields.add(new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "ior", "1.0"));
            methods.add(new jmplib.reflect.Method("toString", MethodType.methodType(String.class),
                    "return \"Surface { color=\" + color + \"}\";", Modifier.PUBLIC, new String[0]));

            transaction.addField(Surface.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Surface.class, methods.toArray(new jmplib.reflect.Method[0]));
            fields = new ArrayList<jmplib.reflect.Field>();
            methods = new ArrayList<jmplib.reflect.Method>();
            // Vec

            methods.add(new jmplib.reflect.Method("add", MethodType.methodType(void.class, Vec.class),
                    "x += a.x;" + "y += a.y;" + "z += a.z;", Modifier.PUBLIC, new String[]{"a"}));
            methods.add(new jmplib.reflect.Method("adds",
                    MethodType.methodType(Vec.class, double.class, Vec.class, Vec.class),
                    "return new Vec(s * a.x + b.x, s * a.y + b.y, s * a.z + b.z);", Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"s", "a", "b"}));
            methods.add(new jmplib.reflect.Method("adds", MethodType.methodType(void.class, double.class, Vec.class),
                    "x += s * b.x;" + "y += s * b.y;" + "z += s * b.z;", Modifier.PUBLIC, new String[]{"s", "b"}));
            methods.add(new jmplib.reflect.Method("sub", MethodType.methodType(Vec.class, Vec.class, Vec.class),
                    "return new Vec(a.x - b.x, a.y - b.y, a.z - b.z);", Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"a", "b"}));
            methods.add(new jmplib.reflect.Method("sub2", MethodType.methodType(void.class, Vec.class, Vec.class),
                    "this.x = a.x - b.x;" + "this.y = a.y - b.y;" + "this.z = a.z - b.z;", Modifier.PUBLIC,
                    new String[]{"a", "b"}));
            methods.add(new jmplib.reflect.Method("mult", MethodType.methodType(Vec.class, Vec.class, Vec.class),
                    "return new Vec(a.x * b.x, a.y * b.y, a.z * b.z);", Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"a", "b"}));
            methods.add(new jmplib.reflect.Method("cross", MethodType.methodType(Vec.class, Vec.class, Vec.class),
                    "return new Vec(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y" + "- a.y * b.x);",
                    Modifier.PUBLIC | Modifier.STATIC, new String[]{"a", "b"}));
            methods.add(new jmplib.reflect.Method("dot", MethodType.methodType(double.class, Vec.class, Vec.class),
                    "return a.x * b.x + a.y * b.y + a.z * b.z;", Modifier.PUBLIC | Modifier.STATIC,
                    new String[]{"a", "b"}));
            methods.add(new jmplib.reflect.Method("comb",
                    MethodType.methodType(Vec.class, double.class, Vec.class, double.class, Vec.class),
                    "return new Vec(a * A.x + b * B.x, a * A.y + b * B.y, a * A.z + b * B.z);",
                    Modifier.PUBLIC | Modifier.STATIC, new String[]{"a", "A", "b", "B"}));
            methods.add(new jmplib.reflect.Method("comb2",
                    MethodType.methodType(void.class, double.class, Vec.class, double.class, Vec.class),
                    "x = a * A.x + b * B.x;" + "y = a * A.y + b * B.y;" + "z = a * A.z + b * B.z;", Modifier.PUBLIC,
                    new String[]{"a", "A", "b", "B"}));
            methods.add(new jmplib.reflect.Method("scale", MethodType.methodType(void.class, double.class),
                    "x *= t;" + "y *= t;" + "z *= t;", Modifier.PUBLIC, new String[]{"t"}));
            methods.add(new jmplib.reflect.Method("negate", MethodType.methodType(void.class),
                    "x = -x;" + "y = -y;" + "z = -z;", Modifier.PUBLIC, new String[0]));
            // methods.add(new jmplib.reflect.Method("normalize",
            // MethodType.methodType(double.class),
            // "double len;" + "len = Math.sqrt(x * x + y * y + z * z);"
            // + "if (len > 0.0) {" + "x /= len;" + "y /= len;"
            // + "z /= len;" + "}" + "return len;");
            methods.add(new jmplib.reflect.Method("toString", MethodType.methodType(String.class),
                    "return \"<\" + x + \",\" + y + \",\" + z + \">\";", Modifier.PUBLIC, new String[0]));

            transaction.addField(Vec.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(Vec.class, methods.toArray(new jmplib.reflect.Method[0]));

            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }
}
