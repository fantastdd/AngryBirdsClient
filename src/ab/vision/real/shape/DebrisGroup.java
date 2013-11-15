package ab.vision.real.shape;

import ab.vision.ABObject;

public class DebrisGroup extends Rect{

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
    public ABObject member1;
    public ABObject member2;

	public void addMember(ABObject member)
	{
		if (member1 != null)
			member2 = member;
		else
			member1 = member;
	}
    


}
