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
	
	public MyAgent(int height, int width) {
	}

	static class State implements Comparable<State>{
		public int x, y, val;
		public State prev = null;
		public double dist = 0;
		public State(int x, int y){
			this.x = x;
			this.y = y;
			this.val = 0; 
		}
		
		public boolean equals(Object other){
			return this.x == ((State)other).x && this.y == ((State)other).y; 
		}
		
		public boolean isValid(int w, int h){
			return x > 0 && y > 0 && x < w & y < h ;
		}
		
		public State WithVal(int val){
			this.val = val;
			return this;
		}
		
		public String toString(){
			return "["+x + ", " + y+", "+val+"]";
		}
		
		public int hashCode() {
			  return 0;
		}

		@Override
		public int compareTo(State o) {
			if (this.dist > o.dist){
				return 1;
			}
			if (this.dist < o.dist){
				return -1;
			}
			
			return 0;
		}
		
		public double GetDistance(MyAgent.State s){
			double xsquared = Math.pow( s.x - this.x,2 );
			double ysquared = Math.pow( s.y - this.y,2 );
			return Math.sqrt( xsquared + ysquared );
		}
		
		
	}
	
	private static class Memory{
		
		private int[][] internal;
		private int currentX;
		private int currentY;
		
		public Memory(int size){
			size += 1;
			internal = new int[size][];
			for (int i = 0 ; i < size; i++){
				internal[i] = new int[size];
				for (int j = 0 ; j < size; j++){
					internal[i][j] = World.UNKNOWN;
				}
			}
			
			currentX = size / 2;
			currentY = size / 2;
		}
		
		public int getState(int x, int y){
			return internal[y][x];
		}
		
		
		public int getSize(){
			return internal.length;
		}
		
		public void AddNewPercept(int[][] percept, int dx , int dy ){
			for (int y = 0 ; y < percept.length; y++){
				for (int x = 0 ; x < percept[y].length; x++){
					//Ak nie je visited, tak uloz do pamati, zmen z unknown na CLEAN,DIRTY,WALL
					if ( new State(currentX + y, currentX + x).isValid(internal.length, internal.length ) ){
						if (internal[currentX + y][currentX + x] != World.VISITED){ 
							internal[currentX + y][currentX + x] = percept[y][x];
						}
					}
				}	
			}
			currentX += dx;
			currentY += dy;
		}
	}
		
	//vstup bude pamat + to co vidi, teda net[][], ulozim net do pamate
	List<State> getAllSuccesors(Memory memory, State state  ){
		State[] possibleN = new State[]{
				new State(state.x, state.y +1 ), 
				new State(state.x, state.y -1),
				new State(state.x+1, state.y ),
				new State(state.x-1, state.y )
		};
		
		List<State> result = new ArrayList<State>();
		for (int i = 0; i < possibleN.length; i++ ){
			State n = possibleN[i];
			if ( n.isValid(memory.getSize(), memory.getSize() )){
				n.WithVal( memory.getState(n.x , n.y ) );
				if (n.val != World.WALL){
					n.prev = state;
					n.dist = n.GetDistance(finish);
					result.add(n);
				}
			}
		}
		return result;
	} 
	
	//tu bude memory... miesto net[][]
	public List<State> BreadthSearch( Memory memory , State start )
	{
		
		PriorityQueue<State> open = new PriorityQueue<MyAgent.State>();
		Set<State> closed = new HashSet<State>();
		
		open.add(start);
		start.GetDistance(finish);
		
		State current = null;
		while ( open.size() > 0 ){
			current = open.poll();
			if ( !closed.contains( current) ){
				closed.add(current);
				if ( current.val == World.DIRTY ){
					finish = current;
					break;
				}

				List<State> successors = getAllSuccesors(memory, current, finish);
				for (State succ : successors){
					if (!closed.contains(succ)){
						open.add(succ);
					}
				}
			}
		}
		
		List<State> path = new ArrayList<MyAgent.State>();
		current = finish;
		while ( current != null){
			path.add(0,current);
			current = current.prev;
		}
		
		return path;
	}
	
	
	public static int[] getOrientationVec(int or){
		int[] res = new int[2];
		switch (or){
			case World.NORTH:
				res[0] = 0;
				res[1] = 1;
				break;
			case World.EAST:
				res[0] = 1;
				res[1] = 0;
				break;
			case World.SOUTH:
				res[0] = 0;
				res[1] = -1;
				break;
			case World.WEST:
				res[0] = -1;
				res[1] = 0;
				break;
		}
		return res;
	}
	
	
	public void move(){
		if (computedPath.size() > 1){
			State currentPos = computedPath.get(0);
			State nextPos = computedPath.get(1);
			
			int xd = nextPos.x - currentPos.x;
			int yd = nextPos.y - currentPos.y;
			
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
					moveFW();
					mem.AddNewPercept(percept(), xd, yd );
					System.out.println("Moving forward");
					computedPath.remove(0);
				}else{
					int dir = -1;
					if  ( ( getOrientation() == World.NORTH && nextOrientation == World.EAST ) || 
						  ( getOrientation() == World.EAST && nextOrientation == World.SOUTH ) || 
						  ( getOrientation() == World.SOUTH && nextOrientation == World.WEST ) || 
						  ( getOrientation() == World.WEST && nextOrientation == World.NORTH ) ){
						dir = 1;
					}
					if ( dir>  0 ){
						System.out.println("Turning right");
						turnRIGHT();
					}else{
						System.out.println("Turning left");
						turnLEFT();
					}
				}
			}
		}
	}
	
	
	//properties....
	List<State> computedPath = null;
	Memory mem = new Memory( 100 );
	
	public void act(){		
		
		if (computedPath == null || computedPath.size() == 0 ){
			int net[][] = percept();
			int center = (net.length / 2) ;		
			//tu sa bude hybat, pri kazdom pohybe sa rozsiry memory
			computedPath = BreadthSearch(mem, new State(center, center), new State(17, 20));
			
		}else{
			move();
		}
	}	
}

