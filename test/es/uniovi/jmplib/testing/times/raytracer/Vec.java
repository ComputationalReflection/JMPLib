package es.uniovi.jmplib.testing.times.raytracer;


public class Vec {


    /**
     * The x coordinate
     */
    public double x;

    /**
     * The y coordinate
     */
    public double y;

    /**
     * The z coordinate
     */
    public double z;

    /**
     * Constructor
     *
     * @param a the x coordinate
     * @param b the y coordinate
     * @param c the z coordinate
     */
    public Vec(double a, double b, double c) {
        x = a;
        y = b;
        z = c;
    }

    /**
     * Copy constructor
     */
    public Vec(Vec a) {
        x = a.x;
        y = a.y;
        z = a.z;
    }

    /**
     * Default (0,0,0) constructor
     */
    public Vec() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    public double normalize() {
        double len;
        len = Math.sqrt(x * x + y * y + z * z);
        if (len > 0.0) {
            x /= len;
            y /= len;
            z /= len;
        }
        return len;
    }

}