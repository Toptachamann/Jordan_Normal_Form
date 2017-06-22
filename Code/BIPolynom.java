import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class BIPolynom {
	private ArrayList<BigInteger> polynom;

	public BIPolynom(ArrayList<BigInteger> coefs) {
		int index = coefs.size() - 1;
		BigInteger zero = BigInteger.valueOf(0);
		while (index > 0 && coefs.get(index).equals(zero)) {
			index--;
		}
		polynom = new ArrayList<BigInteger>();
		if (index >= 0) {
			for (int i = 0; i <= index; i++) {
				polynom.add(coefs.get(i));
			}
		} else {
			polynom.add(BigInteger.valueOf(0));
		}
	}

	public String toString() {
		StringBuilder res = new StringBuilder(0);
		for (int i = 0; i < polynom.size(); i++) {
			if (polynom.get(i).compareTo(BigInteger.ZERO) != 0 || i == 0) {
				if (polynom.get(i).compareTo(BigInteger.ZERO) >= 0) {
					res.append('+');
				}
				if (i == 0) {
					res.append(polynom.get(i).toString() + " ");
				} else if (i == 1) {
					res.append(polynom.get(i).toString() + "x ");
				} else {
					res.append(polynom.get(i).toString() + "x^"
							+ String.valueOf(i) + "  ");
				}
			}
		}
		if (res.length() > 1)
			res.deleteCharAt(res.length() - 1);
		return res.toString();
	}

	public ArrayList<BigInteger> takeDerivative() {
		ArrayList<BigInteger> res = new ArrayList<BigInteger>();
		for (int i = 1; i < this.polynom.size(); i++) {
			res.add(this.polynom.get(i).multiply(BigInteger.valueOf(i)));
		}
		return res;
	}

	public double evaluate(double point) {
		double t = 1.0, res = 0;
		for (int i = 0; i < this.polynom.size(); i++) {
			res += BigDecimal.valueOf(t)
					.multiply(new BigDecimal(polynom.get(i))).doubleValue();
			t *= point;
		}
		return res;
	}

	public BigDecimal evaluate(BigDecimal point) {
		BigDecimal t = new BigDecimal(1.0), res = new BigDecimal(0.0);
		for (int i = 0; i < this.polynom.size(); i++) {
			res = res.add(t.multiply(new BigDecimal(polynom.get(i))));
			t = t.multiply(point);
		}
		return res.setScale(12, BigDecimal.ROUND_HALF_UP);
	}

	public BigInteger evaluate(BigInteger point) {
		BigInteger t = new BigInteger(String.valueOf(1)), res = new BigInteger(
				String.valueOf(0));
		for (int i = 0; i < this.polynom.size(); i++) {
			res = res.add(t.multiply(polynom.get(i)));
			t = t.multiply(point);
		}
		return res;
	}

	public ArrayList<BigInteger> findRoots() {
		ArrayList<BigInteger> roots = new ArrayList<BigInteger>();
		return roots;
	}

	public int size() {
		return polynom.size();
	}

	public void ensureCapacity(int n) {
		this.polynom.ensureCapacity(n);
	}

	public BigInteger get(int index) {
		return this.polynom.get(index);
	}

	private void trimToSize() {
		int i = polynom.size() - 1;
		BigInteger zero = BigInteger.valueOf(0);
		while (polynom.get(i).equals(zero)) {
			polynom.remove(i);
		}
	}

	public void add(BIPolynom p) {
		if (polynom.size() >= p.size()) {
			for (int i = 0; i < p.size(); i++) {
				polynom.set(i, p.get(i).add(polynom.get(i)));
			}
		} else {
			polynom.ensureCapacity(p.size());
			for (int i = 0; i < polynom.size(); i++) {
				polynom.set(i, p.get(i).add(polynom.get(i)));
			}
			for (int i = polynom.size(); i < p.size(); i++) {
				polynom.add(p.get(i));
			}
		}
		trimToSize();
	}

	private BIPolynom divideByRoot(BigInteger root, BIPolynom polynom) {
		polynom.trimToSize();
		int size = polynom.size();
		ArrayList<BigInteger> res = new ArrayList<BigInteger>();
		for (int i = 0; i < size - 1; i++) {
			res.add(BigInteger.ZERO);
		}
		res.set(size - 2, polynom.get(size - 1));
		for (int i = size - 3; i >= 0; i--) {
			res.set(i, root.multiply(res.get(i + 1)).add(polynom.get(i + 1)));
		}
		BIPolynom tt = new BIPolynom(res);
		return tt;
	}

	public ArrayList<Pair<BigInteger, Integer>> findIntegerRoots(long upBound) {
		trimToSize();
		if (polynom.size() > 1
				|| !polynom.get(polynom.size() - 1).equals(BigInteger.ZERO)) {
			@SuppressWarnings("unchecked")
			ArrayList<BigInteger> arr = (ArrayList<BigInteger>) this.polynom
					.clone();
			BIPolynom copy = new BIPolynom(arr);
			ArrayList<Pair<BigInteger, Integer>> res = new ArrayList<Pair<BigInteger, Integer>>();
			int size = polynom.size() - 1;
			BigInteger point = BigInteger.valueOf(0), upperBound = BigInteger
					.valueOf(upBound), minus = BigInteger.valueOf(-1), one = BigInteger
					.valueOf(1);
			while (size > 0 && upperBound.compareTo(point) == 1) {
				while (size > 0 && copy.evaluate(point).equals(BigInteger.ZERO)) {
					copy = divideByRoot(point, copy);
					System.out.println(copy.toString());
					--size;
					if (res.size() > 0
							&& res.get(res.size() - 1).first().equals(point)) {
						res.get(res.size() - 1).setSecond(
								res.get(res.size() - 1).second() + 1);
					} else {
						res.add(new Pair<BigInteger, Integer>(point, 1));
					}
				}
				point = point.multiply(minus);
				while (size > 0 && copy.evaluate(point).equals(BigInteger.ZERO)) {
					copy = divideByRoot(point, copy);
					System.out.println(copy.toString());
					--size;
					if (res.size() > 0
							&& res.get(res.size() - 1).first().equals(point)) {
						res.get(res.size() - 1).setSecond(
								res.get(res.size() - 1).second() + 1);
					} else {
						res.add(new Pair<BigInteger, Integer>(point, 1));
					}
				}
				point = point.multiply(minus).add(one);
			}
			return res;
		} else
			return null;
	}

}