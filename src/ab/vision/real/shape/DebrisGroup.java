package ab.vision.real.shape;

import ab.vision.ABTrackingObject;

public class DebrisGroup extends TrackingRect{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9205734795452228009L;

	public DebrisGroup(double centerX, double centerY, double width,
			double height, double angle, int vision_type, int area) {
		super(centerX, centerY, width, height, angle, vision_type, area);
		member1 = null;
		member2 = null;
	}
    public ABTrackingObject member1;
    public ABTrackingObject member2;
    public final boolean isDebris = false;
	public void addMember(ABTrackingObject member)
	{
		member.isDebris = true;
		if (member1 != null)
			member2 = member;
		else
			member1 = member;
	}
	public String toString()
	{
		String result = String.format(" Debris: %s \n member1:%s \n member2:%s ", super.toString(), member1, member2);
		return  result;
	}


}
