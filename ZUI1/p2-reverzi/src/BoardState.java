public class BoardState {

	private int[][] grid;
	
	private double score;
	
	private int[] moves;
	
	private int player;
	
	private int playerValue;
	
	private int pastMove;
	
	private int killerScore;
	
	/**
	 * @see http://www.samsoft.org.uk/reversi/strategy.htm
	 */
	private static int[][] STATIC_SCORE = {
		{ 99, -8,  8,  6,  6,  8, -8, 99},
		{ -8,-24, -4, -3, -3, -4,-24, -8},
		{  8, -4,  7,  4,  4,  7, -4,  8},
		{  6, -3,  4,  0,  0,  4, -3,  6},
		{  6, -3,  4,  0,  0,  4, -3,  6},
		{  8, -4,  7,  4,  4,  7, -4,  8},
		{ -8,-24, -4, -3, -3, -4,-24, -8},
		{ 99, -8,  8,  6,  6,  8, -8, 99},
	};
	
	public BoardState(int[][] grid, int[] moves) {
		this(grid, moves, -1, BoardTurner.ME);
	}

	public BoardState(int[][] grid, int[] moves, int pastMove, int player) {
		this.grid = grid;
		this.moves = moves;
		this.pastMove = pastMove;
		this.player = player;
		if (BoardTurner.ME == player) {
			playerValue = 1;
		} else {
			playerValue = -1;
		}
	}

	public void evaluate() {

		score = 0;
		int meCount = 0;
		int oppCount = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == BoardTurner.ME) {
					score += STATIC_SCORE[i][j];
					meCount++;
				} else if (grid[i][j] == BoardTurner.OPP) {
					score -= STATIC_SCORE[i][j];
					oppCount++;
				}
			}
		}
		int countValue = -10;
		if (meCount + oppCount > 48) {
			countValue = 1;
		}
		score += moves.length * 10 * playerValue + (meCount - oppCount) * countValue;
	}
	
	public int getPastMove() {
		return pastMove;
	}
	
	public int[] getMoves() {
		return moves;
	}
	
	public int getPlayer() {
		return player;
	}

	public double getScore() {
		return score;
	}

	public int[][] getGrid() {
		return grid;
	}
	
	public void setKillerScore(int score) {
		killerScore = score * playerValue * -1;
	}
	
	public int getKillerScore() {
		return killerScore;
	}

	public String toString() {
		return Double.toString(score);
	}
}
