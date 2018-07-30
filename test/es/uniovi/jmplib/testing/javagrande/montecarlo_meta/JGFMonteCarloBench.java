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


package es.uniovi.jmplib.testing.javagrande.montecarlo_meta;

import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFInstrumentor;
import es.uniovi.jmplib.testing.javagrande.jgfutil.JGFSection3;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.Vector;


public class JGFMonteCarloBench extends CallAppDemo implements JGFSection3 {

    public void JGFsetsize(int size) {
        this.size = size;
    }

    public void prepare() throws StructuralIntercessionException {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();

        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("initSerial",
                MethodType.methodType(void.class),
                "        try {\n" +
                        "            //\n" +
                        "            // Measure the requested path rate.\n" +
                        "            RatePath rateP = new RatePath();\n" +
                        "            rateP.readRatesFile(this.dataDirname, this.dataFilename);\n" +
                        "            rateP.dbgDumpFields();\n" +
                        "            ReturnPath returnP = rateP.getReturnCompounded();\n" +
                        "            returnP.estimatePath();\n" +
                        "            returnP.dbgDumpFields();\n" +
                        "            double expectedReturnRate = returnP.get_expectedReturnRate();\n" +
                        "            double volatility = returnP.get_volatility();\n" +
                        "            //\n" +
                        "            // Now prepare for MC runs.\n" +
                        "            initAllTasks = new ToInitAllTasks(returnP, nTimeStepsMC,\n" +
                        "                    pathStartValue);\n" +
                        "            String slaveClassName = \"MonteCarlo.PriceStock\";\n" +
                        "            //\n" +
                        "            // Now create the tasks.\n" +
                        "            initTasks(nRunsMC);\n" +
                        "            //\n" +
                        "        } catch (DemoException demoEx) {\n" +
                        "            dbgPrintln(demoEx.toString());\n" +
                        "            System.exit(-1);\n" +
                        "        }"));

        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("runSerial",
                MethodType.methodType(void.class),
                "results = new Vector(nRunsMC);\n" +
                        "        // Now do the computation.\n" +
                        "        PriceStock ps;\n" +
                        "        for (int iRun = 0; iRun < nRunsMC; iRun++) {\n" +
                        "            ps = new PriceStock();\n" +
                        "            ps.setInitAllTasks(initAllTasks);\n" +
                        "            ps.setTask(tasks.elementAt(iRun));\n" +
                        "            ps.run();\n" +
                        "            results.addElement(ps.getResult());\n" +
                        "        }"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("processSerial",
                MethodType.methodType(void.class),
                "        //\n" +
                        "        // Process the results.\n" +
                        "        try {\n" +
                        "            processResults();\n" +
                        "        } catch (DemoException demoEx) {\n" +
                        "            dbgPrintln(demoEx.toString());\n" +
                        "            System.exit(-1);\n" +
                        "        }"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("initTasks",
                MethodType.methodType(void.class, int.class),
                "        tasks = new Vector(nRunsMC);\n" +
                        "        for (int i = 0; i < nRunsMC; i++) {\n" +
                        "            String header = \"MC run \" + String.valueOf(i);\n" +
                        "            ToTask task = new ToTask(header, (long) i * 11);\n" +
                        "            tasks.addElement((Object) task);\n" +
                        "        }", "nRunsMC"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("processResults",
                MethodType.methodType(void.class),
                "       double avgExpectedReturnRateMC = 0.0;\n" +
                        "        double avgVolatilityMC = 0.0;\n" +
                        "        double runAvgExpectedReturnRateMC = 0.0;\n" +
                        "        double runAvgVolatilityMC = 0.0;\n" +
                        "        ToResult returnMC;\n" +
                        "        if (nRunsMC != results.size()) {\n" +
                        "            errPrintln(\"Fatal: TaskRunner managed to finish with no all the results gathered in!\");\n" +
                        "            System.exit(-1);\n" +
                        "        }\n" +
                        "\n" +
                        "        RatePath avgMCrate = new RatePath(nTimeStepsMC, \"MC\", 19990109, 19991231, dTime);\n" +
                        "        for (int i = 0; i < nRunsMC; i++) {\n" +
                        "            returnMC = (ToResult) results.elementAt(i);\n" +
                        "            avgMCrate.inc_pathValue(returnMC.get_pathValue());\n" +
                        "            avgExpectedReturnRateMC += returnMC.get_expectedReturnRate();\n" +
                        "            avgVolatilityMC += returnMC.get_volatility();\n" +
                        "            runAvgExpectedReturnRateMC = avgExpectedReturnRateMC / ((double) (i + 1));\n" +
                        "            runAvgVolatilityMC = avgVolatilityMC / ((double) (i + 1));\n" +
                        "        } // for i;\n" +
                        "        avgMCrate.inc_pathValue((double) 1.0 / ((double) nRunsMC));\n" +
                        "        avgExpectedReturnRateMC /= nRunsMC;\n" +
                        "        avgVolatilityMC /= nRunsMC;\n" +
                        "\n" +
                        "        JGFavgExpectedReturnRateMC = avgExpectedReturnRateMC;"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("get_dataDirname",
                MethodType.methodType(String.class),
                "return (this.dataDirname);"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("set_dataDirname",
                MethodType.methodType(void.class, String.class),
                "this.dataDirname = dataDirname;", "dataDirname"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("get_dataFilename",
                MethodType.methodType(String.class),
                "return (this.dataFilename);"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("set_dataFilename",
                MethodType.methodType(void.class, String.class),
                "this.dataFilename = dataFilename;", "dataFilename"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("get_nTimeStepsMC",
                MethodType.methodType(int.class),
                " return (this.nTimeStepsMC);"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("set_nTimeStepsMC",
                MethodType.methodType(void.class, int.class),
                "this.nTimeStepsMC = nTimeStepsMC;", "nTimeStepsMC"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("get_nRunsMC",
                MethodType.methodType(int.class),
                "return (this.nRunsMC);"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("set_nRunsMC",
                MethodType.methodType(void.class, int.class),
                "this.nRunsMC = nRunsMC;", "nRunsMC"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("get_tasks",
                MethodType.methodType(Vector.class),
                "return (this.tasks);"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("set_tasks",
                MethodType.methodType(void.class, Vector.class),
                "this.tasks = tasks;"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("get_results",
                MethodType.methodType(Vector.class),
                "return (this.results);"));
        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("set_results",
                MethodType.methodType(void.class, Vector.class),
                "this.results = results;"));

        transaction.replaceImplementation(AppDemo.class, new jmplib.reflect.Method("init",
                MethodType.methodType(void.class, String.class, String.class, int.class, int.class), "" +
                "            this.dataDirname = dataDirname;\n" +
                "            this.dataFilename = dataFilename;\n" +
                "            this.nTimeStepsMC = nTimeStepsMC;\n" +
                "            this.nRunsMC = nRunsMC;\n" +
                "            this.initialised = false;\n" +
                "            set_prompt(prompt);\n" +
                "            set_DEBUG(DEBUG);"));

        transaction.replaceImplementation(PriceStock.class, new jmplib.reflect.Method("setInitAllTasks",
                MethodType.methodType(void.class, Object.class), "       " +
                " ToInitAllTasks initAllTasks = (ToInitAllTasks) obj;\n" +
                "        mcPath.set_name(initAllTasks.get_name());\n" +
                "        mcPath.set_startDate(initAllTasks.get_startDate());\n" +
                "        mcPath.set_endDate(initAllTasks.get_endDate());\n" +
                "        mcPath.set_dTime(initAllTasks.get_dTime());\n" +
                "        mcPath.set_returnDefinition(initAllTasks.get_returnDefinition());\n" +
                "        mcPath.set_expectedReturnRate(initAllTasks.get_expectedReturnRate());\n" +
                "        mcPath.set_volatility(initAllTasks.get_volatility());\n" +
                "        int nTimeSteps = initAllTasks.get_nTimeSteps();\n" +
                "        mcPath.set_nTimeSteps(nTimeSteps);\n" +
                "        this.pathStartValue = initAllTasks.get_pathStartValue();\n" +
                "        mcPath.set_pathStartValue(pathStartValue);\n" +
                "        mcPath.set_pathValue(new double[nTimeSteps]);\n" +
                "        mcPath.set_fluctuations(new double[nTimeSteps]);"));

        transaction.replaceImplementation(PriceStock.class, new jmplib.reflect.Method("setTask",
                MethodType.methodType(void.class, Object.class), "        ToTask task = (ToTask) obj;\n" +
                "        this.taskHeader = task.get_header();\n" +
                "        this.randomSeed = task.get_randomSeed();"));
        transaction.replaceImplementation(PriceStock.class, new jmplib.reflect.Method("run",
                MethodType.methodType(void.class), "        try {\n" +
                "            mcPath.computeFluctuationsGaussian(randomSeed);\n" +
                "            mcPath.computePathValue(pathStartValue);\n" +
                "            RatePath rateP = new RatePath();\n" +
                "            rateP.initMc(mcPath);\n" +
                "            ReturnPath returnP = rateP.getReturnCompounded();\n" +
                "            returnP.estimatePath();\n" +
                "            expectedReturnRate = returnP.get_expectedReturnRate();\n" +
                "            volatility = returnP.get_volatility();\n" +
                "            volatility2 = returnP.get_volatility2();\n" +
                "            finalStockPrice = rateP.getEndPathValue();\n" +
                "            pathValue = mcPath.get_pathValue();\n" +
                "        } catch (DemoException demoEx) {\n" +
                "            errPrintln(demoEx.toString());\n" +
                "        }"));
        transaction.replaceImplementation(PriceStock.class, new jmplib.reflect.Method("getResult",
                MethodType.methodType(Object.class), "        " +
                "String resultHeader = \"Result of task with Header=\" + taskHeader + \": randomSeed=\" + randomSeed + \": pathStartValue=\" + pathStartValue;\n" +
                "        ToResult res = new ToResult(resultHeader, expectedReturnRate, volatility,\n" +
                "                volatility2, finalStockPrice, pathValue);\n" +
                "        return (Object) res;"));


        transaction.replaceImplementation(RatePath.class, new jmplib.reflect.Method("getReturnCompounded",
                MethodType.methodType(ReturnPath.class), "" +
                "if (pathValue == null || nAcceptedPathValue == 0) {\n" +
                "            throw new DemoException(\"The Rate Path has not been defined!\");\n" +
                "        }\n" +
                "        double[] returnPathValue = new double[nAcceptedPathValue];\n" +
                "        returnPathValue[0] = 0.0;\n" +
                "        try {\n" +
                "            for (int i = 1; i < nAcceptedPathValue; i++) {\n" +
                "                returnPathValue[i] = Math.log(pathValue[i] / pathValue[i - 1]);\n" +
                "            }\n" +
                "        } catch (ArithmeticException aex) {\n" +
                "            throw new DemoException(\"Error in getReturnLogarithm:\" + aex.toString());\n" +
                "        }\n" +
                "        ReturnPath rPath = new ReturnPath(returnPathValue, nAcceptedPathValue,\n" +
                "                ReturnPath.COMPOUNDED);\n" +
                "        //\n" +
                "        // Copy the PathId information to the ReturnPath object.\n" +
                "        rPath.copyInstanceVariables(this);\n" +
                "        rPath.estimatePath();\n" +
                "        return (rPath);"));

        transaction.replaceImplementation(RatePath.class, new jmplib.reflect.Method("getReturnNonCompounded",
                MethodType.methodType(ReturnPath.class), "" +
                "        if (pathValue == null || nAcceptedPathValue == 0) {\n" +
                "            throw new DemoException(\"The Rate Path has not been defined!\");\n" +
                "        }\n" +
                "        double[] returnPathValue = new double[nAcceptedPathValue];\n" +
                "        returnPathValue[0] = 0.0;\n" +
                "        try {\n" +
                "            for (int i = 1; i < nAcceptedPathValue; i++) {\n" +
                "                returnPathValue[i] = (pathValue[i] - pathValue[i - 1]) / pathValue[i];\n" +
                "            }\n" +
                "        } catch (ArithmeticException aex) {\n" +
                "            throw new DemoException(\"Error in getReturnPercentage:\" + aex.toString());\n" +
                "        }\n" +
                "        ReturnPath rPath = new ReturnPath(returnPathValue, nAcceptedPathValue,\n" +
                "                ReturnPath.NONCOMPOUNDED);\n" +
                "        //\n" +
                "        // Copy the PathId information to the ReturnPath object.\n" +
                "        rPath.copyInstanceVariables(this);\n" +
                "        rPath.estimatePath();\n" +
                "        return (rPath);"));
        transaction.replaceImplementation(RatePath.class, new jmplib.reflect.Method("readRatesFile",
                MethodType.methodType(void.class, String.class, String.class), "" +
                "       //System.out.println(\"readRatesFile(\" + dirName+ \", \" + filename + \")\");\n" +
                "        File ratesFile = new File(dirName, filename);\n" +
                "        BufferedReader in;\n" +
                "        if (!ratesFile.canRead()) {\n" +
                "            throw new DemoException(\"Cannot read the file \" + ratesFile.toString());\n" +
                "        }\n" +
                "        try {\n" +
                "            in = new BufferedReader(new FileReader(ratesFile));\n" +
                "        } catch (FileNotFoundException fnfex) {\n" +
                "            throw new DemoException(fnfex.toString());\n" +
                "        }\n" +
                "        //\n" +
                "        // Proceed to read all the lines of data into a Vector object.\n" +
                "        int iLine = 0, initNlines = 100, nLines = 0;\n" +
                "\n" +
                "        String aLine;\n" +
                "        Vector allLines = new Vector(initNlines);\n" +
                "        try {\n" +
                "            while ((aLine = in.readLine()) != null) {\n" +
                "                iLine++;\n" +
                "                //\n" +
                "                // Note, I'm not entirely sure whether the object passed in is copied\n" +
                "                // by value, or just its reference.\n" +
                "                allLines.addElement(aLine);\n" +
                "            }\n" +
                "        } catch (IOException ioex) {\n" +
                "            throw new DemoException(\"Problem reading data from the file \" + ioex.toString());\n" +
                "        }\n" +
                "        nLines = iLine;\n" +
                "        //\n" +
                "        // Now create an array to store the rates data.\n" +
                "        this.pathValue = new double[nLines];\n" +
                "        this.pathDate = new int[nLines];\n" +
                "        //System.out.println(\"readRatesFile(\" + this.pathValue+ \", \" + this.pathDate + \")\" + nLines);\n" +
                "        nAcceptedPathValue = 0;\n" +
                "        iLine = 0;\n" +
                "        for (java.util.Enumeration enumer = allLines.elements();\n" +
                "             enumer.hasMoreElements(); ) {\n" +
                "            aLine = (String) enumer.nextElement();\n" +
                "            String[] field = Utilities.splitString(\",\", aLine);\n" +
                "            int aDate = Integer.parseInt(\"19\" + field[0]);\n" +
                "            //\n" +
                "            // static double Double.parseDouble() method is a feature of JDK1.2!\n" +
                "            double aPathValue = Double.valueOf(field[DATUMFIELD]).doubleValue();\n" +
                "            if ((aDate <= MINIMUMDATE) || (Math.abs(aPathValue) < EPSILON)) {\n" +
                "                dbgPrintln(\"Skipped erroneous data in \" + filename + \" indexed by date=\" + field[0] + \".\");\n" +
                "            } else {\n" +
                "                pathDate[iLine] = aDate;\n" +
                "                pathValue[iLine] = aPathValue;\n" +
                "                iLine++;\n" +
                "            }\n" +
                "        }\n" +
                "        //\n" +
                "        // Record the actual number of accepted data points.\n" +
                "        nAcceptedPathValue = iLine;\n" +
                "        //\n" +
                "        // Now to fill in the structures from the 'PathId' class.\n" +
                "        set_name(ratesFile.getName());\n" +
                "        set_startDate(pathDate[0]);\n" +
                "        set_endDate(pathDate[nAcceptedPathValue - 1]);\n" +
                "        set_dTime((double) (1.0 / 365.0));"));

        transaction.replaceImplementation(Utilities.class, new jmplib.reflect.Method("which",
                MethodType.methodType(String.class, String.class, String.class), "        String executablePath;\n" +
                "        String paths[];\n" +
                "\n" +
                "        paths = splitString(System.getProperty(\"path.separator\"), pathEnv);\n" +
                "        for (int i = 0; i < paths.length; i++) {\n" +
                "            if (paths[i].length() > 0) {\n" +
                "                java.io.File pathFile = new java.io.File(paths[i]);\n" +
                "                if (pathFile.isDirectory()) {\n" +
                "                    String filesInDirectory[];\n" +
                "                    filesInDirectory = pathFile.list();\n" +
                "                    for (int j = 0; j < filesInDirectory.length; j++) {\n" +
                "                        if (DEBUG) {\n" +
                "                            System.out.println(\"DBG: Matching \" + filesInDirectory[j]);\n" +
                "                        }\n" +
                "                        if (filesInDirectory[j].equals(executable)) {\n" +
                "                            executablePath = paths[i] + System.getProperty(\"file.separator\") + executable;\n" +
                "                            return executablePath;\n" +
                "                        }\n" +
                "                    }\n" +
                "                } else {\n" +
                "                    if (DEBUG) {\n" +
                "                        System.out.println(\"DBG: path \" + paths[i] + \" is not a directory!\");\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        } /* for i */\n" +
                "        executablePath = executable + \" not found.\";\n" +
                "        return executablePath;"));
        transaction.replaceImplementation(Utilities.class, new jmplib.reflect.Method("joinString",
                MethodType.methodType(String.class, String.class, String[].class, int.class), "String methodName = \"join\";\n" +
                "        StringBuffer tmpString;\n" +
                "\n" +
                "        int nStrings = java.lang.reflect.Array.getLength(stringArray);\n" +
                "        if (nStrings <= index) {\n" +
                "            tmpString = new StringBuffer();\n" +
                "        } else {\n" +
                "            tmpString = new StringBuffer(stringArray[index]);\n" +
                "            for (int i = (index + 1); i < nStrings; i++) {\n" +
                "                tmpString.append(joinChar).append(stringArray[i]);\n" +
                "            }\n" +
                "        }\n" +
                "        return tmpString.toString();"));
        transaction.replaceImplementation(Utilities.class, new jmplib.reflect.Method("splitString",
                MethodType.methodType(String[].class, String.class, String.class), "        String methodName = \"split\";\n" +
                "\n" +
                "        String myArgs[];\n" +
                "        int nArgs = 0;\n" +
                "        int foundIndex = 0, fromIndex = 0;\n" +
                "\n" +
                "        while ((foundIndex = arg.indexOf(splitChar, fromIndex)) > -1) {\n" +
                "            nArgs++;\n" +
                "            fromIndex = foundIndex + 1;\n" +
                "        }\n" +
                "        if (DEBUG) {\n" +
                "            System.out.println(\"DBG \" + className + \".\" + methodName + \": \" + nArgs);\n" +
                "        }\n" +
                "        myArgs = new String[nArgs + 1];\n" +
                "        nArgs = 0;\n" +
                "        fromIndex = 0;\n" +
                "        while ((foundIndex = arg.indexOf(splitChar, fromIndex)) > -1) {\n" +
                "            if (DEBUG) {\n" +
                "                System.out.println(\"DBG \" + className + \".\" + methodName + \": \" + fromIndex + \" \" + foundIndex);\n" +
                "            }\n" +
                "            myArgs[nArgs] = arg.substring(fromIndex, foundIndex);\n" +
                "            nArgs++;\n" +
                "            fromIndex = foundIndex + 1;\n" +
                "        }\n" +
                "        myArgs[nArgs] = arg.substring(fromIndex);\n" +
                "        return myArgs;"));

        transaction.replaceImplementation(MonteCarloPath.class, new jmplib.reflect.Method("writeFile",
                MethodType.methodType(void.class, String.class, String.class), "       try {\n" +
                "            File ratesFile = new File(dirName, filename);\n" +
                "            if (ratesFile.exists() && !ratesFile.canWrite())\n" +
                "                throw new DemoException(\"Cannot write to specified filename!\");\n" +
                "            PrintWriter out = new PrintWriter(new BufferedWriter(\n" +
                "                    new FileWriter(ratesFile)));\n" +
                "            for (int i = 0; i < nTimeSteps; i++) {\n" +
                "                out.print(\"19990101,\");\n" +
                "                for (int j = 1; j < DATUMFIELD; j++) {\n" +
                "                    out.print(\"0.0000,\");\n" +
                "                }\n" +
                "                out.print(pathValue[i] + \",\");\n" +
                "                out.println(\"0.0000,0.0000\");\n" +
                "            }\n" +
                "            out.close();\n" +
                "        } catch (IOException ioex) {\n" +
                "            throw new DemoException(ioex.toString());\n" +
                "        }"));
        transaction.replaceImplementation(MonteCarloPath.class, new jmplib.reflect.Method("computeFluctuationsGaussian",
                MethodType.methodType(void.class, long.class), "        if (nTimeSteps > fluctuations.length)\n" +
                "            throw new DemoException(\"Number of timesteps requested is greater than the allocated array!\");\n" +
                "        //\n" +
                "        // First, make use of the passed in seed value.\n" +
                "        Random rnd;\n" +
                "        if (randomSeed == -1) {\n" +
                "            rnd = new Random();\n" +
                "        } else {\n" +
                "            rnd = new Random(randomSeed);\n" +
                "        }\n" +
                "        //\n" +
                "        // Determine the mean and standard-deviation, from the mean-drift and volatility.\n" +
                "        double mean = (expectedReturnRate - 0.5 * volatility * volatility) * get_dTime();\n" +
                "        double sd = volatility * Math.sqrt(get_dTime());\n" +
                "        double gauss, meanGauss = 0.0, variance = 0.0;\n" +
                "        for (int i = 0; i < nTimeSteps; i++) {\n" +
                "            gauss = rnd.nextGaussian();\n" +
                "            meanGauss += gauss;\n" +
                "            variance += (gauss * gauss);\n" +
                "            //\n" +
                "            // Now map this onto a general Gaussian of given mean and variance.\n" +
                "            fluctuations[i] = mean + sd * gauss;\n" +
                "            //      dbgPrintln(\"gauss=\"+gauss+\" fluctuations=\"+fluctuations[i]);\n" +
                "        }\n" +
                "        meanGauss /= (double) nTimeSteps;\n" +
                "        variance /= (double) nTimeSteps;\n" +
                "        "));
        transaction.replaceImplementation(MonteCarloPath.class, new jmplib.reflect.Method("computePathValue",
                MethodType.methodType(void.class, double.class), "" +
                "        pathValue[0] = startValue;\n" +
                "        if (returnDefinition == ReturnPath.COMPOUNDED ||\n" +
                "                returnDefinition == ReturnPath.NONCOMPOUNDED) {\n" +
                "            for (int i = 1; i < nTimeSteps; i++) {\n" +
                "                pathValue[i] = pathValue[i - 1] * Math.exp(fluctuations[i]);\n" +
                "            }\n" +
                "        } else {\n" +
                "            throw new DemoException(\"Unknown or undefined update method.\");\n" +
                "        }"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.STATIC | Modifier.PUBLIC,
                int.class, "Serial", "1"));

        //transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.STATIC | Modifier.PUBLIC, double.class, "JGFavgExpectedReturnRateMC", "0.0"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.STATIC | Modifier.PUBLIC,
                boolean.class, "DEBUG", "true"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.STATIC | Modifier.PUBLIC,
                String.class, "prompt", "\"AppDemo>\" "));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(
                PriceStock.class, "psMC"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(
                double.class, "pathStartValue", "100.0"));
        transaction.addField(AppDemo.class, new jmplib.reflect.Field(
                double.class, "avgExpectedReturnRateMC", "0.0"));
        transaction.addField(AppDemo.class, new jmplib.reflect.Field(
                double.class, "avgVolatilityMC", "0.0"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(
                ToInitAllTasks.class, "initAllTasks"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                String.class, "dataDirname"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                String.class, "dataFilename"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                int.class, "nTimeStepsMC"));
        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                int.class, "nRunsMC"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double.class, "dTime", "1.0/365.0"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                boolean.class, "initialised", "false"));

        transaction.addField(AppDemo.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                int.class, "runMode"));


        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                String.class, "taskHeader"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                long.class, "randomSeed", "-1"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double.class, "pathStartValue", "Double.NaN"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                ToResult.class, "result"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double.class, "expectedReturnRate", "Double.NaN"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double.class, "volatility", "Double.NaN"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double.class, "volatility2", "Double.NaN"));

        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double.class, "finalStockPrice", "Double.NaN"));


        transaction.addField(PriceStock.class, new jmplib.reflect.Field(Modifier.PRIVATE,
                double[].class, "pathValue"));

        transaction.commit();
    }

    public void JGFinitialise() {

        try {
            prepare();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            initialise();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void JGFapplication() {

        JGFInstrumentor.startTimer("Section3:MonteCarlo:Run");

        runiters();

        JGFInstrumentor.stopTimer("Section3:MonteCarlo:Run");

        presults();
    }


    public void JGFvalidate() {
        double refval[] = {-0.0333976656762814, -0.03215796752868655};
        double dev = Math.abs(AppDemo.JGFavgExpectedReturnRateMC - refval[size]);
        if (dev > 1.0e-12) {
            System.out.println("Validation failed");
            System.out.println(" expectedReturnRate= " + AppDemo.JGFavgExpectedReturnRateMC + "  " + dev + "  " + size);
        }
    }

    public void JGFtidyup() {

        System.gc();
    }


    public void JGFrun(int size) {

        JGFInstrumentor.addTimer("Section3:MonteCarlo:Total", "Solutions", size);
        JGFInstrumentor.addTimer("Section3:MonteCarlo:Run", "Samples", size);

        JGFsetsize(size);

        JGFInstrumentor.startTimer("Section3:MonteCarlo:Total");

        JGFinitialise();
        JGFapplication();
        JGFvalidate();
        JGFtidyup();

        JGFInstrumentor.stopTimer("Section3:MonteCarlo:Total");

        JGFInstrumentor.addOpsToTimer("Section3:MonteCarlo:Run", (double) input[1]);
        JGFInstrumentor.addOpsToTimer("Section3:MonteCarlo:Total", 1);

        JGFInstrumentor.printTimer("Section3:MonteCarlo:Run");
        JGFInstrumentor.printTimer("Section3:MonteCarlo:Total");
    }


}
 
