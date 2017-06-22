import java.math.BigDecimal;
import java.util.*;

public class DPolynom {
	private ArrayList<Double> polynom;
	public DPolynom(ArrayList<Double> coefs){
		int index = coefs.size() - 1;
		while(coefs.get(index) == 0){
			index--;
		}
		polynom = new ArrayList<Double>();
		for(int i = 0; i <= index; i++){
			polynom.add(coefs.get(i));
		}
	}
	public String toString(){
		return polynom.toString();
	}
	public ArrayList<Double> takeDerivative(){
		ArrayList<Double> res = new ArrayList<Double>();
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
	public BigDecimal evaluate(BigDecimal point){
		BigDecimal t = new BigDecimal("1.0"), res = new BigDecimal("0.0");
		for(int i = 0; i < this.polynom.size(); i++){
			res = res.add(t.multiply(BigDecimal.valueOf(this.polynom.get(i))));
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
	public double get(int index){
		return this.polynom.get(index);
	}
	
	private void trimToSize(){
		int i = polynom.size() - 1;
		while(polynom.get(i) == 0){
			polynom.remove(i);
		}
	}
	
	public void add(DPolynom p){
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