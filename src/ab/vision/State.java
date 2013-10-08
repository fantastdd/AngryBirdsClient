package ab.vision;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ab.demo.other.ActionRobot;
import ab.demo.util.StateUtil;
import ab.vision.GameStateExtractor.GameState;

public class State {
	private Vision vision;
	public BufferedImage image;
	private ABList buildingBlocks;
	private ABList pigs;
	private ABList birds;
	private GameState gameState;
	private ABObject slingshot;
	
	@SuppressWarnings("unused")
	private State(){}
	public State(BufferedImage image) {
		
		this.image = image;
		vision = new Vision(image);
		buildingBlocks = ABList.newList();
		pigs = ABList.newList();
		birds = ABList.newList();
		slingshot = null;
		gameState = GameState.UNKNOWN;
	}
	public ABList findBuildingBlocks()
	{
		if(buildingBlocks.isEmpty())
			buildingBlocks = vision.findBuildingBlocks();
		return buildingBlocks;
	}
	public ABList findPigs()
	{
		if(pigs.isEmpty())
			pigs = vision.findPigs();
		return pigs;
	}
	public ABList findBirds()
	{
		if(birds.isEmpty())
			birds = vision.findBirds();
		return birds;
	}
	public ABObject findSlingshot()
	{
		if(slingshot == null)
		{
		    Rectangle slingMBR = vision.findSlingshotMBR();
		    if(slingMBR != null)
		    	slingshot = new ABObject(slingMBR,ABType.Sling);
		}
		return slingshot;
	}
	public List<Point> findTrajPoints()
	{
		return vision.findTrajPoints();
	}
	public GameState getGameState()
	{
		gameState = StateUtil.checkCurrentState(image);
		return gameState;
		
	}
	public void PrintAllObjects()
	{
		System.out.println(" Pigs: " + findPigs().size());
		System.out.println(" Birds: " + findBirds().size());
		System.out.println(" Buliding Blocks: " + findBuildingBlocks().size());
		
	}

	
	public ABType getBirdTypeOnSling()
	{
		ActionRobot.fullyZoomIn();

		State state = ABUtil.getState();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		ActionRobot.fullyZoomOut();
		ABList _birds = state.findBirds();
		if(_birds.isEmpty())
			return ABType.Unknown;
		ABUtil.sortByY(_birds);	
		
		return _birds.get(0).type;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

