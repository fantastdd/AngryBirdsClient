/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */
 
package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import ab.vision.ABObject;
import ab.vision.real.ImageSegmenter;

public class Rect extends Body
{
    // width and height of the rectangle
    public double width = 0;
    public double height = 0;
    
    // orientation
    public double angle;
    
    public Rect(double xs, double ys,  double w, double h, double theta, int t)
    {
        vision_type = t;
        
        if (h >= w)
        {
            angle = theta;
            width = w;
            height = h;
        }
        else
        {
            angle = theta + Math.PI / 2;
            width = h;
            height = w;
        }
        
        centerY = ys;
        centerX = xs;
    } 

    @Override
    public boolean isSameShape(ABObject ao)
    {
    	if (ao instanceof Rect)
    	{
    		Rect _rect = (Rect)ao;
    		if (Math.abs(width - _rect.width) < sameShapeGap
    				&&
    				Math.abs(height - _rect.height) < sameShapeGap
    				&& Math.abs(angle - _rect.angle) < 0.6 ) //  Math.PI/6 < 0.6 < Math.PI/4
    				return true;
    	}
    	return false;
    }
    public Rect(int box[], int t)
    {
        centerX = (box[0] + box[2]) / 2.0;
        centerY = (box[3] + box[1]) / 2.0;
        width = box[2] - box[0];
        height = box[3] - box[1];
        angle = Math.PI / 2;
        
        if (height < width)
        {
            width = height;
            height = box[2] - box[0];
            angle = 0;
        }
        vision_type = t;
    }
    
    /* draw the rectangle onto canvas */
    public void draw(Graphics2D g, boolean fill, Color boxColor)
    {        
        double angle1 = angle;
        double angle2 = perpendicular(angle1);
        
        // starting point for drawing
        double xs, ys;
        ys = centerY + Math.sin(angle) * height / 2 + 
             Math.sin(Math.abs(Math.PI/2 - angle)) * width / 2;
        if (angle < Math.PI / 2)
            xs = centerX + Math.cos(angle) * height / 2 -
                Math.sin(angle) * width / 2;
        else if (angle > Math.PI / 2)
            xs = centerX + Math.cos(angle) * height / 2 +
                Math.sin(angle) * width / 2;
        else
            xs = centerX - width / 2;
            
        Polygon p = new Polygon();
        p.addPoint(round(xs), round(ys));
        
        xs -= Math.cos(angle1) * height;
        ys -= Math.sin(angle1) * height;
        p.addPoint(round(xs), round(ys));
        
        xs -= Math.cos(angle2) * width;
        ys -= Math.sin(angle2) * width;
        p.addPoint(round(xs), round(ys));
        
        xs += Math.cos(angle1) * height;
        ys += Math.sin(angle1) * height;
        p.addPoint(round(xs), round(ys));
        
        
        if (fill) {
            g.setColor(ImageSegmenter._colors[vision_type]);
            g.fillPolygon(p);
        }
        else {
            g.setColor(boxColor);
            g.drawPolygon(p);
        }
    }
    
    public static double perpendicular(double angle)
    {
        return angle > Math.PI / 2 ? angle - Math.PI / 2 : angle + Math.PI / 2;
    }
	
	public String toString()
	{
		return String.format("Rect: id:%d w:%7.3f h:%7.3f a:%7.3f at x:%5.1f y:%5.1f", id, width, height, angle, centerX, centerY);
	}
}
