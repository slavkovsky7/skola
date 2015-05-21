package sk.mslavkovsky.nn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class PCA {
	private List<RealVector> data;
	private int reductionDim;
	private RealMatrix weights;

	public double alpha = 0.0001;
	public int MAX_EPOCHS= 200;
	public boolean VERBOSE = true;
	public int PRINT_FREQUENCE = 10;
	
	public PCA(List<RealVector> data, int reductDim ){
		this.data = data;
		this.reductionDim = reductDim;
	}
	
	public List<RealVector> getData(){return Utils.deepCopy(data);}
		
	private static RealMatrix initWeights(int rows, int columns){
		RealMatrix matrix = new Array2DRowRealMatrix(rows, columns);
		Random r = new Random();
		for (int i = 0; i < matrix.getRowDimension();i++){
			for (int j = 0; j < matrix.getColumnDimension(); j++){
				matrix.setEntry(i, j, r.nextDouble() );
			}
		}
		return matrix;
	}
	
	private static RealMatrix tril(final RealMatrix m , int index){
		RealMatrix result = m.copy();
		for (int i = 0; i < m.getRowDimension(); i++){
			for (int j = 0; j < m.getColumnDimension(); j++){
				if (j - index > i){
					result.setEntry(i, j,  0 );
				}
			}
		}
		return result;
	}
	
	public void train() throws Exception{
		List<RealVector> normalizedData = normalizeData(data);
		
		int dim = data.get(0).getDimension();
		RealMatrix W = initWeights( reductionDim, dim );
		RealMatrix L = tril(initWeights(reductionDim, reductionDim), -1 );
		
		//L2 normovanie vektorov vah
		for (int i = 0; i < W.getRowDimension(); i++){
			RealVector wi = W.getRowVector(i);
			wi = wi.mapDivide( wi.getNorm() );
			W.setRowVector( i , wi);
		}
		
		int ep = 0;
		while (ep < MAX_EPOCHS){		
			for (int t = 0; t < normalizedData.size(); t++){
				RealVector x = normalizedData.get(t);
				RealVector wx = W.operate(x);
				RealVector y = wx.add( L.operate(wx) );
				//System.out.println(y);
				for (int j = 0 ; j < x.getDimension(); j++){
					double tmp = x.getEntry(j) - W.getColumnVector(j).dotProduct(y);
					RealVector delta_wj = y.mapMultiply( tmp ).mapMultiply(alpha);
					W.setColumnVector(j,  W.getColumnVector(j).add(delta_wj) );
					
				}
				
				for (int i = 0 ; i < L.getRowDimension(); i++){
					for (int k = 0 ; k < L.getColumnDimension(); k++){
						double du_i_k = alpha*y.getEntry(i)*( y.getEntry(k) + y.getEntry(i)*L.getEntry(i, k) );
						L.setEntry(i, k, L.getEntry(i, k) - du_i_k);
					}
				}
				
				L = tril(L, -1);
			}
			ep++;
			if (VERBOSE && ep % PRINT_FREQUENCE == 0){
				System.out.println("ep = " + ep);
				System.out.println("w = " + W.getRowVector(0).getSubVector(0, Math.min( 10, W.getRowDimension() )) + ",...");
			}
			
			Double d = W.getEntry(0, 0) ;
			if ( d.equals( Double.NaN ) ){
				throw new Exception("Traning has diverged :( ...");
			}
		}
		weights = W.copy();
	}
	
	public List<RealVector> compress(List<RealVector> input){
		List<RealVector> normdata = input;
		List<RealVector> result = new ArrayList<RealVector>();
		for (RealVector x : normdata ){
			RealVector a = new ArrayRealVector(reductionDim);
			for (int i = 0; i < reductionDim; i++){
				a.setEntry(i, weights.getRowVector(i).dotProduct(x));
			}
			result.add(a);
		}
		return result;
	}
	
	public List<RealVector> reconstruct( List<RealVector> compressed){
		List<RealVector> result = new ArrayList<RealVector>();
		for ( RealVector a : compressed){
			RealVector x = weights.preMultiply(a);
			result.add(x);
		}
		return result;
	}
	
	
	private static RealVector mean(List<RealVector> data){
		RealVector average = new ArrayRealVector(data.get(0).getDimension());
		for (RealVector v : data ) {
			average = average.add(v);
		}
		average = average.mapMultiply( 1d/data.size());
		return average;
	}
	
	@SuppressWarnings("deprecation")
	public static RealVector variance(List<RealVector> data){
		RealVector result = new ArrayRealVector(data.get(0).getDimension());
		for (RealVector v : data){
			result = result.add(  v.ebeMultiply(v) );
		}
		
		result =  result.mapMultiply( 1d/data.size() );
		for (int i = 0; i < result.getDimension(); i++){
			result.setEntry( i, Math.sqrt(result.getEntry(i)));
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	private List<RealVector> normalizeData(List<RealVector> input ){
		List<RealVector> result = Utils.deepCopy(input);
		RealVector m = mean(result);
		for (int i = 0 ; i < result.size(); i++){
			result.set(i , result.get(i).subtract(m) );
		}
		
		RealVector var = variance(result);
		for (int i = 0 ; i < result.size(); i++){
			result.set(i , result.get(i).ebeDivide(var) );
		}
		return result;
	}
	
	//@SuppressWarnings("deprecation")
	public List<RealVector> getEigenVectorImages(){
		List<RealVector> result = new ArrayList<RealVector>();
		for ( int i = 0 ; i < weights.getRowDimension(); i++){
			RealVector eigenVec = weights.getRowVector(i).copy();
			double max = -1;
			double min = 10;
			for (int j = 0; j < eigenVec.getDimension();j++){
				max = Math.max(max, eigenVec.getEntry(j));
				min = Math.min(min, eigenVec.getEntry(j));
			}
			eigenVec = eigenVec.mapAdd(-min);
			max += -min;
			min = 0;
			
			for (int j = 0; j < eigenVec.getDimension();j++){
				 double entry = eigenVec.getEntry(j) / max;
				 eigenVec.setEntry( j , entry * 255 );
			 }
			 
			 result.add(eigenVec);
		}
		return result;
	}
	
	public RealVector getEigenValues(){
		List<RealVector> normalized = normalizeData(data);
		RealMatrix X = new Array2DRowRealMatrix(normalized.size(), normalized.get(0).getDimension());
		for (int i = 0 ; i < normalized.size(); i++){
			X.setRowVector(i, normalized.get(i));
		}

		RealMatrix cov = new Array2DRowRealMatrix(normalized.get(0).getDimension(), normalized.get(0).getDimension());
		for (int i = 0; i < normalized.size() ; i++){
			RealVector nv = normalized.get(i);
			cov = cov.add( nv.outerProduct(nv) );
		}
		cov = cov.scalarMultiply( 1d/normalized.size() );
		
		RealMatrix eigenValuesMat = weights.multiply(cov).multiply( weights.transpose());		
		RealVector eigenValuesMatDiag = new ArrayRealVector(eigenValuesMat.getColumnDimension());
		for (int i = 0; i < eigenValuesMat.getColumnDimension(); i++){
			eigenValuesMatDiag.setEntry(i, eigenValuesMat.getEntry(i, i));
		}
		
		//EigenDecomposition eigens = new EigenDecomposition(cov);
		//double[] eigenValues = eigens.getRealEigenvalues();
		//double[] eigenValues2 = new double[eigenValues.length];
		//for (int i = 0 ; i < eigenValues.length; i++){
		//	eigenValues2[i] = eigenValues[eigenValues.length-1-i]; 
		//}
		
		//System.out.println(eigenValuesMatDiag);
		//System.out.println(Arrays.toString(eigenValues));
		//System.out.println(Arrays.toString(eigenValues2));
		return eigenValuesMatDiag;
	}
		
}
