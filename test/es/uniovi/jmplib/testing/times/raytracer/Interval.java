package es.uniovi.jmplib.testing.times.raytracer;

public class Interval {

    public int number;
    public int width;
    public int height;
    public int yfrom;
    public int yto;
    public int total;

    public Interval(int number, int width, int height, int yfrom, int yto,
                    int total) {
        this.number = number;
        this.width = width;
        this.height = height;
        this.yfrom = yfrom;
        this.yto = yto;
        this.total = total;
    }
}