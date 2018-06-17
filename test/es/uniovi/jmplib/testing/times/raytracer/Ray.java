package es.uniovi.jmplib.testing.times.raytracer;

public class Ray {


    public Vec P, D;

    public Ray(Vec pnt, Vec dir)
    {
        P = new Vec(pnt.x, pnt.y, pnt.z);
        D = new Vec(dir.x, dir.y, dir.z);
        D.normalize();
    }

    public Ray()
    {
        P = new Vec();
        D = new Vec();
    }
}