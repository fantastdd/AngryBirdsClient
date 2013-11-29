package ab.objtracking.representation.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.real.shape.RectType;

public class GlobalObjectsToolkit {
	//Introduce timing schema later
	private static Map<Integer, LinkedHashSet<ABObject>> wood, stone, ice;
	private static LinkedHashSet<Integer> occludedIds;
	
	public static void registerIniObjs(List<ABObject> iniObjs) {
		System.out.println(" Register ini objs in GlobalObjectsToolkit");
		wood = new HashMap<Integer, LinkedHashSet<ABObject>>();
		stone = new HashMap<Integer, LinkedHashSet<ABObject>>();
		ice = new HashMap<Integer, LinkedHashSet<ABObject>>();
		
	
		
		occludedIds = new LinkedHashSet<Integer>();
		
		for (RectType rectType : RectType.values()) {
			
			wood.put(rectType.id, new LinkedHashSet<ABObject>());
			stone.put(rectType.id, new LinkedHashSet<ABObject>());
			ice.put(rectType.id, new LinkedHashSet<ABObject>());
			
		}
		for (ABObject obj : iniObjs) {
			switch (obj.type) {
			case Wood: {
				wood.get(obj.rectType.id).add(obj);
				break;
			}
			case Stone: {
				stone.get(obj.rectType.id).add(obj);
				break;
			}
			case Ice: {
				ice.get(obj.rectType.id).add(obj);
				break;
			}
			default:
				break;
			}
		}
	}
	public static void updateOccludedObjs(Map<ABObject, ABObject> newToIniMatch)
	{
		for (ABObject newObj :newToIniMatch.keySet())
		{
			occludedIds.remove(newObj.id);
		}
		/*for (Integer occid : occludedIds)
			System.out.println(occid);*/
	}
	public static void addOccludedObjs(List<ABObject> occObjs) {
		for (ABObject obj : occObjs) {
			occludedIds.add(obj.id);
		}
	}
	private static void printIniObjs()
	{
		//Print Wood
		log("================== Print all initial objects ================");
		log("Wood");
		for (Integer rectType : wood.keySet())
		{
			for (ABObject obj : wood.get(rectType))
			{
				log(obj.toString());
			}
		}
		log("Stone");
		for (Integer rectType : stone.keySet())
		{
			for (ABObject obj : stone.get(rectType))
			{
				log(obj.toString());
			}
		}
		log("Ice");
		for (Integer rectType : ice.keySet())
		{
			for (ABObject obj : ice.get(rectType))
			{
				log(obj.toString());
			}
		}
		log("=================== End of Print ========================== \n");
	}
	private static void log(String message){System.out.println(message);}
	// Assign the most recent disappeared objs.
	// unmatched cannot be DebrisGroup?
	public static ABObject getPossibleOccludedMatchByTiming(ABObject unmatched)
	{
		Map<Integer, LinkedHashSet<ABObject>> occObjs;
		
		//printIniObjs();
		
		if (unmatched.type == ABType.Wood)
		{
			occObjs = wood;
		}
		else
			if(unmatched.type == ABType.Stone)
				occObjs = stone;
			else
				if(unmatched.type == ABType.Ice)
					occObjs = ice;
				else
					return null;

		//Search for large-size occluded blocks
		ABObject matchedObj = null;
		for (int i = unmatched.rectType.id; i < RectType.values().length; i ++)
		{
			LinkedHashSet<ABObject> occByRectType = occObjs.get(i);
			for (ABObject obj : occByRectType)
			{
				//System.out.println("@@@ " + obj + "  " + unmatched + "   " + !ShapeToolkit.isDifferentShape(obj, unmatched));
				if(occludedIds.contains(obj.id) && !ShapeToolkit.isDifferentShape(obj, unmatched))
					matchedObj = obj;
				/*if (obj.isSameShape(unmatched))
				{
					
				}*/
			}
			if (matchedObj != null)
			{
				occludedIds.remove(matchedObj.id);
				return matchedObj;
			}
		}
		return null;
	
	}


}
