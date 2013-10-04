package ab.vision;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ABUtil {
	
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
	/**
	 * Get the type of the bird on the slingshot
	 * */
	public static ABType getBirdOnSlingShot(List<ABObject> birds)
	{
	
		if(birds.isEmpty())
			return ABType.Unknown;
		
		sortByY(birds);
		
		return birds.get(0).type;
		
		
	}
	
	

	public static void main(String[] args) {


	}

}
