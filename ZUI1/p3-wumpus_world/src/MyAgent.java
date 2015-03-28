import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class MyAgent extends Agent {

	// -------------Percept types------------------------
	static final int BREEZE = 0; // vedla policka[x,y] je PIT
	static final int STENCH = 1; // vedla policka[x,y] je Wumpus
	static final int BUMP = 2; //
	static final int SCREAM = 3;
	static final int GLITTER = 4;

	// -------------Cardinal directions------------------
	static final int NORTH = 0;
	static final int EAST = 1;
	static final int SOUTH = 2;
	static final int WEST = 3;

	private boolean[] percept = new boolean[5];
	private List<Vector2> computedPath = new ArrayList<Vector2>();
	private Memory memory = null;
	private KnowledgeBase kb;

	private Vector2 previousPosition = null;
	private Vector2 startPosition = null;
	private boolean wumpusAlive = true;
	private boolean returning = false;
	
	public MyAgent(int orientation, int w, int h) {
		super(orientation);
		memory = new Memory();
		createKB();
		startPosition = new Vector2(memory.Current.X, memory.Current.Y);
	}

	private void createKB() {
		HashSet<Formula> axioms = new HashSet<Formula>();

		// if a tile is surrounded by stenches, then it is a wumpus
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(1, 0, "stench", true), 
				new MatchingLiteral(0, -1, "stench", true), 
				new MatchingLiteral(0, 1,"stench", true),
				new MatchingLiteral(-1, 0, "stench",true)));

		axioms.add(new Implication(new ParameterLiteral("pit", true),
				new MatchingLiteral(1, 0, "breeze", true),
				new MatchingLiteral(0, -1, "breeze", true), 
				new MatchingLiteral(0, 1,"breeze", true), 
				new MatchingLiteral(-1, 0, "breeze",true)));

		axioms.add(new Implication(new ParameterLiteral("safe", true),
				new MatchingLiteral(0, 0, "wumpus", false),
				new MatchingLiteral(0,0, "pit", false)));

		//ked zistime kde je wumpus
		/*axioms.add(new Implication(new ParameterLiteral("wumpus", false),
				new MatchingLiteral(1, -1, "wumpus", true)));
		axioms.add(new Implication(new ParameterLiteral("wumpus", false),
				new MatchingLiteral(1,  1, "wumpus", true)));
		axioms.add(new Implication(new ParameterLiteral("wumpus", false),
				new MatchingLiteral(-1, -1, "wumpus", true)));
		axioms.add(new Implication(new ParameterLiteral("wumpus", false),
				new MatchingLiteral(1,  1, "wumpus", true)));
		
		//Ako zistit kde je wumpus
		axioms.add(new Implication(new ParameterLiteral("wumpus", true),
				new MatchingLiteral(1,  -1, "wumpus", false),
				new MatchingLiteral(1,  0, "stench", true),
				new MatchingLiteral(0,  -1, "stench", true)
				));
		*/
		
		kb = new KnowledgeBase(axioms);
	}

	public void act() {
		// System.out.println(kb.toString());
		percept = getPercept();
		this.
		writePercept();
		doAction();
		

	}

	private Vector2 getDirVecByOrientation(){
		int orientation = getOrientation();
		Vector2 result = null;
		switch (orientation){
			case NORTH: result = new Vector2(0,1);
				break;
			case SOUTH:result = new Vector2(0,-1);
				break;
			case WEST:result = new Vector2(-1,0);
				break;
			case EAST:result = new Vector2(1,0);
				break;
		}
		
		return result;
	}

	private void writePercept() {
		ArrayList<Formula> a = new ArrayList<Formula>();

		if (percept[BUMP]){
			memory.Current = previousPosition;
			Vector2 d = getDirVecByOrientation();
			Vector2 wallPos = Vector2.Add(memory.Current, d);
			memory.setTileStatus( wallPos , Memory.TileStatus.Wall);
			kb.tell( new ParameterLiteral(wallPos.X, wallPos.Y, "wumpus", false));
			//kb.tell( new ParameterLiteral(wallPos.X, wallPos.Y, "bump", true));
			//Tuto treba urobit recompute cesty
			computedPath.clear();
		}
		
		int x = memory.Current.X;
		int y= memory.Current.Y;
		
		a.add(new ParameterLiteral(x, y, "wumpus", false));
		//ak toto budem potrebovat tak ak pridem na breeze tak zmaz vsetky !pit, ktore este neboli navstivene 
		//a.add(new ParameterLiteral(x, y, "pit", false));
		//System.out.println("not pit in " + new Vector2(x, y));
		if (percept[BREEZE]) {
			a.add(new ParameterLiteral(x, y, "breeze", true));
			if (!percept[STENCH]){
				a.add(new ParameterLiteral(x+1, y, "wumpus", false));
				a.add(new ParameterLiteral(x-1, y, "wumpus", false));
				a.add(new ParameterLiteral(x, y+1, "wumpus", false));
				a.add(new ParameterLiteral(x, y-1, "wumpus", false));
			}
			//memory.removeConflictPlaces(x,y,"pit", kb);
		}  
		
		if (percept[STENCH] && wumpusAlive) {
			//System.out.print("Stench added [" + x + ", "+y+"]");
			a.add(new ParameterLiteral(x, y, "stench", true));
			if (!percept[BREEZE]){
				a.add(new ParameterLiteral(x+1, y, "pit", false));
				a.add(new ParameterLiteral(x-1, y, "pit", false));
				a.add(new ParameterLiteral(x, y+1, "pit", false));
				a.add(new ParameterLiteral(x, y-1, "pit", false));
			}
		}
		
		if ( (!percept[STENCH] || !wumpusAlive) && !percept[BREEZE]){
			memory.addOk(kb, x, y);		
		}
		kb.tell(a);
	
	}

	private void doAction() {
		if (percept[Constants.GLITTER]) {
			pickUp();
		} else {
			boolean killingWumpus = killingWumpus();
			if (!killingWumpus ){
				boolean goNextSafe = goToSafePlace();
				if (!goNextSafe){
					if (wumpusAlive){
						memory.unvisitClosestStench(kb);
						goNextSafe = goToSafePlace();
					}
				}
				
				if (!goNextSafe && memory.wumpusPosition == null){
					//System.out.println("No other safe place found");
					memory.setTileStatus(startPosition, Memory.TileStatus.Safe);				
					goToSafePlace();
					returning = true;
				}
			}
		}
	}
	
	public boolean killingWumpus(){
		if (memory.wumpusPosition != null && memory.wumpusPosition.nextTo(memory.Current)){
			//System.out.println("I'm next to wumpus");
			Vector2 d =Vector2.Substract( memory.wumpusPosition, memory.Current);
			if ( changeDirection(d.X, d.Y) ){
				//System.out.println("I'm ready to shoot");
				shoot();
				kb.tell(new ParameterLiteral(memory.wumpusPosition.X,  memory.wumpusPosition.Y, "safe", true));
				kb.tell(new ParameterLiteral(memory.wumpusPosition.X,  memory.wumpusPosition.Y, "wumpus", false));
				kb.remove(new ParameterLiteral(memory.wumpusPosition.X,  memory.wumpusPosition.Y, "wumpus", true));
				memory.addOkInsteadOfStench(kb,memory.wumpusPosition.X+1,  memory.wumpusPosition.Y);
				memory.addOkInsteadOfStench(kb,memory.wumpusPosition.X-1,  memory.wumpusPosition.Y);
				memory.addOkInsteadOfStench(kb,memory.wumpusPosition.X,  memory.wumpusPosition.Y+1);
				memory.addOkInsteadOfStench(kb,memory.wumpusPosition.X,  memory.wumpusPosition.Y-1);
				memory.wumpusPosition = null;
				wumpusAlive = false;
			}
			return true;
		}
		return false;
	}

	public boolean goToSafePlace() {
		memory.checkAndAddSafePlaces(kb);
		memory.setTileStatus(memory.Current, Memory.TileStatus.Visited);
		
		//System.out.println(kb);
		if (computedPath == null || computedPath.size() == 0) {
			//System.out.println(kb);
			computedPath = memory.Search();
			if (returning){
				//System.out.println(kb);
				climb();
				return true;
			}
		}
		
		if (computedPath != null && computedPath.size() > 0){
			Vector2 nextPos = computedPath.get(0);
			Vector2 d = Vector2.Substract(nextPos, memory.Current );
			boolean move = changeDirection(d.X , d.Y );
			if (move) {			
				moveFW();
				//System.out.println(memory.toString());
				computedPath.remove(0);
				previousPosition = memory.Current;
				memory.Current = Vector2.Add(memory.Current, d );
			}
			return true;
		}
		return false;
	}
	
	public boolean changeDirection(int xd, int yd) {
		boolean result = false;

		int nextOrientation = -1;
		if (xd == 1 && yd == 0) {
			nextOrientation = EAST;
		} else if (xd == -1 && yd == 0) {
			nextOrientation = WEST;
		} else if (xd == 0 && yd == -1) {
			nextOrientation = SOUTH;
		} else if (xd == 0 && yd == 1) {
			nextOrientation = NORTH;
		}

		if (nextOrientation > -1) {
			if (nextOrientation == getOrientation()) {
				//moveFW();
				result = true;
			} else {
				int dir = -1;
				if ((getOrientation() == NORTH && nextOrientation == EAST)
						|| (getOrientation() == EAST && nextOrientation == SOUTH)
						|| (getOrientation() == SOUTH && nextOrientation == WEST)
						|| (getOrientation() == WEST && nextOrientation == NORTH)) {
					dir = 1;
				}
				if (dir > 0) {
					turnRIGHT();
				} else {
					turnLEFT();
				}
			}
		}
		return result;
	}

	// =====================================================================
	// Search and memory
	// =====================================================================
	// Tile sa pouziva iba na search a ako holder pre X,Y
	private static class Memory {
		public static final int SIZE = 200;

		public enum TileStatus {
			Safe, NotSafe, Visited, Wall
		}

		/***********************************/

		private int minX, maxX;
		private int minY, maxY;
		private TileStatus[][] tiles;
		
		private Vector2 wumpusPosition = null;
		
		public Vector2 Current;
		
		public Memory() {
			tiles = new TileStatus[200 + 1][200 + 1];
			for (int y = 0; y < tiles.length; y++) {
				for (int x = 0; x < tiles[y].length; x++) {
					setTileStatus(x, y, TileStatus.NotSafe);
				}
			}

			minX = Integer.MAX_VALUE;
			maxX = Integer.MIN_VALUE;
			minY = Integer.MAX_VALUE;
			maxY = Integer.MIN_VALUE;

			// Toto je zle , nejakym sposobom toto musis zosynchronizovat s x,y,
			// ktore je v myagent,
			// mozno sa kludne mozes zbavit x,y co je agentov a iba budes mat to
			// co je v memory
			Current = new Vector2(SIZE / 2, SIZE / 2 );
		}

		public void addOk(KnowledgeBase kb, int x , int y){		
			kb.tell(new ParameterLiteral(x, y, "ok", true));
			addSafeTile(kb, x, y + 1);
			addSafeTile(kb,x, y - 1);
			addSafeTile(kb,x + 1, y);
			addSafeTile(kb,x - 1, y);
		}
		
		public void addOkInsteadOfStench(KnowledgeBase kb, int x , int y){
			kb.remove(new ParameterLiteral(x,  y, "stench", true));
			if ( !kb.ask(new ParameterLiteral(x,y, "stench" , true)) &&
				 !kb.ask(new ParameterLiteral(x,y, "breeze" , true)) &&
				 getTileStatus(x, y) == TileStatus.Visited )
			{
				addOk(kb,x,y);
			}
		}
		
		private Vector2 findPitOrWumpus(KnowledgeBase kb, int x, int y, String str1, String str2){
			Vector2 result = null;
			
			ArrayList<Formula> a = new ArrayList<Formula>();
			//TODO::doplnit dok
			if ( kb.ask(new ParameterLiteral(x+1,y-1, str1 , false)) && 
				 kb.ask(new ParameterLiteral(x+1,y  , str2 , true)) &&
				 kb.ask(new ParameterLiteral(x  ,y-1, str2 , true)) )
			{	
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			//TODO::doplnit dok
			if ( kb.ask(new ParameterLiteral(x-1,y-1, str1 , false)) && 
				 kb.ask(new ParameterLiteral(x-1,y  , str2 , true)) &&
				 kb.ask(new ParameterLiteral(x  ,y-1, str2 , true)) )
			{	
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			
			//TODO::doplnit dok
			if ( kb.ask(new ParameterLiteral(x-1,y+1, str1 , false)) && 
			     kb.ask(new ParameterLiteral(x-1,y  , str2 , true)) &&
			     kb.ask(new ParameterLiteral(x  ,y+1, str2 , true)) )
			{	
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			//TODO::doplnit dok
			if ( kb.ask(new ParameterLiteral(x+1,y+1, str1 , false)) && 
				 kb.ask(new ParameterLiteral(x+1,y  , str2 , true)) &&
				 kb.ask(new ParameterLiteral(x  ,y+1, str2 , true)) )
			{	
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			
			/* S W S */
			if ( kb.ask(new ParameterLiteral(x+1,y, str2 , true)) &&
				 kb.ask(new ParameterLiteral(x-1,y, str2 , true)) )
			{	
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			
			/* S
			 * W
			 * S */
			if ( kb.ask(new ParameterLiteral(x,y+1,str2,true)) &&
				 kb.ask(new ParameterLiteral(x,y-1,str2,true)) )
			{	
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			
			/*     !W           W
			 *  !W  S !W  OR !W S !W
			 *      W    	   !W*/
			if ((kb.ask(new ParameterLiteral(x - 1,y+1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 0,y+2,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 1,y+1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 0,y+1,str2,true))) || 
				
				(kb.ask(new ParameterLiteral(x - 1,y-1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 0,y-2,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 1,y-1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 0,y-1,str2,true))))
			{		
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			
			/*     !W          !W
			 *   W  S !W  OR !W S W
			 *     !W    	   !W*/
			if ((kb.ask(new ParameterLiteral(x + 1,y-1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 2,y+0,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 1,y+1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x + 1,y+0,str2,true))) || 
				
				(kb.ask(new ParameterLiteral(x - 1,y-1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x - 2,y+2,str1,false)) &&
				 kb.ask(new ParameterLiteral(x - 1,y+1,str1,false)) &&
				 kb.ask(new ParameterLiteral(x - 1,y+0,str2,true))))
			{		
				a.add(new ParameterLiteral(x, y, str1, true ));
			}
			
			/******************************************************/
			kb.tell(a);
			a.clear();
			
			if ( kb.ask(new ParameterLiteral(x,y, str1 , true) ) ){
				kb.tell(new ParameterLiteral(x, y+1, str1, false ));
				kb.tell(new ParameterLiteral(x, y-1, str1, false ));
				kb.tell(new ParameterLiteral(x+1, y, str1, false ));
				kb.tell(new ParameterLiteral(x-1, y, str1, false ));
				
				//addOkInsteadOfStench(kb,x, y+1 );
				//addOkInsteadOfStench(kb,x, y-1 );
				//addOkInsteadOfStench(kb,x+1, y );
				//addOkInsteadOfStench(kb,x-1, y);
				result = new Vector2(x, y);
			}
			
			return result;
		}
		
		public void checkAndAddSafePlaces(KnowledgeBase kb){
			for (int y = maxY; y >= minY; y--) {
				for (int x = minX; x <= maxX; x++) {
					if ( kb.ask( new ParameterLiteral(x, y, "safe", true) ) ){
						Memory.TileStatus status = getTileStatus(x, y);
						if (status != Memory.TileStatus.Visited && status != Memory.TileStatus.Wall) {
							setTileStatus(x, y, Memory.TileStatus.Safe);
						}
					}
					
					//TODO:: toto mozeme potom aplikovat aj na PIT
					/*****************************************************/
					
					//System.out.print(kb);
					if (wumpusPosition == null){
						//Tu by mozno bolo dorobit bump, pretoze to celkom nezvlada ak je S W
						//																  B S
						wumpusPosition = findPitOrWumpus(kb, x, y , "wumpus", "stench");
					}
					//findPitOrWumpus(kb, x, y , "pit", "breeze");
					/***************************************************/
					
				}
			}
		}
		
		
		private void addSafeTile(KnowledgeBase kb , int xx, int yy) {
			kb.tell(new ParameterLiteral(xx, yy, "safe", true));
			Memory.TileStatus status = getTileStatus(xx, yy);
			if (status != Memory.TileStatus.Visited && status != Memory.TileStatus.Wall) {
				setTileStatus(xx, yy, Memory.TileStatus.Safe);
			}
		}
		
		
		public String toString(){
			//System.out.println("Current pos = " + Current );
			String result = "------------------------------------\n";
			for (int y = maxY; y >= minY; y--) {
				if (y < maxY){
					result += "\n";
				}
				for (int x = minX; x <= maxX; x++) {
					if (x > minX){
						result += " ";
					}
					if ( new Vector2(x, y).equals(Current) ){
						result += "*";
					}else if (getTileStatus(x, y) == (TileStatus.Visited)){
						result += "V";
					}else if (getTileStatus(x, y) == (TileStatus.Wall)){
						result += "□";
					/*} else if (getTileStatus(x, y) == (TileStatus.Stench)){
						result += "S";				
					} else if (getTileStatus(x, y) == (TileStatus.Breeze)){
						result += "B";*/
					}else{
						result += getTileStatus(x, y) == (TileStatus.Safe) ? "S": "N";
					}
				}
			}
			return result;
		}
		
		public void setTileStatus(Vector2 v, TileStatus val) {
			setTileStatus(v.X,  v.Y, val );
		}
		
		public void setTileStatus(int x, int y, TileStatus val) {
			tiles[y][x] = val;
			// TODO::mozno urobit iba pre safe
			minX = Math.min(x, minX);
			maxX = Math.max(x, maxX);
			minY = Math.min(y, minY);
			maxY = Math.max(y, maxY);
		}

		public TileStatus getTileStatus(int x, int y) {
			return tiles[y][x];
		}
		
		public TileStatus getTileStatus(Vector2 v) {
			return tiles[v.Y][v.X];
		}
		
		public boolean unvisitClosestStench(KnowledgeBase kb){
			Vector2 closest = null;
			double minDist = Integer.MAX_VALUE;
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {				
					Vector2 tmp = new Vector2(x, y);
					double tmpDist = Current.GetDistance(tmp);
					if (kb.ask( new ParameterLiteral(x,y,"stench", true) ) &&  tmpDist < minDist) {
						minDist = tmpDist;
						closest = tmp;
					}
				}
			}
			if (closest == null){
				return false;
			}
			setTileStatus(closest, TileStatus.Safe);
			return true;
		}

		public List<Vector2> Search() {
			//System.out.print("-------Search started---------");
			Vector2 start = Current;
			Vector2 finish = null;
			double minDist = Integer.MAX_VALUE;
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					if (getTileStatus(x, y) == TileStatus.Safe) {
						Vector2 tmp = new Vector2(x, y);
						double tmpDist = start.GetDistance(tmp);
						if ( tmpDist < minDist) {
							minDist = tmpDist;
							finish = tmp;
						}
					}
				}
			}
			if (finish == null) {
				return null;
			}
			return Search(start, finish);
		}

		public List<Vector2> Search(Vector2 start, Vector2 finish) {

			boolean finishFound = false;

			
			PriorityQueue<Vector2> open = new PriorityQueue<Vector2>();
			//java.util.Queue<Vector2> open = new LinkedList<Vector2>();
			Set<Vector2> closed = new HashSet<Vector2>();

			open.add(start);
			start.GetDistance(finish);

			Vector2 current = null;
			while (open.size() > 0) {
				current = open.poll();
				if (!closed.contains(current)) {
					closed.add(current);
					if (current.equals(finish)) {
						finish = current;
						finishFound = true;
						break;
					}

					List<Vector2> successors = getAllSuccesors(current, finish);
					for (Vector2 succ : successors) {
						if (!closed.contains(succ)) {
							open.add(succ);
						}
					}
				}
			}

			List<Vector2> path = new ArrayList<Vector2>();
			if (finishFound) {
				current = finish;
				while (current != null) {
					if (!current.equals(start)) {
						path.add(0, current);
					}
					current = current.prev;
				}
			}
			return path;
		}

		// najde najblizsiu cestu ktora nebola visited
		List<Vector2> getAllSuccesors(Vector2 current, Vector2 finish) {
			Vector2[] possibleN = new Vector2[] {
					new Vector2(current.X, current.Y + 1),
					new Vector2(current.X, current.Y - 1),
					new Vector2(current.X + 1, current.Y),
					new Vector2(current.X - 1, current.Y) };

			List<Vector2> result = new ArrayList<Vector2>();
			for (int i = 0; i < possibleN.length; i++) {
				Vector2 n = possibleN[i];
				TileStatus status = getTileStatus(n.X, n.Y);
				// mozeme ist cez safe alebo visited
				if (status == TileStatus.Safe || status == TileStatus.Visited) {
					n.prev = current;
					n.dist = n.GetDistance(finish);
					result.add(n);
				}
			}
			return result;
		}
		
		//TODO::premenovat
		/*public void removeConflictPlaces(int x , int y, String str, KnowledgeBase kb ){
			List<Vector2> result = new ArrayList<Vector2>();
			Vector2 v1 = new Vector2(x+1, y);
			Vector2 v2 = new Vector2(x-1, y);
			Vector2 v3 = new Vector2(x, y+1);
			Vector2 v4 = new Vector2(x, y-1);
			
			if ( getTileStatus(v1) != TileStatus.Visited ){ 
				kb.remove(new ParameterLiteral(v1.X, v1.Y, str, false ));
				kb.remove(new Implication(new ParameterLiteral(v1.X, v1.Y,"safe", true),
						new MatchingLiteral(0, 0, "wumpus", false),
						new MatchingLiteral(0,0, "pit", false)));
			};
			if ( getTileStatus(v2) != TileStatus.Visited ){
				kb.remove(new ParameterLiteral(v2.X, v2.Y, str, false ));
				kb.remove(new Implication(new ParameterLiteral(v2.X, v2.Y,"safe", true),
						new MatchingLiteral(0, 0, "wumpus", false),
						new MatchingLiteral(0,0, "pit", false)));
			}
			if ( getTileStatus(v3) != TileStatus.Visited ) kb.remove(new ParameterLiteral(v3.X, v3.Y, str, false ));
			if ( getTileStatus(v4) != TileStatus.Visited ) kb.remove(new ParameterLiteral(v4.X, v4.Y, str, false ));
		}*/

	};

}