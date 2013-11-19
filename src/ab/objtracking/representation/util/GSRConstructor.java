package ab.objtracking.representation.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import ab.demo.other.ActionRobot;
import ab.objtracking.MagicParams;
import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Relation;
import ab.utils.ImageSegFrame;
import ab.vision.ABList;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.VisionUtils;
import ab.vision.real.MyVision;
import ab.vision.real.shape.Rect;

public class GSRConstructor {
	
	
	//Construct Constraint network (directed-graph)
	public static DirectedGraph<ABObject, ConstraintEdge> constructFullNetwork(List<ABObject> objs)
	{
		DirectedGraph<ABObject, ConstraintEdge> graph = new SimpleDirectedGraph<ABObject, ConstraintEdge>(new ClassBasedEdgeFactory<ABObject, ConstraintEdge>(ConstraintEdge.class));
		//Create Node
		for (ABObject obj : objs)
		{
			graph.addVertex(obj);
		}
		//Sort by ID
				Collections.sort(objs, new Comparator<ABObject>(){

					@Override
					public int compare(ABObject o1, ABObject o2) {
						
						return ((Integer)o1.id).compareTo(o2.id);
					}});
				
		for ( int i = 0; i < objs.size() - 1; i++ )
		{
			ABObject sourceVertex = objs.get(i);
			for (int j = i + 1; j < objs.size(); j++ )
			{
				ABObject targetVertex = objs.get(j);
				Relation r = computeRelation(sourceVertex, targetVertex);
				graph.addEdge(sourceVertex, targetVertex, new ConstraintEdge(sourceVertex, targetVertex, r));	
			}
		}
		return graph;
	}
	
	public static DirectedGraph<ABObject, ConstraintEdge> addVertexToGRNetwork(ABObject obj, DirectedGraph<ABObject, ConstraintEdge> graph)
	{
		
		//Create Node
		graph.addVertex(obj);
			
		for (ABObject vertex : graph.vertexSet())
		{
			if (vertex.id > obj.id)
			{
				Relation r = computeRelation(obj, vertex);
				if(r.toString().contains("S"))
					graph.addEdge(obj, vertex, new ConstraintEdge(obj, vertex, r));
				else
					graph.addEdge(obj, vertex, new ConstraintEdge(obj, vertex, Relation.Unassigned));
			}
			else
				if(obj.id > vertex.id)
				{
					Relation r = computeRelation(vertex, obj);
					if(r.toString().contains("S"))
						graph.addEdge(vertex, obj, new ConstraintEdge(vertex, obj, r));
					else
						graph.addEdge(vertex, obj, new ConstraintEdge(vertex, obj, Relation.Unassigned));
			
				}
			
		}
		return graph;
		
		
	}

	public static DirectedGraph<ABObject, ConstraintEdge> constructGRNetwork(List<ABObject> objs)
	{
		
		DirectedGraph<ABObject, ConstraintEdge> graph = new SimpleDirectedGraph<ABObject, ConstraintEdge>(new ClassBasedEdgeFactory<ABObject, ConstraintEdge>(ConstraintEdge.class));
		//Create Node
		for (ABObject obj : objs)
		{
			graph.addVertex(obj);
		}
		
		//Sort by ID
		Collections.sort(objs, new Comparator<ABObject>(){

			@Override
			public int compare(ABObject o1, ABObject o2) {
				
				return ((Integer)o1.id).compareTo(o2.id);
			}});
		
		for ( int i = 0; i < objs.size() - 1; i++ )
		{
			ABObject sourceVertex = objs.get(i);
			for (int j = i + 1; j < objs.size(); j++ )
			{
				ABObject targetVertex = objs.get(j);
				Relation r = computeRelation(sourceVertex, targetVertex);
				/*if(sourceVertex.id == 4)
					log(sourceVertex + "   "  + targetVertex + "  " + r);*/
			//if(!sourceVertex.isLevel && !targetVertex.isLevel)
				String str = r.toString();
			    if(str.length() > 3)
				{
			    	str = str.substring(0,3);
					if(str.contains("_"))
					{
						//Relation ri = Relation.inverseRelation(r);
						//System.out.println(sourceVertex + "  " + targetVertex);
						graph.addEdge(sourceVertex, targetVertex, new ConstraintEdge(sourceVertex, targetVertex, r));
						//graph.addEdge(targetVertex, sourceVertex, new ConstraintEdge(targetVertex, sourceVertex, ri));
					} else
						{
							graph.addEdge(sourceVertex, targetVertex, new ConstraintEdge(sourceVertex, targetVertex, Relation.Unassigned));
							//graph.addEdge(targetVertex, sourceVertex, new ConstraintEdge(targetVertex, sourceVertex, Relation.Unassigned));
				}
							}
				}
		}
		return graph;
		
		
	}
	private static Relation computeRelation(ABObject source, ABObject target)
	{
	
		   return computeRectToRectRelation(source, target);
	}
	
