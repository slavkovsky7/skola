import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;


public class Utils {
   public static final String REMOVE_CHARS = ",.\'\"!?()//-;:[]";		
   
   public static ArrayList<String> loadText( String text ){
	   Scanner s = new Scanner( text );
	   ArrayList<String> result = new ArrayList<String>();
	   while (s.hasNext()){
		    result.add(s.next());
	   }
	   s.close();
	   return result;
   } 
   
   public static String toText(ArrayList<String> words){
	   	String result = "";
		boolean first = false;
		for (String w : words){
			if (first){
				result += " ";
			}
			first = true;
			result += w;
		}
		return result;
   }
	
   public static ArrayList<String> loadTextFile( String filePath ) throws FileNotFoundException{
	  return loadTextFile(new File(filePath));
   } 
   
   public static ArrayList<String> loadTextFile( File filePath ) throws FileNotFoundException{
	   Scanner s = new Scanner( filePath );
	   ArrayList<String> result = new ArrayList<String>();
	   while (s.hasNext()){
		    result.add(s.next());
	   }
	   s.close();
	   return result;
   } 
   
   public static ArrayList<String> loadTextFileLines(File filePath) throws IOException{
	   String line;
	   BufferedReader br = new BufferedReader(new FileReader(filePath));
	   ArrayList<String> result = new ArrayList<String>();
	   while ((line = br.readLine()) != null) {
		   result.add(line);
	   }
	   br.close();
	   return result;
   }
   
   public static ArrayList<String> loadColumnFromTextFile( File filePath , int columntIndex) throws IOException{
		ArrayList<String> result = new ArrayList<String>();		
		String line;
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null) {
			String[] words = line.split(" ");
			if (columntIndex < words.length){
				result.add(words[columntIndex]);
			}
		}
		br.close();
	   return result;
   } 
   
   public static boolean isWord(String str){
	   for (int i = 0; i < str.length(); i++){
		   if (!Character.isLetter(str.charAt(i))){
			   return false;
		   }
	   }
	   return true;
   }
   
	
	public static String removeChars(String chars, String str){
		String result = "";
		for (int i = 0 ; i < str.length(); i++){
			String chstr = "" + str.charAt(i);
			if ( !chars.contains( chstr ) ){
				result += chstr;
			}else{
				result += " ";
			}
		}
		return result;
	}
	
	public static String removeChars( String str){
		return removeChars(REMOVE_CHARS, str);
	}
	
	public static int[] sampleRandomIndexes( int pickSize, int size ){
		int[] result = new int[pickSize ];
		HashSet<Integer> added = new HashSet<Integer>();
		Random rand = new Random();
		for (int i = 0 ; i < pickSize; i++){
			int newRandom = -1;
			do {
				newRandom = rand.nextInt(size);
			}while( added.contains(newRandom));
			result[i] = newRandom;
			added.add(newRandom);
		}
		Arrays.sort(result);
		return result;
	}
	
	//Toto je dost blbe, v podstate to iste co Porter
	/*public static String luceneStem(String str) throws ParseException{
		Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_35, "English");
		QueryParser parser = new QueryParser(Version.LUCENE_34, "", analyzer);
		return parser.parse(str).toString();
	}*/
	
	public static boolean cmd(String cmd, boolean showOutput) throws IOException, InterruptedException{
		String commandStr = cmd;
		Process command = Runtime.getRuntime().exec(commandStr);
		new ProcessOutputConsumer(command, commandStr,showOutput).start();
		command.waitFor(); 
		return command.exitValue() == 0;
	}
}
