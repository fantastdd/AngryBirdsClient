package ab.objtracking.tracker;

import java.awt.Shape;
import java.util.List;

import ab.objtracking.Tracker;
import ab.vision.ABObject;

public class BasicTracker implements Tracker {
	List<ABObject> initialObjs;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}



	@Override
	public void setInitialObjects(List<ABObject> objs) {
		
		initialObjs = objs;
		
	}

	@Override
	public void matchObjs(List<ABObject> objs) {
		
		//Do match
		
		//Set InitialObjs
		
		initialObjs = objs;
		
	}


	@Override
	public boolean isMatch(Shape a, Shape b) {
		
		return false;
	}

}
