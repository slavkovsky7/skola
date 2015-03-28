package sk.mslavkovsky.nn;

import java.io.File;
import java.io.IOException;

public class Utils {
	public static boolean cmd(String cmd, boolean showOutput) throws IOException, InterruptedException{
		String commandStr = cmd;
		Process command = Runtime.getRuntime().exec(commandStr);
		new ProcessOutputConsumer(command, commandStr,showOutput).start();
		command.waitFor(); 
		return command.exitValue() == 0;
	}
	
	
	
	public static void plot(File file) throws IOException, InterruptedException{
		Utils.cmd("gnuplot -p " + file, false);
	}
}
