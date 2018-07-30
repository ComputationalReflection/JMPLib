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
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 1999.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/


package es.uniovi.jmplib.testing.javagrande.moldyn_meta;

import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFInstrumentor;
import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFSection3;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.text.NumberFormat;


public class JGFMolDynBench extends es.uniovi.jmplib.testing.javagrande.moldyn_meta.md implements JGFSection3 {

//   int size;

    public void JGFsetsize(int size) {
        this.size = size;
    }

    public void prepare() throws StructuralIntercessionException {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();

        transaction.replaceImplementation(md.class, new jmplib.reflect.Method("initialise", "" +
                "        nbf = NumberFormat.getInstance();\n" +
                "        nbf.setMaximumFractionDigits(4);\n" +
                "        nbf.setMinimumFractionDigits(4);\n" +
                "        nbf.setGroupingUsed(false);\n" +
                "\n" +
                "        nbf2 = NumberFormat.getInstance();\n" +
                "        nbf2.setMaximumFractionDigits(1);\n" +
                "        nbf2.setMinimumFractionDigits(1);\n" +
                "\n" +
                "        nbf3 = NumberFormat.getInstance();\n" +
                "        nbf3.setMaximumFractionDigits(6);\n" +
                "        nbf3.setMinimumFractionDigits(6);\n" +
                "\n" +
                "        /* Parameter determination */\n" +
                "\n" +
                "        mm = datasizes[size];\n" +
                "        PARTSIZE = mm * mm * mm * 4;\n" +
                "        mdsize = PARTSIZE;\n" +
                "        md.one = new particle[mdsize];\n" +
                "\n" +
                "        l = LENGTH;\n" +
                "\n" +
                "        side = Math.pow((mdsize / den), 0.3333333);\n" +
                "        rcoff = mm / 4.0;\n" +
                "\n" +
                "        a = side / mm;\n" +
                "        sideh = side * 0.5;\n" +
                "        hsq = h * h;\n" +
                "        hsq2 = hsq * 0.5;\n" +
                "        npartm = mdsize - 1;\n" +
                "        rcoffs = rcoff * rcoff;\n" +
                "        tscale = 16.0 / (1.0 * mdsize - 1.0);\n" +
                "        vaver = 1.13 * Math.sqrt(tref / 24.0);\n" +
                "        vaverh = vaver * h;\n" +
                "\n" +
                "        /* Particle Generation */\n" +
                "\n" +
                "        ijk = 0;\n" +
                "        for (lg = 0; lg <= 1; lg++) {\n" +
                "            for (i = 0; i < mm; i++) {\n" +
                "                for (j = 0; j < mm; j++) {\n" +
                "                    for (k = 0; k < mm; k++) {\n" +
                "                        md.one[ijk] = new particle((i * a + lg * a * 0.5), (j * a + lg * a * 0.5), (k * a),\n" +
                "                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0);\n" +
                "                        ijk = ijk + 1;\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        for (lg = 1; lg <= 2; lg++) {\n" +
                "            for (i = 0; i < mm; i++) {\n" +
                "                for (j = 0; j < mm; j++) {\n" +
                "                    for (k = 0; k < mm; k++) {\n" +
                "                        md.one[ijk] = new particle((i * a + (2 - lg) * a * 0.5), (j * a + (lg - 1) * a * 0.5),\n" +
                "                                (k * a + a * 0.5), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);\n" +
                "                        ijk = ijk + 1;\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        /* Initialise velocities */\n" +
                "\n" +
                "        iseed = 0;\n" +
                "        v1 = 0.0;\n" +
                "        v2 = 0.0;\n" +
                "\n" +
                "        randnum = new random(iseed, v1, v2);\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i += 2) {\n" +
                "            r = randnum.seed();\n" +
                "            md.one[i].xvelocity = r * randnum.v1;\n" +
                "            md.one[i + 1].xvelocity = r * randnum.v2;\n" +
                "        }\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i += 2) {\n" +
                "            r = randnum.seed();\n" +
                "            md.one[i].yvelocity = r * randnum.v1;\n" +
                "            md.one[i + 1].yvelocity = r * randnum.v2;\n" +
                "        }\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i += 2) {\n" +
                "            r = randnum.seed();\n" +
                "            md.one[i].zvelocity = r * randnum.v1;\n" +
                "            md.one[i + 1].zvelocity = r * randnum.v2;\n" +
                "        }\n" +
                "\n" +
                "        /* velocity scaling */\n" +
                "\n" +
                "        ekin = 0.0;\n" +
                "        sp = 0.0;\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "            sp = sp + md.one[i].xvelocity;\n" +
                "        }\n" +
                "        sp = sp / mdsize;\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "            md.one[i].xvelocity = md.one[i].xvelocity - sp;\n" +
                "            ekin = ekin + md.one[i].xvelocity * md.one[i].xvelocity;\n" +
                "        }\n" +
                "\n" +
                "        sp = 0.0;\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "            sp = sp + md.one[i].yvelocity;\n" +
                "        }\n" +
                "        sp = sp / mdsize;\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "            md.one[i].yvelocity = md.one[i].yvelocity - sp;\n" +
                "            ekin = ekin + md.one[i].yvelocity * md.one[i].yvelocity;\n" +
                "        }\n" +
                "\n" +
                "        sp = 0.0;\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "            sp = sp + md.one[i].zvelocity;\n" +
                "        }\n" +
                "        sp = sp / mdsize;\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "            md.one[i].zvelocity = md.one[i].zvelocity - sp;\n" +
                "            ekin = ekin + md.one[i].zvelocity * md.one[i].zvelocity;\n" +
                "        }\n" +
                "\n" +
                "        ts = tscale * ekin;\n" +
                "        sc = h * Math.sqrt(tref / ts);\n" +
                "\n" +
                "        for (i = 0; i < mdsize; i++) {\n" +
                "\n" +
                "            md.one[i].xvelocity = md.one[i].xvelocity * sc;\n" +
                "            md.one[i].yvelocity = md.one[i].yvelocity * sc;\n" +
                "            md.one[i].zvelocity = md.one[i].zvelocity * sc;\n" +
                "\n" +
                "        }\n" +
                "\n" +
                "        /* MD simulation */"));

        transaction.replaceImplementation(md.class, new jmplib.reflect.Method("runiters",
                MethodType.methodType(void.class), "        move = 0;\n" +
                "        for (move = 0; move < movemx; move++) {\n" +
                "\n" +
                "            for (i = 0; i < mdsize; i++) {\n" +
                "                md.one[i].domove(side);        /* move the particles and update velocities */\n" +
                "            }\n" +
                "\n" +
                "            epot = 0.0;\n" +
                "            vir = 0.0;\n" +
                "\n" +
                "            for (i = 0; i < mdsize; i++) {\n" +
                "                md.one[i].force(side, rcoff, mdsize, i);  /* compute forces */\n" +
                "            }\n" +
                "\n" +
                "            sum = 0.0;\n" +
                "\n" +
                "            for (i = 0; i < mdsize; i++) {\n" +
                "                sum = sum + md.one[i].mkekin(hsq2);    /*scale forces, update velocities */\n" +
                "            }\n" +
                "\n" +
                "            ekin = sum / hsq;\n" +
                "\n" +
                "            vel = 0.0;\n" +
                "            count = 0.0;\n" +
                "\n" +
                "            for (i = 0; i < mdsize; i++) {\n" +
                "                vel = vel + md.one[i].velavg(vaverh, h); /* average velocity */\n" +
                "            }\n" +
                "\n" +
                "            vel = vel / h;\n" +
                "\n" +
                "            /* tmeperature scale if required */\n" +
                "\n" +
                "            if ((move < istop) && (((move + 1) % irep) == 0)) {\n" +
                "                sc = Math.sqrt(tref / (tscale * ekin));\n" +
                "                for (i = 0; i < mdsize; i++) {\n" +
                "                    md.one[i].dscal(sc, 1);\n" +
                "                }\n" +
                "                ekin = tref / tscale;\n" +
                "            }\n" +
                "\n" +
                "            /* sum to get full potential energy and virial */\n" +
                "\n" +
                "            if (((move + 1) % iprint) == 0) {\n" +
                "                ek = 24.0 * ekin;\n" +
                "                epot = 4.0 * epot;\n" +
                "                etot = ek + epot;\n" +
                "                temp = tscale * ekin;\n" +
                "                pres = den * 16.0 * (ekin - vir) / mdsize;\n" +
                "                vel = vel / mdsize;\n" +
                "                rp = (count / mdsize) * 100.0;\n" +
                "            }\n" +
                "\n" +
                "        }"));
//
//        transaction.addMethod(particle.class, new jmplib.reflect.Method("domove",
//                MethodType.methodType(void.class, double.class), "\n" +
//                "        xcoord = xcoord + xvelocity + xforce;\n" +
//                "        ycoord = ycoord + yvelocity + yforce;\n" +
//                "        zcoord = zcoord + zvelocity + zforce;\n" +
//                "\n" +
//                "        if (xcoord < 0) {\n" +
//                "            xcoord = xcoord + side;\n" +
//                "        }\n" +
//                "        if (xcoord > side) {\n" +
//                "            xcoord = xcoord - side;\n" +
//                "        }\n" +
//                "        if (ycoord < 0) {\n" +
//                "            ycoord = ycoord + side;\n" +
//                "        }\n" +
//                "        if (ycoord > side) {\n" +
//                "            ycoord = ycoord - side;\n" +
//                "        }\n" +
//                "        if (zcoord < 0) {\n" +
//                "            zcoord = zcoord + side;\n" +
//                "        }\n" +
//                "        if (zcoord > side) {\n" +
//                "            zcoord = zcoord - side;\n" +
//                "        }\n" +
//                "\n" +
//                "        xvelocity = xvelocity + xforce;\n" +
//                "        yvelocity = yvelocity + yforce;\n" +
//                "        zvelocity = zvelocity + zforce;\n" +
//                "\n" +
//                "        xforce = 0.0;\n" +
//                "        yforce = 0.0;\n" +
//                "        zforce = 0.0;", "side"));
//
//        transaction.addMethod(particle.class, new jmplib.reflect.Method("force",
//                MethodType.methodType(void.class, double.class, double.class, int.class, int.class), "\n" +
//                "        double sideh;\n" +
//                "        double rcoffs;\n" +
//                "\n" +
//                "        double xx, yy, zz, xi, yi, zi, fxi, fyi, fzi;\n" +
//                "        double rd, rrd, rrd2, rrd3, rrd4, rrd6, rrd7, r148;\n" +
//                "        double forcex, forcey, forcez;\n" +
//                "\n" +
//                "        int i;\n" +
//                "\n" +
//                "        sideh = 0.5 * side;\n" +
//                "        rcoffs = rcoff * rcoff;\n" +
//                "\n" +
//                "        xi = xcoord;\n" +
//                "        yi = ycoord;\n" +
//                "        zi = zcoord;\n" +
//                "        fxi = 0.0;\n" +
//                "        fyi = 0.0;\n" +
//                "        fzi = 0.0;\n" +
//                "\n" +
//                "        for (i = x + 1; i < mdsize; i++) {\n" +
//                "            xx = xi - md.one[i].xcoord;\n" +
//                "            yy = yi - md.one[i].ycoord;\n" +
//                "            zz = zi - md.one[i].zcoord;\n" +
//                "\n" +
//                "            if (xx < (-sideh)) {\n" +
//                "                xx = xx + side;\n" +
//                "            }\n" +
//                "            if (xx > (sideh)) {\n" +
//                "                xx = xx - side;\n" +
//                "            }\n" +
//                "            if (yy < (-sideh)) {\n" +
//                "                yy = yy + side;\n" +
//                "            }\n" +
//                "            if (yy > (sideh)) {\n" +
//                "                yy = yy - side;\n" +
//                "            }\n" +
//                "            if (zz < (-sideh)) {\n" +
//                "                zz = zz + side;\n" +
//                "            }\n" +
//                "            if (zz > (sideh)) {\n" +
//                "                zz = zz - side;\n" +
//                "            }\n" +
//                "\n" +
//                "            rd = xx * xx + yy * yy + zz * zz;\n" +
//                "\n" +
//                "            if (rd <= rcoffs) {\n" +
//                "                rrd = 1.0 / rd;\n" +
//                "                rrd2 = rrd * rrd;\n" +
//                "                rrd3 = rrd2 * rrd;\n" +
//                "                rrd4 = rrd2 * rrd2;\n" +
//                "                rrd6 = rrd2 * rrd4;\n" +
//                "                rrd7 = rrd6 * rrd;\n" +
//                "                md.epot = md.epot + (rrd6 - rrd3);\n" +
//                "                r148 = rrd7 - 0.5 * rrd4;\n" +
//                "                md.vir = md.vir - rd * r148;\n" +
//                "                forcex = xx * r148;\n" +
//                "                fxi = fxi + forcex;\n" +
//                "                md.one[i].xforce = md.one[i].xforce - forcex;\n" +
//                "                forcey = yy * r148;\n" +
//                "                fyi = fyi + forcey;\n" +
//                "                md.one[i].yforce = md.one[i].yforce - forcey;\n" +
//                "                forcez = zz * r148;\n" +
//                "                fzi = fzi + forcez;\n" +
//                "                md.one[i].zforce = md.one[i].zforce - forcez;\n" +
//                "                md.interactions++;\n" +
//                "            }\n" +
//                "\n" +
//                "        }\n" +
//                "\n" +
//                "        xforce = xforce + fxi;\n" +
//                "        yforce = yforce + fyi;\n" +
//                "        zforce = zforce + fzi;\n" +
//                "\n", "side", "rcoff", "mdsize", "x"));
//
//        transaction.addMethod(particle.class, new jmplib.reflect.Method("mkekin",
//                MethodType.methodType(double.class, double.class), "\n" +
//                "        double sumt = 0.0;\n" +
//                "\n" +
//                "        xforce = xforce * hsq2;\n" +
//                "        yforce = yforce * hsq2;\n" +
//                "        zforce = zforce * hsq2;\n" +
//                "\n" +
//                "        xvelocity = xvelocity + xforce;\n" +
//                "        yvelocity = yvelocity + yforce;\n" +
//                "        zvelocity = zvelocity + zforce;\n" +
//                "\n" +
//                "        sumt = (xvelocity * xvelocity) + (yvelocity * yvelocity) + (zvelocity * zvelocity);\n" +
//                "        return sumt;", "hsq2"));
//
//        transaction.replaceImplementation(particle.class, new jmplib.reflect.Method("velavg",
//                MethodType.methodType(double.class, double.class, double.class), "\n" +
//                "        double velt;\n" +
//                "        double sq;\n" +
//                "\n" +
//                "        sq = Math.sqrt(xvelocity * xvelocity + yvelocity * yvelocity +\n" +
//                "                zvelocity * zvelocity);\n" +
//                "\n" +
//                "        if (sq > vaverh) {\n" +
//                "            md.count = md.count + 1.0;\n" +
//                "        }\n" +
//                "\n" +
//                "        velt = sq;\n" +
//                "        return velt;", "vaverh", "h"));

        //transaction.addField(particle.class, new jmplib.reflect.Field(int.class,"test"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "i"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "j"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "k"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "lg"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "mdsize"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "move"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "mm"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "l"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "rcoff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "rcoffs"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "side"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "sideh"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "hsq"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "hsq2"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "vel"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "a"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "r"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "sum"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "tscale"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "sc"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "ekin"),
                //new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "ek"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "ts"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "sp"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "den", "0.83134"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "tref", "0.722"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "h", "0.064"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "vaver"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "vaverh"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "rand"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "etot"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "temp"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "pres"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "rp"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "u1"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "u2"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "v1"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "v2"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "s"));


        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "ijk"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "npartm"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "PARTSIZE"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "iseed"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "tint"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "irep", "10"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "istop", "19"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "iprint", "10"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "movemx", "50"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, random.class, "randnum"));

        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, NumberFormat.class, "nbf"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, NumberFormat.class, "nbf2"));
        transaction.addField(md.class, new jmplib.reflect.Field(Modifier.PRIVATE, NumberFormat.class, "nbf3"));

        transaction.commit();
    }

    public void JGFinitialise() {

        try {
            prepare();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        initialise();

    }

    public void JGFapplication() {

        JGFInstrumentor.startTimer("Section3:MolDyn:Run");

        runiters();

        JGFInstrumentor.stopTimer("Section3:MolDyn:Run");

    }


    public void JGFvalidate() {
        double refval[] = {1731.4306625334357, 7397.392307839352};
        double dev = Math.abs(ek - refval[size]);
        if (dev > 1.0e-12) {
            System.out.println("Validation failed");
            System.out.println("Kinetic Energy = " + ek + "  " + dev + "  " + size);
        }
    }

    public void JGFtidyup() {

        one = null;
        System.gc();
    }


    public void JGFrun(int size) {

        JGFInstrumentor.addTimer("Section3:MolDyn:Total", "Solutions", size);
        JGFInstrumentor.addTimer("Section3:MolDyn:Run", "Interactions", size);

        JGFsetsize(size);

        JGFInstrumentor.startTimer("Section3:MolDyn:Total");

        JGFinitialise();
        JGFapplication();
        JGFvalidate();
        JGFtidyup();

        JGFInstrumentor.stopTimer("Section3:MolDyn:Total");

        JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Run", (double) interactions);
        JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Total", 1);

        JGFInstrumentor.printTimer("Section3:MolDyn:Run");
        JGFInstrumentor.printTimer("Section3:MolDyn:Total");
    }


}
 
