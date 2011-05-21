package imcrowd.strategy;


import imcrowd.basicObject.agent.Agent;


public class DecreasingStrategy implements CommunicationStrategy{
	float generationWeight[] = {0.8f, 0.5f, 0.3f, 0.1f};
	public DecreasingStrategy(){
		
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
