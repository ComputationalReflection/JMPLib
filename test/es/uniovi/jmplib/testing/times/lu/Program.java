package es.uniovi.jmplib.testing.times.lu;

public class Program {

	public static final String FRIENDLY_NAME = "dense LU matrix factorization";

	public static void main(String[] args) {
        BenchMark mc = new LUBenchMark();
        switch (args.length)
        {
            case 2:
            	BenchMark.DIN = Integer.valueOf(args[0]);
            	BenchMark.ITERATIONS = Integer.valueOf(args[1]);
            	mc.prepare();
                System.out.println(mc.RunStartup());
                break;
            case 5:
                BenchMark.DIN = Integer.valueOf(args[0]);
            	BenchMark.ITERATIONS = Integer.valueOf(args[1]);
            	mc.prepare();
                System.out.println(mc.RunSteady(Integer.valueOf(args[2]), Integer.valueOf(args[3]), Double.valueOf(args[4])));
                break;
            default:
                BenchMark.PrintHelp(System.out, args.length);
                break;
        }
	}

}
