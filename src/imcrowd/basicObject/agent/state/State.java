package imcrowd.basicObject.agent.state;

import java.awt.Color;
import java.awt.Graphics2D;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.strategy.CommunicationStrategy;
import imcrowd.strategy.ImitateStrategy;

public abstract class State {
	
	public static final int CLEAN 			= 0;
	public static final int LATENT			= 1;
	public static final int ENGAGED			= 2;
	public static final int DISENCHANTED 	= 3;
	 
	
	/********************************
	 * the indexes of weightFlocking
	 ********************************/
	public static final int	BROTHERS 	= 0, 	
							ALLIANCE	= 1,
							HOSTILE		= 2;

	public static final int	SEPARATION	= 0,
							COHESION	= 1,
							ALIGNMENT	= 2;
	
	/***************************************************
	 * the attributes belonging to State but not the agent.
	 ***************************************************/
	protected int stateId;
	protected Color color;
	protected float weightFlocking[][] = new float[3][3];		
	
	
	
	//TODO It should be initialized from GUI default setting.
	protected CommunicationStrategy communicationStrategy = new ImitateStrategy();    		
	
	public abstract boolean isDoAgentCollisionAvoidacne(Agent me);
	public abstract boolean isTargetSpOb();
	public abstract void loadFlockingWeights(Agent me);
	public abstract void action(NormalAgent me);
	public abstract void contactInView(NormalAgent me, Agent he);
	public abstract void contactInView(NormalAgent me, Obstacle ob);
	public abstract void contactInCircle(NormalAgent me, Agent he);
	public abstract void obCollisionResponse(Agent me, Obstacle ob);
	public abstract boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile);
	public abstract void reactToInvader(Agent me, Agent he, boolean isHostile);
	public abstract void drawFeatures(Graphics2D g, Agent ag);
	
	public abstract int getSeparationWeight(Agent me);
	public abstract int	getCohesionWeight(Agent me);
	public abstract int getAlignmentWeight(Agent me);
	
	public abstract void updateInfectiveSource(NormalAgent me); 	// evaluating emotional stimuli and update the state.  
	
	public float getFlockingWeight(int force, int relation){
		return weightFlocking[force][relation];
	}
	
	public void setFlockingWeight(int force, int relation, int value){
		weightFlocking[force][relation] = value; 
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getId(){
		return stateId;
	}
	
	
	/* CommunicationStrategy */
	public CommunicationStrategy getCommunicationStrategy() {
		return communicationStrategy;
	}

	public void setCommunicationStrategy(CommunicationStrategy communicationStrategy) {
		this.communicationStrategy = communicationStrategy;
	}
	

}
