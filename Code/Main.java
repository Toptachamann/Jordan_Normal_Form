import java.io.*;
import java.math.*;
import java.util.*;

@SuppressWarnings("unused")
public class Main {
	public static void main(String[] args) {
		MathContext rounder = new MathContext(50, RoundingMode.HALF_UP);
		MathContext preciseRounder = new MathContext(100, RoundingMode.HALF_UP);
		MathContext zeroRounder = new MathContext(0, RoundingMode.HALF_UP);
		MathContext printRounder = new MathContext(4, RoundingMode.HALF_UP);
		BigDecimal epsilon = new BigDecimal("1E-40");
		
		LMatrix m = new LMatrix();
		m.read();
		LambdaMatrix lm = new LambdaMatrix(m);
		lm.printMatrix();

		BIMatrix BIOperator = lm.evaluateAt(BigInteger.ZERO);
		BDMatrix BDOperator = lm.evaluateAt(BigInteger.ZERO).toBDMatrix();
		
		
		BDPolynom eigenPolynom = lm.getPolynom(preciseRounder);
		// System.out.println(polynom.toString());
		BIPolynom roundedEigenPolynom = eigenPolynom.round(zeroRounder);
		System.out.println("Characteristical polynom is:\n"
				+ roundedEigenPolynom.toString());

		ArrayList<Pair<BigInteger, Integer>> roots = roundedEigenPolynom
				.findIntegerRoots(1000);
		System.out.println("Roots:");
		for (Pair<BigInteger, Integer> i : roots) {
			System.out.println("The root " + i.first() + " has multiplicity "
					+ i.second());
		}

		int sumOfMultiplicities = 0;
		for (Pair<BigInteger, Integer> i : roots) {
			sumOfMultiplicities += i.second();
		}
		if (sumOfMultiplicities != roundedEigenPolynom.size() - 1) {
			System.out
					.println("This isn't an operator of simple structure with integer roots");
		} else {
			ArrayList<Triple<BigInteger, Integer, ArrayList<BDVector>>> eigenvaluesAndEigenvectors 
			= new ArrayList<Triple<BigInteger, Integer, ArrayList<BDVector>>>(roots.size());
			for (int k = 0; k < roots.size(); k++) {
				eigenvaluesAndEigenvectors
						.add(k,
								new Triple<BigInteger, Integer, ArrayList<BDVector>>());
				eigenvaluesAndEigenvectors.get(k).setL(roots.get(k).first());
				eigenvaluesAndEigenvectors.get(k).setC(roots.get(k).second());
				BIMatrix matr = lm.evaluateAt(roots.get(k).first());
				//System.out.println("Finding eigenvectors for eigenvalue " + roots.get(k) + '\n');
				ArrayList<BDVector> eigvects = matr.toBDMatrix().findFSS(epsilon, rounder);
				eigenvaluesAndEigenvectors.get(k).setR(eigvects);
				System.out
						.println("\nEigenvectors that correspond to eigenvalue "
								+ roots.get(k).first().toString() + ":");
				for (int i = 0; i < eigvects.size(); i++) {
					System.out.println(eigvects.get(i).copy().toString(epsilon, printRounder));
					System.out
							.println("The product of the matrix and eigenvector is:");
					System.out.println(BDOperator
							.multiply(eigvects.get(i), rounder).toString(epsilon, printRounder));
					System.out.println(BDOperator
							.multiply(eigvects.get(i), rounder).equals(eigvects.get(i).multiply(new BigDecimal(roots.get(k).first()), rounder), epsilon, rounder));
					System.out.println();
				}
			}
			boolean checker = true;
			for (int i = 0; i < roots.size() && checker; i++) {
				if (eigenvaluesAndEigenvectors.get(i).center() != eigenvaluesAndEigenvectors
						.get(i).right().size()) {
					checker = false;
				}
			}
			if (checker) {
				MathContext r = new MathContext(3, RoundingMode.HALF_UP);
				System.out.println("This is an operator of simple structure");
				BDMatrix T = new BDMatrix();
				BIMatrix diagonalized = new BIMatrix(lm.size());
				int counter = 0;
				for (Triple<BigInteger, Integer, ArrayList<BDVector>> i : eigenvaluesAndEigenvectors) {
					for (int j = 0; j < i.center(); j++) {
						diagonalized.setElement(counter, counter, i.left());
						++counter;
					}
					for (BDVector j : i.right()) {
						T.appendColumn(j);
					}
				}
				System.out.println("\nInitial matrix:\n\n"
						+ BIOperator.toString());
				System.out.println("\nDiagonalized matrix is:\n\n"
						+ diagonalized.toString());
				System.out.println("\nMatrix T:\n\n"
						+ T.copy().round(r).toString());
				BDMatrix concatenated = T
						.copy()
						.multiply(diagonalized.toBDMatrix(), rounder)
						.concatenate(
								BDOperator);
				BDMatrix tInverse = concatenated.solve(epsilon, rounder);
				System.out.println("T^(-1) is: \n\n"
						+ tInverse.copy().round(r).toString());
				System.out.println("T * diagonalized * T^(-1), must be A:\n"
						+ T.multiply(diagonalized.toBDMatrix(), rounder)
								.multiply(tInverse, rounder).round(r)
								.toString());
				BDMatrix result = tInverse.multiply(
						BDOperator, rounder)
						.multiply(T, rounder);
				System.out
						.println("Product T^(-1) * A * T is (must be diagonalized):\n\n"
								+ result.toString());
				System.out.println("Rounded result: \n"
						+ result.round(r).toString());
			} else {
				BDMatrix operator = BDOperator;
				System.out
						.println("This isn't an operator of simple structure with integer roots");
				BDMatrix jordanMatrix = new BDMatrix(lm.size());
				BDMatrix T = new BDMatrix(lm.size());
				int startPoint = 0;
				for (Triple<BigInteger, Integer, ArrayList<BDVector>> i : eigenvaluesAndEigenvectors) {
					ArrayList<ArrayList<BDVector>> chainBasis = findChainBasis(
							i, lm, epsilon, rounder);
					for (ArrayList<BDVector> chain : chainBasis) {
						Collections.reverse(chain);
						createJordanCells(startPoint, i.left(), chain,
								jordanMatrix, T);
						startPoint += chain.size();
					}
				}
				BDMatrix Tinverse = T.copy().inverse(epsilon, rounder);                                         //TAplusJordan.solve(rounder);
				System.out.println("Initial operator\n");
				operator.printMatrix();
				System.out.println("Jordan normal form:\n");
				jordanMatrix.printMatrix();
				System.out.println("T:\n");
				T.copy().round(printRounder).printMatrix();
				System.out.println("T^(-1):\n");
				Tinverse.copy().round(printRounder).printMatrix();
				System.out.println("T^(-1)*A*T:\n");
				Tinverse.multiply(operator, rounder).multiply(T, rounder).round(printRounder).printMatrix();;
			}

		}
	}

