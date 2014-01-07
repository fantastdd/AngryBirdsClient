package ab.objtracking.tracker;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.DirectedGraph;

import ab.objtracking.MagicParams;
import ab.objtracking.dynamic.Movement;
import ab.objtracking.dynamic.MovementPredictor;
import ab.objtracking.isomorphism.IsomorphismTest;
import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Relation;
import ab.objtracking.representation.util.DebrisToolkit;
import ab.objtracking.representation.util.GSRConstructor;
import ab.vision.ABTrackingObject;
import ab.vision.ABType;
import ab.vision.real.shape.DebrisGroup;
import ab.vision.real.shape.TrackingRect;


/**
 * 
 * Create Prefs by taking object categories into consideration
 * Detects explosion/debris
 * Analyze neighbor movement trend, Neighbor: which hold GR relations
 * Do isomorphic check
 * */
public class KnowledgeTracker_3 extends SMETracker {


	public KnowledgeTracker_3(int timegap) {
		super(timegap);
		// TODO Auto-generated constructor stub
	}

	public DirectedGraph<ABTrackingObject, ConstraintEdge> initialNetwork, newNetwork;
	protected Map<ABTrackingObject, Movement> initialObjsMovement = new HashMap<ABTrackingObject, Movement>();
	protected List<DebrisGroup> debrisGroupList;
	protected List<ABTrackingObject> debrisList;

