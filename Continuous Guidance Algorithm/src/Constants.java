public class Constants {
	
	public static double VELOCITY_VARIATION = 0.03; // m/s
	public static double VELOCITY_MAX = 3.0;        // m/s
	
	public static double RECTANGLE_DISTANCE = 0.1;
	
	public static double HORIZONTAL_DISTANCE = 10;
	public static double VERTICAL_DISTANCE = 10;
	
	public static int WIDTH = 1600;
	public static int HEIGHT = 800;
	
	public static double ROBOT_SIZE = 1.0;
	public static double CAMERA_SIZE = 0.05;
	public static double CAMERA_RADIUS = CAMERA_SIZE / 2;
	
	
	
	public static Coord LEFT_RECTANGLE = new Coord(-RECTANGLE_DISTANCE, 0);
	public static Coord RIGHT_RECTANGLE = new Coord(RECTANGLE_DISTANCE, 0);
	
	
	
	public static double FPS = 50;
	
	
	
	public static Point toPoint(Coord c) {
		int x = coordXToPointX(c.getX());
		int y = coordYToPointY(c.getY());
		
		return new Point(x, y);
	}
	
	public static double pointXToCoordX(int x) {
		return x * 2 * HORIZONTAL_DISTANCE / WIDTH - HORIZONTAL_DISTANCE;
	}
	
	public static double pointYToCoordY(int y) {
		return y * VERTICAL_DISTANCE / HEIGHT;
	}
	
	public static int coordXToPointX(double x) {
		return (int)
			(WIDTH * (x + HORIZONTAL_DISTANCE) / (2 * HORIZONTAL_DISTANCE));
	}
	
	public static int coordYToPointY(double y) {
		return (int)
			(HEIGHT * y / VERTICAL_DISTANCE);
	}
}
