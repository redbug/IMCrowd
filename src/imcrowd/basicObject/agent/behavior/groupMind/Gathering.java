package imcrowd.basicObject.agent.behavior.groupMind;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.vecmath.Vector2f;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.basicObject.obstacle.StreetPerformance;
import imcrowd.io.imageIO.ImageLoader;

public class Gathering implements GroupMind {
	
	Vector2f target;
	Image img;
	public static final int GATHERING 		= 0;
	
	public final int wGathering				= 5;
	
	public Gathering(Vector2f target){
		this.target = target;
		img = ImageLoader.getInstance().getGifImg(19).getImage();
	}
	
	@Override
	public void action(NormalAgent me) {
		me.behavior.arrive(target, 1, 1);
	}

	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		return me.agentCollisionRespondant(he, true);
	}

	@Override
	public void contactInCircle_Engaged(NormalAgent me, Agent he) {
		
		Color c = he.getStateColor();
		
		if(c == Color.GREEN){
			me.addBeta(wGathering, GATHERING);
		}
		
	}

	@Override
	public void contactInCircle_Latent(NormalAgent me, Agent he) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contactInView_Engaged(NormalAgent me, Agent he) {
		Color c = he.getStateColor();
		
		if(c == Color.YELLOW){
			me.addOneYellow();
		}
		
		else if(c == Color.GREEN){
			me.addOneGreen();
		}
	}
	
	
	@Override
	public void contactInView_Latent(NormalAgent me, Agent he) {

		/*************************************************
		 * count both number of green and yellow agents
		 *************************************************/		
		Color c = he.getStateColor();
		
		if(c == Color.YELLOW){
			me.addOneYellow();
		}
		
		else if(c == Color.GREEN){
			me.addOneGreen();
		}
	}
	
	@Override
	public void contactInView_Latent(NormalAgent me, Obstacle ob){
		me.addOneYellow();
		me.addNGreen(StreetPerformance.thresholdWeighting);
	}


	@Override
	public void loadFlockingWeights(Agent me) {
		me.setFlockingWeight(State.SEPARATION, State.BROTHERS,	0);
		me.setFlockingWeight(State.SEPARATION, State.ALLIANCE,	0);
		me.setFlockingWeight(State.SEPARATION, State.HOSTILE,	0);
		
		me.setFlockingWeight(State.COHESION, State.BROTHERS,	2);
		me.setFlockingWeight(State.COHESION, State.ALLIANCE,	1);
		me.setFlockingWeight(State.COHESION, State.HOSTILE,		0);
		
		me.setFlockingWeight(State.ALIGNMENT, State.BROTHERS,	2);
		me.setFlockingWeight(State.ALIGNMENT, State.ALLIANCE,	1);
		me.setFlockingWeight(State.ALIGNMENT, State.HOSTILE,	0);
	}

	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		if(ag.getTargetOb() == ob){
			return;
		}
		
		ag.behavior.collisionResponse(ob.getPosition(), 3);
			
	}

	@Override
	public void reactToInvader(Agent me, Agent he, boolean isHostile) {
		me.reactToInvader(he);
	}
	
	@Override
	public void drawFeatures(Graphics2D g, Agent ag) {

		Vector2f pos = ag.getPosition();
		
		g.drawImage(img,
					(int)(pos.x - img.getWidth(null)  * 0.5),
					(int)(pos.y - img.getHeight(null) * 0.5),
					null);
	}

	@Override
	public void descBetaCounter(Agent me) {
		me.descBetaCounter();
	}

	@Override
	public String getName() {
		return "Gathering";
	}

	@Override
	public boolean hasAgentCollisionAvoidacne(Agent me){
		return false;
	}

	@Override
	public int getHesitateWeight() {
		return 20;
	} 
}
