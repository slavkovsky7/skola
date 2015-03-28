import java.util.ArrayList;
import java.util.List;

public class BoardTurner {
	
	public static final int ME = World.WHITE_PLAYER;
	
	public static final int OPP = World.BLACK_PLAYER;

	private World world;

	public BoardTurner(World world) {
		this.world = world;
	}

	public List<BoardState> turnAll(BoardState state, int[] killers) {
		List<BoardState> list = new ArrayList<BoardState>();
		for (int move : state.getMoves()) {
			BoardState newState = turn(state, move);
			for (int i = 0; i < killers.length; i++) {
				if (killers[i] == move) {
					newState.setKillerScore(1000);
					break;
				}	
			}
			list.add(newState);
		}
		return list;
	}

	public BoardState turn(BoardState state, int move) {		
		int opponent;
		if (state.getPlayer() == ME) {
			opponent = OPP;
		} else {
			opponent = ME;
		}
		int[][] newGrid = world.getResultingState(state.getGrid(), move, state.getPlayer());
		int[] newMoves = world.getPossibleMoves(newGrid, opponent);
		BoardState newState = new BoardState(newGrid, newMoves, move, opponent);
		newState.evaluate();
		return newState;
	}
	
}
