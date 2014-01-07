package ab.objtracking.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import ab.objtracking.MagicParams;
import ab.objtracking.Tracker;
import ab.objtracking.TrackingFrameComparison;
import ab.objtracking.dynamic.Movement;
import ab.objtracking.dynamic.MovementPredictor;
import ab.objtracking.representation.ConstraintEdge;
import ab.objtracking.representation.Relation;
import ab.objtracking.representation.util.DebrisToolkit;
import ab.objtracking.representation.util.GSRConstructor;
import ab.objtracking.representation.util.GlobalObjectsToolkit;
import ab.objtracking.representation.util.ShapeToolkit;
import ab.vision.ABTrackingObject;
import ab.vision.ABType;
import ab.vision.real.shape.DebrisGroup;
import ab.vision.real.shape.TrackingRect;


/**
 * 
 * Create Prefs by taking object categories into consideration
 * Detects explosion/debris
 * Analyze neighbor movement trend, Neighbor: which hold GR relations
 * 
 * 
 * Solve Dynamic Problems
 * Add Debris Collector
 * Add speed consideration: 3 ms per pixel
 * Integrate with Occluded Toolkit
 * */
public class KnowledgeTrackerBaseLine_7 extends SMETracker {


	public DirectedGraph<ABTrackingObject, ConstraintEdge> iniGRNetwork, newGRNetwork, iniFullNetwork, newFullNetwork;
	protected Map<ABTrackingObject, Movement> iniObjsMovement = new HashMap<ABTrackingObject, Movement>();
	protected List<DebrisGroup> debrisGroupList = new LinkedList<DebrisGroup>();
	protected List<ABTrackingObject> debrisList;
	protected boolean lessIniObjs = false;
	
	
	
	protected List<Set<ABTrackingObject>> iniKGroups, newKGroups;
	
	
	
	public KnowledgeTrackerBaseLine_7(int timegap) {
		super(timegap);
		maximum_distance = (timegap/3 + 1) * (timegap/3 + 1);
	}
	
	protected void preprocessDebris(List<ABTrackingObject> iniObjs)
	{
		Set<Integer> debrisGroupIDs = new HashSet<Integer>();
		//preprocess debris in iniObjs
		
		//Get all known debris group ID
		for (ABTrackingObject iniObj : iniObjs)
		{
			if(iniObj instanceof DebrisGroup)
			{
				debrisGroupIDs.add(iniObj.id);
			}
		}
		// check disassociated debris
		for (int i = 0; i < iniObjs.size() - 1; i++)
		{
			ABTrackingObject iniObj = iniObjs.get(i);
			if (iniObj.isDebris && !debrisGroupIDs.contains(iniObj.id))
			{
				List<ABTrackingObject> allOtherPieces = new ArrayList<ABTrackingObject>();
				for (int j = i + 1; j < iniObjs.size(); j++)
				{
					ABTrackingObject _obj = iniObjs.get(j);
					if (_obj.id == iniObj.id)
					{
						allOtherPieces.add(_obj);
					}
				}
				//only repair two pieces (most likely occluded by the cloud)
				if(allOtherPieces.size() == 1)
				{
					DebrisGroup debris = DebrisToolkit.debrisReconstruct(iniObj, allOtherPieces.get(0));// Test 60 and 61
					if(debris != null)
					{ 
						debrisGroupIDs.add(debris.id);
						//log("add new debris");
						//log(debris + "");
						iniObjs.add(debris);
					}
				}
			}
		}
	} 
	
