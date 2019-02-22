
public class GuidanceAlgorithm {
	private static Controller controlLoop_parallax = new Controller(-200, 0, 0);
	private static Controller controlLoop_offAxis = new Controller(2, 0, 0);
	
	private static double parallax;
	private static double offAxis;
	
	
	
	public static double getTurnRate() {
		double parallax_contribution = controlLoop_parallax.newValue(parallax);
		double offAxis_contribution  = controlLoop_offAxis .newValue(offAxis);
		
		double omega = parallax_contribution + offAxis_contribution;
		
		return omega;
	}
	
	
	
	public static void setParallax(double p) {
		parallax = p;
	}
	
	public static void setOffAxis(double o) {
		offAxis = o;
	}
}
