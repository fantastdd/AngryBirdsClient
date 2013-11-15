/**
 * @author      Andrew Wang <u4853279@anu.edu.au>
 */
 
package ab.vision.real.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import ab.vision.ABObject;
import ab.vision.real.ImageSegmenter;

public class Rect extends Body
{
    // width and height of the rectangle
    public double width = 0;
    public double height = 0;
    public Polygon p;
    public RectType rectType;
    public int area;

    
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
        
        area = (int)(width * height);
        
        assignType(vision_type);
        createPolygonAndSectors();
        calRecType();
    } 
   /* public static void main(String args[])
    {
    	Line2D l = new Line2D.Float(10,10,10,10);
    	Point p = new Point(10,13);
    	System.out.println(l.ptSegDist(p));
    }*/
    private void calRecType()
    {
    	double ratio= height/width;
    	int round =  (int)Math.round(ratio);
    	rectType = RectType.getType(round);
    	
    }
    
    private void createPolygonAndSectors()
    {
    	 sectors = new Line2D[8];
    	 
    	 double angle1 = angle;
         double angle2 = perpendicular(angle1);
         
         // starting point for drawing
         double _xs, _ys;
         _ys = centerY + Math.sin(angle) * height / 2 + 
              Math.sin(Math.abs(Math.PI/2 - angle)) * width / 2;
         if (angle < Math.PI / 2)
             _xs = centerX + Math.cos(angle) * height / 2 -
                 Math.sin(angle) * width / 2;
         else if (angle > Math.PI / 2)
             _xs = centerX + Math.cos(angle) * height / 2 +
                 Math.sin(angle) * width / 2;
         else
             _xs = centerX - width / 2;
             
         p = new Polygon();
         p.addPoint(round(_xs), round(_ys));
         
        
         
         _xs -= Math.cos(angle1) * height;
         _ys -= Math.sin(angle1) * height;
         p.addPoint(round(_xs), round(_ys));
         
       
         
         _xs -= Math.cos(angle2) * width;
         _ys -= Math.sin(angle2) * width;
         p.addPoint(round(_xs), round(_ys));
         
         
         
         _xs += Math.cos(angle1) * height;
         _ys += Math.sin(angle1) * height;
         p.addPoint(round(_xs), round(_ys));
   
         if(angle > Math.PI/2)
         {
        	 sectors[4] = new Line2D.Float(p.xpoints[0], p.ypoints[0], p.xpoints[0], p.ypoints[0]);
        	 sectors[5] = new Line2D.Float(p.xpoints[0], p.ypoints[0], p.xpoints[1], p.ypoints[1]);
             sectors[6] = new Line2D.Float(p.xpoints[1], p.ypoints[1], p.xpoints[1], p.ypoints[1]);
             sectors[7] = new Line2D.Float(p.xpoints[1], p.ypoints[1], p.xpoints[2], p.ypoints[2]);
             sectors[0] = new Line2D.Float(p.xpoints[2], p.ypoints[2], p.xpoints[2], p.ypoints[2]);
             sectors[1] = new Line2D.Float(p.xpoints[2], p.ypoints[2], p.xpoints[3], p.ypoints[3]);
             sectors[2] = new Line2D.Float(p.xpoints[3], p.ypoints[3], p.xpoints[3], p.ypoints[3]);
             sectors[3] = new Line2D.Float(p.xpoints[3], p.ypoints[3], p.xpoints[0], p.ypoints[0]);
         } else
         {
        	 sectors[4] = new Line2D.Float(p.xpoints[0], p.ypoints[0], p.xpoints[0], p.ypoints[0]);
        	 sectors[3] = new Line2D.Float(p.xpoints[0], p.ypoints[0], p.xpoints[1], p.ypoints[1]);
             sectors[2] = new Line2D.Float(p.xpoints[1], p.ypoints[1], p.xpoints[1], p.ypoints[1]);
             sectors[1] = new Line2D.Float(p.xpoints[1], p.ypoints[1], p.xpoints[2], p.ypoints[2]);
             sectors[0] = new Line2D.Float(p.xpoints[2], p.ypoints[2], p.xpoints[2], p.ypoints[2]);
             sectors[7] = new Line2D.Float(p.xpoints[2], p.ypoints[2], p.xpoints[3], p.ypoints[3]);
             sectors[6] = new Line2D.Float(p.xpoints[3], p.ypoints[3], p.xpoints[3], p.ypoints[3]);
             sectors[5] = new Line2D.Float(p.xpoints[3], p.ypoints[3], p.xpoints[0], p.ypoints[0]);
         }
    }
    @Override
    public Rectangle getBounds()
    {
    	return p.getBounds();
    }
    @Override
    public boolean isSameShape(ABObject ao)
    {
    	if (ao instanceof Rect)
    	{
    		//Rect _rect = (Rect)ao;
    		if (isSameSize(ao))
    				/*&&( Math.abs((Math.PI - angle - _rect.angle)) < 0.5 ||(
    					Math.abs(angle - _rect.angle) < 0.5 || (rectType == RectType.rec1x1)*///)
    						/*( ( Math.abs(height/width) - 1)  < 0.01 
    					&& ( Math.PI/2 - Math.abs(angle - _rect.angle) < 0.1))*///)))//  Math.PI/6 < 0.6 < Math.PI/4
    				return true;
    	}
    	return false;
    }
    @Override
    public boolean isSameSize(ABObject ao)
    {
    	if (ao instanceof Rect)
    	{
    		Rect _rect = (Rect)ao;
    		double ratio = ((area > _rect.area)? ((double)area/_rect.area) : ((double)_rect.area/area));
    		if ( (Math.abs(rectType.id - _rect.rectType.id) < 2 )&& ratio < 1.5)
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
        assignType(vision_type);
        area = (int)(width * height);
        createPolygonAndSectors();
        calRecType();
    }
    public Rect(double centerX, double centerY, double width, double height, double angle, int vision_type, int area)
    {
    	  this.centerX = centerX;
    	  this.centerY = centerY;
    	  this.width = width;
    	  this.height = height;
    	  this.angle = angle;
    	  this.vision_type = vision_type;
    	  this.area = area;
    	  createPolygonAndSectors();
          calRecType();    	
    }
    
    public Rect extend(RectType rectType)
    {
    	assert(rectType.id > this.rectType.id);
   
    	double extensionDegree = (double)rectType.id / this.rectType.id - 1;
    	double height = this.height * extensionDegree * 2.2 + this.height;
    	//System.out.println(" height: " + height + " extensionDegree: " + extensionDegree + " rectType" + rectType + " id ");
    	int area = (int)(this.width * height);
    	return new Rect(this.centerX, this.centerY, this.width, height, this.angle, this.vision_type, area);
    	
    }
    
    
    public static void main(String args[])
    {
    	Line2D line = new Line2D.Float(10,10,10,10);
    	Line2D _line = new Line2D.Float(10,11,10,12);
    	System.out.println(_line.ptSegDist(line.getP1()));
    }
    
    /* draw the rectangle onto canvas */
    public void draw(Graphics2D g, boolean fill, Color boxColor)
    {        
    
        
        
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
		return String.format("Rect: id:%d type:%s area:%d w:%7.3f h:%7.3f a:%7.3f at x:%5.1f y:%5.1f", id, rectType, area, width, height, angle, centerX, centerY);
	}
}
