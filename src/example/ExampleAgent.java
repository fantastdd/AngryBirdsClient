/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2013, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys, Kar-Wai Lim, Zain Mubashir, Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package example;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import example.strategy.Strategy_RandomHitPigs;

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.demo.util.StateUtil;
import ab.planner.ExampleTrajectoryPlanner;
import ab.planner.Strategy;
import ab.vision.ABObject;
import ab.vision.ABUtil;
import ab.vision.GameStateExtractor.GameState;
//An example agent that will loop through 1 - 21 levels. 
public class ExampleAgent implements Runnable {


	private ActionRobot aRobot;
	public int currentLevel = 1;
	
	private ExampleTrajectoryPlanner trajectoryPlanner;
	private Strategy strategy;


	public ExampleAgent() {
		aRobot = new ActionRobot();
		trajectoryPlanner = new ExampleTrajectoryPlanner();
		strategy = new Strategy_RandomHitPigs();
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}
	public ExampleAgent(Strategy strategy)
	{
		aRobot = new ActionRobot();
		trajectoryPlanner = new ExampleTrajectoryPlanner();
		this.strategy = strategy;
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}



	// run the client
	public void run() {

		aRobot.loadLevel(currentLevel);
		while (true) {
		
			GameState state = solve();
			
			if (state == GameState.WON) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				int score = StateUtil.checkCurrentScoreSafemode(ActionRobot.proxy);

				System.out.println(" Level " + currentLevel
						+ " Score: " + score + " ");
				
				aRobot.loadLevel(++currentLevel);
				// make a new trajectory planner whenever a new level is entered
				trajectoryPlanner = new ExampleTrajectoryPlanner();

				
		
			} else if (state == GameState.LOST) {
				System.out.println("restart");
				aRobot.restartLevel();
			} else if (state == GameState.LEVEL_SELECTION) {
				System.out
						.println("unexpected level selection page, go to the lasts current level : "
								+ currentLevel);
				aRobot.loadLevel(currentLevel);
			} else if (state == GameState.MAIN_MENU) {
				System.out
						.println("unexpected main menu page, go to the lasts current level : "
								+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				aRobot.loadLevel(currentLevel);
			} else if (state == GameState.EPISODE_MENU) {
				System.out
						.println("unexpected episode menu page, go to the lasts current level : "
								+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				aRobot.loadLevel(currentLevel);
			}

		}

	}


	public GameState solve()

	{
		
		// get the state of the current game
		State state = ABUtil.getState();
		
		// find the sling
		Rectangle sling = state.findSlingshot();

		// confirm the sling
		while (sling == null && state.getGameState() == GameState.PLAYING) {
			System.out
					.println("no slingshot detected. Please remove pop up or zoom out");
			ActionRobot.fullyZoomOut();
			state = ABUtil.getState();
			sling = state.findSlingshot();
		}
	
		// if there is a sling, then play
		if (sling != null) 
		{
				state = ABUtil.getState();
				Point target = strategy.getTarget(state);
				
				if(target != null) {
			
					Shot shot =  trajectoryPlanner.getShot(state, target, strategy.useHighTrajectory(state), strategy.getTapPoint(state));
			
					// check whether the slingshot is changed. the change of the Slingshot indicates a change in the scale.
					{
						ActionRobot.fullyZoomOut();
						state = ABUtil.getState();
						ABObject _sling = state.findSlingshot();
						if(_sling != null){
						
						double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
						//Check whether a significant scale change happens
						if (scale_diff < 25) {
							
							//execute the shot
							aRobot.cshoot(shot);
							state = ABUtil.getState();
							
							// update parameters after a shot is executed
							if (state.getGameState() == GameState.PLAYING) 
							{			
								List<Point> traj = state.findTrajPoints();
								trajectoryPlanner.adjustTrajectory(traj, sling);
							}
						} 
						else
							System.out.println("scale is changed, can not execute the shot, will re-segement the image");
						}else
							System.out.println("no sling detected, can not execute the shot, will re-segement the image");
						
							
					}
				}
				
		
				
		}
		else
			state = ABUtil.getState();
	
		return state.getGameState();
	}

	public static void main(String args[]) {

		ExampleAgent na = new ExampleAgent();
		if (args.length > 0)
			na.currentLevel = Integer.parseInt(args[0]);
		na.run();

	}
}
