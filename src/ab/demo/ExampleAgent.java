/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2013, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys, Kar-Wai Lim, Zain Mubashir, Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ab.demo.other.ActionRobot;
import ab.demo.other.Env;
import ab.demo.other.NaiveMind;
import ab.demo.other.Shot;
import ab.demo.util.StateUtil;
import ab.planner.NaiveTrajectoryPlanner;
import ab.planner.TrajectoryPlanner;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.ABObject;
import ab.vision.Vision;

public class ExampleAgent implements Runnable {


	private ActionRobot ar;
	public int currentLevel = 1;
	public static int time_limit = 12;
	NaiveTrajectoryPlanner tp;




	// a standalone implementation of the Naive Agent
	public ExampleAgent() {
		ar = new ActionRobot();
		tp = new NaiveTrajectoryPlanner();
		
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}

	public int getCurrent_level() {
		return currentLevel;
	}

	public void setCurrent_level(int current_level) {
		this.currentLevel = current_level;
	}

	// run the client
	public void run() {

		ar.loadLevel(currentLevel);
		while (true) {
		
			GameState state = solve();
			if (state == GameState.WON) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				int score = StateUtil.checkCurrentScoreSafemode(ar.proxy);

				System.out.println(" Level " + currentLevel
						+ " Score: " + score + " ");

				ar.loadLevel(++currentLevel);
				// make a new trajectory planner whenever a new level is entered
				tp = new NaiveTrajectoryPlanner();

				
		
			} else if (state == GameState.LOST) {
				System.out.println("restart");
				ar.restartLevel();
			} else if (state == GameState.LEVEL_SELECTION) {
				System.out
						.println("unexpected level selection page, go to the lasts current level : "
								+ currentLevel);
				ar.loadLevel(currentLevel);
			} else if (state == GameState.MAIN_MENU) {
				System.out
						.println("unexpected main menu page, go to the lasts current level : "
								+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				ar.loadLevel(currentLevel);
			} else if (state == GameState.EPISODE_MENU) {
				System.out
						.println("unexpected episode menu page, go to the lasts current level : "
								+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				ar.loadLevel(currentLevel);
			}

		}

	}


	public GameState solve()

	{
		
		// capture Image
		BufferedImage screenshot = ActionRobot.doScreenShot();

		// process image
		Vision vision = new Vision(screenshot);
		
		// find the slingshot
		Rectangle sling = vision.findSlingshotMBR();

		// confirm the slingshot
		while (sling == null && ar.checkState() == GameState.PLAYING) {
			System.out
					.println("no slingshot detected. Please remove pop up or zoom out");
			ar.fullyZoom();
			screenshot = ActionRobot.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();
		}

		GameState state = ar.checkState();
	
		// if there is a sling, then play, otherwise just skip.
		if (sling != null) 
		{
		   
				Point target = NaiveMind.getTarget(vision);
				if(target != null) {
					Shot shot =  tp.getShot(target);
					// check whether the slingshot is changed. the change of the Slingshot indicates a change in the scale.
					{
						ar.fullyZoom();
						screenshot = ActionRobot.doScreenShot();
						vision = new Vision(screenshot);
						Rectangle _sling = vision.findSlingshotMBR();
						double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
						if (scale_diff < 25) {
							
							//Make the shot
							ar.cshoot(shot);
							state = ar.checkState();
							// update parameters after a shot is made
							if (state == GameState.PLAYING) 
							{
								screenshot = ActionRobot.doScreenShot();
								vision = new Vision(screenshot);
								List<Point> traj = vision.findTrajPoints();
								tp.adjustTrajectory(traj, sling);
								
	
							}
						} else
								System.out.println("scale is changed, can not execute the shot, will re-segement the image");
							
					}
				}
				
		
				
		}

				

	
		return state;
	}

	public static void main(String args[]) {

		ExampleAgent na = new ExampleAgent();
		if (args.length > 0)
			na.currentLevel = Integer.parseInt(args[0]);
		na.run();

	}
}
