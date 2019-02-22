
public class CameraFeedback {
	private final boolean IN_FOV; // is it in the FOV?
	private final double PARALLAX;
	private final double OFF_AXIS;
	
	
	
	public CameraFeedback(boolean inFOV, double parallax, double offAxis) {
		IN_FOV = inFOV;
		PARALLAX = parallax;
		OFF_AXIS = offAxis;
	}
	
	
	
	public boolean isInFOV() {
		return IN_FOV;
	}
	
	public double getParallax() {
		return PARALLAX;
	}
	
	public double getOffAxis() {
		return OFF_AXIS;
	}
}
