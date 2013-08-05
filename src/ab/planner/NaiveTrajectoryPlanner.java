package ab.planner;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

public class NaiveTrajectoryPlanner extends TrajectoryPlanner {

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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
