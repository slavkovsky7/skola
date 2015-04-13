package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class MultiLayerNNTester {

	
	
	private static double[] kfoldSingleTest(File train, File test) throws Exception {
		MultiLayerNN nn = new MultiLayerNN(train,INPUT_SIZE, LAYERS);
		return kfoldSingleTest(nn, train, test, null, null);
	}
	
	private static final int TEST_INDEX = 0;
	private static final int TRAIN_INDEX = 1;
	private static final int CV_INDEX = 2;
	
	private static double[] kfoldSingleTest(MultiLayerNN nn, File train, File test, List<RealVector> trainErrors, List<RealVector> testErrors) throws Exception {
	
		nn.MAX_EPOCH_COUNT = EPOCH_COUNT;
		nn.alpha = ALPHA;
		int[] layers = nn.getLayers();
		String modelID = Arrays.toString(layers);
		System.out.println("Training model with " +modelID+ " neurons");
		List<Double> validationAccuracies = new ArrayList<Double>();
		double cv = nn.train(K_FACTOR,validationAccuracies);
		
		double[] test_accuracy = nn.testData(test,true);
		double[] train_accuracy = nn.testData(train );
		
		if (trainErrors != null){
			trainErrors.add( new ArrayRealVector(new double[]{layers[layers.length - 2], 1-train_accuracy[0]  } ) );
		}
		
		if (testErrors != null ){
			testErrors.add ( new ArrayRealVector(new double[]{layers[layers.length - 2], 1-test_accuracy[0] } ) );
		}
		
		System.out.println("--------------------------");
		System.out.println("M("+Arrays.toString(layers)+").CV = " + cv);
		System.out.println("M("+Arrays.toString(layers)+").TrainAccuracies = " + Arrays.toString(train_accuracy) );
		System.out.println("M("+Arrays.toString(layers)+").TestAccuracies = " + Arrays.toString(test_accuracy) );
		System.out.println("M("+Arrays.toString(layers)+").ValidAccuracies = " +  Utils.toString(validationAccuracies));
		System.out.println("AverageTrainAccuracy = " + train_accuracy[0] );
		System.out.println("AverageTestAccuracy = " + test_accuracy[0] );
		
		if (trainErrors == null || testErrors == null){
			System.out.println("ConfusionMatrix");
			MultiLayerNN.InputDataContainer con =  nn.load(test, INPUT_SIZE);
			System.out.println( nn.getBestConfusionMatrix(con.x, con.y));
		}
		
		return new double[]{test_accuracy[0], train_accuracy[0], cv};
	}
	
	
	
	private static void batchKFoldTest(File train, File test, int count) throws Exception{
		double trainAverage = 0;
		double testAverage  = 0;
		for (int i = 0 ; i < count; i++){
			System.out.println("kfold test :" + i);	
			double[] tmp = kfoldSingleTest(train, test); 
			testAverage += tmp[0];
			trainAverage += tmp[1];
		}
		
		trainAverage /= count;
		testAverage  /= count;
		System.out.println("Batch test trainAverage = " + trainAverage);
		System.out.println("Batch test testAverage = " + testAverage);
	}

	private static void findBestModel(File train, File test, boolean twoLayers) throws Exception {

		double bestCV = -1;
		String bestCVmodelID = "";
		
		double bestTrainAccuracy = 0, bestTestAccuracy = 0;
		for ( int i = FIND_START_INDEX; i < FIND_END_INDEX; i+=FIND_FREQ ){
			List<RealVector> trainErrors = new ArrayList<RealVector>(); 
			List<RealVector> testErrors = new ArrayList<RealVector>();
			for (int j = FIND_START_INDEX ; j < FIND_END_INDEX; j+=FIND_FREQ){	
				int[] layers = twoLayers ? new int[]{i,j} : new int[]{j} ;
				MultiLayerNN nn = new MultiLayerNN(train,INPUT_SIZE, layers);
				double[] modelResults = kfoldSingleTest(nn,train,test,trainErrors, testErrors);
				
				if ( modelResults[CV_INDEX] > bestCV){
					bestCV = modelResults[CV_INDEX];
					bestCVmodelID = Arrays.toString(layers);;
					bestTestAccuracy = modelResults[TEST_INDEX];
					bestTrainAccuracy = modelResults[TRAIN_INDEX];
				}
			}
			MultiLayerNN.plotErrorCurve(new File(Settings.OUT_PLOT_DIR, "overfit_"+i+".png"), trainErrors, testErrors, "number of neurons", "error");
			
			if (!twoLayers){
				break;
			}
		}
		
		System.out.println("--------------------------");
		System.out.println("Best : M("+bestCVmodelID+").CV = " + bestCV);
		System.out.println("Best : M("+bestCVmodelID+").AverageTrainAccuracy = " + bestTrainAccuracy);
		System.out.println("Best : M("+bestCVmodelID+").AverageTestccuracy  = " + bestTestAccuracy );
	}
	
	/*******************************************/
	public static int INPUT_SIZE = 2;
	public static double ALPHA = 0.1;
	public static int K_FACTOR = 8;
	public static int EPOCH_COUNT = 500;
	public static int[] LAYERS = new int[]{20,20};
	public static String JOB = "";
	public static int BATCH_TEST_COUNT = 2;
	public static File TEST_FILE =  new File ("src/main/resources/2d.trn.dat");
	public static File TRAIN_FILE = new File ("src/main/resources/2d.tst.dat");
	public static boolean TWO_LAYERS = false;
	
	public static int FIND_START_INDEX = 15;
	public static int FIND_END_INDEX = 40;
	public static int FIND_FREQ = 2;
	
	private static void parseProperties(File propFile) throws Exception{
		Properties prop = new Properties();
		InputStream in = new FileInputStream(propFile);
		prop.load(in);
		ALPHA = Double.parseDouble( prop.getProperty("alpha") );
		K_FACTOR = Integer.parseInt( prop.getProperty("k_factor") );
		EPOCH_COUNT = Integer.parseInt( prop.getProperty("epoch_count") );
		JOB =  prop.getProperty("job");	
		BATCH_TEST_COUNT = Integer.parseInt( prop.getProperty("batch_test_count") );
		TRAIN_FILE = new File( prop.getProperty("train_file") );	
		TEST_FILE =  new File( prop.getProperty("test_file") );	
		TWO_LAYERS = Boolean.parseBoolean("two_layers");
		try{
			String strLayers = prop.getProperty("layers");
			String[] strLayersArray = strLayers.split(",");
			LAYERS = new int[strLayersArray.length];
			for (int i = 0; i < strLayersArray.length; i++){
				LAYERS[i] = Integer.parseInt( strLayersArray[i] );
			}
		}catch (Exception ex){
			throw new Exception("Ivalid layers string in property file");
		}
		
		FIND_START_INDEX = Integer.parseInt( prop.getProperty("find_start_index") );	
		FIND_END_INDEX =  Integer.parseInt( prop.getProperty("find_end_index") );
		FIND_FREQ = Integer.parseInt( prop.getProperty("find_frequency") );;
		
		System.out.println("---------Settings---------");
		System.out.println("ALPHA 	 	= " + ALPHA ); 
		System.out.println("K_FACTOR	= " + K_FACTOR );
		System.out.println("EPOCH_COUNT	= " + EPOCH_COUNT ); 
		System.out.println("LAYERS 		= " + Arrays.toString(LAYERS) );
		System.out.println("TEST_FILE 	= " + TEST_FILE );
		System.out.println("TRAIN_FILE 	= " + TRAIN_FILE );
		System.out.println("JOB 		= " + JOB );
		System.out.println("TWO_LAYERS 	= " + TWO_LAYERS );
		System.out.println("FIND_START_INDEX = " + FIND_START_INDEX );
		System.out.println("FIND_END_INDEX 	 = " + FIND_END_INDEX );
		System.out.println("FIND_FREQ   	 = " + FIND_FREQ );
		System.out.println("--------------------------");
	}
	
	public static void main(String[] args){
		try {
			parseProperties(new File("settings.properties") );
			
			//TODO::argumenty
			if ( JOB.equals( "best_model") ){
				findBestModel(TRAIN_FILE, TEST_FILE, TWO_LAYERS);
			}else if ( JOB.equals( "batch_test") ){
				batchKFoldTest(TRAIN_FILE, TEST_FILE, BATCH_TEST_COUNT);
			}else if ( JOB.equals("single_test") ){
				kfoldSingleTest(TRAIN_FILE, TEST_FILE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
