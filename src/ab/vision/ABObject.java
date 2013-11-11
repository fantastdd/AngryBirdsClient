package ab.vision;

import java.awt.Rectangle;
import java.util.HashMap;

import ab.vision.real.shape.Rect;

public class ABObject extends Rectangle {
 
 public ABType type;
 private static int counter = 0;
 protected final int sameShapeGap = 4;
 public final static int unassigned = -1;
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
public ABObject()
{
	this.id = counter ++;
}
public ABType getType()
{
	return type;
}
public boolean isSameShape(ABObject ao)
{
	if (Math.abs(width - ao.width) < sameShapeGap && Math.abs(height - ao.height) < sameShapeGap)
		return true;
	return false;
}
public ABPoint getCenter() {
	
   return new ABPoint(getCenterX(), getCenterY());
}


public static void resetCounter() {
	
	counter = 0;
	
}
public static void main(String args[])
{
	Rect o1 = new Rect(5, 1, 1, 1, 1, 1);
	
	Rect o2 = new Rect(5, 2, 1, 1, 1, 1);
	HashMap<ABObject, Integer> map = new HashMap<ABObject, Integer>();
	System.out.println(o1.equals(o2));
	map.put(o1, 0);
	map.put(o2, 2);
	int index = map.get(o1);
	map.put(o1, ++index);
	System.out.println(map.get(o1));
}
@Override
public boolean equals(Object body) {

	return hashCode() == body.hashCode();
}
 
 
}
