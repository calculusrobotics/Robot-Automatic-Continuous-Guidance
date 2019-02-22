
public class Coord {
	private double x;
	private double y;
	
	
	
	public Coord(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	
	
	public double distTo(Coord c) {
		return Math.sqrt(
			Math.pow(c.getX() - x, 2) +
			Math.pow(c.getY() - y, 2) 
		);
	}
}
