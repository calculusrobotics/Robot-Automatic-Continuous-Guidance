
public class GuidanceAlgorithm {
	private static Controller controlLoop_parallax = new Controller(Constants.PARALLAX_KP,
			                                                        Constants.PARALLAX_KI,
			                                                        Constants.PARALLAX_KD);
	private static Controller controlLoop_offAxis = new Controller(Constants.OFF_AXIS_KP,
			                                                       Constants.OFF_AXIS_KI,
			                                                       Constants.OFF_AXIS_KD);
	
	private static double parallax;
	private static double offAxis;
	
	
	
	public static double getTurnRate(double distance) {
		double parallax_contribution = controlLoop_parallax.newValue(parallax);
		double offAxis_contribution  = controlLoop_offAxis .newValue(offAxis);
		
		if (distance < Constants.MIN_PARALLAX_DISTANCE)
		{
			parallax_contribution = 0;
			offAxis_contribution *= Constants.OFF_AXIS_GAIN_BOOST;
		}
		
		double omega = parallax_contribution + offAxis_contribution;
		
		return omega;
	}
	
	
	
	public static void setParallax(double p) {
		parallax = p;
	}
	
	public static void setOffAxis(double o) {
		offAxis = o;
	}
	
	public static void reset() {
		parallax = 0;
		offAxis = 0;
		
		controlLoop_parallax.reset();
		controlLoop_offAxis.reset();
	}
}
