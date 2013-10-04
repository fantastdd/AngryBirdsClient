package ab.vision;

import java.awt.Rectangle;

public class ABObject extends Rectangle {
 /**
	 * 
	 */
	private static final long serialVersionUID = 1949786599051438075L;
public ABType type;
 private static int counter = 0;
 public int id;
public ABObject(Rectangle mbr, ABType type) {
	super(mbr);
	this.type = type;
	this.id = counter++;
}

public static void resetCounter() {
	
	counter = 0;
	
}
 
 
}