package sk.mslavkovsky.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Utils {
	public static boolean cmd(String cmd, boolean showOutput, boolean wait_for, File dir) throws IOException, InterruptedException{
		String commandStr = cmd;
		Process command = Runtime.getRuntime().exec(commandStr);
		new ProcessOutputConsumer(command, commandStr,showOutput).start();
		if (wait_for){
			command.waitFor();
			return command.exitValue() == 0;
		}
		return true;
	}
	
	
	public static boolean cmd(String cmd, boolean showOutput, boolean wait_for) throws IOException, InterruptedException{
		return cmd(cmd, showOutput,wait_for, null);
	}
	
	public static boolean cmd(String cmd, boolean showOutput) throws IOException, InterruptedException{
		return cmd(cmd, showOutput, true);
	}
	
	
	
	public static void plot(File file) throws IOException, InterruptedException{
		Utils.cmd("gnuplot -p " + file, true, true);
	}

	public static void plotVector(File output, RealVector v) throws IOException, InterruptedException{
		
		//File parent = output == null ? new File("") : output.getParentFile();
		
		File dataFile = new File(  "generated.data");
		PrintWriter writer = new PrintWriter(dataFile);
		for (int i = 0; i < v.getDimension(); i++){
			writer.println(v.getEntry(i));
		}
		writer.close();
	
		File scriptFile = new File( "generated-code.gnuplot") ;
		PrintWriter pw = new PrintWriter( scriptFile );
		
		if ( output != null ) {
			pw.println( "set terminal png size 1024,768");
			pw.println( "set output '"+output.getAbsoluteFile()+"'");
		}
		
		pw.println("set xlabel \"X axis\" ");
		pw.println("set ylabel \"Y axis\" ");
		
		String plotCmd = "plot \""+dataFile.getAbsoluteFile() +"\" with lines";
		pw.println(plotCmd);

		
		pw.close();
		
		Utils.plot(scriptFile);
	}
	
	public static List<RealVector> deepCopy(List<RealVector> source) {
	    List<RealVector> result = new ArrayList<RealVector>();
	    for(RealVector el : source) {
	        result.add( el.copy() );
	    }
	    return result;
	}
	
	public static List<RealMatrix> deepCopyMat(List<RealMatrix> source) {
	    List<RealMatrix> result = new ArrayList<RealMatrix>();
	    for(RealMatrix el : source) {
	        result.add( el.copy() );
	    }
	    return result;
	}
	
	public static double average(List<Double> l){
		double result = 0;
		for (Double d : l){
			result+= d;
		}
		return result / l.size();
	}
	
	public static double mean(RealVector v){
		double result = 0;
		for (int i = 0 ; i < v.getDimension(); i++){
			result+= v.getEntry(i);
		}
		return result / v.getDimension();
	}
	
	public static double mse(List<RealVector> l1, List<RealVector> l2){
		double result = 0;
		for (int i = 0 ; i < l1.size(); i++){
			result += l1.get(i).subtract(l2.get(i)).getNorm();
		}
		return result;
	}
	
	public static String toString(List<Double> d){
		String result = "";
		for (int i = 0 ; i < d.size();i++){
			if ( i > 0 ){
				result += ", ";
			}
			result =result + d.get(i).floatValue();
		}
		return result;
	}
	
	public static List<RealVector> toList(RealVector[] data){
		List<RealVector> result = new ArrayList<RealVector>();
		for (int i = 0; i < data.length; i++ ){
			result.add( data[i].copy() );
		}
		return result;
	}
}
