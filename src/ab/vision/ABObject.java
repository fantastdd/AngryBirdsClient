package ab.vision;

import java.awt.Rectangle;

public class ABObject {
 private Rectangle mbr;
 private String type;
 private static int counter = 0;
 private int id;
public ABObject(Rectangle mbr, String type) {
	super();
	this.mbr = mbr;
	this.type = type;
	this.id = counter++;
}
public Rectangle getMbr() {
	return mbr;
}
public String getType() {
	return type;
}
public int getId() {
	return id;
}
 
 
}
