package ab.vision;

import ab.vision.ABObject;

public class ABPoint extends  java.awt.Point {
	public ABPoint() { super(); }
	public ABPoint(ABObject object) { super((int) object.getCenterX(), (int) object.getCenterY());	}
	public ABPoint(int x, int y) {super(x,y);}
	public ABPoint(double x, double y)
	{
		super((int)(x), (int)(y));
	}
}
