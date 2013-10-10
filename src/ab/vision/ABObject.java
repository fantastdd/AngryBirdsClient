package ab.vision;

import java.awt.Rectangle;

public class ABObject extends Rectangle {
 
 public ABType type;
 private static int counter = 0;
 public int id;
public ABObject(Rectangle mbr, ABType type) {
	super(mbr);
	this.type = type;
	this.id = counter++;
}
public ABObject(Rectangle mbr, ABType type, int id) {
	super(mbr);
	this.type = type;
	this.id = id;
}
public ABObject(ABObject ab)
{
	super(ab.getBounds());
	this.type = ab.type;
	this.id = ab.id;
}

public ABType getType()
{
	return type;
	}
public ABPoint getCenter() {
	
   return new ABPoint((int)getCenterX(), (int)getCenterY());
}


public static void resetCounter() {
	
	counter = 0;
	
}
 
 
}
