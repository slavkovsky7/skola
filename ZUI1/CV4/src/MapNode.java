
public class MapNode {

	private String name;
	private String color;
	
	public MapNode(String parName) {
		this.name = parName;
		this.color = "none";
	}
	
	public String GetName(){
		return this.name;
	}
	
	public String GetColor(){
		return this.color;
	}
	
	public void SetColor(String parColor){
		this.color = parColor;
	}
}