	@Override
	public void createPrefs(List<ABTrackingObject> newObjs) 
	{
		
		List<DirectedGraph<ABTrackingObject, ConstraintEdge>> graphs = GSRConstructor.contructNetworks(iniObjs);
		iniGRNetwork = graphs.get(1);
		iniFullNetwork = graphs.get(0);
		iniKGroups = GSRConstructor.getAllKinematicsGroups(iniGRNetwork);
		
		preprocessDebris(iniObjs);
		
		graphs = GSRConstructor.contructNetworks(newObjs);
		
		newGRNetwork = graphs.get(1);
		newFullNetwork = graphs.get(0);
				
		debrisList = new LinkedList<ABTrackingObject>();
		//If no previous movement detected
		if(iniObjsMovement.isEmpty()){

			iniObjsMovement = MovementPredictor.predict(iniGRNetwork);
		}  
	    debrisGroupList = DebrisToolkit.getAllDummyRectangles(newGRNetwork);
	    //Create dummy debris
		if (debrisGroupList.isEmpty())
			log("\n No Debris Group Detected ");
		else
			log(" Create Debris on New Objects");
	    for (DebrisGroup debris : debrisGroupList)	
			log(debris.toString());
		
		newObjs.addAll(debrisGroupList);
		
		//printMovement(iniObjsMovement);
		
		
		
		prefs = new HashMap<ABTrackingObject, List<Pair>>();
		iniPrefs = new HashMap<ABTrackingObject, List<Pair>>();

		for (ABTrackingObject obj : newObjs) 
		{	
			List<Pair> diffs = new LinkedList<Pair>();
			ABType objType = obj.type;
			for (ABTrackingObject iniObj : iniObjs) 
			{   

				if(objType == iniObj.type)
				{
					Movement movement = iniObjsMovement.get(iniObj);
					
					if( movement != null)
					{
						//Evaluate movement by taking spatial change into consideration, evaluating on iniGRFullNetwork
						movement = MovementPredictor.adjustMovementOnGR(movement, iniGRNetwork);
						//System.out.println(movement);
						//System.out.println(movement.isValidMovement((int)(obj.getCenterX() - iniObj.getCenterX()), (int)(obj.getCenterY() - iniObj.getCenterY()), false));
						/*if(iniObj.id == 6)
							System.out.println("\n movement " + movement + "\n" + obj + "  xshift " + (int)(obj.getCenterX() - iniObj.getCenterX()) + " yshift " + (int)(obj.getCenterY() - iniObj.getCenterY()) + 
						
									movement.isValidMovement((int)(obj.getCenterX() - iniObj.getCenterX()), (int)(obj.getCenterY() - iniObj.getCenterY()), false));*/
					}
					
					if(movement == null || movement.isValidMovement(iniObj, obj, false))
					{
						float squareShift = calMassShift(obj, iniObj);
						boolean sameShape = iniObj.isSameShape(obj);
						if(squareShift < maximum_distance)
						{
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

			}
			Collections.sort(diffs, new PairComparator());
			prefs.put(obj, diffs);
		}
		for (ABTrackingObject iniObj : iniPrefs.keySet()) {
			Collections.sort(iniPrefs.get(iniObj), new PairComparator());
		}
		newComingObjs = newObjs;
		
		//printPrefs(iniPrefs);
		//printPrefs(prefs);
	}
	protected void printMovement(Map<ABTrackingObject, Movement> movements)
	{
		log("\n Print Initial Objects Movements");
		for (ABTrackingObject obj : movements.keySet())
		{
			log(movements.get(obj) + "");
		}
	}
	protected Map<ABTrackingObject, ABTrackingObject> matchObjs(List<ABTrackingObject> iniObjs, List<ABTrackingObject> newObjs)
	{
		Map<ABTrackingObject, ABTrackingObject> newToIniMatch = new HashMap<ABTrackingObject, ABTrackingObject>();
		Map<ABTrackingObject, ABTrackingObject> iniToNewMatch = new HashMap<ABTrackingObject, ABTrackingObject>();
		
		getNextMatchSet(iniObjs, newObjs, newToIniMatch, iniToNewMatch);
		
        //printMatch(iniToNewMatch, false);
		
		evaluateMatch(iniToNewMatch, newToIniMatch, iniKGroups);
		
		return newToIniMatch;
	}
	/**
	 * Convert A - R - B, A - R - C, B - R - C to  A - R - B - R - C. Thus removing the indirect relation between A and C
	 * 
	 * **/
	protected void evaluateMatch(Map<ABTrackingObject, ABTrackingObject> iniToNewMatch, Map<ABTrackingObject, ABTrackingObject> NewToIniMatch, List<Set<ABTrackingObject>> kinematicGroups)
	{
		//check each group;
		 for (Set<ABTrackingObject> kg: kinematicGroups)
		 {
			 List<ABTrackingObject> list = new ArrayList<ABTrackingObject>();
			 list.addAll(kg);
			 int n = list.size();
			 //Most of the groups are of size 2
			 if( n == 2)
			 {
				 ABTrackingObject _o1 = list.get(0);
				 ABTrackingObject _newO1 = iniToNewMatch.get(_o1);
				 if(_newO1 != null){
					 ABTrackingObject _o2 = list.get(1);
					 ConstraintEdge e;
					 Relation r;
					 e = iniFullNetwork.getEdge(_o1, _o2);
					 if( e == null)
					 {
						 e = iniFullNetwork.getEdge(_o2, _o1);
						 r = Relation.inverse(e.label);
					 }
					 else
						  r = e.label;
					
					 ABTrackingObject _newO2 = iniToNewMatch.get(_o2);
					 if(_newO2 != null)
					 {
						 Relation _r = (GSRConstructor.computeRectToRectRelation(_newO1, _newO2)).r;
						 //System.out.println(newO1 + "  " + newO2 + "  " + _r + "   " + r);
						 if ( Relation.isOpposite(_o1, _o2, r, _newO1, _newO2, _r) && _o1.type == _o2.type) 
						 {
							 //log("@@ " + _o1.id + "  " + _o2.id + "  " + r + "  " + _r);
							 swap( iniToNewMatch, NewToIniMatch, _o1, _o2, _newO1, _newO2 );
					 }
					 }
				 }
			 }
			 else{
				 int newn = 0;
				 while( n > 0)
				 {
					 newn = 0;
					 for (int i = 1; i < n - 1; i++ )
					 {
						 ABTrackingObject o1 = list.get(i - 1);
						 ABTrackingObject newO1 = iniToNewMatch.get(o1);
						 if(newO1 != null)
						 {
							 ABTrackingObject o2 = list.get(i);
							 ConstraintEdge e;
							 Relation r;
							 e = iniFullNetwork.getEdge(o1, o2);
							 if( e == null)
							 {
								 e = iniFullNetwork.getEdge(o2, o1);
								 r = Relation.inverse(e.label);
							 }
							 else
								  r = e.label;
							
							 ABTrackingObject newO2 = iniToNewMatch.get(o2);
							 if(newO2 != null)
							 {
								 Relation _r = (GSRConstructor.computeRectToRectRelation(newO1, newO2)).r;
								 //System.out.println(newO1 + "  " + newO2 + "  " + _r + "   " + r);
								 if ( Relation.isOpposite(o1, o2, r, newO1, newO2, _r) && o1.type == o2.type) 
								 {
									
									 swap( iniToNewMatch, NewToIniMatch, o1, o2, newO1, newO2 );
									 newn = i;
								 }
							 }
						 }		 
					 }
					 n = newn;
				 }
			 }
			 
		 }
		
	}
	protected void swap(Map<ABTrackingObject, ABTrackingObject> iniToNewMatch, Map<ABTrackingObject, ABTrackingObject> NewToIniMatch, ABTrackingObject o1, ABTrackingObject o2, ABTrackingObject newO1, ABTrackingObject newO2)
	{
		iniToNewMatch.remove(o1);
		iniToNewMatch.remove(o2);
		
		NewToIniMatch.remove(newO1);
		NewToIniMatch.remove(newO2);
		
		iniToNewMatch.put(o1, newO2);
		iniToNewMatch.put(o2, newO1);
		
		NewToIniMatch.put(newO1, o2);
		NewToIniMatch.put(newO2, o1);
	}
	/**
	 * @param newToIniMatch: newObj -> iniObj
	 * @param iniToNewMatch: iniObj -> newObj
	 * 
	 * */
	protected void getNextMatchSet
	(List<ABTrackingObject> iniObjs, List<ABTrackingObject> newObjs, Map<ABTrackingObject, ABTrackingObject> newToIniMatch, Map<ABTrackingObject, ABTrackingObject> iniToNewMatch)
	{
		HashMap<ABTrackingObject, ABTrackingObject> current = new HashMap<ABTrackingObject, ABTrackingObject>();
		
		LinkedList<ABTrackingObject> freeObjs = new LinkedList<ABTrackingObject>();
		
		freeObjs.addAll(newObjs);
		
		HashMap<ABTrackingObject, Integer> next = new HashMap<ABTrackingObject, Integer>(); //prefs recorder
		
		//initialize the current map
		for(ABTrackingObject obj : iniObjs) current.put(obj, null);
		//initialize prefs recorder
		for(ABTrackingObject obj: freeObjs) next.put(obj, 0);
		
		
		while(!freeObjs.isEmpty())
		{
			ABTrackingObject freeObj = freeObjs.remove();
			int index = next.get(freeObj);

			List<Pair> pairs =  prefs.get(freeObj);
			if (pairs == null || index == pairs.size())
				unmatchedNewObjs.add(freeObj);
			else 
			{
				Pair pair = pairs.get(index);
				ABTrackingObject iniObj = pair.obj;
				next.put(freeObj, ++index);
				if(pair.sameShape && !iniObj.isDebris)
				{
					if (current.get(iniObj) == null)
						current.put(iniObj, freeObj);
					else 
					{
						ABTrackingObject rival = current.get(iniObj);
						if (prefers(iniObj, freeObj, rival, iniPrefs)) 
						{
							current.put(iniObj, freeObj);
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
		
		unmatchedIniObjs = new LinkedList<ABTrackingObject>();
		
		Map<ABTrackingObject,DebrisGroup> iniMatchedToDebris = new HashMap<ABTrackingObject, DebrisGroup>();
		for (ABTrackingObject obj : iniObjs)
		{
			ABTrackingObject newObj = current.get(obj);
			
			if(newObj != null)
			{
				if (newObj instanceof DebrisGroup)
				{
					iniMatchedToDebris.put(obj, (DebrisGroup)newObj);
				}
				else
				{
					iniToNewMatch.put(obj, newObj);
					newToIniMatch.put(newObj, obj);
				}
			} 
			else
				unmatchedIniObjs.add(obj);
		}
		//printNewToIniMatch(newToIniMatch);
		for (ABTrackingObject obj : iniMatchedToDebris.keySet())
		{
			 DebrisGroup newDebris = iniMatchedToDebris.get(obj);
			 ABTrackingObject member1 = newDebris.member1;
			 ABTrackingObject member2 = newDebris.member2;
			 
			 ABTrackingObject iniMember1 = newToIniMatch.get(member1);
			 if (iniMember1 != null && !iniMember1.isDebris)
			 {
				 ABTrackingObject iniMember2 = newToIniMatch.get(member2);
				 if (iniMember2 != null && !iniMember2.isDebris)
				 {
					 unmatchedIniObjs.add(obj);
					 continue;
				 }
			 }
			 iniToNewMatch.put(obj, newDebris);
			 newToIniMatch.put(newDebris, obj);
			
		}
	
		
	}
	

	
	
	@Override
	public void debrisRecognition(List<ABTrackingObject> unmatchedNewObjs, List<ABTrackingObject> unmatchedIniObjs) {

	   log(" Debris Recognition ");
		for (ABTrackingObject iniObj : unmatchedIniObjs)
			log("@@@" + iniObj);
		
		currentOccludedObjs.addAll(unmatchedIniObjs);
		
		for (ABTrackingObject newObj : unmatchedNewObjs) 
		{
			
			List<Pair> pairs = prefs.get(newObj);
			Pair pair = null;
			int pointer = 0;
			while (pairs != null && !pairs.isEmpty()&& pointer < pairs.size() && newObj.type != ABType.Pig)
			{	
				pair = pairs.get(pointer);
				//assuming circles in the initial frame will never turn to rect //TODO robust damage components detection
				if(unmatchedIniObjs.contains(pair.obj))
				{	
					break;
				}
				else
					pointer++;
			}
			newObj.id = ABTrackingObject.unassigned;
			 //log(" unmatched new object: " + newObj);

			if (pair != null)
			{
				//TODO non necessary loop
				for (ABTrackingObject iniObj : unmatchedIniObjs) {
					
				/*	log("--------\n" + newObj.toString() + "  \n" + iniObj.toString() + " \n " 
					+ !ShapeToolkit.cannotBeDebris(newObj, iniObj, matchedObjs));*/
					if (pair.obj.equals(iniObj) && pair.diff < MagicParams.DiffTolerance
							&& !ShapeToolkit.cannotBeDebris(newObj, iniObj)
						) {
					
						
						link(newObj, iniObj, true);
						//log("$$$");
						matchedObjs.put(newObj, iniObj);
						debrisList.add(newObj);
						currentOccludedObjs.remove(iniObj);
						
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
			ABTrackingObject iniObj = matchedObjs.get(debris);
			if( iniObj instanceof TrackingRect )//&& debris instanceof Rect)
			{
				TrackingRect _iniObj = (TrackingRect)iniObj;
				//Rect _debris = (Rect)debris;
				for (ABTrackingObject newObj : unmatchedNewObjs)
				{
					if(debris!= newObj && newObj.type != ABType.Pig)
					{
						
						if (_iniObj.isDebris)
						{
							//_initialObj = (Rect)_initialObj.getOriginalShape();
							
							for(DebrisGroup group : debrisGroupList)
							{
								if (group.member1 == debris || group.member2 == debris)
								{	
									_iniObj = group;
									break;
								}
							}
							//System.out.println(" Original Shape: " + _initialObj);
							
						}
						/*if(_initialObj.id == 4)
						{
							System.out.println(" debris " + debris);
							System.out.println(" initial " + _initialObj + " newobj " + newObj);
							System.out.println(DebrisToolKit.isSameDebris(debris, _initialObj, newObj));
						}*/
						if(DebrisToolkit.isSameDebris(debris, _iniObj, newObj))
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
							currentOccludedObjs.remove(iniObj);
							matchedObjs.put(newObj, iniObj);
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
	public boolean matchObjs(List<ABTrackingObject> newObjs) {
		

		matchedObjs = new HashMap<ABTrackingObject, ABTrackingObject>();
		
		currentOccludedObjs = new LinkedList<ABTrackingObject>();

		
		if (iniObjs != null ) 
		{
			lastInitialObjs = iniObjs;
			createPrefs(newObjs);
			//printPrefs(prefs);
			Map<ABTrackingObject, ABTrackingObject> newToIniMatch;
			
			unmatchedNewObjs = new LinkedList<ABTrackingObject>();
			
			List<ABTrackingObject> membersOfMatchedDebrisGroup = new LinkedList<ABTrackingObject>();
			
			newToIniMatch = matchObjs(iniObjs, newObjs);
			
			//Assign Id
			for (ABTrackingObject obj: newToIniMatch.keySet())
			{
				if (matchedObjs.containsKey(obj))
					continue;
				else
				{
					ABTrackingObject iniObj = newToIniMatch.get(obj);
					if (iniObj != null)
					{
						
						link(obj, iniObj, iniObj.isDebris);
						matchedObjs.put(obj, iniObj);
						if (obj.isDebris)
							debrisList.add(obj);
						
						if(obj instanceof DebrisGroup)
						{
							DebrisGroup debris = (DebrisGroup)obj;
							ABTrackingObject member1 = debris.member1;
							ABTrackingObject member2 = debris.member2;

							unmatchedNewObjs.remove(member1);
							unmatchedNewObjs.remove(member2);
					
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
				}
			}
			
			//printMatch(matchedObjs, true);
			//Remove false debris group. False Debris: new objs debris which is not matched, by its members are matched
			List<ABTrackingObject> falseDebris = new LinkedList<ABTrackingObject>();
			for (ABTrackingObject newObj : unmatchedNewObjs)
			{
				if (newObj instanceof DebrisGroup)
				{
					DebrisGroup debris = (DebrisGroup) newObj;
					if (matchedObjs.get(debris.member1) != null || matchedObjs.get(debris.member2) != null)
						falseDebris.add(debris);
				}
			}
			newObjs.removeAll(falseDebris);
			
			
			//printMatch(matchedObjs, true);
			
			unmatchedIniObjs.removeAll(membersOfMatchedDebrisGroup);
			
			debrisRecognition(unmatchedNewObjs, unmatchedIniObjs);
			
			iniObjsMovement.clear();

			log("Print Occluded Objects");
			for (ABTrackingObject occludedObj : currentOccludedObjs)
				System.out.println(occludedObj);
			
			//GlobalObjectsToolkit.addOccludedObjs(currentOccludedObjs);
			GlobalObjectsToolkit.updateOccludedObjs(matchedObjs);
			// ========== Match the remaining objs from prevoius occluded objs ==============
			unmatchedNewObjs.removeAll(matchedObjs.keySet());
			for (ABTrackingObject obj : unmatchedNewObjs)
			{
				ABTrackingObject _obj = GlobalObjectsToolkit.getPossibleOccludedMatch(obj);
				if(_obj != null)
				{
					link(obj, _obj, true);
					matchedObjs.put(obj, _obj);
				}
			}
			// ========== End Match =========================================================
			//printMatch(matchedObjs, true);
			
			
			
			// only retain those which have been matched;
			newObjs.retainAll(matchedObjs.keySet());
			
			//printNewToIniMatch(matchedObjs);
			
			// remove duplicates: If in the Initial scenario, we have Debris_A1, Debirs_A2, Debris_A3, and Debirs_A1 and A2 forms a dummy rectangle A12, in the resulting scenario, 
			//we have Debris_B1, Debris_B2, and Debris_B2 == Debris_A3, however, since Debris_A3 is a debris will not be matched first. So Debris_B2 will be matched to A12
			// and Debris_A3 will be treated as occlude obj and added to the next initial objs, which creates a duplicate.
			
			for (ABTrackingObject newObj : currentOccludedObjs)
			{
				if (newObjs.contains(newObj))
				{
					continue;
				}
				newObjs.add(newObj);
			}
			
			//newObjs.addAll(currentOccludedObjs);
			
			// remove all the matched objs from occludedObjsBuffer (the pre of the pre frame may contain an occluded obj that is matched now)
			occludedObjsBuffer.removeAll(matchedObjs.keySet());
			
			
			newObjs.removeAll(occludedObjsBuffer); // remove all the remembered occluded objects from the previous frame. We only buffer one frame.
			occludedObjsBuffer.addAll(currentOccludedObjs);
			
			//Remove unused Debris Group
			/*for (DebrisGroup group : debrisGroupList)
			{
				if(!group.member1.isDebris || !group.member2.isDebris )
					objs.remove(group);
			}*/
			
			
			
			
			//Set Initial Objs Movements
			iniObjsMovement.clear();
			
			//new edit remove on unmatched new objs. since they all have -1 id, which we do not want to use.
			
			for (ABTrackingObject obj : matchedObjs.keySet())
			{
				ABTrackingObject initial = matchedObjs.get(obj);
				
				if(initial != null){
					/*if(initial.id == -1)
						log("Error Detected " + initial);*/
					
					Movement movement = new Movement(obj);
					movement.generateInertia(initial);
					iniObjsMovement.put(obj, movement);
					
				}
				
			}
			
			
			
			
			//isomorphismProcess(iniGRNetwork, newGRNetwork, objs);
			
			this.setIniObjs(newObjs);
			
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



	public static void main(String args[])
	{
		//String filename = "speedTest_48";
		String filename = "t12";//"t11";//"e1l7_54";//"e1l18_55";// "t11";//"t11";//"e2l3_65";//;
		int timegap = 200;
		if(filename.contains("_"))
			timegap = Integer.parseInt(filename.substring(filename.indexOf("_") + 1));
		Tracker tracker = new KnowledgeTrackerBaseLine_7(timegap);
		TrackingFrameComparison tfc = new TrackingFrameComparison(filename, tracker);// t3,t9,t5,t13 Fixed: t11, t12, t6, t14, t15[not]
		TrackingFrameComparison.continuous = true;
		tfc.run();
	}
}
