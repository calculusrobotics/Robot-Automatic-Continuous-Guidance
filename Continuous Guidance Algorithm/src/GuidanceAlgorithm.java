
public class GuidanceAlgorithm {
	private static Controller controlLoop_parallax = new Controller(Constants.PARALLAX_KP,
			                                                        Constants.PARALLAX_KI,
			                                                        Constants.PARALLAX_KD);
	private static Controller controlLoop_offAxis = new Controller(Constants.OFF_AXIS_KP,
			                                                       Constants.OFF_AXIS_KI,
			                                                       Constants.OFF_AXIS_KD);
	
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
