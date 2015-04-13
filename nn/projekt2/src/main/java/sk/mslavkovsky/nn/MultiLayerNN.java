package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class MultiLayerNN {
	
	
	public static class InputDataContainer
	{
		public List<RealVector> x;
		public List<RealVector> y;
		
		public InputDataContainer(){
			this.x = new ArrayList<RealVector>();
			this.y = new ArrayList<RealVector>();
		}
		
		public InputDataContainer(List<RealVector> x, List<RealVector> y){
			this.x = x;
			this.y = y;
		}
		
		public InputDataContainer copy(){
			InputDataContainer result = new InputDataContainer();
			result.x = Utils.deepCopy(this.x);
			result.y = Utils.deepCopy(this.y);
			return result;
		}
	}
	/*************************************/
	
	private List<List<RealMatrix>> w; 
	//private List<RealVector> xdata = new ArrayList<RealVector>();
	//private List<RealVector> ydata = new ArrayList<RealVector>();
	private int dimension;
	private InputDataContainer data;
	
	private int[] layers;
	public int MAX_EPOCH_COUNT = 5000;
	public double alpha = 0.1;
	public double TRESHOLD = 0.5d;
	public int CHECK_FREQUENCY = 100;
	public boolean VERBOSE = true;
	public double EPSILON = 0.0001;
	
	private HashMap<String, RealVector> classToVector = new HashMap<String, RealVector>();
	private HashMap<RealVector,String > vectorToClass = new HashMap<RealVector, String>();
	
	public int[] getLayers(){
		return layers;
	}
	
	public MultiLayerNN(File f, int inputSize, int[] hiddenLayers) throws FileNotFoundException{
		data = load(f, inputSize);
		dimension = inputSize;
		this.layers = new int[hiddenLayers.length + 2];
		this.layers[0] = data.x.get(0).getDimension();
		for (int i = 0 ; i < hiddenLayers.length; i++){
			layers[i+1] = hiddenLayers[i];
		}
		this.layers[ layers.length - 1 ] = data.y.get(0).getDimension();
	}
	
	public void printData(List<RealVector> xxdata, List<RealVector> yydata){
		for (int i = 0; i < xxdata.size(); i++){
			System.out.println( xxdata.get(i).toString() + " -> " + yydata.get(i));
		}
	}

	
	public InputDataContainer load(File f, int inputSize) throws FileNotFoundException{
		Scanner scanner = new Scanner(f);
		ArrayList<double[]> xPremim = new ArrayList<double[]>();
		ArrayList<String>	yPrelim = new ArrayList<String>();
		SortedSet<String> classNames = new TreeSet<String>();
		while (scanner.hasNext()){
			double[] x = new double[inputSize];
			for (int i = 0 ; i < inputSize; i++){
				x[i] = scanner.nextDouble();
			}
			xPremim.add(x);
			String y = scanner.next(); 
			yPrelim.add( y );
			classNames.add(y);
			//scanner.nextLine();
		}	
		scanner.close();
		
		InputDataContainer result = new InputDataContainer();
		
		
		int i = 0;
		for ( String className : classNames){
			RealVector v = new ArrayRealVector(classNames.size());
			v.setEntry(i, 1);
			classToVector.put(className, v); 
			vectorToClass.put(v, className);
			i++;
		}
		
		for ( i = 0; i < xPremim.size(); i++){
			result.x.add( new ArrayRealVector( xPremim.get(i) ) );
			result.y.add( classToVector.get(yPrelim.get(i)).copy() );
		}
		
		return result;
	}
		
	private static void shufftle(List<RealVector> xxdata, List<RealVector> yydata){
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0 ; i < xxdata.size(); i++){
			indexes.add(i);
		}
		Collections.shuffle(indexes);
		
		List<RealVector> xxdataCopy = new ArrayList<RealVector>(xxdata);
		List<RealVector> yydataCopy = new ArrayList<RealVector>(yydata);
		xxdata.clear();
		yydata.clear();
		for (int i = 0; i < indexes.size(); i++ ){
			int index = indexes.get(i)  ;
			xxdata.add(xxdataCopy.get(index));
			yydata.add(yydataCopy.get(index));
		}
	}
	
	private static List<RealMatrix> initWeights(int[] layers){
		List<RealMatrix> result = new ArrayList<RealMatrix>();
		Random r = new Random();
		for (int i = 1 ; i < layers.length; i++ ){
			RealMatrix m = new Array2DRowRealMatrix(layers[i], layers[i-1] + 1);
			for (int x = 0; x < m.getRowDimension(); x++){
				for (int y = 0; y < m.getColumnDimension(); y++){
					m.setEntry(x, y, r.nextDouble() );
				}
			}
			result.add( m );
		}
		return result;
	}
	
	private static double sigmoid(double x){
		return 1d / ( 1d + Math.exp( -x ) );
	}
		
	private static RealVector sigmoid(RealVector net){
		RealVector result = new ArrayRealVector(net.getDimension());
		for (int i = 0; i < net.getDimension(); i++){
			result.setEntry(i, sigmoid(net.getEntry(i)) );
		}
		return result;
	}
	
	private static List<RealVector> forwardPass(RealVector x, List<RealMatrix> w ){
		List<RealVector> h = new ArrayList<RealVector>();
		h.add(x);
		for ( int i = 1 ; i < w.size() + 1; i++ ){
			RealVector net = w.get(i - 1 ).operate( h.get(i-1).append(-1) );
			h.add(sigmoid(net));
		}
		return h;
	}
	
	private RealVector classify( RealVector v ){
		RealVector result = new ArrayRealVector(v.getDimension());
		int maxIndex = -1;
		double max = 0;
		for (int i = 0 ; i < v.getDimension(); i++){
			if ( v.getEntry(i) > max  ){
				maxIndex = i;
				max = v.getEntry(i);
			}
		}
		if (max > TRESHOLD ){
			result.setEntry(maxIndex, 1);
		}else{
			result.setEntry(maxIndex, 0);
		}
		return result;
	}
	
	private double getError(RealVector target , RealVector out){
		double result = 0;
		for (int i = 0 ; i < target.getDimension(); i++){			
			result += Math.pow( target.getEntry(i) - out.getEntry(i), 2d );
		}
		return result;
	}

	
	private static RealVector hadamard(RealVector v, RealVector u ){
		RealVector result = new ArrayRealVector(v.getDimension());
		for (int i = 0 ; i < v.getDimension(); i++){
			result.setEntry(i, v.getEntry(i)* u.getEntry(i));
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
	
	private static List<RealVector> normalizeInputs(List<RealVector> train, List<RealVector> test){
		RealVector average = mean(train);
		List<RealVector> result = new ArrayList<RealVector>();
		for (RealVector v : train ) {
			result.add( v.subtract(average) );
		}
		return result;
	}
	
	private static List<RealVector> normalizeInputs(List<RealVector> data){
		return normalizeInputs(data, data);
	}
	
	//na i pozicii je je trenovacia, na i + 1 je {trenovacia - validacna}
	public static List<List<RealVector>> getKSubSets(List<RealVector> data, int kFoldFactor){
		List<List<RealVector>> result = new ArrayList<List<RealVector>>();
		int chunkSize = data.size() / kFoldFactor;
		for (int k = 0; k < kFoldFactor; k++){
			
			List<RealVector> trainList = Utils.deepCopy(data);
			if (kFoldFactor > 1){
				for (int j = k*chunkSize ; j < k*(chunkSize + 1); j++){
					trainList.remove(k*chunkSize);
				}
			}
			result.add( trainList );
			
			List<RealVector> validationList = data.subList(k*chunkSize, k*chunkSize + chunkSize- 1);
			result.add( Utils.deepCopy(validationList) );
			
		}
		return result;
	}
	
	private class TrainThread extends Thread{
		public List<RealMatrix> weights;
		public List<RealVector> trainErrors;
		public List<RealVector> validErrors;
		
		
		private List<RealVector> subtrainx;
		private List<RealVector> subtrainy;
		private List<RealVector> subvalidx;
		private List<RealVector> subvalidy;
		
		private String trainTag;
		
		public TrainThread(List<RealVector> subtrainx, List<RealVector> subtrainy, List<RealVector> subvalidx, List<RealVector> subvalidy, String trainTag ){
			this.subtrainx = Utils.deepCopy(subtrainx);
			this.subtrainy = Utils.deepCopy(subtrainy);
			this.subvalidx = Utils.deepCopy(subvalidx);
			this.subvalidy = Utils.deepCopy(subvalidy);
			this.trainTag = trainTag;
		}
	
		public void run(){
			trainErrors = new ArrayList<RealVector>();
			validErrors = new ArrayList<RealVector>();
			
			try{
				System.out.println(trainTag + " : Training start");
				weights = train(subtrainx, subtrainy, subvalidx, subvalidy, trainErrors, validErrors, trainTag);
				System.out.println(trainTag + " : Training done");
				if (VERBOSE){
					plotErrorCurve(new File(Settings.OUT_PLOT_DIR, "curve_"+trainTag+".png"), trainErrors, validErrors);
				}
			}catch(Exception ex){
				throw new RuntimeException(ex);
			}
		}
	}
	
	//Vracia pole hodnot, kde result[0] je priemer hodnot, dalej su pre jednotlive modely
	public double[] testData(File f) throws IOException, InterruptedException{
		return testData( load(f, dimension),false );
	}
	
	public double[] testData(File f, boolean plot) throws IOException, InterruptedException{
		return testData( load(f, dimension),plot );
	}
	
	public double[] testData(InputDataContainer d, boolean plot) throws IOException, InterruptedException{
		double[] result = new double[w.size() + 1];
		
		List<RealVector> xtest = Utils.deepCopy(d.x);
		List<RealVector> ytest = Utils.deepCopy(d.y);
		xtest = normalizeInputs(xtest, data.x);
		shufftle(xtest, ytest);
		
		double sum = 0;
		for ( int i = 1; i < result.length; i++ ){
			List<RealMatrix> weigts = w.get(i-1);
			result[i] = validate( weigts, xtest, ytest);
			sum += result[i];
			if (plot){
				plotData(new File(Settings.OUT_PLOT_DIR, "actual_test_"+i+".png"),xtest,ytest);
				Thread.sleep(100);
				List<RealVector> predicted = predict(weigts, xtest);
				plotData(new File(Settings.OUT_PLOT_DIR, "predicted_test_"+i+".png"),xtest,predicted);
			}
		}
		result[0] = sum / w.size();
		return result;
	}
	
	
	public double train(int kFactor, List<Double> validationAccuracies) throws IOException, InterruptedException{
		List<RealVector> xtrain = Utils.deepCopy(data.x);
		List<RealVector> ytrain = Utils.deepCopy(data.y);
		
		shufftle(xtrain, ytrain);
		shufftle(xtrain, ytrain);
		
		List<List<RealVector>>  subsetsX = getKSubSets(xtrain, kFactor);
		List<List<RealVector>>  subsetsY = getKSubSets(ytrain, kFactor);

		ExecutorService service = Executors.newFixedThreadPool(4);
		List<TrainThread> trainTaskAll = new ArrayList<TrainThread>();
		
		List<InputDataContainer> subValids = new ArrayList<MultiLayerNN.InputDataContainer>();
		for ( int i = 0 ; i < kFactor; i++){
			List<RealVector> subtrainx = subsetsX.get(2*i);
			List<RealVector> subtrainy = subsetsY.get(2*i);
		
			List<RealVector> subvalidx = subsetsX.get(2*i+1);
			List<RealVector> subvalidy = subsetsY.get(2*i+1);
			
			
			subtrainx = normalizeInputs( subtrainx );
			subvalidx = normalizeInputs( subvalidx );
			
			subValids.add( new InputDataContainer( subvalidx, subvalidy ) );
			
			if (VERBOSE){
				plotData(new File(Settings.OUT_PLOT_DIR, "expected_train_"+i+".png"),subtrainx,subtrainy);
				Thread.sleep(100);
				plotData(new File(Settings.OUT_PLOT_DIR, "expected_valid_"+i+".png"),subvalidx, subvalidy);
			}
			
			TrainThread t = new TrainThread(subtrainx, subtrainy, subvalidx, subvalidy, ("kfold-"+i) );
			service.execute(t);
			trainTaskAll.add(t);
		}
			
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		double CV = 0;	
		w = new ArrayList<List<RealMatrix>>();
		for ( int i = 0 ; i < kFactor; i++){
			//todo:: kokotina tu je nejaka
			TrainThread t = trainTaskAll.get(i);	
			double accuracy = validate(t.weights, subValids.get(i).x, subValids.get(i).y);
			validationAccuracies.add(accuracy);
			CV += accuracy;
			w.add(t.weights);
		}		
		return CV / kFactor;
	}
	
	public List<RealMatrix> train(List<RealVector> xtrain, List<RealVector> ytrain, List<RealVector> xvalid, List<RealVector> yvalid, List<RealVector> trainErrors, List<RealVector> validErrors, String trainTag) throws IOException, InterruptedException{
				

		//TODO::zober minimum validacnej chyby, vyskusaj momentum
		List<RealMatrix> weights = initWeights(layers);	
		double E = 1;
		int ep = 0;
		
		double bestE  = E;
		List<RealMatrix> bestWeights = weights;
		
		while ( E > EPSILON && ep < MAX_EPOCH_COUNT){
			E = 0;
			shufftle( xtrain , ytrain );
			
			List<RealVector> ypredicted = new ArrayList<RealVector>();
			for (int i = 0 ; i < xtrain.size(); i++){
				
				RealVector x = xtrain.get(i);
				List<RealVector> h = forwardPass(x, weights); 
				RealVector y = h.get( h.size() - 1 );
				RealVector target = ytrain.get(i);
				
				RealVector cout = classify(y);
				double e = getError(target, cout);
				
				ypredicted.add(classify(y));
				
				if ( e > 0){
					RealVector[] delta = new RealVector[weights.size()]; 
					for ( int l = weights.size() - 1 ; l >= 0; l-- ){
						RealVector sig = h.get(l+1);
						RealVector der = hadamard(sig, sig.mapMultiply(-1).mapAdd(1) );				
						if (l == weights.size() - 1){
							delta[l] = hadamard(target.subtract(y), der );
						}else{			
							RealMatrix w_prev = weights.get(l+1);
							RealMatrix w_unbias = w_prev.getSubMatrix(0, w_prev.getRowDimension()-1, 0, w_prev.getColumnDimension() - 2);				
							RealVector tmp =  w_unbias.transpose().operate( delta[l+1] );
							delta[l] = hadamard(tmp, der);
						}
						
					}
					
					for ( int l = weights.size() - 1 ; l >= 0; l-- ){
						RealMatrix dwi  = delta[l].mapMultiply(alpha).outerProduct( h.get(l).append(-1));
						weights.set(l, weights.get(l).add(dwi));
					}
				}
			}

			double accuracy  = validate(weights,xvalid,yvalid);
			E = 1d - accuracy;

			if (E < bestE){
				bestE = E;
				bestWeights = Utils.deepCopyMat(weights);
			}
			
			if ( ep % CHECK_FREQUENCY == 0 || E <= EPSILON){	
				if (VERBOSE){
					System.out.println(trainTag + " : ep = " + ep + ", E = " + E );
				}
				
				if (trainErrors != null ){
					double trainE = 1d - validate(weights,xtrain,ytrain);;
					trainErrors.add(new ArrayRealVector( new double[]{ep,trainE } ) );
				}
				
				if (validErrors != null){
					validErrors.add(new ArrayRealVector( new double[]{ep,E } ) );
				}
			}
			ep++;
		}
		if (VERBOSE && ep < MAX_EPOCH_COUNT){
			System.out.println(trainTag + " : Training has converged " );
		}
		return bestWeights;
	}
	
	private void plotData(File outPut, List<RealVector> xtrain , List<RealVector> ytrain ) throws IOException, InterruptedException{
		HashMap<String, PrintWriter> pws = new HashMap<String, PrintWriter>();
		for (int i = 0; i < xtrain.size(); i++){
			String fName = "generated-data_" + vectorToClass.get(ytrain.get(i))+ ".dat";
			PrintWriter pw = pws.get(fName);
			if ( pw == null){
				pw = new PrintWriter(new File(Settings.GENERATED_DIR, fName).getAbsoluteFile() );
				pws.put(fName, pw);
			}
			
			pw.println(xtrain.get(i).getEntry(0) +" "+xtrain.get(i).getEntry(1));
		}
		
		for ( PrintWriter pw : pws.values()){
			pw.close();
		}
		
		PrintWriter pw = new PrintWriter(Settings.SCRIPT_FILE);
		
		pw.println( "set terminal png size 1024,768");
		pw.println( "set output '"+outPut.getAbsoluteFile()+"'");

		pw.println("set xlabel \"X1 axis\" ");
		pw.println("set ylabel \"X2 axis\" ");
		pw.println("set zlabel \"X3 axis\" "); 
		
		pw.println("set xrange [-5:5] ");
		pw.println("set yrange [-50:50] "); 
		
		
		pw.println("set title \"3D surface from a function\"");

		pw.println("set style line 1 lc rgb 'red' 	lt 1 lw 2 pt 7 ps 1"); 
		pw.println("set style line 2 lc rgb 'green' lt 1 lw 2 pt 7 ps 1");
		pw.println("set style line 3 lc rgb 'blue' 	lt 1 lw 2 pt 7 ps 1");
		
		String plotCmd = "plot ";
		int i = 1;
		for ( String fname : pws.keySet() ){
			if (i > 1){
				plotCmd += ", ";
			}
			plotCmd += "\""+new File(Settings.GENERATED_DIR, fname).getAbsoluteFile() +"\" with points ls " + i;
			i++;
		}
		pw.println(plotCmd);
		pw.close();
		Utils.plot(Settings.SCRIPT_FILE);
		
	}
	
	public static void plotErrorCurve( File outPut, List<RealVector> trainErrors , List<RealVector> validErrors) throws IOException, InterruptedException{
		plotErrorCurve(outPut, trainErrors, validErrors, "epoch" , "error");
	}
	
	public static void plotErrorCurve( File outPut, List<RealVector> trainErrors , List<RealVector> validErrors, String xname, String yname) throws IOException, InterruptedException{
		File trainErrorsFile = new File(Settings.GENERATED_DIR, "generated-train-errors.dat");
		File validErrorsFile = new File(Settings.GENERATED_DIR, "generated-valid-errors.dat");
		
		PrintWriter pwT = new PrintWriter(trainErrorsFile);
		PrintWriter pwV = new PrintWriter(validErrorsFile);
		for (int i = 0; i < trainErrors.size(); i++){
			RealVector vT = trainErrors.get(i);
			RealVector vV = validErrors.get(i);
			pwT.println( vT.getEntry(0) + " " + vT.getEntry(1) );
			pwV.println( vV.getEntry(0) + " " + vV.getEntry(1) );
		}
		pwT.close();
		pwV.close();
		
		PrintWriter pw = new PrintWriter(Settings.SCRIPT_FILE);
		pw.println( "set terminal png size 1024,768");
		pw.println( "set output '"+outPut+"'");
		pw.println( "set yrange [0:1]");
		pw.println("set xlabel \""+xname+"\" ");
		pw.println("set ylabel \""+yname+"\" ");
		pw.println("set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 0.5"); 
		pw.println("plot \""+trainErrorsFile.getAbsoluteFile()+"\" with lines ls 1, \""+validErrorsFile.getAbsolutePath()+"\" with lines ls 2");	
		pw.close();	
		
		Utils.plot(Settings.SCRIPT_FILE);
	}
	
	private List<RealVector> predict(List<RealMatrix> weights, List<RealVector> xxdata){
		List<RealVector> result = new ArrayList<RealVector>();
		for (int i = 0; i < xxdata.size(); i++){
			List<RealVector> fw = forwardPass(xxdata.get(i), weights);
			RealVector predicted = fw.get(fw.size() - 1  );
			result.add( classify(predicted) );;
		}
		return result;
	}
	
	private double validate(List<RealMatrix> weights, List<RealVector> xxdata, List<RealVector> yydata){
		double okCount = 0;
		List<RealVector> predicted = predict(weights,xxdata);
		for (int i = 0; i < xxdata.size(); i++){
			if (predicted.get(i).equals( yydata.get(i) ) ){
				okCount += 1;
			}
		}
		return okCount / xxdata.size();
	}
	
	public List<RealMatrix> getBestWeights(List<RealVector> xxdata, List<RealVector> yydata){
		int bestIndex = 0;
		double best = -1;
		for (int i = 0 ; i < w.size(); i++){
			double a = validate(w.get(i), xxdata, yydata); 
			if ( a > best){
				best = a;
				bestIndex = i;
			}
		}
		return w.get(bestIndex);
	}
	
	public RealMatrix getBestConfusionMatrix(List<RealVector> xxdata, List<RealVector> yydata){
		xxdata = normalizeInputs(xxdata, data.x);
		
		List<RealMatrix> weights = getBestWeights(xxdata, yydata);
		RealMatrix result = new Array2DRowRealMatrix(classToVector.size(), classToVector.size() + 1);
		
		HashMap<String, Integer> classToIndex = new HashMap<String, Integer>();
		Set<String> orderedClasses = new TreeSet<String>(classToVector.keySet());
		int index = 0;
		for (String cls : orderedClasses){
			classToIndex.put(cls, index);
			index++;
		}
		
		
		for (int i = 0; i < xxdata.size(); i++){
			List<RealVector> fw = forwardPass(xxdata.get(i), weights );
			RealVector predicted = classify(fw.get(fw.size() - 1  ));
			RealVector actual =  yydata.get(i);
			
			int actualIndex = classToIndex.get( vectorToClass.get(actual) );
			
			
			String predicetStr = vectorToClass.get(predicted);
			int predictedIndex = 0;
			if (predicetStr == null){
				predictedIndex = classToIndex.size();
			}else{
				predictedIndex = classToIndex.get( predicetStr  );
			}
			double oldVal = result.getEntry(actualIndex, predictedIndex);
			result.setEntry(actualIndex, predictedIndex, oldVal + 1);
		}
		return result;
	}
}
