package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.ArrayRealVector;

import sk.mslavkovsky.nn.SigmoidPerceptron.LearningType;
import sk.mslavkovsky.nn.SigmoidPerceptron.TrainErrorType;


public class PerceptronTester {


	
	public static void testPerceptron(Perceptron p, double[][] data ){
		for (int i = 0 ; i < data.length; i++){
			double[] sample = new double[ data[i].length -1];
			for (int j = 0; j < sample.length; j++){
				sample[j] = data[i][j];
			}
			double expected = data[i][ data[i].length - 1 ];
			double predicted = p.predict( sample );
			if ( Math.abs( predicted - expected ) > 0.05){
				System.out.println("Test for sample {"+new ArrayRealVector(data[i])+"} has failed");
			}
		}
	}
	
	TrainStatistics[] trainBatch(int batchTestCount, double alpha,double[][] data) throws FileNotFoundException{
		TrainStatistics[] result = new TrainStatistics[batchTestCount];
		for (int i = 0; i < batchTestCount; i++){
			Perceptron p = useDiscrete ? new DiscretePerceptron(data, alpha): new SigmoidPerceptron(data,alpha);
			if (p.getClass() == SigmoidPerceptron.class) {
				((SigmoidPerceptron)p).ERROR_TYPE = errorType;
				((SigmoidPerceptron)p).LEARNING_TYPE = learnType;
			}
			p.MAX_EPOCHS = 6000;
			result[i] = p.train(null);
			//testPerceptron(p, data);
		}
		return result;
	}
	
	double computeAverageEpoch(TrainStatistics[] stats){
		double sum = 0;
		for (TrainStatistics statistics  : stats ){
			sum += statistics.Epoch;
		}
		return sum/stats.length;
	}
	
	void runAlphaTest(File dataF, File scriptF , String name,  double[][] data, double start, double end, double delta, int batchTestCount ) throws IOException{
	
		ArrayList<Vector2D> curve = new ArrayList<Vector2D>();
		double alpha = start;
		while ( alpha < end){
			TrainStatistics[] stats = trainBatch(batchTestCount, alpha, data);
			String percType = useDiscrete ? "discrete" : "sigmoid"; 
			System.out.println("Training "+percType+ " perceptrons for alpha = " + alpha);
			double eAverage = computeAverageEpoch(stats); 
			Vector2D v = new Vector2D(alpha, eAverage) ;
			curve.add( v  );
			System.out.println("	Output[a,E] : " + v );
			alpha += delta;
		}
		
		PrintWriter pw = new PrintWriter(dataF);
		for (Vector2D v : curve){
			pw.println( v.getX() + " " + v.getY() );
		}
		pw.close();
		
		
		pw = new PrintWriter(scriptF);
		pw.println("set term wxt title '"+name+"'");
		//pw.println("set yrange [0:100] ");
		pw.println("set xlabel \"alpha\" ");
		pw.println("set ylabel \"epoch\" ");
		pw.println("set style line 1 lc rgb 'red' lt 1 lw 2 pt 7 ps 0.5"); 
		pw.println("plot \""+dataF+"\" with lines ls 1");	
		pw.close();
	}
	
	public static void druhaUloha() throws IOException, InterruptedException{
		//double[][] data = Perceptron.load(new File("/home/martin/workspace/tmp/projekt1/hard.txt"));
		//SigmoidPerceptron p = new SigmoidPerceptron(data, 0.05);
		System.out.println("===== Phase : Simulacia ucenia ====="  );
		SigmoidPerceptron p = new SigmoidPerceptron(OR_3D, 0.01);
		p.ERROR_TYPE = TrainErrorType.Classification;
		p.LEARNING_TYPE = LearningType.Online;
		p.VERBOSE = true;
		p.PLOT_FREQUENCE = 1;
		
		TrainStatistics stats = p.train(TRAIN_FILE);		
		System.out.println(stats.toString());
		
		p.generatePlotTrainCode(PerceptronTester.SCRIPT_FILE, TRAIN_FILE);
		Utils.plot(PerceptronTester.SCRIPT_FILE);

		p.generatePlotCode(PerceptronTester.SCRIPT_FILE, PerceptronTester.NEG_FILE, PerceptronTester.POS_FILE);
		Utils.plot(PerceptronTester.SCRIPT_FILE);
	}
	