	public static Relation computeRectToRectRelation(ABObject source, ABObject target)
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
			return Relation.BOTTOM;
		} 
		else
			if(target.type == ABType.Hill)
				return Relation.TOP;
		Line2D[] sourceSectors = source.sectors;
		Line2D[] targetSectors = target.sectors;
		double distance;
		int sIndex = -1;
		int tIndex = -1;
		double minDistance = Integer.MAX_VALUE;
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
		_sourceAngle =  (source.angle >= Math.PI/2)? source.angle - Math.PI/2: source.angle;
		_targetAngle =  (target.angle >= Math.PI/2)? target.angle - Math.PI/2: target.angle;
		
		angleDiff = Math.abs(_sourceAngle - _targetAngle);
		if (angleDiff < MagicParams.AngleTolerance || angleDiff > Math.PI/2 - MagicParams.AngleTolerance)
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
				if(!source.isLevel && !target.isLevel)
					switch(index)
					{
						case 1: return Relation.getRelation(1, false, 5, false);
						case 3: return Relation.getRelation(3, false, 7, false);
						case 5: return Relation.getRelation(5, false, 1, false);
						case 7: return Relation.getRelation(7, false, 3, false);
						default: return Relation.Invalid_1;
					}
				else
					if(source.isLevel && target.isLevel)
					{
						switch(index)
						{
							case 1: return Relation.getRelation(1, true, 5, true);
							case 3: return Relation.getRelation(3, true, 7, true);
							case 5: return Relation.getRelation(5, true, 1, true);
							case 7: return Relation.getRelation(7, true, 3, true);
							default: return Relation.Invalid_1;
						}
					}
					else
						return Relation.Invalid_2;
			} 
			else
			{
				index -= 4;	
				index = index * 2 + 1;
				if(!source.isLevel && !target.isLevel)
					switch(index)
					{
						case 1: return Relation.getRelation(5, false, 1, false);
						case 3: return Relation.getRelation(7, false, 3, false);
						case 5: return Relation.getRelation(1, false, 5, false);
						case 7: return Relation.getRelation(3, false, 7, false);
						default: return Relation.Invalid_1;
					}
				else
					if(source.isLevel && target.isLevel)
					{
						switch(index)
						{
							case 1: return Relation.getRelation(5, true, 1, true);
							case 3: return Relation.getRelation(7, true, 3, true);
							case 5: return Relation.getRelation(1, true, 5, true);
							case 7: return Relation.getRelation(3, true, 7, true);
							default: return Relation.Invalid_1;
						}
					}
					else
						return Relation.Invalid_2;
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
			  
			   Relation r =  Relation.getRelation(sIndex, source.isLevel, tIndex, target.isLevel);
			   //System.out.println(sIndex + "  " + tIndex);
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
				return Relation.TOP;
			else
				return Relation.BOTTOM;
		}
		else
			if(vertical_intersect)
			{
				if(left)
					return Relation.LEFT;
				else
					return Relation.RIGHT;
			}
			else 
			{
				if(above && left)
					return Relation.TOP_LEFT;
				else
					if(above)
						return Relation.TOP_RIGHT;
					else
						if(left)
							return Relation.BOTTOM_LEFT;
						else
							return Relation.BOTTOM_RIGHT;
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
		if( (s1 > e2 + MagicParams.VisionGap || s2 > e1 + MagicParams.VisionGap))
			return false;
		return true;
	}
	public static void printNetwork(Graph<ABObject, ConstraintEdge> network){
		
		//System.out.println(network);
		for (ABObject vertex: network.vertexSet())
		{
			System.out.println(" vertex: " + vertex);
			for (ConstraintEdge edge: network.edgesOf(vertex))
			{
				System.out.println(edge);
			}
			System.out.println("------------------------------");
		}
	}
	public static void performanceTesting()
	{
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
		DirectedGraph<ABObject, ConstraintEdge> network = GSRConstructor.constructGRNetwork(allInterestObjs);
		System.out.println(" Time: " + (System.nanoTime() - time) + " nanos ");
		for (ABObject vertex: network.vertexSet())
		{
			System.out.println(" vertex: " + vertex);
			for (ConstraintEdge edge: network.edgesOf(vertex))
			{
				System.out.println(edge);
			}
			System.out.println("------------------------------");
		}
		
	}
	public static void main(String[] args) {
		//Rect: id:2 type:rec8x1 area:208 w:  4.697 h: 52.162 a:  2.545 at x:543.5 y:344.0 isDebris:false [ S2_S6 ] 
		//Rect: id:3 type:rec2x1 area:72 w:  6.119 h: 12.205 a:  2.545 at x:533.0 y:343.5 isDebris:false
		Rect rec1 = new Rect(547.5, 329.5, 6.216, 25.171, 1.477, -1, 150);
		Rect rec2 = new Rect(555.0,348.5, 6, 25, 1.571, -1, 150);
		System.out.println(rec1.isLevel);
		for (Line2D line : rec2.sectors)
		{
			System.out.println(line.getP1() + "  " + line.getP2());
		}
		System.out.println(GSRConstructor.computeRectToRectRelation(rec1, rec2));
	}

}
