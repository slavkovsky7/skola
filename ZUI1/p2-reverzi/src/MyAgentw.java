import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyAgentw extends Agent {

	private BoardComparator cmp = new BoardComparator();
	
	private long endTime;
	
	private int[][] killers = {new int[TERMINATE_DEPTH ], new int[TERMINATE_DEPTH]};
	
	boolean terminate;
	
	private static final int TERMINATE_THRESHOLD = 3; // MS
	
	private static final int TERMINATE_DEPTH = 100;
	
	private BoardTurner turner = new BoardTurner(world);

	public MyAgentw(World world) {
		super(world);
	}
	
	private long getRemainingTime() {
		return (endTime - System.nanoTime()) / 1000000;
	}

	Random rnd = new Random();
	public int act(int grid[][], int[] moves, int timeLimit) {

		int result = 0;
		int mostLeft =  Integer.MAX_VALUE;
		int mostBottom =  Integer.MAX_VALUE; 
		for (int i = 0; i < moves.length; i++) {
			int leftOffset = moves[i] / 10;
			int bottomOffset = 7 - moves[i] % 10;
			
			if (leftOffset <= mostLeft) {
				mostLeft = leftOffset;
				if (bottomOffset < mostBottom) {
					mostBottom = bottomOffset;
					result = i;
				}
			}
		}
		result = rnd.nextInt(moves.length);
		return result;
		
	
		/*endTime = System.nanoTime() + timeLimit * 1000000;
		terminate = false;

		BoardState rootBoard = new BoardState(grid, moves);
		return getIterativeMoveId(rootBoard);*/
	}
	
	public int getIterativeMoveId(BoardState board) {
		  int result = 0;
		  int moveId = 0;
		  int depth = 0;
		  while(true) {
			  depth++;
			  for (int i = 0; i < killers.length; i++) {
				  for (int j = 0; j < killers[i].length; j++) {
					  killers[i][j] = 0;
				  }
			  }
			  moveId = getMoveId(board, depth);
			  if (terminate || depth == TERMINATE_DEPTH) {
				  break;
			  }
			  result = moveId;
		  }
		  System.out.println("MEE: " + depth);
		  return result;
	}
	
	private int getMoveId(BoardState board, int depth) {
		int result = 0;
		int[] moves = board.getMoves();
		double maxValue = Double.NEGATIVE_INFINITY;
		double value = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < moves.length; i++) {
			BoardState childBoard = turner.turn(board, moves[i]);
			value = alphaBeta(childBoard, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			if (value > maxValue) {
				maxValue = value;
				result = i;
			}
		}
		return result;
	}

	private double alphaBeta(BoardState board, int depth, double alpha, double beta) {
		
		if (terminate) {
			return -1;
		} else if (depth == 0 || board.getMoves().length == 0) {
			return board.getScore();
		} else if (getRemainingTime() < TERMINATE_THRESHOLD) {
			terminate = true;
			return -1;
		}
		int depthId = depth - 1;
		int[] depthKillers = {killers[0][depthId], killers[1][depthId]};
		List<BoardState> boardList = turner.turnAll(board, depthKillers);
		
		if (board.getPlayer() == BoardTurner.ME) {
			
			cmp.setDesc();
			Collections.sort(boardList, cmp);
			
			for (BoardState newBoard : boardList) {
				alpha = Math.max(alpha, alphaBeta(newBoard, depth - 1, alpha, beta));
				if (beta <= alpha) {
					if (killers[0][depthId] != 0) {
						killers[1][depthId] = killers[0][depthId];
					}
					killers[0][depth] = newBoard.getPastMove();
					break;
				}
			}
			return alpha;
		} else {
			
			cmp.setAsc();
			Collections.sort(boardList, cmp);
			
			for (BoardState newBoard : boardList) {
				beta = Math.min(beta, alphaBeta(newBoard, depth - 1, alpha, beta));
				if (beta <= alpha) {
					if (killers[0][depthId] != 0) {
						killers[1][depthId] = killers[0][depthId];
					}
					killers[0][depthId] = newBoard.getPastMove();
					break;
				}
			}
			return beta;
		}
	}

}
