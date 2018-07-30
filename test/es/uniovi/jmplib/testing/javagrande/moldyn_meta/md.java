/**************************************************************************
 *                                                                         *
 *             Java Grande Forum Benchmark Suite - Version 2.0             *
 *                                                                         *
 *                            produced by                                  *
 *                                                                         *
 *                  Java Grande Benchmarking Project                       *
 *                                                                         *
 *                                at                                       *
 *                                                                         *
 *                Edinburgh Parallel Computing Centre                      *
 *                                                                         *
 *                email: epcc-javagrande@epcc.ed.ac.uk                     *
 *                                                                         *
 *                  Original version of this code by                       *
 *                         Dieter Heermann                                 *
 *                       converted to Java by                              *
 *                Lorna Smith  (l.smith@epcc.ed.ac.uk)                     *
 *                   (see copyright notice below)                          *
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 1999.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/


package es.uniovi.jmplib.testing.javagrande.moldyn_meta;

import java.text.NumberFormat;

public class md {

    public static final int ITERS = 100;
    public static final double LENGTH = 50e-10;
    public static final double m = 4.0026;
    public static final double mu = 1.66056e-27;
    public static final double kb = 1.38066e-23;
    public static final double TSIM = 50;
    public static final double deltat = 5e-16;
    public static particle one[] = null;
    public static double epot = 0.0;
    public static double vir = 0.0;
    public static double count = 0.0;
    int size;
    int datasizes[] = {8, 13};

    public static int interactions = 0;

//    int i, j, k, lg, mdsize, move, mm;
//
//    double l, rcoff, rcoffs, side, sideh, hsq, hsq2, vel;
//    double a, r, sum, tscale, sc, ekin, ek, ts, sp;
    double ek;
//    double den = 0.83134;
//    double tref = 0.722;
//    double h = 0.064;
//    double vaver, vaverh, rand;
//    double etot, temp, pres, rp;
//    double u1, u2, v1, v2, s;
//
//    int ijk, npartm, PARTSIZE, iseed, tint;
//    int irep = 10;
//    int istop = 19;
//    int iprint = 10;
//    int movemx = 50;
//
//    random randnum;
//
//    NumberFormat nbf;
//    NumberFormat nbf2;
//    NumberFormat nbf3;

    public void initialise() {

//        nbf = NumberFormat.getInstance();
//        nbf.setMaximumFractionDigits(4);
//        nbf.setMinimumFractionDigits(4);
//        nbf.setGroupingUsed(false);
//
//        nbf2 = NumberFormat.getInstance();
//        nbf2.setMaximumFractionDigits(1);
//        nbf2.setMinimumFractionDigits(1);
//
//        nbf3 = NumberFormat.getInstance();
//        nbf3.setMaximumFractionDigits(6);
//        nbf3.setMinimumFractionDigits(6);
//
//        /* Parameter determination */
//
//        mm = datasizes[size];
//        PARTSIZE = mm * mm * mm * 4;
//        mdsize = PARTSIZE;
//        md.one = new particle[mdsize];
//
//        l = LENGTH;
//
//        side = Math.pow((mdsize / den), 0.3333333);
//        rcoff = mm / 4.0;
//
//        a = side / mm;
//        sideh = side * 0.5;
//        hsq = h * h;
//        hsq2 = hsq * 0.5;
//        npartm = mdsize - 1;
//        rcoffs = rcoff * rcoff;
//        tscale = 16.0 / (1.0 * mdsize - 1.0);
//        vaver = 1.13 * Math.sqrt(tref / 24.0);
//        vaverh = vaver * h;
//
//        /* Particle Generation */
//
//        ijk = 0;
//        for (lg = 0; lg <= 1; lg++) {
//            for (i = 0; i < mm; i++) {
//                for (j = 0; j < mm; j++) {
//                    for (k = 0; k < mm; k++) {
//                        md.one[ijk] = new particle((i * a + lg * a * 0.5), (j * a + lg * a * 0.5), (k * a),
//                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
//                        ijk = ijk + 1;
//                    }
//                }
//            }
//        }
//        for (lg = 1; lg <= 2; lg++) {
//            for (i = 0; i < mm; i++) {
//                for (j = 0; j < mm; j++) {
//                    for (k = 0; k < mm; k++) {
//                        md.one[ijk] = new particle((i * a + (2 - lg) * a * 0.5), (j * a + (lg - 1) * a * 0.5),
//                                (k * a + a * 0.5), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
//                        ijk = ijk + 1;
//                    }
//                }
//            }
//        }
//
//        /* Initialise velocities */
//
//        iseed = 0;
//        v1 = 0.0;
//        v2 = 0.0;
//
//        randnum = new random(iseed, v1, v2);
//
//        for (i = 0; i < mdsize; i += 2) {
//            r = randnum.seed();
//            md.one[i].xvelocity = r * randnum.v1;
//            md.one[i + 1].xvelocity = r * randnum.v2;
//        }
//
//        for (i = 0; i < mdsize; i += 2) {
//            r = randnum.seed();
//            md.one[i].yvelocity = r * randnum.v1;
//            md.one[i + 1].yvelocity = r * randnum.v2;
//        }
//
//        for (i = 0; i < mdsize; i += 2) {
//            r = randnum.seed();
//            md.one[i].zvelocity = r * randnum.v1;
//            md.one[i + 1].zvelocity = r * randnum.v2;
//        }
//
//        /* velocity scaling */
//
//        ekin = 0.0;
//        sp = 0.0;
//
//        for (i = 0; i < mdsize; i++) {
//            sp = sp + md.one[i].xvelocity;
//        }
//        sp = sp / mdsize;
//
//        for (i = 0; i < mdsize; i++) {
//            md.one[i].xvelocity = md.one[i].xvelocity - sp;
//            ekin = ekin + md.one[i].xvelocity * md.one[i].xvelocity;
//        }
//
//        sp = 0.0;
//        for (i = 0; i < mdsize; i++) {
//            sp = sp + md.one[i].yvelocity;
//        }
//        sp = sp / mdsize;
//
//        for (i = 0; i < mdsize; i++) {
//            md.one[i].yvelocity = md.one[i].yvelocity - sp;
//            ekin = ekin + md.one[i].yvelocity * md.one[i].yvelocity;
//        }
//
//        sp = 0.0;
//        for (i = 0; i < mdsize; i++) {
//            sp = sp + md.one[i].zvelocity;
//        }
//        sp = sp / mdsize;
//
//        for (i = 0; i < mdsize; i++) {
//            md.one[i].zvelocity = md.one[i].zvelocity - sp;
//            ekin = ekin + md.one[i].zvelocity * md.one[i].zvelocity;
//        }
//
//        ts = tscale * ekin;
//        sc = h * Math.sqrt(tref / ts);
//
//        for (i = 0; i < mdsize; i++) {
//
//            md.one[i].xvelocity = md.one[i].xvelocity * sc;
//            md.one[i].yvelocity = md.one[i].yvelocity * sc;
//            md.one[i].zvelocity = md.one[i].zvelocity * sc;
//
//        }
//
//        /* MD simulation */

    }

    public void runiters() {

//        move = 0;
//        for (move = 0; move < movemx; move++) {
//
//            for (i = 0; i < mdsize; i++) {
//                one[i].domove(side);        /* move the particles and update velocities */
//            }
//
//            epot = 0.0;
//            vir = 0.0;
//
//            for (i = 0; i < mdsize; i++) {
//                one[i].force(side, rcoff, mdsize, i);  /* compute forces */
//            }
//
//            sum = 0.0;
//
//            for (i = 0; i < mdsize; i++) {
//                sum = sum + one[i].mkekin(hsq2);    /*scale forces, update velocities */
//            }
//
//            ekin = sum / hsq;
//
//            vel = 0.0;
//            count = 0.0;
//
//            for (i = 0; i < mdsize; i++) {
//                vel = vel + one[i].velavg(vaverh, h); /* average velocity */
//            }
//
//            vel = vel / h;
//
//            /* tmeperature scale if required */
//
//            if ((move < istop) && (((move + 1) % irep) == 0)) {
//                sc = Math.sqrt(tref / (tscale * ekin));
//                for (i = 0; i < mdsize; i++) {
//                    one[i].dscal(sc, 1);
//                }
//                ekin = tref / tscale;
//            }
//
//            /* sum to get full potential energy and virial */
//
//            if (((move + 1) % iprint) == 0) {
//                ek = 24.0 * ekin;
//                epot = 4.0 * epot;
//                etot = ek + epot;
//                temp = tscale * ekin;
//                pres = den * 16.0 * (ekin - vir) / mdsize;
//                vel = vel / mdsize;
//                rp = (count / mdsize) * 100.0;
//            }
//
//        }
//
//
    }


}


class random {

    public int iseed;
    public double v1, v2;

    public random(int iseed, double v1, double v2) {
        this.iseed = iseed;
        this.v1 = v1;
        this.v2 = v2;
    }

    public double update() {

        double rand;
        double scale = 4.656612875e-10;

        int is1, is2, iss2;
        int imult = 16807;
        int imod = 2147483647;

        if (iseed <= 0) {
            iseed = 1;
        }

        is2 = iseed % 32768;
        is1 = (iseed - is2) / 32768;
        iss2 = is2 * imult;
        is2 = iss2 % 32768;
        is1 = (is1 * imult + (iss2 - is2) / 32768) % (65536);

        iseed = (is1 * 32768 + is2) % imod;

        rand = scale * iseed;

        return rand;

    }

    public double seed() {

        double s, u1, u2, r;
        s = 1.0;
        do {
            u1 = update();
            u2 = update();

            v1 = 2.0 * u1 - 1.0;
            v2 = 2.0 * u2 - 1.0;
            s = v1 * v1 + v2 * v2;

        } while (s >= 1.0);

        r = Math.sqrt(-2.0 * Math.log(s) / s);

        return r;

    }
}


