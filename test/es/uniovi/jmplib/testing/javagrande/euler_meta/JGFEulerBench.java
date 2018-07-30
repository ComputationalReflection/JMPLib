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


package es.uniovi.jmplib.testing.javagrande.euler_meta;

import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFInstrumentor;
import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFSection3;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class JGFEulerBench extends Tunnel implements JGFSection3 {


    public void JGFsetsize(int size) {
        this.size = size;
    }

    public void prepare() throws StructuralIntercessionException {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();

        transaction.replaceImplementation(Tunnel.class, new jmplib.reflect.Method("initialise",
                "\n" +
                        "        int i, j, k, n;             /* Dummy counters */\n" +
                        "        double scrap, scrap2;     /* Temporary storage */\n" +
                        "\n" +
                        "\n" +
                        "        /* Set scale factor for interpolation */\n" +
                        "        scale = datasizes[size];\n" +
                        "\n" +
                        "\n" +
                        "        String path = new java.io.File(\".\").getCanonicalPath() + \"\\\\test\\\\es\\\\uniovi\\\\jmplib\\\\testing\\\\javagrande\";\n" +
                        "\n" +
                        "        /* Open data file */\n" +
                        "        FileReader instream = new FileReader(path + \"\\\\tunnel.dat\");\n" +
                        "\n" +
                        "        /* Convert the stream into tokens (which helps you parse it) */\n" +
                        "        StreamTokenizer intokens = new StreamTokenizer(instream);\n" +
                        "\n" +
                        "        /* Read header */\n" +
                        "        if (intokens.nextToken() == StreamTokenizer.TT_NUMBER)\n" +
                        "            imaxin = (int) intokens.nval;\n" +
                        "        else\n" +
                        "            throw new IOException();\n" +
                        "        if (intokens.nextToken() == StreamTokenizer.TT_NUMBER)\n" +
                        "            jmaxin = (int) intokens.nval;\n" +
                        "        else\n" +
                        "            throw new IOException();\n" +
                        "\n" +
                        "        // Read data into temporary array\n" +
                        "        // note: dummy extra row and column needed to make interpolation simple\n" +
                        "        oldval = new double[nf][imaxin + 1][jmaxin + 1];\n" +
                        "\n" +
                        "        for (i = 0; i < imaxin; i++) {\n" +
                        "            for (j = 0; j < jmaxin; j++) {\n" +
                        "                for (k = 0; k < nf; k++) {\n" +
                        "                    if (intokens.nextToken() == StreamTokenizer.TT_NUMBER) {\n" +
                        "                        oldval[k][i][j] = (double) intokens.nval;\n" +
                        "                    } else {\n" +
                        "                        throw new IOException();\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "\n" +
                        "        //interpolate onto finer grid\n" +
                        "\n" +
                        "        imax = (imaxin - 1) * scale + 1;\n" +
                        "        jmax = (jmaxin - 1) * scale + 1;\n" +
                        "\n" +
                        "        newval = new double[nf][imax][jmax];\n" +
                        "\n" +
                        "        for (k = 0; k < nf; k++) {\n" +
                        "            for (i = 0; i < imax; i++) {\n" +
                        "                for (j = 0; j < jmax; j++) {\n" +
                        "                    int iold = i / scale;\n" +
                        "                    int jold = j / scale;\n" +
                        "                    double xf = ((double) i % scale) / ((double) scale);\n" +
                        "                    double yf = ((double) j % scale) / ((double) scale);\n" +
                        "                    newval[k][i][j] = (1.0 - xf) * (1.0 - yf) * oldval[k][iold][jold]\n" +
                        "                            + (1.0 - xf) * yf * oldval[k][iold][jold + 1]\n" +
                        "                            + xf * (1.0 - yf) * oldval[k][iold + 1][jold]\n" +
                        "                            + xf * yf * oldval[k][iold + 1][jold + 1];\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "\n" +
                        "\n" +
                        "        //create arrays\n" +
                        "\n" +
                        "        deltat = new double[imax + 1][jmax + 2];\n" +
                        "        opg = new double[imax + 2][jmax + 2];\n" +
                        "        pg = new double[imax + 2][jmax + 2];\n" +
                        "        pg1 = new double[imax + 2][jmax + 2];\n" +
                        "        sxi = new double[imax + 2][jmax + 2];\n" +
                        "        ;\n" +
                        "        seta = new double[imax + 2][jmax + 2];\n" +
                        "        ;\n" +
                        "        tg = new double[imax + 2][jmax + 2];\n" +
                        "        tg1 = new double[imax + 2][jmax + 2];\n" +
                        "        ug = new Statevector[imax + 2][jmax + 2];\n" +
                        "        a = new double[imax][jmax];\n" +
                        "        d = new Statevector[imax + 2][jmax + 2];\n" +
                        "        f = new Statevector[imax + 2][jmax + 2];\n" +
                        "        g = new Statevector[imax + 2][jmax + 2];\n" +
                        "        r = new Statevector[imax + 2][jmax + 2];\n" +
                        "        ug1 = new Statevector[imax + 2][jmax + 2];\n" +
                        "        xnode = new double[imax][jmax];\n" +
                        "        ynode = new double[imax][jmax];\n" +
                        "\n" +
                        "        for (i = 0; i < imax + 2; ++i)\n" +
                        "            for (j = 0; j < jmax + 2; ++j) {\n" +
                        "                d[i][j] = new Statevector();\n" +
                        "                f[i][j] = new Statevector();\n" +
                        "                g[i][j] = new Statevector();\n" +
                        "                r[i][j] = new Statevector();\n" +
                        "                ug[i][j] = new Statevector();\n" +
                        "                ug1[i][j] = new Statevector();\n" +
                        "            }\n" +
                        "\n" +
                        "        /* Set farfield values (we use normalized units for everything */\n" +
                        "        cff = 1.0;\n" +
                        "        vff = 0.0;\n" +
                        "        pff = 1.0 / gamma;\n" +
                        "        rhoff = 1.0;\n" +
                        "        tff = pff / (rhoff * rgas);\n" +
                        "\n" +
                        "        // Copy the interpolated data to arrays\n" +
                        "\n" +
                        "\n" +
                        "        for (i = 0; i < imax; i++) {\n" +
                        "            for (j = 0; j < jmax; j++) {\n" +
                        "\n" +
                        "                xnode[i][j] = newval[0][i][j];\n" +
                        "                ynode[i][j] = newval[1][i][j];\n" +
                        "                ug[i + 1][j + 1].a = newval[2][i][j];\n" +
                        "                ug[i + 1][j + 1].b = newval[3][i][j];\n" +
                        "                ug[i + 1][j + 1].c = newval[4][i][j];\n" +
                        "                ug[i + 1][j + 1].d = newval[5][i][j];\n" +
                        "\n" +
                        "                scrap = ug[i + 1][j + 1].c / ug[i + 1][j + 1].a;\n" +
                        "                scrap2 = ug[i + 1][j + 1].b / ug[i + 1][j + 1].a;\n" +
                        "                tg[i + 1][j + 1] = ug[i + 1][j + 1].d / ug[i + 1][j + 1].a\n" +
                        "                        - (0.5 * (scrap * scrap + scrap2 * scrap2));\n" +
                        "                tg[i + 1][j + 1] = tg[i + 1][j + 1] / Cv;\n" +
                        "                pg[i + 1][j + 1] = rgas * ug[i + 1][j + 1].a * tg[i + 1][j + 1];\n" +
                        "\n" +
                        "            }\n" +
                        "        }\n" +
                        "\n" +
                        "\n" +
                        "        /* Calculate grid cell areas */\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j)\n" +
                        "                a[i][j] = 0.5 * ((xnode[i][j] - xnode[i - 1][j - 1])\n" +
                        "                        * (ynode[i - 1][j] - ynode[i][j - 1]) -\n" +
                        "                        (ynode[i][j] - ynode[i - 1][j - 1])\n" +
                        "                                * (xnode[i - 1][j] - xnode[i][j - 1]));\n" +
                        "        // throw away temporary arrays\n" +
                        "        oldval = newval = null;"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("doIteration",
                MethodType.methodType(void.class),
                "double scrap;\n" +
                        "        int i, j;\n" +
                        "\n" +
                        "        /* Record the old pressure values */\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j) {\n" +
                        "                opg[i][j] = pg[i][j];\n" +
                        "            }\n" +
                        "\n" +
                        "\n" +
                        "        calculateDummyCells(pg, tg, ug);\n" +
                        "        calculateDeltaT();\n" +
                        "\n" +
                        "        calculateDamping(pg, ug);\n" +
                        "\n" +
                        "        /* Do the integration */\n" +
                        "        /* Step 1 */\n" +
                        "        calculateF(pg, tg, ug);\n" +
                        "        calculateG(pg, tg, ug);\n" +
                        "        calculateR();\n" +
                        "\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j) {\n" +
                        "                ug1[i][j].a = ug[i][j].a - 0.25 * deltat[i][j] / a[i][j] * (r[i][j].a - d[i][j].a);\n" +
                        "                ug1[i][j].b = ug[i][j].b - 0.25 * deltat[i][j] / a[i][j] * (r[i][j].b - d[i][j].b);\n" +
                        "                ug1[i][j].c = ug[i][j].c - 0.25 * deltat[i][j] / a[i][j] * (r[i][j].c - d[i][j].c);\n" +
                        "                ug1[i][j].d = ug[i][j].d - 0.25 * deltat[i][j] / a[i][j] * (r[i][j].d - d[i][j].d);\n" +
                        "            }\n" +
                        "        calculateStateVar(pg1, tg1, ug1);\n" +
                        "\n" +
                        "        /* Step 2 */\n" +
                        "        calculateDummyCells(pg1, tg1, ug1);\n" +
                        "        calculateF(pg1, tg1, ug1);\n" +
                        "        calculateG(pg1, tg1, ug1);\n" +
                        "        calculateR();\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j) {\n" +
                        "                ug1[i][j].a =\n" +
                        "                        ug[i][j].a - 0.33333 * deltat[i][j] / a[i][j] * (r[i][j].a - d[i][j].a);\n" +
                        "                ug1[i][j].b =\n" +
                        "                        ug[i][j].b - 0.33333 * deltat[i][j] / a[i][j] * (r[i][j].b - d[i][j].b);\n" +
                        "                ug1[i][j].c =\n" +
                        "                        ug[i][j].c - 0.33333 * deltat[i][j] / a[i][j] * (r[i][j].c - d[i][j].c);\n" +
                        "                ug1[i][j].d =\n" +
                        "                        ug[i][j].d - 0.33333 * deltat[i][j] / a[i][j] * (r[i][j].d - d[i][j].d);\n" +
                        "            }\n" +
                        "        calculateStateVar(pg1, tg1, ug1);\n" +
                        "\n" +
                        "        /* Step 3 */\n" +
                        "        calculateDummyCells(pg1, tg1, ug1);\n" +
                        "        calculateF(pg1, tg1, ug1);\n" +
                        "        calculateG(pg1, tg1, ug1);\n" +
                        "        calculateR();\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j) {\n" +
                        "                ug1[i][j].a =\n" +
                        "                        ug[i][j].a - 0.5 * deltat[i][j] / a[i][j] * (r[i][j].a - d[i][j].a);\n" +
                        "                ug1[i][j].b =\n" +
                        "                        ug[i][j].b - 0.5 * deltat[i][j] / a[i][j] * (r[i][j].b - d[i][j].b);\n" +
                        "                ug1[i][j].c =\n" +
                        "                        ug[i][j].c - 0.5 * deltat[i][j] / a[i][j] * (r[i][j].c - d[i][j].c);\n" +
                        "                ug1[i][j].d =\n" +
                        "                        ug[i][j].d - 0.5 * deltat[i][j] / a[i][j] * (r[i][j].d - d[i][j].d);\n" +
                        "\n" +
                        "            }\n" +
                        "        calculateStateVar(pg1, tg1, ug1);\n" +
                        "\n" +
                        "        /* Step 4 (final step) */\n" +
                        "        calculateDummyCells(pg1, tg1, ug1);\n" +
                        "        calculateF(pg1, tg1, ug1);\n" +
                        "        calculateG(pg1, tg1, ug1);\n" +
                        "        calculateR();\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j) {\n" +
                        "                ug[i][j].a -= deltat[i][j] / a[i][j] * (r[i][j].a - d[i][j].a);\n" +
                        "                ug[i][j].b -= deltat[i][j] / a[i][j] * (r[i][j].b - d[i][j].b);\n" +
                        "                ug[i][j].c -= deltat[i][j] / a[i][j] * (r[i][j].c - d[i][j].c);\n" +
                        "                ug[i][j].d -= deltat[i][j] / a[i][j] * (r[i][j].d - d[i][j].d);\n" +
                        "            }\n" +
                        "        calculateStateVar(pg, tg, ug);\n" +
                        "\n" +
                        "        /* calculate RMS Pressure Error */\n" +
                        "        error = 0.0;\n" +
                        "        for (i = 1; i < imax; ++i)\n" +
                        "            for (j = 1; j < jmax; ++j) {\n" +
                        "                scrap = pg[i][j] - opg[i][j];\n" +
                        "                error += scrap * scrap;\n" +
                        "            }\n" +
                        "        error = Math.sqrt(error / (double) ((imax - 1) * (jmax - 1)));"
        ));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateStateVar",
                MethodType.methodType(void.class, double[][].class, double[][].class,
                        Statevector[][].class), "/* Calculates the new state values for range-kutta */\n" +
                "        /* Works for default values, 4/11 at 9:45 pm */ {\n" +
                "        double temp, temp2;\n" +
                "        int i, j;\n" +
                "\n" +
                "        for (i = 1; i < imax; ++i) {\n" +
                "            for (j = 1; j < jmax; ++j) {\n" +
                "                temp = localug[i][j].b;\n" +
                "                temp2 = localug[i][j].c;\n" +
                "                localtg[i][j] = localug[i][j].d / localug[i][j].a - 0.5 *\n" +
                "                        (temp * temp + temp2 * temp2) / (localug[i][j].a * localug[i][j].a);\n" +
                "\n" +
                "                localtg[i][j] = localtg[i][j] / Cv;\n" +
                "                localpg[i][j] = localug[i][j].a * rgas * localtg[i][j];\n" +
                "            }\n" +
                "        }" +
                "        }", "localpg", "localtg", "localug"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateR",
                MethodType.methodType(void.class), "/* Works for default values, straight channel (all 0's) 4/11, 9:15 pm */\n" +
                "\n" +
                "        double deltax, deltay;\n" +
                "        double temp;\n" +
                "        int i, j;\n" +
                "        Statevector scrap;\n" +
                "\n" +
                "        for (i = 1; i < imax; ++i) {\n" +
                "            for (j = 1; j < jmax; ++j) {\n" +
                "\n" +
                "                /* Start by clearing R */\n" +
                "                r[i][j].a = 0.0;\n" +
                "                r[i][j].b = 0.0;\n" +
                "                r[i][j].c = 0.0;\n" +
                "                r[i][j].d = 0.0;\n" +
                "\n" +
                "                /* East Face */\n" +
                "                deltay = (ynode[i][j] - ynode[i][j - 1]);\n" +
                "                deltax = (xnode[i][j] - xnode[i][j - 1]);\n" +
                "                temp = 0.5 * deltay;\n" +
                "                r[i][j].a += temp * (f[i][j].a + f[i + 1][j].a);\n" +
                "                r[i][j].b += temp * (f[i][j].b + f[i + 1][j].b);\n" +
                "                r[i][j].c += temp * (f[i][j].c + f[i + 1][j].c);\n" +
                "                r[i][j].d += temp * (f[i][j].d + f[i + 1][j].d);\n" +
                "\n" +
                "                temp = -0.5 * deltax;\n" +
                "                r[i][j].a += temp * (g[i][j].a + g[i + 1][j].a);\n" +
                "                r[i][j].b += temp * (g[i][j].b + g[i + 1][j].b);\n" +
                "                r[i][j].c += temp * (g[i][j].c + g[i + 1][j].c);\n" +
                "                r[i][j].d += temp * (g[i][j].d + g[i + 1][j].d);\n" +
                "\n" +
                "                /* South Face */\n" +
                "                deltay = (ynode[i][j - 1] - ynode[i - 1][j - 1]);\n" +
                "                deltax = (xnode[i][j - 1] - xnode[i - 1][j - 1]);\n" +
                "\n" +
                "                temp = 0.5 * deltay;\n" +
                "                r[i][j].a += temp * (f[i][j].a + f[i][j - 1].a);\n" +
                "                r[i][j].b += temp * (f[i][j].b + f[i][j - 1].b);\n" +
                "                r[i][j].c += temp * (f[i][j].c + f[i][j - 1].c);\n" +
                "                r[i][j].d += temp * (f[i][j].d + f[i][j - 1].d);\n" +
                "\n" +
                "                temp = -0.5 * deltax;\n" +
                "                r[i][j].a += temp * (g[i][j].a + g[i][j - 1].a);\n" +
                "                r[i][j].b += temp * (g[i][j].b + g[i][j - 1].b);\n" +
                "                r[i][j].c += temp * (g[i][j].c + g[i][j - 1].c);\n" +
                "                r[i][j].d += temp * (g[i][j].d + g[i][j - 1].d);\n" +
                "\n" +
                "                /* West Face */\n" +
                "                deltay = (ynode[i - 1][j - 1] - ynode[i - 1][j]);\n" +
                "                deltax = (xnode[i - 1][j - 1] - xnode[i - 1][j]);\n" +
                "\n" +
                "                temp = 0.5 * deltay;\n" +
                "                r[i][j].a += temp * (f[i][j].a + f[i - 1][j].a);\n" +
                "                r[i][j].b += temp * (f[i][j].b + f[i - 1][j].b);\n" +
                "                r[i][j].c += temp * (f[i][j].c + f[i - 1][j].c);\n" +
                "                r[i][j].d += temp * (f[i][j].d + f[i - 1][j].d);\n" +
                "\n" +
                "                temp = -0.5 * deltax;\n" +
                "                r[i][j].a += temp * (g[i][j].a + g[i - 1][j].a);\n" +
                "                r[i][j].b += temp * (g[i][j].b + g[i - 1][j].b);\n" +
                "                r[i][j].c += temp * (g[i][j].c + g[i - 1][j].c);\n" +
                "                r[i][j].d += temp * (g[i][j].d + g[i - 1][j].d);\n" +
                "\n" +
                "\n" +
                "                /* North Face */\n" +
                "                deltay = (ynode[i - 1][j] - ynode[i][j]);\n" +
                "                deltax = (xnode[i - 1][j] - xnode[i][j]);\n" +
                "\n" +
                "                temp = 0.5 * deltay;\n" +
                "                r[i][j].a += temp * (f[i][j].a + f[i + 1][j].a);\n" +
                "                r[i][j].b += temp * (f[i][j].b + f[i + 1][j].b);\n" +
                "                r[i][j].c += temp * (f[i][j].c + f[i + 1][j].c);\n" +
                "                r[i][j].d += temp * (f[i][j].d + f[i + 1][j].d);\n" +
                "\n" +
                "                temp = -0.5 * deltax;\n" +
                "                r[i][j].a += temp * (g[i][j].a + g[i][j + 1].a);\n" +
                "                r[i][j].b += temp * (g[i][j].b + g[i][j + 1].b);\n" +
                "                r[i][j].c += temp * (g[i][j].c + g[i][j + 1].c);\n" +
                "                r[i][j].d += temp * (g[i][j].d + g[i][j + 1].d);\n" +
                "\n" +
                "            }\n" +
                "        }"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateG",
                MethodType.methodType(void.class, double[][].class, double[][].class,
                        Statevector[][].class), "/* Works for default values 4/10: 5:15 pm */\n" +
                "        double temp, temp2, temp3;\n" +
                "        double v;\n" +
                "        int i, j;\n" +
                "\n" +
                "        for (i = 0; i < imax + 1; ++i) {\n" +
                "            for (j = 0; j < jmax + 1; ++j) {\n" +
                "                v = localug[i][j].c / localug[i][j].a;\n" +
                "                g[i][j].a = localug[i][j].c;\n" +
                "                g[i][j].b = localug[i][j].b * v;\n" +
                "                g[i][j].c = localug[i][j].c * v + localpg[i][j];\n" +
                "                temp = localug[i][j].b * localug[i][j].b;\n" +
                "                temp2 = localug[i][j].c * localug[i][j].c;\n" +
                "                temp3 = localug[i][j].a * localug[i][j].a;\n" +
                "                g[i][j].d = localug[i][j].c * (Cp * localtg[i][j] +\n" +
                "                        (0.5 * (temp + temp2) / (temp3)));\n" +
                "            }\n" +
                "        }", "localpg", "localtg", "localug"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateF",
                MethodType.methodType(void.class, double[][].class, double[][].class,
                        Statevector[][].class), "/* Works for default values 4/10: 4:50 pm */\n" +
                "        {\n" +
                "            double u;\n" +
                "            double temp1, temp2, temp3;\n" +
                "            int i, j;\n" +
                "\n" +
                "            for (i = 0; i < imax + 1; ++i) {\n" +
                "                for (j = 0; j < jmax + 1; ++j) {\n" +
                "                    u = localug[i][j].b / localug[i][j].a;\n" +
                "                    f[i][j].a = localug[i][j].b;\n" +
                "                    f[i][j].b = localug[i][j].b * u + localpg[i][j];\n" +
                "                    f[i][j].c = localug[i][j].c * u;\n" +
                "                    temp1 = localug[i][j].b * localug[i][j].b;\n" +
                "                    temp2 = localug[i][j].c * localug[i][j].c;\n" +
                "                    temp3 = localug[i][j].a * localug[i][j].a;\n" +
                "                    f[i][j].d = localug[i][j].b * (Cp * localtg[i][j] +\n" +
                "                            (0.5 * (temp1 + temp2) / (temp3)));\n" +
                "                }\n" +
                "            }\n" +
                "        }", "localpg", "localtg", "localug"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateDamping",
                MethodType.methodType(void.class, double[][].class,
                        Statevector[][].class), "        double adt, sbar;\n" +
                "        double nu2;\n" +
                "        double nu4;\n" +
                "        double tempdouble;\n" +
                "        int ascrap, i, j;\n" +
                "        Statevector temp = new Statevector();\n" +
                "        Statevector temp2 = new Statevector();\n" +
                "        Statevector scrap2 = new Statevector(), scrap4 = new Statevector();\n" +
                "\n" +
                "        nu2 = secondOrderDamping * secondOrderNormalizer;\n" +
                "        nu4 = fourthOrderDamping * fourthOrderNormalizer;\n" +
                "\n" +
                "        /* First do the pressure switches */\n" +
                "        /* Checked and works with defaults, 4/12 at 1:20 am */\n" +
                "        /* The east and west faces have been checked numerically vs.John's old */\n" +
                "        /* Scheme, and work! 4/13 @ 2:20 pm */\n" +
                "        for (i = 1; i < imax; ++i)\n" +
                "            for (j = 1; j < jmax; ++j) {\n" +
                "                sxi[i][j] = Math.abs(localpg[i + 1][j] -\n" +
                "                        2.0 * localpg[i][j] + localpg[i - 1][j]) / localpg[i][j];\n" +
                "                seta[i][j] = Math.abs(localpg[i][j + 1] -\n" +
                "                        2.0 * localpg[i][j] + localpg[i][j - 1]) / localpg[i][j];\n" +
                "            }\n" +
                "\n" +
                "        /* Then calculate the fluxes */\n" +
                "        for (i = 1; i < imax; ++i) {\n" +
                "            for (j = 1; j < jmax; ++j) {\n" +
                "\n" +
                "                /* Clear values */\n" +
                "                /* East Face */\n" +
                "                if (i > 1 && i < imax - 1) {\n" +
                "                    adt = (a[i][j] + a[i + 1][j]) / (deltat[i][j] + deltat[i + 1][j]);\n" +
                "                    sbar = (sxi[i + 1][j] + sxi[i][j]) * 0.5;\n" +
                "                } else {\n" +
                "                    adt = a[i][j] / deltat[i][j];\n" +
                "                    sbar = sxi[i][j];\n" +
                "                }\n" +
                "                tempdouble = nu2 * sbar * adt;\n" +
                "                scrap2.a = tempdouble * (localug[i + 1][j].a - localug[i][j].a);\n" +
                "                scrap2.b = tempdouble * (localug[i + 1][j].b - localug[i][j].b);\n" +
                "                scrap2.c = tempdouble * (localug[i + 1][j].c - localug[i][j].c);\n" +
                "                scrap2.d = tempdouble * (localug[i + 1][j].d - localug[i][j].d);\n" +
                "\n" +
                "                if (i > 1 && i < imax - 1) {\n" +
                "                    temp = localug[i + 2][j].svect(localug[i - 1][j]);\n" +
                "\n" +
                "                    temp2.a = 3.0 * (localug[i][j].a - localug[i + 1][j].a);\n" +
                "                    temp2.b = 3.0 * (localug[i][j].b - localug[i + 1][j].b);\n" +
                "                    temp2.c = 3.0 * (localug[i][j].c - localug[i + 1][j].c);\n" +
                "                    temp2.d = 3.0 * (localug[i][j].d - localug[i + 1][j].d);\n" +
                "\n" +
                "                    tempdouble = -nu4 * adt;\n" +
                "                    scrap4.a = tempdouble * (temp.a + temp2.a);\n" +
                "                    scrap4.b = tempdouble * (temp.a + temp2.b);\n" +
                "                    scrap4.c = tempdouble * (temp.a + temp2.c);\n" +
                "                    scrap4.d = tempdouble * (temp.a + temp2.d);\n" +
                "                } else {\n" +
                "                    scrap4.a = 0.0;\n" +
                "                    scrap4.b = 0.0;\n" +
                "                    scrap4.c = 0.0;\n" +
                "                    scrap4.d = 0.0;\n" +
                "                }\n" +
                "\n" +
                "                temp.a = scrap2.a + scrap4.a;\n" +
                "                temp.b = scrap2.b + scrap4.b;\n" +
                "                temp.c = scrap2.c + scrap4.c;\n" +
                "                temp.d = scrap2.d + scrap4.d;\n" +
                "                d[i][j] = temp;\n" +
                "\n" +
                "                /* West Face */\n" +
                "                if (i > 1 && i < imax - 1) {\n" +
                "                    adt = (a[i][j] + a[i - 1][j]) / (deltat[i][j] + deltat[i - 1][j]);\n" +
                "                    sbar = (sxi[i][j] + sxi[i - 1][j]) * 0.5;\n" +
                "                } else {\n" +
                "                    adt = a[i][j] / deltat[i][j];\n" +
                "                    sbar = sxi[i][j];\n" +
                "                }\n" +
                "\n" +
                "                tempdouble = -nu2 * sbar * adt;\n" +
                "                scrap2.a = tempdouble * (localug[i][j].a - localug[i - 1][j].a);\n" +
                "                scrap2.b = tempdouble * (localug[i][j].b - localug[i - 1][j].b);\n" +
                "                scrap2.c = tempdouble * (localug[i][j].c - localug[i - 1][j].c);\n" +
                "                scrap2.d = tempdouble * (localug[i][j].d - localug[i - 1][j].d);\n" +
                "\n" +
                "\n" +
                "                if (i > 1 && i < imax - 1) {\n" +
                "                    temp = localug[i + 1][j].svect(localug[i - 2][j]);\n" +
                "                    temp2.a = 3.0 * (localug[i - 1][j].a - localug[i][j].a);\n" +
                "                    temp2.b = 3.0 * (localug[i - 1][j].b - localug[i][j].b);\n" +
                "                    temp2.c = 3.0 * (localug[i - 1][j].c - localug[i][j].c);\n" +
                "                    temp2.d = 3.0 * (localug[i - 1][j].d - localug[i][j].d);\n" +
                "\n" +
                "                    tempdouble = nu4 * adt;\n" +
                "                    scrap4.a = tempdouble * (temp.a + temp2.a);\n" +
                "                    scrap4.b = tempdouble * (temp.a + temp2.b);\n" +
                "                    scrap4.c = tempdouble * (temp.a + temp2.c);\n" +
                "                    scrap4.d = tempdouble * (temp.a + temp2.d);\n" +
                "                } else {\n" +
                "                    scrap4.a = 0.0;\n" +
                "                    scrap4.b = 0.0;\n" +
                "                    scrap4.c = 0.0;\n" +
                "                    scrap4.d = 0.0;\n" +
                "                }\n" +
                "\n" +
                "                d[i][j].a += scrap2.a + scrap4.a;\n" +
                "                d[i][j].b += scrap2.b + scrap4.b;\n" +
                "                d[i][j].c += scrap2.c + scrap4.c;\n" +
                "                d[i][j].d += scrap2.d + scrap4.d;\n" +
                "\n" +
                "                /* North Face */\n" +
                "                if (j > 1 && j < jmax - 1) {\n" +
                "                    adt = (a[i][j] + a[i][j + 1]) / (deltat[i][j] + deltat[i][j + 1]);\n" +
                "                    sbar = (seta[i][j] + seta[i][j + 1]) * 0.5;\n" +
                "                } else {\n" +
                "                    adt = a[i][j] / deltat[i][j];\n" +
                "                    sbar = seta[i][j];\n" +
                "                }\n" +
                "                tempdouble = nu2 * sbar * adt;\n" +
                "                scrap2.a = tempdouble * (localug[i][j + 1].a - localug[i][j].a);\n" +
                "                scrap2.b = tempdouble * (localug[i][j + 1].b - localug[i][j].b);\n" +
                "                scrap2.c = tempdouble * (localug[i][j + 1].c - localug[i][j].c);\n" +
                "                scrap2.d = tempdouble * (localug[i][j + 1].d - localug[i][j].d);\n" +
                "\n" +
                "                if (j > 1 && j < jmax - 1) {\n" +
                "                    temp = localug[i][j + 2].svect(localug[i][j - 1]);\n" +
                "                    temp2.a = 3.0 * (localug[i][j].a - localug[i][j + 1].a);\n" +
                "                    temp2.b = 3.0 * (localug[i][j].b - localug[i][j + 1].b);\n" +
                "                    temp2.c = 3.0 * (localug[i][j].c - localug[i][j + 1].c);\n" +
                "                    temp2.d = 3.0 * (localug[i][j].d - localug[i][j + 1].d);\n" +
                "\n" +
                "                    tempdouble = -nu4 * adt;\n" +
                "                    scrap4.a = tempdouble * (temp.a + temp2.a);\n" +
                "                    scrap4.b = tempdouble * (temp.a + temp2.b);\n" +
                "                    scrap4.c = tempdouble * (temp.a + temp2.c);\n" +
                "                    scrap4.d = tempdouble * (temp.a + temp2.d);\n" +
                "                } else {\n" +
                "                    scrap4.a = 0.0;\n" +
                "                    scrap4.b = 0.0;\n" +
                "                    scrap4.c = 0.0;\n" +
                "                    scrap4.d = 0.0;\n" +
                "                }\n" +
                "                d[i][j].a += scrap2.a + scrap4.a;\n" +
                "                d[i][j].b += scrap2.b + scrap4.b;\n" +
                "                d[i][j].c += scrap2.c + scrap4.c;\n" +
                "                d[i][j].d += scrap2.d + scrap4.d;\n" +
                "\n" +
                "                /* South Face */\n" +
                "                if (j > 1 && j < jmax - 1) {\n" +
                "                    adt = (a[i][j] + a[i][j - 1]) / (deltat[i][j] + deltat[i][j - 1]);\n" +
                "                    sbar = (seta[i][j] + seta[i][j - 1]) * 0.5;\n" +
                "                } else {\n" +
                "                    adt = a[i][j] / deltat[i][j];\n" +
                "                    sbar = seta[i][j];\n" +
                "                }\n" +
                "                tempdouble = -nu2 * sbar * adt;\n" +
                "                scrap2.a = tempdouble * (localug[i][j].a - localug[i][j - 1].a);\n" +
                "                scrap2.b = tempdouble * (localug[i][j].b - localug[i][j - 1].b);\n" +
                "                scrap2.c = tempdouble * (localug[i][j].c - localug[i][j - 1].c);\n" +
                "                scrap2.d = tempdouble * (localug[i][j].d - localug[i][j - 1].d);\n" +
                "\n" +
                "                if (j > 1 && j < jmax - 1) {\n" +
                "                    temp = localug[i][j + 1].svect(localug[i][j - 2]);\n" +
                "                    temp2.a = 3.0 * (localug[i][j - 1].a - localug[i][j].a);\n" +
                "                    temp2.b = 3.0 * (localug[i][j - 1].b - localug[i][j].b);\n" +
                "                    temp2.c = 3.0 * (localug[i][j - 1].c - localug[i][j].c);\n" +
                "                    temp2.d = 3.0 * (localug[i][j - 1].d - localug[i][j].d);\n" +
                "\n" +
                "                    tempdouble = nu4 * adt;\n" +
                "                    scrap4.a = tempdouble * (temp.a + temp2.a);\n" +
                "                    scrap4.b = tempdouble * (temp.a + temp2.b);\n" +
                "                    scrap4.c = tempdouble * (temp.a + temp2.c);\n" +
                "                    scrap4.d = tempdouble * (temp.a + temp2.d);\n" +
                "                } else {\n" +
                "                    scrap4.a = 0.0;\n" +
                "                    scrap4.b = 0.0;\n" +
                "                    scrap4.c = 0.0;\n" +
                "                    scrap4.d = 0.0;\n" +
                "                }\n" +
                "                d[i][j].a += scrap2.a + scrap4.a;\n" +
                "                d[i][j].b += scrap2.b + scrap4.b;\n" +
                "                d[i][j].c += scrap2.c + scrap4.c;\n" +
                "                d[i][j].d += scrap2.d + scrap4.d;\n" +
                "            }\n" +
                "        }", "localpg", "localug"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateDeltaT",
                MethodType.methodType(void.class), "        double xeta, yeta, xxi, yxi;              /* Local change in x and y */\n" +
                "        int i, j;\n" +
                "        double mint;\n" +
                "        double c, q, r;\n" +
                "        double safety_factor = 0.7;\n" +
                "\n" +
                "        for (i = 1; i < imax; ++i)\n" +
                "            for (j = 1; j < jmax; ++j) {\n" +
                "                xxi = (xnode[i][j] - xnode[i - 1][j]\n" +
                "                        + xnode[i][j - 1] - xnode[i - 1][j - 1]) * 0.5;\n" +
                "                yxi = (ynode[i][j] - ynode[i - 1][j]\n" +
                "                        + ynode[i][j - 1] - ynode[i - 1][j - 1]) * 0.5;\n" +
                "                xeta = (xnode[i][j] - xnode[i][j - 1]\n" +
                "                        + xnode[i - 1][j] - xnode[i - 1][j - 1]) * 0.5;\n" +
                "                yeta = (ynode[i][j] - ynode[i][j - 1]\n" +
                "                        + ynode[i - 1][j] - ynode[i - 1][j - 1]) * 0.5;\n" +
                "\n" +
                "                q = (yeta * ug[i][j].b - xeta * ug[i][j].c);\n" +
                "                r = (-yxi * ug[i][j].b + xxi * ug[i][j].c);\n" +
                "                c = Math.sqrt(gamma * rgas * tg[i][j]);\n" +
                "\n" +
                "                deltat[i][j] = safety_factor * 2.8284 * a[i][j] /\n" +
                "\n" +
                "                        ((Math.abs(q) + Math.abs(r)) / ug[i][j].a + c *\n" +
                "                                Math.sqrt(xxi * xxi + yxi * yxi + xeta * xeta + yeta * yeta +\n" +
                "                                        2.0 * Math.abs(xeta * xxi + yeta * yxi)));\n" +
                "            }\n" +
                "\n" +
                "        /* If that's the user's choice, make it time accurate */\n" +
                "        if (ntime == 1) {\n" +
                "            mint = 100000.0;\n" +
                "            for (i = 1; i < imax; ++i)\n" +
                "                for (j = 1; j < jmax; ++j)\n" +
                "                    if (deltat[i][j] < mint)\n" +
                "                        mint = deltat[i][j];\n" +
                "\n" +
                "            for (i = 1; i < imax; ++i)\n" +
                "                for (j = 1; j < jmax; ++j)\n" +
                "                    deltat[i][j] = mint;\n" +
                "        }"));

        transaction.addMethod(Tunnel.class, new jmplib.reflect.Method("calculateDummyCells",
                MethodType.methodType(void.class, double[][].class, double[][].class,
                        Statevector[][].class), "        double c;\n" +
                "        double jminus;\n" +
                "        double jplus;\n" +
                "        double s;\n" +
                "        double rho, temp, u, v;\n" +
                "        double scrap, scrap2;\n" +
                "        double theta;\n" +
                "        double uprime;\n" +
                "        int i, j;\n" +
                "        Vector2 norm = new Vector2();\n" +
                "        Vector2 tan = new Vector2();\n" +
                "        Vector2 u1 = new Vector2();\n" +
                "\n" +
                "        uff = machff;\n" +
                "        jplusff = uff + 2.0 / (gamma - 1.0) * cff;\n" +
                "        jminusff = uff - 2.0 / (gamma - 1.0) * cff;\n" +
                "\n" +
                "        for (i = 1; i < imax; ++i) {\n" +
                "            /* Bottom wall boundary cells */\n" +
                "            /* Routine checked by brute force for initial conditions, 4/9; 4:30 */\n" +
                "            /* Routine checked by brute force for random conditions, 4/13, 4:40 pm */\n" +
                "            /* Construct tangent vectors */\n" +
                "            tan.ihat = xnode[i][0] - xnode[i - 1][0];\n" +
                "            tan.jhat = ynode[i][0] - ynode[i - 1][0];\n" +
                "            norm.ihat = -(ynode[i][0] - ynode[i - 1][0]);\n" +
                "            norm.jhat = xnode[i][0] - xnode[i - 1][0];\n" +
                "\n" +
                "            scrap = tan.magnitude();\n" +
                "            tan.ihat = tan.ihat / scrap;\n" +
                "            tan.jhat = tan.jhat / scrap;\n" +
                "            scrap = norm.magnitude();\n" +
                "            norm.ihat = norm.ihat / scrap;\n" +
                "            norm.jhat = norm.jhat / scrap;\n" +
                "\n" +
                "            /* now set some state variables */\n" +
                "            rho = localug[i][1].a;\n" +
                "            localtg[i][0] = localtg[i][1];\n" +
                "            u1.ihat = localug[i][1].b / rho;\n" +
                "            u1.jhat = localug[i][1].c / rho;\n" +
                "\n" +
                "            u = u1.dot(tan) + u1.dot(norm) * tan.jhat / norm.jhat;\n" +
                "            u = u / (tan.ihat - (norm.ihat * tan.jhat / norm.jhat));\n" +
                "\n" +
                "            v = -(u1.dot(norm) + u * norm.ihat) / norm.jhat;\n" +
                "\n" +
                "            /* And construct the new state vector */\n" +
                "            localug[i][0].a = localug[i][1].a;\n" +
                "            localug[i][0].b = rho * u;\n" +
                "            localug[i][0].c = rho * v;\n" +
                "            localug[i][0].d = rho * (Cv * localtg[i][0] + 0.5 * (u * u + v * v));\n" +
                "            localpg[i][0] = localpg[i][1];\n" +
                "\n" +
                "            /* Top Wall Boundary Cells */\n" +
                "            /* Checked numerically for default conditions, 4/9 at 5:30 pm */\n" +
                "            /* Construct normal and tangent vectors */\n" +
                "            /* This part checked and works; it produces the correct vectors */\n" +
                "            tan.ihat = xnode[i][jmax - 1] - xnode[i - 1][jmax - 1];\n" +
                "            tan.jhat = ynode[i][jmax - 1] - ynode[i - 1][jmax - 1];\n" +
                "            norm.ihat = ynode[i][jmax - 1] - ynode[i - 1][jmax - 1];\n" +
                "            norm.jhat = -(xnode[i][jmax - 1] - xnode[i - 1][jmax - 1]);\n" +
                "\n" +
                "            scrap = tan.magnitude();\n" +
                "            tan.ihat = tan.ihat / scrap;\n" +
                "            tan.jhat = tan.jhat / scrap;\n" +
                "            scrap = norm.magnitude();\n" +
                "            norm.ihat = norm.ihat / scrap;\n" +
                "            norm.jhat = norm.jhat / scrap;\n" +
                "\n" +
                "            /* now set some state variables */\n" +
                "            rho = localug[i][jmax - 1].a;\n" +
                "            temp = localtg[i][jmax - 1];\n" +
                "            u1.ihat = localug[i][jmax - 1].b / rho;\n" +
                "            u1.jhat = localug[i][jmax - 1].c / rho;\n" +
                "\n" +
                "            u = u1.dot(tan) + u1.dot(norm) * tan.jhat / norm.jhat;\n" +
                "            u = u / (tan.ihat - (norm.ihat * tan.jhat / norm.jhat));\n" +
                "\n" +
                "            v = -(u1.dot(norm) + u * norm.ihat) / norm.jhat;\n" +
                "\n" +
                "            /* And construct the new state vector */\n" +
                "            localug[i][jmax].a = localug[i][jmax - 1].a;\n" +
                "            localug[i][jmax].b = rho * u;\n" +
                "            localug[i][jmax].c = rho * v;\n" +
                "            localug[i][jmax].d = rho * (Cv * temp + 0.5 * (u * u + v * v));\n" +
                "            localtg[i][jmax] = temp;\n" +
                "            localpg[i][jmax] = localpg[i][jmax - 1];\n" +
                "        }\n" +
                "\n" +
                "        for (j = 1; j < jmax; ++j) {\n" +
                "            /* Inlet Boundary Cells: unchecked */\n" +
                "            /* Construct the normal vector; This works, 4/10, 2:00 pm */\n" +
                "            norm.ihat = ynode[0][j - 1] - ynode[0][j];\n" +
                "            norm.jhat = xnode[0][j] - xnode[0][j - 1];\n" +
                "            scrap = norm.magnitude();\n" +
                "            norm.ihat = norm.ihat / scrap;\n" +
                "            norm.jhat = norm.jhat / scrap;\n" +
                "            theta = Math.acos((ynode[0][j - 1] - ynode[0][j]) /\n" +
                "                    Math.sqrt((xnode[0][j] - xnode[0][j - 1]) * (xnode[0][j] - xnode[0][j - 1])\n" +
                "                            + (ynode[0][j - 1] - ynode[0][j]) * (ynode[0][j - 1] - ynode[0][j])));\n" +
                "\n" +
                "            u1.ihat = localug[1][j].b / localug[1][j].a;\n" +
                "            u1.jhat = localug[1][j].c / localug[1][j].a;\n" +
                "            uprime = u1.ihat * Math.cos(theta);\n" +
                "            c = Math.sqrt(gamma * rgas * localtg[1][j]);\n" +
                "            /* Supersonic inflow; works on the initial cond, 4/10 at 3:10 pm */\n" +
                "            if (uprime < -c) {\n" +
                "                /* Use far field conditions */\n" +
                "                localug[0][j].a = rhoff;\n" +
                "                localug[0][j].b = rhoff * uff;\n" +
                "                localug[0][j].c = rhoff * vff;\n" +
                "                localug[0][j].d = rhoff * (Cv * tff + 0.5 * (uff * uff + vff * vff));\n" +
                "                localtg[0][j] = tff;\n" +
                "                localpg[0][j] = pff;\n" +
                "            }\n" +
                "            /* Subsonic inflow */\n" +
                "            /* This works on the initial conditions 4/10 @ 2:20 pm */\n" +
                "            else if (uprime < 0.0) {\n" +
                "                /* Calculate Riemann invarients here */\n" +
                "                jminus = u1.ihat - 2.0 / (gamma - 1.0) * c;\n" +
                "                s = Math.log(pff) - gamma * Math.log(rhoff);\n" +
                "                v = vff;\n" +
                "\n" +
                "                u = (jplusff + jminus) / 2.0;\n" +
                "                scrap = (jplusff - u) * (gamma - 1.0) * 0.5;\n" +
                "                localtg[0][j] = (1.0 / (gamma * rgas)) * scrap * scrap;\n" +
                "                localpg[0][j] = Math.exp(s) / Math.pow((rgas * localtg[0][j]), gamma);\n" +
                "                localpg[0][j] = Math.pow(localpg[0][j], 1.0 / (1.0 - gamma));\n" +
                "\n" +
                "                /* And now: construct the new state vector */\n" +
                "                localug[0][j].a = localpg[0][j] / (rgas * localtg[0][j]);\n" +
                "                localug[0][j].b = localug[0][j].a * u;\n" +
                "                localug[0][j].c = localug[0][j].a * v;\n" +
                "                localug[0][j].d = localug[0][j].a * (Cv * tff + 0.5 * (u * u + v * v));\n" +
                "            }\n" +
                "            /* Other options */\n" +
                "            /* We should throw an exception here */\n" +
                "            else {\n" +
                "                System.err.println(\"You have outflow at the inlet, which is not allowed.\");\n" +
                "            }\n" +
                "\n" +
                "            /* Outlet Boundary Cells */\n" +
                "            /* Construct the normal vector; works, 4/10 3:10 pm */\n" +
                "            norm.ihat = ynode[0][j] - ynode[0][j - 1];\n" +
                "            norm.jhat = xnode[0][j - 1] - xnode[0][j];\n" +
                "            scrap = norm.magnitude();\n" +
                "            norm.ihat = norm.ihat / scrap;\n" +
                "            norm.jhat = norm.jhat / scrap;\n" +
                "            scrap = xnode[0][j - 1] - xnode[0][j];\n" +
                "            scrap2 = ynode[0][j] - ynode[0][j - 1];\n" +
                "            theta = Math.acos((ynode[0][j] - ynode[0][j - 1]) /\n" +
                "                    Math.sqrt(scrap * scrap + scrap2 * scrap2));\n" +
                "\n" +
                "            u1.ihat = localug[imax - 1][j].b / localug[imax - 1][j].a;\n" +
                "            u1.jhat = localug[imax - 1][j].c / localug[imax - 1][j].a;\n" +
                "            uprime = u1.ihat * Math.cos(theta);\n" +
                "            c = Math.sqrt(gamma * rgas * localtg[imax - 1][j]);\n" +
                "            /* Supersonic outflow; works for defaults cond, 4/10: 3:10 pm */\n" +
                "            if (uprime > c) {\n" +
                "                /* Use a backward difference 2nd order derivative approximation */\n" +
                "                /* To set values at exit */\n" +
                "                localug[imax][j].a = 2.0 * localug[imax - 1][j].a - localug[imax - 2][j].a;\n" +
                "                localug[imax][j].b = 2.0 * localug[imax - 1][j].b - localug[imax - 2][j].b;\n" +
                "                localug[imax][j].c = 2.0 * localug[imax - 1][j].c - localug[imax - 2][j].c;\n" +
                "                localug[imax][j].d = 2.0 * localug[imax - 1][j].d - localug[imax - 2][j].d;\n" +
                "                localpg[imax][j] = 2.0 * localpg[imax - 1][j] - localpg[imax - 2][j];\n" +
                "                localtg[imax][j] = 2.0 * localtg[imax - 1][j] - localtg[imax - 2][j];\n" +
                "            }\n" +
                "            /* Subsonic Outflow; works for defaults cond, 4/10: 3:10 pm */\n" +
                "            else if (uprime < c && uprime > 0) {\n" +
                "                jplus = u1.ihat + 2.0 / (gamma - 1) * c;\n" +
                "                v = localug[imax - 1][j].c / localug[imax - 1][j].a;\n" +
                "                s = Math.log(localpg[imax - 1][j]) -\n" +
                "                        gamma * Math.log(localug[imax - 1][j].a);\n" +
                "\n" +
                "                u = (jplus + jminusff) / 2.0;\n" +
                "                scrap = (jplus - u) * (gamma - 1.0) * 0.5;\n" +
                "                localtg[imax][j] = (1.0 / (gamma * rgas)) * scrap * scrap;\n" +
                "                localpg[imax][j] = Math.exp(s) /\n" +
                "                        Math.pow((rgas * localtg[imax][j]), gamma);\n" +
                "                localpg[imax][j] = Math.pow(localpg[imax][j], 1.0 / (1.0 - gamma));\n" +
                "                rho = localpg[imax][j] / (rgas * localtg[imax][j]);\n" +
                "\n" +
                "                /* And now, construct the new state vector */\n" +
                "                localug[imax][j].a = rho;\n" +
                "                localug[imax][j].b = rho * u;\n" +
                "                localug[imax][j].c = rho * v;\n" +
                "                localug[imax][j].d = rho * (Cv * localtg[imax][j] + 0.5 * (u * u + v * v));\n" +
                "\n" +
                "            }\n" +
                "            /* Other cases that shouldn't have to be used. */\n" +
                "            else if (uprime < -c) {\n" +
                "                /* Supersonic inflow */\n" +
                "                /* Use far field conditions */\n" +
                "                localug[0][j].a = rhoff;\n" +
                "                localug[0][j].b = rhoff * uff;\n" +
                "                localug[0][j].c = rhoff * vff;\n" +
                "                localug[0][j].d = rhoff * (Cv * tff + 0.5 * (uff * uff + vff * vff));\n" +
                "                localtg[0][j] = tff;\n" +
                "                localpg[0][j] = pff;\n" +
                "            }\n" +
                "            /* Subsonic inflow */\n" +
                "            /* This works on the initial conditions 4/10 @ 2:20 pm */\n" +
                "            else if (uprime < 0.0) {\n" +
                "                /* Debug: throw exception here? */\n" +
                "                /* Calculate Riemann invarients here */\n" +
                "                jminus = u1.ihat - 2.0 / (gamma - 1.0) * c;\n" +
                "                s = Math.log(pff) - gamma * Math.log(rhoff);\n" +
                "                v = vff;\n" +
                "\n" +
                "                u = (jplusff + jminus) / 2.0;\n" +
                "                scrap = (jplusff - u) * (gamma - 1.0) * 0.5;\n" +
                "                localtg[0][j] = (1.0 / (gamma * rgas)) * scrap * scrap;\n" +
                "                localpg[0][j] = Math.exp(s) / Math.pow((rgas * localtg[0][j]), gamma);\n" +
                "                localpg[0][j] = Math.pow(localpg[0][j], 1.0 / (1.0 - gamma));\n" +
                "\n" +
                "                /* And now: construct the new state vector */\n" +
                "                localug[0][j].a = localpg[0][j] / (rgas * localtg[0][j]);\n" +
                "                localug[0][j].b = localug[0][j].a * u;\n" +
                "                localug[0][j].c = localug[0][j].a * v;\n" +
                "                localug[0][j].d = localug[0][j].a * (Cv * tff + 0.5 * (u * u + v * v));\n" +
                "            }\n" +
                "            /* Other Options */\n" +
                "            /* Debug: throw exception here? */\n" +
                "            else {\n" +
                "                System.err.println(\"You have inflow at the outlet, which is not allowed.\");\n" +
                "            }\n" +
                "        }\n" +
                "        /* Do something with corners to avoid division by zero errors */\n" +
                "        /* What you do shouldn't matter */\n" +
                "        localug[0][0] = localug[1][0];\n" +
                "        localug[imax][0] = localug[imax][1];\n" +
                "        localug[0][jmax] = localug[1][jmax];\n" +
                "        localug[imax][jmax] = localug[imax][jmax - 1];", "localpg",
                "localtg", "localug"));

        transaction.replaceImplementation(Tunnel.class, new jmplib.reflect.Method("runiters",
                MethodType.methodType(void.class),
                "        for (int i = 0; i < iter; i++) {\n" +
                        "            doIteration();\n" +
                        "        }"));

        // transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "size"));
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int[].class, "datasizes", "new int []{8, 12}"));
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "machff", "0.7"));    /* Inflow mach number */

        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "secondOrderDamping", "1.0"));
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PUBLIC, double.class, "fourthOrderDamping", "1.0"));
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "ntime", "1")); /* 0 = local timestep, 1 = time accurate */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "scale")); /* Refine input grid by this factor */
        // transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "error"));

        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "a"));   /* Grid cell area */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "deltat"));   /* Timestep */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "opg"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "pg"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "pg1")); /* Pressure */

        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "sxi"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "seta"));
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "tg"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "tg1"));                           /* Temperature */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "xnode"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double[][].class, "ynode"));     /* Storage of node coordinates */

        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double[][][].class, "oldval"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double[][][].class, "newval"));
        /* Tepmoray arrays for interpolation */

        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "cff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "uff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "vff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "pff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "rhoff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "tff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "jplusff"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "jminusff"));

        /* Far field values */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "datamax"),
                new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "datamin"));
        //  transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "iter", "100")); /* Number of iterations */
        //  transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "imax"),
        //          new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "jmax"));
        /* Number of nodes in x and y direction*/
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "imaxin"),
                new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "jmaxin"));
        /* Number of nodes in x and y direction in unscaled data */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "nf", "6")); /* Number of fields in data file */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, Statevector[][].class, "d"));   /* Damping coefficients */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, Statevector[][].class, "f")
                ,
                new jmplib.reflect.Field(Modifier.PRIVATE, Statevector[][].class, "g"));   /* Flux Vectors */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, Statevector[][].class, "r"),
                new jmplib.reflect.Field(Modifier.PRIVATE, Statevector[][].class, "ug1"));
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.PRIVATE, Statevector[][].class, "ug"));      /* Storage of data */

        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.FINAL, double.class, "Cp", "1004.5"));      /* specific heat, const pres. */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.FINAL, double.class, "Cv", "717.5"));      /* specific heat, const vol. */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.FINAL, double.class, "gamma", "1.4"));   /* Ratio of specific heats */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.FINAL, double.class, "rgas", "287.0"));       /* Gas Constant */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.FINAL, double.class, "fourthOrderNormalizer", "0.02")); /* Damping coefficients */
        transaction.addField(Tunnel.class, new jmplib.reflect.Field(Modifier.FINAL, double.class, "secondOrderNormalizer", "0.02"));

        transaction.commit();
    }

    public void JGFinitialise() {
        try {
            prepare();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        JGFInstrumentor.startTimer("Section3:Euler:Init");

        try {
            initialise();
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file tunnel.dat");
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            System.err.println("IOException in initialisation");
            System.exit(0);
        }

        JGFInstrumentor.stopTimer("Section3:Euler:Init");

    }

    public void JGFapplication() {

        JGFInstrumentor.startTimer("Section3:Euler:Run");

        runiters();

        JGFInstrumentor.stopTimer("Section3:Euler:Run");

    }


    public void JGFvalidate() {
        double refval[] = {0.0033831416599344965, 0.006812543658280322};
        double dev = Math.abs(error - refval[size]);
        if (dev > 1.0e-12) {
            System.out.println("Validation failed");
            System.out.println("Computed RMS pressure error = " + error);
            System.out.println("Reference value = " + refval[size]);
        }
    }

    public void JGFtidyup() {
    /*    a = null;
        deltat = null;
        opg = null;
        pg = null;
        pg1 = null;
        sxi = null;
        seta = null;
        tg = null;
        tg1 = null;
        xnode = null;
        ynode = null;
        d = null;
        f = null;
        g = null;
        r = null;
        ug1 = null;
        ug = null;*/

        System.gc();
    }


    public void JGFrun(int size) {

        JGFInstrumentor.addTimer("Section3:Euler:Total", "Solutions", size);
        JGFInstrumentor.addTimer("Section3:Euler:Init", "Gridpoints", size);
        JGFInstrumentor.addTimer("Section3:Euler:Run", "Timesteps", size);

        JGFsetsize(size);

        JGFInstrumentor.startTimer("Section3:Euler:Total");

        JGFinitialise();
        JGFapplication();
        JGFvalidate();
        JGFtidyup();

        JGFInstrumentor.stopTimer("Section3:Euler:Total");

        JGFInstrumentor.addOpsToTimer("Section3:Euler:Init", (double) (imax * jmax));
        JGFInstrumentor.addOpsToTimer("Section3:Euler:Run", (double) iter);
        JGFInstrumentor.addOpsToTimer("Section3:Euler:Total", 1);

        JGFInstrumentor.printTimer("Section3:Euler:Init");
        JGFInstrumentor.printTimer("Section3:Euler:Run");
        JGFInstrumentor.printTimer("Section3:Euler:Total");
    }


}
 
