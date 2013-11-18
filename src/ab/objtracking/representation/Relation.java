package ab.objtracking.representation;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public enum Relation {
	
	Invalid,Invalid_1, Invalid_2,
	Unknown,
	Unassigned,
	
	
	//Atomic Sectors
	S1, S2, S3, S4, S5, S6, S7, S8,
	
	//GR relations
	S1_S3(S1,S2), S1_S4(S1,S4), S1_S5(S1,S5), S1_S6(S1,S6), S1_S7(S1,S7),
	
	S2_S5(S2,S5), S2_S6(S2,S6), S2_S7(S2,S7),

	S3_S1(S3,S1), S3_S5(S3,S5), S3_S6(S3,S6), S3_S7(S3,S7), S3_S8(S3,S8),
	
	S4_S7(S4,S7), S4_S8(S4,S8), S4_S1(S4,S1),
	
	S5_S1(S5,S1), S5_S2(S5,S2), S5_S3(S5,S3), S5_S7(S5,S7), S5_S8(S5,S8),
	
	S6_S1(S6,S1), S6_S2(S6, S2), S6_S3(S6, S3),
	
	S7_S1(S7,S1), S7_S2(S7,S2), S7_S3(S7,S3), S7_S4(S7,S4), S7_S5(S7,S5),
	
	S8_S3(S8,S3), S8_S4(S8,S4), S8_S5(S8,S5),
	
	
	//Boxes Relations
	TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT(TOP, LEFT), TOP_RIGHT(TOP, RIGHT), BOTTOM_LEFT(BOTTOM, LEFT), BOTTOM_RIGHT(BOTTOM, RIGHT);
	

	private  Relation left;
	private  Relation right;
	private Relation(){}
	private static UndirectedGraph<Relation,DefaultEdge> graph; //neighbor graph
	private static DijkstraShortestPath<Relation, DefaultEdge> shortestPath;
	public static boolean isNeighbor(Relation r1, Relation r2)
	{
		if(r1 == r2)
			return true;
		if(graph.getEdge(r1, r2) == null)
			return false;
		return true;
	}
	
	public static boolean isNeighbor(Relation r1, Relation r2, int dis)
	{
		if(r1 == r2)
			return true;
		shortestPath = new DijkstraShortestPath<Relation, DefaultEdge>(graph, r1, r2);
		
		//System.out.println(shortestPath.getPathLength());
		if(shortestPath.getPathLength() < dis)
			return true;
		return false;
	}
	
    static 
    {
    	System.out.println(" Generate Neighborhood graph ");
    	graph = new SimpleGraph<Relation, DefaultEdge>(DefaultEdge.class);
    	//Serialize later;
    	for (Relation relation : Relation.values())
    	{
    		 graph.addVertex(relation);
    	}
    	
    	for (Relation relation: Relation.values()){
    		
    		/*if(relation != Relation.Unassigned)
    			graph.addEdge(relation, Relation.Unassigned);*/
    		switch(relation)
    		{
	    		case S1_S3: graph.addEdge(S1_S3, S1_S4); graph.addEdge(S1_S3, S8_S3); break;
	    		case S1_S4: graph.addEdge(S1_S4, S1_S3); graph.addEdge(S1_S4, S1_S5); graph.addEdge(S1_S4, S8_S4); break;
	    		case S1_S5: graph.addEdge(S1_S5, S1_S6); graph.addEdge(S1_S5, S1_S4); graph.addEdge(S1_S5, S2_S5); graph.addEdge(S1_S5, S8_S5); break;
	    		case S1_S6: graph.addEdge(S1_S6, S1_S7); graph.addEdge(S1_S6, S1_S5); graph.addEdge(S1_S6, S2_S6); break;
	    		case S1_S7: graph.addEdge(S1_S7, S1_S6); graph.addEdge(S1_S7, S2_S7); break;
	    		
	    		case S2_S5: graph.addEdge(S2_S5, S2_S6); graph.addEdge(S2_S5, S1_S5); graph.addEdge(S2_S5, S3_S5); break;
	    		case S2_S6: graph.addEdge(S2_S6, S2_S7); graph.addEdge(S2_S6, S2_S5); graph.addEdge(S2_S6, S1_S6); graph.addEdge(S2_S6, S3_S6); graph.addEdge(S2_S6, S3_S7); graph.addEdge(S2_S6, S1_S5);break;
	    		case S2_S7: graph.addEdge(S2_S7, S2_S6); graph.addEdge(S2_S7, S1_S7); graph.addEdge(S2_S7, S3_S7); break;
	    		
	    		case S3_S1: graph.addEdge(S3_S1, S3_S8); graph.addEdge(S3_S1, S4_S1); break; 
	    		case S3_S5: graph.addEdge(S3_S5, S3_S6); graph.addEdge(S3_S5, S2_S5); break;
	    		case S3_S6: graph.addEdge(S3_S6, S3_S7); graph.addEdge(S3_S6, S3_S5); graph.addEdge(S3_S6, S2_S6); break;
	    		case S3_S7: graph.addEdge(S3_S7, S3_S8); graph.addEdge(S3_S7, S3_S6); graph.addEdge(S3_S7, S2_S7); graph.addEdge(S3_S7,S4_S7); break;
	    		case S3_S8: graph.addEdge(S3_S8, S3_S7); graph.addEdge(S3_S8, S3_S1); graph.addEdge(S3_S8, S4_S8); break;
	    		
	    		case S4_S7: graph.addEdge(S4_S7, S4_S8); graph.addEdge(S4_S7, S3_S7); graph.addEdge(S4_S7, S5_S7); break;
	    		case S4_S8: graph.addEdge(S4_S8, S4_S1); graph.addEdge(S4_S8, S4_S7); graph.addEdge(S4_S8, S3_S8); graph.addEdge(S4_S8, S5_S8); graph.addEdge(S4_S8, S5_S1); graph.addEdge(S4_S8, S7_S3);break;
	    		case S4_S1: graph.addEdge(S4_S1, S4_S8); graph.addEdge(S4_S1, S3_S1); graph.addEdge(S4_S1, S5_S1); break;
	    		
	    		case S5_S1: graph.addEdge(S5_S1, S5_S2); graph.addEdge(S5_S1, S5_S8); graph.addEdge(S5_S1, S4_S1); graph.addEdge(S5_S1, S6_S1); break;
	    		case S5_S2: graph.addEdge(S5_S2, S5_S1); graph.addEdge(S5_S2, S5_S3); graph.addEdge(S5_S2, S6_S2); break;
	    		case S5_S3: graph.addEdge(S5_S3, S5_S2); graph.addEdge(S5_S3, S6_S3); break; 
	    		case S5_S7: graph.addEdge(S5_S7, S5_S8); graph.addEdge(S5_S7, S4_S7); break;
	    		case S5_S8: graph.addEdge(S5_S8, S5_S1); graph.addEdge(S5_S8, S5_S7); graph.addEdge(S5_S8, S4_S8); break;
	    		
	    		case S6_S1: graph.addEdge(S6_S1, S6_S2); graph.addEdge(S6_S1, S5_S1); graph.addEdge(S6_S1, S7_S1); break;
	    		case S6_S2: graph.addEdge(S6_S2, S6_S3); graph.addEdge(S6_S2, S6_S1); graph.addEdge(S6_S2, S5_S2); graph.addEdge(S6_S2, S7_S2); graph.addEdge(S6_S2, S7_S3); graph.addEdge(S6_S2, S5_S1);break;
	    		case S6_S3: graph.addEdge(S6_S3, S6_S2); graph.addEdge(S6_S3, S5_S3); graph.addEdge(S6_S3, S7_S3); break;
	    		
	    		case S7_S1: graph.addEdge(S7_S1, S7_S2); graph.addEdge(S7_S1, S6_S1); break;
	    		case S7_S2: graph.addEdge(S7_S2, S7_S3); graph.addEdge(S7_S2, S7_S1); graph.addEdge(S7_S2, S6_S2); break;
	    		case S7_S3: graph.addEdge(S7_S3, S7_S2); graph.addEdge(S7_S3, S7_S4); graph.addEdge(S7_S3, S6_S3); graph.addEdge(S7_S3, S8_S3); break;
	    		case S7_S4: graph.addEdge(S7_S4, S7_S3); graph.addEdge(S7_S4, S7_S5); graph.addEdge(S7_S4, S8_S4); break; 
	    		case S7_S5: graph.addEdge(S7_S5, S7_S4); graph.addEdge(S7_S5, S8_S5); break;
	    		
	    		case S8_S3: graph.addEdge(S8_S3, S8_S4); graph.addEdge(S8_S3, S7_S3); graph.addEdge(S8_S3, S1_S3); break;
	    		case S8_S4: graph.addEdge(S8_S4, S8_S3); graph.addEdge(S8_S4, S8_S5); graph.addEdge(S8_S4, S7_S4); graph.addEdge(S8_S4, S1_S4); graph.addEdge(S8_S4, S1_S5); graph.addEdge(S8_S4, S3_S7); break;
	    		case S8_S5: graph.addEdge(S8_S5, S8_S4); graph.addEdge(S8_S5, S7_S5); graph.addEdge(S8_S5, S1_S5);
	    		
	    		default: break;
		}
    	}
    }
	private Relation(Relation left, Relation right)
	{
		this.left = left;
		this.right = right;
	}
	
	public static Relation getLeftpart(Relation relation)
	{
		if(relation.left == null)
			return relation;
		return relation.left;
	}
	public static Relation getRightpart(Relation relation)
	{
		if(relation.right == null)
			return relation;
		return relation.right;
	}
	public static Relation inverseRelation(Relation relation)
	{
		switch(relation)
		{
			case S1_S3: return S3_S1;
			case S1_S4: return S4_S1;
			case S1_S5: return S5_S1;
			case S1_S6: return S6_S1;
			case S1_S7: return S7_S1;
			case S2_S5: return S5_S2;
			case S2_S6: return S6_S2;
			case S2_S7: return S7_S2;
			case S3_S1: return S1_S3;
			case S3_S5: return S5_S3;
			case S3_S6: return S6_S3;
			case S3_S7: return S7_S3;
			case S3_S8: return S8_S3;
			case S4_S7: return S7_S4;
			case S4_S8: return S8_S4;
			case S4_S1: return S1_S4;
			case S5_S1: return S1_S5;
			case S5_S2: return S2_S5;
			case S5_S3: return S3_S5;
			case S5_S7: return S7_S5;
			case S5_S8: return S8_S5;
			case S6_S1: return S1_S6;
			case S6_S2: return S2_S6;
			case S6_S3: return S3_S6;
			case S7_S1: return S1_S7;
			case S7_S2: return S2_S7;
			case S7_S3: return S3_S7;
			case S7_S4: return S4_S7;
			case S7_S5: return S5_S7;
			case S8_S3: return S3_S8;
			case S8_S4: return S4_S8;
			case S8_S5: return S5_S8;
			case TOP: return BOTTOM;
			case BOTTOM: return TOP;
			case LEFT: 	return RIGHT;
			case RIGHT: return LEFT;
			case TOP_LEFT: return BOTTOM_RIGHT;
			case TOP_RIGHT: return BOTTOM_LEFT;
			case BOTTOM_LEFT: return TOP_RIGHT;
			case BOTTOM_RIGHT: return TOP_LEFT;
			case Unknown: return Unknown;
			case Invalid: return Invalid;
			case Invalid_1: return Invalid_1;
			case Invalid_2: return Invalid_2;
			default: return Unassigned;
		}
		
	}
	public static Relation getRelation(int sector1, int sector2)
	{
		switch(sector1)
		{
			case 0: 
				{
					switch(sector2)
					{
						case 2: return Relation.S1_S3;
						case 3: return Relation.S1_S4;
						case 4: return Relation.S1_S5;
						case 5: return Relation.S1_S6;
						case 6: return Relation.S1_S7;
						default: return Relation.Invalid;
					}
				}
			case 2: 
			{
				switch(sector2)
				{
					case 0: return Relation.S3_S1;
					case 4: return Relation.S3_S5;
					case 5: return Relation.S3_S6;
					case 6: return Relation.S3_S7;
					case 7: return Relation.S3_S8;
					default: return Relation.Invalid;
				}
			}
			case 4: 
			{
				switch(sector2)
				{
					case 0: return Relation.S5_S1;
					case 1: return Relation.S5_S2;
					case 2: return Relation.S5_S3;
					case 6: return Relation.S5_S7;
					case 7: return Relation.S5_S8;
					default: return Relation.Invalid;
				}
			}
			case 6: 
			{
				switch(sector2)
				{
					case 0: return Relation.S7_S1;
					case 1: return Relation.S7_S2;
					case 2: return Relation.S7_S3;
					case 3: return Relation.S7_S4;
					case 4: return Relation.S7_S5;
					default: return Relation.Invalid;
				}
			}
			case 1: 
			{
				switch(sector2)
				{
					case 4: return Relation.S2_S5;
					case 5: return Relation.S2_S6;
					case 6: return Relation.S2_S7;
					default: return Relation.Invalid;
				}
			}
			case 3: 
			{
				switch(sector2)
				{
					case 6: return Relation.S4_S7;
					case 7: return Relation.S4_S8;
					case 0: return Relation.S4_S1;
					default: return Relation.Invalid;
				}
			}
			case 5: 
			{
				switch(sector2)
				{
					case 0: return Relation.S6_S1;
					case 1: return Relation.S6_S2;
					case 2: return Relation.S6_S3;
					default:return Relation.Invalid;
				}
			}
			case 7: 
			{
				switch(sector2)
				{
					case 2: return Relation.S8_S3;
					case 3: return Relation.S8_S4;
					case 4: return Relation.S8_S5;
					default: return Relation.Invalid;
				}
			}
			default: return Relation.Invalid;
			
		}
	}
	public static void main(String args[])
	{ 
		System.out.println(Relation.isNeighbor(Relation.S6_S2, Relation.S4_S8, 3));
	}
}
