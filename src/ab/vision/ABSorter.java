package ab.vision;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ABSorter {
	
	/**by
	 * Sort the ABObjects according their X coordinate (top-left corner)
	 * */
	public static List<Rectangle> sortByX(List<Rectangle> objects)
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
	public static List<Rectangle> sortByY(List<Rectangle> objects)
	{
		Collections.sort(objects, new Comparator<Rectangle>(){

			@Override
			public int compare(Rectangle o1, Rectangle o2) {
				
				return ((Integer)(o1.y)).compareTo((Integer)(o2.y));
			}
			
			
		});
		return objects;
		
	}
	

	public static void main(String[] args) {


	}

}
