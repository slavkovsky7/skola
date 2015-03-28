public abstract class MapGraph {

	public MapNode[] states;
	private MapEdge[] borders;
	
	public MapGraph() {
		
		this.states = new MapNode[7];
		this.borders = new MapEdge[9];
		
		this.states[0] = new MapNode("Western Australia");
		this.states[1] = new MapNode("Northern Territory");
		this.states[2] = new MapNode("South Australia");
		this.states[3] = new MapNode("Queensland");
		this.states[4] = new MapNode("New South Wales");
		this.states[5] = new MapNode("Victoria");
		this.states[6] = new MapNode("Tasmania");
		
		this.borders[0] = new MapEdge("Western Australia","Northern Territory");
		this.borders[1] = new MapEdge("Western Australia","South Australia");
		this.borders[2] = new MapEdge("Northern Territory","South Australia");
		this.borders[3] = new MapEdge("Northern Territory","Queensland");
		this.borders[4] = new MapEdge("South Australia","Queensland");
		this.borders[5] = new MapEdge("South Australia","New South Wales");
		this.borders[6] = new MapEdge("South Australia","Victoria");
		this.borders[7] = new MapEdge("Queensland","New South Wales");
		this.borders[8] = new MapEdge("Victoria","New South Wales");
	}
	
	abstract void colorGraph();
	
	public boolean isBorder(String parNode1, String parNode2) {
		boolean ret = false;
		for (int i = 0; i < borders.length; i++) {
			if ((borders[i].node1 == parNode1) && (borders[i].node2 == parNode2)) {
				ret = true;
			}
			if ((borders[i].node2 == parNode1) && (borders[i].node1 == parNode2)) {
				ret = true;
			}
		}
		return ret;
	}
	
	public String displayGraph(){
		String ret = "\n LIST OF STATES AND COLORS:\n";
		for (int i = 0; i < states.length; i++) {
			ret = ret+"  State "+states[i].GetName()+" => "+states[i].GetColor()+"\n";
		}
		ret = ret+"\n";
		return ret;
	}
	
	public String displayErrors(){
		String ret = "";
		int x,y;
		x=0;
		y=0;
		ret = " LIST OF ERRORS:\n";
		for (int i = 0; i<states.length; i++) {
			if (states[i].GetColor() == "none") ret = ret+"  "+states[i].GetName()+" has no color.\n";
		}
		for (int i = 0; i<borders.length; i++) {
			for (int j = 0; j<states.length; j++) {
				if (states[j].GetName() == borders[i].node1) x = j;
				if (states[j].GetName() == borders[i].node2) y = j;
			}
			if ((states[x].GetColor() == states[y].GetColor()) && (states[x].GetColor() != "none")) {
				ret = ret+"  "+states[x].GetName()+" and "+states[y].GetName()+" have the same color.\n";
			}
		}
		return ret;
	}
	
	public void color(String parName, String parColor) {
		for (int i = 0; i<states.length; i++) {
			if (states[i].GetName() == parName) states[i].SetColor(parColor);
		}
	}
}