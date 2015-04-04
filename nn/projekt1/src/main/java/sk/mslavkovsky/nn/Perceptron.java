package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public abstract class Perceptron {
	public int MAX_EPOCHS = 100000;
	public int MAX_POINTS = 5000;
	public int PLOT_FREQUENCE = 1;
	public boolean VERBOSE = false;
	
	protected RealVector w;
	protected RealVector initW;
	protected RealVector[] x;
	protected RealVector ydata;
	protected double alpha;

	protected Perceptron(double[][] data, double alpha ){
		this.alpha = alpha;
		this.x = new RealVector[data.length];
		this.ydata = new ArrayRealVector(data.length);
		for ( int i = 0 ; i < data.length; i++){
			this.x[i] = new ArrayRealVector(data[i], 0, data[i].length - 1 );
			this.ydata.setEntry(i, data[i][ data[i].length - 1 ]);
		}
	}	
	
	public int getInputDataDimension(){
		return w.getDimension() - 1;
	}
			
	public static double[][] load(File f) throws IOException{
		Scanner scanner = new Scanner(f);
		ArrayList<double[]> lines = new ArrayList<double[]>();
		while (scanner.hasNext()){
			lines.add(new double[]{ scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble()});
		}
		scanner.close();
		
		double[][] result = new double[lines.size()][];
		for ( int i = 0 ; i < lines.size(); i++ ){
			result[i] = lines.get(i);
		}
		return result;
	}

	public void generatePlotTrainCode(File script, File datFile) throws IOException, InterruptedException{
		PrintWriter pw = new PrintWriter(script);
		pw.println("set xlabel \"w0 axis\" ");
		pw.println("set ylabel \"w1 axis\" ");
		pw.println("set zlabel \"Error axis\" "); 
		pw.println("splot \"" + datFile + "\" with lines");
		pw.close();;
	}

	private String getPlaneFormula(RealVector v, boolean threeDim){
		if (threeDim){
			double a =   -v.getEntry(0)/v.getEntry(2);;
			double b =   -v.getEntry(1)/v.getEntry(2);;
			double d =  v.getEntry(3)/v.getEntry(2);
			return a+"*x + "+b+"*y + "+(d); 	
		}else{
			double a = -v.getEntry(0)/v.getEntry(1);
			double b = v.getEntry(2)/ v.getEntry(1);
			return a+"*x + "+ b; 
		}
	}

	public void generatePlotCode(File f, File negFile, File posFile) throws FileNotFoundException{
		
				
		PrintWriter pw = new PrintWriter(f);
		pw.println("set xlabel \"X1 axis\" ");
		pw.println("set ylabel \"X2 axis\" ");
		pw.println("set zlabel \"X3 axis\" "); 
		
		pw.println("set title \"3D surface from a function\"");

		pw.println("set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 1"); 
		pw.println("set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1");  
		
		boolean threeDim = w.getDimension() -  1 >= 3;
		String plot = threeDim ? "splot" : "plot";
		pw.println( plot + " "
				+"\""+negFile+"\" with points ls 1, "
				+"\""+posFile+"\" with points ls 2 , "
				+getPlaneFormula(w, threeDim)+" with lines ls 4, "
				+getPlaneFormula(initW, threeDim)+" with lines ls 7"
		) ; 

		/*planeFo
		if (threeDim){
			double a =   -w.getEntry(0)/w.getEntry(2);;
			double b =   -w.getEntry(1)/w.getEntry(2);;
			double d =  w.getEntry(3)/w.getEntry(2);
			String planeFormula = a+"*x + "+b+"*y + "+(d); 	

		}else{
			double a = -w.getEntry(0)/w.getEntry(1);
			double b = w.getEntry(2)/ w.getEntry(1);
			String planeFormula = a+"*x + "+ b; 
			pw.println("plot \""+negFile+"\" with points ls 1, \""+posFile+"\" with points ls 2 , "+planeFormula+" with lines ls 4");	
		}*/
		
		pw.close();

		PrintWriter pwNeg = new PrintWriter( negFile );
		PrintWriter pwPos = new PrintWriter( posFile );
		
		for (int i = 0 ; i < x.length; i++){
			String str = "";
			for (int j = 0 ; j < x[i].getDimension(); j++){
				if ( j > 0) str += " ";
				str += x[i].getEntry(j); 
			}
			if (ydata.getEntry(i) > 0){
				pwPos.println(str);
			}else{
				pwNeg.println(str);
			}
		}
		
		pwNeg.close();
		pwPos.close();
	}

	protected static RealVector initWeightVec(int size){
		Random r = new Random();
		double[] vec = new double[size];
		for (int i = 0 ; i < size; i++){
			vec[i] = r.nextDouble();
		}
		return new ArrayRealVector(vec);
	}
	
	protected static RealVector[] PickData(RealVector[] in, RealVector y, boolean random){
		ArrayList<RealVector> l = new ArrayList<RealVector>();
		for (int i = 0; i < in.length; i++){
			l.add( in[i].append( y.getEntry(i)) );
		}
		if (random){
			Collections.shuffle(l);
		}
		RealVector[] result = new RealVector[in.length];
		for (int i = 0; i < l.size(); i++){
			result[i] = l.get(i);
		}
		return result;
	}
	
	public abstract double predict(double[] d);
	
	
	public abstract TrainStatistics train(File f) throws FileNotFoundException;
	
}
