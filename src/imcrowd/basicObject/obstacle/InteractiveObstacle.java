package imcrowd.basicObject.obstacle;

import imcrowd.io.imageIO.ImageLoader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.vecmath.Vector2f;


public class InteractiveObstacle extends Obstacle{
	
	public static final int TRASHCAN	=	0;
	public static final int CAR			=	1;
	public static final int ROADBLOCK	=	2;
	public static final int ROADBLOCK2	=	3;
	
	int interactiveId;
	
	int hitpoint;		
	int max_hitpoint;
	int pollutionRadius;
	float pollutionValue;
	
	AffineTransform at, at2;

	protected Ellipse2D pollutionCircle;				
	
	public InteractiveObstacle(Vector2f p, ImageIcon img, int interactiveId){
		super(p, img);
		isInteractive = true;
		isInfective	  = false;
			  	  
		this.interactiveId = interactiveId;
		
		switch(interactiveId){
			case TRASHCAN:
				max_hitpoint 	= 1000;
				pollutionRadius = 75;
				pollutionValue	= -0.05f;
				break;
				
			case CAR:
				max_hitpoint 	= 5000;
				pollutionRadius = 100;
				pollutionValue 	= -0.1f;
				break;
			
			case ROADBLOCK:
				max_hitpoint 	= 500;
				pollutionRadius = 50;
				pollutionValue	= -0.03f;
				break;
			
			case ROADBLOCK2:
				max_hitpoint 	= 100;
				pollutionRadius = 25;
				pollutionValue	= -0.01f;
				break;
		}
		
		hitpoint = max_hitpoint;
		
		at = new AffineTransform();
		at.setToTranslation( vPosition.x - width/2, vPosition.y - height/2 );
		
		at2 = new AffineTransform();
		
		pollutionCircle = new Ellipse2D.Double(vPosition.x - pollutionRadius, vPosition.y - pollutionRadius, pollutionRadius*2, pollutionRadius*2);
		gridManager.updateNeighborContext(this);
	}

	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);
	}
	
	@Override
	public Map<String, String> getAttributes(int i){
		
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrName = "ob_interactiveId"+i;
		value = String.valueOf(interactiveId);
		attrMap.put(attrName, value);
		
		attrName = "ob_type"+i;
		value = String.valueOf("InteractiveOb");
		attrMap.put(attrName, value);
				
		attrName = "ob_IMG"+i;
		
		String imgName = img.toString();
		int indexSlash = imgName.lastIndexOf('/');
		int len = imgName.length();
		
		value = String.valueOf(imgName.substring(indexSlash+1, len));
		attrMap.put(attrName, value);
		
		return attrMap;
	}
	
	private void transform(){
		at.setToIdentity();
		at2.setToIdentity();
		
		at.translate( vPosition.x , vPosition.y );		
		at.rotate(Math.toRadians((- (float)180/max_hitpoint) * (max_hitpoint-hitpoint)));
		at.translate(- width/2, -height/2);
		
		at2.translate( vPosition.x , vPosition.y );
		at2.scale(0.5f, 0.5f);
		at2.translate(- width/2, -height * 1.5);
	}
	
	public float getPollutionValue(){
		return pollutionValue;
	}
	
	public boolean isHitable(){
		return (hitpoint > 0);
	}
	
	public void hit(){
		if(hitpoint > 0){
			hitpoint--;
			transform();
			
			if(hitpoint == 0){
				gridManager.addNeighborhoodContext(this);
			}
		}	
	}
	
	public Rectangle2D getPollutionCircleBBox() {
		return pollutionCircle.getBounds2D();
	}
	
	@Override
	public void setRegion(){
		transform();
		super.setRegion();
		pollutionCircle = new Ellipse2D.Double(vPosition.x - pollutionRadius, vPosition.y - pollutionRadius, pollutionRadius*2, pollutionRadius*2);
	}
	
	@Override
	public void paint(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  //±Ä¥Î¬XÃä
		g.drawImage(img.getImage(), at, null);
		
		if(!isHitable()){
			g.drawImage(ImageLoader.getInstance().getGifImg(21).getImage(), at2, null);
		}
		
		//g.draw(region);    
		if(isSelected){
			g.drawImage(selected.getImage(), (int)(vPosition.x - selected.getIconWidth()/2),
					    (int)(vPosition.y - height/2 -selected.getIconHeight()-5), null);
			
			g.setColor(Color.LIGHT_GRAY);
			g.draw(pollutionCircle);
			drawNeighborHoodMark(g);
		}
	}
}	
