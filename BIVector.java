import java.math.*;
import java.util.ArrayList;
public class BIVector {
	private BigInteger[] vector;
	private int size;
	
	public BIVector(int size){
		this.size = size;
		vector = new BigInteger[size];
		for(int i = 0; i < size; i++){
			vector[i] = BigInteger.ZERO;
		}
	}
	@SuppressWarnings("unchecked")
	public BIVector(ArrayList<BigInteger> list){
		this.size = list.size();
		for(int i = 0; i < size; i++){
			vector[i] = new BigInteger(list.get(i).toString());
		}
	}
	
	public BDVector toBDVector(){
		return new BDVector(vector);
	}
	public String toString(){
		String result = "[";
		for(int i = 0; i < size; i++){
			if(vector[i].compareTo(BigInteger.ZERO) == 0){
				result += "0 ";
			}else{
				result += vector[i].toString() + " ";
			}
		}
		result += "]";
		return result;
	}
	
	
	
}
