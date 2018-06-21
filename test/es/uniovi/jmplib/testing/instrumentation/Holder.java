package es.uniovi.jmplib.testing.instrumentation;

public class Holder {

    public final int test = 0;
    public String f1 = "";
    public int f2 = 0;

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }
    // public static int _f2_unary(Holder h, int type){
    // switch (type) {
    // case 1:
    // return h.f2++;
    // case 2:
    // return ++h.f2;
    // case 3:
    // return h.f2--;
    // case 4:
    // return --h.f2;
    // default:
    // throw new RuntimeException("Invalid unary type");
    // }
    // }

}
