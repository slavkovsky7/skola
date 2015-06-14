package sk.mslavkovsky.zui2;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Hmm {
	private RealMatrix E;
	private RealMatrix X;
	private RealVector startP;
	
	public Hmm(double[] start, double[][] X, double[][] E){
		this.E = new Array2DRowRealMatrix(E);
		this.X = new Array2DRowRealMatrix(X);
		this.startP = new ArrayRealVector(start);
	}
	
	@SuppressWarnings("deprecation")
	public RealVector[] filter(int[] obs){
		RealVector[] F = new RealVector[obs.length];
		RealVector filtration = startP.copy();
		for (int t = 0; t < obs.length; t++){
			RealVector prediction = this.X.operate(filtration);
			filtration = prediction.ebeMultiply( this.E.getColumnVector( obs[t] ) );
			filtration = filtration.mapDivide( Utils.sum(filtration) );
			F[t] = filtration.copy();
		}
		return F;
	}
	
	public RealVector predictOneStep(int[] obs){
		RealVector[] filtered = filter(obs);
		return this.X.operate(filtered[filtered.length-1]);
	}
	
	@SuppressWarnings("deprecation")
	private RealVector computeRecursive(int[] obs, int k){
		if ( k >= obs.length - 1){
			return new ArrayRealVector(X.getColumnDimension(), 1);
		}else{
			RealVector result = this.X.operate( E.getColumnVector(obs[k]));
			RealVector recurs = computeRecursive(obs, k+1);
			return result.ebeMultiply(recurs);
		}
	}
	
	@SuppressWarnings("deprecation")
	public RealVector smooth(int[] obs, int k){
		if ( k >= obs.length - 1 ){
			throw new RuntimeException(" invalid argument k");
		}
		
		RealVector recursive = computeRecursive(obs, k);
		RealVector[] F = filter(obs);
		RealVector result = recursive.ebeMultiply( F[k]);
		return result.mapDivide( Utils.sum(result) );
	}
	
	public static class ViterbiResult{
		public RealVector[] probs;
		public int[] states;
		
		public ViterbiResult(int[] states, RealVector[] probs){
			this.states = states;
			this.probs = probs;
		}
	}
	
	public ViterbiResult viterbi(int obs[]){
		RealVector[] V1 = new ArrayRealVector[ obs.length ];
		RealVector[] V2 = new ArrayRealVector[ obs.length ];
		
		int n_states = this.X.getColumnDimension();	
		V1[0] = new ArrayRealVector(n_states);
		V2[0] = new ArrayRealVector(n_states);
		for (int j = 0; j < n_states; j++){
			V1[0].setEntry(j, this.startP.getEntry(j)*this.E.getEntry(j, obs[0]) );
			V2[0].setEntry(j, j);
		}
		
		for (int i = 1 ; i < obs.length; i++){
			V1[i] = new ArrayRealVector(n_states);
			V2[i] = new ArrayRealVector(n_states);
			for( int j = 0; j < n_states; j++){
				for ( int k = 0; k < n_states; k++ ){
					double val = V1[i-1].getEntry(k)* this.X.getEntry(k, j)* this.E.getEntry(j, obs[i]);
					if (val > V1[i].getEntry(j)){
						V1[i].setEntry(j, val);
						V2[i].setEntry(j, k);
					}
				}
			}
		}
		
		int[] Y = new int[obs.length];
		int[] Z = new int[obs.length];
		
		
		int index  = Utils.maxIndex( V1[ V1.length - 1] ); 
		//double val = Utils.maxVal( V1[ V1.length - 1] );
		
		Z[Z.length-1] = index;
		Y[Z.length-1] = index;
		
		for (int i = obs.length - 1 ; i >= 1; i--){
			Z[i-1] = (int)V2[i].getEntry(Z[i]);
			Y[i-1] = Z[i-1];
		}
		return new ViterbiResult(Y,V1);
	} 
	
}
