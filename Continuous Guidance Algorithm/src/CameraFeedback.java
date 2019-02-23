
public class CameraFeedback {
	private final boolean IN_FOV; // is it in the FOV?
	private final boolean IN_AUTO_ASSIST_REGION;
	private final double PARALLAX;
	private final double OFF_AXIS;
	
	
	
	public CameraFeedback(boolean inFOV, boolean inAutoAssistRegion, double parallax, double offAxis) {
		IN_FOV = inFOV;
		IN_AUTO_ASSIST_REGION = inAutoAssistRegion;
		PARALLAX = parallax;
		OFF_AXIS = offAxis;
	}
	
	
	
	public boolean isInFOV() {
		return IN_FOV;
	}
	
	public boolean isInAutoAssistRegion() {
		return IN_AUTO_ASSIST_REGION;
	}
	
	public double getParallax() {
		return PARALLAX;
	}
	
	public double getOffAxis() {
		return OFF_AXIS;
	}
}
