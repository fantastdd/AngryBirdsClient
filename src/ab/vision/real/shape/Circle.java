/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */
 
package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import ab.vision.ABObject;
import ab.vision.ABShape;
import ab.vision.real.ImageSegmenter;

public class Circle extends Body
{
    // radius of the circle
    public double r;
    public Rectangle bounds;
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
        shape = ABShape.Circle;
        //int diameter = (int)(2 * r);
        //bounds = new Rectangle((int)(xs - r), (int)(ys - r), diameter, diameter);
        bounds = new Rectangle((int)(xs - r * Math.sin(Math.PI/4)), (int)(ys - r * Math.sin(Math.PI/4)), 
        		(int)(2 * r * Math.sin(Math.PI/4)), (int)(r * Math.sin(Math.PI/4))); 
        assignType(vision_type);
        
        angle = 0;
        sectors = new Line2D[8];
        Rectangle rec = bounds;
        
        sectors[0] = new Line2D.Float(rec.x + rec.width, rec.y, rec.x + rec.width, rec.y );
        sectors[1] = new Line2D.Float(rec.x + rec.width, rec.y, rec.x , rec.y );
        sectors[2] = new Line2D.Float(rec.x, rec.y, rec.x , rec.y );
        sectors[3] = new Line2D.Float(rec.x, rec.y, rec.x , rec.y + rec.height);
        sectors[4] = new Line2D.Float(rec.x, rec.y + rec.height, rec.x, rec.y + rec.height);
        sectors[5] = new Line2D.Float(rec.x, rec.y + rec.height, rec.x + rec.width, rec.y + rec.height);
        sectors[6] = new Line2D.Float(rec.x + rec.width, rec.y + rec.height, rec.x + rec.width, rec.y + rec.height);
        sectors[7] = new Line2D.Float(rec.x + rec.width, rec.y + rec.height, rec.x + rec.width, rec.y);
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
    @Override
    public Rectangle getBounds()
    {
    	return bounds;
    }
    public Circle(int box[], int t)
    {
        centerX = (box[0] + box[2]) / 2.0;
        centerY = (box[1] + box[3]) / 2.0;
        r = (box[2] - box[0] + box[3] - box[1]) / 4.0;
        //int diameter = (int)(2 * r);
        //bounds = new Rectangle((int)box[0], (int)box[1], diameter , diameter );
        bounds = new Rectangle((int)(centerX - r * Math.sin(Math.PI/4)), (int)(centerY - r * Math.sin(Math.PI/4)), 
        		(int)(2 * r * Math.sin(Math.PI/4)), (int)(r * Math.sin(Math.PI/4))); 
        angle = 0;
        sectors = new Line2D[8];
        Rectangle rec = bounds;
        
        sectors[0] = new Line2D.Float(rec.x + rec.width, rec.y, rec.x + rec.width, rec.y );
        sectors[1] = new Line2D.Float(rec.x + rec.width, rec.y, rec.x , rec.y );
        sectors[2] = new Line2D.Float(rec.x, rec.y, rec.x , rec.y );
        sectors[3] = new Line2D.Float(rec.x, rec.y, rec.x , rec.y + rec.height);
        sectors[4] = new Line2D.Float(rec.x, rec.y + rec.height, rec.x, rec.y + rec.height);
        sectors[5] = new Line2D.Float(rec.x, rec.y + rec.height, rec.x + rec.width, rec.y + rec.height);
        sectors[6] = new Line2D.Float(rec.x + rec.width, rec.y + rec.height, rec.x + rec.width, rec.y + rec.height);
        sectors[7] = new Line2D.Float(rec.x + rec.width, rec.y + rec.height, rec.x + rec.width, rec.y);
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
