package ab.objtracking.representation.util;

import java.awt.Point;

import ab.objtracking.MagicParams;
import ab.vision.ABTrackingObject;
import ab.vision.real.shape.TrackingRect;

public class ShapeToolkit {
	
	
	
	public static boolean isDifferentShape(ABTrackingObject o1, ABTrackingObject o2)
	{
		if (o1 instanceof TrackingRect && o2 instanceof TrackingRect)
		{
			TrackingRect _o1 = (TrackingRect)o1;
			TrackingRect _o2 = (TrackingRect)o2;
			//System.out.println("@@@" + o1 + "  " + o2);
			return isRectDifferent(_o1, _o2);
		}
		else
			return !o1.isSameShape(o2);
	}
	public static float calMassShift(ABTrackingObject o1, ABTrackingObject o2) {
		
		Point center1 = o1.getCenter();
		Point center2 = o2.getCenter();
	
		double diff = (center1.getX() - center2.getX())
				* (center1.getX() - center2.getX())
				+ (center1.getY() - center2.getY())
				* (center1.getY() - center2.getY());
		/*if(o2.id == 14 && o1.id == 18)
			System.out.println(center1.getX() + "    " + center1.getY() + "  " + 
								center2.getX() + "  " + center2.getY() + "  " + diff + "  " + (float)diff);*/
		return (float) diff;
	
	}
	private static boolean isRectDifferent(TrackingRect _o1, TrackingRect _o2)
	{
		//boolean result = ( Math.abs(_o1.rectType.id - _o2.rectType.id) > 2 || ((_o1.widthType != _o2.widthType) && (_o1.rectType.id <= 6 || _o2.rectType.id <= 6)));
		
		return !_o1.isSameShape(_o2);
	}
	/**
	 * @param matchedObjs 
	 * @return false if debris cannot be the debris of o;
	 * */
	public static boolean cannotBeDebris(ABTrackingObject debris, ABTrackingObject o)
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
		if( (debris.getPreciseWidth()) - w > MagicParams.VisionGap 
			//	|| debris.getPreciseHeight() - h > MagicParams.VisionGap
			)
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
