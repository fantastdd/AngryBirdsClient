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

/**
 * Assuming the resulting scenarios always have less objects than the initial
 * */
public class SMTracker implements Tracker {
	List<ABObject> initialObjs = null;
	Map<ABObject, List<Pair>> prefs;
	Map<ABObject, List<Pair>> iniPrefs;
	boolean startTracking = false;

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
				diffs.add(new Pair(iniObj, calDiff(obj, iniObj)));
				if (!iniPrefs.containsKey(iniObj)) {
					List<Pair> iniDiffs = new LinkedList<Pair>();
					iniDiffs.add(new Pair(obj, calDiff(obj, iniObj)));
					iniPrefs.put(iniObj, iniDiffs);
				} else {
					iniPrefs.get(iniObj).add(
							new Pair(obj, calDiff(obj, iniObj)));
				}
			}
			Collections.sort(diffs, new PairComparator());
			prefs.put(obj, diffs);
		}
		for (ABObject iniObj : iniPrefs.keySet()) {
			Collections.sort(iniPrefs.get(iniObj), new PairComparator());
		}

	}

	/**
	 * @param iniObj
	 *            : the object in the initial scenario
	 * @param oldObject
	 *            : the last matched object in the next frame
	 * @param rivalObject
	 *            : a new coming object
	 * @return true: if iniObj prefers oldObject over rivalObject
	 * */
	public boolean prefers(ABObject iniObj, ABObject oldObject,
			ABObject rivalObject) {
		for (Pair pair : iniPrefs.get(iniObj)) {
			if (pair.obj.equals(oldObject))
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
	}

	private double calDiff(ABObject o1, ABObject o2) {
		Point center1 = o1.getCenter();
		Point center2 = o2.getCenter();
		double diff = (center1.getX() - center2.getX())
				* (center1.getX() - center2.getX())
				+ (center1.getY() - center2.getY())
				* (center1.getY() - center2.getY());
		return diff;
	}

	@Override
	public void setInitialObjects(List<ABObject> objs) {

		initialObjs = objs;
		// System.out.println(" initial objs: " + initialObjs.size());

	}

	private HashMap<ABObject, ABObject> matchObjs(List<ABObject> moreObjs,
			List<ABObject> lessObjs) {
		
		HashMap<ABObject, ABObject> current = new HashMap<ABObject, ABObject>();
		LinkedList<ABObject> freeObjs = new LinkedList<ABObject>();
		freeObjs.addAll(lessObjs);
		// int[] next = new int[current.length];
		HashMap<ABObject, Integer> next = new HashMap<ABObject, Integer>();

		for (ABObject obj : moreObjs)
			current.put(obj, null);

		//System.out.println(" freeObjs size: " + freeObjs.size());
		for (ABObject obj : freeObjs)
		{
			//System.out.println(obj + "  " + obj.hashCode());
			next.put(obj, 0);
		}
		//System.out.println(" key size: " + next.keySet().size());
		// while there are no free objects or all the original objects have been
		// assigned.
		while (!freeObjs.isEmpty()) {
			ABObject obj = freeObjs.remove();
			int index = next.get(obj);
			/*System.out.println(obj + " " + index);
			for (ABObject t : next.keySet())
				System.out.println(next.get(t));*/
			ABObject iniObj = prefs.get(obj).get(index).obj;
			
			next.put(obj, ++index);
			if (current.get(iniObj) == null)
				current.put(iniObj, obj);
			else {
				ABObject rival = current.get(iniObj);
				if (prefers(iniObj, obj, rival)) {
					current.put(iniObj, obj);
					freeObjs.add(rival);
				} else
					freeObjs.add(obj);
			}
		}
		return current;
	}

	@Override

	public boolean matchObjs(List<ABObject> objs) {
		/*
		 * if(initialObjs != null) System.out.println(initialObjs.size());
		 */
		//System.out.println(objs.size());
		// Do match, assuming initialObjs.size() > objs.size(): no objects will be created
		if (initialObjs != null
				&& 
			initialObjs.size() >= objs.size()) {
			
			boolean lessIni = (objs.size() > initialObjs.size()); // If the num
																	// of
																	// initial
																	// objects >
																	// next
																	// frame obj

			createPrefs(objs);
			Map<ABObject,ABObject> match;
			if(!lessIni)
				{
					match = matchObjs(initialObjs, objs);
					// Assign Id
					for (ABObject iniObj : match.keySet()) {
						ABObject obj = match.get(iniObj);
						if(obj != null)
							obj.id = iniObj.id;
					}
					
				}
			else
			{
				//System.out.println(" less ini");
				Map<ABObject, List<Pair>> temp;
				temp = iniPrefs;
				iniPrefs = prefs;
				prefs = temp;
				match = matchObjs(objs, initialObjs);
				// Assign Id
				for (ABObject obj : match.keySet()) {
					ABObject iniObj = match.get(obj);
					if(iniObj != null)
						obj.id = iniObj.id;
				}
				
			}	

			this.setInitialObjects(objs);
			return true;
		}
		return false;
	}

	@Override
	public boolean isMatch(Shape a, Shape b) {

		return false;
	}

	class PairComparator implements Comparator<Pair> {
		@Override
		public int compare(Pair o1, Pair o2) {
			return ((Double) o1.diff).compareTo((Double) o2.diff);
		}
	}

	class Pair {
		public ABObject obj;
		public double diff;

		public Pair(ABObject obj, double diff) {
			super();
			this.obj = obj;
			this.diff = diff;
		}

	}

	@Override
	public List<ABObject> getMatchedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ABObject> getInitialObjects() {
		// TODO Auto-generated method stub
		return null;
	}
}
