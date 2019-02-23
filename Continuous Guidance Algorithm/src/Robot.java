
public class Robot {
	public static final double SIZE2 = Constants.ROBOT_SIZE / 2;
	public static final double DIAG = Math.sqrt(2) * SIZE2;
	
	
	
	/**
	 * Put angle in [-PI, PI]
	 */
	private static double mag180(double angle) {
		angle += Math.PI;
		angle %= (2 * Math.PI);
		if (angle < 0) {
			angle += 2 * Math.PI;
		}
		angle -= Math.PI;
		
		return angle;
	}
	
	
	
	private Coord pos = new Coord(Constants.HORIZONTAL_STARTING_POINT, Constants.VERTICAL_STARTING_POINT);
	private Coord camera_pos = new Coord(
		Constants.HORIZONTAL_STARTING_POINT,
		Constants.VERTICAL_STARTING_POINT // center of robot
	);
	// 0 = straight up
	// + = looking to the left
	// - = looking to the right
	private double angle = Constants.STARTING_ANGLE;
	private double omega = 0; // turn rate
	
	private double velocityVariation = Constants.VELOCITY_VARIATION;
	
	
	
	// meters per second
	private double linearVelocity = Constants.VELOCITY_MAX;
	
	public void setVelocity(double velocity) {
		linearVelocity = velocity;
	}
	
	
	
	public double getOffAxis() {
		double robotAngle = Math.atan(pos.getX() / pos.getY());
		
		return mag180(robotAngle - angle);
	}
	
	public double getLeftOffAxis() {
		Coord vec = new Coord(
			pos.getX() - Constants.LEFT_RECTANGLE.getX(),
			pos.getY() - Constants.LEFT_RECTANGLE.getY()
		);
		
		double rectangleAngle = Math.atan(vec.getX() / vec.getY());
		
		return mag180(rectangleAngle - angle);
	}
	
	public double getRightOffAxis() {
		Coord vec = new Coord(
			pos.getX() - Constants.RIGHT_RECTANGLE.getX(),
			pos.getY() - Constants.RIGHT_RECTANGLE.getY()
		);
		
		double rectangleAngle = Math.atan(vec.getX() / vec.getY());
		
		return mag180(rectangleAngle - angle);
	}
	
	
	
	public double getParallax() {
		double distLeft = camera_pos.distTo(Constants.LEFT_RECTANGLE);
		double distRight = camera_pos.distTo(Constants.RIGHT_RECTANGLE);
		
		
		
		// pixel height is inversely proportional to the distance for a frustum based camera
		return ((1 / distLeft) - (1 / distRight)) / ((1 / distLeft) + (1 / distRight));
	}
	
	
	
	/**
	 * @param omega angular rate (rad/s)
	 */
	public void rotate(double omega) {
		this.omega = omega;
	}
	
	
	
	public void stop() {
		linearVelocity = 0;
		omega = 0;
	}
	
	
	
	public void updateKinematics() {
		double diffVelocity = omega * Constants.ROBOT_SIZE / 2.0;
		
		
		
		double leftVelocity = linearVelocity - diffVelocity;
		if (Math.abs(leftVelocity) < velocityVariation) {
			leftVelocity = 0;
		} else {
			leftVelocity += velocityVariation * (2 * Math.random() - 1);
		}
		
		double rightVelocity = linearVelocity + diffVelocity;
		if (Math.abs(rightVelocity) < velocityVariation) {
			rightVelocity = 0;
		} else {
			rightVelocity += velocityVariation * (2 * Math.random() - 1);
		}
		
		
		
		double real_linearVelocity = 1/2.0 * (leftVelocity + rightVelocity);
		double real_omega = 2 * (rightVelocity - leftVelocity) / Constants.ROBOT_SIZE;
		
		
		
		angle += real_omega / Constants.KINEMATICS_RATE_HZ;
		
		angle = mag180(angle);
		
		
		
		// robot center
		double x = pos.getX();
		double y = pos.getY();

		x -= real_linearVelocity * Math.sin(angle) / Constants.KINEMATICS_RATE_HZ;
		y -= real_linearVelocity * Math.cos(angle) / Constants.KINEMATICS_RATE_HZ;
		
		pos.setX(x);
		pos.setY(y);
		
		
		
		// camera center = robot center
		x = pos.getX();
		y = pos.getY();
		
		camera_pos.setX(x);
		camera_pos.setY(y);
	}
	
	
	
	public CameraFeedback updateCamera() {
		double leftAngle = getLeftOffAxis();
		double rightAngle = getRightOffAxis();
		
		boolean inFOV = !(
			Math.abs(leftAngle) >= Constants.FOV_RAD2 ||
			Math.abs(rightAngle) >= Constants.FOV_RAD2
		);
		
		boolean inAutoAssistRegion = (
			pos.getX() * pos.getX() -
			pos.getY() * pos.getY()
		) <= Constants.RECTANGLE_DISTANCE * Constants.RECTANGLE_DISTANCE;
		
		return new CameraFeedback(inFOV, inAutoAssistRegion, getParallax(), getOffAxis());
	}
	
	
	
	
	
	// graphics stuff
	public Coord getTopLeft() {
		double x = pos.getX() - DIAG * Math.sin(angle + Math.PI / 4);
		double y = pos.getY() - DIAG * Math.cos(angle + Math.PI / 4);
		
		return new Coord(x, y);
	}
	
	public Coord getTopRight() {
		double x = pos.getX() - DIAG * Math.sin(angle - Math.PI / 4);
		double y = pos.getY() - DIAG * Math.cos(angle - Math.PI / 4);
		
		return new Coord(x, y);
	}
	
	public Coord getBottomLeft() {
		double x = pos.getX() - DIAG * Math.sin(angle + 3 * Math.PI / 4);
		double y = pos.getY() - DIAG * Math.cos(angle + 3 * Math.PI / 4);
		
		return new Coord(x, y);
	}
	
	public Coord getBottomRight() {
		double x = pos.getX() - DIAG * Math.sin(angle - 3 * Math.PI / 4);
		double y = pos.getY() - DIAG * Math.cos(angle - 3 * Math.PI / 4);
		
		return new Coord(x, y);
	}
	
	public Coord getFOVMarker1() {
		double x = camera_pos.getX() - Constants.FOV_MARKER_LENGTH * Math.sin(angle + Constants.FOV_RAD2);
		double y = camera_pos.getY() - Constants.FOV_MARKER_LENGTH * Math.cos(angle + Constants.FOV_RAD2);
		
		return new Coord(x, y);
	}
	
	public Coord getFOVMarker2() {
		double x = camera_pos.getX() - Constants.FOV_MARKER_LENGTH * Math.sin(angle - Constants.FOV_RAD2);
		double y = camera_pos.getY() - Constants.FOV_MARKER_LENGTH * Math.cos(angle - Constants.FOV_RAD2);
		
		return new Coord(x, y);
	}

	public Coord getArmMarker() {
		double x = camera_pos.getX() - Constants.ARM_MARKER_LENGTH * Math.sin(angle);
		double y = camera_pos.getY() - Constants.ARM_MARKER_LENGTH * Math.cos(angle);
		
		return new Coord(x, y);
	}
	
	
	public Coord getCamera() {
		return camera_pos;
	}
}
