import java.math.*;
import java.util.ArrayList;
public class BDVector {
	private BigDecimal[] vector;
	private int size;
	
	public BDVector(int size){
		this.size = size;
		vector = new BigDecimal[size];
		for(int i = 0; i < size; i++){
			vector[i] = BigDecimal.ZERO;
		}
	}
	public BDVector(BigInteger[] arr){
		size = arr.length;
		vector = new BigDecimal[size];
		for(int i = 0; i < size; i++){
			vector[i] = new BigDecimal(arr[i]);
		}
	}
	public BDVector(BigDecimal[] arr){
		size = arr.length;
		vector = new BigDecimal[size];
		for(int i = 0; i < size; i++){
			vector[i] = new BigDecimal(arr[i].toString());
		}
	}
	public BDVector(ArrayList<BigDecimal> list){
		this.size = list.size();
		vector = new BigDecimal[size];
		for(int i = 0; i < size; i++){
			vector[i] = new BigDecimal(list.get(i).toString());
		}
	}
	
	public boolean equals(BDVector vect, BigDecimal epsilon, MathContext rounder){
		if(this.size != vect.size())
			return false;
		for(int i = 0; i < this.size; i++){
			if(vector[i].subtract(vect.getElement(i), rounder).abs().compareTo(epsilon) > 0)
				return false;
		}
		return true;
	}
	
	public BDVector multiply(BigDecimal multiplicand, MathContext rounder){
		BigDecimal[] vect = new BigDecimal[this.size()];
		for(int i = 0; i < this.size; i++){
			vect[i] = vector[i].multiply(multiplicand, rounder);
		}
		return new BDVector(vect);
	}
	
	public String toString(){
		String result = "[";
		for(int i = 0; i < size; i++){
			result += vector[i].toString() + " ";
		}
		result += "]";
		return result;
	}
	public String toString(BigDecimal epsilon, MathContext rounder){
		String result = "[";
		for(int i = 0; i < size; i++){
			if(vector[i].abs().compareTo(epsilon) < 0)
				result += "0.000 ";
			else
				result += vector[i].round(rounder).toPlainString() + ' ';
		}
		result += "]";
		return result;
	}
	
	public int size(){
		return this.size;
	}
	
	public BigDecimal[] getVector(){
		return this.vector;
	}
	
	public BigDecimal getElement(int i){
		return vector[i];
	}
	 
	public BDVector copy(){
		return new BDVector(this.getVector());
	}
	public void setElement(int i, BigDecimal value){
		vector[i] = new BigDecimal(value.toString());
	}
}
