package example.strategy;

import java.awt.Point;

import ab.planner.Strategy;
import ab.vision.ABList;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.State;


public class HitRandomPig extends Strategy {
	ABList pigs;
	ABObject pig;
	@Override
	public Point getTarget(State state) 
	{
		Point target = null;
		pigs = state.findPigs();
		if(!pigs.isEmpty()){
			
			pig = pigs.random();
			target = new Point((int) pig.getCenterX(), (int) pig.getCenterY());
		}
		return target;
	}

	@Override
	public boolean useHighTrajectory(State state) {
		// randomly choose between the trajectories, with a 1 in 6 chance of choosing the high one
		 if (random(6) == 0)
			   return true;
		return false;
	}

	@Override
	public int getTapPoint(State state) {	
		
		//Find out the type of the bird on the sling
		ABType type = state.getBirdTypeOnSling();
		int interval = 0;
		debug(" Bird Type: " + type);
		switch (type) {
		case RedBird: {
			interval = 0;
			break;
		}
		case YellowBird: {
			interval = 65 + random(25);
			break;
		}
		case BlueBird: {
			interval = 65 + random(20);
			break;
		}
		case WhiteBird: {
			interval = 70 + random(20);
			break;
		}
		case BlackBird: {
			interval = 70 + random(20);
			break;
		}
		default: {
		
			//Default tapping: Yellow
			interval = 65 + random(25);
			break;
		}
		}
		
		return interval;
	}
}
