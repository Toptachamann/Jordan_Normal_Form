import java.math.*;
import java.util.*;

public class RectBIMatrix {
	private int colSize, rowSize;
	BigInteger[][] matrix;

	public RectBIMatrix() {
		colSize = rowSize = 0;
	}

	public RectBIMatrix(BigInteger[][] matr) {
		colSize = matr.length;
		rowSize = matr[0].length;
		matrix = new BigInteger[colSize][rowSize];
		assign(matr);
	}

	public RectBIMatrix(BIMatrix matr) {
		colSize = rowSize = matr.size();
		assign(matr.getMatr());
	}

	private void assign(BigInteger[][] matr) {
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				matrix[i][j] = new BigInteger(matr[i][j].toString());
			}
		}
	}

	public String toString() {
		String res = "";
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				res += matrix[i][j].toString() + " ";
			}
			res += '\n';
		}
		return res;
	}

	public RectBIMatrix concatenate(BIMatrix matr) {
		BigInteger[][] m = matr.getMatr();
		if (this.colSize == matr.size) {
			BigInteger[][] res = new BigInteger[this.colSize][this.rowSize
					+ matr.size];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
				for (int j = 0; j < matr.size; j++) {
					res[i][this.rowSize + j] = m[i][j];
				}
			}
			return new RectBIMatrix(res);
		} else {
			System.out.println("Can't concatenate matrices \n"
					+ this.toString() + '\n' + matr.toString());
			return null;
		}
	}

	public void addColumn(ArrayList<BigInteger> column) {
		if (matrix == null) {
			colSize = 1;
			rowSize = column.size();
			BigInteger[][] res = new BigInteger[1][rowSize];
			for (int i = 0; i < colSize; i++) {
				res[1][i] = new BigInteger(column.get(i).toString());
				matrix = res;
			}
		} else if (this.colSize == column.size()) {
			BigInteger[][] res = new BigInteger[colSize][rowSize - 1];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
				res[i][rowSize] = new BigInteger(column.get(i).toString());
			}
			matrix = res;
		} else {
			System.out.println("Can`t add column");
		}
	}

	public BDMatrix solve() {

		return null;
	}

}
