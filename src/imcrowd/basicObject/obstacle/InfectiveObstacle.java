package imcrowd.basicObject.obstacle;

import java.util.Map;
import java.util.Properties;

import imcrowd.basicObject.InfectiveSource;
import javax.vecmath.Vector2f;


public abstract class InfectiveObstacle extends Obstacle{
	protected InfectiveSource infectiveSource;
	
	protected InfectiveObstacle(Vector2f p){	
		super(p);
		isInfective = true;
		isInteractive = false;
	}	
	
	public InfectiveSource getInfectiveSource(){
		return infectiveSource;
	}
		
	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);
	}
	
	@Override
	public Map<String, String> getAttributes(int i){
		
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrName = "ob_alpha"+i;
		value = String.valueOf(infectiveSource.getAlpha());
		attrMap.put(attrName, value);
		
		attrName = "ob_beta"+i;
		value = String.valueOf(infectiveSource.getBeta());
		attrMap.put(attrName, value);
		
		attrName = "ob_iSeparation"+i;
		value = String.valueOf(infectiveSource.getSeparationI());
		attrMap.put(attrName, value);

		attrName = "ob_iCohesion"+i;
		value = String.valueOf(infectiveSource.getCohesionI());
		attrMap.put(attrName, value);

		attrName = "ob_iAlignment"+i;
		value = String.valueOf(infectiveSource.getAlignmentI());
		attrMap.put(attrName, value);	
		
		return attrMap;
	}
	

}
