package es.uniovi.jmplib.testing.times.lu;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import jmplib.IntercessorTransaction;
import jmplib.exceptions.StructuralIntercessionException;

public class LUBenchMark extends BenchMark {

	@Override
	public int runOneIteration() {

		double A[][] = randomMatrix(DIN, DIN);
		double lu[][] = new double[DIN][DIN];
		int pivot[] = new int[DIN];
		
		LUTest test = new LUTest(A, lu, pivot);
		Chronometer chronometer = new Chronometer();
		chronometer.start();
		for (int i = 0; i < ITERATIONS; i++) {
			test.test();
		}
		chronometer.stop();
		return chronometer.GetMicroSeconds();
	}

	private double[][] randomMatrix(int M, int N) {
		double A[][] = new double[M][N];

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				A[i][j] = r.nextDouble();
		return A;
	}

	@Override
	public void prepare() {
		IntercessorTransaction transaction = new IntercessorTransaction();
		try {
			transaction.addMethod(LUTest.class, "CopyMatrix",
					MethodType.methodType(void.class, double[][].class,
							double[][].class), "int M = A.length;" + "int N = A[0].length;"
									+ "int remainder = N & 3;"
									+ "for (int i = 0; i < M; i++) {"
									+ "double Bi[] = B[i];" + "double Ai[] = A[i];"
									+ "for (int j = 0; j < remainder; j++)"
									+ "Bi[j] = Ai[j];"
									+ "for (int j = remainder; j < N; j += 4) {"
									+ "Bi[j] = Ai[j];" + "Bi[j + 1] = Ai[j + 1];"
									+ "Bi[j + 2] = Ai[j + 2];"
									+ "Bi[j + 3] = Ai[j + 3];" + "}" + "}",
					Modifier.PUBLIC | Modifier.STATIC,
					new String[] { "B", "A" });
			transaction.addMethod(LUTest.class, "factor", MethodType
					.methodType(int.class, double[][].class, int[].class),
					"int N = A.length;"
							+ "int M = A[0].length;"
							+ "int minMN = Math.min(M, N);"
							+ "for (int j = 0; j < minMN; j++) {"
							+ "int jp = j;" + "double t = Math.abs(A[j][j]);"
							+ "for (int i = j + 1; i < M; i++) {"
							+ "double ab = Math.abs(A[i][j]);"
							+ "if (ab > t) {" + "jp = i;" + "t = ab;" + "}"
							+ "}" + "pivot[j] = jp;" + "if (A[jp][j] == 0)"
							+ "return 1;" + "if (jp != j) {"
							+ "double tA[] = A[j];" + "A[j] = A[jp];"
							+ "A[jp] = tA;" + "}" + "if (j < M - 1){"
							+ "double recp = 1.0 / A[j][j];"
							+ "for (int k = j + 1; k < M; k++)"
							+ "A[k][j] *= recp;" + "}" + "if (j < minMN - 1) {"
							+ "for (int ii = j + 1; ii < M; ii++) {"
							+ "double Aii[] = A[ii];" + "double Aj[] = A[j];"
							+ "double AiiJ = Aii[j];"
							+ "for (int jj = j + 1; jj < N; jj++)"
							+ "Aii[jj] -= AiiJ * Aj[jj];" + "}" + "}" + "}"
							+ "return 0;", Modifier.PUBLIC | Modifier.STATIC, new String[] { "A", "pivot" });
			transaction.replaceImplementation(LUTest.class, "test", 
					"CopyMatrix(lu, A);"
					+ "factor(lu, pivot);");
			transaction.commit();
		} catch (StructuralIntercessionException e) {
			e.printStackTrace();
		}

	}
}
