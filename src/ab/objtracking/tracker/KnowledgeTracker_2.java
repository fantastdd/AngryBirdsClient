package ab.objtracking.tracker;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import ab.objtracking.MagicParams;
import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Movement;
import ab.objtracking.representation.util.DebrisToolKit;
import ab.objtracking.representation.util.GSRConstructor;
import ab.objtracking.representation.util.MovementPredictor;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.real.shape.DebrisGroup;
import ab.vision.real.shape.Rect;


/**
 * 
 * Create Prefs by taking object categories into consideration
 * Detects explosion/debris
 * Analyze neighbor movement trend, Neighbor: which hold GR relations
 * 
 * */
public class KnowledgeTracker_2 extends SMETracker {


	public DirectedGraph<ABObject, ConstraintEdge> initialNetwork, newNetwork;
	protected Map<ABObject, Movement> initialObjsMovement = new HashMap<ABObject, Movement>();
	
	
	@Override
	public void createPrefs(List<ABObject> objs) 
	{
		initialNetwork = GSRConstructor.constructGRNetwork(initialObjs);
		newNetwork = GSRConstructor.constructGRNetwork(objs);
		
		//If no previous movement detected
		if(initialObjsMovement.isEmpty()){
			/*Map<ABObject, Movement> occludedObjsMovement = new HashMap<ABObject, Movement>();
			for (ABObject object: currentOccludedObjs)
			{
				occludedObjsMovement.put(object, initialObjsMovement.get(object));
			}*/
			initialObjsMovement = MovementPredictor.predict(initialNetwork);
		}  
		//Create dummy debris
		List<DebrisGroup> debrisList = DebrisToolKit.getAllDummyRectangles(newNetwork);
		for (DebrisGroup debris : debrisList)
		{
			System.out.println(String.format(" Debris:%s member1:%s member2:%s ", debris, debris.member1, debris.member2));
		}
		objs.addAll(debrisList);		
		
		
		//initialObjsMovement.putAll(occludedObjsMovement);
		
		
		GSRConstructor.printNetwork(newNetwork);
		
		prefs = new HashMap<ABObject, List<Pair>>();
		iniPrefs = new HashMap<ABObject, List<Pair>>();
	
		for (ABObject obj : objs) 
		{	
			List<Pair> diffs = new LinkedList<Pair>();
			ABType objType = obj.type;
			for (ABObject iniObj : initialObjs) {
				if(objType == iniObj.type)
				{
					Movement movement = initialObjsMovement.get(iniObj);
					if( movement != null)
					{
						//Evaluate movement by taking spatial change into consideration
						movement = MovementPredictor.adjustMovement(movement, initialNetwork);
						if(iniObj.id == 8)
							System.out.println(movement);
						//Sysstem.out.println(obj + "  " + movement.isValidMovement((int)(obj.getCenterX() - iniObj.getCenterX()), (int)(obj.getCenterY() - iniObj.getCenterY()), false));
					}
					if(movement == null || movement.isValidMovement((int)(obj.getCenterX() - iniObj.getCenterX()), (int)(obj.getCenterY() - iniObj.getCenterY()), false) )
					{
						float squareShift = calMassShift(obj, iniObj);
						boolean sameShape = iniObj.isSameShape(obj);
						diffs.add(new Pair(iniObj, squareShift, sameShape));
						if (!iniPrefs.containsKey(iniObj)) 
						{
							List<Pair> iniDiffs = new LinkedList<Pair>();
							iniDiffs.add(new Pair(obj, squareShift, sameShape));
							iniPrefs.put(iniObj, iniDiffs);
					}
					else
					{
						iniPrefs.get(iniObj).add(
								new Pair(obj, squareShift, sameShape));
					}	
				}
			}
	
			}
			Collections.sort(diffs, new PairComparator());
			prefs.put(obj, diffs);
		}
		for (ABObject iniObj : iniPrefs.keySet()) {
			Collections.sort(iniPrefs.get(iniObj), new PairComparator());
		}
		newComingObjs = objs;
		initialObjsMovement.clear();
		printPrefs(iniPrefs);
		//printPrefs(prefs);
	}
	
