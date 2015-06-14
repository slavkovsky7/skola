package sk.mslavkovsky.zui2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sk.mslavkovsky.zui2.Hmm.ViterbiResult;

public class Uloha2 {
	
	public static String[] statesToStrings(int[] states, String[] statesString){
		String[] result = new String[states.length];
		for (int i = 0; i < result.length; i++){
			result[i] = statesString[ states[i]];
		}
		return result;
	}
	
	public static void main(String[] args){
		double[][] X = new double[][]{ {0.7, 0.3}, { 0.2,  0.8} };
		double[][] E = new double[][]{ {0.5, 0.5}, { 0.2,  0.8} };
		double[] start = { 0.6, 0.4 };
		
		//0 je hlava, 1 je znak
		String[] stateStrings = new String[]{ "fair", "biased" };
		int[] obs = new int[]{0,1,0,0,1,0,1,1,1};
		
		int k = 5;
		Hmm hmm = new Hmm(start, X, E);
		
		
		System.out.println("filtered:");
		System.out.println( Utils.toString(hmm.filter(obs)));

		System.out.println();
		System.out.println("smooth:");
		System.out.println( hmm.smooth(obs,k-1) );

		System.out.println();
		System.out.println("predicted:");
		System.out.println( hmm.predictOneStep(obs) );
		
		ViterbiResult res = hmm.viterbi(obs);
		System.out.println();
		System.out.println("Viterbi");
		System.out.println(Arrays.toString( statesToStrings(res.states, stateStrings)));
		System.out.println(Utils.toString(res.probs));
		
		List<String> list =new ArrayList<String>();
		list.add("adsas");
		list.add("asdasdasd");
		System.out.println(list);

	}
}
