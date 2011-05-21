package imcrowd.basicObject.agent.specialAgent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import imcrowd.basicObject.agent.Agent;
import imcrowd.io.imageIO.ImageLoader;


public abstract class SpecialAgent extends Agent{
	ImageLoader imgLoader = ImageLoader.getInstance();
	
	String name;
	
	protected Image img;
	protected float imgScale;
	
	
	protected SpecialAgent(float x, float y, float theta, int groupId){
		super(x, y, theta, groupId);
		isSpecial = true;
		colorId = -1;
		colorOfPS = null;
	}
	
	public void paint(Graphics2D g){
		//keep original color and stroke.
		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();		
				
		if(isDrawView){
			
			/* draw the view field */
		    drawViewField(g);		
			
			/* draw the wander circle.*/
		    drawWanderCircle(g);
		    
		    
			if(nearestTarget != null){
				g.drawOval((int)nearestTarget.getPosition().x -10, (int)nearestTarget.getPosition().y -10, 20, 20);
			}
		    
		    
		    
		    /* draw the boundary markers of the neighborhood */ 
		    drawNeighborHoodMark(g);
   			
			g.setPaint(oldColor);
			g.setStroke(oldStroke);
		}	
				
		/*********************************
		 * draw the image of special agent
		 *********************************/
		AffineTransform at = new AffineTransform();
		at.translate(vPosition.x - (img.getWidth(null) * 0.5 * imgScale), (vPosition.y - img.getHeight(null) * 0.5 * imgScale));
		at.scale(imgScale, imgScale);
		g.drawImage(img, at, null);
									
		//recover the original color and stroke.
		g.setPaint(oldColor);
		g.setStroke(oldStroke);
	}
	
	public String getName(){
		return name;
	}
	
}
