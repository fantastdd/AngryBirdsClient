package ab.vision;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ab.demo.other.ActionRobot;
import example.State;

public class ABUtil {
	
	static final int gap = 5;
	
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
	public static boolean isSupport(ABObject o1, ABObject o2)
	{
		int ex_o1 = o1.x + o1.width;
		int ex_o2 = o2.x + o2.width;
		int ey_o1 = o1.y + o1.height;
		int ey_o2 = o2.y + o2.height;
		if(
			(Math.abs(ey_o2 - o1.y) < gap)
			&&
			()
		  )
		
		
		
		return false;
		
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
	public static boolean isSupport(ABObject supportee, ABObject supporter)
	{
		return false;
	}
	

	public static void main(String[] args) {


	}

}
