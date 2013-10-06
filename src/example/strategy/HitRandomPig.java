package example.strategy;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

import example.State;

import ab.planner.Strategy;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.ABUtil;


public class HitRandomPig implements Strategy {
	List<ABObject> pigs;
	@Override
	public Point getTarget(State state) 
	{
		Point _tpt = null;
		pigs = state.findPigs();
		if(!pigs.isEmpty()){
			Random r = new Random();
			int index = r.nextInt(pigs.size());
			
			Rectangle pig = pigs.get(index);
		
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
