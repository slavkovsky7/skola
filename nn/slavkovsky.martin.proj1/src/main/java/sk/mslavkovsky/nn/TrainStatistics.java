package sk.mslavkovsky.nn;

import org.apache.commons.math3.linear.RealVector;


public class TrainStatistics{
	
	public RealVector InitWeights;
	public RealVector Weights;
	public RealVector MinWeights;
	public int Epoch;
	public double Error;
	public double MinError;
	public boolean Converged;
	
    TrainStatistics(Boolean converged, RealVector initw, RealVector w, RealVector minW, int epoch, double E, double minE ){
		this.InitWeights = initw.copy();
		this.MinWeights = minW.copy();
		this.Weights = w.copy();
		this.Epoch = epoch;
		this.Error = E;
		this.MinError = minE;
		this.Converged = converged;
	}
    
    public TrainStatistics withTrainConverged(boolean converged){ this.Converged = converged; return this;}
    public TrainStatistics withWeights(RealVector w){ this.Weights = w; return this;}
    public TrainStatistics withInitWeights(RealVector w){ this.InitWeights= w; return this;}
    public TrainStatistics withMinWeights(RealVector w){ this.MinWeights= w; return this;}
    public TrainStatistics withEpoch(int epoch){ this.Epoch= epoch; return this;}
    public TrainStatistics withError(double error){ this.Error=error; return this;}
    public TrainStatistics withMinError(double error){ this.MinError=error; return this;}
	
    
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("InitWeights = " + InitWeights +"\n");
		sb.append("Weights     = " + Weights +"\n");
		sb.append("MinWeights  = " + MinWeights +"\n");
		sb.append("Error       = " + Error +"\n");
		sb.append("MinError    = "  +MinError +"\n" );	
		sb.append("Epoch       = " + Epoch +"\n" );
		sb.append("Converged   = " + Converged );
		return sb.toString();
	}

}