	@Override
	public void createPrefs(List<ABTrackingObject> objs) 
	{
	
		initialNetwork = GSRConstructor.constructGRNetwork(iniObjs);
		newNetwork = GSRConstructor.constructGRNetwork(objs);
		debrisList = new LinkedList<ABTrackingObject>();
		//If no previous movement detected
		if(initialObjsMovement.isEmpty()){

			initialObjsMovement = MovementPredictor.predict(initialNetwork);
		}  
		//Create dummy debris
	    debrisGroupList = DebrisToolkit.getAllDummyRectangles(newNetwork);
		for (DebrisGroup debris : debrisGroupList)
		{
			
			System.out.println(String.format(" Debris:%s \n member1:%s \n member2:%s ", debris, debris.member1, debris.member2));
		}
		objs.addAll(debrisGroupList);	
		//Reconstruct after adding the debris
		//newNetwork =  GSRConstructor.constructGRNetwork(objs);

		//initialObjsMovement.putAll(occludedObjsMovement);
		//log(" Print New Coming Network");
		//GSRConstructor.printNetwork(newNetwork);
		log(" Print Initial Network");
		GSRConstructor.printNetwork(initialNetwork);
/*
		log("isomorphism Check: ");
		System.out.println(IsomorphismTest.isIsomorphic(newNetwork, initialNetwork));*/
		
		prefs = new HashMap<ABTrackingObject, List<Pair>>();
		iniPrefs = new HashMap<ABTrackingObject, List<Pair>>();

		for (ABTrackingObject obj : objs) 
		{	
			List<Pair> diffs = new LinkedList<Pair>();
			ABType objType = obj.type;
			for (ABTrackingObject iniObj : iniObjs) 
			{   

				if(objType == iniObj.type)
				{
					Movement movement = initialObjsMovement.get(iniObj);
					if( movement != null)
					{
						//Evaluate movement by taking spatial change into consideration
						movement = MovementPredictor.adjustMovementOnGR(movement, initialNetwork);
						
						/*if(iniObj.id == 6)
							System.out.println("\n movement " + movement + "\n" + obj + "  xshift " + (int)(obj.getCenterX() - iniObj.getCenterX()) + " yshift " + (int)(obj.getCenterY() - iniObj.getCenterY()) + 
						
									movement.isValidMovement((int)(obj.getCenterX() - iniObj.getCenterX()), (int)(obj.getCenterY() - iniObj.getCenterY()), false));*/
					
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
		for (ABTrackingObject iniObj : iniPrefs.keySet()) {
			Collections.sort(iniPrefs.get(iniObj), new PairComparator());
		}
		newComingObjs = objs;
		initialObjsMovement.clear();
		printPrefs(iniPrefs);
		//printPrefs(prefs);
	}

	protected Map<ABTrackingObject, ABTrackingObject> matchObjs(List<ABTrackingObject> moreObjs, List<ABTrackingObject> lessObjs, Map<ABTrackingObject, List<Pair>> morePrefs, Map<ABTrackingObject, List<Pair>> lessPrefs) {

		HashMap<ABTrackingObject, ABTrackingObject> current = new HashMap<ABTrackingObject, ABTrackingObject>();
		LinkedList<ABTrackingObject> freeObjs = new LinkedList<ABTrackingObject>();
		freeObjs.addAll(lessObjs);

		HashMap<ABTrackingObject, Integer> next = new HashMap<ABTrackingObject, Integer>();

		for (ABTrackingObject obj : moreObjs)
			current.put(obj, null);

		// System.out.println(" freeObjs size: " + freeObjs.size());
		for (ABTrackingObject obj : freeObjs) {
			next.put(obj, 0);
		}
		// System.out.println(" key size: " + next.keySet().size());
		// while there are no free objects or all the original objects have been
		// assigned.
		unmatchedIniObjs = new LinkedList<ABTrackingObject>();

		while (!freeObjs.isEmpty()) {

			ABTrackingObject freeObj = freeObjs.remove();
			int index = next.get(freeObj);
			/*
			 * System.out.println(obj + " " + index); for (ABObject t :
			 * next.keySet()) System.out.println(next.get(t));
			 */
			List<Pair> pairs = lessPrefs.get(freeObj);
			if (pairs == null || index == pairs.size())
				unmatchedIniObjs.add(freeObj);
			else 
			{
				Pair pair = pairs.get(index);
				ABTrackingObject moreObj = pair.obj;
				next.put(freeObj, ++index);
				if(pair.sameShape && !moreObj.isDebris && pair.diff < MagicParams.DiffTolerance)
				{
					if (current.get(moreObj) == null)
						current.put(moreObj, freeObj);
					else 
					{
						ABTrackingObject rival = current.get(moreObj);
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
	public void debrisRecognition(List<ABTrackingObject> newObjs, List<ABTrackingObject> initialObjs) {

	   
		/*for (ABObject iniObj : initialObjs)
			System.out.println("@@@" + iniObj);*/
		currentOccludedObjs.addAll(initialObjs);
		for (ABTrackingObject newObj : newObjs) 
		{
			
			List<Pair> pairs = prefs.get(newObj);
			Pair pair = null;
			int pointer = 0;
			while (pairs != null && !pairs.isEmpty()&& pointer < pairs.size() && newObj.type != ABType.Pig)
			{	
				pair = pairs.get(pointer);
				//assuming circles in the initial frame will never turn to rect //TODO robust damage components detection
				if(initialObjs.contains(pair.obj))
				{	
					break;
				}
				else
					pointer++;
			}
			newObj.id = ABTrackingObject.unassigned;
			// log(" unmatched new object: " + newObj + "  " + (newObj.type != ABType.Pig) + " " + pair);

			if (pair != null)
			{
				//TODO non necessary loop
				for (ABTrackingObject initialObj : initialObjs) {
					//if(pair.obj.id == 2)
					//	System.out.println(pair.obj + "   " + initialObj + "   " + pair.obj.equals(initialObj));
					// pair.diff's threshold can be estimated by frame frequency
					if (pair.obj.equals(initialObj) && pair.diff < MagicParams.DiffTolerance) {
					
						
						link(newObj, initialObj, true);
						matchedObjs.put(newObj, initialObj);
						debrisList.add(newObj);
						currentOccludedObjs.remove(initialObj);
						//if(initialObj.id == 10)
						//	System.out.println("@@@" + initialObj + "  " + newObj + " " + currentOccludedObjs.contains(initialObj));
						break;
						
					}

				}
			}
		}
		/* for (ABObject object : currentOccludedObjs)
	     {
	    	 log("##" + object.toString());
	     }*/
		//newObjs.removeAll(debrisList);
		// Damage Recognition, call back schema: if an object has been detected as damaged, and only one part of the object has been found, the algo will go back to check for 
		//the other part, even though that part has been matched
		for (ABTrackingObject debris: debrisList)
		{
			ABTrackingObject initialObj = matchedObjs.get(debris);
			if( initialObj instanceof TrackingRect )//&& debris instanceof Rect)
			{
				TrackingRect _initialObj = (TrackingRect)initialObj;
				//Rect _debris = (Rect)debris;
				for (ABTrackingObject newObj : newObjs)
				{
					if(debris!= newObj && newObj.type != ABType.Pig)
					{
						
						if (_initialObj.isDebris)
						{
							for(DebrisGroup group : debrisGroupList)
							{
								if (group.member1 == debris || group.member2 == debris)
								{	
									_initialObj = group;
									break;
								}
							}
							
							
						}
						/*if(_initialObj.id == 4)
						{
							System.out.println(" debris " + debris);
							System.out.println(" initial " + _initialObj + " newobj " + newObj);
							System.out.println(DebrisToolKit.isSameDebris(debris, _initialObj, newObj));
						}*/
						if(DebrisToolkit.isSameDebris(debris, _initialObj, newObj))
						{
							ABTrackingObject newObjLastMatch = matchedObjs.get(newObj);
							if(newObjLastMatch != null && newObj.id != debris.id && !currentOccludedObjs.contains(newObjLastMatch))
							{
								
								//TODO optimize the following search;
								boolean anotherMatch = false;
								for (ABTrackingObject matched : matchedObjs.keySet())
								{
									ABTrackingObject _lastmatch = matchedObjs.get(matched);
									if(_lastmatch == newObjLastMatch && matched != newObj)
									{
										anotherMatch = true;
									}
											
								}
								if(!anotherMatch)
									currentOccludedObjs.add(newObjLastMatch);
							}
							link(newObj, debris, true);
							currentOccludedObjs.remove(initialObj);
							matchedObjs.put(newObj, initialObj);
						}
						//	}
						//}
						//}
					}
				}

			}
		}
	    /* for (ABObject object : currentOccludedObjs)
	     {
	    	 log("@@" + object.toString());
	     }*/
	}


	@Override
	public boolean matchObjs(List<ABTrackingObject> objs) {
		

		matchedObjs = new HashMap<ABTrackingObject, ABTrackingObject>();
		
		currentOccludedObjs = new LinkedList<ABTrackingObject>();

		if (iniObjs != null /*&& initialObjs.size() >= objs.size()*/) 
		{

			lastInitialObjs = iniObjs;

			boolean lessIni = (objs.size() > iniObjs.size()); // If the num of initial objects > the next
			
			// log(" " + initialObjs.size() + "  " + objs.size());
			createPrefs(objs);
			//printPrefs(prefs);
			Map<ABTrackingObject, ABTrackingObject> match;
			unmatchedNewObjs = new LinkedList<ABTrackingObject>();
			List<ABTrackingObject> membersOfMatchedDebrisGroup = new LinkedList<ABTrackingObject>();
			if (!lessIni) 
			{
				match = matchObjs(iniObjs, objs, iniPrefs, prefs);

				// Assign Id
				for (ABTrackingObject iniObj : match.keySet()) {
					
					ABTrackingObject obj = match.get(iniObj);
					if (obj != null)
					{
					    link(obj, iniObj, iniObj.isDebris);
						matchedObjs.put(obj, iniObj);
						if(obj.isDebris)
							debrisList.add(obj);
						if(obj instanceof DebrisGroup)
						{
							DebrisGroup debris = (DebrisGroup)obj;
							ABTrackingObject member1 = debris.member1;
							ABTrackingObject member2 = debris.member2;
							//assign id after debris recognition, otherwise unmatchedLessObjs cannot remove 
							unmatchedIniObjs.remove(member1);
							unmatchedIniObjs.remove(member2);
							matchedObjs.remove(member1);
							matchedObjs.remove(member2);
							link(member1, obj, true);
							link(member2, obj, true);
							matchedObjs.put(member1, iniObj);
							matchedObjs.put(member2, iniObj);

						}
						if(iniObj instanceof DebrisGroup)
						{
							DebrisGroup debris = (DebrisGroup)iniObj;
							membersOfMatchedDebrisGroup.add(debris.member1);
							membersOfMatchedDebrisGroup.add(debris.member2);
						}
					}
					else	
							unmatchedNewObjs.add(iniObj);
						
				}
				unmatchedNewObjs.removeAll(membersOfMatchedDebrisGroup);
				// log(" debris recognition WAS performed: more objects in the initial");
				debrisRecognition(unmatchedIniObjs, unmatchedNewObjs);
			} else {
				log(" Next frame has more objs");
				/*
				 * Map<ABObject, List<Pair>> temp; temp = iniPrefs; iniPrefs =
				 * prefs; prefs = temp;
				 */
				match = matchObjs(objs, iniObjs, prefs, iniPrefs);
				// Assign Id
				for (ABTrackingObject obj : match.keySet()) {

					if(matchedObjs.containsKey(obj))
						continue;
					else
					{

						ABTrackingObject iniObj = match.get(obj);

						if (iniObj != null)
						{	
							link(obj, iniObj, iniObj.isDebris);
							matchedObjs.put(obj, iniObj);
							if(obj.isDebris)
								debrisList.add(obj);
							if(obj instanceof DebrisGroup)
							{
								DebrisGroup debris = (DebrisGroup)obj;
								ABTrackingObject member1 = debris.member1;
								ABTrackingObject member2 = debris.member2;

								unmatchedNewObjs.remove(member1);
								unmatchedNewObjs.remove(member2);
								/*   if (obj.id == 7)
						    {
						    	System.out.println(String.format("member1: %s %s member2: %s %s ", 
						    			member1, objs.contains(member1), member2, objs.contains(member2)));
						    	for (ABObject _obj : unmatchedMoreObjs)
						    	{
						    		System.out.println(_obj);
						    	}
						    	System.out.println("======================");
						    }*/
								matchedObjs.remove(member1);
								matchedObjs.remove(member2);
								link(member1, obj, true);
								link(member2, obj, true);
								matchedObjs.put(member1, iniObj);
								matchedObjs.put(member2, iniObj);

							}
						}
						else
							unmatchedNewObjs.add(obj);
					}
				}
				// Process unassigned objs
				/*//log("debris recognition was performed");
				for (abobject obj : unmatchedmoreobjs)
				{
					log(obj.tostring());
				}*/
				debrisRecognition(unmatchedNewObjs, unmatchedIniObjs);

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
			for (ABTrackingObject occludedObj : currentOccludedObjs)
				System.out.println(occludedObj);

			
			
			
			objs.addAll(currentOccludedObjs);
			objs.removeAll(occludedObjsBuffer); // remove all the remembered occluded objects from the previous frame. We only buffer one frame.
			occludedObjsBuffer.addAll(currentOccludedObjs);
			//Those who has the same id and has rectangular shape 

			//Set Initial Objs Movements
			initialObjsMovement.clear();
			for (ABTrackingObject obj : matchedObjs.keySet())
			{
				ABTrackingObject initial = matchedObjs.get(obj);
				if(initial != null){
					Movement movement = new Movement(obj);
					movement.generateInertia(initial);
					initialObjsMovement.put(obj, movement);
					/*if(obj.id == 7)
						System.out.println(" Generate Initial " + " obj " + obj + " initial " + initial + " " +movement);*/
				}
			}

			isomorphismProcess(initialNetwork, newNetwork, objs);
			
			this.setIniObjs(objs);
			
			//GSRConstructor.printNetwork(newNetwork);
			//printPrefs(prefs);
			/*log(" Print all Objects (next frame) after matching");
			for (ABObject obj : objs)
			{
				log(obj.toString());
			}*/
			return true;
		}
		return false;
	}

/**
 * Retain only those iniObjs:
	1: has been matched
	2: matched objs are not debris 
 * */
	protected void isomorphismProcess(DirectedGraph<ABTrackingObject, ConstraintEdge> iniNetwork, DirectedGraph<ABTrackingObject, ConstraintEdge> newNetwork, List<ABTrackingObject> newObjs)
	{
	
		List<ABTrackingObject> removedInitialObjs = new LinkedList<ABTrackingObject>();
		List<ABTrackingObject> matchedDebrisIniObjs = new LinkedList<ABTrackingObject>();
		for (ABTrackingObject obj : newObjs)
		{
			ABTrackingObject initialObj = matchedObjs.get(obj);
			/*System.out.println("==============");
			System.out.println(obj);
			System.out.println(initialObj);*/
			if(initialObj != null)
			{
			    if(obj.isDebris && !( obj instanceof DebrisGroup))
			    {
			    	newNetwork.removeVertex(obj);
			    	removedInitialObjs.add(initialObj);
			    	//iniNetwork.removeVertex(initialObj);
			    }
			    else
			    	if(obj instanceof DebrisGroup)
			    	{
			    		GSRConstructor.addVertexToGRNetwork(obj, newNetwork);
			    		matchedDebrisIniObjs.add(initialObj);
			    	}
			}
			else
				newNetwork.removeVertex(obj);
		}
		removedInitialObjs.removeAll(matchedDebrisIniObjs);
		for (ABTrackingObject obj: removedInitialObjs)
		{
			iniNetwork.removeVertex(obj);
		}
		
		for (ABTrackingObject obj : currentOccludedObjs)
		{
			iniNetwork.removeVertex(obj);
		}

		//IMPORTANT: Since the newObj network has been created before matching (id can be changed), so e.g. NewObj (id = 12) = InitialObj(id = 13), then newObj's id will be changed to 13, The edge should be also changed!
		//Redirect edges;
		List<ConstraintEdge> removedEdges = new LinkedList<ConstraintEdge>();
		List<ConstraintEdge> addedEdges = new LinkedList<ConstraintEdge>();
		for (ConstraintEdge edge : newNetwork.edgeSet())
		{
			ABTrackingObject source = edge.getSource();
			ABTrackingObject target = edge.getTarget();
			if(source.id > target.id)
			{
				removedEdges.add(edge);
			    addedEdges.add(new ConstraintEdge(target, source, Relation.inverse(edge.label)));
			     
			}
		}
		newNetwork.removeAllEdges(removedEdges);
		for (ConstraintEdge edge: addedEdges)
		{
			newNetwork.addEdge(edge.getSource(), edge.getTarget(), edge);
		}
		
		/*log("print initial network\n");
		GSRConstructor.printNetwork(iniNetwork);
		log("print newNetwork\n");
		GSRConstructor.printNetwork(newNetwork);*/
		if (! IsomorphismTest.isIsomorphic(newNetwork, iniNetwork))
		{
			ABTrackingObject source = IsomorphismTest.getLastConflictSource();
			ABTrackingObject target = IsomorphismTest.getLastConflictTarget();
			if(source != null && source.type == target.type){
				
				log(" Conflict Pair");
				log(source.toString());
				log(target.toString());
				int temp = source.id;
				source.id = target.id;
				target.id = temp;
				if(source instanceof DebrisGroup)
				{
					DebrisGroup group = ((DebrisGroup)source);
					group.member1.id = group.id;
					group.member2.id = group.id;
				}
				if(target instanceof DebrisGroup)
				{
					DebrisGroup group = ((DebrisGroup)target);
					group.member1.id = group.id;
					group.member2.id = group.id;
				}
				}
			
			
			
			log(" Not Isomorphic");
		}
		else
			log(" Isomorphic");
		
		
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