	public static void createJordanCells(int startPoint, BigInteger eigenvalue,
			ArrayList<BDVector> chain, BDMatrix jordanMatrix, BDMatrix T) {
		BigDecimal bdEigenValue = new BigDecimal(eigenvalue);
		for (int i = startPoint; i < startPoint + chain.size(); i++) {
			jordanMatrix.setElement(i, i, bdEigenValue);
		}
		for (int i = startPoint + 1; i < startPoint + chain.size(); i++) {
			jordanMatrix.setElement(i - 1, i, BigDecimal.ONE);
			;
		}
		for (BDVector chainVector : chain) {
			T.setColumn(startPoint, chainVector);
			++startPoint;
		}

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<BDVector>> findChainBasis(Triple<BigInteger, Integer, ArrayList<BDVector>> triple, LambdaMatrix lm, BigDecimal epsilon, MathContext rounder) {
		ArrayList<BDVector> eigenvects = triple.right(), temp;
		ArrayList<ArrayList<BDVector>> chainBasis = new ArrayList<ArrayList<BDVector>>(
				eigenvects.size());
		ArrayList<ArrayList<BDVector>> fssArr = new ArrayList<ArrayList<BDVector>>();
		fssArr.add(new ArrayList<BDVector>());
		fssArr.add((ArrayList<BDVector>) eigenvects.clone());

		ArrayList<Integer> di = new ArrayList<Integer>();
		di.add(0);
		di.add(triple.right().size());

		int index = 1;
		BIMatrix operator = lm.evaluateAt(triple.left()), currentOperator = lm
				.evaluateAt(triple.left());
		BDMatrix bdOperator = operator.toBDMatrix();
		ArrayList<BDVector> fss;

		while (fssArr.get(fssArr.size() - 1).size() < triple.center()) {
			++index;
			currentOperator = currentOperator.multiply(operator);
			//System.out.println("Printing system for fss for eigenvalue " + triple.left() + ". Power of an operator is " + index + "\n");
			fss = currentOperator.copy().findEigenBDVectors(rounder);
			fssArr.add(fss);
			di.add(fss.size());
		}
		if (fssArr.get(fssArr.size() - 1).size() > triple.center())
			System.out.println("Fatal error with fss");
		else {
			ArrayList<Pair<Integer, Integer>> chainsLengths = computeChainsLengths(di);
			int operatorPower = index, numOfVectorsToDetermine = triple
					.center();
			ArrayList<BDVector> linearlyIndependent;
			while (operatorPower > 0) {
				if (chainsLengths.size() > 0 && chainsLengths.get(0).first() == operatorPower) {
					ArrayList<BDVector> firstSet = (ArrayList<BDVector>) fssArr
							.get(operatorPower - 1).clone();
					for (ArrayList<BDVector> i : chainBasis) {
						for (int j = operatorPower - 1; j >= 0; j--) {
							firstSet.add(i.get(i.size() - 1 - j).copy());
						}
					}
					linearlyIndependent = findLinearlyIndependent(firstSet,
							fssArr.get(operatorPower), epsilon, rounder);
					chainsLengths.remove(0);
					for (BDVector i : linearlyIndependent) {
						ArrayList<BDVector> newChain = new ArrayList<BDVector>();
						newChain.add(i);
						// operator.printMatrix();
						for (int j = operatorPower - 1; j > 0; j--) {
							newChain.add(bdOperator.multiply(
									newChain.get(newChain.size() - 1), rounder));
						}
						chainBasis.add(newChain);
					}
				}
				--operatorPower;
			}
		}

		return chainBasis;
	}
	
	public static ArrayList<Pair<Integer, Integer>> computeChainsLengths(ArrayList<Integer> di){
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		ArrayList<Integer> mi = new ArrayList<Integer>();
		for(int i = 1; i < di.size(); i++){
			mi.add(di.get(i) - di.get(i-1));
		}
		while(mi.size() > 0){
			int t = mi.get(mi.size() - 1);
			int length = mi.size();
			mi.remove(mi.size() - 1);
			if(t != 0){
				result.add(new Pair<Integer, Integer>(length, t));
				for(int i = 0; i < mi.size(); i++){
					mi.set(i, mi.get(i) - t);
				}
			}
		}
		System.out.println("");
		return result;
	}

	public static ArrayList<BDVector> findLinearlyIndependent(
			ArrayList<BDVector> firstSet, ArrayList<BDVector> secondSet, BigDecimal epsilon,
			MathContext rounder) {
		if(firstSet.size() == 0)
			return secondSet;
		ArrayList<BDVector> result = new ArrayList<BDVector>();
		BDMatrix fromFirstSet = new BDMatrix();
		for (BDVector i : firstSet)
			fromFirstSet.appendColumn(i);
		for (BDVector i : secondSet) {
			if (fromFirstSet.copy().isLinearlyIndependent(i, epsilon, rounder)) {
				result.add(i.copy());
				fromFirstSet.appendColumn(i);
			}
			//fromFirstSet.printMatrix();
		}
		return result;
	}

	/*
	 * int numOfChains = triple.right().size(), vectSize =
	 * triple.right().get(0).size(); ArrayList<Integer> alreadyDetermined = new
	 * ArrayList<Integer>(numOfChains); int numOfDeterminedBasisVectors = 0,
	 * powerOfAnOperator = index - 1; BDMatrix I = new BDMatrix(lm.size());
	 * for(int i = 0; i < vectSize; i++){ I.setElement(i, i, BigDecimal.ONE); }
	 * while(powerOfAnOperator > 0){ BDMatrix forFindingLinearlyIndependent =
	 * new BDMatrix(); for(BDVector i : fssArr.get(powerOfAnOperator)){
	 * forFindingLinearlyIndependent.appendColumn(i.getVector()); } for(int i :
	 * alreadyDetermined){ for(int j = 0; j <= powerOfAnOperator; j++){
	 * forFindingLinearlyIndependent
	 * .appendColumn(chainBasis.get(i).get(j).getVector()); } }
	 * forFindingLinearlyIndependent.concatenate(I);
	 * 
	 * --powerOfAnOperator; }
	 */

	/*
	 * for(Triple<BigInteger, Integer, ArrayList<ArrayList<BigDecimal>>> i :
	 * eigenvaluesAndEigenvectors){ if(i.center() != i.right().size()){
	 * ArrayList<ArrayList<BigDecimal>> fss = i.right(); BIMatrix temp =
	 * lm.evaluateAt(i.left()); while(fss.size() < i.center()){ temp =
	 * temp.multiply(lm.evaluateAt(i.left())); fss =
	 * temp.copy().findEigenVectors(rounder); } for(ArrayList<BigDecimal> j :
	 * fss){ System.out.println(j.toString()); } } }
	 */

}
