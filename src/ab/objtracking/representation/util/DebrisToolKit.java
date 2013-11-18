package ab.objtracking.representation.util;

import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import ab.objtracking.MagicParams;
import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Relation;
import ab.vision.ABObject;
import ab.vision.ABShape;
import ab.vision.ABType;
import ab.vision.real.shape.DebrisGroup;
import ab.vision.real.shape.Rect;
import ab.vision.real.shape.RectType;

public class DebrisToolKit {


	public static List<DebrisGroup> getAllDummyRectangles(DirectedGraph<ABObject, ConstraintEdge> network)
	{
		List<DebrisGroup> debrisList = new LinkedList<DebrisGroup>();
		Set<ABObject> vertices = network.vertexSet();
		for (ABObject obj : vertices)
		{
			if(obj.rectType == RectType.rec8x1 || obj.type == ABType.Pig)
				continue;
			else
			{
				Set<ConstraintEdge> edges = network.edgesOf(obj);
				for (ConstraintEdge edge : edges)
				{
					ABObject target = edge.getTarget();
					
					if(obj.id < target.id && obj.type == target.type && canBeSameDebrisGroup(obj, target, edge.label) )
					{
						DebrisGroup debris = debrisReconstruct(obj, target, edge.label);
						if(debris != null)
						{
							debrisList.add(debris);
						}
					}
				}
			}
		}
		return debrisList;
	}
	/**
	 * Recover the shape of the original from one piece
	 * TODO
	 * */
	public static ABObject debrisReconstruct(ABObject o1, ABObject original)
	{
		if(original.shape == ABShape.Rect)
		{
			if(o1.shape == ABShape.Rect)
			{
				
			}
		}
		return null;
	}
	
	/**
	 * Recover the shape of the original from two pieces
	 * */
	public static DebrisGroup debrisReconstruct(ABObject o1, ABObject o2, Relation o1Too2)
	{
		DebrisGroup debris;
		if (!o1.isLevel)
		{
			Relation leftpart = Relation.getLeftpart(o1Too2);
			   
				double centerX = (leftpart == Relation.S8 || leftpart == Relation.S6)?( o1.sectors[2].getX1() + o2.sectors[6].getX1())/2: ( o1.sectors[6].getX1() + o2.sectors[2].getX1())/2;
				double centerY = (leftpart == Relation.S8 || leftpart == Relation.S6)? (o1.sectors[4].getY1() + o2.sectors[0].getY1())/2 :  (o1.sectors[0].getY1() + o2.sectors[4].getY1())/2;
				double width = o1.getPreciseWidth();
				double height = o1.getPreciseHeight() + o2.getPreciseHeight();
				double angle = o1.angle;
				int area = (int)(width * height);
			/*	System.out.println(o1);
				System.out.println(String.format("centerX: %.2f centerY: %.2f width: %.2f height: %.2f", centerX, centerY, width, height));*/
				debris = new DebrisGroup(centerX, centerY, width, height, angle, -1,area);
				debris.type = o1.type;
				debris.addMember(o1);
				debris.addMember(o2);
				return debris;
			
		}
		else
		{
			Relation rightpart = Relation.getRightpart(o1Too2);
			
				double centerX = (rightpart == Relation.S8 || rightpart == Relation.S6)?( o2.sectors[2].getX1() + o1.sectors[6].getX1())/2: ( o2.sectors[6].getX1() + o1.sectors[2].getX1())/2;
				double centerY = (rightpart == Relation.S8 || rightpart == Relation.S6)? (o2.sectors[4].getY1() + o1.sectors[0].getY1())/2 :  (o2.sectors[0].getY1() + o1.sectors[4].getY1())/2;
				double width = o2.getPreciseWidth();
				double height = o1.getPreciseHeight() + o2.getPreciseHeight();
				double angle = o2.angle;
				int area = (int)(width * height);
				debris = new DebrisGroup(centerX, centerY, width, height, angle, -1,area);
				debris.type = o2.type;
				debris.addMember(o1);
				debris.addMember(o2);
				return debris;
			
		}
		
	}
	
	
	/*
	 * Determine whether two objects are likely to be of the same debris group. Only consider rotated rectangles otherwise most of stacked leveled objects will be considered as debris
	 * **/
	public static boolean canBeSameDebrisGroup(ABObject o1, ABObject o2, Relation o1Too2)
	{
		//(o1.)
		if(o1.shape != ABShape.Rect && o2.shape != ABShape.Rect)
			return false;
		if(o1.isLevel && o2.isLevel && ((o1.getPreciseWidth() > MagicParams.SlimRecWidth ) || (o2.getPreciseWidth()> MagicParams.SlimRecWidth)))
				return false;
		/*if(o1.id == 10 && o2.id == 11)
			System.out.println( o1 + "  " + o2 + "  " +  sameAngle(Math.abs(o1.angle - o2.angle) ));*/;
		double orientationDiff = Math.abs(o1.angle - o2.angle);
		double angle = getAngle(o1.getCenterX() - o2.getCenterX(), o1.getCenterY() - o2.getCenterY());
		double diff = Math.abs(angle - o1.angle);
		
		if(sameAngle(diff)) /*&& (o1.getPreciseWidth() < MagicParams.SlimRecWidth ) && (o2.getPreciseWidth()< MagicParams.SlimRecWidth)*///since circle are also "level", but circle some times are just tiny mis-detected rectangles which are highly likely to be debris
		{
		   if(sameAngle(orientationDiff)|| Math.abs(o2.getPreciseHeight() - o1.getPreciseWidth()) < MagicParams.VisionGap ){
			if(o1.angle < Math.PI/2)
			{
				
				if(Relation.getLeftpart(o1Too2) == Relation.S2 || Relation.getLeftpart(o1Too2) == Relation.S6)
					return true;
			}
			else
				if(o1.angle > Math.PI/2)
				{
					if(Relation.getLeftpart(o1Too2) == Relation.S4 || Relation.getLeftpart(o1Too2) == Relation.S8)
						return true;
				}
		   }		
		}
		return false;
	}
	
