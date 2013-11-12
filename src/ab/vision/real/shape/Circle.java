/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */
 
package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;

import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.real.ImageSegmenter;

public class Circle extends Body
{
    // radius of the circle
    public double r;
    
    /* Create a new circle
     * @param   xs, ys - coordinate of the circle centre
     *          radius - circle radius
     *          t      - type of the object
     */
    public Circle(double xs, double ys, double radius, int t)
    {
        centerX = xs;
        centerY = ys;
        r = radius;
        vision_type = t;
        assignType(vision_type);
    }
    @Override
    public boolean isSameShape(ABObject ao)
    {
    	if (ao instanceof Circle)
    	{
    		if (Math.abs(r - ((Circle)ao).r) < sameShapeGap)
    				return true;
    	}
    	return false;
    }
    public Circle(int box[], int t)
    {
        centerX = (box[0] + box[2]) / 2.0;
        centerY = (box[1] + box[3]) / 2.0;
        r = (box[2] - box[0] + box[3] - box[1]) / 4.0;
        vision_type = t;
        assignType(vision_type);
    }
    
    /* draw the circle onto canvas */
    public void draw(Graphics2D g, boolean fill, Color boxColor)
    {
        if (fill)
        {
            g.setColor(ImageSegmenter._colors[vision_type]);
            g.fillOval(round(centerX - r), round(centerY - r), round(r * 2), round(r * 2));
        }
        else
        {
            g.setColor(boxColor);
            g.drawOval(round(centerX - r), round(centerY - r), round(r * 2), round(r * 2));
        }
    }
	
	public String toString()
	{
		return String.format("Circ: id:%d type:%s r:%7.3f at x:%5.1f y:%5.1f", id, type, r, centerX, centerY);
	}
}
