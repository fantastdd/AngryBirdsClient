package ab.objtracking.tracker;

import java.util.List;

import ab.vision.ABObject;


/**
 * Detects explosion/debris
 * */
public class SMETracker extends TrackerTemplate {


	/**
	 * If the diff between two ABObjects is still within group
	 * */
	public boolean withinScope() {
		return true;
	}
	@Override
	public void createPrefs(List<ABObject> objs) 
	{
		super.createPrefs(objs);
		printPrefs(iniPrefs);
	}

	public static void main(String args[])
	{
		double x = 644;
		double y = 346.5;
		double _x = 636.5;
		double _y = 340;
		float r = (float)(((x - _x)*(x - _x) + (y - _y) * (y - _y)));
		System.out.println(r);
	}
}
