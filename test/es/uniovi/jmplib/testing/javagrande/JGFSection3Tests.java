package es.uniovi.jmplib.testing.javagrande;

/* *************************************************************************
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


import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFInstrumentor;
import org.junit.Test;

import static org.junit.Assert.fail;

public class JGFSection3Tests {

    @Test
    public void test() {

        int size = 0;

        try {
            JGFInstrumentor.printHeader(3, size);

            es.uniovi.jmplib.testing.javagrande.euler_meta.JGFEulerBench ebmeta =
                    new es.uniovi.jmplib.testing.javagrande.euler_meta.JGFEulerBench();
            ebmeta.JGFrun(size);


            es.uniovi.jmplib.testing.javagrande.euler.JGFEulerBench eb = new es.uniovi.jmplib.testing.javagrande.euler.JGFEulerBench();
            eb.JGFrun(size);


            es.uniovi.jmplib.testing.javagrande.moldyn.JGFMolDynBench mdb =
                    new es.uniovi.jmplib.testing.javagrande.moldyn.JGFMolDynBench();
            mdb.JGFrun(size);

            es.uniovi.jmplib.testing.javagrande.moldyn_meta.JGFMolDynBench mdbmeta =
                    new es.uniovi.jmplib.testing.javagrande.moldyn_meta.JGFMolDynBench();
            mdbmeta.JGFrun(size);


            es.uniovi.jmplib.testing.javagrande.montecarlo_meta.JGFMonteCarloBench mcbmeta =
                    new es.uniovi.jmplib.testing.javagrande.montecarlo_meta.JGFMonteCarloBench();
            mcbmeta.JGFrun(size);


            es.uniovi.jmplib.testing.javagrande.montecarlo.JGFMonteCarloBench mcb =
                    new es.uniovi.jmplib.testing.javagrande.montecarlo.JGFMonteCarloBench();
            mcb.JGFrun(size);


            es.uniovi.jmplib.testing.javagrande.search.JGFSearchBench sb =
                    new es.uniovi.jmplib.testing.javagrande.search.JGFSearchBench();
            sb.JGFrun(size);


            es.uniovi.jmplib.testing.javagrande.search_meta.JGFSearchBench sbmeta =
                    new es.uniovi.jmplib.testing.javagrande.search_meta.JGFSearchBench();
            sbmeta.JGFrun(size);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
}

