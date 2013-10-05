package example;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ab.demo.util.StateUtil;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class State {
	private Vision vision;
	public BufferedImage image;
	private ArrayList<ABObject> buildingBlocks;
	private ArrayList<ABObject> pigs;
	private ArrayList<ABObject> birds;
	private GameState gameState;
	private ABObject slingshot;
	
	@SuppressWarnings("unused")
	private State(){}
	public State(BufferedImage image) {
		
		this.image = image;
		vision = new Vision(image);
		buildingBlocks = new ArrayList<ABObject>();
		pigs = new ArrayList<ABObject>();
		birds = new ArrayList<ABObject>();
		slingshot = null;
		gameState = GameState.UNKNOWN;
	}
	public ArrayList<ABObject> findBuildingBlocks()
	{
		if(buildingBlocks.isEmpty())
			buildingBlocks = vision.findBuildingBlocks();
		return buildingBlocks;
	}
	public ArrayList<ABObject> findPigs()
	{
		if(pigs.isEmpty())
			pigs = vision.findPigs();
		return pigs;
	}
	public ArrayList<ABObject> findBirds()
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

