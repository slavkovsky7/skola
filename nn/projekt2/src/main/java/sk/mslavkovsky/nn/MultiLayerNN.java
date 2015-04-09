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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.w3c.dom.ls.LSInput;

public class MultiLayerNN {
	private List<RealMatrix> w; 
	private List<RealVector> xdata = new ArrayList<RealVector>();
	private List<RealVector> ydata = new ArrayList<RealVector>();
	
	private List<RealVector> xdata_test = new ArrayList<RealVector>();
	private List<RealVector> ydata_test = new ArrayList<RealVector>();
	
	
	private int[] layers;
	public int MAX_EPOCH_COUNT = 10000;
	public double alpha = 0.1;
	public double TRESHOLD = 0.5d;
	public int CHECK_FREQUENCY = 100;
	public boolean VERBOSE = true;
	public double EPSILON = 0.0001;
	
	private HashMap<String, RealVector> classToVector = new HashMap<String, RealVector>();
	private HashMap<RealVector,String > vectorToClass = new HashMap<RealVector, String>();
	
	public static final File GENERATED_DIR = new File("generated");
	public static final File SCRIPT_FILE= new File(GENERATED_DIR,"generated-plot-code.gnuplot");
	public static final File OUT_PLOT_DIR = new File(GENERATED_DIR,"output" );
	
	
	
	public MultiLayerNN(File f, File testFile, int inputSize, int[] hiddenLayers) throws FileNotFoundException{
		load(f, inputSize, xdata, ydata);
		load(f, inputSize, xdata_test, ydata_test);
		
		this.layers = new int[hiddenLayers.length + 2];
		this.layers[0] = xdata.get(0).getDimension();
		for (int i = 0 ; i < hiddenLayers.length; i++){
			layers[i+1] = hiddenLayers[i];
		}
		this.layers[ layers.length - 1 ] = ydata.get(0).getDimension();
		
		//TODO::Kontrola na vstupy
	}
	
