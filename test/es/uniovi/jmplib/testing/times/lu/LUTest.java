package es.uniovi.jmplib.testing.times.lu;

/**
 * LU matrix factorization. (Based on TNT implementation.) Decomposes a matrix A
 * into a triangular lower triangular factor (L) and an upper triangular factor
 * (U) such that A = L*U. By convnetion, the main diagonal of L consists of 1's
 * so that L and U can be stored compactly in a NxN matrix.
 */
public class LUTest extends Test {
	
	private double[][] A;
	private double[][] lu;
	private int[] pivot;

	public LUTest(double[][] A, double[][] lu, int[] pivot) {
		this.A = A;
		this.lu = lu;
		this.pivot = pivot;
	}

	@Override
	public void test() {
	}
}
