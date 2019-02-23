public class Constants {
	public static final double VELOCITY_VARIATION = 0.0254*2;

	public static final double VELOCITY_MAX = 4;
	public static final double USER_CONTROL_TURN_RATE = Math.PI; // rad per second
	public static final double USER_CONTROL_ACCELERATION = 2; // m/s^2

	public static final double GUIDANCE_STOP = 0.1;
	public static final double STOP_AT = 0.8;
	
	public static final double OFF_AXIS_KP = 3.5;
	public static final double OFF_AXIS_KI = 0;
	public static final double OFF_AXIS_KD = 0;

	public static final double PARALLAX_KP = -150;
	public static final double PARALLAX_KI = 0;
	public static final double PARALLAX_KD = 0;

	public static final double MIN_PARALLAX_DISTANCE = 1.5;
	public static final double OFF_AXIS_GAIN_BOOST = 2.5;
	
	// distance from target center to either rectangle
	public static double RECTANGLE_DISTANCE = 0.1;
	
	// field dimensions
	public static double HORIZONTAL_DISTANCE = 5;
	public static double VERTICAL_DISTANCE = 5;
	
	// for convenience, our brains don't work in radians but the math does
	public static double STARTING_ANGLE_DEG = 45;
	public static double STARTING_ANGLE = STARTING_ANGLE_DEG * Math.PI / 180;
	public static double HORIZONTAL_STARTING_POINT = 0.5;
	public static double VERTICAL_STARTING_POINT   = 2.2;
	
	
	public static int WIDTH = 1600;
	public static int HEIGHT = 800;
	
	public static double ROBOT_SIZE = 1.0;
	public static double CAMERA_SIZE = 0.00847;
	public static double CAMERA_RADIUS = CAMERA_SIZE / 2;
	
	
	
	public static Coord LEFT_RECTANGLE = new Coord(-RECTANGLE_DISTANCE, 0);
	public static Coord RIGHT_RECTANGLE = new Coord(RECTANGLE_DISTANCE, 0);
	

	// main loop runs at 1000Hz (+ kinematics)
	public static double KINEMATICS_RATE_HZ = 1000.0;
	public static int KINEMATICS_PERIOD_MSEC = (int)(1000.0 / KINEMATICS_RATE_HZ);
	
	public static double DRIVE_FPS = 50.0;
	public static int DRIVE_PERIOD_MSEC = (int)(1000.0 / DRIVE_FPS);
	
	
	public static double CAMERA_FPS = 30.0;
	public static int CAMERA_LATENCY = 1; // cycles
	public static int CAMERA_PERIOD_MSEC = (int)(1000.0 / CAMERA_FPS);
	public static double FOV_DEG = 75;
	public static double FOV_RAD2 = FOV_DEG / 2 * Math.PI / 180;
	public static double FOV_MARKER_LENGTH = 0.5;
	
	public static double ARM_MARKER_LENGTH = 0.8;
	
	
	
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
	
	public static int scaleCoordXToPointX(double x) {
		return (int) (x * WIDTH / (2 * HORIZONTAL_DISTANCE));
	}
	
	public static int coordYToPointY(double y) {
		return (int)
			(HEIGHT * y / VERTICAL_DISTANCE);
	}
}
