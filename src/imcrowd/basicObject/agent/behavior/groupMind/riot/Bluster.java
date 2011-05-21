package imcrowd.basicObject.agent.behavior.groupMind.riot;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;

public class Bluster extends Riot {
	private static Bluster singleton = new Bluster();
	
	private Bluster(){
		behId = Riot.BLUSTER;
		img = ImageLoader.middleFingerImg.getImage();
		imgScale = 1;
	}
	
	public static Riot getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		
		me.setFlockingWeight(State.SEPARATION, State.BROTHERS,	0);
		me.setFlockingWeight(State.SEPARATION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.SEPARATION, State.HOSTILE,	2); // the confrontation will be more realistic while modifying the weight from 2 to 5. 
		
		/********************************************************************************
		 * When all the weights of the cohesion are set to zero, the avepos_own and 
		 * the avepos_hostile are both zero, too. Hence, the moveArooundRiot() in the 
		 * action function selects the wander behavior so that the confrontation shows 
		 * a little bit unstable. However, if all the weights were set to 1, the state 
		 * of the confrontation would never be broken.
		 ********************************************************************************/
		me.setFlockingWeight(State.COHESION, State.BROTHERS,	0);
		me.setFlockingWeight(State.COHESION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.COHESION, State.HOSTILE,		0);
		
		me.setFlockingWeight(State.ALIGNMENT, State.BROTHERS,	3);
		me.setFlockingWeight(State.ALIGNMENT, State.ALLIANCE,	2);
		me.setFlockingWeight(State.ALIGNMENT, State.HOSTILE,	0);
	}
	
	
	@Override
	public void action(NormalAgent me) {
		//me.behavior.stop(1);
		me.moveAroundRiot();
		changeState(me);
	}

	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		ag.behavior.collisionResponse(ob.getPosition(), 3);
	}
	
	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		return me.agentCollisionRespondant(he, true);
	}
	
	@Override
	public void descBetaCounter(Agent me) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean hasAgentCollisionAvoidacne(Agent me){
		return true;
	}
}
