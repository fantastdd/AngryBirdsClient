package ab.objtracking;

import java.util.List;
import java.util.Map;

import ab.vision.ABObject;

public interface Tracker {
	
	public void setIniObjs(List<ABObject> objs);
	/**
	 * @param objs: a list of ABObjects
	 * @return true: matching has been performed.
	 * match the objs to the initial objects by setting their IDs;
	 * */
	public boolean matchObjs(List<ABObject> objs);
	public List<ABObject> getMatchedObjects();
	public List<ABObject> getInitialObjects();
	public Map<ABObject, ABObject> getLastMatch();
	public Map<Integer, Integer> getMatchedId();
	public void startTracking(List<ABObject> iniObjs);
	public boolean isTrackingStart();
	
	public void setTimeGap(int timegap);

}
