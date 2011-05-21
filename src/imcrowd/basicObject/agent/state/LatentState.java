package imcrowd.basicObject.agent.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import javax.vecmath.Vector2f;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;

public class LatentState extends State {
	private static LatentState singleton = new LatentState();
	private Color stateColor = new Color(255, 255, 51, 100);
	
	private LatentState(){
		this.stateId = State.LATENT;
		this.color = Color.YELLOW;
	}
	
	public static State getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		weightFlocking[SEPARATION][BROTHERS] 	= 0.5f;
		weightFlocking[SEPARATION][ALLIANCE]	= 1f;
		weightFlocking[SEPARATION][HOSTILE]		= 5;
		
		weightFlocking[COHESION][BROTHERS]		= 1;
		weightFlocking[COHESION][ALLIANCE]		= 1;
		weightFlocking[COHESION][HOSTILE]		= 1;
		
		weightFlocking[ALIGNMENT][BROTHERS]		= 1;
		weightFlocking[ALIGNMENT][ALLIANCE]		= 0;
		weightFlocking[ALIGNMENT][HOSTILE]		= 0;
	}
	
	@Override
	public void action(NormalAgent ag) {
		ag.latentStateAction();
	}

	
	
	@Override
	public void contactInCircle(NormalAgent me, Agent he){
		me.getCollectiveBehavior().contactInCircle_Latent(me, he);
	}
	
	@Override
	public void contactInView(NormalAgent me, Obstacle ob) {
		if(!ob.isInfectant()){
			return;
		}
		
		me.getCollectiveBehavior().contactInView_Latent(me, ob);
	}
	
	
	@Override
	public void contactInView(NormalAgent me, Agent he) {
		me.getCollectiveBehavior().contactInView_Latent(me, he);
	}

	@Override
	public void obCollisionResponse(Agent ag, Obstacle ob){
		ag.behavior.collisionResponse(ob.getPosition(), 3);
	}
	
	@Override
	public void drawFeatures(Graphics2D g, Agent ag) {
		AffineTransform at = new AffineTransform();
		Image img = ImageLoader.getInstance().getGifImg(17).getImage();
		Vector2f pos = ag.getPosition();
		
		double imgWidth = img.getWidth(null) * 0.5 * 0.75;
		double imgHeight = img.getHeight(null) * 0.5 * 0.75;
		double tx = pos.x - imgWidth;
		double ty = pos.y - imgHeight;
		
		g.setColor(stateColor);
		g.fillOval((int)tx, (int)ty, (int)(imgWidth * 2), (int)(imgHeight * 2));
		
		at.translate(tx, ty );
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
		
		int num = ag.getNumGreen() + ag.getNumYellow();
		
		/***************************
		 * cooling down
		 ***************************/
		if( num == 0) {
			ag.descAlphaCounter();
		}
		/*****************************
		 * emotional stimuli
		 *****************************/
		else{
			for(int i=0; i < num; i++){
				ag.incrAlphaCounter();
			}
		}
		
		/****************************
		 * switch back to Clean state
		 ****************************/
		if(!ag.isEmotionArousal()){
			ag.setInfectant(false);
			ag.changeState(CleanState.getInstance());
		}
		/****************************
		 * switch to Engaged State
		 ****************************/
		else if(ag.isTakeAction()){
			ag.changeState(EngagedState.getInstance());
		}
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
