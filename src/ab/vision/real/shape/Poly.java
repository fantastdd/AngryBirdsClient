package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import ab.vision.ABObject;
import ab.vision.real.ImageSegmenter;
import ab.vision.real.LineSegment;
/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */

public class Poly extends Body
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3628493742939819604L;
	public Polygon polygon = null;
    
    public Poly(ArrayList<LineSegment> lines, int left, int top, int t, double xs, double ys)
    {
        polygon = new Polygon();
        vision_type = t;
        
        if (lines != null)
        {
            for (LineSegment l : lines)
            {
                Point start = l._start;
                polygon.addPoint(start.x + left, start.y + top);
            }
        }
        centerX = xs;
        centerY = ys;
    }
    @Override
    public boolean isSameShape(ABObject ao)
    {
    	if (ao instanceof Poly)
    	{
    		Polygon _polygon = ((Poly)ao).polygon;
    		if(
    				Math.abs( polygon.getBounds().width -
    						_polygon.getBounds().width) < sameShapeGap
    						&& 
    						Math.abs( polygon.getBounds().height -
    	    						_polygon.getBounds().height) < sameShapeGap
    	    	)
    			return true;
    						
    			
    	}
    	return false;
    }
    public void draw(Graphics2D g, boolean fill, Color boxColor)
    {
        if (fill) {
            g.setColor(ImageSegmenter._colors[vision_type]);
            g.fillPolygon(polygon);
        }
        else {
            g.setColor(boxColor);
            g.drawPolygon(polygon);
        }
    }
	
	public String toString()
	{
		return String.format("Poly: id:%d %dpts at x:%5.1f y:%5.1f", id, polygon.npoints, centerX, centerY);
	}
}
