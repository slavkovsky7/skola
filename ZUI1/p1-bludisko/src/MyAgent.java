import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.ietf.jgss.Oid;


public  class MyAgent extends Agent{	
	
	

	public static final int UNKNOWN = 3;
	public static final int VISITED = 5;
	
	public MyAgent(int height, int width) {
		mem = new Memory( 2*Math.max(width, height)+ 10);
	}
	
	private static class Field implements Comparable<Field>{
		public int X,Y;
	
		//len pre prioritu vo vyhladavni
		public Field prev = null;
		public double dist = 0;
		
		
		public Field(int x, int y){
			this.X = x;
			this.Y = y;
		}
		

		public boolean equals(Object other){
			return this.X == ((Field)other).X && this.Y == ((Field)other).Y; 
		}
		

		public int hashCode() {
			  return 0;
		}

		@Override
		public int compareTo(Field o) {
			if (this.dist > o.dist){
				return 1;
			}
			if (this.dist < o.dist){
				return -1;
			}
			
			return 0;
		}
		
		public double GetDistance(Field f){
			double xsquared = Math.pow( f.X - this.X,2 );
			double ysquared = Math.pow( f.Y - this.Y,2 );
			return Math.sqrt( xsquared + ysquared );
		}
		

		public String toString(){
			return "["+X + ", " + Y+"]";
		}
	}
	
	private static class Memory{
		
		private int[][] internal;
		private Field current = null;
		private Set<Field> dirtyPlaces = new HashSet<Field>();
		private Set<Field> cleanPlaces = new HashSet<Field>();
		
		
		
		public Memory(int size){
			size += 1;
			internal = new int[size][];
			for (int i = 0 ; i < size; i++){
				internal[i] = new int[size];
				for (int j = 0 ; j < size; j++){
					internal[i][j] = UNKNOWN;
				}
			};
			current = new Field(internal.length / 2, internal.length / 2);
		}
		
		public int getFieldValue(int x, int y){
			return internal[y][x];
		}
		
		
		public int getSize(){
			return internal.length;
		}
		
		//po kazdom moveFW sa to zavola, mozeme tu pravdepodobne nastavit sucasne miesto na visited
		public void AddNewPercept(int[][] percept, int dx , int dy ){
			current = new Field(current.X  + dx, current.Y  + dy);
				
			for (int y = 0 ; y < percept.length; y++){
				for (int x = 0 ; x < percept[y].length; x++){
					//TODO::is valid
					//if ( currentX + y < internal.length && cu internal.length ) ){
						int tmpY = current.Y - percept.length/2 + y;
						int tmpX = current.X - percept.length/2 + x;		
						
						//TODO::
						if (tmpY >= internal.length || tmpX >= internal.length){
							print();
						}
						if (internal[tmpY][tmpX] != VISITED){ 
							internal[tmpY][tmpX]  = percept[y][x];
						}
						
						if ( internal[tmpY][tmpX] == World.DIRTY){
							dirtyPlaces.add( new Field(tmpX, tmpY) );
						}
						
						if ( internal[tmpY][tmpX] == World.CLEAN ){
							//if ( isNextToUknown(tmpX, tmpY) ){
							//System.out.println("Adding " + new Field(tmpX, tmpY) );	
							cleanPlaces.add( new Field(tmpX, tmpY) );
							//}
						}
						
					//}
				}	
			}
			//VisitPlace(current);
		}
		
		public boolean isItClean(){
			return dirtyPlaces.size() == 0 && cleanPlaces.size() == 0;
		}

		private boolean isNextToUknown(int x, int y){
			if (x == 105 && y == 95){
				System.out.println("damn niggar");
			}
			for (int yy = y - 1 ; yy <= y + 1 ; yy++){
				for (int xx = x - 1 ; xx <= x + 1 ; xx++){
					if ( /*xx != x && yy != y &&*/ (internal[yy][xx] == UNKNOWN ) ){
						return true;
					}
				}
			}
			return false;
		}
		