	public static boolean isSameDebris(ABObject debris, Rect initialObj, ABObject newObj)
	{
		if(debris.type == newObj.type)
		{
			Rect dummy = debris.extend(initialObj.rectType);
			//Relation r = GSRConstructor.computeRectToRectRelation(debris, initialObj);
			Polygon p = dummy.p;
			if(p.contains(newObj.getCenter()))// && newObj instanceof Rect)//damage detection only supports rect currently
			{
				//Inverse Check
				dummy = newObj.extend(initialObj.rectType);
				if(dummy.p.contains(debris.getCenter()))
				{
					//Spatial Consistency Check
					double angle, diff, orientationDiff;
					double contactWidth, contactHeight;
					if(debris.shape != ABShape.Circle)
					{
						angle = getAngle(debris.getCenterX() - newObj.getCenterX(), debris.getCenterY() - newObj.getCenterY());
						diff = Math.abs(angle - debris.angle);
					    contactWidth = debris.getPreciseWidth();
					    contactHeight = newObj.getPreciseHeight();
					}
					else
					{
						angle = getAngle(newObj.getCenterX() - debris.getCenterX(), newObj.getCenterY() - debris.getCenterY());
						diff = Math.abs(angle - newObj.angle);
						contactWidth = newObj.getPreciseWidth();
						contactHeight = debris.getPreciseHeight();
						
					}
					if(debris.shape == ABShape.Circle || newObj.shape == ABShape.Circle)
						orientationDiff = 0;
					else
						orientationDiff = Math.abs(debris.angle - newObj.angle);
					/*if(debris.id == 2)
					{
						System.out.println(" debris " + debris + " newObj " + newObj + " angle " + angle + " diff " + diff );
					}*/
					//
					 if(sameAngle(diff) && (sameAngle(orientationDiff)|| Math.abs(contactWidth - contactHeight) < MagicParams.VisionGap ))
					{
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}
	private static boolean sameAngle(double diff)
	{
		return Math.abs(diff - Math.PI) < MagicParams.AngleTolerance * 2 || diff < MagicParams.AngleTolerance * 2;
	}
	private static double getAngle(double x, double y)
	{
		if (y < 0)
		{
			x = -x;
			y = -y;
		}
		return Math.atan2(y, x);
			
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Math.atan2(12, -1));
	}

}
