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


package es.uniovi.jmplib.testing.javagrande.search_meta;

import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFInstrumentor;
import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFSection3;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;


public class JGFSearchBench extends SearchGame implements JGFSection3 {

    private int size;

    public void JGFsetsize(int size) {
        this.size = size;
    }

    public void prepare() throws StructuralIntercessionException {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();

        transaction.replaceImplementation(SearchGame.class, new jmplib.reflect.Method("solve",
                MethodType.methodType(int.class), "        int i, side;\n" +
                "        int x, work, score;\n" +
                "        long poscnt;\n" +
                "\n" +
                "        nodes = 0L;\n" +
                "        msecs = 1L;\n" +
                "        side = (plycnt + 1) & 1;\n" +
                "        for (i = 0; ++i <= 7; )\n" +
                "            if (height[i] <= 6) {\n" +
                "                if (wins(i, height[i], 1 << side) || colthr[columns[i]] == (1 << side))\n" +
                "                    return (side != 0 ? WIN : LOSE) << 5;    // all score, no work:)\n" +
                "            }\n" +
                "        if ((x = transpose()) != ABSENT) {\n" +
                "            if ((x & 32) == 0)   // exact score\n" +
                "                return x;\n" +
                "        }\n" +
                "        JGFInstrumentor.startTimer(\"Section3:AlphaBetaSearch:Run\");\n" +
                "        score = ab(LOSE, WIN);\n" +
                "        poscnt = posed;\n" +
                "        for (work = 1; (poscnt >>= 1) != 0; work++) ; //work = log of #positions stored\n" +
                "        JGFInstrumentor.stopTimer(\"Section3:AlphaBetaSearch:Run\");\n" +
                "        return score << 5 | work;"));

        transaction.replaceImplementation(SearchGame.class, new jmplib.reflect.Method("ab",
                MethodType.methodType(int.class, int.class, int.class), "        int besti, i, j, h, k, l, val, score;\n" +
                "        int x, v, work;\n" +
                "        int nav, av[] = new int[8];\n" +
                "        long poscnt;\n" +
                "        int side, otherside;\n" +
                "\n" +
                "        nodes++;\n" +
                "        if (plycnt == 41)\n" +
                "            return DRAW;\n" +
                "        side = (otherside = plycnt & 1) ^ 1;\n" +
                "        for (i = nav = 0; ++i <= 7; ) {\n" +
                "            if ((h = height[i]) <= 6) {\n" +
                "                if (wins(i, h, 3) || colthr[columns[i]] != 0) {\n" +
                "                    if (h + 1 <= 6 && wins(i, h + 1, 1 << otherside))\n" +
                "                        return LOSE;        // for 'o'\n" +
                "                    av[0] = i;        // forget other moves\n" +
                "                    while (++i <= 7)\n" +
                "                        if ((h = height[i]) <= 6 &&\n" +
                "                                (wins(i, h, 3) || colthr[columns[i]] != 0))\n" +
                "                            return LOSE;\n" +
                "                    nav = 1;\n" +
                "                    break;\n" +
                "                }\n" +
                "                if (!(h + 1 <= 6 && wins(i, h + 1, 1 << otherside)))\n" +
                "                    av[nav++] = i;\n" +
                "            }\n" +
                "        }\n" +
                "        if (nav == 0)\n" +
                "            return LOSE;\n" +
                "        if (nav == 1) {\n" +
                "            makemove(av[0]);\n" +
                "            score = -ab(-beta, -alpha);\n" +
                "            backmove();\n" +
                "            return score;\n" +
                "        }\n" +
                "        if ((x = transpose()) != ABSENT) {\n" +
                "            score = x >> 5;\n" +
                "            if (score == DRAWLOSE) {\n" +
                "                if ((beta = DRAW) <= alpha)\n" +
                "                    return score;\n" +
                "            } else if (score == DRAWWIN) {\n" +
                "                if ((alpha = DRAW) >= beta)\n" +
                "                    return score;\n" +
                "            } else return score; // exact score\n" +
                "        }\n" +
                "        poscnt = posed;\n" +
                "        l = besti = 0;    // initialize arbitrarily for silly javac\n" +
                "        score = Integer.MIN_VALUE;    // try to get the best bound if score > beta\n" +
                "        for (i = 0; i < nav; i++) {\n" +
                "            for (j = i, val = Integer.MIN_VALUE; j < nav; j++) {\n" +
                "                k = av[j];\n" +
                "                v = history[side][height[k] << 3 | k];\n" +
                "                if (v > val) {\n" +
                "                    val = v;\n" +
                "                    l = j;\n" +
                "                }\n" +
                "            }\n" +
                "            j = av[l];\n" +
                "            if (i != l) {\n" +
                "                av[l] = av[i];\n" +
                "                av[i] = j;\n" +
                "            }\n" +
                "            makemove(j);\n" +
                "            val = -ab(-beta, -alpha);\n" +
                "            backmove();\n" +
                "            if (val > score) {\n" +
                "                besti = i;\n" +
                "                if ((score = val) > alpha && (alpha = val) >= beta) {\n" +
                "                    if (score == DRAW && i < nav - 1)\n" +
                "                        score = DRAWWIN;\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        if (besti > 0) {\n" +
                "            for (i = 0; i < besti; i++)\n" +
                "                history[side][height[av[i]] << 3 | av[i]]--;    // punish bad historiess\n" +
                "            history[side][height[av[besti]] << 3 | av[besti]] += besti;\n" +
                "        }\n" +
                "        poscnt = posed - poscnt;\n" +
                "        for (work = 1; (poscnt >>= 1) != 0; work++) ;    // work=log #positions stored\n" +
                "        if (x != ABSENT) {\n" +
                "            if (score == -(x >> 5))    // combine < and >\n" +
                "                score = DRAW;\n" +
                "            transrestore(score, work);\n" +
                "        } else transtore(score, work);\n" +
                "        // if (plycnt == REPORTPLY) {\n" +
                "        //  System.out.println(toString() + \"##-<=>+#\".charAt(4+score) + work);\n" +
                "        //}\n" +
                "        return score;\n" +
                "    }", "alpha", "beta"));


        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("emptyTT",
                MethodType.methodType(void.class), "        int i, h, work;\n" +
                "\n" +
                "        for (i = 0; i < TRANSIZE; i++)\n" +
                "            if ((work = (h = he[i]) & 31) < 31)           // bytes are signed!!!\n" +
                "                he[i] = (byte) (h - (work < 16 ? work : 4));     // work = work monus 4\n" +
                "        posed = hits = 0;"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("hitRate",
                MethodType.methodType(double.class),
                "return posed != 0 ? (double) hits / (double) posed : 0.0;"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("hash",
                MethodType.methodType(void.class), "int t1, t2;\n" +
                "\n" +
                "        long htemp;\n" +
                "\n" +
                "        t1 = (columns[1] << 7 | columns[2]) << 7 | columns[3];\n" +
                "        t2 = (columns[7] << 7 | columns[6]) << 7 | columns[5];\n" +
                "\n" +
                "        htemp = t1 > t2 ? (long) (t1 << 7 | columns[4]) << 21 | t2 :\n" +
                "                (long) (t2 << 7 | columns[4]) << 21 | t1;\n" +
                "        lock = (int) (htemp >> 17);\n" +
                "        htindex = (int) (htemp % TRANSIZE);\n" +
                "        stride = NSAMELOCK + lock % STRIDERANGE;\n" +
                "        if (lock < 0) {        // can't take unsigned mod in Java :(\n" +
                "            if ((stride += INTMODSTRIDERANGE) < NSAMELOCK)\n" +
                "                stride += STRIDERANGE;\n" +
                "        }"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("transpose",
                MethodType.methodType(int.class), "        hash();\n" +
                "        for (int x = htindex, i = 0; i < PROBES; i++) {\n" +
                "            if (ht[x] == lock)\n" +
                "                return he[x];\n" +
                "            if ((x += stride) >= TRANSIZE)\n" +
                "                x -= TRANSIZE;\n" +
                "        }\n" +
                "        return ABSENT;"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("result",
                MethodType.methodType(String.class), "        int x;\n" +
                "\n" +
                "        return (x = transpose()) == ABSENT ? \"n/a\" : result(x);"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("result",
                MethodType.methodType(String.class, int.class),
                "return \"\" + \"##-<=>+#\".charAt(4 + (x >> 5)) + \"(\" + (x & 31) + \")\";"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("transrestore",
                MethodType.methodType(void.class, int.class, int.class), "        int i, x;\n" +
                "\n" +
                "        if (work > 31)\n" +
                "            work = 31;\n" +
                "        posed++;\n" +
                "        hash();\n" +
                "        for (x = htindex, i = 0; i < PROBES; i++) {\n" +
                "            if (ht[x] == lock) {\n" +
                "                hits++;\n" +
                "                he[x] = (byte) (score << 5 | work);\n" +
                "                return;\n" +
                "            }\n" +
                "            if ((x += stride) >= TRANSIZE)\n" +
                "                x -= TRANSIZE;\n" +
                "        }\n" +
                "        transput(score, work);"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("transtore",
                MethodType.methodType(void.class, int.class, int.class), "        if (work > 31)\n" +
                "            work = 31;\n" +
                "        posed++;\n" +
                "        hash();\n" +
                "        transput(score, work);"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("transput",
                MethodType.methodType(void.class, int.class, int.class), "        for (int x = htindex, i = 0; i < PROBES; i++) {\n" +
                "            if (work > (he[x] & 31)) {\n" +
                "                hits++;\n" +
                "                ht[x] = lock;\n" +
                "                he[x] = (byte) (score << 5 | work);\n" +
                "                return;\n" +
                "            }\n" +
                "            if ((x += stride) >= TRANSIZE)\n" +
                "                x -= TRANSIZE;\n" +
                "        }"));
        transaction.replaceImplementation(TransGame.class, new jmplib.reflect.Method("htstat",
                MethodType.methodType(String.class), "        int total, i;\n" +
                "        StringBuffer buf = new StringBuffer();\n" +
                "        int works[];\n" +
                "        int typecnt[];                // bound type stats\n" +
                "\n" +
                "        works = new int[32];\n" +
                "        typecnt = new int[8];\n" +
                "        for (i = 0; i < 32; i++)\n" +
                "            works[i] = 0;\n" +
                "        for (i = 0; i < 8; i++)\n" +
                "            typecnt[i] = 0;\n" +
                "        for (i = 0; i < TRANSIZE; i++) {\n" +
                "            works[he[i] & 31]++;\n" +
                "            if ((he[i] & 31) != 0)\n" +
                "                typecnt[4 + (he[i] >> 5)]++;\n" +
                "        }\n" +
                "        for (total = i = 0; i < 8; i++)\n" +
                "            total += typecnt[i];\n" +
                "        if (total > 0)\n" +
                "            buf.append(\"store rate = \" + hitRate() +\n" +
                "                    \"\\n- \" + typecnt[4 + LOSE] / (double) total +\n" +
                "                    \" < \" + typecnt[4 + DRAWLOSE] / (double) total +\n" +
                "                    \" = \" + typecnt[4 + DRAW] / (double) total +\n" +
                "                    \" > \" + typecnt[4 + DRAWWIN] / (double) total +\n" +
                "                    \" + \" + typecnt[4 + WIN] / (double) total + \"\\n\");\n" +
                "        for (i = 0; i < 32; i++) {\n" +
                "            buf.append(works[i]);\n" +
                "            buf.append((i & 7) == 7 ? '\\n' : '\\t');\n" +
                "        }\n" +
                "        return buf.toString();"));

        transaction.replaceImplementation(Game.class, new jmplib.reflect.Method("reset",
                MethodType.methodType(void.class), "        plycnt = 0;\n" +
                "        for (int i = 0; i < 19; i++)\n" +
                "            dias[i] = 0;\n" +
                "        for (int i = 0; i < 8; i++) {\n" +
                "            columns[i] = 1;\n" +
                "            height[i] = 1;\n" +
                "            rows[i] = 0;\n" +
                "        }"));

        transaction.replaceImplementation(Game.class, new jmplib.reflect.Method("toString",
                MethodType.methodType(String.class), "        StringBuffer buf = new StringBuffer();\n" +
                "\n" +
                "        for (int i = 1; i <= plycnt; i++)\n" +
                "            buf.append(moves[i]);\n" +
                "        return buf.toString();"));

        transaction.replaceImplementation(Game.class, new jmplib.reflect.Method("wins",
                MethodType.methodType(boolean.class, int.class, int.class, int.class), "" +
                "        int x, y;\n" +
                "\n" +
                "        sidemask <<= (2 * n);\n" +
                "        x = rows[h] | sidemask;\n" +
                "        y = x & (x << 2);\n" +
                "        if ((y & (y << 4)) != 0)\n" +
                "            return true;\n" +
                "        x = dias[5 + n + h] | sidemask;\n" +
                "        y = x & (x << 2);\n" +
                "        if ((y & (y << 4)) != 0)\n" +
                "            return true;\n" +
                "        x = dias[5 + n - h] | sidemask;\n" +
                "        y = x & (x << 2);\n" +
                "        return (y & (y << 4)) != 0;", "n", "h", "sidemask"));

        transaction.replaceImplementation(Game.class, new jmplib.reflect.Method("backmove",
                MethodType.methodType(void.class), "        int mask, d, h, n, side;\n" +
                "\n" +
                "        side = plycnt & 1;\n" +
                "        n = moves[plycnt--];\n" +
                "        h = --height[n];\n" +
                "        columns[n] >>= 1;\n" +
                "        mask = ~(1 << (2 * n + side));\n" +
                "        rows[h] &= mask;\n" +
                "        dias[5 + n + h] &= mask;\n" +
                "        dias[5 + n - h] &= mask;"));
        transaction.replaceImplementation(Game.class, new jmplib.reflect.Method("makemove",
                MethodType.methodType(void.class, int.class), "        int mask, d, h, side;\n" +
                "\n" +
                "        moves[++plycnt] = n;\n" +
                "        side = plycnt & 1;\n" +
                "        h = height[n]++;\n" +
                "        columns[n] = (columns[n] << 1) + side;\n" +
                "        mask = 1 << (2 * n + side);\n" +
                "        rows[h] |= mask;\n" +
                "        dias[5 + n + h] |= mask;\n" +
                "        dias[5 + n - h] |= mask;", "n"));


        transaction.addField(SearchGame.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                long.class, "msecs"));

        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PROTECTED | Modifier.STATIC,
                int.class, "NSAMELOCK", "0x20000"));
        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PROTECTED | Modifier.STATIC,
                int.class, "STRIDERANGE", "(TRANSIZE / PROBES - NSAMELOCK)"));
        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PROTECTED | Modifier.STATIC,
                int.class, "INTMODSTRIDERANGE", "(int) ((1L << 32) % STRIDERANGE)"));
        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PROTECTED | Modifier.STATIC,
                int.class, "ABSENT", " -128"));

        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PROTECTED,
                long.class, "posed"));
        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PROTECTED,
                long.class, "hits"));

        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                int.class, "stride"));
        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                int.class, "htindex"));
        transaction.addField(TransGame.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                int.class, "lock"));

        transaction.commit();
    }

    public void JGFinitialise() {
        try {
            prepare();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        reset();
        for (int i = 0; i < startingMoves[size].length(); i++)
            makemove(startingMoves[size].charAt(i) - '0');
        emptyTT();
    }

    public void JGFapplication() {
        int result = solve();
    }

    public void JGFvalidate() {
        int i, works[];
        int ref[][] =
                {
//{1048236,842,348,242,182,82,40,18,10,4,3,4,0,0,0,0,
//      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//      {238095,363779,141507,124308,83203,48002,25572,12720,6520,      
//                3184,1580,784,415,177,98,29,22,10,4,0,2,0,0,0,0,0,0,0,0,0,0,0},
                        {422, 97347, 184228, 270877, 218810, 132097, 72059, 37601, 18645, 9200, 4460,
                                2230, 1034, 502, 271, 121, 55, 28, 11, 6, 4, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 9, 2885, 105101, 339874, 282934, 156627, 81700, 40940, 20244, 10278, 4797, 2424,
                                1159, 535, 246, 139, 62, 28, 11, 11, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0}
                };

        works = new int[32];
        for (i = 0; i < 32; i++) works[i] = 0;
        for (i = 0; i < TRANSIZE; i++) works[he[i] & 31]++;

        for (i = 0; i < 32; i++) {
            int error = works[i] - ref[size][i];
            if (error != 0) {
                System.out.print("Validation failed for work count " + i);
                System.out.print("Computed value = " + works[i]);
                System.out.print("Reference value = " + ref[size][i]);
            }
        }


    }

    public void JGFtidyup() {
        // Make sure large arrays are gc'd.
        ht = null;
        he = null;
        System.gc();
    }

    public void JGFrun(int size) {


        JGFInstrumentor.addTimer("Section3:AlphaBetaSearch:Run", "positions", size);

        JGFsetsize(size);
        JGFinitialise();
        JGFapplication();
        JGFvalidate();
        JGFtidyup();


        JGFInstrumentor.addOpsToTimer("Section3:AlphaBetaSearch:Run", (double) nodes);
        JGFInstrumentor.printTimer("Section3:AlphaBetaSearch:Run");
    }
}



