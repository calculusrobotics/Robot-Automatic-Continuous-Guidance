
public class Point {
	private final int X;
	private final int Y;
	
	public Point(int x, int y) {
		X = x;
		Y = y;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	@Override
	public String toString() {
		return "(" + X + ", " + Y + ")";
	}
}
