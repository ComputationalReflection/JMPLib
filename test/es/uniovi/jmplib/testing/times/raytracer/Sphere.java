package es.uniovi.jmplib.testing.times.raytracer;

public class Sphere extends Primitive {


    Vec c;
    double r, r2;

    public Sphere(Vec center, double radius)
    {
        c = center;
        r = radius;
        r2 = r * r;
    }
}