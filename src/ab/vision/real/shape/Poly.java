package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

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
		return String.format("Poly: %dpts at x:%5.1f y:%5.1f", polygon.npoints, centerX, centerY);
	}
}
