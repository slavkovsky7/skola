public abstract class Agent {

	
	protected World world;	
	private boolean running;	
	public Agent(World world){
		this.world = world;
		running = true;
	}
	
	/**
	*zaradi agenta do sveta
	**/
	public void putInWorld(World w){
		world= w;
	}
	
	public abstract int act(int plocha[][], int[] tahy, int cutoff);
			
		
	/* =========================================================================================== */
	/* ================================= DO NOT MODIFY ============================================ */
	/* =========================================================================================== */
	
	public void halt(){
		running = false;
	}
	
	public boolean isRunning(){
		return running;
	}
	

	

	

	/* =========================================================================================== */
	/* ================================= DO NOT USE ============================================ */
	/* =========================================================================================== */
	

    
}
