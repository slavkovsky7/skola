package sk.mslavkovsky.zui2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Classifier {
	
	public Classifier(File f) throws FileNotFoundException{
		constructClassMaps();
		data = loadData(f);
	}
	
	public String printData(){
		String result = "";
		for (RealVector v : data){
			result += v.toString() + "\n";
		}
		return result;
	}
	
	private HashMap<String, Integer> stringToClass;
	private List<RealVector> data;
	private void constructClassMaps(){
		stringToClass = new HashMap<String, Integer>();
		//Prices
		stringToClass.put("low", 0);
		stringToClass.put("med", 1);
		stringToClass.put("high", 2);
		stringToClass.put("vhigh", 3);
		//Capacities
		stringToClass.put("2", 2);
		stringToClass.put("3", 3);
		stringToClass.put("4", 4);
		stringToClass.put("more", 5);
		stringToClass.put("5more", 5);
		//size
		stringToClass.put("small", 1);
		stringToClass.put("med", 2);
		stringToClass.put("big", 3);
		//label
		stringToClass.put("unacc", 1);
		stringToClass.put("acc", 2);
		stringToClass.put("good", 3);
		stringToClass.put("vgood", 3);
	}
	
	private String classToString(int cls){
		for ( Map.Entry<String, Integer> entry: stringToClass.entrySet() ){
			if ( entry.getValue().equals(cls) ){
				return entry.getKey();
			}
		}
		return null;
	}
	
	private int stringToClass(String str){
		return this.stringToClass.get(str);
	}
	
	private RealMatrix computeProps(int column){
		//TODO::
		return null;
	}
	
	public List<RealVector> loadData(File f) throws FileNotFoundException{
		List<RealVector> result = new ArrayList<RealVector>();
		Scanner scanner = new Scanner(f);
		while ( scanner.hasNext()){
			String line = scanner.nextLine();
			String[] strValues = line.split(",");
			RealVector v = new ArrayRealVector( strValues.length );
			for (int i = 0 ; i < strValues.length; i++){
				v.setEntry(i, stringToClass(strValues[i]));
			}
			result.add(v);
		}
		scanner.close();
		return result;
	}
	
	
	
	public static void main(String[] args){
		try {
			File f = new File("/home/martin/workspace/skola/ZUI2/projekt1/uloha4_java/src/main/resources/car_orig.data");
			Classifier bayesClassifier = new Classifier(f);
			System.out.println( bayesClassifier.printData() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
