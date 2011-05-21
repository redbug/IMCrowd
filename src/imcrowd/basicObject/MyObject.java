package imcrowd.basicObject;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2f;


public interface MyObject {
	void setSelect(boolean d);						
	void setPosition(Vector2f p);					
	void setPosition(float x, float y);
	void setRegion();								
	void paint(Graphics2D g);						
	boolean isPointInside(Point2D p);				
	Ellipse2D getRegion();							
	Vector2f getPosition();							
}
