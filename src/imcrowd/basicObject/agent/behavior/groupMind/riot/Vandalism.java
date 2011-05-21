package imcrowd.basicObject.agent.behavior.groupMind.riot;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.basicObject.obstacle.InteractiveObstacle;
import imcrowd.io.imageIO.ImageLoader;

public class Vandalism extends Riot {
	private static Vandalism singleton = new Vandalism();
 
	
	private Vandalism(){
		behId = Riot.VANDALISM;
		img = ImageLoader.vandalismImg.getImage();
		imgScale = (float)24 / img.getWidth(null);
	}
	
	public static Riot getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		me.setFlockingWeight(State.SEPARATION, State.BROTHERS,	0);
		me.setFlockingWeight(State.SEPARATION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.SEPARATION, State.HOSTILE,	2);
		
		me.setFlockingWeight(State.COHESION, State.BROTHERS,	0);
		me.setFlockingWeight(State.COHESION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.COHESION, State.HOSTILE,		0);
		
		me.setFlockingWeight(State.ALIGNMENT, State.BROTHERS,	0);
		me.setFlockingWeight(State.ALIGNMENT, State.ALLIANCE,	0);
		me.setFlockingWeight(State.ALIGNMENT, State.HOSTILE,	0);
	}
	
	@Override
	public void action(NormalAgent me) {

		InteractiveObstacle spOb = (InteractiveObstacle)me.getTargetOb();
		if(spOb != null){
			if(spOb.isHitable()){
				me.behavior.vandalism(spOb, 1);
			}else{
				me.setTargetOb(null);
			}
		}
		
		changeState(me);
	}

	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		if(ag.getTargetOb() == ob){
			((InteractiveObstacle)ob).hit();
		}
		else{
			ag.behavior.collisionResponse(ob.getPosition(), 3);
		}	
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
		return false;
	}
}