	public void printData(List<RealVector> xxdata, List<RealVector> yydata){
		for (int i = 0; i < xxdata.size(); i++){
			System.out.println( xxdata.get(i).toString() + " -> " + yydata.get(i));
		}
	}
	
	
	private void load(File f, int inputSize, List<RealVector> outXData, List<RealVector> outYData) throws FileNotFoundException{
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
		
		outXData.clear();
		outYData.clear();
		
		int i = 0;
		for ( String className : classNames){
			RealVector v = new ArrayRealVector(classNames.size());
			v.setEntry(i, 1);
			classToVector.put(className, v); 
			vectorToClass.put(v, className);
			i++;
		}
		
		for ( i = 0; i < xPremim.size(); i++){
			outXData.add( new ArrayRealVector( xPremim.get(i) ) );
			outYData.add( classToVector.get(yPrelim.get(i)).copy() );
		}
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
		RealVector result = v.copy();
		for (int i = 0 ; i < v.getDimension(); i++){
			result.setEntry(i, result.getEntry(i) > TRESHOLD ? 1 : 0);
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
	
	private static List<RealVector> normalizeInputs(List<RealVector> data){

		RealVector average = new ArrayRealVector(data.get(0).getDimension());
		for (RealVector v : data ) {
			average = average.add(v);
		}
		average = average.mapMultiply( 1d/data.size());
		List<RealVector> result = new ArrayList<RealVector>();
		for (RealVector v : data ) {
			result.add( v.subtract(average) );
		}
		return result;
	}
	
	//na i pozicii je je trenovacia, na i + 1 je {trenovacia - validacna}
	public static List<List<RealVector>> getKSubSets(List<RealVector> data, int kFoldFactor){
		List<List<RealVector>> result = new ArrayList<List<RealVector>>();
		int chunkSize = data.size() / kFoldFactor;
		for (int k = 0; k < kFoldFactor; k++){
			
			List<RealVector> trainList = Utils.deepCopy(data);
			for (int j = k*chunkSize ; j < k*chunkSize + chunkSize; j++){
				trainList.remove(k*chunkSize);
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
		
		public TrainThread(List<RealVector> subtrainx, List<RealVector> subtrainy, List<RealVector> subvalidx, List<RealVector> subvalidy){
			this.subtrainx = Utils.deepCopy(subtrainx);
			this.subtrainy = Utils.deepCopy(subtrainy);
			this.subvalidx = Utils.deepCopy(subvalidx);
			this.subvalidy = Utils.deepCopy(subvalidy);
		}
	
		public void run(){
			trainErrors = new ArrayList<RealVector>();
			validErrors = new ArrayList<RealVector>();
			
			try{
				weights = train(subtrainx, subtrainy, subvalidx, subvalidy, trainErrors, validErrors);
			}catch(Exception ex){
				throw new RuntimeException(ex);
			}
		}
	}
	
	public void train(int kFoldFactor, int testSetPercentage) throws IOException, InterruptedException{
		List<RealVector> xtrain = Utils.deepCopy(xdata);
		List<RealVector> ytrain = Utils.deepCopy(ydata);
	
		List<RealVector> xtest = Utils.deepCopy(xdata_test);
		List<RealVector> ytest = Utils.deepCopy(ydata_test);
		
		xtrain = normalizeInputs( xtrain );
		xtest = normalizeInputs(xtest);
		
		shufftle(xtrain, ytrain);
		shufftle(xtest, ytest);
		//int trainMaxIndex = (xtrain.size() / testSetPercentage ) * (testSetPercentage - 1);
		//List<RealVector> xtest = xtrain.subList(trainMaxIndex, xtrain.size() - 1);
		//List<RealVector> ytest = ytrain.subList(trainMaxIndex, ytrain.size() - 1);		
		//xtrain = Utils.deepCopy( xtrain.subList(0, trainMaxIndex - 1) );
		//ytrain = Utils.deepCopy( ytrain.subList(0, trainMaxIndex - 1) );
		
		
		//k-fold cross validation split
		shufftle(xtrain, ytrain);
		//printData(xtrain, ytrain);
		System.out.println(" ============= Original ============= ");
		plotData(new File(OUT_PLOT_DIR, "expected.png"),xtrain, ytrain);
		
		List<List<RealVector>>  subsetsX = getKSubSets(xtrain, kFoldFactor);
		List<List<RealVector>>  subsetsY = getKSubSets(ytrain, kFoldFactor);
		
		List<List<RealMatrix>> bestWeightsList = null;
		double maxCV = -10; 
		
		for ( int i = 0 ; i < subsetsX.size(); i+=2){
			List<RealVector> subtrainx = subsetsX.get(i);
			List<RealVector> subtrainy = subsetsY.get(i);
		
			List<RealVector> subvalidx = subsetsX.get(i+1);
			List<RealVector> subvalidy = subsetsY.get(i+1);
			
			System.out.println("------------- Training "+i+"-th model ------------- ");
			plotData(new File(OUT_PLOT_DIR, "expected_train_"+i+".png"),subtrainx,subtrainy);
			Thread.sleep(1000);
			plotData(new File(OUT_PLOT_DIR, "expected_valid_"+i+".png"),subvalidx, subvalidy);
			
			
			
			ExecutorService service = Executors.newFixedThreadPool(4);
			List<TrainThread> trainTasks = new ArrayList<TrainThread>();
			for (int k = 0 ; k < kFoldFactor; k++){
				TrainThread t = new TrainThread(subtrainx, subtrainy, subvalidx, subvalidy);
				trainTasks.add( t );
				service.execute(t);
				//TODO::
			} 
			
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			
			
			List<List<RealVector>> trainErrorsLists = new ArrayList<List<RealVector>>();
			List<List<RealVector>> validErrorsLists = new ArrayList<List<RealVector>>();
			List<List<RealMatrix>> weightsList = new ArrayList<List<RealMatrix>>();
			System.out.println("..................................");
			double CV = 0;
			for (int k = 0 ; k < kFoldFactor; k++){
				TrainThread t = trainTasks.get(k);
				double accuracy = validate(t.weights, subvalidx, subvalidy);
				System.out.println("Resulting validation accuracy  = " + accuracy);
				trainErrorsLists.add(t.trainErrors);
				validErrorsLists.add(t.validErrors);
				plotErrorCurve(new File(OUT_PLOT_DIR, "curve_"+i+"_"+k+".png"), t.trainErrors, t.validErrors);

				CV += accuracy;
				
				weightsList.add(t.weights);
			}
			CV = CV / kFoldFactor;

			List<RealVector> trainErrors = getAverage(trainErrorsLists);
			List<RealVector> validErrors = getAverage(validErrorsLists);
			plotErrorCurve(new File(OUT_PLOT_DIR, "curve_"+i+"_avg.png"), trainErrors, validErrors);
			
			System.out.println("CrossValidation coeficient = " + CV);
			if (CV > maxCV){
				maxCV = CV;
				bestWeightsList = weightsList;
			}
			
			//TODO:: test data accuracy
		}	
		
		
		System.out.println("================ Testing model on test data ================ ");
		double avg = 0;
		for ( List<RealMatrix> weights : bestWeightsList ){
			double accuracy = validate(weights, xtest, ytest);
			avg += accuracy;
			System.out.println("Testing accuracy = " + accuracy);
		}
		avg /= bestWeightsList.size();
		
		System.out.println("Average testing accuracy = " + avg);
	}
	
	private List<RealVector> getAverage( List<List<RealVector>> lists){
		if (lists.size() == 0){
			throw new RuntimeException("Empty list");
		}
		
		int maxSize = 0;
		int indexMaxList = 0;
		for (int i = 0;  i < lists.size();i++){
			if (lists.get(i).size() > maxSize){
				maxSize = lists.get(i).size();
				indexMaxList = i;
			}
		}
		
		List<RealVector> result = new ArrayList<RealVector>(maxSize);
		for (int i = 0; i < maxSize; i++){
			result.add(new ArrayRealVector(new double[]{0,0}));
		}
		
		for (int i = 0 ; i < lists.size(); i++){
			for (int j = 0 ; j < lists.get(i).size(); j++){
				RealVector tmp = result.get(j);
				tmp = tmp.add( lists.get(i).get(j) );
				result.set(j,tmp );
			}
		}
		
		for (int j = 0 ; j < result.size(); j++){
			result.set(j, result.get(j).mapMultiply( 1d/ lists.size() ) );
			result.get(j).setEntry(0, lists.get(indexMaxList).get(j).getEntry(0) );
		}
		return result;
	}
	
	public List<RealMatrix> train(List<RealVector> xtrain, List<RealVector> ytrain, List<RealVector> xvalid, List<RealVector> yvalid, List<RealVector> trainErrors, List<RealVector> validErrors) throws IOException, InterruptedException{
		
		//TODO::zober minimum validacnej chyby, vyskusaj momentum
		List<RealMatrix> weights = initWeights(layers);	
		double E = 1;
		int ep = 0;
		
		double bestE  = E;
		List<RealMatrix> bestWeights = weights;
		
		while ( E > EPSILON && ep < MAX_EPOCH_COUNT){
			E = 0;
			shufftle( xtrain , ytrain );
			
			//DEBUG::
			List<RealVector> ypredicted = new ArrayList<RealVector>();
			//DEBUG::
			for (int i = 0 ; i < xtrain.size(); i++){
				
				RealVector x = xtrain.get(i);
				List<RealVector> h = forwardPass(x, weights); 
				RealVector y = h.get( h.size() - 1 );
				RealVector target = ytrain.get(i);
				
				RealVector cout = classify(y);
				double e = (0.5d)* getError(target, cout);
				
				//DEBUG::
				ypredicted.add(classify(y));
				
				//DEBUG::
				
				//System.out.println(e);
				//System.out.println(x);
				//System.out.println(target);
				//System.out.println(y);
				//System.out.println("------------------------");
				
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
						//System.out.println(  h.get(l).append(-1 ) );
						//System.out.println(  delta[l] );
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
			
			if ( ep % CHECK_FREQUENCY == 0){
				//DEBUG::
				//File f = new File("output", "generated_" + ep + ".png");
				//plotData(f,xtrain, ypredicted);
				
				if (VERBOSE){
					System.out.println("ep = " + ep + ", E = " + E );
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
		
		return bestWeights;
	}
	
	private void plotData(File outPut, List<RealVector> xtrain , List<RealVector> ytrain ) throws IOException, InterruptedException{
		HashMap<String, PrintWriter> pws = new HashMap<String, PrintWriter>();
		for (int i = 0; i < xtrain.size(); i++){
			String fName = "generated-data_" + vectorToClass.get(ytrain.get(i))+ ".dat";
			PrintWriter pw = pws.get(fName);
			if ( pw == null){
				pw = new PrintWriter(new File(GENERATED_DIR, fName).getAbsoluteFile() );
				pws.put(fName, pw);
			}
			
			pw.println(xtrain.get(i).getEntry(0) +" "+xtrain.get(i).getEntry(1));
		}
		
		for ( PrintWriter pw : pws.values()){
			pw.close();
		}
		
		PrintWriter pw = new PrintWriter(SCRIPT_FILE);
		
		pw.println( "set terminal png size 1024,768");
		pw.println( "set output '"+outPut.getAbsoluteFile()+"'");

		pw.println("set xlabel \"X1 axis\" ");
		pw.println("set ylabel \"X2 axis\" ");
		pw.println("set zlabel \"X3 axis\" "); 
		
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
			plotCmd += "\""+new File(GENERATED_DIR, fname).getAbsoluteFile() +"\" with points ls " + i;
			i++;
		}
		pw.println(plotCmd);
		pw.close();
		
		//TODO:: vylepsit
		//Utils.cmd("killall gnuplot",false);
		Utils.plot(SCRIPT_FILE);
		//Thread.sleep(50);
		//Utils.cmd("cd "+SCRIPT_FILE.getAbsoluteFile().getParent()+" &&  tail -f  "+SCRIPT_FILE.getAbsolutePath()+" | gnuplot  '-'", false, false);
		
	}
	
	private void plotErrorCurve( File outPut, List<RealVector> trainErrors , List<RealVector> validErrors) throws IOException, InterruptedException{
		File trainErrorsFile = new File(GENERATED_DIR, "generated-train-errors.dat");
		File validErrorsFile = new File(GENERATED_DIR, "generated-valid-errors.dat");
		
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
		
		PrintWriter pw = new PrintWriter(SCRIPT_FILE);
		pw.println( "set terminal png size 1024,768");
		pw.println( "set output '"+outPut+"'");
		pw.println("set xlabel \"epoch\" ");
		pw.println("set ylabel \"error\" ");
		pw.println("set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 0.5"); 
		pw.println("plot \""+trainErrorsFile.getAbsoluteFile()+"\" with lines ls 1, \""+validErrorsFile.getAbsolutePath()+"\" with lines ls 2");	
		pw.close();	
		
		Utils.plot(SCRIPT_FILE);
	}
	
	private double validate(List<RealMatrix> weights, List<RealVector> xxdata, List<RealVector> yydata){
		double okCount = 0;
		for (int i = 0; i < xxdata.size(); i++){
			List<RealVector> fw = forwardPass(xxdata.get(i), weights );
			RealVector predicted = fw.get(fw.size() - 1  );
			if (classify(predicted).equals( yydata.get(i) ) ){
				okCount += 1;
			}
		}
		return okCount / xxdata.size();
	}
	
	public static void main(String[] args){
		//URL url = Thread.currentThread().getContextClassLoader().getResource("2d.trn.dat");
		
		File f = new File ("src/main/resources/2d.trn.dat");
		File test = new File ("src/main/resources/2d.tst.dat");
		try {
			MultiLayerNN nn = new MultiLayerNN(f,test,2, new int[]{10,9,8});
			nn.train(8, 10);
			//nn.printData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
