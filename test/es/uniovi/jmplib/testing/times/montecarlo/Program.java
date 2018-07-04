package es.uniovi.jmplib.testing.times.montecarlo;

public class Program {

	public static final String FRIENDLY_NAME = "Monte Carlo";

	public static void main(String[] args) {
        BenchMark mc = new MonteCarloBenchMark();
        switch (args.length)
        {
            case 1:
            	BenchMark.ITERATIONS = Integer.valueOf(args[0]);
            	mc.prepare();
                System.out.println(mc.RunStartup());
                break;
            case 4:
                BenchMark.ITERATIONS = Integer.valueOf(args[0]);
            	mc.prepare();
                System.out.println(mc.RunSteady(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Double.valueOf(args[3])));
                break;
            default:
                BenchMark.PrintHelp(System.out, args.length);
                break;
        }
	}

}
