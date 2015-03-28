import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Learner {

	
	public static final int GENRE_LIMIT = 500;
	public static final int MIN_BOW_WORD_COUNT = 10;
	public static final int CPU_COUNT = 4;
	public static final int TOP_WORDS_COUNT = 10;
	public static final int TOP_OTHER_GENRES_COUNT = 10;
	

	private File imdbGenreFile; 
	private File imdbPlotFile; 
	private File distDir;
	private File svmFilesDir; 
	private File libsvmDir;
	private int distSize;
	private int maxGenresPerMovie;
	private File genreFile;
	private File genreFileTrans;
	
	private File weightsFile;
	
	private File svmTrain;
	private File svmPredict;
	
	public File getPlainFile(String genre)  { return (genre == null) ? new File(distDir, "plain.txt") 	 : new File(distDir, genre + "_plain.txt") ;}
	public File getStemmedFile(String genre){ return (genre == null) ? new File(distDir, "stemmed.txt")  : new File(distDir, genre + "_stemmed.txt");}
	public File getTrainFile(String genre)	{ return (genre == null) ? new File(svmFilesDir,"train.svm") : new File(svmFilesDir, genre + "_train.svm");}
	public File getTestFile	(String genre)	{ return (genre == null) ? new File(svmFilesDir,"test.svm")  : new File(svmFilesDir, genre + "_test.svm");}
	public File getTransFile()				{ return new File(svmFilesDir,"trans.svm");}
	public File getModelFile(String genre)	{ return (genre == null) ? new File(svmFilesDir,"train.svm.model")  : new File(svmFilesDir, genre + "_train.svm.model");}
	public File getPredicFile(String genre) { return (genre == null) ? new File(svmFilesDir,"predicted.o")  : new File(svmFilesDir, genre + "_predicted.o");};

	private String useGenres;
	private boolean showLibSvmOutput;
	
	//Konstruktor , nacita niektore nastavanie z mc.properties suboru. Vysvetlenia mozno
	//najst priamo v subore
	public Learner(File propFile) throws IOException{
		Properties prop = new Properties();
		InputStream in = new FileInputStream(propFile);
		prop.load(in);
		imdbGenreFile = new File( prop.getProperty("imdb_genre_file") );
		imdbPlotFile = new File( prop.getProperty("imdb_plot_file") );
		distDir = new File( prop.getProperty("distribution_dir") );
		svmFilesDir = new File( prop.getProperty("svm_files_dir") );
		libsvmDir = new File( prop.getProperty("lib_svm_dir") );
		distSize = Integer.parseInt(  prop.getProperty("distribution_size") );
		

		maxGenresPerMovie = Integer.parseInt(  prop.getProperty("max_genre_per_movie") );
		useGenres = prop.getProperty("use_genres");
		showLibSvmOutput = Boolean.parseBoolean(prop.getProperty("lib_svm_output"));
		in.close();
		
		/**************prop end**************/
		
		weightsFile = new File(svmFilesDir, "weights.txt" );
		genreFile = new File(distDir, "genres.txt");
		genreFileTrans = new File(distDir,"genres_trans.txt");
		svmTrain = new File(libsvmDir , "svm-train");
		svmPredict = new File(libsvmDir , "svm-predict");

		
	}
	
	//Sparsuje subor ktory obsahuje zanry filmov a ich nazvy. Najprv treba sparsovat tento
	//neskor pouzite spolu s funkciou parseImdbFilePlot
	public ArrayList<Movie> parseImdbGenreFile(File file, ArrayList<String> allGenres ) throws IOException{
		ArrayList<Movie> allMovies = new ArrayList<Movie>();
		
		//Scanner scanner = new Scanner(imdbGenreFile);
		String prevTitle = null;
		
		ArrayList<String> tmpGenreList = new ArrayList<String>();
		
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			
			String w[] = line.split("\t");
			String genre = w[ w.length - 1 ].toLowerCase();
			
			
			String movieTitle = "";
			for (int i = 0 ; i < w.length -1 ; i++){
				if ( !w[i].equals("")){
					if (i > 0){
						movieTitle += "";
					}
					movieTitle += w[i];
				}
			}
			
			if (prevTitle != null && !prevTitle.equals(movieTitle) ){
				Movie movie = new Movie(prevTitle, tmpGenreList);
				if (tmpGenreList.size() <= maxGenresPerMovie){
					allMovies.add ( movie );
				}
				tmpGenreList.clear();
			}
			
			if (!allGenres.contains(genre) ){
				allGenres.add(genre);
			}
			tmpGenreList.add(genre);
			prevTitle = movieTitle;
		}
		br.close();
		//scanner.close();
		
		if (allGenres != null){
			Collections.sort(allGenres);
		}
		
		ArrayList<Movie> result = allMovies;
		return result;
	} 
	
	//vyberie nahodnu mnozinu z pola filmov
	public ArrayList<Movie> sampleRandomMovies(ArrayList<Movie> list, int pickSize ){
		ArrayList<Movie> result = new ArrayList<Movie>();
		int sampled[] = Utils.sampleRandomIndexes( Math.min(pickSize, list.size()) , list.size());
		for ( int index : sampled){
			result.add( list.get(index) );
		}
		return result;
	}
	
	//parsuje imdb subor s nazvami a obsahmi filmov
	public ArrayList<Movie> parseImdbFilePlot( File file, ArrayList<Movie> list ) throws IOException{
		
		ArrayList<Movie> result = new ArrayList<Movie>();
		
		int listIndex = 0;
		String plot = "";
		
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		boolean readingPlot = false;
		
		while ((line = br.readLine()) != null) {
			if (line.length() > 3){
				String lineTag = line.substring(0, 3); 
				line = line.split(lineTag)[1].trim();
				if ( lineTag.equals("MV:") ){
					if ( readingPlot ){
						list.get(listIndex).plot = plot;
						result.add(list.get(listIndex)); 
						
						plot = "";
						//listIndex++;	
						readingPlot = false;
					}
					
					
					boolean end = false;
					while ( line.compareTo( list.get(listIndex).title ) > 0){
						listIndex++;
						if (listIndex >= list.size() ){
							end = true;
							break;
						}
					}
					
					if (end) break;

					if (line.equals( list.get(listIndex).title ) ){
						readingPlot = true;
					}
				}else if ( readingPlot && lineTag.equals("PL:") ){
					plot += line+ " ";	
				}
			}
		}
		br.close();
		
		return result;
	}

	//Vygeneruje N = |Genres| suborov v data/dist_data/$GENRE_plain.txt
	//kazdy takyto subor obsahuje polovicu filmov ktore patria do zanru a druhu
	//polovicu, ktora tam nepatri, pouzite pri moznosti -gen-bdist
	public void generateBinaryDistributions()throws IOException{
		System.out.println("Generating binary distributions...");
		System.out.println("Parsing imdb genre file...");
		ArrayList<String> genres = new ArrayList<String>();
		ArrayList<Movie> list = parseImdbGenreFile(imdbGenreFile, genres);
		System.out.println("Parsing imdb plot for "+list.size()+ " movies...");
		list = parseImdbFilePlot(imdbPlotFile, list);
		genres = filterGenreList(genres);
		ArrayList<String> skippedGenres = new ArrayList<String>();
		for (String genre : genres ){;
			System.out.println("Generating plain file for " + genre ) ;
			ArrayList<Movie> genreMovies   = new ArrayList<Movie>(list.size() / 2 );
			ArrayList<Movie> nonGenreMovies= new ArrayList<Movie>(list.size() / 2 );
		
			
			for (Movie m : list){
				if (m.containsGenre(genre)){
					genreMovies.add(m);
				}else{
					nonGenreMovies.add(m);
				}
			}
			int genreTotalCount = genreMovies.size();
			if (genreTotalCount < GENRE_LIMIT){
				System.out.println("Skipping genre as there are only " + genreTotalCount + " movies in db");
				skippedGenres.add(genre);
				continue;
			}
			int randomSampleCount = Math.min(genreTotalCount, distSize / 2 );
			genreMovies = sampleRandomMovies(genreMovies, randomSampleCount );
			nonGenreMovies = sampleRandomMovies(nonGenreMovies, randomSampleCount );
			
			genreMovies.addAll(nonGenreMovies);
			Collections.sort(genreMovies);
			
			PrintWriter pw = new PrintWriter(getPlainFile(genre));
			
			for (Movie m : genreMovies){
				pw.println(m.toString());
			}
			System.out.println("Generated movies count = " + genreMovies.size() + ", genreTotalImdbMovie = " + genreTotalCount);
			pw.close();
		}
		genres.removeAll(skippedGenres);
		saveGenreList(genreFile , genres);
		
	}
			
	//Filtruje jednotlive data/dist_data/$GENRE_plain.txt pomocou Stemmera 
	//a maze stopwords
	public void filterBinaryDistribution() throws IOException{
		ArrayList<String> genres = getGenreList();
		for (String genre : genres){
			System.out.println(genre + " : Filtering distribution" );
			ArrayList<Movie> list = loadDistritubion( getPlainFile(genre) );	
			PrintWriter pw = new PrintWriter(getStemmedFile(genre));
	
			for (Movie m : list){
				m.filter();
				pw.println(m.toString());
			}			
			pw.close();
		}
	}

	// podobne ako generateBinaryDistributions ale generuje len jeden subor s
	// nazvom plain.txt. Nekontroluje kolko % z ktoreho zanra sa nachadza v
	// mnozine. Vybera uplne nahodne
	public void generateTransDistribution()throws IOException{
			System.out.println("Generating distribution...");
			System.out.println("Parsing imdb genre file...");
			ArrayList<String> genres = new ArrayList<String>();
			ArrayList<Movie> list = parseImdbGenreFile(this.imdbGenreFile,genres);
			System.out.println("Parsing imdb plot...");
			list = parseImdbFilePlot(this.imdbPlotFile, list);
			list = sampleRandomMovies(list, distSize );
			
			System.out.println("Generating plain distribution file...");
			PrintWriter pw = new PrintWriter( getPlainFile(null) );
			
			for (Movie m : list){
				pw.println(m.toString());
			}
			System.out.println("Generated movies count = " + list.size());
			pw.close();
			
			saveGenreList(genreFileTrans, genres);
	}
	
	//naloaduje filmy z $GENRE_plain.txt alebo $GENRE_stemmed.txt
	public static ArrayList<Movie> loadDistritubion(File file) throws IOException
	{
		ArrayList<Movie> result = new ArrayList<Movie>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		int count = 0;
		String title = "";
		String plot = "";
		while ((line = br.readLine()) != null) {
			if (count % 3 == 1){
				title = line;
			}else if (count % 3 == 2){
				plot = line;
				
				String[] strTmp = title.split("<->");
				
				title = strTmp[0];
				String[] genres = strTmp[1].split(",");
				
				result.add(new Movie(title, plot, genres));
				
				title = "";
				plot = "";
			}
			count++;
		}
		br.close();
		return result;
	}
	
	private HashMap<String, Integer> getWordCounter(ArrayList<Movie> movies){
		return getWordCounter(movies, null);
	}
	
	//vrati mapu , ktorej sa vieme spytat kolkokrat sa nachadza nejake slovo 
	//nachadza v celej mnozine dokumentov
	private HashMap<String, Integer> getWordCounter(ArrayList<Movie> movies, String obligatoryGenre){
		HashMap<String, Integer> wordCounter = new HashMap<String, Integer>();
		for (int i = 0; i < movies.size(); i++){
			Movie m = movies.get(i);
			if (obligatoryGenre != null && !m.containsGenre(obligatoryGenre)){
				continue;
			}
			ArrayList<String> words = Utils.loadText(m.plot);
			for (String w : words){
				Integer wordCount =  wordCounter.get(w);
				if (wordCount == null){
					wordCounter.put(w,1);
				}else{
					wordCounter.put(w, wordCount + 1);
				}
			}
		}
		return wordCounter;
	}
	
	// vyrobi Bow reprezentaciu 
	public HashMap<String, Integer> generateBowDictionary(int minWordCount, ArrayList<Movie> movies, ArrayList<String> outBowIndexed ){
		HashMap<String, Integer> wordCounter = getWordCounter(movies);
		
		int totalCount = 0;
		ArrayList<String> sortList = outBowIndexed;
		for (Map.Entry<String, Integer> entry : wordCounter.entrySet()) {
			totalCount += entry.getValue();
			if ( entry.getValue() >= minWordCount ){
				sortList.add(entry.getKey());
			}
		}
		
		Collections.sort(sortList);
		
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		int i = 0;
		for (String w : sortList){
			result.put(w, i);
			i++;
		}
		
		System.out.println("Pocet vsetkych slov = " + totalCount + ", unikatne slova = " + wordCounter.size() + ", vysledny bow = " + result.size());
		return result;
	}
	
	
	// tf-idf(word, synopsis) = (word count in movie synopses / plot.length) * log *  ( synopses count / synopses which contain word );
	// vypocita tf-idf vektory pomocou BoW
	public void generateMovieVectors(ArrayList<Movie> movies, ArrayList<String> bowIndexed, HashMap<String, Integer> bow){
		
		
		float[][] bowMatrix = new float[movies.size()][];
		int[] plotSizes = new int[movies.size()];
	
		for (int i = 0 ; i < movies.size(); i++){
			Movie movie = movies.get(i);
			
			ArrayList<String> plotWords = Utils.loadText(movie.plot);
			bowMatrix[i] = new float[bowIndexed.size()];
			plotSizes[i] = plotWords.size();
			
			for (String word : plotWords){
				Integer bowWordIndex = bow.get(word);
				if (bowWordIndex!= null){
					bowMatrix[i][bowWordIndex]++;
				}
			}
			//movie.plotVect = plotVector;
		}
		
		int[] nonZeroColumns = new int[bowIndexed.size()];
		for (int j = 0; j < nonZeroColumns.length; j++){
			for (int i = 0; i < bowMatrix.length; i++){
				nonZeroColumns[j] += Math.min( 1, bowMatrix[i][j] );
			}
		}
		
		
		for (int i = 0; i < bowMatrix.length; i++){
			float length = 0;
			for (int j = 0; j < bowMatrix[i].length; j++){
				bowMatrix[i][j] = ( (float)bowMatrix[i][j] / (float)plotSizes[i] ) * (float)Math.log10( (float)movies.size() / (float)nonZeroColumns[j] );
				
				length += Math.pow( bowMatrix[i][j] , 2);
			}
			length = (float)Math.sqrt(length);
			
			//Unit Vector
			for (int j = 0; j < bowMatrix[i].length; j++){
				bowMatrix[i][j] = bowMatrix[i][j] / length;
			}
			
			movies.get(i).tfidfVect = bowMatrix[i];
		}
	}

	//Rozdeli uz finalnu mnozinu do testovacich(20%) a trenovacich(80%) podmonozin
	private HashSet<Integer> getTrainIndexes(ArrayList<Movie> movies){
		int[] trainIndexes = Utils.sampleRandomIndexes( (movies.size() / 10) * 8 , movies.size());
		HashSet<Integer> result = new HashSet<Integer>();
		for (int index : trainIndexes){
			result.add(index);
		}
		trainIndexes = null;
		return result;
	}
	
	//Vytvori SVM subory pouzivane binarnymi subormi svm-train, svm-predict
	//pouziva sa moznostou -svmt , riesi Label Combination approach
	public void generateTransformSVM() throws IOException{
		
		ArrayList<Movie> movies = loadDistritubion( getStemmedFile(null) );
		ArrayList<String> bowIndexed = new ArrayList<String>();
		HashMap<String, Integer> bow = generateBowDictionary(MIN_BOW_WORD_COUNT, movies, bowIndexed);
		ArrayList<String> genres = getGenreListTrans();
		
		System.out.println("Generating transform svm vectors...");
		generateMovieVectors(movies, bowIndexed, bow);
		
		System.out.println("Generating transform svm files...");
		
		PrintWriter pwTest = new PrintWriter(new FileWriter(getTestFile(null)));
		PrintWriter pwTrain = new PrintWriter(new FileWriter(getTrainFile(null)));
		PrintWriter pwTrans = new PrintWriter(new FileWriter(getTransFile()));
		
		
		HashMap<String, Integer> genreIndexes = new HashMap<String, Integer>();
		int c = 0;
		for (String genre : genres){
			genreIndexes.put(genre, c);
			c++;
		}

		HashSet<Integer> trainIndexesSet = getTrainIndexes(movies);			
		
		int transIndexesCount = 0;
		HashMap<String, Integer> genreTransIndexesMapper = new HashMap<String, Integer>();
		
		for (int i = 0 ; i < movies.size(); i++){
			Movie movie = movies.get(i);
			PrintWriter pw = trainIndexesSet.contains(i) ? pwTrain : pwTest;
	
			String genresIndexesString = "";
			for (int j = 0; j < movie.genres.size(); j++){
				if ( j > 0){
					genresIndexesString += ",";
				}
				genresIndexesString += genreIndexes.get( movie.genres.get(j));
			}
			
			
			Integer genresIndex = genreTransIndexesMapper.get(genresIndexesString);
			if (genresIndex == null){
				genresIndex = transIndexesCount;
				genreTransIndexesMapper.put(genresIndexesString,genresIndex);
				pwTrans.println(genresIndex +  "->" + genresIndexesString);
				transIndexesCount++;
			}
		
			pw.println(genresIndex +" "+ movie.getTfIdfString() );
		}
		
		
		pwTest.close();
		pwTrain.close();
		pwTrans.close();
	}
	
	//Spocita Accuracy,Precision,Recall,F-mesaure zo suboru predicted.o a
	//test.svm pre BinaryApproach
	public void mesaureBinary(File test, File predicted ) throws IOException{
		ArrayList<String> actualValues = Utils.loadColumnFromTextFile(test,0);
		ArrayList<String> predictions = Utils.loadTextFile(predicted);
		
		if (actualValues.size() != predictions.size()){
			System.out.println("Files not equal , cannot compare");
		}
		
		int truePositives  = 0;
		int falseNegatives = 0;
		
		int trueNegatives  = 0;
		int falsePositives = 0;
				
		for (int i = 0; i < actualValues.size(); i++){	
			//Condition positive
			if ( actualValues.get(i).equals("1") ){
				if (predictions.get(i).equals( actualValues.get(i))){
					truePositives++;
				}else{
					falseNegatives++;
				}
			//Condition negative
			}else{
				if (predictions.get(i).equals( actualValues.get(i))){
					trueNegatives++;
				}else{
					falsePositives++;
				}
			}
		}
		double accuracy = (double)( truePositives + trueNegatives) / (double) (truePositives + falsePositives + trueNegatives + falseNegatives);
		double precision = (double)truePositives / (double) (truePositives + falsePositives);
		double recall = (double)truePositives / (double) (falseNegatives + truePositives);
		double fmesaure = (double)(2*precision*recall)/ (precision + recall);
		System.out.println("Accuracy  = " + toPercentage(accuracy));
		System.out.println("Precision = " + toPercentage(precision));
		System.out.println("Recall 	  = " + toPercentage(recall));
		System.out.println("F-mesaure = " + toPercentage(fmesaure));
	}
	
	private String toPercentage(double d){
		return String.format("%.2f", (d*100))+ "%";
	}
	
	
	// Pre label approach spocita na zaklade tresholdu presnost pre subory predicted.o, test.o
	// ak je treshold nastaveny na 0.5, tak polovica predpovedanych musi byt predpovedana OK aby to bolo povazovane
	// za korektnu predikciu
	public void measureTrans( float rowOkTreshold ) throws IOException{
		ArrayList<String> actualValues = Utils.loadColumnFromTextFile(getTestFile(null),0);
		ArrayList<String> predictions = Utils.loadTextFile(getPredicFile(null));
		ArrayList<String> transClasses = Utils.loadTextFile(getTransFile());
		

		HashMap<Integer, String> transToActual = new HashMap<Integer,String>();
		HashMap<String, Integer> actualToTrans  = new HashMap<String, Integer>();
		for (String word : transClasses){
			String[] splitted = word.split("->");
			Integer index = Integer.parseInt(splitted[0]);
			String actualGenres = splitted[1];
			
			transToActual.put(index, actualGenres);
			actualToTrans.put(actualGenres,index);
		}
		
		
		if (actualValues.size() != predictions.size()){
			System.out.println("Files not equal , cannot compare");
		}
		
		int predictedRowsCount = 0;
		for (int i = 0; i < actualValues.size(); i++){
			Integer actualIndex = Integer.parseInt(actualValues.get(i));
			Integer predictedIndex = Integer.parseInt(predictions.get(i));
			String[] actualRow    = transToActual.get(actualIndex ).split(",");
			String[] predictedRow = transToActual.get(predictedIndex ).split(",");
			
			int predictedOkElCount = 0;
			for ( String predictedRowEl : predictedRow){
				for (String actualRowEl : actualRow){
					if ( predictedRowEl.equals( actualRowEl ) ){
						predictedOkElCount++; 
					}
				}				
			}
			
			//String s1 = transToActual.get(actualIndex );
			//String s2 = transToActual.get(predictedIndex );
			if ( (double)predictedOkElCount / (double)predictedRow.length >= rowOkTreshold){
				predictedRowsCount++;
				//System.out.println( s1 +" - " +s2 + " match");
			}else{
				//System.out.println( s1 +" - " +s2 +" don't match");
			}
		}
		
		double precision = (double)predictedRowsCount / (double)predictions.size();
		System.out.println("Precision = " + precision);
	}
	
	/////////Thread
	private class WorkerThread implements Runnable {
	    private final String genre;
	    public String weightLine = "";
	    
		public WorkerThread(String genre){
	        this.genre = genre;	        
	    }

	    @Override
	    public void run() {
	    	try{
				System.out.println("Loading distribution for " + genre);
				ArrayList<Movie> movies = loadDistritubion(getStemmedFile(genre));
				ArrayList<String> bowIndexed = new ArrayList<String>();
				HashMap<String, Integer> bow = generateBowDictionary(MIN_BOW_WORD_COUNT, movies, bowIndexed);
				System.out.println("Generating svm "+movies.size()+" vectors for " + genre);
				
				generateMovieVectors(movies, bowIndexed, bow);
				
				
				HashSet<Integer> trainIndexesSet = getTrainIndexes(movies);
				System.out.println("Generating svm files for " + genre);
				int positives = 0;
				PrintWriter pwTest = new PrintWriter(getTestFile(genre));
				PrintWriter pwTrain = new PrintWriter(getTrainFile(genre));
				for (int i = 0 ; i < movies.size(); i++){
					Movie movie = movies.get(i);
					PrintWriter pw = trainIndexesSet.contains(i) ? pwTrain : pwTest;
					int cls = movie.containsGenre(genre) ? 1 : -1;
					if (cls == 1 && trainIndexesSet.contains(i) ){
						positives++;
					}
					pw.println(cls +" "+ movie.getTfIdfString() );
				}
				
				int neg = ( trainIndexesSet.size() - positives);
				weightLine = new String(genre+"->"+positives+","+  neg);
				
				System.out.println(genre + "Positive->Negative counts are " + positives + "->" + neg );
				pwTest.close();
				pwTrain.close();
				
	    	}catch (IOException ex){
	    		ex.printStackTrace();
	    	}
	    }
	}
	
	//Pre binary approach pocita SVM subory , podobne ako generateTransformSVM
	//akurat tu sa vytvoria $GENRE_train.svm, $GENRE_test.svm s uz vybalancovanymi datami
	public void generateBinarySVMs() throws IOException{
		ArrayList<String> genres = getGenreList();
		
		//HashMap<String, Movie> alreadyGenerated = new HashMap<String, Movie>();
		
		ExecutorService executor = Executors.newFixedThreadPool(CPU_COUNT);
		
		HashMap<String, WorkerThread> workers = new HashMap<String, Learner.WorkerThread>();
		for (String genre : genres) {
			WorkerThread worker = new WorkerThread(genre);
			workers.put(genre, worker);
			executor.execute(worker);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

		PrintWriter weights_file = new PrintWriter(weightsFile);
		for (String genre : genres) {
			weights_file.println(  workers.get(genre).weightLine );
		}
		weights_file.close();
		
	}
	
	//ulozi list zanrov do data_dist/genres.txt
	public void saveGenreList(File genreFile , ArrayList<String> genres ) throws IOException{
		PrintWriter pw = new PrintWriter(genreFile);
		for ( String genre : genres){
			pw.println(genre);
		}
		pw.close();
	}
	
	//filtruje list zanrov, ak chceme pracovat len s action napriklad musime
	//zmenit mc.properties subor, konkretne use_genres
	private  ArrayList<String> filterGenreList( ArrayList<String> loaded ){
		ArrayList<String> result = new ArrayList<String>();
		if ( !useGenres.equals("all")){
			String[] usedGenres = useGenres.split(",");
			
			for (String s : usedGenres){
				s = s.toLowerCase();
				if (loaded.contains(s)){
					result.add(s);
				}
			}
		}else{
			result = loaded;
		}
		return result;
	}
	
	public ArrayList<String> getGenreList() throws IOException{
		ArrayList<String> loaded = Utils.loadTextFileLines(genreFile);
		return filterGenreList(loaded);
	}
	
	public ArrayList<String> getGenreListTrans() throws IOException{
		return Utils.loadTextFileLines(genreFileTrans);
	}
	
	//velmi to nema zmysel, loaduje vahy pre Binary Approach, tie su vsak takmer 1:1
	private void loadWeights(File f, ArrayList<Integer> positive, ArrayList<Integer> negative ) throws IOException{
		ArrayList<String> lines = Utils.loadTextFileLines( f );
		for (String line : lines){
			String[] strSplitted = line.split("->");
			strSplitted = strSplitted[1].split(",");
			
			positive.add(Integer.parseInt(strSplitted[0]));
			negative.add(Integer.parseInt(strSplitted[1]));
		}
	}
	
	//spusta binarny subor svm-train. Ten je v libsvm adresari
	private boolean train(double cost , double gama, int[] classes , double[] weights, int type, File train, File model, boolean quiet) throws IOException, InterruptedException{
		String weightsStr= "";
		if (weights != null){
			for (int i = 0 ; i < weights.length; i++){
				if (i > 0){
					weightsStr += " ";
				}
				weightsStr += "-w"+classes[i]+ " " + weights[i]; 
			}
		}
		String q = quiet ? " -q" : "";
		String command = svmTrain + q +" -c "+cost+" -g "+gama+" "+weightsStr+" -t "+type+" " + "-m 2048 " + train + " " + model; 
		return Utils.cmd( command , showLibSvmOutput);
	}
	
	//spusta binarny subor svm-predict. Ten je v libsvm adresari
	private boolean predict (File test, File model, File predicted) throws IOException, InterruptedException{
		String command = svmPredict + " " + test + " " + model + " " + predicted; 
		return Utils.cmd( command , showLibSvmOutput);
	}

	//natrenuje postupne vsetky $GENRE_train.svm subory. Vysledok uklada to
	//$GENRE_train.svm.model
	public void trainBinarySVMs() throws IOException, InterruptedException{
		ArrayList<Integer> positives = new ArrayList<Integer>();
		ArrayList<Integer> negatives = new ArrayList<Integer>();
		loadWeights(weightsFile, positives, negatives);
		ArrayList<String> genres = getGenreList();
		for ( int i = 0; i < genres.size(); i++){
			System.out.println("----------------------------------");
			String genre = genres.get(i).toLowerCase();
			
			File test  = getTestFile(genre);
			File train = getTrainFile(genre);
			File predicted = getPredicFile(genre);

			double w1 = (double)negatives.get(i) / (double)positives.get(i);
			double w0 = (double)positives.get(i) / (double)negatives.get(i);
			
			
			System.out.println( genre + " : training and predicting w1 = " + w1 +" w0 = " + w0);
			File model = getModelFile(genre);
			
			
			if ( train(2d, 2d, new int[]{-1,1}, new double[]{w0,w1}, 2, train, model, false) ){
				if ( predict(test, model, predicted)){
					mesaureBinary(test, predicted);
				}else{
					System.out.println(genre +" : predicting has failed" );
					break;	
				}
			}else{
				System.out.println(genre+ " : training  has failed" );
				break;
			}
			//break;
			
		}
	}
	
	public static class TopWordElement implements Comparable<TopWordElement>{
		public String word;
		public int count;
		public TopWordElement(String word, int count){
			this.word = word;
			this.count = count;
		}
		@Override
		public int compareTo(TopWordElement o) {
			return Integer.compare(this.count, o.count);
		}
		
		public static String queueToString(PriorityQueue<TopWordElement> queue){
			String result = "";
			int i = 0;
			while(!queue.isEmpty()){
				TopWordElement tw = queue.poll();
		
				String tmp = result;
				result = tw.word + "("+tw.count+")";
				if (i > 0){
					result += ",";
				}
				result += tmp;
				i++;
			}
			return result;
		}
		
		public static PriorityQueue<TopWordElement> getTopElements(HashMap<String, Integer> map, int topCount){
			PriorityQueue<TopWordElement> queue = new PriorityQueue<Learner.TopWordElement>();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				queue.add(new TopWordElement(entry.getKey(), entry.getValue()));
				if (queue.size() > topCount){
					queue.poll();
				}
			}
			return queue;
		}
	}
	
	
	// vypocita niektore zaujimave statistiky ako pocet priemerny zanrov na film
	// priemerny pocet slov v obsahu filmov isteho zanru
	// tiez metoda accuracy, precisiion atd.
	// pusta sa cez -mb
	public void showTextStatisticsBinary(int topWordsCount, int topOtherGenresCount) throws IOException{
		ArrayList<String> genres = getGenreList();
		for (String genre : genres){
			
			ArrayList<Movie> movies = loadDistritubion(getStemmedFile(genre));
			HashMap<String, Integer> wordCounter = getWordCounter(movies, genre);
			
			
			PriorityQueue<TopWordElement> queue = TopWordElement.getTopElements(wordCounter, topWordsCount);
			String mostCommonWords = TopWordElement.queueToString(queue);
			
			HashMap<String, Integer> otherGenresCounter = new HashMap<String, Integer>();
			
			int mc = 0;
			int	total = 0;		
			int plotSizes = 0;
			for (Movie m : movies){
				if (m.containsGenre(genre)){
					mc++;
					total += m.genres.size();
					//counting other genres in 'genre' movies
					for (String og : m.genres){
						if (!og.equals(genre)){
							Integer n = otherGenresCounter.get(og);
							n = n == null ? 0 : n;
							otherGenresCounter.put(og, n+1);				
						}
					}
				}
				plotSizes += Utils.loadText(m.plot).size();
			}
			
			PriorityQueue<TopWordElement> topOtherGenresQueue = TopWordElement.getTopElements(otherGenresCounter, topOtherGenresCount);
			String otheGenres = TopWordElement.queueToString(topOtherGenresQueue);
			
			double genresPerMovie = (double)total/(double)mc;
			double averagePlotSize = (double)plotSizes/(double)movies.size();		

			System.out.println("--------------- "+genre + "---------------");
			File predicted = getPredicFile(genre);
			File test  = getTestFile(genre);
			mesaureBinary(test, predicted);
			System.out.println("Genres pre movie  = " + genresPerMovie);
			System.out.println("Average plot size = " + averagePlotSize);
			System.out.println("Most common words = " + mostCommonWords);
			System.out.println("Other genres      = " + otheGenres);
		}
	}
	
	// podobne ako trainBinarySVM len pocita pre Label Combination approach
	// vysledok ulozi do train.svm.model
	public void trainTransSVM() throws IOException, InterruptedException{
		File train = getTrainFile(null);
		File test  = getTestFile(null);
		File model = getModelFile(null);
		System.out.println("Training trans svm model ..");
		boolean result = train(2d, 2d, null, null, 0, train, model, true);
		if ( result ){
			 System.out.println("Predicting svm model ..");
			 predict(test, model, getPredicFile(null));
		}else{
			System.out.println("Training failed");
		}
	}
	
	//vyfiltruje plain.txt na stemmed.txt
	public void filterTransDistribution() throws IOException{
		System.out.println("Loading distribution...");
		ArrayList<Movie> list = loadDistritubion( getPlainFile(null) );
		System.out.println("Filtering distribution...");	
		PrintWriter pw = new PrintWriter( getStemmedFile(null) );
		HashSet<String> originalWords = new  HashSet<String>();
		HashSet<String> newWords = new  HashSet<String>();
		for (Movie m : list){
			originalWords.addAll(Utils.loadText(m.plot));
			m.filter();
			pw.println(m.toString());
			newWords.addAll(Utils.loadText(m.plot));
		}
		System.out.println("Original " + originalWords.size() + " words count reducted to " + newWords.size() );		
		pw.close();
	}

	public static void main(String[] args)
	{
		//args = new String[]{"-traint"};
		//args = new String[]{"-m"};
		
		/*args = new String[]{"-m" ,
				"/home/martin/workspace/tmp/libsvm-3.20/predicted.o",
				"/home/martin/workspace/tmp/libsvm-3.20/movie_dist/cat.txt"};*/
		try {

			Learner learner = new Learner(new File("mc.properties"));
			for ( int i = 0 ; i < args.length; i++){
					String arg = args[i];

					if (arg.equals("-gen-tdist")){
						learner.generateTransDistribution();
					}else if ( arg.equals("-gen-bdist")){
						learner.generateBinaryDistributions();
					}else if (arg.equals("-fil-bdist")){
						learner.filterBinaryDistribution();
					}else if (arg.equals("-fil-tdist")){
						learner.filterTransDistribution();
					}else if (arg.equals("-svmt") ){
						learner.generateTransformSVM();
					}else if (arg.equals("-svmb")){
						learner.generateBinarySVMs();
					}else if (arg.equals("-trainb")){
						learner.trainBinarySVMs();
					}else if (arg.equals("-traint")){
						learner.trainTransSVM();
						System.out.println("Use train_trans.sh instead");
					}else if (arg.equals("-mt")){
						learner.measureTrans( 0.5f );
					}else if (arg.equals("-mb")){
						learner.showTextStatisticsBinary(TOP_WORDS_COUNT,TOP_OTHER_GENRES_COUNT);
					}

				}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
