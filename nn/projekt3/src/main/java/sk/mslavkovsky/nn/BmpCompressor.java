package sk.mslavkovsky.nn;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class BmpCompressor {
	
	
	public static RealVector[] loadBitmap(File f, int filterWidth, int filterHeight) throws IOException{
		return loadBitmap(ImageIO.read(f) , filterWidth, filterHeight);
	}
	
	public static RealVector[] loadBitmap(BufferedImage img, int filterWidth, int filterHeight) throws IOException{
		if ( img.getHeight() % filterHeight > 0 || img.getWidth() % filterWidth > 0){
			throw new RuntimeException("Invalid filter size");
		}
		
		int filterCountX= img.getWidth() / filterWidth;
		int filterCountY= img.getHeight() / filterHeight;
		
		RealVector[] result = new RealVector[ filterCountY * filterCountX];
		for (int i = 0; i < result.length; i++){
			result[i] = new ArrayRealVector(filterWidth*filterHeight);
		}
		
		for (int y = 0; y < img.getHeight(); y++){
			for (int x = 0; x < img.getWidth(); x++){
				int filterX = x / filterWidth;
				int filterY = y / filterHeight;
				int inFilterX = x % filterWidth;
				int inFilterY = y % filterHeight;
				
				int filterIndex   = filterCountX * filterY   + filterX;
				int inFilterIndex = filterWidth  * inFilterY + inFilterX;
				
				Color c = new Color(img.getRGB(x, y));
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();
				int gray = ( r +  g +  b) / 3;
				
				result[filterIndex].setEntry(inFilterIndex,gray);
			}					
		}
		return result;
	}
	
	public static BufferedImage saveBitmap(RealVector[] vectors, int filterWidth, int filterHeight, int imgWidth, int imgHeight){
		
		int filterCountX= imgWidth / filterWidth;
		int filterCountY= imgHeight/ filterHeight;
		
		if ( imgHeight % filterHeight > 0 || imgWidth % filterWidth > 0 || filterCountX * filterCountY != vectors.length){
			throw new RuntimeException("Invalid filter size");
		}
		
		BufferedImage result = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < imgHeight; y++){
			for (int x = 0; x < imgWidth; x++){
				int filterX = x / filterWidth;
				int filterY = y / filterHeight;
				int inFilterX = x % filterWidth;
				int inFilterY = y % filterHeight;
				
				int filterIndex   = filterCountX * filterY   + filterX;
				int inFilterIndex = filterWidth  * inFilterY + inFilterX;
				
				double rgb = vectors[filterIndex].getEntry(inFilterIndex);
				rgb = Math.max(0, rgb);
				rgb = Math.min(255, rgb);
				//rgb = Math.min(rgb*0.9d,255d);
				Color c = new Color((int)rgb,(int)rgb,(int)rgb);
				result.setRGB(x, y, c.getRGB());
			}					
		}
		return result;
	}
}