		public Field getClosestDirtyPlace(){
			Field closest = null;
			for ( Field f : dirtyPlaces){
				if ( closest == null || current.GetDistance(f) < current.GetDistance(closest) ){
					closest = f;
				}
			}
			
			return closest;
		}
		
		public Field getFarestCleanPlace(){
			List<Field> toRemove = new ArrayList<MyAgent.Field>();
			
			Field farest = null;
			for ( Field f : cleanPlaces){
				if ( farest == null || current.GetDistance(f) < current.GetDistance(farest) ){
					if (isNextToUknown(f.X, f.Y)){
						farest = f;
					}else{
						toRemove.add(f);
					}
				}
			}
			
			for ( Field f : toRemove){
				cleanPlaces.remove(f);
				internal[f.Y][f.X] = VISITED;
			}
			
			return farest;
		}
		
		public void VisitPlace(Field visited){
			cleanPlaces.remove(visited);
			dirtyPlaces.remove(visited);
			internal[visited.Y][visited.X] = VISITED;
		}
		
		public void RemovePlace(Field place){
			cleanPlaces.remove(place);
			dirtyPlaces.remove(place);
		}
		
		public void print(){
			System.out.print("----");
			for (int x = 0 ; x < internal.length; x++){
				if (x > 0){
					System.out.print("|");
				}
				System.out.print(String.format("%03d", x));
			}
			System.out.println("--------------------------------------------------------------------------------------------------------");
			for (int y = 0 ; y < internal.length; y++){
				System.out.print(String.format("%03d|", y));
				for (int x = 0 ; x < internal[y].length; x++){
					if ( x > 0){
						System.out.print(",  ");
					}
					
					String chara ="";
					switch ( getFieldValue(x, y)){
						case World.WALL : chara = "#"; break;
						case World.CLEAN : chara = " "; break;
						case UNKNOWN: chara = "X";break;
						case World.DIRTY: chara = "O";break;
						case VISITED: chara = "@";break;
					}
					if (x == current.X && y == current.Y){
						chara = "*";
					}
					System.out.print(chara);
					
				}
				System.out.println("");
			}
		}
	}
	
	
	
	private boolean canGoFw(int[][] per, int xd, int yd){
		int center = per.length / 2;
		return per[ center + yd ][center + xd ] != World.WALL; 
	}
	
	boolean moved = false;
	
	public boolean move(int[][] per, int xd, int yd){
		boolean result = false;
		//if (moved){
		//	mem.AddNewPercept(per, xd, yd );
		// mem.print();
		//}
		//moved = false;
		//////
		int nextOrientation = -1;
		if ( xd == 1 && yd == 0){
			nextOrientation = World.EAST;
		}else if ( xd == -1 && yd == 0 ){
			nextOrientation = World.WEST;
		}else if ( xd == 0 && yd == -1 ){
			nextOrientation = World.NORTH;
		}else if ( xd == 0 && yd == 1 ){
			nextOrientation = World.SOUTH;
		}
		
		if (nextOrientation > -1){
			if (nextOrientation == getOrientation()){
				if (canGoFw(per, xd, yd)){
					moveFW();
					result = true;
					//System.out.println("Moving forward : ");
				}
			}else{
				int dir = -1;
				if  ( ( getOrientation() == World.NORTH && nextOrientation == World.EAST ) || 
					  ( getOrientation() == World.EAST && nextOrientation == World.SOUTH ) || 
					  ( getOrientation() == World.SOUTH && nextOrientation == World.WEST ) || 
					  ( getOrientation() == World.WEST && nextOrientation == World.NORTH ) ){
					dir = 1;
				}
				if ( dir>  0 ){
					//System.out.println("Turning right");
					turnRIGHT();
				}else{
					//System.out.println("Turning left");
					turnLEFT();
				}
			}
		}
		return result;
	}
	
	
	List<Field> getAllSuccesors(Memory memory, Field current, Field finish ){
		Field[] possibleN = new Field[]{
				new Field(current.X, current.Y +1 ), 
				new Field(current.X, current.Y -1),
				new Field(current.X+1, current.Y ),
				new Field(current.X-1, current.Y )
		};
		
		List<Field> result = new ArrayList<Field>();
		for (int i = 0; i < possibleN.length; i++ ){
			Field n = possibleN[i];
			//TODO::
			//if ( n.isValid(memory.getSize(), memory.getSize() )){
				int fieldVal = memory.getFieldValue( n.X , n.Y );
				//ideme len tam kde to pozname a tam kde nie je stena
				//unknown pripad by ani nemal nastat, lebo vstup bude davat Memory.GetFarestClean  alebo Memory.GetClosestDirty
				if ( fieldVal != World.WALL && fieldVal != UNKNOWN){
					n.prev = current;
					n.dist = n.GetDistance(finish);
					result.add(n);
				}
			//}
		}
		return result;
	} 
	
