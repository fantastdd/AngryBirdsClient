package ab.planner;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import ab.demo.other.ActionRobot;
import ab.demo.other.NaiveMind;
import ab.demo.other.Shot;
import ab.vision.Vision;

public class NaiveTrajectoryPlanner extends TrajectoryPlanner {

	private Point prevTarget = null;
	private boolean firstShot = true;
	public Shot shot = null;
	private BufferedImage plot = null;

	public int getYellowBirdTapTime(Rectangle sling, Point release, Point target)
	{
		Point tapPoint = new Point();
		int distance = target.x - sling.x;
		
		Random randomGenerator = new Random();
		double r = (double)((randomGenerator.nextInt(25) + 65)) / 100;
		System.out.println(" tap at " + r + " of the distance");
		tapPoint.setLocation(new Point((int)(distance * r + sling.x) , target.y));
		return getTimeByDistance(sling, release, tapPoint);
		
	}
	public int getBlueBirdTapTime(Rectangle sling, Point release, Point target)
	{
		Point tapPoint = new Point();
		int distance = target.x - sling.x;
		
		Random randomGenerator = new Random();
		double r = (double)((randomGenerator.nextInt(20) + 65)) / 100;
		System.out.println(" tap at " + r + " of the distance");
		tapPoint.setLocation(new Point((int)(distance * r + sling.x) , target.y));
		return getTimeByDistance(sling, release, tapPoint);
	}
	public int getWhiteBirdTapTime(Rectangle sling, Point release, Point target)
	{
		Point tapPoint = new Point();
		int distance = target.x - sling.x;
		
		Random randomGenerator = new Random();
		double r = (double)((randomGenerator.nextInt(20) + 70)) / 100;
		System.out.println(" tap at " + r + " of the distance");
		tapPoint.setLocation(new Point((int)(distance * r + sling.x) , target.y));
		return getTimeByDistance(sling, release, tapPoint);
	}
	
	public Shot getShot(Point _tpt)
	{
		Point releasePoint;
		BufferedImage screenshot = ActionRobot.doScreenShot();
		Vision vision = new Vision(screenshot);
		Rectangle sling = vision.findSlingshotMBR();
		while(sling == null)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("no slingshot detected. Please remove pop up or zoom out");
			screenshot = ActionRobot.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();
		}
		// if the target is very close to before, randomly choose a
		// point near it
		
		int bird_type = NaiveMind.getBirdOnSlingShot(screenshot);
		Random r = new Random();
		
		if (prevTarget != null && distance(prevTarget, _tpt) < 10) {
			double _angle = r.nextDouble() * Math.PI * 2;
			_tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
			_tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
			System.out.println("Randomly changing to " + _tpt);
		}

		prevTarget = new Point(_tpt.x, _tpt.y);

		// estimate the trajectory
		ArrayList<Point> pts = estimateLaunchPoint(sling, _tpt);

		// do a high shot when entering a level to find an accurate
		// velocity
		if (firstShot && pts.size() > 1) {
			releasePoint = pts.get(1);
		} else if (pts.size() == 1)
			releasePoint = pts.get(0);
		else {
		
			// randomly choose between the trajectories, with a 1 in
			// 6 chance of choosing the high one
		
			if (r.nextInt(6) == 0)
				releasePoint = pts.get(1);
			else
				releasePoint = pts.get(0);
		}
		Point refPoint = getReferencePoint(sling);
	

		System.out.println("the release point is: " + releasePoint);

		//Calculate the tapping time
		if (releasePoint != null) {
			double releaseAngle = getReleaseAngle(sling,
					releasePoint);

			System.out.println(" The release angle is : "
					+ Math.toDegrees(releaseAngle));

			int tap_time = 0;

			switch (bird_type) {
			case NaiveMind.black_bird: {
				System.out.println(" Bird Type: Black");
				tap_time = 0;
				break;
			}
			case NaiveMind.yellow_bird: {
				System.out.println(" Bird Type: Yellow");
				tap_time = getYellowBirdTapTime(sling,
						releasePoint, _tpt);
				break;
			}
			case NaiveMind.blue_bird: {
				System.out.println(" Bird Type: Blue");
				tap_time = getBlueBirdTapTime(sling,
						releasePoint, _tpt);
				break;
			}
			case NaiveMind.white_bird: {
				System.out.println(" Bird Type: White");
				tap_time = getWhiteBirdTapTime(sling,
						releasePoint, _tpt);
				break;
			}
			default: {
				System.out.println(" Bird Type: Red");
				tap_time = getYellowBirdTapTime(sling,
						releasePoint, _tpt);
				break;
			}
			}
			
			
			shot = new Shot( refPoint.x, refPoint.y, 
					(int) releasePoint
					.getX() - refPoint.x, (int) releasePoint.getY()
					-  refPoint.y,0,tap_time);
			
			plot = this.plotTrajectory(screenshot, sling, releasePoint);
			
		}
		return shot;
	}
	
	
		private double distance(Point p1, Point p2) {
			return Math
					.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
							* (p1.y - p2.y)));
		}
		public BufferedImage plotTrajectory() {
			
			return plot;
		}

	


}
