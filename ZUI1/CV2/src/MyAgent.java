import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Queue;
import java.util.Set;

import org.ietf.jgss.Oid;


public  class MyAgent extends Agent{	
	
	public MyAgent(int height, int width) {
	}

	boolean fuck = false;
	
	static class State{
		public int x, y, val;
		public State prev = null;
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
	}
		
	List<State> getAllSuccesors(int[][] net, State state ){
		State[] possibleN = new State[]{
				new State(state.x, state.y +1 ), 
				new State(state.x, state.y -1),
				new State(state.x+1, state.y ),
				new State(state.x-1, state.y )
		};
		
		int w = net.length;
		int h = net[0].length;
		List<State> result = new ArrayList<State>();
		for (int i = 0; i < possibleN.length; i++ ){
			State n = possibleN[i];
			if ( n.isValid(w, h)){
				n.WithVal( net[n.y][n.x] );
				if ( (state.val == World.CLEAN && n.val == World.DIRTY ) ||
					 (state.val == World.DIRTY && n.val == World.CLEAN ) ){
						result.add(n);
						n.prev = state;
				}
			}
		}
		return result;
	} 
	
	
	/*
	OPEN - the queue of states that will be visited
	CLOSE - the set of already visited states
	State - variable containg state that is currently visited
	SucState - variable containg a successor state of State

	1. OPEN = {starting-state}
	2. CLOSE = {}
	3. WHILE (OPEN IS NOT EMPTY)
	4.    State = get and remove first element from OPEN
	5.    if (State not in CLOSE)
	6.		add State to CLOSE
	7.			if (State is our goal)
	8.	    		return path from starting-state to State
	9.		for all successors SucState of State
	10.	    	if (SucState  ∉ CLOSE)
	11.				add SucState as last to OPEN

	12. return failure*/
	
	public List<State> BreadthSearch( int[][] net , State start, State finish )
	{
		
		Queue<State> open = new LinkedList<MyAgent.State>();
		Set<State> closed = new HashSet<State>();
		
		open.add(start);
		State current = null;
		while ( open.size() > 0 ){
			current = open.poll();
			if ( !closed.contains( current) ){
				closed.add(current);
				if ( current.equals(finish) ){
					finish = current;
					break;
				}

				List<State> successors = getAllSuccesors(net, current);
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
					computedPath.remove(0);
				}else{
					turnRIGHT();
				}
			}
		}
	}
	
	List<State> computedPath = null;
	public void act(){		
		if (computedPath == null){
			int net[][] = percept();
			int center = (net.length / 2) ;		
			computedPath = BreadthSearch(net, new State(center, center), new State(17, 20));
		}else{
			move();
		}
		/* ZACIATOK MIESTA PRE VAS KOD */
	}	
}
