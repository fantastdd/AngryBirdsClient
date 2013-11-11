package ab.objtracking;

import java.awt.Shape;
import java.util.List;

import ab.vision.ABList;
import ab.vision.ABObject;

public interface Tracker {
	public boolean isMatch(Shape a, Shape b);
	public void setInitialObjects(List<ABObject> objs);
	/**
	 * @param objs: a list of ABObjects
	 * @return true: matching has been performed.
	 * match the objs to the initial objects by setting their IDs;
	 * */
	public boolean matchObjs(List<ABObject> objs);
	public List<ABObject> getMatchedObjects();
	public List<ABObject> getInitialObjects();
	public void startTracking();
	public boolean isTrackingStart();

}
