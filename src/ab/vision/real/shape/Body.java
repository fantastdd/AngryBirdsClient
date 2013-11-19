/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */

package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import ab.vision.ABObject;
import ab.vision.ABPoint;

public abstract class Body extends ABObject
{
   
	private static final long serialVersionUID = 4126732384091164666L;
	
	public Body()
	{
		super();
	}

	// type of the object, specified by Andrew, 
    public int vision_type; 
    // position (x, y) as center of the object
    public double centerX = 0;
    public double centerY = 0;
    public abstract void draw(Graphics2D g, boolean fill, Color boxColor);
    
    public static int round(double i)
    {
        return (int) (i + 0.5);
    }
    @Override
    public ABPoint getCenter()
    {
    	return new ABPoint(centerX, centerY);
    }
    @Override
    public double getCenterX()
    {
    	return centerX;
    }
    @Override 
    public double getCenterY()
    {
    	return centerY;
    }
    @Override
    public int hashCode() {
    	 int hash = 1;
         hash = hash * 17 + type.hashCode();
         hash = hash * 31 + shape.hashCode();
         hash = hash * 93 + (int)(angle * 1000);
         //hash = hash * 31 + id;
         hash = hash * 13 + (int)getCenterX();
         hash = hash * 67 + (int)getCenterY();
         return hash;
    }
    @Override
    public boolean equals(Object body)
    {
    	return hashCode() == body.hashCode();
    }
}
