import java.util.Comparator;


public class BoardComparator implements Comparator<BoardState> {

	private int order = 0;
	
	@Override
	public int compare(BoardState o1, BoardState o2) {

		if (o1.getScore() + o1.getKillerScore() > o2.getScore() + o2.getKillerScore()) {
			return 1 * order;
		} else if (o1.getScore() + o1.getKillerScore() < o2.getScore() + o2.getKillerScore()) {
			return -1 * order;
		}
		return 0;
	}

	public void setAsc() {
		order = 1;
	}
	
	public void setDesc() {
		order = -1;
	}
}
