package ab.objtracking;

import java.util.List;
import java.util.Map;

import ab.vision.ABTrackingObject;

public interface Tracker {
	
	public void setIniObjs(List<ABTrackingObject> objs);
	/**
	 * @param objs: a list of ABObjects
	 * @return true: matching has been performed.
	 * match the objs to the initial objects by setting their IDs;
	 * */
	public boolean matchObjs(List<ABTrackingObject> objs);
	public List<ABTrackingObject> getMatchedObjects();
	public List<ABTrackingObject> getInitialObjects();
	public Map<ABTrackingObject, ABTrackingObject> getLastMatch();
	public Map<Integer, Integer> getMatchedId();
	public void startTracking(List<ABTrackingObject> iniObjs);
	public boolean isTrackingStart();
	
	public void setTimeGap(int timegap);

}
