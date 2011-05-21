package imcrowd.strategy;

import imcrowd.basicObject.agent.Agent;


public class RationalStrategy extends ImitateStrategy{	
	
	public RationalStrategy(){
		
	}
		
	public int getSeparationWeight(Agent ag){
		float w = ag.getCurrentRationValue();
		return Math.round( w 	* ag.getSeparationWeight() + 
						  (1-w) * ag.getSeparationI());	
	}
	
	public int getCohesionWeight(Agent ag){
		float w = ag.getCurrentRationValue();
		return Math.round( w 	* ag.getCohesionWeight() + 
						  (1-w) * ag.getCohesionI());	
	}
	
	public int getAlignmentWeight(Agent ag){
		float w = ag.getCurrentRationValue();
		return Math.round( w 	* ag.getAlignmentWeight() + 
						  (1-w) * ag.getAlignmentI());	
	}
}
