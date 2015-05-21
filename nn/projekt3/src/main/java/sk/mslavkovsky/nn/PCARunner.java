package sk.mslavkovsky.nn;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class PCARunner {
	
	public static List<RealVector> loadTextData(File f, int dim) throws FileNotFoundException{
		List<RealVector> result = new ArrayList<RealVector>();
		Scanner scanner = new Scanner(f);
		while (scanner.hasNext()){
			double[] vecData = new double[dim]; 	
			for (int i = 0 ; i < dim; i++){
				vecData[i] = scanner.nextDouble();
			}
			result.add(new ArrayRealVector(vecData));
		}
		scanner.close();
		return result;
	}
	
	public static void convertBitmap(File outputDir, File trainImage, File testImage, int pcaCount,  int filterWidth, int filterHeight, double alpha, int epochs) throws Exception{		
		BufferedImage img = ImageIO.read(trainImage);
		RealVector[] trainImgVectorsArr = BmpCompressor.loadBitmap(img, filterWidth, filterHeight);
		List<RealVector> trainImgVectors = Utils.toList(trainImgVectorsArr);
		PCA pca = new PCA(trainImgVectors , pcaCount );
		pca.alpha = alpha;
		pca.MAX_EPOCHS = epochs;
		pca.train();
		
		
		List<RealVector> compressedTrain = pca.compress(trainImgVectors);
		List<RealVector> reconstructedTrain = pca.reconstruct(compressedTrain);
		img = BmpCompressor.saveBitmap(reconstructedTrain.toArray(new RealVector[reconstructedTrain.size()]), filterWidth, filterHeight, img.getWidth(), img.getHeight());
		ImageIO.write(img, "bmp", new File(outputDir, "reconstructed_train.bmp"));
		
		RealVector[] testImgVectors = BmpCompressor.loadBitmap(testImage, filterWidth, filterHeight);
		List<RealVector> compressedTest = pca.compress(Utils.toList(testImgVectors));
		List<RealVector> reconstructedTest = pca.reconstruct(compressedTest);
		img = BmpCompressor.saveBitmap(reconstructedTest.toArray(new RealVector[reconstructedTest.size()]), filterWidth, filterHeight, img.getWidth(), img.getHeight());
		ImageIO.write(img, "bmp", new File(outputDir, "reconstructed_test.bmp"));
		
		List<RealVector> eigenVector = pca.getEigenVectorImages();
		for (int i = 0 ; i < eigenVector.size(); i++){
			RealVector r = eigenVector.get(i);
			img = BmpCompressor.saveBitmap(new RealVector[]{r}, filterWidth, filterHeight, filterWidth, filterHeight);
			ImageIO.write(img, "bmp", new File(outputDir, "eigen_"+i+".bmp"));
		}
		
		RealVector eigenValues = pca.getEigenValues();
		Utils.plotVector(new File(outputDir, "eigenvalues.png"), eigenValues);
		System.out.println("Eigenvalues plotted");
		System.out.println("  " + eigenValues);
		
	}
	
	private static int epochCount = 200;
	private static int pcaCount = 8;
	private static int filterWidth = 4;
	private static int filterHeight = 3;
	private static double alpha = 0.001;
	private static File train = new File("angelina.bmp");
	private static File test = new File("angelina.bmp");
	
	private static void parseProperties(File propFile) throws Exception{
		
		InputStream in = new FileInputStream(propFile);
		Properties prop = new Properties();
		prop.load(in);
		epochCount = Integer.parseInt(prop.getProperty("epoch_count")); 
		pcaCount = Integer.parseInt(prop.getProperty("pca_count"));
		filterWidth = Integer.parseInt(prop.getProperty("filter_width"));
		filterHeight = Integer.parseInt(prop.getProperty("filter_height"));
		alpha = Double.parseDouble(prop.getProperty("alpha"));
		train = new File( prop.getProperty("train"));
		test = new File( prop.getProperty("test"));
		
		
		System.out.println("Properties:");
		System.out.println("\t epochCount   : " + epochCount );
		System.out.println("\t pcaCount     : " + pcaCount );
		System.out.println("\t filterWidth  : " + filterWidth );
		System.out.println("\t filterHeight : " + filterHeight );
		System.out.println("\t alpha        : " + alpha );
		System.out.println("\t train        : " + train );
		System.out.println("\t test         : " + test );
	}
	
	public static void main(String[] args){
		try{
			parseProperties(new File("settings.properties"));
			String[] splitted = train.getName().split("\\.(?=[^\\.]+$)");
			
			File outDir = new File("generated", splitted[0]);
			outDir.mkdirs();
			convertBitmap(outDir, train , test, pcaCount, filterWidth, filterHeight, alpha, epochCount);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
