package ab.objtracking.tracker;

import java.awt.Point;
import java.awt.Shape;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ab.objtracking.Tracker;
import ab.vision.ABObject;

public class TrackerTemplate implements Tracker{

	List<ABObject> initialObjs = null;
	List<ABObject> lastInitialObjs = null;
	Map<ABObject, List<Pair>> prefs;
	Map<ABObject, List<Pair>> iniPrefs;
	List<ABObject> unmatchedLessObjs;
	List<ABObject> unmatchedMoreObjs;
	List<ABObject> matchedObjs;
	boolean startTracking = false;

	private static void log(String message) {
		System.out.println(message);
	}

	/**
	 * @param: objects in the next frame create preferences based on the mass
	 *         center shift
	 * */
	public void createPrefs(List<ABObject> objs) {
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
		matchedObjs = objs;
		//printPrefs(iniPrefs);
	
	}

	/**
	 * @param iniObj
	 *            : the object in the initial scenario
	 * @param oldObject
	 *            : the last matched object in the next frame
	 * @param rivalObject
	 *            : a new coming object
	 * @return true: if iniObj prefers oldObject over rivalObject¡£ Compare the
	 *         mass center
	 * */
	public boolean prefers(ABObject oldObj, ABObject lastMatchedObject, ABObject rivalObject, Map<ABObject, List<Pair>> prefs) {
		for (Pair pair : prefs.get(oldObj)) {
			if (pair.obj.equals(lastMatchedObject))
				return true;
			if (pair.obj.equals(rivalObject))
				return false;
		}
		System.out.println("Error in prefers ");
		return false;
	}

	@Override
	public boolean isTrackingStart() {
		return startTracking;
	}

	@Override
	public void startTracking() {
		startTracking = true;
		//reset 
		initialObjs = null;
		lastInitialObjs = null;
		matchedObjs = null;
	}

	private float calDiff(ABObject o1, ABObject o2) {
	
		Point center1 = o1.getCenter();
		Point center2 = o2.getCenter();
	;
		double diff = (center1.getX() - center2.getX())
				* (center1.getX() - center2.getX())
				+ (center1.getY() - center2.getY())
				* (center1.getY() - center2.getY());
		/*if(o2.id == 14 && o1.id == 18)
			System.out.println(center1.getX() + "    " + center1.getY() + "  " + 
								center2.getX() + "  " + center2.getY() + "  " + diff + "  " + (float)diff);*/
		return (float) diff;
	
	}

	@Override
	public void setInitialObjects(List<ABObject> objs) {
	
		initialObjs = objs;
	
	}

	private HashMap<ABObject, ABObject> matchObjs(List<ABObject> moreObjs, List<ABObject> lessObjs, Map<ABObject, List<Pair>> morePrefs, Map<ABObject, List<Pair>> lessPrefs) {
	
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
			if (index == pairs.size())
				unmatchedLessObjs.add(freeObj);
			else {
					Pair pair = pairs.get(index);
					ABObject moreObj = pair.obj;
					next.put(freeObj, ++index);
					if(pair.sameShape)
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
	public boolean matchObjs(List<ABObject> objs) {
		/*
		 * if(initialObjs != null) System.out.println(initialObjs.size());
		 */
		// System.out.println(objs.size());
		// Do match, assuming initialObjs.size() > objs.size(): no objects will
		// be created
		if (initialObjs != null) 
		{
	
			lastInitialObjs = initialObjs;
	
			boolean lessIni = (objs.size() > initialObjs.size()); // If the num
																	// of
																	// initial
																	// objects >
																	// next
																	// frame obj
			// log(" " + initialObjs.size() + "  " + objs.size());
			createPrefs(objs);
			//printPrefs(prefs);
			Map<ABObject, ABObject> match;
			unmatchedMoreObjs = new LinkedList<ABObject>();
			if (!lessIni) {
				match = matchObjs(initialObjs, objs, iniPrefs, prefs);
	
				// Assign Id
				for (ABObject iniObj : match.keySet()) {
					ABObject obj = match.get(iniObj);
					if (obj != null)
						obj.id = iniObj.id;
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
						obj.id = iniObj.id;
					else
						unmatchedMoreObjs.add(obj);
				}
				// Process unassigned objs
				// log("debris recognition WAS performed");
				debrisRecognition(unmatchedMoreObjs, unmatchedLessObjs);
	
			}
	
			this.setInitialObjects(objs);
			//printPrefs(prefs);
			return true;
		}
		return false;
	}

	public TrackerTemplate() {
		super();
	}

	public void debrisRecognition(List<ABObject> newObjs, List<ABObject> initialObjs) {
		for (ABObject newObj : newObjs) {
			// log(" unmatched new object: " + newObj);
			List<Pair> pairs = prefs.get(newObj);
			Pair pair = null;
			int pointer = 0;
			while (!pairs.isEmpty()&& pointer < pairs.size())
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
						break;
						// log(" matched initial object: " + initialObjs);
					}
	
				}
			}
		}
	}

	@Override
	public boolean isMatch(Shape a, Shape b) {
	
		return false;
	}

	@Override
	public List<ABObject> getMatchedObjects() {
	
		return matchedObjs;
	}

	@Override
	public List<ABObject> getInitialObjects() {
		// TODO Auto-generated method stub
		return lastInitialObjs;
	}

	public void printPrefs(Map<ABObject, List<Pair>> prefs) {
		
		for (ABObject ao : prefs.keySet()) {
			System.out.println(ao);
			List<Pair> pairs = prefs.get(ao);
			for (Pair pair : pairs) {
				System.out.println(pair);
			}
			System.out.println("----------------------");
		}
		System.out.println("============ Prefs End ============");
	
	}
	class PairComparator implements Comparator<Pair> {
		@Override
		public int compare(Pair o1, Pair o2) {
			return ((Float) o1.diff).compareTo((Float) o2.diff);
		}
	}

	class Pair {
		public ABObject obj;
		public float diff;
		public boolean sameShape;
	
		public Pair(ABObject obj, float diff, boolean sameShape) {
			super();
			this.obj = obj;
			this.diff = diff;
			this.sameShape = sameShape;
		}

		@Override
		public String toString() {
			return " comparedObj: " + obj + "  diff: " + diff + " isSameShape "
					+ sameShape;
		}

	}

}