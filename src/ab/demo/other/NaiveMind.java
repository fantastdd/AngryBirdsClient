package ab.demo.other;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import ab.vision.Vision;

public class NaiveMind {

	/**
	 * @param args
	 */
	public final static int yellow_bird = 2;
	public final static int blue_bird = 3;
	public final static int red_bird = 1;
	public final static int white_bird = 5;
	public final static int black_bird = 4;
	public final static int unknown_bird = 0;
	
	//only need to know black/white birds
	public static int getBirdOnSlingShot(BufferedImage image)
	{
		Vision vision = new Vision(image);
		List<Rectangle> redbirds = vision.findRedBirds();
		int y = 0;
		Rectangle sling = vision.findSlingshot();
		if(sling != null){
			y = vision.findSlingshot().y;
			for(Rectangle rec : redbirds)
			{
				if(rec.getCenterY() > y)
					return red_bird;
			}
			List<Rectangle> yellowbirds = vision.findYellowBirds();
			for(Rectangle rec : yellowbirds)
			{
				if(rec.getCenterY() > y)
					return yellow_bird;
			}
			List<Rectangle> bluebirds = vision.findBlueBirds();
			for(Rectangle rec : bluebirds)
			{
				if(rec.getCenterY() > y)
					return blue_bird;
			}
			List<Rectangle> blackbirds = vision.findBlackBirds();
			for(Rectangle rec : blackbirds)
			{
				if(rec.getCenterY() > y)
					return black_bird;
			}
			List<Rectangle> whitebirds = vision.findWhiteBirds();
			for(Rectangle rec : whitebirds)
			{
				if(rec.getCenterY() > y)
					return white_bird;
			}
		}
		return unknown_bird;
		
		
		
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
