package sk.mslavkovsky.zui2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public static double[] toArray(List<Double> list){
		double[] result = new double[list.size() ];
		for (int i = 0; i < list.size(); i++){
			result[i] = list.get(i);
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
	
	public static double[][] splitArray(double[] input, int index){
		double[][] result = new double[2][];
		result[0] = Arrays.copyOfRange(input, 0, index);
		result[1] = Arrays.copyOfRange(input, index, input.length);
		return result;
	}
}