package ab.objtracking.representation.util;

import ab.objtracking.MagicParams;
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
			return ( Math.abs(_o1.rectType.id - _o2.rectType.id) > 2 || ((_o1.widthType != _o2.widthType) && (_o1.rectType.id <= 6 || _o2.rectType.id <= 6)));
		}
		else
			return !o1.isSameShape(o2);
	}
	/**
	 * @param matchedObjs 
	 * @return false if debris cannot be the debris of o;
	 * */
	public static boolean cannotBeDebris(ABObject debris, ABObject o)
	{
		
		//ABObject newObj = matchedObjs.get(o);
		//int area = o.getOriginalShape().area;
		double w = o.getOriginalShape().getPreciseWidth();
		double h = o.getOriginalShape().getPreciseHeight();
/*		if (o.id == 2)
		{
			System.out.println(" ^^^^" + debris + " \n^^^^" + o.getOriginalShape()
					+ " \n^^^^^" + (debris.rectType.id)  + "   " + o.getOriginalShape().rectType.id
					+ "  ");
			
		} 
		*/
		//System.out.println("#### " + debris);
		if(debris.type != o.type)
			return true;
		if( (debris.getPreciseWidth()) - w > MagicParams.VisionGap && debris.getPreciseHeight() - h > MagicParams.VisionGap)
			return true;
		if(debris.rectType.id - o.getOriginalShape().rectType.id > 2)
			return true;
	/*	if(debris instanceof Rect && o.getOriginalShape() instanceof Rect)
		{
			
			if (((Rect)debris).widthType != ((Rect)o.getOriginalShape()).widthType)
			return true;
		}*/
		
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
