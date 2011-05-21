package imcrowd.basicObject.goal;

import imcrowd.basicObject.ConfigurationIO;
import imcrowd.basicObject.MyObject;
import imcrowd.io.imageIO.ImageLoader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.vecmath.Vector2f;


public class Goal implements MyObject, ConfigurationIO{
	static int idCounter = 0;			// goal id counter.
	boolean isSelected;					// the goal is selected or not.
	int height,width;					// the height and width of the goal's image. 
	int id;
	
	ImageIcon img;						// the image of the goal.	
	ImageIcon selected;					// the image indicating the selected status of the goal.
	Ellipse2D region;					// the region of the goal.
	
	Vector2f vPosition;					// the position of the goal.	
	
	public Goal(Vector2f p, ImageIcon img){
		id = idCounter++;
		isSelected = false;
		
		this.img = img;
		height = img.getIconHeight();
		width = img.getIconWidth();
		
		selected = ImageLoader.selectedImg;	
		
		vPosition = new Vector2f(p);
		region = new Ellipse2D.Double(vPosition.x - 3*width/4, vPosition.y, width, height);
	}

	
	public void setAttributes(Properties configuration, int i){
	}
	
	public Map<String, String> getAttributes(int i){
		
		HashMap<String, String> attrMap = new HashMap<String, String>();
		String attrName, value;
		
		attrName = "goal_vPosition"+i;
		value = String.valueOf(vPosition.toString().replace("(","").replace(")","").replace(",",""));
		attrMap.put(attrName, value);
		
		
		attrName = "goal_IMG"+i;
		
		String imgName = img.toString();
		int indexSlash = imgName.lastIndexOf('/');
		int len = imgName.length();
		
		value = String.valueOf(imgName.substring(indexSlash+1, len));
		attrMap.put(attrName, value);
		
		return attrMap;
	}	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Goal other = (Goal) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void setSelect(boolean bool){
		isSelected = bool;
	}
	
	/* Evaluates the goal to see if it overlaps or intersects with the point */
	public boolean isPointInside(Point2D p)
	{ 
	  return region.contains(p); 
	}
	
	/* goalImg */
	public ImageIcon getImg(){
		return img;
	}
	
	/* region */
	public Ellipse2D getRegion(){
		return region;
	}
	
	public void setRegion(){
		region.setFrame(vPosition.x - 3*width/4, vPosition.y, width, height);
	}
	
	/* vPosition */
	public Vector2f getPosition(){
		return vPosition;
	}
	
	public void setPosition(float x, float y){
		vPosition.x = x;
		vPosition.y = y;
		setRegion();
	}
	
	public void setPosition(Vector2f p){
		vPosition = p;
		setRegion();
	}
	
	public void paint(Graphics2D g){
		g.setPaint(Color.GRAY);
		g.draw(region);
		g.setPaint(Color.BLACK);
		g.drawImage(img.getImage(), (int)vPosition.x - width/2, (int)vPosition.y - height/2, null);
		
		if(isSelected){
			g.drawImage(selected.getImage(), (int)(vPosition.x - selected.getIconWidth()/2),
					    (int)(vPosition.y - height/2 -selected.getIconHeight()-5), null);
		}
	}
	
}
