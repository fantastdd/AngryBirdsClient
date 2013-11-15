package ab.objtracking.representation.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import ab.demo.other.ActionRobot;
import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Relation;
import ab.utils.ImageSegFrame;
import ab.vision.ABList;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.VisionUtils;
import ab.vision.real.MyVision;

public class GSRConstructor {
	
	final static float AngleTolerance = 0.25f; //15 degree
	//Construct Constraint network (directed-graph)
	public static DirectedMultigraph<ABObject, ConstraintEdge> constructNetwork(List<ABObject> objs)
	{
		DirectedMultigraph<ABObject, ConstraintEdge> graph = new DirectedMultigraph<ABObject, ConstraintEdge>(new ClassBasedEdgeFactory<ABObject, ConstraintEdge>(ConstraintEdge.class));
		//Create Node
		for (ABObject obj : objs)
		{
			graph.addVertex(obj);
		}
		
		for ( int i = 0; i < objs.size() - 1; i++ )
		{
			ABObject sourceVertex = objs.get(i);
			for (int j = i + 1; j < objs.size(); j++ )
			{
				ABObject targetVertex = objs.get(j);
				Relation r = computeRelation(sourceVertex, targetVertex);
				Relation ri = Relation.inverseRelation(r);
				graph.addEdge(sourceVertex, targetVertex, new ConstraintEdge(sourceVertex, targetVertex, r));
				graph.addEdge(targetVertex, sourceVertex, new ConstraintEdge(targetVertex, sourceVertex, ri));
			}
		}
		return graph;
	}
	private static Relation computeRelation(ABObject source, ABObject target)
	{
	
		   return computeRectToRectRelation(source, target);
	}
	
	private static Relation computeRectToRectRelation(ABObject source, ABObject target)
	{
		Rectangle mbr_1 = source.getBounds();
		Rectangle mbr_2 = target.getBounds();
		boolean vertical_intersect =  isIntervalIntersect(mbr_1.y, mbr_1.y + mbr_1.height, mbr_2.y, mbr_2.y + mbr_2.height);
		boolean horizontal_intersect = isIntervalIntersect(mbr_1.x, mbr_1.x + mbr_1.width, mbr_2.x, mbr_2.x + mbr_2.width);
		if (vertical_intersect && horizontal_intersect)
			return computeRectToRectContactRelation(source, target);
		else 
			return computeNonContactRelation(source, target, vertical_intersect, horizontal_intersect);
		
	}
	
