package ab.objtracking.tracker;

import java.awt.Point;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

import ab.objtracking.Tracker;
import ab.vision.ABObject;
/**
 * Matching objs using SM algorithm
 * */
public class BasicTracker implements Tracker {
	List<ABObject> initialObjs = null;
	boolean startTracking = false;
	
	public void createPrefs(List<ABObject> objs)
	{
	}
	@Override
	public boolean isTrackingStart()
	{
		return startTracking;
	}
	@Override
	public void startTracking()
	{
		startTracking = true;
	}
	private double calDiff(ABObject o1, ABObject o2)
	{
		Point center1 = o1.getCenter();
		Point center2 = o2.getCenter();
		double diff = 
				(center1.getX() - center2.getX()) * (center1.getX() - center2.getX())
				+
				(center1.getY() - center2.getY() ) * (center1.getY() - center2.getY()); 
		return diff;
	}
	@Override
	public void setInitialObjects(List<ABObject> objs) {
		
		initialObjs = objs;
		//System.out.println(" initial objs: " + initialObjs.size());
		
	}

	@Override
	public void matchObjs(List<ABObject> objs) {
	/*	if(initialObjs != null)
			System.out.println(initialObjs.size());*/
		//Do match
		if(initialObjs != null){
			//freeObjs are the objects in the initial scenario. |freeObjs| > |objs|
			LinkedList<ABObject> freeObjs = new LinkedList<ABObject>();
			freeObjs.addAll(initialObjs);
			//System.out.println(initialObjs.size() + "  " + freeObjs.size());
			for(ABObject obj: objs)
			{
				double diff = Double.MAX_VALUE;
				ABObject prefObj = null;
				//System.out.println(obj + "  " + freeObjs.size());
				int count = freeObjs.size();
				while(count > 0)
				{
					ABObject test_obj = freeObjs.remove();
					double _diff = calDiff(obj, test_obj);
				/*	if(test_obj.id == 8)
						System.out.println(obj + " new id: " + obj.id +  "  original id: " + test_obj.id + "  " + test_obj + "  " + _diff);*/
					if(_diff < diff)
					{	
						diff = _diff;
						if(prefObj != null)
							freeObjs.add(prefObj);
						prefObj = test_obj;
					}
					else
						freeObjs.add(test_obj);
					count --;
				}
				// signal a match by assigning the same id to the obj
				if(prefObj != null)
					obj.id = prefObj.id;
			}
			
		}
	}


	@Override
	public boolean isMatch(Shape a, Shape b) {
		
		return false;
	}


}

