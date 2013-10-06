package ab.vision;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import ab.demo.other.ActionRobot;
import ab.planner.Strategy;
import example.State;

public class ABUtil {
	
	public static int gap = 5;
	
    public static State getState()
    {
    	//Do screenshot
        BufferedImage image = ActionRobot.doScreenShot();
        
    	return new State(image);
    }

	
	/**by
	 * Sort the ABObjects according their X coordinate (top-left corner)
	 * */
	public static List<ABObject> sortByX(List<ABObject> objects)
	{
		Collections.sort(objects, new Comparator<Rectangle>(){

			@Override
			public int compare(Rectangle o1, Rectangle o2) {
				
				return ((Integer)(o1.x)).compareTo((Integer)(o2.x));
			}
			
			
		});
		return objects;
	}
	
	/**
	 * Sort the ABObjects according their Y coordinate (top-left corner)
	 * */
	public static List<ABObject> sortByY(List<ABObject> objects)
	{
		Collections.sort(objects, new Comparator<Rectangle>(){

			@Override
			public int compare(Rectangle o1, Rectangle o2) {
				
				return ((Integer)(o1.y)).compareTo((Integer)(o2.y));
			}
			
			
		});
		return objects;
		
	}
	public void process(Rectangle rec){};
	
	public static ABType getBirdTypeOnSling()
	{
		ActionRobot.fullyZoomIn();
		ABType type = getBirdOnSling(getState().findBirds());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		ActionRobot.fullyZoomOut();
		return type;
	}
	// If o1 supports o2, return true
	public static boolean isSupport(ABObject o2, ABObject o1)
	{
		int ex_o1 = o1.x + o1.width;
		int ex_o2 = o2.x + o2.width;
		
		int ey_o2 = o2.y + o2.height;
		if(
			(Math.abs(ey_o2 - o1.y) < gap)
			&& 
 			!( o2.x - ex_o1  > gap || o1.x - ex_o2 > gap )
		  )
	        return true;	
		
		return false;
		
	}
	//Return a link list of ABObjects that support o1 (test by isSupport function ). 
	//objs refers to a list of potential supporters.
	//Empty list will be returned if no such supporters. 
	public static LinkedList<ABObject> getSupporters(ABObject o2, 
			List<ABObject> objs)
			{
				LinkedList<ABObject> result = new LinkedList<ABObject>();
				//Loop through the potential supporters
		        for(ABObject o1: objs)
		        {
		        	if(isSupport(o2,o1))
		        		result.add(o1);
		        }
		        return result;
			}
	/**
	 * Get the type of the bird on the slingshot
	 * */
	private static ABType getBirdOnSling(List<ABObject> birds)
	{
		if(birds.isEmpty())
			return ABType.Unknown;
		sortByY(birds);	
		return birds.get(0).type;
		
		
	}
	//New a strategy by the class name
	public static Strategy getStrategy(String strategyFullName)
	{
		Strategy strategy = null;
		try {
			strategy = (Strategy) Class.forName(strategyFullName).newInstance();
		} catch (InstantiationException e1) {
		
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Can not find the strategy: " + strategyFullName);
		}
		return strategy;
	}
	

	public static void main(String[] args) {


	}

}
