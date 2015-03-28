import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;


public class Movie implements Comparable<Movie> {
	public ArrayList<String> genres = new ArrayList<String>();
	public String title;
	public String plot = "";
	
	public float[] tfidfVect= null;
	public Movie( String title , ArrayList<String> genresIn ){
		for (String genre : genresIn){
			this.genres.add(genre);
		}
		this.title = title;
	}
	
	public Movie( String title , String plot, String[] genresIn ){
		for (String genre : genresIn){
			this.genres.add(genre);
		}
		this.title = title;
		this.plot = plot;
	}
	
	@Override
	public String toString(){
		String result = "--------------------------------------------------------------------------------\n"; 
		result += title + "<->";
		boolean first = false;
		for (String genre : genres){
			if (first){ result += ","; }
			first = true;
			result += genre; 
		}
		result += "\n" + plot;
		if ( tfidfVect != null){
			result += "\n" + Arrays.toString(tfidfVect);
		}
		return result; 
	}
	
	
	//tf-idf vektor do stringu
	public String getTfIdfString(){
		StringBuilder b = new StringBuilder();
		for (int j = 0 ; j < tfidfVect.length; j++){
			if ( tfidfVect[j] != 0d ){
				b.append( " " + (j+1)+":"+tfidfVect[j]);
			}
		}
		return b.toString();
	}
	
	//tu sa pouziva stemming a mazanie stop word listu
	public int filter() throws FileNotFoundException{
		ArrayList<String> plotWords = Utils.loadText(plot);
		plotWords = StopWordsFilter.execute(plotWords);
		plotWords = PorterStemmer.execute(plotWords);
		//plotWords = LovinsStemmer.execute(plotWords);
		plot = Utils.toText(plotWords);
		return plotWords.size();
	}
	
	public boolean containsGenre(String genre){
		genre = genre.toLowerCase();
		for (String itg : genres){
			if (itg.toLowerCase().equals(genre)){
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(Movie o) {
		return this.title.compareTo(o.title);
	}
	
	@Override
	public boolean equals(Object o ){
		Movie other = (Movie)o;
		return this.title.equals(other.title); 
	}
}
