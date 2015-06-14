package sk.mslavkovsky.zui2;

import org.apache.commons.math3.linear.RealVector;

public class Utils {
	public static double sum(RealVector v){
		double result = 0;
		for (int i = 0; i < v.getDimension(); i++){
			result += v.getEntry(i);
		}
		return result;
	}
	
	public static String toString(RealVector[] vecs){
		String result = "";
		for (int i = 0; i < vecs.length; i++){
			if (i > 0){
				result += "\n";
			}
			result += vecs[i];
		}
		return result;
	}
	
	public static double maxVal( RealVector v){
		return v.getEntry( maxIndex(v) );
	}
	
	public static int maxIndex(RealVector v){
		int result = -1;
		for(int i = 0; i < v.getDimension(); i++){
			if (result == -1 || v.getEntry(i) > v.getEntry(result) ){
				result = i;
			}
		}
		return result;
	}
	
}
