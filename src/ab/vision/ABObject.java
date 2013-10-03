package ab.vision;

import java.awt.Rectangle;

public class ABObject {
 private Rectangle mbr;
 public ABType type;
 private static int counter = 0;
 private int id;
public ABObject(Rectangle mbr, ABType type) {
	super();
	this.mbr = mbr;
	this.type = type;
	this.id = counter++;
}
public Rectangle getMbr() {
	return mbr;
}
public ABType getType() {
	return type;
}
public int getId() {
	return id;
}
public static void resetCounter() {
	
	counter = 0;
	
}
 
 
}
