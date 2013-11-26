package ab.objtracking.representation.util;

import ab.vision.ABObject;
import ab.vision.real.shape.Rect;

public class ShapeToolKit {
	
	public static boolean isDifferentShape(ABObject o1, ABObject o2)
	{
		if (o1 instanceof Rect && o2 instanceof Rect)
		{
			Rect _o1 = (Rect)o1;
			Rect _o2 = (Rect)o2;
			return ( Math.abs(_o1.rectType.id - _o2.rectType.id) > 2 || (_o1.widthType != _o2.widthType));
		}
		else
			return !o1.isSameShape(o2);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
