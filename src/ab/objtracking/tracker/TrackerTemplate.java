package ab.objtracking.tracker;

import java.awt.Point;
import java.awt.Shape;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ab.objtracking.Tracker;
import ab.objtracking.dynamic.Movement;
import ab.objtracking.representation.util.GlobalObjectsToolkit;
import ab.vision.ABTrackingObject;
import ab.vision.ABType;
import ab.vision.real.shape.TrackingCircle;
import ab.vision.real.shape.TrackingRect;
import ab.vision.real.shape.RectType;

public abstract class TrackerTemplate implements Tracker{

	List<ABTrackingObject> iniObjs = null;
	List<ABTrackingObject> lastInitialObjs = null;
	Map<ABTrackingObject, List<Pair>> prefs;
	Map<ABTrackingObject, List<Pair>> iniPrefs;
	List<ABTrackingObject> unmatchedIniObjs;
	List<ABTrackingObject> unmatchedNewObjs;
	Map<ABTrackingObject, ABTrackingObject> matchedObjs;
	List<ABTrackingObject> newComingObjs;
	Map<Integer, Integer> matchedId = new HashMap<Integer, Integer>();
	long maximum_distance;
	int timegap;
	
	public TrackerTemplate(int timegap)
	{
		this.timegap = timegap;
		maximum_distance = (timegap/3 + 1) * (timegap/3 + 1);
	}
	public void setTimeGap(int timegap)
	{
		this.timegap = timegap;
		maximum_distance = (timegap/3 + 1) * (timegap/3 + 1);
	}
	private boolean startTracking = false;
	protected void log(String message) {
		System.out.println(message);
	}

	/**
	 * @param: objects in the next frame create preferences based on the mass
	 *         center shift
	 * */
	public abstract void createPrefs(List<ABTrackingObject> objs);

	/**
	 * @param targetObj
	 *            : the object 
	 * @param lastObj
	 *            : the last matched object of targetObj
	 * @param rivalObj
	 *            : a new coming object that maybe more suitable for targetObj
	 * @return true: if targetObj prefers lastMatchedObj over rivalObject.
	 *       
	 * */
	public abstract boolean prefers(ABTrackingObject targetObj, ABTrackingObject lastObj, ABTrackingObject rivalObj, Map<ABTrackingObject, List<Pair>> prefs);

	@Override
	public boolean isTrackingStart() {
		return startTracking;
	}

