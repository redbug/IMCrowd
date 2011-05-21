package imcrowd.basicObject.agent.boid;

import imcrowd.basicObject.ConfigurationIO;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Boid implements ConfigurationIO{
	/* Reynolds Model. */ 
	int wSeparation;		
	int wCohesion;			
	int wAlignment;			
	
	public Boid(int s, int c, int a){
		this.wSeparation 	= s;
		this.wCohesion 		= c;
		this.wAlignment 	= a;
	}
	
	public void setAttributes(Properties configuration, int i){
		
		String str;
		
		str = configuration.getProperty("ag_wSeparation"+i);
		wSeparation = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_wCohesion"+i);
		wCohesion = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_wAlignment"+i);
		wAlignment = Integer.valueOf(str);
	}	
	
	public Map<String, String> getAttributes(int i){
		Map<String, String> attrMap = new HashMap<String, String>();
		String attrName, value;
		
		attrName = "ag_wSeparation"+i;
		value = String.valueOf(wSeparation);
		attrMap.put(attrName, value);
		
		attrName = "ag_wCohesion"+i;
		value = String.valueOf(wCohesion);
		attrMap.put(attrName, value);
		
		attrName = "ag_wAlignment"+i;
		value = String.valueOf(wAlignment);
		attrMap.put(attrName, value);
		
		return attrMap;
	}
	
	
	/* Separation Force */
	public int getSeparationWeight(){
		return wSeparation;
	}
	
	public void setSeparationWeight(int s){
		wSeparation = s;
	}
	
	/* Cohesion Force */
	public int getCohesionWeight(){
		return wCohesion;
	}
	
	public void setCohesionWeight(int c){
		wCohesion = c;
	}
	
	/* Alignment Force */
	public int getAlignmentWeight(){
		return wAlignment;
	}
	
	public void setAlignmentWeight(int a){
		wAlignment = a;
	}
}
