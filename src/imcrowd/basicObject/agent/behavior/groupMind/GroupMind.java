package imcrowd.basicObject.agent.behavior.groupMind;

import java.awt.Graphics2D;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.Obstacle;


public interface GroupMind {
	public boolean hasAgentCollisionAvoidacne(Agent me);
	public void contactInCircle_Engaged(NormalAgent me, Agent he);
	public void contactInCircle_Latent(NormalAgent me, Agent he);
	public void contactInView_Engaged(NormalAgent me, Agent he);
	public void contactInView_Latent(NormalAgent me, Agent he);
	public void contactInView_Latent(NormalAgent me, Obstacle ob);
	public void obCollisionResponse(Agent me, Obstacle ob);
	public void drawFeatures(Graphics2D g, Agent me);
	public void action(NormalAgent me);
	public void loadFlockingWeights(Agent me);
	public void descBetaCounter(Agent me);
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile);
	public void reactToInvader(Agent me, Agent he, boolean isHostile);
	public String getName();
	public int getHesitateWeight();
}
