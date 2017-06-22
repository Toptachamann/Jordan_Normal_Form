import java.math.*;
import java.util.*;

public class BDMatrix {
	private int colSize, rowSize;
	private BigDecimal[][] matrix;

	public BDMatrix(int size) {
		colSize = rowSize = size;
		matrix = new BigDecimal[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = BigDecimal.ZERO;
			}
		}
	}

	public BDMatrix(BigDecimal[][] matr) {
		colSize = matr.length;
		rowSize = matr[0].length;
		matrix = new BigDecimal[colSize][rowSize];
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				matrix[i][j] = new BigDecimal(matr[i][j].toString());
			}
		}
	}

	public BDMatrix() {
		colSize = rowSize = 0;
	}

	public String toString() {
		BigDecimal epsilon = new BigDecimal("1E-8");
		MathContext rounder = new MathContext(0, RoundingMode.HALF_UP);
		String res = "";
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
			if(matrix[i][j].compareTo(BigDecimal.ZERO) >= 0)
				res += ' ';
			res += matrix[i][j].toString() + " ";
			
				/*if (matrix[i][j].round(rounder).subtract(matrix[i][j]).abs().compareTo(epsilon) < 0)
					res += matrix[i][j].toBigInteger().toString() + " ";
				else
					res += matrix[i][j].toString() + " ";*/
			}
			res += '\n';
		}
		return res;
	}

	public BDMatrix roundMatrix(BigDecimal epsilon, MathContext rounder) {
		MathContext zeroRounder = new MathContext(0, RoundingMode.HALF_UP);
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				if (matrix[i][j]
						.subtract(matrix[i][j].round(zeroRounder),
								rounder).abs().compareTo(epsilon) < 0)
					matrix[i][j] = matrix[i][j].round(zeroRounder);
				else
					matrix[i][j] = matrix[i][j].round(rounder);
			}
		}
		return this;
	}

	public void printMatrix() {
		System.out.println(this.toString());
	}

	public BigDecimal[][] getMatrix() {
		return matrix;
	}

	public BDMatrix copy() {
		return new BDMatrix(matrix);
	}

	public BigDecimal getElement(int i, int j) {
		if (i >= 0 && j >= 0 && i < colSize && j < rowSize) {
			return new BigDecimal(matrix[i][j].toString());
		} else {
			System.out.println("Wrong indices' parameters: i = " + i + ", j = "
					+ j);
			return null;
		}
	}

	public void setElement(int i, int j, BigDecimal value) {
		matrix[i][j] = new BigDecimal(value.toString());
	}

	public BDMatrix round(MathContext rounder) {
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				matrix[i][j] = matrix[i][j].round(rounder).setScale(rounder.getPrecision(), rounder.getRoundingMode());
			}
		}
		return new BDMatrix(matrix);
	}

	public BDVector getColumn(int columnNumber) {
		BigDecimal[] result = new BigDecimal[colSize];
		for (int i = 0; i < colSize; i++) {
			result[i] = matrix[i][columnNumber];
		}
		return new BDVector(result);
	}

	public void setColumn(int colIndex, BDVector row) {
		BigDecimal[] t = row.getVector();
		for (int i = 0; i < colSize; i++) {
			matrix[i][colIndex] = new BigDecimal(t[i].toString());
		}
	}

	public int size() {
		return rowSize;
	}

	public BIMatrix toBIMatrix() {
		BigInteger[][] result = new BigInteger[rowSize][colSize];
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				result[i][j] = matrix[i][j].toBigInteger();
			}
		}
		return new BIMatrix(result);
	}

	private void applyInversions(ArrayList<Pair<Integer, Integer>> inversions) {
		Pair<Integer, Integer> t;
		for (int i = inversions.size(); i >= 0; i--) {
			t = inversions.get(i);
			swapRows(t.first(), t.second());
		}
	}

	private BigDecimal multiplyRows(int i, int j, BDMatrix m,
			MathContext rounder) {
		BigDecimal res = BigDecimal.ZERO;
		for (int k = 0; k < this.rowSize; k++) {
			res = res.add(matrix[i][k].multiply(m.getElement(k, j)), rounder);
		}
		return res;
	}

	public BDMatrix multiply(BDMatrix m, MathContext rounder) {
		this.toString();
		if (this.rowSize == m.colSize) {
			BigDecimal[][] res = new BigDecimal[this.colSize][m.rowSize];
			for (int i = 0; i < this.colSize; i++) {
				for (int j = 0; j < m.rowSize; j++) {
					res[i][j] = multiplyRows(i, j, m, rounder);
				}
			}
			return new BDMatrix(res);
		} else {
			System.out.println("Matrices can't be multiplied");
			return null;
		}
	}

	public BDVector multiply(BDVector vector, MathContext rounder) {
		BDVector result = new BDVector(vector.size());
		for (int i = 0; i < vector.size(); i++) {
			BigDecimal t = BigDecimal.ZERO;
			for (int j = 0; j < vector.size(); j++) {
				t = t.add(matrix[i][j].multiply(vector.getElement(j)));
			}
			result.setElement(i, t);
		}
		return result;
	}

	private void subtractRows(int minuend, int subtrahend,
			BigDecimal multiplicand, MathContext rounder) {
		for (int k = 0; k < rowSize; k++) {
			matrix[minuend][k] = matrix[minuend][k].subtract(
					matrix[subtrahend][k].multiply(multiplicand, rounder),
					rounder);
		}
	}

	public void appendColumn(BDVector vector) {
		appendColumn(vector.getVector());
	}

	public void appendColumn(BigDecimal[] column) {
		if (matrix == null) {
			colSize = column.length;
			rowSize = 1;
			BigDecimal[][] res = new BigDecimal[colSize][1];
			for (int i = 0; i < colSize; i++) {
				res[i][0] = new BigDecimal(column[i].toString());
				matrix = res;
			}
		} else if (this.colSize == column.length) {
			BigDecimal[][] res = new BigDecimal[colSize][rowSize + 1];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
				res[i][rowSize] = new BigDecimal(column[i].toString());
			}
			matrix = res;
			++rowSize;
		} else {
			System.out.println("Can`t add column");
		}
	}

	public void appendColumn(ArrayList<BigDecimal> column) {
		if (matrix == null) {
			colSize = column.size();
			rowSize = 1;
			matrix = new BigDecimal[colSize][1];
			for (int i = 0; i < colSize; i++) {
				matrix[i][0] = new BigDecimal(column.get(i).toString());
			}
		} else if (this.colSize == column.size()) {
			BigDecimal[][] res = new BigDecimal[colSize][rowSize + 1];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
				res[i][rowSize] = new BigDecimal(column.get(i).toString());
			}
			matrix = res;
			++rowSize;
		} else {
			System.out.println("Can`t append column");
		}
	}

	public void appendRow(BDVector vector) {
		appendRow(vector.getVector());
	}

	public void appendRow(BigDecimal[] row) {
		if (matrix == null) {
			colSize = 1;
			rowSize = row.length;
			matrix = new BigDecimal[1][rowSize];
			for (int i = 0; i < rowSize; i++) {
				matrix[0][i] = new BigDecimal(row[i].toString());
			}
		} else if (this.rowSize == row.length) {
			BigDecimal[][] res = new BigDecimal[colSize + 1][rowSize];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
			}
			for (int i = 0; i < rowSize; i++) {
				res[colSize][i] = new BigDecimal(row[i].toString());
			}
			matrix = res;
			++colSize;
		} else {
			System.out.println("Can`t append row");
		}
	}

	public void appendRow(ArrayList<BigDecimal> row) {
		if (matrix == null) {
			colSize = 1;
			rowSize = row.size();
			matrix = new BigDecimal[1][rowSize];
			for (int i = 0; i < rowSize; i++) {
				matrix[0][i] = new BigDecimal(row.get(i).toString());
			}
		} else if (this.rowSize == row.size()) {
			BigDecimal[][] res = new BigDecimal[colSize + 1][rowSize];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
			}
			for (int i = 0; i < rowSize; i++) {
				res[colSize][i] = new BigDecimal(row.get(i).toString());
			}
			matrix = res;
			++colSize;
		} else {
			System.out.println("Can`t append row");
		}
	}

	public BDMatrix concatenate(BDMatrix matr) {
		BigDecimal[][] m = matr.getMatrix();
		if (this.colSize == matr.colSize) {
			BigDecimal[][] res = new BigDecimal[this.colSize][this.rowSize
					+ matr.rowSize];
			for (int i = 0; i < colSize; i++) {
				for (int j = 0; j < rowSize; j++) {
					res[i][j] = matrix[i][j];
				}
				for (int j = 0; j < matr.rowSize; j++) {
					res[i][this.rowSize + j] = m[i][j];
				}
			}
			return new BDMatrix(res);
		} else {
			System.out.println("Can't concatenate matrices \n"
					+ this.toString() + '\n' + matr.toString());
			return null;
		}
	}

	private void swapRows(int first, int second) {
		BigDecimal t;
		for (int i = 0; i < rowSize; i++) {
			t = matrix[first][i];
			matrix[first][i] = matrix[second][i];
			matrix[second][i] = t;
		}
	}

	private void swapColumns(int first, int second) {
		BigDecimal t;
		for(int i = 0; i < colSize; i++){
			t = matrix[i][first];
			matrix[i][first] = matrix[i][second];
			matrix[i][second] = t;
		}
	}

	public BDMatrix minor(int n, int m) {
		BigDecimal[][] result = new BigDecimal[colSize - 1][rowSize - 1];
		int k = 0, l;
		for (int i = 0; i < colSize - 1;) {
			l = 0;
			if (k != n) {
				for (int j = 0; j < rowSize - 1;) {
					if (l != m) {
						result[i][j] = new BigDecimal(matrix[k][l].toString());
						j++;
					}
					l++;
				}
				i++;
			}
			k++;
		}
		return new BDMatrix(result);
	}

	public BigDecimal algebraicComplement(int i, int j, BigDecimal epsilon, MathContext rounder) {
		BDMatrix minor = this.minor(i, j);
		BigDecimal result = minor.det(epsilon, rounder);
		return result;
	}

	public BigDecimal det(BigDecimal epsilon, MathContext rounder) {
		BDMatrix copy = this.copy();
		ArrayList<Pair<Integer, Integer>> inversions = copy.triangulate(
				colSize, epsilon, rounder).first();
		BigDecimal[][] matrix = copy.getMatrix();
		BigDecimal result = BigDecimal.ONE;
		for (int i = 0; i < colSize; i++) {
			result = result.multiply(matrix[i][i], rounder);
		}
		if (inversions.size() % 2 == 1)
			result = result.negate(rounder);
		return result;
	}

	public BDMatrix inverse(BigDecimal epsilon, MathContext rounder) {
		BigDecimal det = this.det(epsilon, rounder);
		if (det.abs().compareTo(epsilon) < 0 || colSize != rowSize) {
			System.out.println("Can't find inverse matrix, det is equal to 0");
			return null;
		}
		BDMatrix result = new BDMatrix(colSize);
		BigDecimal t;
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize; j++) {
				t = this.algebraicComplement(j, i, epsilon, rounder)
						.divide(det, rounder);
				if ((i + j) % 2 == 1)
					t = t.negate();
				result.setElement(i, j, t);
			}
		}
		return result;
	}

	private void triangulateColumnUp(int i, int j, BigDecimal epsilon, MathContext rounder) {
		if (matrix[i][j].abs().compareTo(epsilon) > 0) {
			BigDecimal multiplicand;
			for (int k = i - 1; k >= 0; k--) {
				if (matrix[i][j].abs().compareTo(epsilon) > 0) {
					multiplicand = matrix[k][j].divide(matrix[i][j], rounder);
					subtractRows(k, i, multiplicand, rounder);
					//this.printMatrix();
				}
				//this.printMatrix();
			}
		}
	}

	private Pair<Integer, Integer> triangulateColumnDown(int i, int j, BigDecimal epsilon, 
			MathContext rounder) {
		Pair<Integer, Integer> result = replaceWithNonZeroDown(epsilon, i, j);
		if (result != null) {
			BigDecimal multiplicand;
			for (int k = i + 1; k < colSize; k++) {
				if (matrix[k][j].abs().compareTo(epsilon) > 0) {
					multiplicand = matrix[k][j].divide(matrix[i][j], rounder);
					subtractRows(k, i, multiplicand, rounder);
				}
				
			}
			//this.printMatrix();
			return result;
		} else
			return null;
	}

	private Pair<Integer, Integer> replaceWithNonZeroDown(BigDecimal epsilon, int i, int j) {
		Pair<Integer, Integer> result;
		int index = i;
		while (index < colSize
				&& matrix[index][j].abs().compareTo(epsilon) < 0) {
			++index;
		}
		if (index < colSize) {
			result = new Pair<Integer, Integer>(i, index);
			if (index != i) {
				swapRows(i, index);
			}
			return result;
		} else
			return null;
	}

	private Pair<Integer, Integer> replaceWithNonZeroUp(BigDecimal epsilon, int i, int j) {
		Pair<Integer, Integer> result;
		int index = i;
		while (index >= 0 && matrix[index][j].abs().compareTo(epsilon) < 0) {
			--index;
		}
		if (index >= 0) {
			result = new Pair<Integer, Integer>(i, index);
			if (index != i) {
				swapRows(i, index);
			}
			return result;
		} else
			return null;
	}

	private void trimTheRow(BigDecimal epsilon, int i, MathContext rounder) {
		if (matrix[i][i].abs().compareTo(epsilon) > 0) {
			BigDecimal multiplicand = BigDecimal.ONE.divide(
					matrix[i][i], rounder);
			for (int j = i; j < rowSize; j++) {
				matrix[i][j] = matrix[i][j].multiply(multiplicand, rounder);
			}
		} else {
			System.out
					.println("The rank of this matrix isn't equal to the column size");
		}
	}

	private Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> triangulate(
			int numOfColumns, BigDecimal epsilon,  MathContext rounder) {
		ArrayList<Pair<Integer, Integer>> rowInversions = new ArrayList<Pair<Integer, Integer>>();
		Pair<Integer, Integer> t;
		//this.printMatrix();
		int i = 0, j = 0;
		for (; j < numOfColumns;) {
			t = triangulateColumnDown(i, i, epsilon, rounder);
			//this.printMatrix();
			if (t != null) {
				if (t.first() != t.second())
					rowInversions.add(t);
				++i;
				++j;
			} else {
				++j;
			}
		}
		ArrayList<Pair<Integer, Integer>> columnInversions = sortMatrix(epsilon, numOfColumns);
		//this.printMatrix();
		return new Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>>(
				rowInversions, columnInversions);
	}

	public int findNonZeroElemInTheRow(BigDecimal epsilon, int i, boolean isHomogeneous) {
		int end = isHomogeneous ? rowSize : rowSize - 1;
		for (int j = i + 1; j < end; j++) { // don't need to look at elements in
											// the concatenated matrix
			//this.printMatrix();
			if (matrix[i][j].abs().compareTo(epsilon) > 0)
			return j;
		}
		return -1;
	}

	public boolean isZeroColumnDown(int i, int j) {
		for (int k = i + 1; k < colSize; k++) {
			if (matrix[k][j].compareTo(BigDecimal.ZERO) != 0)
				return false;
		}
		return true;
	}

	private ArrayList<Pair<Integer, Integer>> sortMatrix(BigDecimal epsilon, int size) {
		int t;
		ArrayList<Pair<Integer, Integer>> columnInversions = new ArrayList<Pair<Integer, Integer>>();
		for (int i = 0; i < size; i++) {
			if (matrix[i][i].abs().compareTo(epsilon) < 0) {
				t = findNonZeroElemInTheRow(epsilon, i, true);
				if(t != -1){
				swapColumns(i, t);
				columnInversions.add(new Pair<Integer, Integer>(t, i));
				}
			}
		}
		return columnInversions;
	}

	private ArrayList<Integer> findFreeVariables(BigDecimal epsilon) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		//this.printMatrix();
		for (int i = 0; i < colSize; i++) {
		//System.out.println(matrix[i][i].toString());
			if (matrix[i][i].abs().compareTo(epsilon) < 0)
				result.add(i);
		}
		return result;
	}

	private void substituteInSystem(BigDecimal epsilon, BDVector solution, MathContext rounder) {
		boolean isHomogeneous;
		if (colSize == rowSize)
			isHomogeneous = true;
		else
			isHomogeneous = false;
		for (int i = colSize - 1; i >= 0; i--) {
			if (matrix[i][i].abs().compareTo(epsilon) > 0) {
				BigDecimal sum = BigDecimal.ZERO;
				if (!isHomogeneous) {
					sum = sum.add(matrix[i][rowSize - 1], rounder);
				}
				for (int j = colSize - 1; j > i; j--) {
					sum = sum.add(
							matrix[i][j].negate().multiply(
									solution.getElement(j), rounder), rounder);
				}
				sum = sum.divide(matrix[i][i], rounder);
				solution.setElement(i, sum);
			}
		}
	}

	public void applyInversionsToSolutions(ArrayList<BDVector> solutions,
			ArrayList<Pair<Integer, Integer>> inversions) {
		Collections.reverse(inversions);
		for(Pair<Integer, Integer> i : inversions){
			for(BDVector vect : solutions){
				BigDecimal t = vect.getElement(i.first());
				vect.setElement(i.first(), vect.getElement(i.second()));
				vect.setElement(i.second(), t);
			}
		}
	}

	public ArrayList<BDVector> findFSS(BigDecimal epsilon, MathContext rounder) {
		ArrayList<Pair<Integer, Integer>> inversions = triangulate(colSize, epsilon,
				rounder).second();
		//this.copy().round(new MathContext(3, RoundingMode.HALF_UP)).printMatrix();
		ArrayList<Integer> freeVariables = findFreeVariables(epsilon);
		ArrayList<BDVector> fss = new ArrayList<BDVector>(freeVariables.size());
		BDVector t;
		for (int i = 0; i < freeVariables.size(); i++) {
			t = new BDVector(colSize);
			t.setElement(freeVariables.get(i), BigDecimal.ONE);
			fss.add(t);
		}
		for (BDVector solution : fss)
			substituteInSystem(epsilon, solution,  rounder);
		applyInversionsToSolutions(fss, inversions);
		return fss;
	}

	private boolean isBasisVector(int columnNumber) {
		int numOfOnes = 0, numOfZeros = 0;
		for (int i = 0; i < colSize; i++) {
			if (matrix[i][columnNumber].equals(BigInteger.ZERO))
				++numOfZeros;
			else if (matrix[i][columnNumber].equals(BigInteger.ONE))
				++numOfOnes;
		}
		if (numOfOnes == 1 && numOfZeros == colSize - 1)
			return true;
		else
			return false;
	}

	public boolean isSatisfiable(BigDecimal epsilon) {
		for (int i = 0; i < rowSize - 1; i++) {
			if (matrix[i][i].abs().compareTo(epsilon) < 0
					&& matrix[i][rowSize - 1].abs().compareTo(epsilon) > 0)
				return false;
		}
		for (int i = rowSize - 1; i < colSize; i++) {
			if (matrix[i][rowSize - 1].abs().compareTo(epsilon) > 0)
				return false;
		}
		return true;
	}

	public boolean isLinearlyIndependent(BDVector vector, BigDecimal epsilon,
			MathContext rounder) {
		this.appendColumn(vector);
		//this.printMatrix();
		triangulate(rowSize - 1, epsilon, rounder);
		//this.printMatrix();
		if (this.isSatisfiable(epsilon))
			return false;
		else
			return true;
	}

	/*
	 * public ArrayList<BDVector> findLiearlyIndependent(int numOfLastColumns,
	 * MathContext rounder) { ArrayList<BDVector> result = new
	 * ArrayList<BDVector>();
	 * 
	 * final int numOfColumnsToHandle = rowSize - numOfLastColumns;
	 * ArrayList<Pair<Integer, Integer>> inversions = new
	 * ArrayList<Pair<Integer, Integer>>(); Pair<Integer, Integer> t;
	 * 
	 * for (int i = 0; i < numOfColumnsToHandle; i++) { t =
	 * triangulateColumnDown(i, i, rounder); if (t != null) { inversions.add(t);
	 * } } for (int i = numOfColumnsToHandle; i >= 0; i--) { t =
	 * triangulateColumnUp(i, i, rounder); if (t != null) { inversions.add(t); }
	 * } for (int i = 0; i < numOfColumnsToHandle; i++) { trimTheRow(i,
	 * rounder); } applyInversions(inversions); for (int i =
	 * numOfColumnsToHandle; i < rowSize; i++) { if (isBasisVector(i))
	 * result.add(getColumn(i)); } return result; }
	 */

	public BDMatrix solve(BigDecimal epsilon, MathContext rounder) {
		BigDecimal multiplicand;
		for (int i = 0; i < colSize; i++) {
			if (replaceWithNonZeroDown(epsilon, i, i) != null) {
				for (int j = i + 1; j < colSize; j++) {
					if (matrix[j][i].compareTo(BigDecimal.ZERO) != 0) {
						multiplicand = matrix[j][i].divide(matrix[i][i],
								rounder);
						subtractRows(j, i, multiplicand, rounder);
						// System.out.println(this.toString());
					}
				}
			} else {
				System.out
						.println("The rank of this matrix isn't equal to the column size");
				return null;
			}
		}
		for (int i = colSize - 1; i >= 0; i--) {
			if (replaceWithNonZeroUp(epsilon, i, i) != null) {
				for (int j = i - 1; j >= 0; j--) {
					if (matrix[j][i].compareTo(BigDecimal.ZERO) != 0) {
						multiplicand = matrix[j][i].divide(matrix[i][i],
								rounder);
						subtractRows(j, i, multiplicand, rounder);
						// System.out.println(this.toString());
					}
				}
			} else {
				System.out
						.println("The rank of this matrix isn't equal to the column size");
				return null;
			}
		}
		for (int i = 0; i < colSize; i++) {
			trimTheRow(epsilon, i, rounder);
			// System.out.println(this.toString());
		}
		BigDecimal[][] res = new BigDecimal[colSize][rowSize - colSize];
		for (int i = 0; i < colSize; i++) {
			for (int j = 0; j < rowSize - colSize; j++) {
				res[i][j] = matrix[i][j + colSize];
			}
		}
		return new BDMatrix(res);
	}

}