	protected Map<ABObject, ABObject> matchObjs(List<ABObject> moreObjs, List<ABObject> lessObjs, Map<ABObject, List<Pair>> morePrefs, Map<ABObject, List<Pair>> lessPrefs) {
		
		HashMap<ABObject, ABObject> current = new HashMap<ABObject, ABObject>();
		LinkedList<ABObject> freeObjs = new LinkedList<ABObject>();
		freeObjs.addAll(lessObjs);
	
		HashMap<ABObject, Integer> next = new HashMap<ABObject, Integer>();
	
		for (ABObject obj : moreObjs)
			current.put(obj, null);
	
		// System.out.println(" freeObjs size: " + freeObjs.size());
		for (ABObject obj : freeObjs) {
			next.put(obj, 0);
		}
		// System.out.println(" key size: " + next.keySet().size());
		// while there are no free objects or all the original objects have been
		// assigned.
		unmatchedLessObjs = new LinkedList<ABObject>();
	
		while (!freeObjs.isEmpty()) {
	
			ABObject freeObj = freeObjs.remove();
			int index = next.get(freeObj);
			/*
			 * System.out.println(obj + " " + index); for (ABObject t :
			 * next.keySet()) System.out.println(next.get(t));
			 */
			List<Pair> pairs = lessPrefs.get(freeObj);
			if (pairs == null || index == pairs.size())
				unmatchedLessObjs.add(freeObj);
			else 
			{
					Pair pair = pairs.get(index);
					ABObject moreObj = pair.obj;
					next.put(freeObj, ++index);
					if(pair.sameShape && pair.diff < 1000)
					{
						if (current.get(moreObj) == null)
							current.put(moreObj, freeObj);
						else 
						{
							ABObject rival = current.get(moreObj);
							if (prefers(moreObj, freeObj, rival, morePrefs)) 
							{
								current.put(moreObj, freeObj);
								freeObjs.add(rival);
							}
							else
								freeObjs.add(freeObj);
						}
					}
					else
						freeObjs.add(freeObj);
				}
		}
	
		return current;
	}
	@Override
	public void debrisRecognition(List<ABObject> newObjs, List<ABObject> initialObjs) {
		
		List<ABObject> debrisList = new LinkedList<ABObject>();
		currentOccludedObjs.addAll(initialObjs);
		for (ABObject newObj : newObjs) 
		{
			
			List<Pair> pairs = prefs.get(newObj);
			Pair pair = null;
			int pointer = 0;
			while (!pairs.isEmpty()&& pointer < pairs.size() && newObj.type != ABType.Pig)
			{	
				pair = pairs.get(pointer);
				//assuming circles in the initial frame will never turn to rect //TODO robust damage components detection
				if(initialObjs.contains(pair.obj))
				{	
					/*for (ABObject _obj : initialObjs)
						System.out.println(_obj + "  " + _obj.hashCode());
					System.out.println(pair.obj + "  " + pair.obj.hashCode() + "  " + initialObjs.contains(pair.obj));*/
					break;
				}
				else
					pointer++;
			}
			newObj.id = ABObject.unassigned;
			// log(" unmatched new object: " + newObj + "  " + (newObj.type != ABType.Pig) + " " + pair);
				
			if (pair != null)
			{
				//System.out.println(" pair check");
				//TODO non necessary loop
				for (ABObject initialObj : initialObjs) {
					//System.out.println(initialObj);
					//if(pair.obj.id == 2)
					//	System.out.println(pair.obj + "   " + initialObj + "   " + pair.obj.equals(initialObj));
					// pair.diff's threshold can be estimated by frame frequency
					if (pair.obj.equals(initialObj) && pair.diff < MagicParams.DiffTolerance) {
						// System.out.println(pair.obj + "  " +
						// pair.obj.hashCode() + "   " + initialObj + "  " +
						// initialObj.hashCode() + "   " +
						// pair.obj.equals(initialObj));
						newObj.id = initialObj.id;
						matchedObjs.put(newObj, initialObj);
						debrisList.add(newObj);
						currentOccludedObjs.remove(initialObj);
						break;
						// log(" matched initial object: " + initialObjs);
					}
	
				}
			}
		}
		//newObjs.removeAll(debrisList);
		// Damage Recognition, call back schema: if an object has been detected as damaged, and only one part of the object has been found, the algo will go back to check for 
		//the other part, even though that part has been matched
		for (ABObject debris: debrisList)
		{
			ABObject initialObj = matchedObjs.get(debris);
			if( initialObj instanceof Rect )//&& debris instanceof Rect)
			{
				Rect _initialObj = (Rect)initialObj;
				//Rect _debris = (Rect)debris;
				for (ABObject newObj : newObjs)
				{
					if(/*unmatchedDebris.id == ABObject.unassigned &&*/ newObj.type != ABType.Pig)
					{
						//System.out.println(" debris " + debris);
						//System.out.println(" unmatched " + unmatchedDebris);
					//	Rect dummy = debris.extend(_initialObj.rectType);
						//System.out.println(" initial " + _initialObj + " newobj " + newObj + " dummy" + dummy);
						//System.out.println(" dummy " + dummy);
					//	Polygon p = dummy.p;
						
					//	if(p.contains(newObj.getCenter()) && (debris.type == newObj.type))// && newObj instanceof Rect)//damage detection only supports rect currently
						//{
							//Inverse Check
							//dummy = newObj.extend(_initialObj.rectType);
							if(debris != newObj && DebrisToolKit.isSameDebris(debris, _initialObj, newObj))
							{
								ABObject newObjLastMatch = matchedObjs.get(newObj);
								if(newObjLastMatch != null && newObj.id != debris.id && !currentOccludedObjs.contains(newObjLastMatch))
									currentOccludedObjs.add(newObjLastMatch);
								newObj.id = debris.id;
								currentOccludedObjs.remove(initialObj);
								matchedObjs.put(newObj, initialObj);
							}
					//	}
					//}
				//}
			}
		}
		
	}
		}}
	
	
	@Override
	public boolean matchObjs(List<ABObject> objs) {
		/*
		 * if(initialObjs != null) System.out.println(initialObjs.size());
		 */
		// System.out.println(objs.size());
		// Do match, assuming initialObjs.size() > objs.size(): no objects will
		// be created
		matchedObjs = new HashMap<ABObject, ABObject>();
		currentOccludedObjs = new LinkedList<ABObject>();
		
		if (initialObjs != null /*&& initialObjs.size() >= objs.size()*/) 
		{
	
			lastInitialObjs = initialObjs;
	
			boolean lessIni = (objs.size() > initialObjs.size()); // If the num
																	// of3.d
																	// initial
																	// objects >
																	// next
																	// frame obj
			// log(" " + initialObjs.size() + "  " + objs.size());
			createPrefs(objs);
			//printPrefs(prefs);
			Map<ABObject, ABObject> match;
			unmatchedMoreObjs = new LinkedList<ABObject>();
			if (!lessIni) 
			{
				match = matchObjs(initialObjs, objs, iniPrefs, prefs);
	
				// Assign Id
				for (ABObject iniObj : match.keySet()) {
					ABObject obj = match.get(iniObj);
					if (obj != null)
					{
						obj.id = iniObj.id;
						matchedObjs.put(obj, iniObj);
						if(obj instanceof DebrisGroup)
						{
							DebrisGroup debris = (DebrisGroup)obj;
							ABObject member1 = debris.member1;
							ABObject member2 = debris.member2;
							member1.id = obj.id;
							member2.id = obj.id;
							matchedObjs.put( member1, iniObj);
							matchedObjs.put(member2, iniObj);
							unmatchedLessObjs.remove(member1);
							unmatchedLessObjs.remove(member2);
						}
					}
					else
						unmatchedMoreObjs.add(iniObj);
				}
	
				// log(" debris recognition WAS performed: more objects in the initial");
				debrisRecognition(unmatchedLessObjs, unmatchedMoreObjs);
			} else {
				log(" more objs in next frame");
				/*
				 * Map<ABObject, List<Pair>> temp; temp = iniPrefs; iniPrefs =
				 * prefs; prefs = temp;
				 */
				match = matchObjs(objs, initialObjs, prefs, iniPrefs);
				// Assign Id
				for (ABObject obj : match.keySet()) {
					ABObject iniObj = match.get(obj);
					if (iniObj != null)
					{	
						obj.id = iniObj.id; 
						matchedObjs.put(obj, iniObj);
						if(obj instanceof DebrisGroup)
						{
							DebrisGroup debris = (DebrisGroup)obj;
							ABObject member1 = debris.member1;
							ABObject member2 = debris.member2;
							member1.id = obj.id;
							member2.id = obj.id;
							matchedObjs.put( member1, iniObj);
							matchedObjs.put(member2, iniObj);
							unmatchedMoreObjs.remove(member1);
							unmatchedMoreObjs.remove(member2);
						}
					}
					else
						unmatchedMoreObjs.add(obj);
				}
				// Process unassigned objs
				// log("debris recognition WAS performed");
				debrisRecognition(unmatchedMoreObjs, unmatchedLessObjs);
	
			}
			
			/*log(" Movement Consistency Check ");
			List<Movement> movementConflicts = new LinkedList<Movement>();
			for (ABObject source: initialNetwork.vertexSet())
			{
					validateMovement(source, initialNetwork, movementConflicts);
			}
			for (Movement movement: movementConflicts)
			{
				
			}*/
			
			
			log("Print Occluded Objects");
			for (ABObject occludedObj : currentOccludedObjs)
				System.out.println(occludedObj);
			
			objs.addAll(currentOccludedObjs);
			objs.removeAll(occludedObjsBuffer); // remove all the remembered occluded objects from the previous frame. We only buffer one frame.
			occludedObjsBuffer.addAll(currentOccludedObjs);
			//Those who has the same id and has rectangluar shape 
			
			//Set Initial Objs Movements
			initialObjsMovement.clear();
			for (ABObject obj : matchedObjs.keySet())
			{
				ABObject initial = matchedObjs.get(obj);
				if(initial != null){
					Movement movement = new Movement(obj);
					movement.generateInertia(initial);
					initialObjsMovement.put(obj, movement);
					if(obj.id == 7)
						System.out.println(" Generate Initial " + " obj " + obj + " initial " + initial + " " +movement);
				}
			}
			
			
			this.setInitialObjects(objs);
			//printPrefs(prefs);
			return true;
		}
		return false;
	}
	
	
	protected boolean validateMovement(ABObject source, DirectedGraph<ABObject, ConstraintEdge> network, List<Movement> globalConflicts)
	{
		Set<ConstraintEdge> edges = network.edgesOf(source);
		int size = edges.size();
		Movement sourceMovement = initialObjsMovement.get(source);
		int count = 0;
		List<Movement> conflicts = new LinkedList<Movement>();
		for (ConstraintEdge edge : edges)
		{
			ABObject target = edge.getSource();
			if(source.id == target.id)
				target = edge.getTarget();
			Movement movement = initialObjsMovement.get(target);
			boolean isSameMovement = sourceMovement.isSameMovement(movement);
			if(!isSameMovement && movement.landMarkMovement)
				return false;
			else if (isSameMovement)
				count ++;
			else
				if(!isSameMovement)
				{
						movement.setCorrectMovement(sourceMovement.xDirection, sourceMovement.yDirection);
						conflicts.add(movement);
				}
			
		}
		if (size > 3 || count > size/2)
		{
			sourceMovement.landMarkMovement = true;
			globalConflicts.remove(sourceMovement); //in case added by other objs
			globalConflicts.addAll(conflicts);
		}
		return true;
	}
	
	
	
	public static void main(String args[])
	{
		double x = 644;
		double y = 346.5;
		double _x = 636.5;
		double _y = 340;
		float r = (float)(((x - _x)*(x - _x) + (y - _y) * (y - _y)));
		assert(_y < 0);
		System.out.println(r);
	}
}