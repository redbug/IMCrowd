package imcrowd.basicObject.agent.state;

import java.awt.Color;
import java.awt.Graphics2D;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.Obstacle;

public class EngagedState extends State {
	private static EngagedState singleton = new EngagedState();
	
	private EngagedState(){
		this.stateId = State.ENGAGED;
		this.color = Color.GREEN;
	}
	
	
	public static State getInstance(){
		return singleton;
	}
		
	@Override
	public void updateInfectiveSource(NormalAgent me) {
		me.getCollectiveBehavior().descBetaCounter(me);
		
		if(me.isPurge()){
			me.setInfectant(false);
			me.changeState(DisenchantedState.getInstance());
			me.resetAlphaCounter();
		}
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		me.getCollectiveBehavior().loadFlockingWeights(me);
	}
	
	@Override
	public void action(NormalAgent me) {
		//me.getCollectiveBehavior().action(me);
		me.engagedAction();
	}

	
	@Override
	public void contactInCircle(NormalAgent me, Agent he){
		me.getCollectiveBehavior().contactInCircle_Engaged(me, he);
	}
	
	@Override
	public void contactInView(NormalAgent me, Agent he) {
		me.getCollectiveBehavior().contactInView_Engaged(me, he);
	}
	
	@Override
	public void contactInView(NormalAgent me, Obstacle ob) {

	}
	

	@Override
	public void obCollisionResponse(Agent me, Obstacle ob){
		me.getCollectiveBehavior().obCollisionResponse(me, ob);
	}
	
	@Override
	public void drawFeatures(Graphics2D g, Agent me){
		me.getCollectiveBehavior().drawFeatures(g, me);
	}

	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		return me.getCollectiveBehavior().agentCollisionRespondant(me, he, isHostile);
	}


	@Override
	public void reactToInvader(Agent me, Agent he, boolean isHostile) {
		me.getCollectiveBehavior().reactToInvader(me, he, isHostile);
	}
	

	@Override
	public int getAlignmentWeight(Agent me) {
		return communicationStrategy.getAlignmentWeight(me);
	}


	@Override
	public int getCohesionWeight(Agent me) {
		return communicationStrategy.getCohesionWeight(me);
	}


	@Override
	public int getSeparationWeight(Agent me) {
		return communicationStrategy.getSeparationWeight(me);
	}


	@Override
	public boolean isTargetSpOb() {
		return true;
	}

	@Override
	public boolean isDoAgentCollisionAvoidacne(Agent me) {
		return me.getCollectiveBehavior().hasAgentCollisionAvoidacne(me);
	}
	
	
}
