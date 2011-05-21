package imcrowd.basicObject.agent.behavior.groupMind;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2f;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.obstacle.Monster;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;

public class Panic implements GroupMind{
	
	Vector2f target;
	Image img;
	float imgScale;
	
	public static final int PANIC 			= 0;
	
	public final int wEscape				= 5;
	
	
	public Panic(Vector2f target){
		this.target = target;
		img = ImageLoader.scaryImg.getImage();
		imgScale = (float)24 / img.getWidth(null);
	}
	
	@Override
	public void action(NormalAgent me) {
		me.behavior.speedUp(5000);
		me.behavior.flee(target, 1000, 1);
	}

	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		return me.agentCollisionRespondant(he, true);
	}

	@Override
	public void contactInCircle_Engaged(NormalAgent me, Agent he) {
		Color c = he.getStateColor();
		
		if(c == Color.GREEN){
			me.addBeta(wEscape, PANIC);
		}
		
	}

	@Override
	public void contactInCircle_Latent(NormalAgent me, Agent he) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contactInView_Engaged(NormalAgent me, Agent he) {

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
		me.addNGreen(Monster.thresholdWeighting);
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
		ag.behavior.collisionResponse(ob.getPosition(), 3);
	}

	@Override
	public void reactToInvader(Agent me, Agent he, boolean isHostile) {
		me.reactToInvader(he);
	}
	
	@Override
	public void drawFeatures(Graphics2D g, Agent ag) {
		Vector2f pos = ag.getPosition();
	
		AffineTransform at = new AffineTransform();
		at.translate(pos.x - (img.getWidth(null) * 0.5 * imgScale), (pos.y - img.getHeight(null) * 0.5 * imgScale));
		at.scale(imgScale, imgScale);
		g.drawImage(img, at, null);
	}

	@Override
	public void descBetaCounter(Agent me) {
		me.descBetaCounter();
	}

	@Override
	public String getName() {
		return "Panic";
	}
	
	@Override
	public boolean hasAgentCollisionAvoidacne(Agent me){
		return true;
	}

	@Override
	public int getHesitateWeight() {
		return 0;
	}
	
}
