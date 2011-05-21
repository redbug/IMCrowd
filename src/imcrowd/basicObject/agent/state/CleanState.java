package imcrowd.basicObject.agent.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2f;

import imcrowd.basicObject.InfectiveSource;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.InfectiveObstacle;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;

public class CleanState extends State {
	private static CleanState singleton = new CleanState();
	
	private CleanState(){
		this.stateId = State.CLEAN;
		this.color = Color.WHITE;
	}
	

	public static State getInstance(){
		return singleton;
	}
	
	@Override
	public void loadFlockingWeights(Agent me){
		weightFlocking[SEPARATION][BROTHERS] = 1;
		weightFlocking[SEPARATION][ALLIANCE] = 3;
		weightFlocking[SEPARATION][HOSTILE]	 = 5;
		
		weightFlocking[COHESION][BROTHERS]	 = 1;
		weightFlocking[COHESION][ALLIANCE]	 = 0;
		weightFlocking[COHESION][HOSTILE]	 = 0;
		
		weightFlocking[ALIGNMENT][BROTHERS]	 = 1;
		weightFlocking[ALIGNMENT][ALLIANCE]	 = 0;
		weightFlocking[ALIGNMENT][HOSTILE]	 = 0;
	}
	
	
	@Override
	public void action(NormalAgent ag) {
		ag.cleanStateAction();
	}

	
	@Override
	public void contactInCircle(NormalAgent me, Agent he){
		
	}
	
	
	@Override
	public void contactInView(NormalAgent me, Obstacle ob){
		
		if(!ob.isInfectant()){
			return;
		}
		
		InfectiveSource mySrc  = me.getInfectiveSource();
		
		if(mySrc == null){
			InfectiveSource src = ((InfectiveObstacle)ob).getInfectiveSource();
			
			me.setInfectiveSource((InfectiveSource)src.clone());
			me.getInfectiveSource().setHostAgent(me);
			me.initializeInfectiveSource();			
			me.setInfectant(true);	
		}
		
		else{
			me.addOneYellow();		
		}
		
	}
	
	@Override
	public void contactInView(NormalAgent me, Agent he) {
		
		/**************************************************************
		 * Just copy the infective source from another agent.
		 * Don't need to count both the number of green and yellow agents.
		 **************************************************************/
		InfectiveSource mySrc  = me.getInfectiveSource();
		InfectiveSource hisSrc = he.getInfectiveSource();
		
		if(mySrc == null){
			if(he.isInfectant()){
				me.setInfectiveSource((InfectiveSource)hisSrc.clone());
				me.getInfectiveSource().setHostAgent(me);
				me.initializeInfectiveSource();					
				me.setInfectant(true);	
			}
			
//			if(!he.isSpecial()){
//				Color c = he.getStateColor();
//				if(c != Color.GREEN && c != Color.YELLOW){
//					return;
//				}
//			}
			
		
		}
		
		else{
			
			/*************************************************
			 * count both the number of green and yellow agents
			 *************************************************/
			
			if(he.isSpecial() && he.getInfectiveSource() != null){
				me.addOneYellow();
			}
			else{
			
				Color c = he.getStateColor();
				
				if(c == Color.GREEN){
					me.addOneGreen();
				}
				else if(c == Color.YELLOW){
					me.addOneYellow();
				}
			}		
		}
		
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
		if(ag.getInfectiveSource() != null){
			
			int num = ag.getNumGreen() + ag.getNumYellow();
			
			if( num == 0) {
				ag.descAlphaCounter();
			}else{
				for(int i=0; i < num; i++){
					ag.incrAlphaCounter();
				}
			}
					
			if(ag.isEmotionArousal()){
				ag.changeState(LatentState.getInstance());
			}
		}
		
	}

	@Override
	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile){
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
