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

public class Perceptron {

		private RealVector w;
		private RealVector[] x;
		private RealVector ydata;
		//private double theta;
		private double alpha;
		
		public Perceptron(double[][] data, double alpha ){
			this.alpha = alpha;
			this.x = new RealVector[data.length];
			this.ydata = new ArrayRealVector(data.length);
			for ( int i = 0 ; i < data.length; i++){
				this.x[i] = new ArrayRealVector(data[i], 0, data[i].length - 1 );
				this.ydata.setEntry(i, data[i][ data[i].length - 1 ]);
			}
		}
		
		public final int MAX_EPOCHS = 100000;
		public final int MAX_POINTS = 5000;
		public final int PLOT_FREQUENCE = 1;
		public final double SIG_TRESHOLD = 0.5;
		public final double EPSILON = 0.1;
		
		public double classify(double x){
			return x > SIG_TRESHOLD ? 1 : 0;  
		}
		
		public int getInputDataDimension(){
			return w.getDimension() - 1;
		}
		
		public static class TrainStatistics{
			
			public final RealVector InitWeights;
			public final RealVector Weights;
			public final RealVector MinWeights;
			public final int Epoch;
			public final double Error;
			public final double MinError;
			public final boolean Converged;
			
		    TrainStatistics(Boolean converged, RealVector initw, RealVector w, RealVector minW, int epoch, double E, double minE ){
				this.InitWeights = initw.copy();
				this.MinWeights = minW.copy();
				this.Weights = w.copy();
				this.Epoch = epoch;
				this.Error = E;
				this.MinError = minE;
				this.Converged = converged;
			}
			
			@Override
			public String toString(){
				StringBuilder sb = new StringBuilder();
				sb.append("InitWeights = " + InitWeights +"\n");
				sb.append("Weights     = " + Weights +"\n");
				sb.append("MinWeights  = " + MinWeights +"\n");
				sb.append("Error       = " + Error +"\n");
				sb.append("MinError    = "  +MinError +"\n" );	
				sb.append("Epoch 	   = " + Epoch +"\n" );
				sb.append("Converged   = " + Converged );
				return sb.toString();
			}

		}
		
		//Batch learning
		public TrainStatistics train(File f, boolean online, boolean useClassError) throws FileNotFoundException{
			
			PrintWriter pw = new PrintWriter(f);
			RealVector initVector = initWeightVec( x[0].getDimension() + 1 );
			w = initVector.copy();
			//w = new ArrayRealVector(new double[]{0.8792295433, 0.0507936459, 0.8375446602, 0.0726232539});
			//RealVector minW = w;
			double E = Double.MAX_VALUE;
			double minE = Double.MAX_VALUE;
			RealVector minW = w;;
			int epoch = 0;
			while ( E > EPSILON && epoch < MAX_EPOCHS ){
				epoch++;
				
				E = 0;		
				//Toto by malo byt prerobene na batch Learning, resp to aspon skus
				RealVector[] xperm = PickData(x,ydata, false);
				RealVector deltaSum = new ArrayRealVector(w.getDimension());
				for (int p = 0 ; p < xperm.length; p++ ){
					RealVector xi = xperm[p];
					double d= xi.getEntry(xi.getDimension() - 1);
					xi.setEntry(xi.getDimension() - 1, -1);

					double net = xi.dotProduct(w);
					double y = f(net);
					double e = (1d/2d)*Math.pow(d- ( useClassError ? classify(y) : y) , 2d );
					E += e;
					
					if (e > 0){
						//Online Learning
						if (online){
							w = w.add( xi.mapMultiply(  alpha*(d-y)*sigmoidDerivative(net) )  );		
							if ( epoch < MAX_POINTS && epoch % PLOT_FREQUENCE == 0 ) {
								pw.println( w.getEntry(0) + " " + w.getEntry(1) + " " + E );
							}
						}else{
							deltaSum =deltaSum.add( xi.mapMultiply(  (d-y)*sigmoidDerivative(net) ) ) ;
						}
					}
				}
				//System.out.println("------------------");
				//System.out.println("Epoch  = " + epoch  );
				//System.out.println("E = " + E);
				
				if (!online){
					w = w.add( deltaSum.mapMultiply(alpha) );
					if ( epoch < MAX_POINTS && epoch % PLOT_FREQUENCE == 0 ) {
						pw.println( w.getEntry(0) + " " + w.getEntry(1) + " " + E );
					}
				}
							
				if (epoch > 2 && E < minE){
					minE = E;
					minW = w.copy();
				}
			}
			
			
			//w = minW;
			//w = w.getSubVector(0, w.getDimension() - 1 );
			pw.close();	
			
			boolean converved = E <= EPSILON;
			return new TrainStatistics(converved, initVector, w, minW.copy(), epoch, E,minE);
		}
		
		private double sigmoid(double x){
			return 1d / ( 1d + Math.exp( -x ) );
		}
		
		private double sigmoidDerivative(double x){
			double s = sigmoid(x) ;
			return s / ( 1 - s );
		}
		
		private double f(double x){
			return sigmoid(x);
			//return (x > 0) ? 1 : 0;
		}
		
		private static RealVector initWeightVec(int size){
			Random r = new Random();
			double[] vec = new double[size];
			for (int i = 0 ; i < size; i++){
				vec[i] = r.nextDouble();
			}
			return new ArrayRealVector(vec);
		}
		
		private static RealVector[] PickData(RealVector[] in, RealVector y, boolean random){
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
				
		public void generatePlotCode(File f, File negFile, File posFile, boolean threeDim) throws FileNotFoundException{
			PrintWriter pw = new PrintWriter(f);
			pw.println("set xlabel \"X1 axis\" ");
			pw.println("set ylabel \"X2 axis\" ");
			pw.println("set zlabel \"X3 axis\" "); 
			
			pw.println("set title \"3D surface from a function\"");

			pw.println("set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 1"); 
			pw.println("set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1");  
			
			
			if (threeDim){
				double a =   -w.getEntry(0)/w.getEntry(2);;
				double b =   -w.getEntry(1)/w.getEntry(2);;
				double d =  w.getEntry(3)/w.getEntry(2);
				String planeFormula = a+"*x + "+b+"*y + "+(d); 	
				pw.println("splot \""+negFile+"\" with points ls 1, \""+posFile+"\" with points ls 2 , "+planeFormula+" with lines ls 4");
			}else{
				double a = -w.getEntry(0)/w.getEntry(1);
				double b = w.getEntry(2)/ w.getEntry(1);
				String planeFormula = a+"*x + "+ b; 
				pw.println("plot \""+negFile+"\" with points ls 1, \""+posFile+"\" with points ls 2 , "+planeFormula+" with lines ls 4");	
			}
			
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
		
		public void generatePlotTrainCode(File f) throws IOException, InterruptedException{
			PrintWriter pw = new PrintWriter(f);
			pw.println("splot \"" + f + "\" with lines");
			pw.close();;
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
		
		public double predict(double[] d){
			RealVector x = new ArrayRealVector(d);
			double s = w.dotProduct( x.append(-1) );
			return classify( f(s) );
		}
}
