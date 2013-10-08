package ab.planner;

import java.awt.Point;
import java.util.Random;

import ab.vision.State;


public abstract class Strategy {
	Random r;
	
	public Strategy()
	{
		r = new Random();
	}
	public int random(int range)
	{
		return r.nextInt(range);
	}
	public void debug(String message)
	{
		System.out.println(message);
	} 
public abstract Point getTarget(State state);
public abstract boolean useHighTrajectory(State state);
public abstract int getTapPoint(State state);

}
