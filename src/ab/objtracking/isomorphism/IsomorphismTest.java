package ab.objtracking.isomorphism;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.experimental.isomorphism.IsomorphismRelation;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Relation;
import ab.vision.ABTrackingObject;

public class IsomorphismTest {

	private static GREdgeChecker<ABTrackingObject, ConstraintEdge> edgeChecker;
	public static boolean isIsomorphic(DirectedGraph<ABTrackingObject, ConstraintEdge> graph1, DirectedGraph<ABTrackingObject, ConstraintEdge> graph2)
	{
		edgeChecker = new GREdgeChecker<ABTrackingObject, ConstraintEdge>();
		@SuppressWarnings("unchecked")
		GraphIsomorphismInspector<IsomorphismRelation<ABTrackingObject, ConstraintEdge>> inspector 
		= AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(graph1, graph2, new GRVertexChecker<ABTrackingObject, ConstraintEdge>(), edgeChecker);
		return inspector.isIsomorphic();
	}
	
	public static ABTrackingObject getLastConflictSource()
	{
		return edgeChecker.lastConflictSource;
	}
	public static ABTrackingObject getLastConflictTarget()
	{
		return edgeChecker.lastConflictTarget;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimpleDirectedGraph<ABTrackingObject, ConstraintEdge> graph1 = new SimpleDirectedGraph<ABTrackingObject, ConstraintEdge>(new ClassBasedEdgeFactory<ABTrackingObject, ConstraintEdge>(ConstraintEdge.class));
		SimpleDirectedGraph<ABTrackingObject, ConstraintEdge> graph2 = new SimpleDirectedGraph<ABTrackingObject, ConstraintEdge>(new ClassBasedEdgeFactory<ABTrackingObject, ConstraintEdge>(ConstraintEdge.class));
		ABTrackingObject o1 = new ABTrackingObject();
		//o1.id = 12;
		o1.width = 10;
		System.out.println(o1.hashCode());
		ABTrackingObject o2 = new ABTrackingObject();
		//o2.id = 11;
		o2.width = 1500;
		System.out.println(o2.hashCode());
		ABTrackingObject o3 = new ABTrackingObject();
		//o3.id = 10;
		o3.width = 12;
		System.out.println(o3.hashCode());
		
		ABTrackingObject o4 = new ABTrackingObject();
		//o4.id = 10;
		o4.width = 1500;
		System.out.println(o4.hashCode());
		ABTrackingObject o5 = new ABTrackingObject();
		//o5.id = 11;
		o5.width = 10;
		System.out.println(o5.hashCode());
		ABTrackingObject o6 = new ABTrackingObject();
		//o6.id = 12;
		o6.width = 12;
		System.out.println(o6.hashCode());
		
		
		graph1.addVertex(o1);
		graph1.addVertex(o2);
		graph1.addVertex(o3);
		
		graph2.addVertex(o5);
		graph2.addVertex(o4);
		graph2.addVertex(o6);
		
		graph1.addEdge(o1, o2, new ConstraintEdge(o1, o2, Relation.UNASSIGNED));
		graph1.addEdge(o2, o3, new ConstraintEdge(o2, o3, Relation.S1_S6));
		graph1.addEdge(o1, o3, new ConstraintEdge(o1, o3, Relation.UNASSIGNED));
		
		
		graph2.addEdge(o5, o6, new ConstraintEdge(o5, o6, Relation.UNASSIGNED));
		graph2.addEdge(o4, o6, new ConstraintEdge(o4, o6, Relation.UNASSIGNED));
		graph2.addEdge(o4, o5, new ConstraintEdge(o4, o5, Relation.S1_S6));
		
		
		System.out.println(isIsomorphic(graph1, graph2));

	}

}
