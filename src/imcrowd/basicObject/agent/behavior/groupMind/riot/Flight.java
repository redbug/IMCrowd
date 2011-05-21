package imcrowd.basicObject.agent.behavior.groupMind.riot;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;


public class Flight extends Riot {
	private static Flight singleton = new Flight();
	
	final int TIMESTEP = 200;
	public static int counter = 0;
	
	private Flight(){
		behId = Riot.FLIGHT;	
		img = ImageLoader.evadingImg.getImage();
		imgScale = (float)24 / img.getWidth(null);
	}
	
	public static Riot getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		me.setFlockingWeight(State.SEPARATION, State.BROTHERS,	0);
		me.setFlockingWeight(State.SEPARATION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.SEPARATION, State.HOSTILE,	3);
		
		me.setFlockingWeight(State.COHESION, State.BROTHERS,	0);
		me.setFlockingWeight(State.COHESION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.COHESION, State.HOSTILE,		0);
		
		me.setFlockingWeight(State.ALIGNMENT, State.BROTHERS,	0);
		me.setFlockingWeight(State.ALIGNMENT, State.ALLIANCE,	0);
		me.setFlockingWeight(State.ALIGNMENT, State.HOSTILE,	0);
	}
	
	
	@Override
	public void action(NormalAgent me) {
		if(counter == 0){
			counter = TIMESTEP;
		}
		
		Agent target = me.getEvadeTarget();
		
		if(target == null){
			target = me.getNearestTarget();
		}
		
		if(target != null){
			//me.behavior.evade(target, 100);
			me.behavior.flee(target.getPosition(), 100, 100);
		}
		
		--counter;
		
		if(counter == 0){
			changeState(me);	
		}
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
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		if(isHostile){
			return me.agentCollisionRespondant(he, true);
		}else{
			return me.agentCollisionRespondant(he, false);
		}
	}
	
	
	@Override
	public boolean hasAgentCollisionAvoidacne(Agent me){
		return true;
	}
	
}
