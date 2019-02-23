import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;





/**
 * @author Howard Beck (calculusrobotics), Mike Kessel (RocketRedNeck)
 */
public class Simulator extends JPanel {
	// we <3 Java
	private static final long serialVersionUID = 1L;
	
	
	
	private static final int WIDTH = Constants.WIDTH;
	private static final int HEIGHT = Constants.HEIGHT;
	
	
	
	private JFrame frame;
	
	private Robot oppy;
	
	
	
	private boolean userControl = true;
	private double user_velocity = 0;
	private double user_acceleration = 0;
	
	// BucketTables, basically
	private boolean inFOV = false;
	private boolean inAutoAssistRegion = false;
	private double parallax = 0;
	private double offAxis = 0;
	
	
	
	
	
	public Simulator() {
		frame = new JFrame("Roomba Guidance Algorithm");
		
		frame.addKeyListener(new ManualControl());
		frame.add(this);
		
		oppy = new Robot();
		
		
		
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		int cycles = 0;
		
		double omega = 0; // turn rate to give to drive subsystem
		
		boolean cont = true;
		
		while (cont) {
			try {
				double distance = oppy.getCamera().distTo(new Coord(0, 0));
				
				if (!userControl) {
					if (cycles % Constants.CAMERA_PERIOD_MSEC == 0) {
						CameraFeedback cameraData = oppy.updateCamera();
						
						inFOV = cameraData.isInFOV();
						inAutoAssistRegion = cameraData.isInAutoAssistRegion();
						if (inFOV) {
							parallax = cameraData.getParallax();
							offAxis = cameraData.getOffAxis();
						} else {
							GuidanceAlgorithm.reset();
						}
					}
					
					if ((cycles % Constants.CAMERA_PERIOD_MSEC == Constants.CAMERA_LATENCY) &&
						(distance > Constants.GUIDANCE_STOP)) { // latency on camera feed data
						// feed data from last capture to guidance algorithm
						if (inFOV) {
							oppy.setVelocity(Constants.VELOCITY_MAX);
							
							GuidanceAlgorithm.setParallax(parallax);
							GuidanceAlgorithm.setOffAxis(offAxis);
							
							omega = GuidanceAlgorithm.getTurnRate(distance);
						} else {
							// reduced velocity
							oppy.setVelocity(Constants.VELOCITY_MAX);
							
							// use last offAxis in hopes that it will get rectangles in FOV
							omega = Constants.OFF_AXIS_KP * offAxis;
						}
					}
					
					if (cycles % Constants.DRIVE_PERIOD_MSEC == 0) {
						oppy.rotate(omega); // give it a new turn rate
					}
				} else {
					// keep using camera when in user control
					// (to test if tape in FOV and if in auto assist region)
					if (cycles % Constants.CAMERA_PERIOD_MSEC == 0) {
						CameraFeedback cameraData = oppy.updateCamera();
						
						inFOV = cameraData.isInFOV();
						inAutoAssistRegion = cameraData.isInAutoAssistRegion();
						parallax = cameraData.getParallax();
						offAxis = cameraData.getOffAxis();
					}
					
					
					
					user_velocity += user_acceleration / Constants.KINEMATICS_RATE_HZ;

					if (Math.abs(user_velocity) >= Constants.VELOCITY_MAX) {
						user_velocity = Math.signum(user_velocity) * Constants.VELOCITY_MAX;
					}
					
					oppy.setVelocity(user_velocity);
				}
				
				
				
				oppy.updateKinematics();
				frame.repaint();
				
				// if sufficiently close, stop running simulator
				
				if (distance <= Constants.STOP_AT) {
					cont = false;
				}
				
				
				
				Thread.sleep(1L);
				
				cycles++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// paint background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		
		
		// region inside of peaks
		g.setColor(Color.GRAY);
		for (int i = 0; i <= HEIGHT; i++) {
			double y = Constants.pointYToCoordY(i);
			double x = Math.sqrt(y * y + Math.pow(Constants.RECTANGLE_DISTANCE, 2));
			
			Point left = Constants.toPoint(new Coord(-x, y));
			Point right = Constants.toPoint(new Coord(x, y));
			
			g.drawLine(left.getX(), left.getY(), left.getX(), left.getY());
			g.drawLine(right.getX(), right.getY(), right.getX(), right.getY());
		}
		// threshold distance to stop using parallax
		int xRadius = Constants.scaleCoordXToPointX(Constants.MIN_PARALLAX_DISTANCE);
		int yRadius = Constants.coordYToPointY(Constants.MIN_PARALLAX_DISTANCE);
		g.drawOval(WIDTH / 2 - xRadius, -yRadius, 2 * xRadius, 2 * yRadius);
		
		
		
		// robot border
		g.setColor(Color.BLACK);
		Point topLeft = Constants.toPoint(oppy.getTopLeft());
		Point topRight = Constants.toPoint(oppy.getTopRight());
		Point bottomLeft = Constants.toPoint(oppy.getBottomLeft());
		Point bottomRight = Constants.toPoint(oppy.getBottomRight());
		
		g.drawLine(topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY());
		g.drawLine(topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY());
		g.drawLine(bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
		g.drawLine(bottomLeft.getX(), bottomLeft.getY(), topLeft.getX(), topLeft.getY());
				
		// robot camera
		if (inAutoAssistRegion) {
			g.setColor(Color.GREEN);
		} else {
			g.setColor(Color.RED);
		}
		Point camera = Constants.toPoint(oppy.getCamera());
		
		g.fillOval(
			camera.getX() - 5, camera.getY() - 5,
			10, 10 
		);
		
		// FOV
		if (inFOV) {
			g.setColor(Color.GREEN);
		} else {
			g.setColor(Color.RED);
		}
		Point fovMarker1 = Constants.toPoint(oppy.getFOVMarker1());
		Point fovMarker2 = Constants.toPoint(oppy.getFOVMarker2());
		Point armMarker = Constants.toPoint(oppy.getArmMarker());
		
		g.drawLine(
			camera.getX(), camera.getY(),
			fovMarker1.getX(), fovMarker1.getY()
		);
		g.drawLine(
				camera.getX(), camera.getY(),
				fovMarker2.getX(), fovMarker2.getY()
			);
		
		g.setColor(Color.BLACK);
		g.drawLine(
				camera.getX(), camera.getY(),
				armMarker.getX(), armMarker.getY()
			);
	}
	
	
	
	
	
	private class ManualControl implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			
			
			
			if (code == KeyEvent.VK_ENTER) {
				userControl = !userControl;
				
				oppy.stop();
				user_velocity = 0;
				user_acceleration = 0;
			}
			
			if (userControl) {
				if (code == KeyEvent.VK_RIGHT) {
					oppy.rotate(-Constants.USER_CONTROL_TURN_RATE);
				} else if (code == KeyEvent.VK_LEFT) {
					oppy.rotate(Constants.USER_CONTROL_TURN_RATE);
				} else if (code == KeyEvent.VK_UP) {
					user_acceleration = Constants.USER_CONTROL_ACCELERATION;
				} else if (code == KeyEvent.VK_DOWN) {
					user_acceleration = -Constants.USER_CONTROL_ACCELERATION;
				} else if (code == KeyEvent.VK_SPACE) {
					oppy.rotate(0);
					
					user_velocity = 0;
					user_acceleration = 0;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int code = e.getKeyCode();
			
			if (userControl) {
				if (code == KeyEvent.VK_RIGHT) {
					oppy.rotate(0);
				} else if (code == KeyEvent.VK_LEFT) {
					oppy.rotate(0);
				} else if (code == KeyEvent.VK_UP) {
					user_acceleration = 0;
				} else if (code == KeyEvent.VK_DOWN) {
					user_acceleration = 0;
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
	
	
	
	
	public static void main(String[] args) {
		new Simulator();
	}
}
