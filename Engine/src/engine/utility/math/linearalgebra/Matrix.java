package engine.utility.math.linearalgebra;

public class Matrix {
	
	public static final double[] IDENTITY_MATRIX = new double[] {
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
	};
	
	public static final Matrix IDENTITY = new Matrix(IDENTITY_MATRIX);
	
	private static final int MAX_COLUMNS = 1000;
	private static final int MAX_ROWS = 1000;
	private double[] matrix;
	
	public Matrix() {
		matrix = null;
	}
	
	public Matrix(int w, int h) {
		if(w > MAX_COLUMNS || h > MAX_ROWS) throw new IllegalArgumentException("Matrix dimension(s) exceeded!");
		matrix = new double[w * h];
	}
	
	public Matrix(double[] matrix) {
		this.matrix = matrix;
	}

	public void scalarAdd(double scalar) {
		for(int i = 0; i < matrix.length; ++i) {
			matrix[i] += scalar;
		}
	}
	
	public void scalarSub(double scalar) {
		for(int i = 0; i < matrix.length; ++i) {
			matrix[i] -= scalar;
		}
	}
	
	public void scalarMul(double scalar) {
		for(int i = 0; i < matrix.length; ++i) {
			matrix[i] *= scalar;
		}
	}
	
	public void scalarDiv(double scalar) {
		for(int i = 0; i < matrix.length; ++i) {
			matrix[i] /= scalar;
		}
	}
}
