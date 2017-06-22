import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public class BDPolynom {
	private ArrayList<BigDecimal> polynom;

	public BDPolynom(ArrayList<BigDecimal> coefs) {
		int index = coefs.size() - 1;
		BigDecimal zero = BigDecimal.valueOf(0.0);
		while (index >= 0 && coefs.get(index).equals(zero)) {
			index--;
		}
		polynom = new ArrayList<BigDecimal>();
		if (index >= 0) {
			for (int i = 0; i <= index; i++) {
				polynom.add(coefs.get(i));
			}
		} else {
			polynom.add(BigDecimal.valueOf(0.0));
		}
	}

	public BDPolynom() {
		polynom = new ArrayList<BigDecimal>();
		polynom.add(BigDecimal.valueOf(0.0));
	}

	public String toString() {
		String res = "";
		for (int i = 0; i < polynom.size(); i++) {
			res += polynom.get(i).toString() + " ";
		}
		return res;
	}

	public ArrayList<BigDecimal> takeDerivative() {
		ArrayList<BigDecimal> res = new ArrayList<BigDecimal>();
		for (int i = 1; i < this.polynom.size(); i++) {
			res.add(this.polynom.get(i)
					.multiply(BigDecimal.valueOf((double) i)));
		}
		return res;
	}

	public double evaluate(double point) {
		double t = 1.0, res = 0;
		for (int i = 0; i < this.polynom.size(); i++) {
			res += this.polynom.get(i).multiply(BigDecimal.valueOf(t))
					.doubleValue();
			t *= point;
		}
		return res;
	}

	public BigDecimal evaluate(BigDecimal point) {
		BigDecimal t = new BigDecimal(1.0), res = new BigDecimal(0.0);
		for (int i = 0; i < this.polynom.size(); i++) {
			res = res.add(t.multiply(polynom.get(i)));
			t = t.multiply(point);
		}
		return res.setScale(12, BigDecimal.ROUND_HALF_UP);
	}

	public ArrayList<BigDecimal> findRoots() {
		ArrayList<BigDecimal> roots = new ArrayList<BigDecimal>();
		return roots;
	}

	public int size() {
		return polynom.size();
	}

	public void ensureCapacity(int n) {
		this.polynom.ensureCapacity(n);
	}

	public BigDecimal get(int index) {
		return this.polynom.get(index);
	}

	private void trimToSize() {
		int i = polynom.size() - 1;
		BigDecimal zero = BigDecimal.valueOf(0.0);
		while (polynom.get(i).equals(zero)) {
			polynom.remove(i);
		}
	}

	public void add(BDPolynom p) {
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

	public BDPolynom multiply(BigDecimal value, MathContext rounder) {
		ArrayList<BigDecimal> res = new ArrayList<BigDecimal>();
		for (int i = 0; i < polynom.size(); i++) {
			res.add(i, polynom.get(i).multiply(value, rounder));
		}
		BDPolynom p = new BDPolynom(res);
		return p;
	}

	public BDPolynom divide(BigDecimal value, MathContext rounder) {
		ArrayList<BigDecimal> res = new ArrayList<BigDecimal>();
		for (int i = 0; i < polynom.size(); i++) {
			res.add(i,
					polynom.get(i).divide(value, rounder.getPrecision(), rounder.getRoundingMode()));
		}
		BDPolynom p = new BDPolynom(res);
		return p;
	}

	public BDPolynom multiply(BDPolynom p, MathContext rounder) {
		int totalSize = p.size() + polynom.size();
		ArrayList<BigDecimal> res = new ArrayList<BigDecimal>(totalSize);
		BigDecimal zero = BigDecimal.ZERO;
		for (int i = 0; i < totalSize; i++) {
			res.add(zero);
		}
		for (int i = 0; i < p.size(); i++) {
			for (int j = 0; j < polynom.size(); j++) {
				res.set(i + j,
						res.get(i + j).add(p.get(i).multiply(polynom.get(j), rounder)));
			}
		}
		BDPolynom a = new BDPolynom(res);
		return a;
	}

	public BIPolynom round(MathContext rounder) {
		BIPolynom res;
		ArrayList<BigInteger> arr = new ArrayList<BigInteger>();
		BigDecimal t;
		for (int i = 0; i < polynom.size(); i++) {
			t = polynom.get(i).setScale(rounder.getPrecision(), rounder.getRoundingMode());
			arr.add(t.toBigInteger());
		}
		res = new BIPolynom(arr);
		return res;
	}
}
