package imcrowd.basicObject.agent.state;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2f;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;


public class DisenchantedState extends State{
	private static DisenchantedState singleton = new DisenchantedState();
	
	private DisenchantedState(){
		this.stateId = State.DISENCHANTED;
		color = Color.LIGHT_GRAY;
	}
	
	public static State getInstance(){
		return singleton;
	}
	
	
	@Override
	public void loadFlockingWeights(Agent me){
		weightFlocking[SEPARATION][BROTHERS] = 1;
		weightFlocking[SEPARATION][ALLIANCE] = 3;
		weightFlocking[SEPARATION][HOSTILE]	 = 5;
		
		weightFlocking[COHESION][BROTHERS]	 = 0;
		weightFlocking[COHESION][ALLIANCE]	 = 0;
		weightFlocking[COHESION][HOSTILE]	 = 0;
		
		weightFlocking[ALIGNMENT][BROTHERS]	 = 0;
		weightFlocking[ALIGNMENT][ALLIANCE]	 = 0;
		weightFlocking[ALIGNMENT][HOSTILE]	 = 0;
	}
	
	@Override
	public void action(NormalAgent ag) {
		ag.disenchantedStateAction();
	}

	@Override
	public void contactInCircle(NormalAgent me, Agent he){
		
	}
	
	@Override
	public void contactInView(NormalAgent me, Agent he) {
	
	}
	
	@Override
	public void contactInView(NormalAgent me, Obstacle ob) {
	
	}
	
	
	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		ag.behavior.collisionResponse(ob.getPosition(), 3);
	}

	@Override
	public void drawFeatures(Graphics2D g, Agent ag) {
		Vector2f pos = ag.getPosition();
		
		g.setPaint(Color.BLUE);
		g.setStroke(new BasicStroke(2));
		g.drawString("x", (int)pos.x - 3, (int)pos.y - 8);
		
		AffineTransform at = new AffineTransform();
		Image img = ImageLoader.getInstance().getGifImg(17).getImage();
		
		at.translate(pos.x - (img.getWidth(null) * 0.5 * 0.75), (pos.y - img.getHeight(null) * 0.5 * 0.75));
		at.scale(0.75, 0.75);
		g.drawImage(img, at, null);
	}

	@Override
	public int getAlignmentWeight(Agent ag) {
		return ag.getAlignmentWeight();
	}


	@Override
	public int getCohesionWeight(Agent ag) {
		return ag.getCohesionWeight();
	}


	@Override
	public int getSeparationWeight(Agent ag) {
		return ag.getSeparationWeight();
	}

	@Override
	public void updateInfectiveSource(NormalAgent ag) {
		
	}

	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
		return me.agentCollisionRespondant(he, true);
	}

	@Override
	public void reactToInvader(Agent me, Agent he, boolean isHostile) {
		me.reactToInvader(he);
	}

	@Override
	public boolean isTargetSpOb() {
		return false;
	}
	
	@Override
	public boolean isDoAgentCollisionAvoidacne(Agent me) {
		return true;
	}

}
