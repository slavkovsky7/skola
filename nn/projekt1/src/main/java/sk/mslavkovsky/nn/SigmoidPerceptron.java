package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class SigmoidPerceptron extends Perceptron{
	public double SIG_TRESHOLD = 0.5;
	public double EPSILON = 0.1;
	public LearningType LEARNING_TYPE = LearningType.Online;
	public TrainErrorType ERROR_TYPE = TrainErrorType.Classification;

	public SigmoidPerceptron(double[][] data, double alpha ){
		super(data, alpha);
	}
		
		
	private double classify(double x){
		return x > SIG_TRESHOLD ? 1 : 0;  
	}
	
				
	public static enum LearningType{Online,Batch}
	public static enum TrainErrorType{ Classification, Aproximation}
	
	//TODO:: urobit cez dedenie
	@Override
	public TrainStatistics train(File f) throws FileNotFoundException{

		boolean online = LEARNING_TYPE == LearningType.Online;
		boolean useClassError = ERROR_TYPE == TrainErrorType.Classification;
		PrintWriter pw = null; 
		if (f != null ){
			pw = new PrintWriter(f);
		}
		initW = initWeightVec( x[0].getDimension() + 1 );
		w = initW.copy();
		//w = new ArrayRealVector(new double[]{0.8792295433, 0.0507936459, 0.8375446602, 0.0726232539});
		//RealVector minW = w;
		double E = Double.MAX_VALUE;
		double minE = Double.MAX_VALUE;
		RealVector minW = w;
		int epoch = 0;
		while ( E > EPSILON && epoch < MAX_EPOCHS ){
			epoch++;
			
			E = 0;		
			//Toto by malo byt prerobene na batch Learning, resp to aspon skus
			RealVector[] xperm = PickData(x,ydata, online);
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
						if ( pw != null && epoch < MAX_POINTS && epoch % PLOT_FREQUENCE == 0 ) {
							pw.println( w.getEntry(0) + " " + w.getEntry(1) + " " + E );
						}
						
						if (VERBOSE){
							System.out.format("x = %s, expected = %5.3f, predicted c(%6.3f) = %.3f, w = %s\n", 
								xi.getSubVector(0, xi.getDimension() - 1).toString(),
								d, y, classify(y), w.toString());
									
									
						}
					}else{
						deltaSum =deltaSum.add( xi.mapMultiply(  (d-y)*sigmoidDerivative(net) ) ) ;
					}
				}
			}
			
			if (VERBOSE){
				System.out.println("Epoch  = " + epoch +", E = " + E  );
				System.out.println("------------------");
			}
			
			if (!online){
				w = w.add( deltaSum.mapMultiply(alpha) );
				if ( pw != null && epoch < MAX_POINTS && epoch % PLOT_FREQUENCE == 0 ) {
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
		if ( pw != null ) {
				pw.close();	
		}
		boolean converved = E <= EPSILON;
		return new TrainStatistics(converved, initW, w, minW.copy(), epoch, E,minE);
	}
	
	private double sigmoid(double x){
		return 1d / ( 1d + Math.exp( -x ) );
	}
	
	private double sigmoidDerivative(double x){
		double s = sigmoid(x) ;
		return s * ( 1 - s );
	}
	
	private double f(double x){
		return sigmoid(x);
		//return (x > 0) ? 1 : 0;
	}
				
	public double predict(double[] d){
		RealVector x = new ArrayRealVector(d);
		double s = w.dotProduct( x.append(-1) );
		return classify( f(s) );
	}
}
