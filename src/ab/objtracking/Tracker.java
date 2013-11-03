package ab.objtracking;

import java.awt.Shape;
import java.util.List;

import ab.vision.ABObject;

public interface Tracker {
	public boolean isMatch(Shape a, Shape b);
	public void setInitialObjects(List<ABObject> objs);
	/**
	 * @param objs: a list of ABObjects
	 * match the objs to the initial objects by setting their IDs;
	 * */
	public void matchObjs(List<ABObject> objs);

}
