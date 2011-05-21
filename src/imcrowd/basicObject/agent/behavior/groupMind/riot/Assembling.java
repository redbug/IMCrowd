package imcrowd.basicObject.agent.behavior.groupMind.riot;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;


public class Assembling extends Riot {
	private static Assembling singleton = new Assembling();
	
	private Assembling(){
		behId = Riot.ASSEMBLING;
		img = ImageLoader.getInstance().getGifImg(18).getImage();
		imgScale = (float)24 / img.getWidth(null);
	}
	
	public static Riot getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		me.setFlockingWeight(State.SEPARATION, State.BROTHERS,	1);
		me.setFlockingWeight(State.SEPARATION, State.ALLIANCE,	1);
		me.setFlockingWeight(State.SEPARATION, State.HOSTILE,	5);
		
		me.setFlockingWeight(State.COHESION, State.BROTHERS,	1);
		me.setFlockingWeight(State.COHESION, State.ALLIANCE,	1);
		me.setFlockingWeight(State.COHESION, State.HOSTILE,		1);
		
		me.setFlockingWeight(State.ALIGNMENT, State.BROTHERS,	1);
		me.setFlockingWeight(State.ALIGNMENT, State.ALLIANCE,	1);
		me.setFlockingWeight(State.ALIGNMENT, State.HOSTILE,	0);
	}
	
	
	@Override
	public void action(NormalAgent me) {
		me.moveAroundRiot();
		changeState(me);
	}
	
	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		
		return me.agentCollisionRespondant(he, true);

		
//		if(isHostile){
//			return me.agentCollisionRespondant(he, true);
//		}else{
//			return me.agentCollisionRespondant(he, false);
//		}
	}
	
	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		ag.behavior.collisionResponse(ob.getPosition(), 3);
	}

	@Override
	public void descBetaCounter(Agent me) {
		me.descBetaCounter();
	}
	
	@Override
	public boolean hasAgentCollisionAvoidacne(Agent me){
		return false;
	}

}
