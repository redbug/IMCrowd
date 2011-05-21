package imcrowd.strategy;

import imcrowd.basicObject.agent.Agent;

public interface CommunicationStrategy {
	public int getSeparationWeight(Agent ag);
	public int getCohesionWeight(Agent ag);
	public int getAlignmentWeight(Agent ag);
}
