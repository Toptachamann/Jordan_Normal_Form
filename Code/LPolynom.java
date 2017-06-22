import java.math.BigDecimal;
import java.util.*;

public class LPolynom {
	private ArrayList<Long> polynom;
	public LPolynom(ArrayList<Long> coefs){
		int index = coefs.size() - 1;
		while(coefs.get(index) == 0){
			index--;
		}
		polynom = new ArrayList<Long>();
		for(int i = 0; i <= index; i++){
			polynom.add(coefs.get(i));
		}
	}
	public String toString(){
		return polynom.toString();
	}
	public ArrayList<Long> takeDerivative(){
		ArrayList<Long> res = new ArrayList<Long>();
		for(int i = 1; i < this.polynom.size(); i++){
			res.add(this.polynom.get(i)*i);
		}
		return res;
	}
	

	
	public double evaluate(double point){
		double t = 1.0, res = 0;
		for(int i = 0; i < this.polynom.size(); i++){
			res += this.polynom.get(i)*t;
			t *= point;
		}
		return res;
	}
	public long evaluate(long point){
		long t = 1, res = 0;
		for(int i = 0; i < this.polynom.size(); i++){
			res += this.polynom.get(i)*t;
			t *= point;
		}
		return res;
	}
	public BigDecimal evaluate(BigDecimal point){
		BigDecimal t = new BigDecimal("1.0"), res = new BigDecimal("0.0"), 
				coef = new BigDecimal(0.0);
		for(int i = 0; i < this.polynom.size(); i++){
			res = res.add(t.multiply(coef.valueOf(this.polynom.get(i))));
			t = t.multiply(point);
		}
		return res.setScale(12, BigDecimal.ROUND_HALF_UP);
	}	
	
	public ArrayList<BigDecimal> findRoots(){
		ArrayList<BigDecimal> roots = new ArrayList<BigDecimal>();
		return roots;
	}
	
	public int size(){
		return polynom.size();
	}
	public void ensureCapacity(int n){
		this.polynom.ensureCapacity(n);
	}
	public long get(int index){
		return this.polynom.get(index);
	}
	
	private void trimToSize(){
		int i = polynom.size() - 1;
		while(polynom.get(i) == 0){
			polynom.remove(i);
		}
	}
	
	public void add(LPolynom p){
		if(polynom.size() >= p.size()){
			for(int i = 0; i < p.size(); i++){
				polynom.set(i, p.get(i) + polynom.get(i));
			}
		}else{
			polynom.ensureCapacity(p.size());
			for(int i = 0; i < polynom.size(); i++){
				polynom.set(i, p.get(i) + polynom.get(i));
			}
			for(int i = polynom.size(); i < p.size(); i++){
				polynom.add(p.get(i));
			}
		}
		trimToSize();
	}


}
