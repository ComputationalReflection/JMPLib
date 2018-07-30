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
 *      Original version of this code by Hon Yau (hwyau@epcc.ed.ac.uk)     *
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 1999.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/


package es.uniovi.jmplib.testing.javagrande.montecarlo_meta;

import java.io.IOException;

/**
 * Wrapper code to invoke the Application demonstrator.
 *
 * @author H W Yau
 * @version $Revision: 1.19 $ $Date: 1999/02/16 19:10:02 $
 */
public class CallAppDemo {
    public int size;
    int datasizes[] = {10000, 60000};
    int input[] = new int[2];
    AppDemo ap = null;

    public void initialise() throws IOException {

        input[0] = 1000;
        input[1] = datasizes[size];

        String dirName = new java.io.File(".").getCanonicalPath() + "\\test\\es\\uniovi\\jmplib\\testing\\javagrande\\Data";
        String filename = "hitData";
        ap = new AppDemo(dirName, filename,
                (input[0]), (input[1]));
        ap.init(dirName, filename,
                (input[0]), (input[1]));
        ap.initSerial();
    }

    public void runiters() {
        ap.runSerial();
    }

    public void presults() {
        ap.processSerial();
    }

}
