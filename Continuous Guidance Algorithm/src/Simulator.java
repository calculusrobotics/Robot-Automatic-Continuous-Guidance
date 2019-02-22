import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;





/**
 * @author Howard Beck (calculusrobotics)
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
	
	
	
	
	
	public Simulator() {
		frame = new JFrame("Roomba Guidance Algorithm");
		
		frame.addKeyListener(new ManualControl());
		frame.add(this);
		
		oppy = new Robot();
		
		
		
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		int cycles = 0;
		
		// BucketTables, basically
		double parallax = 0;
		double offAxis = 0;
		
		double omega = 0; // turn rate to give to drive subsystem
		
		boolean cont = true;
		
		while (cont) {
			try {
				double distance = oppy.getCamera().distTo(new Coord(0, 0));
				
				if (!userControl) {
					if (cycles % Constants.CAMERA_PERIOD_MSEC == 0) {
						double cameraData[] = oppy.updateCamera();
						
						parallax = cameraData[0];
						offAxis = cameraData[1];
					}
					
					if ((cycles % Constants.CAMERA_PERIOD_MSEC == Constants.CAMERA_LATENCY) &&
						(distance > Constants.GUIDANCE_STOP)) { // latency on camera feed data
						// feed data from last capture to guidance algorithm
						GuidanceAlgorithm.setParallax(parallax);
						GuidanceAlgorithm.setOffAxis(offAxis);
						
						omega = GuidanceAlgorithm.getTurnRate();
					}
					
					if (cycles % Constants.DRIVE_PERIOD_MSEC == 0) {
						oppy.rotate(omega); // give it a new turn rate
					}
					
					cycles++;
					
					oppy.setVelocity(Constants.VELOCITY_MAX);
				} else {
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
		g.setColor(Color.GREEN);
		Point camera = Constants.toPoint(oppy.getCamera());
		
		g.fillOval(
			camera.getX() - 5, camera.getY() - 5,
			10, 10 
		);
	}
	
	
	
	
	
	private class ManualControl implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			
			
			
			if (code == KeyEvent.VK_ENTER) {
				userControl = !userControl;
				
				oppy.rotate(0); // stop rotating
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
