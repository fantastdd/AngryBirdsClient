package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import ab.vision.ABObject;
import ab.vision.ABShape;
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
        assignType(vision_type);
        shape = ABShape.Poly;
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
        angle = 0;
        
        sectors = new Line2D[8];
        Rectangle rec = polygon.getBounds();
        
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
    @Override
    public Rectangle getBounds()
    {
    	return polygon.getBounds();
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
