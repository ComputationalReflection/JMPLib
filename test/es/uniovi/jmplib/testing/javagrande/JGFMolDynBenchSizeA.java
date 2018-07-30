package es.uniovi.jmplib.testing.javagrande; /**************************************************************************
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
import es.uniovi.jmplib.testing.javagrande.moldyn.JGFMolDynBench;

public class JGFMolDynBenchSizeA{

  public static void main(String argv[]){

    JGFInstrumentor.printHeader(3,0);

    JGFMolDynBench mold = new JGFMolDynBench();
    mold.JGFrun(0);
 
  }
}
 
