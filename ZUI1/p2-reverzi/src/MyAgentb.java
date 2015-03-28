import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.naming.TimeLimitExceededException;

public  class MyAgentb extends Agent{
	
	Random rnd = new Random();
	
			
	public MyAgentb(World world){
		super(world);
	}
	
	private long timeLeft() {
		long xxy = ( System.nanoTime() - startTime);
		return timeout - xxy;
	}
	
	private int getTimeLimit(int timeLimit ){
		if (timeLimit < 20)
			return (timeLimit/2);
		if ( timeLimit < 100)
			return (timeLimit - timeLimit/4);
		if (timeLimit < 500)
			return timeLimit - (timeLimit/5);
		return timeLimit - (timeLimit / 10);
	}
	
	public int act(int plocha[][], int[] tahy, int timeLimit){
		//System.out.println("begin act");
		int result = 0;
		/* ZACIATOK MIESTA PRE VAS KOD */
		//timeout = getTimeLimit(timeLimit);
		timeout = getTimeLimit(timeLimit) * 1000_000;
		result = iterativeDeepining(tahy, plocha, 20 );
		//System.out.println( "Time : " + (System.currentTimeMillis() - b ) ); 
		/* KONIEC MIESTA PRE VAS KOD */
		return result;
	}	
	
	long startTime = 0;
	int timeout = 0;
	
	private boolean isTimeout(){
		return timeLeft() <= 0;
	}
	
	private int iterativeDeepining(int[] tahy, int[][] plocha , int maxDepth ){	
		startTime = System.nanoTime();
		//return alphabetaRoot(tahy, plocha, 11);
		int bestMove = 0;
		for (int i = 1 ; i <= maxDepth; i++){
			Integer lastMove = alphabetaRoot(tahy, plocha, i);
			if ( isTimeout() || lastMove == null ){
				break;
			} 
			bestMove = lastMove;
		}
		return bestMove;
	}
	
	private Integer alphabetaRoot(int[] tahy, int[][] plocha, int depth ){
		int result = 0;
		double max = Integer.MIN_VALUE;
		Node root = new Node(plocha,tahy,Node.AGENT_PLAYER, world);
		List<Node> children = root.getChildren(world);
		
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			Double value = alphabeta( child , depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, Node.OPPONENT_PLAYER);
			if (value == null){
				return null;
			}
			if (value > max) {
				max = value;
				result = i;
			}
		}
		return result;
	}
	
	
	private Double alphabeta( Node node, int depth, double alpha, double beta, final int player ){
		if (timeLeft() < 10){
			return null;
		}
		if ( depth == 0 || node.moves.length == 0 /*|| getRemainingTime() < 2*/ ){
			return node.getHeuristic();
		}
		//boolean timeouted = isTimeout();
		List<Node> children = node.getChildren(world);
		Collections.sort(children);


		if ( player == Node.AGENT_PLAYER){
			for (Node child : children){
				Double rekAlpha = alphabeta(child, depth - 1, alpha, beta, Node.OPPONENT_PLAYER);
				if (rekAlpha == null) return null;
				alpha = Math.max(alpha, rekAlpha);
				if (beta <= alpha){
					break;
				}
			}
			return alpha;
		}else{
			for (Node child : children){
				Double rekBeta = alphabeta(child, depth - 1, alpha, beta, Node.AGENT_PLAYER );
				if (rekBeta == null) return null;
				beta = Math.min(beta , rekBeta);
				if (beta <= alpha){
					break;
				}
			}
			return beta;
		}
	}
}