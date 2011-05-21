package imcrowd.strategy;

import imcrowd.basicObject.agent.Agent;

public class GateKeeperStrategy implements CommunicationStrategy{
	public GateKeeperStrategy(){
		
	}
	
	
	public int getSeparationWeight(Agent testAgent){
		return 0;
	}
	
	public int getCohesionWeight(Agent testAgent){
		return 0;
	}
	
	public int getAlignmentWeight(Agent testAgent){
		return 0;
	}
}
