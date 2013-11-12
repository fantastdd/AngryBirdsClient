package ab.objtracking.tracker;

import java.awt.Polygon;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ab.objtracking.tracker.TrackerTemplate.Pair;
import ab.objtracking.tracker.TrackerTemplate.PairComparator;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.real.shape.Rect;


/**
 * Detects explosion/debris
 * */
public class SMETracker extends TrackerTemplate {


	/**
	 * If the diff between two ABObjects is still within group
	 * */
	public boolean withinScope() {
		return true;
	}
	
	@Override
	public void createPrefs(List<ABObject> objs) 
	{
		// Alternatively, we can choose PriorityQueue to store objs;
				prefs = new HashMap<ABObject, List<Pair>>();
				iniPrefs = new HashMap<ABObject, List<Pair>>();
			
				for (ABObject obj : objs) {
					List<Pair> diffs = new LinkedList<Pair>();
					for (ABObject iniObj : initialObjs) {
						boolean sameShape = iniObj.isSameShape(obj);
						diffs.add(new Pair(iniObj, calDiff(obj, iniObj), sameShape));
						if (!iniPrefs.containsKey(iniObj)) {
							List<Pair> iniDiffs = new LinkedList<Pair>();
							iniDiffs.add(new Pair(obj, calDiff(obj, iniObj), sameShape));
							iniPrefs.put(iniObj, iniDiffs);
						} else {
							iniPrefs.get(iniObj).add(
									new Pair(obj, calDiff(obj, iniObj), sameShape));
						}
			
					}
					Collections.sort(diffs, new PairComparator());
					prefs.put(obj, diffs);
				}
				for (ABObject iniObj : iniPrefs.keySet()) {
					Collections.sort(iniPrefs.get(iniObj), new PairComparator());
				}
				newComingObjs = objs;
				//printPrefs(iniPrefs);
		printPrefs(prefs);
	}

	@Override
	public void debrisRecognition(List<ABObject> newObjs, List<ABObject> initialObjs) {
		
		List<ABObject> debrisList = new LinkedList<ABObject>();
		
		for (ABObject newObj : newObjs) 
		{
			
			List<Pair> pairs = prefs.get(newObj);
			Pair pair = null;
			int pointer = 0;
			while (!pairs.isEmpty()&& pointer < pairs.size() && newObj.type != ABType.Pig)
			{	
				pair = pairs.get(pointer);
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
			/*if(newObj instanceof Rect &&(((Rect)newObj).centerX == 549.5)) 
			{
				System.out.println(newObj);
				System.out.println("====================");
				for(Pair _pair : pairs) 
					System.out.println(_pair.obj + "  " + _pair.diff);
				System.out.println(pair);
				System.out.println(initialObjs.size());
			}*/
	
			
			if (pair != null)
			{
				//System.out.println(" pair check");
				for (ABObject initialObj : initialObjs) {
					//System.out.println(initialObj);
					//if(pair.obj.id == 2)
					//	System.out.println(pair.obj + "   " + initialObj + "   " + pair.obj.equals(initialObj));
					// pair.diff's threshold can be estimated by frame frequency
					if (pair.obj.equals(initialObj) && pair.diff < 300) {
						// System.out.println(pair.obj + "  " +
						// pair.obj.hashCode() + "   " + initialObj + "  " +
						// initialObj.hashCode() + "   " +
						// pair.obj.equals(initialObj));
						newObj.id = initialObj.id;
						matchedObjs.put(newObj, initialObj);
						debrisList.add(newObj);
						break;
						// log(" matched initial object: " + initialObjs);
					}
	
				}
			}
		}
		// Damage Recognition
		for (ABObject debris: debrisList)
		{
			ABObject initialObj = matchedObjs.get(debris);
			if( initialObj instanceof Rect && debris instanceof Rect)
			{
				Rect _initialObj = (Rect)initialObj;
				Rect _debris = (Rect)debris;
				for (ABObject unmatchedDebris : newObjs)
				{
					if(unmatchedDebris.id == ABObject.unassigned && unmatchedDebris.type != ABType.Pig)
					{
						//System.out.println(" debris " + _debris);
						//System.out.println(" unmatched " + unmatchedDebris);
						//System.out.println(" initial " + _initialObj);
						Rect dummy = _debris.extend(_initialObj.rectType);
						//System.out.println(" dummy " + dummy);
						Polygon p = dummy.p;
						if(p.contains(unmatchedDebris.getCenter()))
						{
							unmatchedDebris.id = _debris.id;
							matchedObjs.put(unmatchedDebris, initialObj);
						}
					}
				}
			}
		}
		
	}

	public static void main(String args[])
	{
		double x = 644;
		double y = 346.5;
		double _x = 636.5;
		double _y = 340;
		float r = (float)(((x - _x)*(x - _x) + (y - _y) * (y - _y)));
		System.out.println(r);
	}
}
