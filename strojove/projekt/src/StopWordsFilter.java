import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

//filtruej stop-words slova list, ktory je nacitany zo suboru a vymaze slova ako a,an,the,which atd...
public class StopWordsFilter {
	
	private HashSet<String> stopWords = new HashSet<String>();

	public StopWordsFilter(String filterPath) throws FileNotFoundException{
		ArrayList<String> stopWordsInput = Utils.loadTextFile(filterPath);
		for ( String w : stopWordsInput){
			stopWords.add(w);
		}
	}
	
	
	public StopWordsFilter() throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("stopwords.txt").getFile());
		ArrayList<String> stopWordsInput = Utils.loadTextFile(file);
		for ( String w : stopWordsInput){
			stopWords.add(w.toLowerCase());
		}
	}

	
	public ArrayList<String> filterText(ArrayList<String> text){
		ArrayList<String> result = new ArrayList<String>();
		for (String w : text){
		   w = w.toLowerCase();
		   w = Utils.removeChars( Utils.REMOVE_CHARS , w).trim();
		   String[] splitted = w.split(" ");
		   for (String sw : splitted){
			   sw = sw.trim();
			   if (!stopWords.contains(sw) && Utils.isWord(sw)){
				   result.add(sw);
			   }
		   }
		}
		return result;
	}
	
	public static ArrayList<String> execute(String filterPath , ArrayList<String> text ) throws FileNotFoundException{
		StopWordsFilter filter = new StopWordsFilter(filterPath);
		return filter.filterText(text);
	}
	
	public static ArrayList<String> execute(ArrayList<String> text ) throws FileNotFoundException{
		StopWordsFilter filter = new StopWordsFilter();
		return filter.filterText(text);
	}
}
