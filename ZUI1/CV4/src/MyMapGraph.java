import java.nio.channels.FileChannel.MapMode;
import java.util.*;

public class MyMapGraph extends MapGraph{
	
	public List<MapNode> getNeighbours(MapNode state){
		List<MapNode> result = new ArrayList<MapNode>();
		for (int i = 0 ; i < states.length; i++){
			if (isBorder( state.GetName(), states[i].GetName() ) && !states[i].GetName().equals(state.GetName())){
				result.add(states[i]);
			}
		}
		return result;
	}
	
	private static final String RED = "red";
	private static final String GREEN= "green";
	private static final String BLUE = "blue";
	private static final String NONE = "none";
	
	public Set<String> GetAllColors(){
		Set<String> result = new HashSet<String>();
		result.add(RED);
		result.add(GREEN);
		result.add(BLUE);
		return result;
	}
	
	public int GetPossibleColors(MapNode state){
		Set<String> colorSet = GetAllColors();
		List<MapNode> neigbours = getNeighbours(state);
		for ( MapNode n : neigbours){
			neigbours.ad
		}
		return 0;
	}
	
	public void colorGraph(){
		Stack<MapNode> stack = new Stack<MapNode>();
		Set<MapNode> visited = new HashSet<MapNode>();
		stack.push( states[0] );
		while ( !stack.isEmpty() ){
			MapNode current = stack.pop();
			if (!visited.contains( current ) ){
				List<MapNode> neigbours = getNeighbours(current);
				for ( MapNode n : neigbours){
					if ( !visited.contains(n) ){
						stack.add(n);
					}
				}
				System.out.println(current.GetName());
				visited.add(current);
			}
			
		}
	}	
}