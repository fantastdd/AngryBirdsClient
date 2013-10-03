package ab.demo.other;

import java.awt.Point;
import java.awt.image.BufferedImage;

import ab.planner.NaiveTrajectoryPlanner;
import ab.utils.ImageSegFrame;
import ab.vision.Vision;
import ab.vision.VisionUtils;

public class ShootingManager {
	
	private NaiveTrajectoryPlanner tp = null;
	private ActionRobot aRobot = null;
/*	public void selectTrajectory(Point target)
	{
		Shot shot = null;
		//initialize the trajectory planner
		if (tp == null)
			tp = new NaiveTrajectoryPlanner();
		if(target != null)
		{
			
			shot = tp.getShot(target);
			//Plot Trajectory
			BufferedImage plot = tp.plotTrajectory();
			int[][] meta = VisionUtils.computeMetaInformation(plot);
			//plot = VisionUtils.analyseScreenShot(plot);
			//segFrame = new ShowImageSegmentation("Plotting Trajectory",plot, meta);
			// RefreshSegmentationThread rst = new  RefreshSegmentationThread(segFrame);
			// rst.start();
			segFrame.refresh(plot, meta);
			actionRobot.cshoot(shot);		
			
		}
		else
			System.out.println(" You need a target before shooting");
	}*/
	 
	public void makeShot()
	{
		 aRobot = new ActionRobot();
		BufferedImage screenshot = ActionRobot.doScreenShot();
		Vision vision = new Vision(screenshot);
		Point target = NaiveMind.getTarget(vision);
		NaiveTrajectoryPlanner tp = new NaiveTrajectoryPlanner();
		Shot shot = tp.getShot(target);
		int[][] meta = VisionUtils.computeMetaInformation(screenshot);
		screenshot = VisionUtils.analyseScreenShot(screenshot);
		 ImageSegFrame segFrame = new ImageSegFrame("Vision Process: Scenario Recognition", screenshot, meta);
		 BufferedImage plot = tp.plotTrajectory();
		 meta = VisionUtils.computeMetaInformation(plot);
		
		 segFrame.refresh(plot, meta);
		 aRobot.cshoot(shot);
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
