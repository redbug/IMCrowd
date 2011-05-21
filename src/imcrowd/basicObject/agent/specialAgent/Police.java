package imcrowd.basicObject.agent.specialAgent;
import imcrowd.basicObject.agent.Agent;

import imcrowd.basicObject.goal.Goal;

import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.engine.Engine;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.manager.AgentManager;
import imcrowd.manager.GridManager;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.vecmath.Vector2f;

public class Police extends SpecialAgent{
			
	GridManager gridManager;
	
	public Police(float x, float y, float theta, int groupId){	
		super(x, y, theta, groupId);
		
		isInfectant = false;
		infectiveSource = null;
		
		img = ImageLoader.getInstance().getGifImg(13).getImage();
		imgScale = 0.9f;
		
		width = (int)(img.getWidth(null) * imgScale);
		height = (int)(img.getHeight(null) * imgScale);		

		point_TopLeft = new float[2];
		point_TopLeft[0] = vPosition.x - (float)width/2;
		point_TopLeft[1] = vPosition.y - (float)height/2;
		personalSpace 	= new Ellipse2D.Double(point_TopLeft[0], point_TopLeft[1], width, height);
		
		name = "Police";
	
		gridManager = Engine.getInstance().getGridManager();
		
	}
	
	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);
	}	
	
	@Override
	public Map<String, String> getAttributes(int i){
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrName = "ag_type"+i;
		value = String.valueOf(name);
		attrMap.put(attrName, value);
		
		return attrMap;
	}
	
	public void cleanStateAction(){}
	public void latentStateAction(){}
	public void consensusStateAction(){}
	public void engagedAction(){}
	public void disenchantedStateAction(){}
	
	protected void action()
	{
		state.loadFlockingWeights(this);
		
		Vector2f target;
		
		//behavior.wander(10);	//the base line strategy in the experiments of my thesis
		
		if((target = gridManager.queryBestPosition(this)) != null){
			behavior.arrive(target, 2, 10);
		}
		else{
			approachToGoal();
			//behavior.wander(5);
		}	
		
	}
	
	
	protected void dispatchAGoal(){

		 if(goalManager.isEmpty()){
			 goal = null;
		 }
		 else{ 
			 if(goal == null){
			     goal = goalManager.getNewGoal();    
			 }
			 else if (isReachGoal(goal)){
				 
				 /**********************************************************************
				  * if the number of goals > 1, I will get an another goal
				  * if the number of goal = 1, I will keep the same goal (do nothing) 
				  ********************************************************************/
				 if(goalManager.size() > 1){
					 Goal newGoal;
					 do{
						 newGoal = goalManager.getNewGoal();
					 }while(newGoal.equals(goal));
					 lastGoal = goal;
					 goal = newGoal;		 
				 }
			 }
		 }
	}
	
	
	
	public BitSet interactWithOthers(ArrayList<Agent> agentList){
				
		event.clear();
		
		boolean isCollisionResponce = false;
		float distanceMe2He;
		
		Vector2f totalSeparation = new Vector2f();			// accumulate total separation force;
		Vector2f vecMe2He = new Vector2f();				// relative position (in global space)			
		
		Agent he; 
	
		Vector2f hisPosition;
		
//		resetLocalInformation();
		
		if(agentList != null) {	
			
			/**********************************************
			 * check out other agents.
			 **********************************************/
			for (int i = 0; i < agentList.size(); i++) {		
				
				he = agentList.get(i);	
				
				if (he == this)
					continue;				
				
				if(!he.isAlive()){
					if(he == nearestTarget){
						nearestTarget = null;
					}
					if(he == evadeTarget){
						evadeTarget = null;
					}
					continue;
				}
				
				vecMe2He.set(0,0);
				
				hisPosition = he.getPosition();	
				
				vecMe2He.sub(vPosition, hisPosition);
				distanceMe2He = vecMe2He.length(); 
				
				/***********************************************************
				 * He is within my view field.
				 ***********************************************************/
				if (isAgentInView(he, distanceMe2He)) { 	 	
						
									
//				accumulateSeparation(vecMe2He, 
//									 totalSeparation, 
//									 this.height, 
//									 state.getFlockingWeight(State.SEPARATION, State.ALLIANCE));
				
				event.set(AgentManager.AM_INVIEW); 
			
//				agentCollisionPredictor(he);

				isCollisionResponce = agentCollisionRespondant(he, true);
								
//				reactToInvader(he);
					
					
				} 
				
			}	
		}
			
		
//		else{
//			if(!isCollisionResponce){
//				
//				/********************************************
//				 * Flocking
//				 ********************************************/
//				behavior.separation(totalSeparation, 5);
//				
//				/******************************************* 
//				 * Collision Avoidance 
//				 *******************************************/	
//				agentCollisionAvoidance();
//	
//			}	
//		}	
		
		

		/* Collision avoidance */
		interactWithObstacles();
		
				
		// TODO flow vector of the grids
		/* The flow vector which can affect the movement of agents */
		
		//Point2D flowgrid = gridManager.getFlowInGrid(testAgent);
		//Fs.add(new Vector2f((float)flowgrid.getX(), (float)flowgrid.getY(), 0.0f));
		//testAgent.setFa(Fs);
		
		return event;		// didn't collision and agentInView.
		
		
	}
	
	@Override
	protected void interactWithObstacles(){
		List<Obstacle> obList = engine.getObstacleManager().getObList();
		Obstacle ob;
		obsInView.clear();
		
		for (int i = 0; i < obList.size(); i++) {
			ob = obList.get(i);

			if (isObstacleInView(ob)) {				
				/*****************************************
				 * collide with an obstacle
				 *****************************************/
				if (testObstacleCollision(ob)) {
					event.set(AgentManager.AM_COLLISION_OB);
					state.obCollisionResponse(this, ob);
				}
			}
		}
	}
	
	
	
}
