import java.util.LinkedList;
import java.util.List;

public class Node implements Comparable<Node>{
	
	public static final int AGENT_PLAYER = World.BLACK_PLAYER;
	public static final int OPPONENT_PLAYER = getOpponent(AGENT_PLAYER);
	
	public static int getOpponent(int player){
		if (player == World.BLACK_PLAYER)
			return World.WHITE_PLAYER;
		else
			return World.BLACK_PLAYER;
	}		
	/*********************************************************************/
			
	
	final int player;
	final int[][] net;
	int[] moves;
	World world;
	public Node(int[][] net, int[] moves, final int player, World world){
		this.player = player;
		this.net = net;
		this.moves= moves;
		this.world = world;
		computeHeuristic();
	}
	
	public List<Node> getChildren( World world){
		List<Node> result = new LinkedList<Node>();
		for (int move : moves){
			int[][] newNet = world.getResultingState(net, move, player);
			int[] 	newMoves = world.getPossibleMoves(newNet, getOpponent(this.player)); 
			Node node = new Node(newNet, newMoves, getOpponent(this.player), world);
			result.add(node);
		}
		return result;
	}
	
	/***********************************************/
	Double computedHeuristic = null;
	private final static int[][] SCORE_TABLE = {
		{70, -3, 11, 8, 8, 11, -3, 70},
		{-3, -7, -4, 1, 1, -4, -7, -3},
		{11, -4, 2, 2, 2, 2, -4, 11},
		{8, 1, 2, -3, -3, 2, 1, 8},
		{8, 1, 2, -3, -3, 2, 1, 8},
		{11, -4, 2, 2, 2, 2, -4, 11},
		{-3, -7, -4, 1, 1, -4, -7, -3},
		{70, -3, 11, 8, 8, 11, -3, 70}
	};
	
	public void computeHeuristic() {
		int score = 0;
		int discCount = 0;
		for (int i = 0; i < net.length; i++) {
			for (int j = 0; j < net[i].length; j++) {
				if (net[i][j] == AGENT_PLAYER || net[i][j] == OPPONENT_PLAYER ) {
					score += SCORE_TABLE[i][j];
					discCount++;
				}
			}
		}
		
		computedHeuristic = (double)(score + ((double)moves.length)*10 );
		computedHeuristic = computedHeuristic * (player == AGENT_PLAYER ? 1d : -1d );
	}

	
	/***********************************************/

	public double getHeuristic(){
		return computedHeuristic;
	}
	
	/***********************************************/
	
	@Override
	public int compareTo(Node o) {
		double th = this.getHeuristic();
		double oh = this.getHeuristic();
		if (th > oh) return 1;
		if (th < oh) return -1;
		return 0;
	}
	
}
