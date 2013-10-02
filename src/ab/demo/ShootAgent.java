package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.Vision;

public class ShootAgent {
    
	
	public static void shoot(String[] args, boolean cshoot)
	{
		ActionRobot ar = new ActionRobot();
		TrajectoryPlanner tp = new TrajectoryPlanner();
		ar.fullyZoom();
		Vision vision = new Vision(ActionRobot.doScreenShot());
		Rectangle slingshot = vision.findSlingshot();
		while(slingshot == null)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("no slingshot detected. Please remove pop up or zoom out");
			vision = new Vision(ActionRobot.doScreenShot());
			slingshot = vision.findSlingshot();
		}
		Point refPoint = tp.getReferencePoint(slingshot);
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int tap = 0;
		if(args.length > 3)
			tap = Integer.parseInt(args[3]);
		
		Shot shot = null;
		if(cshoot)
			shot = new Shot( refPoint.x, refPoint.y, -x, y,0,tap);
		else
		{
			int r = x;
			double theta = y / 100;
			int dx = -(int) (r * Math.cos(Math.toRadians(theta)));
			int dy = (int) (r * Math.sin(Math.toRadians(theta)));
			shot = new Shot( refPoint.x, refPoint.y, dx, dy,0,tap);
		}
		vision = new Vision(ActionRobot.doScreenShot());
		Rectangle _slingshot = vision.findSlingshot();
		if(!slingshot.equals(_slingshot))
			System.out.println("the scale is changed, the shot might not be executed properly.");
		ar.cshoot(shot);
		System.exit(0);
	}
	


}