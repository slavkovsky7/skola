package sk.mslavkovsky.zui2;

import java.awt.AlphaComposite;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.math3.util.Precision;

public class Uloha4 {
	
	public double[] data;
	
	public Uloha4(double[] data){
		this.data = data;
	}
	
	public Uloha4(File f) throws FileNotFoundException{
		this.data = load(f);
	}
	
	public double[] load(File f) throws FileNotFoundException{
		Scanner scanner = new Scanner(f);
		List<Double> resultList = new ArrayList<Double>();
		while (scanner.hasNextDouble()){
			resultList.add(scanner.nextDouble());
		}
		scanner.close();
		return Utils.toArray(resultList);
	}
	
	
	void generateGnuDataFile(File outFile, double d[] ) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(outFile);
		for (int x = 0; x < d.length; x++){
			pw.println( x+" " + d[x] );
		}
		pw.close();
	}
	
	public void plotEverything(String outPutStr, Map<String, double[]> datas) throws IOException, InterruptedException{
		
		for( Map.Entry<String, double[]> entry : datas.entrySet() ){
			File f = new File(Settings.GENERATED_DIR, "generated-"+entry.getKey()+".dat"); 
			generateGnuDataFile(f,entry.getValue());
		}
		
	
		PrintWriter pw = new PrintWriter(Settings.SCRIPT_FILE);
		//File output = new File(Settings.OUT_PLOT_DIR, outPutStr);
		//pw.println( "set terminal png size 1024,768");
		//pw.println( "set output '"+output.getAbsoluteFile()+"'");
		pw.println("set xlabel \"X axis\" ");
		pw.println("set ylabel \"Y axis\" ");
		
		String plotCmd = "plot ";
		int ls = 1;
		for( Map.Entry<String, double[]> entry : datas.entrySet() ){
			File f = new File(Settings.GENERATED_DIR, "generated-"+entry.getKey()+".dat"); 
			generateGnuDataFile(f,entry.getValue());
			
			if (ls > 1) {
				plotCmd += ", ";
			}
			plotCmd += "\"" + f + "\" with lines ls " + ls;
			ls++;
		}

				
				
		pw.println(plotCmd);
		pw.close();
		Utils.plot(Settings.SCRIPT_FILE);
	
	}

	public double[] movingAverage(double[] d, int n){
		double[] result = new double[d.length];
		for (int i = 0 ; i < d.length; i++){
			if (i < n ){
				result[i] = d[i];
			}else{
				double sum = 0;
				for (int j = i - n ; j < i ; j++){
					sum += d[j]; 
				}
				result[i] = (1d/n)*sum;
			}
		}
		return result;
	}
	
	public double[] singleExponentialSmoothing(double[] d, double alpha, int predictCount ){
		double[] result = new double[d.length + predictCount];
		result[0] = d[0];
		for (int i = 1; i < d.length; i++){
			result[i] = alpha*d[i] + (1 - alpha)*result[i-1];
		}
		
		for (int i = d.length; i < d.length + predictCount ;i++){
			result[i] = alpha*d[d.length - 1] + (1-alpha)*result[i-1];
		}
		return result;
	}
	
	//TODO::Vratit S aj ForeCast
	public double[][] doubleExponentialSmoothing(double[] d , double alpha, double beta, int predictCount){
		double[] b = new double[d.length];
		double[] s = new double[d.length];
		
		s[0] = d[0];
		//s[1] = d[1];   
		
		//b[0] = d[0];
		b[0] = (d[1] - d[0] + d[2] - d[1] + d[3]-d[2] ) / 3;
		for (int i = 1; i < d.length; i++){
			s[i] = alpha*d[i] + (1 - alpha)*( s[i - 1] + b[i-1] );
			b[i] = beta*( s[i] - s[i-1] ) + (1 - beta)*b[i-1];
		}
		

		/*for (int i = d.length; i < d.length + predictCount ;i++){
			int m = i + 1 - d.length;
			s[i] = s[d.length - 1] + m*b[ d.length - 1];
		}*/

		//FORECAST
		double f[] = new double[d.length + predictCount];
		f[0] = d[0];
		for (int t = 1; t < f.length; t++){
			int tt = Math.min(d.length-1, t);
			int m = t + 1 - tt;
			f[t] = s[tt] + m*b[tt];
		}

		double[][] result = new double[2][];
		result[0]  = f;
		result[1]  = s;
		return result;
	}
	
	//TODO::Vratit S aj ForeCast
	public double[][] trippleExponentialSmoothing(double[] d , int period,  double alpha, double beta, double gama, int predictCount){
		//TODO::
		double[] b = new double[d.length];
		for (int i = 0; i < period; i++ ){
			b[0] += ( d[period+i] - d[i] ) / (double)period;
		}
		b[0] /= period;
		
		
		int seasons = d.length / period;
		double[] A = new double[ seasons ];
		for (int j = 0 ; j < seasons; j++){
			for (int i = 0 ; i < period; i++){
				A[j] += d[period*j + i]; 
			}
			A[j] /= period;
		}
		
		double[] it = new double[d.length];
		for (int i = 0; i < period; i++){
			for (int j = 0 ; j < seasons ; j++){
				it[i] += d[j*period + i] / A[j];
			}
			it[i] /= seasons;
		}
		
		double s[] = new double[d.length];
		s[0] = d[0];
		for (int t = 1; t < d.length; t++){
			boolean overPeriod = t - period >= 0;
			double ctl_1 =  overPeriod ? it[t-period] : 1; 
			s[t] = (alpha*d[t])/ctl_1 + (1-alpha)*(s[t-1] + b[t-1]);
			b[t] = beta*( s[t] - s[t-1] ) + (1-beta)*b[t-1];
			if (overPeriod){
				it[t] = gama*(d[t] / s[t]) + (1-gama)*it[t-1];
			}
		}
		
		
		
		/*for (int t = d.length; t < f.length; t++){
			int m = t + 1 - d.length;
			int index = (d.length - period + m - 1 ) % period;
			s[t] = ( s[d.length - 1] + m*b[ d.length - 1] ) *  it[ index ] ;
		}*/
		
		//Forecast
		double f[] = new double[d.length + predictCount];
		for (int t = 0; t < f.length; t++){
			if (t >= period){
				int tt = Math.min(d.length-1, t);
				int m = t + 1 - tt;
				int index = (tt - period + m - 1 ) % period;
				f[t] = ( s[tt] + m*b[tt] ) *  it[ index ] ;
			}else{
				f[t] = d[t];
			}
		}
	
		double[][] result = new double[2][];
		result[0]  = f;
		result[1]  = s;
		return result;
	}
	
	public void findBestModel(double[] y, boolean tripple){
		
		int PERIOD = 4;
		int SPLIT_FACTOR = 4;
		
		
		
		double bestAlpha = 0;
		double bestBeta = 0;
		double bestGama = 0;
		
		double minMSE = Double.MAX_VALUE; 
		double delta = 0.01;
		
		int steps = (int) ( 1 / delta );
		for (int i = 0; i < steps; i++){
			for (int j = 0; j < steps; j++){
				for (int k = 0; k < steps; k++){
					double alpha = ( tripple ? i : j ) * delta; 
					double beta  = ( tripple ? j : k ) * delta;
					double gama  = ( tripple ? k : 0 ) * delta;
					
					double[][] splitted = Utils.splitArray( y, y.length - SPLIT_FACTOR );
					
					double[][] dexp = null;
					if (tripple){
						dexp = trippleExponentialSmoothing(splitted[0], PERIOD, alpha, beta, gama, SPLIT_FACTOR);//(y, alpha, beta, 0);
					}else{
						dexp = doubleExponentialSmoothing(splitted[0], alpha, beta, SPLIT_FACTOR);
					}
					
					double[][] dexp_splitted = Utils.splitArray( dexp[0], dexp[0].length - SPLIT_FACTOR );
					
					double mse = meanSquareError(splitted[1], dexp_splitted[1] ) ;
					if ( mse < minMSE  ){
						minMSE = mse;
						bestAlpha = alpha;
						bestBeta = beta;
						bestGama = gama;
					}
					
					//System.out.println("Model : alpha = " + alpha + ", beta = " + beta + ", MSE = " + mse);
				}
			}
			
			if (!tripple){
				break;
			}
		}
		String str = "Best Model : alpha = " + bestAlpha + ", beta = " + bestBeta;
		if (tripple){
			str += ", gama = "+  bestGama; 
		}
		str += ", MSE = " + minMSE;
		System.out.println(str);
		
	}
	
	public static double meanSquareError(double[] y, double f[] ){
		double result = 0;
		for (int i = 0 ; i < y.length; i++){
			result += Math.pow( f[i] - y[i] , 2);
		}
		result /= y.length;
		return result;
	}
	
	
	public static double[] findBestModel(){
		//TODO::
		return null;
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException{
		try {
			Uloha4 uloha = new Uloha4(new File("data3.txt"));
			HashMap<String, double[]> dataMap = new HashMap<String, double[]>();
		
			
			//double[] ma = uloha.movingAverage(uloha.data, 50);
			//double[] exp1 = uloha.singleExponentialSmoothing(uloha.data, 0.3, 10);
			//double[][] exp2 = uloha.doubleExponentialSmoothing(uloha.data, 0.1, 0.18, 50);
			double[][] exp3 = uloha.trippleExponentialSmoothing(uloha.data, 4, 0.16, 0.31, 0.76, 50 );
			uloha.findBestModel(uloha.data, true);
			
			dataMap.put("original", uloha.data);
			//dataMap.put("moving avg", ma );
			//dataMap.put("e2_smoothed", exp2[0] );
			//dataMap.put("e2_forecast", exp2[1] );
			dataMap.put("e3_forecast", exp3[0] );
			dataMap.put("e3_smoothed", exp3[1] );
			
			
			uloha.plotEverything("output.png", dataMap );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