	@Override
	public void startTracking(List<ABTrackingObject> initialObjs) {
		startTracking = true;
		//reset 
		this.iniObjs = initialObjs;
		//System.out.println("@@@@@@@@@");
		GlobalObjectsToolkit.registerIniObjs(initialObjs);
		lastInitialObjs = null;
		newComingObjs = null;
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

	protected float calMassShift(ABTrackingObject o1, ABTrackingObject o2) {
	
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
	/**
	 * Intended for tolerating vision error in detecting wood
	 * Convert all wood circles to 1x1 wood blocks since vision is likely to treat 1x1 wood as circle
	 * */
	protected void preprocessObjs(List<ABTrackingObject> objs)
	{
		List<ABTrackingObject> removal = new LinkedList<ABTrackingObject>();
		List<ABTrackingObject> addedBlocks = new LinkedList<ABTrackingObject>();
		for (ABTrackingObject obj : objs)
		{
			if (obj instanceof TrackingCircle && obj.type == ABType.Wood)
			{
			
				ABTrackingObject newBlock = new TrackingRect(
						obj.getCenterX(), obj.getCenterY(), obj.getBounds().width, 
						obj.getBounds().height, 0, -1, obj.area);
				log(" circle to rec conversion: Circle " + obj);
				log(" circle to rec conversion: Rect " + newBlock);
				 
				newBlock.id = obj.id;
				 newBlock.type = ABType.Wood;
				 addedBlocks.add(newBlock);
				 removal.add(obj);
			}
		}
		objs.removeAll(removal);
		objs.addAll(addedBlocks);
		/*for (ABObject obj : objs)
			log(obj.toString());*/
	}
	@Override
	public void setIniObjs(List<ABTrackingObject> objs) {
	
		iniObjs = objs;
	
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
					if(pair.sameShape && pair.diff < 1000)
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
	public boolean matchObjs(List<ABTrackingObject> objs) {
		/*
		 * if(initialObjs != null) System.out.println(initialObjs.size());
		 */
		// System.out.println(objs.size());
		// Do match, assuming initialObjs.size() > objs.size(): no objects will
		// be created
		matchedObjs = new HashMap<ABTrackingObject, ABTrackingObject>();
		if (iniObjs != null /*&& initialObjs.size() >= objs.size()*/) 
		{
	
			lastInitialObjs = iniObjs;
	
			boolean lessIni = (objs.size() > iniObjs.size()); // If the num
																	// of3.d
																	// initial
																	// objects >
																	// next
																	// frame obj
			// log(" " + initialObjs.size() + "  " + objs.size());
			createPrefs(objs);
			//printPrefs(prefs);
			Map<ABTrackingObject, ABTrackingObject> match;
			unmatchedNewObjs = new LinkedList<ABTrackingObject>();
			if (!lessIni) {
				match = matchObjs(iniObjs, objs, iniPrefs, prefs);
	
				// Assign Id
				for (ABTrackingObject iniObj : match.keySet()) {
					ABTrackingObject obj = match.get(iniObj);
					if (obj != null)
					{
						obj.id = iniObj.id;
						matchedObjs.put(obj, iniObj);
					}
					else
						unmatchedNewObjs.add(iniObj);
				}
	
				// log(" debris recognition WAS performed: more objects in the initial");
				debrisRecognition(unmatchedIniObjs, unmatchedNewObjs);
			} else {
				log(" more objs in next frame");
				/*
				 * Map<ABObject, List<Pair>> temp; temp = iniPrefs; iniPrefs =
				 * prefs; prefs = temp;
				 */
				match = matchObjs(objs, iniObjs, prefs, iniPrefs);
				// Assign Id
				for (ABTrackingObject obj : match.keySet()) {
					ABTrackingObject iniObj = match.get(obj);
					if (iniObj != null)
					{	
						obj.id = iniObj.id; 
						matchedObjs.put(obj, iniObj);
					}
					else
						unmatchedNewObjs.add(obj);
				}
				// Process unassigned objs
				// log("debris recognition WAS performed");
				debrisRecognition(unmatchedNewObjs, unmatchedIniObjs);
	
			}
	
			this.setIniObjs(objs);
			//printPrefs(prefs);
			return true;
		}
		return false;
	}

	public TrackerTemplate() {
		super();
	}

	public abstract void debrisRecognition(List<ABTrackingObject> newObjs, List<ABTrackingObject> initialObjs);


	@Override
	public List<ABTrackingObject> getMatchedObjects() {
	
		return newComingObjs;
	}

	@Override
	public List<ABTrackingObject> getInitialObjects() {
		// TODO Auto-generated method stub
		return lastInitialObjs;
	}

	public void printPrefs(Map<ABTrackingObject, List<Pair>> prefs) {
		
		log("  ====================  Print Prefs =========================\n  ");
		for (ABTrackingObject ao : prefs.keySet()) {
			System.out.println(ao);
			List<Pair> pairs = prefs.get(ao);
			for (Pair pair : pairs) {
				log(pair.toString());
			}
			log("----------------------");
		}
		log("============ Prefs End ============\n");
	
	}
	class PairComparator implements Comparator<Pair> {
		@Override
		public int compare(Pair o1, Pair o2) {
			return ((Float) o1.diff).compareTo((Float) o2.diff);
		}
	}
	/**
	 *@param newObj, iniObj: new object and the matched initial object
	 *@param isDebris: whether the newObj is a piece of iniObj
	 *
	 * Link newObj to iniObj by setting the id equal to iniObj's id, and set the original shape 
	 * of newObj if newObj is debris (otherwise the original shape of newObj is newObj itself)
	 * 
	 * */
	protected void link(ABTrackingObject newObj, ABTrackingObject iniObj, boolean isDebris)
	{
		matchedId.put(newObj.id, iniObj.id);
		newObj.id = iniObj.id;
		if(isDebris && (! (iniObj.getOriginalShape().isSameShape(newObj) || 
		  (newObj.rectType == RectType.rec8x1/* && iniObj.getOriginalShape().rectType == RectType.rec8x1*/))
				))
		{	
			newObj.setOriginalShape(GlobalObjectsToolkit.getIniObjById(iniObj.id));
			newObj.isDebris = true;
		}
		else 
			/*if(isDebris && (iniObj instanceof DebrisGroup))
			{
				newObj.isDebris = true;
			}
			else*/
				newObj.isDebris = false;
	
	} 
	@Override
	public Map<Integer, Integer> getMatchedId()
	{
		return matchedId;
	}
	protected void printMatch(Map<ABTrackingObject, ABTrackingObject> newToIniMatch, boolean newToIni)
	{
		String str1 = "";
		String str2 = "";
		if ( newToIni)
		{	
			str1 = "newObj: ";
			str2 = "iniObj: ";
		}
		else
		{
			str1 = "iniObj: ";
			str2 = "newObj: ";
		}
			
		log(" ===========  Print Match ============= ");
		for (ABTrackingObject newObj : newToIniMatch.keySet())
		{
			System.out.println(str1 + newObj);
			System.out.println(str2 + newToIniMatch.get(newObj));
			System.out.println("==========");
		}
	}
	protected void printMovement(Map<ABTrackingObject, Movement> movements)
	{
		log("\n Print Initial Objects Movements");
		for (ABTrackingObject obj : movements.keySet())
		{
			log(movements.get(obj) + "");
		}
	}
	protected void printMatch(List<ABTrackingObject> interestObj, Map<ABTrackingObject, ABTrackingObject> newToIniMatch, boolean newToIni)
	{
		String str1 = "";
		String str2 = "";
		if ( newToIni)
		{	
			str1 = "newObj: ";
			str2 = "iniObj: ";
		}
		else
		{
			str1 = "iniObj: ";
			str2 = "newObj: ";
		}
			
		log(" ===========  Print Match ============= ");
		for (ABTrackingObject newObj : interestObj)
		{
			System.out.println(str1 + newObj);
			System.out.println(str2 + newToIniMatch.get(newObj));
			System.out.println("==========");
		}
	}
	
	class Pair {
		public ABTrackingObject obj;
		public float diff;
		public boolean sameShape;
	
		public Pair(ABTrackingObject obj, float diff, boolean sameShape) {
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