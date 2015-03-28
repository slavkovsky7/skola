
public class Vector2 implements Comparable<Vector2> {
	public int X,Y;

	//len pre prioritu vo vyhladavni
	public Vector2 prev = null;
	public double dist = 0;
	
	
	public Vector2(int x, int y){
		this.X = x;
		this.Y = y;
	}
	

	public boolean equals(Object other){
		return this.X == ((Vector2)other).X && this.Y == ((Vector2)other).Y; 
	}
	

	public int hashCode() {
		  return 0;
	}

	@Override
	public int compareTo(Vector2 o) {
		if (this.dist > o.dist){
			return 1;
		}
		if (this.dist < o.dist){
			return -1;
		}
		
		return 0;
	}
	
	public double GetDistance(Vector2 f){
		double xsquared = Math.pow( f.X - this.X,2 );
		double ysquared = Math.pow( f.Y - this.Y,2 );
		return Math.sqrt( xsquared + ysquared );
	}
	

	public String toString(){
		return "["+X + ", " + Y+"]";
	}
	
	public static Vector2 Add(Vector2 v, Vector2 u){
		return new Vector2(v.X + u.X, v.Y + u.Y );
	}

	public static Vector2 Substract(Vector2 v, Vector2 u){
		return new Vector2(v.X - u.X, v.Y - u.Y );
	}
	
	public boolean nextTo(Vector2 other){
		Vector2 l = Vector2.Substract(this, other);
		l = new Vector2(Math.abs(l.X),Math.abs(l.Y));
		return l.equals(new Vector2(0, 1)) || l.equals( new Vector2(1,0));
	}
}
