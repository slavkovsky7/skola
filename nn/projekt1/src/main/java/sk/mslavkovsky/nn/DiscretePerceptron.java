package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class DiscretePerceptron extends Perceptron{
	
	public DiscretePerceptron (double[][] data, double alpha ){
		super(data, alpha);
	}

	@Override
	public double predict(double[] d) {
		return f( w.dotProduct(new ArrayRealVector(d).append(-1) ));
	}

	private double f(double x){
		return (x > 0 ? 1 : 0 );
	}
	
	@Override
	public TrainStatistics train(File f) throws FileNotFoundException {
		PrintWriter pw = null; 
		if (f != null ){
			pw = new PrintWriter(f);
		}	
		
		
		w = initWeightVec(x[0].getDimension() + 1 );
		RealVector initVector = w.copy();
		int epoch = 0;
		double E = 1;
		while (E > 0 && epoch < MAX_EPOCHS){
			E = 0;
			RealVector[] xperm = PickData(x,ydata, true);
			for (int p = 0 ; p < xperm.length; p++ ){
				RealVector xi = xperm[p];
				double d= xi.getEntry(xi.getDimension() - 1);
				xi.setEntry(xi.getDimension() - 1, -1);
				double net = xi.dotProduct(w);
				double y = f(net);
				double e = (1d/2d)*Math.pow( d- y , 2d );
				E += e;
				
				if (e > 0){
					w = w.add( xi.mapMultiply(  alpha*(d-y) )  );		
					if ( pw != null && epoch < MAX_POINTS && epoch % PLOT_FREQUENCE == 0 ) {
						pw.println( w.getEntry(0) + " " + w.getEntry(1) + " " + E );
					}
				}
			}
			epoch++;
		}
		
		if (pw != null){
			pw.close();
		}
		return new TrainStatistics(epoch < MAX_EPOCHS, w, initVector, w, epoch, E, -1);
	}
	
}