	public static void prvaUloha() throws IOException, InterruptedException{
		System.out.println("===== Phase : Priebeh ucenia [alpha,epoch] : sigmoidny perceptron AND====="  );
		PerceptronTester tester = new PerceptronTester();
		tester.runAlphaTest(DATA_FILE, SCRIPT_FILE, "3D-AND SIGMOID", AND_3D, AlphaStart, DeltaEnd, DeltaAlpha, BatchCount );
		Utils.plot(SCRIPT_FILE);

		System.out.println("===== Phase : Priebeh ucenia [alpha,epoch] : sigmoidny perceptron OR ====="  );
		PerceptronTester tester2 = new PerceptronTester();
		tester2.runAlphaTest(DATA_FILE, SCRIPT_FILE, "3D OR SIGMOID ", OR_3D, AlphaStart, DeltaEnd, DeltaAlpha, BatchCount );
		Utils.plot(SCRIPT_FILE);
		
		if (hardData != null){
			System.out.println("===== Phase : Priebeh ucenia [alpha,epoch] : sigmoidny perceptron HARD ====="  );
			tester.runAlphaTest(DATA_FILE, SCRIPT_FILE, "HARD SIGMOID", hardData,AlphaStart, DeltaEnd, DeltaAlpha, HardBatchCount );
			Utils.plot(SCRIPT_FILE);
		}
	}
	
	public static void tretiaUloha() throws IOException, InterruptedException{
		
		PerceptronTester tester = new PerceptronTester();
		tester.useDiscrete = true;
		
		System.out.println("===== Phase : Priebeh ucenia [alpha,epoch] : diskretny perceptron AND_3D ====="  );
		tester.runAlphaTest(DATA_FILE, SCRIPT_FILE, "3D-AND DISCRETE", AND_3D, AlphaStart, DeltaEnd, DeltaAlpha, BatchCount );
		Utils.plot(SCRIPT_FILE);
		
		System.out.println("===== Phase : Priebeh ucenia [alpha,epoch] : diskretny perceptron OR_3D ====="  );
		tester.runAlphaTest(DATA_FILE, SCRIPT_FILE, "3D OR DISCRETE", OR_3D, AlphaStart, DeltaEnd, DeltaAlpha, BatchCount );
		Utils.plot(SCRIPT_FILE);
		
		if (hardData != null){
			System.out.println("===== Phase : Priebeh ucenia [alpha,epoch] : diskretny perceptron HARD ====="  );
			tester.runAlphaTest(DATA_FILE, SCRIPT_FILE, "HARD DISCRETE", hardData, AlphaStart, DeltaEnd, DeltaAlpha, HardBatchCount );
			Utils.plot(SCRIPT_FILE);
		}
	}
	
	/********************** Options *********************/
	//Objektove, ale moze byt pouzite aj ako nastavenia objektu samotneho
	public boolean useDiscrete = false;
	public TrainErrorType errorType = TrainErrorType.Classification; 
	public LearningType learnType = LearningType.Online; 
	
	
	public static int BatchCount = 50;
	public static int HardBatchCount = 25;
	public static double DeltaAlpha = 0.01;
	public static double AlphaStart = 0.01;
	public static double DeltaEnd = 0.95;
	
	
	private static final File POS_FILE= new File("generated-pos.dat");
	private static final File NEG_FILE= new File("generated-neg.dat");
	private static final File SCRIPT_FILE= new File("generated-plot-code.gnuplot");
	private static final File TRAIN_FILE= new File("generated-train.dat");
	private static final File DATA_FILE= new File("data-train.dat");
	private static double[][] hardData = null;
	
	
	/********************** Fidex data *********************/
	public final static double[][] OR_2D = new double[][]{
		{0,0,0},
		{0,1,1},
		{1,0,1},
		{1,1,1}
	};
	
	public final static double[][] AND_2D = new double[][]{
		{0,0,0},
		{0,1,0},
		{1,0,0},
		{1,1,1}
	};
	
	public final static double[][] XOR_2D = new double[][]{
		{0,0,0},
		{0,1,1},
		{1,0,1},
		{1,1,0} 
	};
	
	public final static double[][] OR_3D = new double[][]{
		{0,0,0,0},
		{0,0,1,1},
		{0,1,0,1},
		{1,0,0,1},
		{1,1,0,1},
		{1,0,1,1},
		{0,1,1,1},
		{1,1,1,1}
	};
	
	public final static double[][] AND_3D = new double[][]{
		{0,0,0,0},
		{0,0,1,0},
		{0,1,0,0},
		{1,0,0,0},
		{1,1,0,0},
		{1,0,1,0},
		{0,1,1,0},
		{1,1,1,1}
	};
	
	/********************** Main *********************/
	public static void main(String[] args){
		try{
			File f = new File("/home/martin/workspace/tmp/projekt1/hard.txt");
			if (f.exists()){
				hardData = Perceptron.load(f);
			}
			prvaUloha();
			tretiaUloha();
			druhaUloha();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("\nFinished.");
	}
}
