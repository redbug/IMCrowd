package imcrowd.basicObject.obstacle;

import imcrowd.basicObject.InfectiveSource;
import imcrowd.basicObject.agent.behavior.groupMind.Gathering;
import imcrowd.basicObject.agent.boid.Boid;
import imcrowd.engine.Controller;
import imcrowd.io.imageIO.ImageLoader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Properties;

import javax.vecmath.Vector2f;


public class StreetPerformance extends InfectiveObstacle{
	
	final public static int thresholdWeighting = 5; 
	
	ImageLoader imgLoader = ImageLoader.getInstance();
	
	public StreetPerformance(Vector2f p ){	
		super(p);
		
		Controller	ct = Controller.getInstance();	
		
		setInfectiveSource(
				ct.getAlpha(),
				ct.getBeta(),
				ct.getSpeparationI(),
				ct.getCohesionI(),
				ct.getAlignmentI()
		);
		
		width  = 160;
		height = 160;
		
		region = new Ellipse2D.Double(
				vPosition.x - (float)width/2,
				vPosition.y - (float)height/2, 
				width, 
				height);
		
		gridManager.updateNeighborContext(this);
	}
	
	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);
		
		String str;
		
		int infectSrc[] = new int[5];
		
		str = configuration.getProperty("Alpha");
		infectSrc[0] = Integer.valueOf(str);
	
		str = configuration.getProperty("Beta");
		infectSrc[1] = Integer.valueOf(str);
		
		str = configuration.getProperty("ob_iSeparation"+i);
		infectSrc[2] = Integer.valueOf(str);
		
		str = configuration.getProperty("ob_iCohesion"+i);
		infectSrc[3] = Integer.valueOf(str);
		
		str = configuration.getProperty("ob_iAlignment"+i);
		infectSrc[4] = Integer.valueOf(str);
		
		setInfectiveSource(
				infectSrc[0],
				infectSrc[1],
				infectSrc[2],
				infectSrc[3],
				infectSrc[4]
		);
	}
	
	@Override
	public Map<String, String> getAttributes(int i){
		
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrName = "ob_type"+i;
		value = String.valueOf("StreetPerformance");
		attrMap.put(attrName, value);
			
		return attrMap;
	}
	
	
	public void setInfectiveSource(int alpha, int beta, int iS, int iC, int iA){
		infectiveSource = new InfectiveSource(
				alpha,
				beta,
				new Boid(iS, iC, iA),
				new Gathering(this.getPosition())
			);
	}
	
	
	@Override
	public void paint(Graphics2D g){
		//keep original color and stroke.
		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();		
				
		
		/*********************************
		 * draw the image of the special agent
		 *********************************/
		AffineTransform at = new AffineTransform();
		Image img;
		
		
		img = imgLoader.getDancerImg(1).getImage();
		at.translate(vPosition.x - 15, vPosition.y - 60);
		at.scale(0.8, 0.8);
		g.drawImage(img, at, null);
		
		at.setToIdentity();
		img = imgLoader.getDancerImg(1).getImage();
		at.translate(vPosition.x - 70, vPosition.y - 60);
		at.scale(0.8, 0.8);
		g.drawImage(img, at, null);
		
		at.setToIdentity();
		img = imgLoader.getDancerImg(3).getImage();
		at.translate(vPosition.x - 65, vPosition.y - 70);
		at.scale(0.6, 0.6);
		g.drawImage(img, at, null);
		
		at.setToIdentity();
		img = imgLoader.getDancerImg(5).getImage();
		at.translate(vPosition.x - 55, vPosition.y - 30);
		at.scale(0.8, 0.8);
		g.drawImage(img, at, null);
			
		at.setToIdentity();
		img = imgLoader.getDancerImg(4).getImage();
		at.translate(vPosition.x -5, vPosition.y - 30);
		at.scale(0.7, 0.7);
		g.drawImage(img, at, null);

		
		if(isSelected){
			g.drawImage(selected.getImage(), (int)(vPosition.x - selected.getIconWidth()/2),
					    (int)(vPosition.y - height/4 -selected.getIconHeight()-5), null);
			
			drawNeighborHoodMark(g);
		}
		
		//g.draw(region);

		
		//recover the original color and stroke.
		g.setPaint(oldColor);
		g.setStroke(oldStroke);
	}
	
}
