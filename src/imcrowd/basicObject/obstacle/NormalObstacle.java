package imcrowd.basicObject.obstacle;

import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.vecmath.Vector2f;

public class NormalObstacle extends Obstacle{
	public NormalObstacle(Vector2f p, ImageIcon img){
		super(p, img);
		isInteractive = false;
		isInfective	  = false;
		
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
		
		
		attrName = "ob_type"+i;
		value = String.valueOf("NormalOb");
		attrMap.put(attrName, value);
		
		attrName = "ob_IMG"+i;
		
		String imgName = img.toString();
		int indexSlash = imgName.lastIndexOf('/');
		int len = imgName.length();
		
		value = String.valueOf(imgName.substring(indexSlash+1, len));
		attrMap.put(attrName, value);
		
		return attrMap;
	}
}
