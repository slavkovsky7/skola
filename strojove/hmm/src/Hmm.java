import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Hmm {

	//A[stav][stav]
	private double[][] A;
	//B[emisia][stav]
	private double[][] B;
	
	private final static int EMISSION_COUNT = 27;
	
	private HashMap<Character, Integer> stateToIndex = new HashMap<Character, Integer>();
	private HashMap<Integer, Character> indexToState = new HashMap<Integer, Character>();
	
	
	public static char[] readFileToCharArray(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(10000);

 
		
		FileInputStream fileInput = new FileInputStream(filePath);
		int r;
		while ((r = fileInput.read()) != -1) {
			char c = (char) r;
			if (c != '\n' && c != '\r')
			fileData.append(c);
		}
		fileInput.close();
	
		return  fileData.toString().toCharArray();	
	} 
	
	public Hmm(String fileName, int numStates) throws IOException{
				
		char[] file = readFileToCharArray(fileName);		
		int l = file.length / 2 ;
		HashMap<Character, HashMap<Character, Integer>> preHmm = new HashMap<Character, HashMap<Character,Integer>>();
		int[] inStateCount = new int[numStates];
		
		Character prevState = null;
		
		int[][] transitions = new int[numStates][numStates];
		
		for (int i = 0 ; i < l; i++){
			char s = file[ l + i ]; 
			char e = file[i];
			if(!preHmm.containsKey(s)){
				preHmm.put(s, new HashMap<Character, Integer>());
				stateToIndex.put(s, stateToIndex.size());
				indexToState.put(indexToState.size(), s);
			}
			
			inStateCount[stateToIndex(s)]++;
			
			if (prevState != null ){
				transitions[stateToIndex(prevState)][stateToIndex(s)]++;
			}
			prevState = s;
			
			HashMap<Character, Integer> state = preHmm.get(s);
			if (!state.containsKey(e)){
				state.put(e, 1);
			}else{
				state.put(e, state.get(e) + 1);
			}
		}
		
		A = new double[ numStates ][ numStates ];
		B = new double[numStates ][ EMISSION_COUNT];
		
		//assign transition probabilities
		for (int i = 0 ; i < A.length ; i++){
			for (int j = 0 ; j < A[i].length; j++){
				A[i][j] = divide( (double)transitions[i][j], (double)inStateCount[i] );
			}
		}
		
		//assign emission probabilties
		for (int i = 0 ; i < B.length ; i++){ 
			for (int j = 0 ; j < B[i].length; j++){
				char e = indexToEmission(j);
				char s = indexToState(i);	
				if (preHmm.get(s).get(e) == null){
					preHmm.get(s).put(e, 0);
				}
				B[i][j] = divide( (double)preHmm.get(s).get(e), (double)inStateCount[i] );
			}
		}
	
		
		double scount = 0;
		double ecount = 0;
		for (int i = 0 ; i < EMISSION_COUNT; i++){
			System.out.println("s - "+indexToEmission(i)+" ->"+ B[stateToIndex('s')][i] );
			System.out.println("e - "+indexToEmission(i)+" ->"+ B[stateToIndex('e')][i] );
			scount += B[stateToIndex('s')][i];
			ecount += B[stateToIndex('e')][i];
		}
		System.out.println("S.B = " + scount);
		System.out.println("E.B = " + ecount);
		
	}
	
	private static double divide(double a, double b){
		if (b == 0){
			return 0;
		}
		return a/b;
	}

	private static int emissionToIndex(char c){
		if (c == ' '){
			return EMISSION_COUNT - 1;
		}
		return ((int)c - 97);
	}
	
	private static char indexToEmission(int index){
		if (index == EMISSION_COUNT - 1){
			return ' ';
		}
		return (char)(index + 97);
	}
	
	private int stateToIndex(char c){
		return stateToIndex.get(c);
	}
	
	private char indexToState(int index){
		return indexToState.get(index);
	}
	
	
	public static void main(String[] args){
		String resDir = "/home/martin/workspace/skola/strojove/hmm/resources/";
		try{
			Hmm hmm = new Hmm(resDir+"sk-en-1000-train.txt", 2);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