	private static Relation computeRectToRectContactRelation(ABObject source, ABObject target)
	{
		if (source.type == ABType.Hill)
		{
			return Relation.Under;
		} 
		else
			if(target.type == ABType.Hill)
				return Relation.Above;
		Line2D[] sourceSectors = source.sectors;
		Line2D[] targetSectors = target.sectors;
		double distance;
		double minDistance = Integer.MAX_VALUE;
		int sIndex = -1;
		int tIndex = -1;
		/*if(sourceSectors == null)
			System.out.println(source);*/
		int[] EdgeSumDist = new int[8];
	
		for (int i = 0; i < sourceSectors.length; i ++)
		{
			Line2D ss = sourceSectors[i];
			for (int j = (i%2 + 1)%2; j < targetSectors.length; j += 2)
			{
				Line2D ts = targetSectors[j];
				if(i%2 == 0)
				{	
					distance = ts.ptSegDist(ss.getP1());
					EdgeSumDist[4 + (j - 1)/2] += distance; 
				}
				else
					{
						distance = ss.ptSegDist(ts.getP1());
						EdgeSumDist[(i - 1)/2] += distance;
					}
				
				if (distance <= minDistance)
				{
					minDistance = distance;
					sIndex = i;
					tIndex = j;
				}
			}
		}
		// check edge-edge relation, relaxation here
		double angleDiff;
		double _sourceAngle, _targetAngle;
		_sourceAngle =  (source.angle > Math.PI/2)? Math.PI - source.angle: source.angle;
		_targetAngle =  (target.angle > Math.PI/2)? Math.PI - target.angle: target.angle;
		
		angleDiff = Math.abs(_sourceAngle - _targetAngle);
		if (angleDiff < AngleTolerance)
		{
			//edge touch
			double sMin = Integer.MAX_VALUE;
			int index = -1;
			int counter = -1;
			for (int sum : EdgeSumDist)
			{
				counter ++;
				if(sum < sMin)
				{
					sMin = sum;
					index = counter; 
				}
			}
			if (index < 4)
			{
				index = index * 2 + 1;
				switch(index)
				{
					case 1: return Relation.getRelation(1, 5);
					case 3: return Relation.getRelation(3, 7);
					case 5: return Relation.getRelation(5, 1);
					case 7: return Relation.getRelation(7, 3);
					default: return Relation.Invalid_1;
				}
			} 
			else
			{
				index -= 4;	
				index = index * 2 + 1;
				switch(index)
				{
					case 1: return Relation.getRelation(5, 1);
					case 3: return Relation.getRelation(7, 3);
					case 5: return Relation.getRelation(1, 5);
					case 7: return Relation.getRelation(3, 7);
					default: return Relation.Invalid_1;
				}
			}
			
	/*		if(sIndex%2 == 1)
			{
				
				switch(sIndex)
				{
					case 1: return Relation.getRelation(1, 5);
					case 3: return Relation.getRelation(3, 7);
					case 5: return Relation.getRelation(5, 1);
					case 7: return Relation.getRelation(7, 3);
					default: return Relation.Invalid_1;
				}
			} else
				if(tIndex%2 == 1)
				{
					switch(tIndex)
					{
						case 1: return Relation.getRelation(5, 1);
						case 3: return Relation.getRelation(7, 3);
						case 5: return Relation.getRelation(1, 5);
						case 7: return Relation.getRelation(3, 7);
						default: return Relation.Invalid_1;
					}
				}
				else
					{
						//System.out.println(sIndex + "  " + tIndex + "  " + source + "  " + target);
						log(" Error in  computeRectToRectContactRelation");
					}*/
		}
		else 
			{
			   Relation r =  Relation.getRelation(sIndex, tIndex);
			   /*if(r == Relation.Invalid && source.id == 3 && target.id == 4){
				 	System.out.println(source + "  " + target + "  " + (sIndex + 1) + "  " + (tIndex + 1) + "  " + minDistance);
				   	for (Line2D line : source.sectors)
				   		System.out.println(line.getP1() + "  " + line.getP2());
				   	System.out.println("--------------------");
				   	for (Line2D line : target.sectors)
				   		System.out.println(line.getP1() + "  " + line.getP2());
				   	System.out.println("--------------------");
				  
				   }*/
			   return r;
			}
		
	
	}
	private static void log(String message)
	{
		System.out.println(message);
	}
	//TODO Perform some relaxiaion here
	private static Relation computeNonContactRelation(ABObject source, ABObject target, boolean vertical_intersect, boolean horizontal_intersect)
	{
		Point source_center = source.getCenter();
		Point target_center = target.getCenter();
		boolean above = source_center.getY() < target_center.getY();
		boolean left = 	source_center.getX() < target_center.getX();
		if (horizontal_intersect)
		{
			if(above)
				return Relation.Above;
			else
				return Relation.Under;
		}
		else
			if(vertical_intersect)
			{
				if(left)
					return Relation.Left;
				else
					return Relation.Right;
			}
			else 
			{
				if(above&&left)
					return Relation.Above_Left;
				else
					if(above)
						return Relation.Above_Right;
					else
						if(left)
							return Relation.Under_Left;
						else
							return Relation.Under_Right;
			}
		
				
		
	}
	
/*	private static boolean isMBRIntersect(Rectangle mbr_1, Rectangle mbr_2)
	{
		if( isIntervalIntersect(mbr_1.x, mbr_1.x + mbr_1.width, mbr_2.x, mbr_2.x + mbr_2.width)
				&& isIntervalIntersect(mbr_1.y, mbr_1.y + mbr_1.height, mbr_2.y, mbr_2.y + mbr_2.height))
			return true;
		return false;
	}*/
	private static boolean isIntervalIntersect(int s1, int e1, int s2, int e2)
	{
		if( (s1 > e2 || s2 > e1))
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		
		new ActionRobot();
		BufferedImage screenshot = ActionRobot.doScreenShot();
		screenshot = VisionUtils.resizeImage(screenshot, 800, 1200);
		
		MyVision vision = new MyVision(screenshot);
		
		
		ABList allInterestObjs = ABList.newList();
		allInterestObjs.addAll(vision.findObjects());
		vision.drawObjectsWithID(screenshot, true);
		ImageSegFrame frame = new ImageSegFrame(" Test GSR Constructor ", screenshot, null);
		screenshot = VisionUtils.resizeImage(screenshot, 800, 1200);
		frame.refresh(screenshot);
		long time = System.nanoTime();
		DirectedMultigraph<ABObject, ConstraintEdge> network = GSRConstructor.constructNetwork(allInterestObjs);
		for (ABObject vertex: network.vertexSet())
		{
			System.out.println(" vertex: " + vertex);
			for (ConstraintEdge edge: network.edgesOf(vertex))
			{
				System.out.println(edge);
			}
			System.out.println("------------------------------");
		}
		
		System.out.println(" Time: " + (System.nanoTime() - time));

	}

}
