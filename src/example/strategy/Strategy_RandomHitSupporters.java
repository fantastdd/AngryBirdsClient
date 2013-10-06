package example.strategy;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ab.planner.Strategy;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.ABUtil;
import example.State;


public class Strategy_RandomHitSupporters implements Strategy {
	List<ABObject> pigs;
	List<ABObject> buildingBlocks;
	@Override
	//Get the center point of a building block that supports a pig
	public Point getTarget(State state) 
	{
		List<ABObject> supporters = new LinkedList<ABObject>();
		Point _tpt = null;
		pigs = state.findPigs();
		buildingBlocks = state.findBuildingBlocks();
		if(!pigs.isEmpty()){
			Random r = new Random();
			// Choose a random pig
			int index = r.nextInt(pigs.size());			
			ABObject pig = pigs.get(index);
			// Get the list of supporters that supports the pig
			supporters = ABUtil.getSupporters(pig, buildingBlocks);
			System.out.println(" Supporters Num: " + supporters.size());
			if(!supporters.isEmpty())
			{
				// Choose a random supporter
				ABObject supporter = supporters.get(r.nextInt(supporters.size()));
				_tpt = new Point((int)supporter.getCenterX(), (int)supporter.getCenterY());
			}
			else
				_tpt = new Point((int) pig.getCenterX(), (int) pig.getCenterY());
		}
		return _tpt;
	}

	@Override
	public boolean useHighTrajectory(State state) {
		// randomly choose between the trajectories, with a 1 in 6 chance of choosing the high one
		 if (new Random().nextInt(6) == 0)
			   return true;
		return false;
	}

	@Override
	public float getTapPoint(State state) {	
		
		//Find out the type of the bird on the sling
		ABType type = ABUtil.getBirdTypeOnSling();
		float interval = 0;
		Random randomGenerator = new Random();
		switch (type) {
		case RedBirds: {
			System.out.println(" Bird Type: Red");
			interval = 0;
			break;
		}
		case YellowBirds: {
			System.out.println(" Bird Type: Yellow");
			interval = (float)((randomGenerator.nextInt(25) + 65)) / 100;
			break;
		}
		case BlueBirds: {
			System.out.println(" Bird Type: Blue");
			interval = (float)((randomGenerator.nextInt(20) + 65)) / 100;
			break;
		}
		case WhiteBirds: {
			System.out.println(" Bird Type: White");
			interval = (float)((randomGenerator.nextInt(20) + 70)) / 100;
			break;
		}
		case BlackBirds: {
			System.out.println(" Bird Type: Black");
			interval = (float)((randomGenerator.nextInt(20) + 70)) / 100;
			break;
		}
		default: {
			System.out.println(" Bird Type: Red");
			//Default tapping: Yellow
			interval = (float)((randomGenerator.nextInt(25) + 65)) / 100;
			break;
		}
		}
		
		return interval;
	}
}
