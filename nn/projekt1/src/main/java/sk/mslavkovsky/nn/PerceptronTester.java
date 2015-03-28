package sk.mslavkovsky.nn;

import java.io.File;

import org.apache.commons.math3.linear.ArrayRealVector;

public class PerceptronTester {
	
	private static final File POS_FILE= new File("generated-pos.dat");
	private static final File NEG_FILE= new File("generated-neg.dat");
	private static final File SCRIPT_FILE= new File("generated-plot-code.gnuplot");
	
	final double[][] OR_2D = new double[][]{
		{0,0,0},
		{0,1,1},
		{1,0,1},
		{1,1,1}
	};
	
	final double[][] AND_2D = new double[][]{
		{0,0,0},
		{0,1,0},
		{1,0,0},
		{1,1,1}
	};
	
	double[][] XOR_2D = new double[][]{
		{0,0,0},
		{0,1,1},
		{1,0,1},
		{1,1,0} 
	};
	
	final double[][] OR_3D = new double[][]{
		{0,0,0,0},
		{0,0,1,1},
		{0,1,0,1},
		{1,0,0,1},
		{1,1,0,1},
		{1,0,1,1},
		{0,1,1,1},
		{1,1,1,1}
	};
	
	final double[][] AND_3D = new double[][]{
		{0,0,0,0},
		{0,0,1,0},
		{0,1,0,0},
		{1,0,0,0},
		{1,1,0,0},
		{1,0,1,0},
		{0,1,1,0},
		{1,1,1,1}
	};
	
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
	
	public static void main(String[] args){
		try{
			
			double[][] data = Perceptron.load(new File("/home/martin/workspace/tmp/projekt1/hard.txt"));
			Perceptron p = new Perceptron(data, 0.05);
			Perceptron.TrainStatistics stats = p.train(new File("train.dat"),true, true);		
			
			System.out.println(stats.toString());
			
			p.generatePlotTrainCode(PerceptronTester.SCRIPT_FILE);
			Utils.plot(PerceptronTester.SCRIPT_FILE);

			p.generatePlotCode(PerceptronTester.SCRIPT_FILE, PerceptronTester.NEG_FILE, PerceptronTester.POS_FILE, p.getInputDataDimension() >= 3 );
			Utils.plot(PerceptronTester.SCRIPT_FILE);
			
			//p.generatePlotCode2d(new File("test"), false);
			//p.plot(new File("test"));
			
			
			testPerceptron(p, data);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("\nFinished.");
	}
}
