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

public abstract class TrackerTemplate implements Tracker{

	List<ABObject> initialObjs = null;
	List<ABObject> lastInitialObjs = null;
	Map<ABObject, List<Pair>> prefs;
	Map<ABObject, List<Pair>> iniPrefs;
	List<ABObject> unmatchedLessObjs;
	List<ABObject> unmatchedMoreObjs;
	Map<ABObject, ABObject> matchedObjs;
	List<ABObject> newComingObjs;
	boolean startTracking = false;

	protected void log(String message) {
		System.out.println(message);
	}

	/**
	 * @param: objects in the next frame create preferences based on the mass
	 *         center shift
	 * */
	public abstract void createPrefs(List<ABObject> objs);

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
	public void startTracking(List<ABObject> initialObjs) {
		startTracking = true;
		//reset 
		this.initialObjs = initialObjs;
		lastInitialObjs = null;
		newComingObjs = null;
	}

	protected float calDiff(ABObject o1, ABObject o2) {
	
		Point center1 = o1.getCenter();
		Point center2 = o2.getCenter();
	
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
			else {
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
	public boolean matchObjs(List<ABObject> objs) {
		/*
		 * if(initialObjs != null) System.out.println(initialObjs.size());
		 */
		// System.out.println(objs.size());
		// Do match, assuming initialObjs.size() > objs.size(): no objects will
		// be created
		matchedObjs = new HashMap<ABObject, ABObject>();
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
			if (!lessIni) {
				match = matchObjs(initialObjs, objs, iniPrefs, prefs);
	
				// Assign Id
				for (ABObject iniObj : match.keySet()) {
					ABObject obj = match.get(iniObj);
					if (obj != null)
					{
						obj.id = iniObj.id;
						matchedObjs.put(obj, iniObj);
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
					}
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

	public abstract void debrisRecognition(List<ABObject> newObjs, List<ABObject> initialObjs);

	@Override
	public boolean isMatch(Shape a, Shape b) {
	
		return false;
	}

	@Override
	public List<ABObject> getMatchedObjects() {
	
		return newComingObjs;
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