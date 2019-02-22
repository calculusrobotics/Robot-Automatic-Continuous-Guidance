
public class Robot {
	public static final double SIZE2 = Constants.ROBOT_SIZE / 2;
	public static final double DIAG = Math.sqrt(2) * SIZE2;
	
	private Coord pos = new Coord(Constants.HORIZONTAL_DISTANCE / 2.5, Constants.VERTICAL_DISTANCE / 2);
	private Coord camera_pos = null;
	// 0 = straight up
	// + = looking to the left
	// - = looking to the right
	private double angle = 0;
	
	private double velocityVariation = Constants.VELOCITY_VARIATION;
	
	
	
	// meters per second
	private double linearVelocity = Constants.VELOCITY_MAX;
	
	
	
	private Controller controlLoop_parallax = new Controller(-200, 0, 0);
	private Controller controlLoop_offAxis = new Controller(2, 0, 0);
	
	
	
	public double getOffAxis() {
		double robotAngle = Math.atan(pos.getX() / pos.getY());
		
		return robotAngle - angle;
	}
	
	public double getParallax() {
		double distLeft = camera_pos.distTo(Constants.LEFT_RECTANGLE);
		double distRight = camera_pos.distTo(Constants.RIGHT_RECTANGLE);
		
		
		
		// pixel height is inversely proportional to the distance for a frustum based camera
		return ((1 / distLeft) - (1 / distRight)) / ((1 / distLeft) + (1 / distRight));
	}
	
	
	
	public void drive() {
		double x = pos.getX();
		double y = pos.getY();
		
		double variation = velocityVariation * (2 * Math.random() - 1);
		
		x -= (linearVelocity + variation) * Math.sin(angle) / Constants.FPS;
		y -= (linearVelocity + variation) * Math.cos(angle) / Constants.FPS;
		
		pos.setX(x);
		pos.setY(y);
		
		recalculateCamera();
	}
	
	/**
	 * @param omega angular rate (rad/s)
	 */
	public void rotate(double omega) {
		angle += omega / Constants.FPS;
		
		angle %= 360;
		
		recalculateCamera();
	}
	
	
	
	private void recalculateCamera() {
		double x = pos.getX() - SIZE2 * Math.sin(angle);
		double y = pos.getY() - SIZE2 * Math.cos(angle);
		
		camera_pos = new Coord(x, y);
	}
	
	
	
	public void update() {
		recalculateCamera();
		
		double parallax_contribution = controlLoop_parallax.newValue(getParallax());
		double offAxis_contribution  = controlLoop_offAxis .newValue(getOffAxis());
		
		double omega = parallax_contribution + offAxis_contribution;
		
		rotate(omega);
		
		drive();
		
		//System.out.println(getParallax() + " - " + getOffAxis());
	}
	
	
	
	public Coord getPos() {
		return pos;
	}
	
	public void setPos(Coord c) {
		pos = c;
	}
	
	
	
	
	
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
	
	public Coord getCamera() {
		return camera_pos;
	}
}
