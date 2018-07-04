package es.uniovi.jmplib.testing.times.spectralnorm;

public class Program {

	public static final String FRIENDLY_NAME = "Shootout";

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Test test = new SpectralNormTest();
		BenchMark mc = new SpectralNormBenchMark(test);
		switch (args.length) {
		case 1:
			BenchMark.ITERATIONS = Integer.valueOf(args[0]);
			mc.prepare();
			System.out.println(mc.RunStartup());
			break;
		case 4:
			BenchMark.ITERATIONS = Integer.valueOf(args[0]);
			mc.prepare();
			System.out.println(mc.RunSteady(Integer.valueOf(args[1]),
					Integer.valueOf(args[2]), Double.valueOf(args[3])));
			break;
		default:
			BenchMark.PrintHelp(System.out, args.length);
			break;
		}
	}

}
