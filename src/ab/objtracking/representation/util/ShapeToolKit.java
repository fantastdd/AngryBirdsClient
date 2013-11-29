package ab.objtracking.representation.util;

import java.util.Map;

import ab.vision.ABObject;
import ab.vision.real.shape.Rect;

public class ShapeToolkit {
	
	public static boolean isDifferentShape(ABObject o1, ABObject o2)
	{
		if (o1 instanceof Rect && o2 instanceof Rect)
		{
			Rect _o1 = (Rect)o1;
			Rect _o2 = (Rect)o2;
			//System.out.println("@@@" + o1 + "  " + o2);
			return ( Math.abs(_o1.rectType.id - _o2.rectType.id) > 2 || (_o1.widthType != _o2.widthType));
		}
		else
			return !o1.isSameShape(o2);
	}
	/**
	 * @param matchedObjs 
	 * @return false if debris cannot be the debris of o;
	 * */
	public static boolean cannotBeDebris(ABObject debris, ABObject o, Map<ABObject, ABObject> matchedObjs)
	{
		//ABObject newObj = matchedObjs.get(o);
		int area = o.getOriginalShape().area;
		//System.out.println("#### " + debris);
		if(debris.type != o.type)
			return true;
		if(debris.area > area)
			return true;
		
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