	public List<Field> Search( Memory memory , Field start , Field finish )
	{
		
		boolean finishFound = false;
		
		PriorityQueue<Field> open = new PriorityQueue<Field>();
		Set<Field> closed = new HashSet<Field>();
		
		open.add(start);
		start.GetDistance(finish);
		
		Field current = null;
		while ( open.size() > 0 ){
			current = open.poll();
			if ( !closed.contains( current) ){
				closed.add(current);
				if ( current.equals( finish ) ){
					finish = current;
					finishFound = true;
					break;
				}

				List<Field> successors = getAllSuccesors(memory, current, finish);
				for (Field succ : successors){
					if (!closed.contains(succ)){
						open.add(succ);
					}
				}
			}
		}
		
		List<Field> path = new ArrayList<Field>();
		if (finishFound){
			current = finish;
			while ( current != null){
				path.add(0,current);
				current = current.prev;
			}
		}
		return path;
	}
	
	Memory mem;
	boolean begin = false;
	List<Field> computedPath = null;
	
	
	
	boolean addNewPercept = false;
	int last_dx = 0 , last_dy = 0;
	
	public void act(){	 
		
		//System.out.println(mem.current);
		//mem.print();
		int[][] per = percept();
		if (addNewPercept){
			mem.AddNewPercept(per , last_dx, last_dy);
			addNewPercept = false;
			//mem.print();
		}
		
		
		if (!begin){
			begin = true;
			mem.AddNewPercept(per , 0, 0);
//			mem.print();
		}else{
			if ( mem.getFieldValue( mem.current.X, mem.current.Y ) == World.DIRTY ){
				suck();
				//mem.VisitPlace(mem.current);
			}else{
				if (mem.isItClean()){
					mem.print();
					halt();
				}
				if (computedPath == null || computedPath.size() == 0){
					Field finish = mem.getClosestDirtyPlace();
					if (finish == null){
						finish =  mem.getFarestCleanPlace();
					}
					if (finish != null){
						computedPath = Search(mem, mem.current , finish);
						//Could not find path to next
						if (computedPath.size() == 0){
							mem.RemovePlace(finish);
						}
					}
				}else{
						Field nextPos = computedPath.get(0);
						
						int xd = nextPos.X - mem.current.X;
						int yd  = nextPos.Y - mem.current.Y;
						boolean move = move(per, xd,yd) ;
						if ( move || ( xd == 0 && yd == 0 ) )
						{
							computedPath.remove(0);
							//mem.VisitPlace(mem.current);
						};
						
						if ( move ){
							//mem.VisitPlace(mem.current);
							addNewPercept = true;
							last_dx = xd;
							last_dy = yd;
							//System.out.println("move");
						}
				}
			}
			mem.VisitPlace(mem.current);
		}
		
	}	
}
