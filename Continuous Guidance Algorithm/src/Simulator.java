import java.awt.Color;
import java.awt.Graphics;

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
	
	
	
	
	
	public Simulator() {
		frame = new JFrame("Roomba Guidance Algorithm");
		
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
				if (cycles % Constants.CAMERA_FPS == 0) {
					double cameraData[] = oppy.updateCamera();
					
					parallax = cameraData[0];
					offAxis = cameraData[1];
				}
				if (cycles % Constants.CAMERA_FPS == Constants.CAMERA_LATENCY) { // latency on camera feed data
					// feed data from last capture to guidance algorithm
					GuidanceAlgorithm.setParallax(parallax);
					GuidanceAlgorithm.setOffAxis(offAxis);
					
					omega = GuidanceAlgorithm.getTurnRate();
				}
				
				if (cycles % Constants.DRIVE_FPS == 0) {
					oppy.rotate(omega); // give it a new turn rate
				}
				
				oppy.updateKinematics();
				frame.repaint();
				
				// if sufficiently close (2 in), stop running simulator
				double distance = oppy.getCamera().distTo(new Coord(0, 0));
				
				if (distance <= 0.0254 * 2) {
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
	
	
	
	public static void main(String[] args) {
		new Simulator();
	}
}
