import java.math.*;
import java.util.*;

public class LambdaMatrix {
	private BIPolynom[][] matr;
	int size;

	public LambdaMatrix(BigInteger[][] arr) {
		initializeMatrix(arr);
	}

	public LambdaMatrix(BIMatrix m) {
		initializeMatrix(m.getMatr());
	}

	public LambdaMatrix(LMatrix l) {
		BIMatrix m = new BIMatrix(l.getMatr());
		initializeMatrix(m.getMatr());
	}

	private void initializeMatrix(BigInteger[][] arr) {
		size = arr.length;
		matr = new BIPolynom[size][size];

		ArrayList<BigInteger> t = new ArrayList<BigInteger>(1);
		t.add(BigInteger.ZERO);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				t.set(0, new BigInteger(arr[i][j].toString()));
				matr[i][j] = new BIPolynom(t);
			}
		}
		this.toString();
		t.add(BigInteger.valueOf(-1));
		t.set(0, BigInteger.valueOf(0));
		BIPolynom p = new BIPolynom(t);
		for (int i = 0; i < size; i++) {
			matr[i][i].add(p);
		}
	}

	public void printMatrix() {
		System.out.println("Lamda matrix is:");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print('[' + matr[i][j].toString() + ']');
			}
			System.out.println();
		}
	}

	public String toString() {
		String res = "";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				res += '[' + matr[i][j].toString() + ']';
			}
			res += '\n';
		}
		return res;
	}

	public BIMatrix evaluateAt(BigInteger point) {
		BigInteger[][] arr = new BigInteger[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				arr[i][j] = matr[i][j].evaluate(point);
			}
		}
		BIMatrix matr = new BIMatrix(arr);
		return matr;
	}

	private BDPolynom getLi(ArrayList<Pair<BigDecimal, BigDecimal>> values,
			int i, MathContext rounder) {
		BigDecimal divider = BigDecimal.ONE;
		ArrayList<BigDecimal> t = new ArrayList<BigDecimal>();
		t.add(BigDecimal.ONE);
		BDPolynom res = new BDPolynom(t), p;
		t.add(BigDecimal.ONE);
		for (int j = 0; j <= size; j++) {
			if (i != j) {
				divider = divider.multiply(values.get(i).first()
						.subtract(values.get(j).first(), rounder), rounder);
				t.set(0,
						values.get(j).first()
								.multiply(BigDecimal.valueOf(-1.0), rounder));
				p = new BDPolynom(t);
				res = res.multiply(p, rounder);
			}
		}
		res = res.divide(divider, rounder);
		return res;
	}

	public BDPolynom getPolynom(MathContext rounder) {
		System.out.println("Printing point - value");
		ArrayList<Pair<BigDecimal, BigDecimal>> values = new ArrayList<>();
		for (int i = 0; i <= size; i++) {
			values.add(new Pair<BigDecimal, BigDecimal>(BigDecimal
					.valueOf((double) i), new BigDecimal(evaluateAt(
					BigInteger.valueOf(i)).det()).setScale(2)));
		}
		for (int i = 0; i <= size; i++) {
			System.out.println(values.get(i).toString());
		}
		BDPolynom res = new BDPolynom();
		for (int i = 0; i <= size; i++) {
			res.add(getLi(values, i, rounder).multiply(values.get(i).second(), rounder));
		}
		return res;
	}

	public int size() {
		return this.size;
	}
}
