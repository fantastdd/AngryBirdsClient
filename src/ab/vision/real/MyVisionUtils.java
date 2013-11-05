package ab.vision.real;

import java.awt.image.BufferedImage;

import ab.objtracking.DisplayTracking;
import ab.objtracking.Tracker;
import ab.vision.ABList;

public class MyVisionUtils {
	
	public static BufferedImage constructImageSegWithTracking(BufferedImage screenshot, Tracker tracker) {

		// process imaged
		MyVision vision = new MyVision(screenshot);
		
		ABList allInterestObjs = ABList.newList();
		allInterestObjs.addAll(vision.findObjects());
		if(DisplayTracking.askForIniScenario)
		{
			tracker.setInitialObjects(allInterestObjs);
			System.out.println(" Initial objects size: " + allInterestObjs.size());
			DisplayTracking.flipAskForInitialScenario();
			tracker.startTracking();
		}
		else
		{
			if(tracker != null && tracker.isTrackingStart())
			{
				tracker.matchObjs(allInterestObjs);
				tracker.setInitialObjects(allInterestObjs);
				//System.out.println(" match completed");
			}
		}
		// draw objects
		vision.drawObjectsWithID(screenshot, true);
		
	
		return screenshot;
	}
	

}
