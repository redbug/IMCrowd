package imcrowd.basicObject.agent.behavior.groupMind.riot;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;

public class Assault extends Riot {
	private static Assault singleton = new Assault();
	
	
	
	private Assault(){
		behId = Riot.ASSAULT;
		img = ImageLoader.chasingImg.getImage();
		imgScale = (float)24 / img.getWidth(null);
	}
	
	public static Riot getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		me.setFlockingWeight(State.SEPARATION, State.BROTHERS,	0);
		me.setFlockingWeight(State.SEPARATION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.SEPARATION, State.HOSTILE,	0);
		
		me.setFlockingWeight(State.COHESION, State.BROTHERS,	0);
		me.setFlockingWeight(State.COHESION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.COHESION, State.HOSTILE,		0);
		
		me.setFlockingWeight(State.ALIGNMENT, State.BROTHERS,	0);
		me.setFlockingWeight(State.ALIGNMENT, State.ALLIANCE,	0);
		me.setFlockingWeight(State.ALIGNMENT, State.HOSTILE,	0);
	}
	
	
	
	@Override
	public void action(NormalAgent me) {
		
		Agent target = me.getNearestTarget();
		
		if(target != null){				
			if(me.testAttackable(target)){			
				target.hitBy(me);
			}
			//me.behavior.arrive(target.getPosition(), 1, 1);
			me.behavior.pursuit(target, 10);
		}
		else{
			me.changeCollectiveBehavior(Assembling.getInstance());
		}
		
		changeState(me);
			
	}
 
	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		if(isHostile){	
			return me.agentCollisionRespondant(he, false);
		}else{
			return me.agentCollisionRespondant(he, true);
		}
	}
	
	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		ag.behavior.collisionResponse(ob.getPosition(), 3);
	}

	@Override
	public void descBetaCounter(Agent me) {
			
	}
	
	@Override
	public boolean hasAgentCollisionAvoidacne(Agent me){
		return false;
	}

	
}
