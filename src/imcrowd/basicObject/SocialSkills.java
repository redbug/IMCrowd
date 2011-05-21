package imcrowd.basicObject;

import imcrowd.engine.Engine;
import imcrowd.ui.ExperimentParameterTab;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class SocialSkills implements ConfigurationIO {
	public float salesman;
	public float marven;
	public float connector;
	public float total;
	
	Random rand = Engine.rand;
	
	ExperimentParameterTab param = ExperimentParameterTab.getInstance();
	
	static Logger logger = Logger.getLogger(SocialSkills.class);
	
	public SocialSkills(){
		logger.setLevel(Level.INFO); 
		
		float mean, sd;
		
		/********************************
		 * 			salesman
		 ********************************/
		mean 	= param.getParameterValue(ExperimentParameterTab.SOCIAL_SALESMAN_MEAN) * 0.1f;
		sd 		= param.getParameterValue(ExperimentParameterTab.SOCIAL_SALESMAN_SD) * 0.1f ;
		
		salesman = 	(float)(rand.nextGaussian() * sd) + mean;		//normal distribution[MEAN,STANDER_DEVIATION]
		salesman = (salesman < 0)? 0: ((salesman > 1)? 1: salesman);
		logger.debug("   salesman:"+salesman);
		
		/********************************
		 * 			marven
		 ********************************/		
		mean 	= param.getParameterValue(ExperimentParameterTab.SOCIAL_MARVEN_MEAN) * 0.1f;
		sd 		= param.getParameterValue(ExperimentParameterTab.SOCIAL_MARVEN_SD) * 0.1f;
		
		marven = 	(float)(rand.nextGaussian() * sd) + mean;			//normal distribution[MEAN,STANDER_DEVIATION]
		marven = (marven < 0)? 0: ((marven > 1)? 1: marven);
		logger.debug("   marven:"+marven);
		
		
		/********************************
		 * 			connnector
		 ********************************/
		mean 	= param.getParameterValue(ExperimentParameterTab.SOCIAL_CONNECTOR_MEAN) * 0.1f;
		sd 		= param.getParameterValue(ExperimentParameterTab.SOCIAL_CONNECTOR_SD) * 0.1f;
		
		connector = (float)(rand.nextGaussian() * sd) + mean;		//normal distribution[MEAN,STANDER_DEVIATION]
		connector = (connector < 0)? 0: ((connector > 1)? 1: connector);
		logger.debug("   connnector:"+connector);
		
		total = salesman + marven + connector;
	}
	
	public void setAttributes(Properties configuration, int i){
		String str;
		
		str = configuration.getProperty("ag_salesman"+i);
		salesman = Float.valueOf(str);
		
		str = configuration.getProperty("ag_marven"+i);
		marven = Float.valueOf(str);
		
		str = configuration.getProperty("ag_connector"+i);
		connector = Float.valueOf(str);
		
		str = configuration.getProperty("ag_total"+i);
		total = Float.valueOf(str);
	}
	
	public Map<String, String> getAttributes(int i){
		Map<String, String> attrMap = new HashMap<String, String>();
		String attrName, value;
		
		attrName = "ag_salesman"+i;
		value = String.valueOf(salesman);
		attrMap.put(attrName, value);
		
		attrName = "ag_marven"+i;
		value = String.valueOf(marven);
		attrMap.put(attrName, value);
		
		attrName = "ag_connector"+i;
		value = String.valueOf(connector);
		attrMap.put(attrName, value);
		
		attrName = "ag_total"+i;
		value = String.valueOf(total);
		attrMap.put(attrName, value);
		
		return attrMap;
	}	
	
	
	
	public float getTotal(){
		return total;
	}
}
