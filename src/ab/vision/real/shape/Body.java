/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */

package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

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
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + vision_type;
        hash = hash * 31 + id;
        hash = hash * 13 + (int)centerX;
        hash = hash * 67 + (int)centerY;
        return hash;
    }
    @Override
    public boolean equals(Object body)
    {
    	return hashCode() == body.hashCode();
    }
}
