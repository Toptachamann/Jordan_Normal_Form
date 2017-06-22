import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class BIMatrix {
	private BigInteger[][] matrix;
	int size;
	private Scanner in;
	private PrintWriter out;

	public BIMatrix() {
		initializeStreams();
	}

	public BIMatrix(int size) {
		this.size = size;
		matrix = new BigInteger[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = BigInteger.ZERO;
			}
		}
	}

	public BIMatrix(BigInteger arr[][]) {
		initializeStreams();
		size = arr.length;
		matrix = new BigInteger[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = arr[i][j];
			}
		}
	}

	public BIMatrix(long arr[][]) {
		initializeStreams();
		size = arr.length;
		matrix = new BigInteger[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = BigInteger.valueOf(arr[i][j]);
			}
		}
	}

	private void initializeStreams() {
		in = new Scanner(System.in);
		OutputStream outputStream = System.out;
		out = new PrintWriter(outputStream);
		out.println(5);
	}

	public void read() {
		int size = in.nextInt();
		long t;
		matrix = new BigInteger[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				t = in.nextLong();
				matrix[i][j] = new BigInteger(String.valueOf(t));
			}
		}
	}

	public void printMatrix() {
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				System.out.print(matrix[i][j].toString() + " ");
			}
			System.out.print('\n');
		}
	}

	public int size() {
		return size;
	}

	public String toString() {
		String res = "";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				res += matrix[i][j].toString() + " ";
			}
			res += '\n';
		}
		return res;
	}

	public BigInteger[][] getMatr() {
		return matrix;
	}

	private void multiplyRow(int row, BigInteger value) {
		for (int i = 0; i < size; i++) {
			matrix[row][i] = matrix[row][i].multiply(value);
		}
	}

	private void divideRow(int row, BigInteger value) {
		for (int i = 0; i < size; i++) {
			if (!matrix[row][i].equals(BigInteger.ZERO)) {
				matrix[row][i] = matrix[row][i].divide(value);
			}
		}
	}

	private BigInteger GCDInTheColumn(int starti) {
		BigInteger res = new BigInteger(String.valueOf(1));
		if (starti == size - 1)
			return res;
		res = matrix[starti][starti].gcd(matrix[starti + 1][starti]);
		for (int i = starti + 2; i < size; i++) {
			res = res.gcd(matrix[i][starti]);
		}
		return res;
	}

	private BigInteger GCDInTheRow(int row) {
		BigInteger res = new BigInteger(String.valueOf(1));
		if (matrix.length == 1) {
			return matrix[0][0];
		}
		res = matrix[row][0].gcd(matrix[row][1]);
		for (int i = 2; i < matrix.length; i++) {
			res = res.gcd(matrix[row][i]);
		}
		return res;
	}

	private void trimTheRow(int row) {
		if (row < size) {
			int numOfNonZeroInRow = numOfNonZeroInTheRow(row);
			if (numOfNonZeroInRow != 0) {
				BigInteger gcdInTheRow = GCDInTheRow(row);
				divideRow(row, gcdInTheRow);
			}
		}
	}

	private BigInteger productInTheRow(int row) {
		BigInteger res = new BigInteger(String.valueOf(1));
		for (int i = 0; i < size; i++) {
			if (!matrix[i][row].equals(BigInteger.ZERO))
				res = res.multiply(matrix[row][i]);
		}
		return res;
	}

	private BigInteger productInTheColumn(int column) {
		BigInteger res = new BigInteger(String.valueOf(1));
		for (int i = column; i < size; i++) {
			if (!matrix[i][column].equals(BigInteger.ZERO))
				res = res.multiply(matrix[i][column]);
		}
		return res;
	}

	private void substrucRows(int first, int second) {
		for (int i = 0; i < size; i++) {
			matrix[second][i] = matrix[second][i].subtract(matrix[first][i]);
		}
	}

	private void swapRows(int first, int second) {
		BigInteger t;
		for (int i = 0; i < size; i++) {
			t = matrix[first][i];
			matrix[first][i] = matrix[second][i];
			matrix[second][i] = t;
		}
	}

	private void swapColumns(int i, int j){
		BigInteger t;
		for(int k = 0; k < size; k++){
			t = matrix[k][i];
			matrix[k][i] = matrix[k][j];
			matrix[k][j] = t;
		}
	}
	
	private boolean replaceWithNonZero(int i) {
		int index = i;
		BigInteger zero = BigInteger.valueOf(0);
		while (index < size && matrix[index][i].equals(zero)) {
			++index;
		}
		if (index < size) {
			if (index != i) {
				swapRows(i, index);
				multiplyRow(i, BigInteger.valueOf(-1));
			}
			return true;
		} else
			return false;
	}

	private int numOfNonZeroInTheColumn(int col) {
		BigInteger zero = BigInteger.valueOf(0);
		int res = 0;
		for (int j = col; j < size; j++) {
			if (!matrix[j][col].equals(zero))
				++res;
		}
		return res;
	}

	private int numOfNonZeroInTheRow(int row) {
		BigInteger zero = BigInteger.valueOf(0);
		int res = 0;
		for (int i = 0; i < size; i++) {
			if (!matrix[row][i].equals(zero))
				++res;
		}
		return res;
	}

	public BigInteger det() {
		// printMatrix();
		// System.out.println();
		BigInteger res = new BigInteger(String.valueOf(1)), divider = new BigInteger(
				String.valueOf(1)), zero = BigInteger.valueOf(0), gcd, product;
		int numOfNonZero;
		for (int i = 0; i < size - 1; i++) {
			if (replaceWithNonZero(i)) {
				gcd = GCDInTheColumn(i);
				product = productInTheColumn(i);
				numOfNonZero = numOfNonZeroInTheColumn(i);
				for (int j = 1; j < numOfNonZero; j++) {
					product = product.divide(gcd);
				}
				for (int j = i; j < size; j++) {
					if (!matrix[j][i].equals(zero)) {
						divider = divider
								.multiply(product.divide(matrix[j][i]));
						multiplyRow(j, product.divide(matrix[j][i]));
					}
				}
				for (int j = i + 1; j < size; j++) {
					if (!matrix[j][i].equals(zero)) {
						substrucRows(i, j);
					}
				}
			} else {
				return BigInteger.valueOf(0);
			}
		}
		for (int i = 0; i < size; i++) {
			res = res.multiply(matrix[i][i]);
		}
		res = res.divide(divider);
		// System.out.println("Determinant: " + res.toString() + '\n');
		return res;
	}

	private void makeTriangular(BigInteger[][] matr) {
		int numOfNonZero;
		BigInteger gcd, columnProduct;
		for (int i = 0; i < matr.length - 1; i++) {
			if (replaceWithNonZero(i)) {
				gcd = GCDInTheColumn(i);
				columnProduct = productInTheColumn(i);
				numOfNonZero = numOfNonZeroInTheColumn(i);
				for (int j = 1; j < numOfNonZero; j++) {
					columnProduct = columnProduct.divide(gcd);
				}
				for (int j = i; j < size; j++) {
					if (!matrix[j][i].equals(BigInteger.ZERO)) {
						multiplyRow(j, columnProduct.divide(matrix[j][i]));
					}
				}
				for (int j = i + 1; j < size; j++) {
					if (!matrix[j][i].equals(BigInteger.ZERO)) {
						substrucRows(i, j);
					}
				}

				for (int j = i; j < size; j++) {
					trimTheRow(j);
				}
			}
		}
	}

	private int numOfZeroRows() {
		boolean hasNonZero = false;
		int res = 0;
		for (int i = 0; i < size; i++) {
			hasNonZero = false;
			for (int j = 0; j < size && !hasNonZero; j++) {
				if (!matrix[i][j].equals(BigInteger.ZERO))
					hasNonZero = true;
			}
			if (!hasNonZero)
				++res;
		}
		return res;
	}

	public ArrayList<ArrayList<BigDecimal>> findEigenVectors(MathContext rounder) {
		// printMatrix();
		// System.out.println();
		// System.out.println("Making this matrix triangular");
		BigInteger[][] matr = (BigInteger[][]) matrix.clone();
		makeTriangular(matr);
		//this.printMatrix();
		// printMatrix();
		//System.out.println();
		return findFSS(rounder);
	}

	private BigDecimal sumOfVariables(int row, ArrayList<BigDecimal> vect,
			MathContext rounder) {
		if (row == size - 1)
			return BigDecimal.ZERO;
		BigDecimal res = BigDecimal.ZERO;
		for (int i = row + 1; i < size; i++) {
			res = res.add(vect.get(i).multiply(new BigDecimal(matrix[row][i]),
					rounder));
		}
		res = res.multiply(BigDecimal.valueOf(-1.0), rounder);
		return res;
	}

	private boolean isZeroRow(int i) {
		for (int j = 0; j < size; j++) {
			if (!matrix[i][j].equals(BigInteger.ZERO))
				return false;
		}
		return true;
	}

	private ArrayList<Integer> findZeroRows() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			if (matrix[i][i].compareTo(BigInteger.ZERO) == 0) {
				res.add(i);
			}
		}
		return res;
	}
	
	private ArrayList<Integer> findNonZeroElemsInTheRow(int row){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = row + 1; i < size; i++){
			if(matrix[row][i].compareTo(BigInteger.ZERO) != 0){
				result.add(i);
			}
		}
		return result;
	}
	
	private boolean isZeroColDown(int i, int j){
		boolean checker = true;
		for(int k = i + 1; k < size && checker; k++){
			if(matrix[k][j].compareTo(BigInteger.ZERO) != 0){
				checker = false;
			}
		}
		return checker;
	}
	
	private ArrayList<Pair<Integer, Integer>> sortMatrix(){
		ArrayList<Integer> t;
		ArrayList<Pair<Integer, Integer>> inversions = new ArrayList<Pair<Integer, Integer>>();
		for(int i = 0; i < size - 1; i++){
			if(matrix[i][i].compareTo(BigInteger.ZERO) == 0){
				t = findNonZeroElemsInTheRow(i);
				for(int j : t){
					if(isZeroColDown(i, j)){
						swapColumns(i, j);
						inversions.add(new Pair<Integer, Integer>(i, j));
						break;
					}
				}
			}
		}
		return inversions;
	}
	
	private ArrayList<ArrayList<BigDecimal>> findFSS(MathContext rounder) {
		ArrayList<Pair<Integer, Integer>> inversions = sortMatrix();
		//this.printMatrix();
		ArrayList<Integer> zeroRows = findZeroRows();
		if (zeroRows.size() == 0) {
			System.out.println("Error, problem with eigenvalue");
			return null;
		} else {
			BigDecimal ONE = new BigDecimal(1.0);
			ONE = ONE.setScale(5, RoundingMode.HALF_UP);
			ArrayList<ArrayList<BigDecimal>> eigenVectors = new ArrayList<ArrayList<BigDecimal>>(
					zeroRows.size());
			for (int i = 0; i < zeroRows.size(); i++) {
				eigenVectors.add(new ArrayList<BigDecimal>(size));
				for (int j = 0; j < size; j++) {
					eigenVectors.get(i).add(BigDecimal.ZERO);
				}
			}
			for (int i = 0; i < zeroRows.size(); i++) {
				eigenVectors.get(i).set(zeroRows.get(i), ONE);
			}
			if (zeroRows.size() == size)
				return eigenVectors;
			BigDecimal t;
			for (int i = 0; i < zeroRows.size(); i++) {
				for (int j = size - i - 1; j >= 0; j--) {
					if (!(new BigDecimal(matrix[j][j])).equals(BigDecimal.ZERO)) {
						t = sumOfVariables(j, eigenVectors.get(i), rounder);
						eigenVectors.get(i)
								.set(j,
										t.divide(new BigDecimal(matrix[j][j]),
												rounder));
					}
				}
			}
			BigDecimal temp;
			for(Pair<Integer, Integer> i : inversions){
				for(ArrayList<BigDecimal> j : eigenVectors){
					temp = j.get(i.first());
					j.set(i.first(), j.get(i.second()));
					j.set(i.second(), temp);
				}
			}
			return eigenVectors;
		}
	}
	
	public ArrayList<BDVector> findEigenBDVectors(MathContext rounder){
		ArrayList<ArrayList<BigDecimal>> t = findEigenVectors(rounder);
		ArrayList<BDVector> result = new ArrayList<BDVector>(t.size());
		for(int i = 0; i < t.size(); i++){
			result.add(new BDVector(t.get(i)));
		}
		return result;
	}

	private BigDecimal productOfRows(int row, ArrayList<BigDecimal> vect,
			MathContext rounder) {
		BigDecimal res = BigDecimal.ZERO;
		for (int i = 0; i < size; i++) {
			res = res.add(vect.get(i).multiply(new BigDecimal(matrix[row][i]),
					rounder));
		}
		return res;
	}

	public ArrayList<BigDecimal> multiply(ArrayList<BigDecimal> vect,
			MathContext rounder) {
		if (vect.size() == this.size) {
			ArrayList<BigDecimal> res = new ArrayList<BigDecimal>();
			for (int i = 0; i < this.size; i++) {
				res.add(productOfRows(i, vect, rounder));
			}
			return res;
		} else
			return null;
	}

	public BIMatrix copy() {
		BigInteger[][] copy = (BigInteger[][]) matrix.clone();
		BIMatrix res = new BIMatrix(copy);
		return res;
	}

	public void setElement(int i, int j, BigInteger value) {
		matrix[i][j] = value;
	}

	public BIMatrix minor(int n, int m) {
		BigInteger[][] t = new BigInteger[size - 1][size - 1];
		int k = 0, l;
		for (int i = 0; i < size - 1;) {
			l = 0;
			if (k != n) {
				for (int j = 0; j < size - 1;) {
					if (l != m) {
						t[i][j] = copy(k, l);
						j++;
					}
					l++;
				}
				i++;
			}
			k++;
		}
		return new BIMatrix(t);
	}

	public BigInteger algebraicComplement(int i, int j) {
		BIMatrix t = minor(i, j);
		if ((i + j) % 2 == 1) {
			return t.det().multiply(BigInteger.valueOf((long) -1));
		} else
			return t.det();
	}

	public BDMatrix inverse(MathContext rounder) {
		BigDecimal[][] t = new BigDecimal[size][size];
		BigDecimal det = new BigDecimal(this.copy().det(), rounder);
		BigDecimal ac;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ac = new BigDecimal(algebraicComplement(j, i));
				t[i][j] = ac.divide(det, rounder);
			}
		}
		return new BDMatrix(t);
	}

	public BIMatrix multiply(BIMatrix m){
		BigInteger [][] matr = m.getMatr();
		BigInteger[][] result = new BigInteger[size][size];
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				BigInteger entry = BigInteger.ZERO;
				for(int k = 0; k < size; k++){
					entry = entry.add(matrix[i][k].multiply(matr[k][j]));
				}
				result[i][j] = entry;
			}
		}
		return new BIMatrix(result);
	}

	public BDMatrix toBDMatrix() {
		BigDecimal[][] res = new BigDecimal[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				res[i][j] = new BigDecimal(matrix[i][j]);
			}
		}
		return new BDMatrix(res);
	}

	public BigInteger copy(int i, int j) {
		return new BigInteger(matrix[i][j].toString());
	}

	public RectBIMatrix toRectBIMatrix() {
		return new RectBIMatrix(this.getMatr());
	}

}