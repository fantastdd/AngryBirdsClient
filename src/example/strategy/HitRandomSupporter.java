package example.strategy;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import ab.planner.Strategy;
import ab.vision.ABList;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.ABUtil;
import ab.vision.State;


public class HitRandomSupporter extends Strategy {
	ABList pigs, buildingBlocks, supporters;
	ABObject pig, supporter;
	@Override
	//Get the center point of a building block that supports a pig
	public Point getTarget(State state) 
	{
		supporters = ABList.newList();
		Point target = null;
		pigs = state.findPigs();
		buildingBlocks = state.findBuildingBlocks();
		if(!pigs.isEmpty()){
		
			// Choose a random pig
			pig = pigs.random();
			// Get the list of supporters that supports the pig
			supporters = ABUtil.getSupporters(pig, buildingBlocks);
			debug(" Supporters Num: " + supporters.size());
			if(!supporters.isEmpty())
			{
				// Choose a random supporter
				supporter = supporters.random();
				target = new Point((int)supporter.getCenterX(), (int)supporter.getCenterY());
			}
			else
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
