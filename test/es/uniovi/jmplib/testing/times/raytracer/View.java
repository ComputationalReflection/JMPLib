package es.uniovi.jmplib.testing.times.raytracer;


public class View {

    public Vec from;
    public Vec at;
    public Vec up;
    public double dist;
    public double angle;
    public double aspect;

    public View(Vec from, Vec at, Vec up, double dist, double angle, double aspect) {
        this.from = from;
        this.at = at;
        this.up = up;
        this.dist = dist;
        this.angle = angle;
        this.aspect = aspect;
    }
}