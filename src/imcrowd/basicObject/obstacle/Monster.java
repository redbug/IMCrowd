package imcrowd.basicObject.obstacle;

import imcrowd.basicObject.InfectiveSource;
import imcrowd.basicObject.agent.behavior.groupMind.Panic;
import imcrowd.basicObject.agent.boid.Boid;
import imcrowd.engine.Controller;
import imcrowd.io.imageIO.ImageLoader;

import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Properties;

import javax.vecmath.Vector2f;


public class Monster extends InfectiveObstacle{
	
	final public static int thresholdWeighting = 50; 
	
	public Monster(Vector2f p){	
		super(p);
		
		Controller	ct = Controller.getInstance();
		
		setInfectiveSource(
				ct.getAlpha(),
				ct.getBeta(),
				ct.getSpeparationI(),
				ct.getCohesionI(),
				ct.getAlignmentI()
		);
				
		img = ImageLoader.getInstance().getPngImg(10);
		
		width = (int)(img.getImage().getWidth(null));
		height = (int)(img.getImage().getHeight(null));
		
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
		value = String.valueOf("Monster");
		attrMap.put(attrName, value);
			
		return attrMap;
	}
	
	public void setInfectiveSource(int alpha, int beta, int iS, int iC, int iA){
		infectiveSource = new InfectiveSource(
				alpha,
				beta,
				new Boid(iS, iC, iA),
				new Panic(this.getPosition())
			);
	}
	
}
