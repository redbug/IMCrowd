package imcrowd.strategy;

import imcrowd.basicObject.agent.Agent;


public class ImitateStrategy implements CommunicationStrategy{
	
	public ImitateStrategy(){
		
	}
	
	
	public int getSeparationWeight(Agent ag){
		return ag.getSeparationI();
	}
	
	public int getCohesionWeight(Agent ag){
		return ag.getCohesionI();
	}
	
	public int getAlignmentWeight(Agent ag){
		return ag.getAlignmentI();
	}
	
}
