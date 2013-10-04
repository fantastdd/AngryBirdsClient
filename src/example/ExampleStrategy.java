package example;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import ab.planner.Strategy;
import ab.vision.Vision;


public class ExampleStrategy implements Strategy {


	
/**
 * Perform some reasoning to get a target
 * @param Vision
 * @return target point
 */
	public Point getTarget(Vision vision)
	{
		Point _tpt = null;
		List<Rectangle> pigs = vision.findPigsMBR();
		if(!pigs.isEmpty()){
			Random r = new Random();
			int index = r.nextInt(pigs.size());
			
			Rectangle pig = pigs.get(index);
		
			 _tpt = new Point((int) pig.getCenterX(), (int) pig.getCenterY());
		 }
		return _tpt;
	}
	
	
	
}
