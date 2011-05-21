package imcrowd.basicObject.agent.specialAgent;

import imcrowd.basicObject.InfectiveSource;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.riot.Assembling;
import imcrowd.basicObject.agent.boid.Boid;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.goal.Goal;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.manager.AgentManager;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Properties;


import javax.vecmath.Vector2f;

public class Agitator extends SpecialAgent{
	
	
	
	public Agitator(float x, float y, float theta, int groupId){	
		super(x, y, theta, groupId);
		
		isInfectant = true;
		
		setInfectiveSource(
				ct.getAlpha(),
				ct.getBeta(),
				ct.getSpeparationI(),
				ct.getCohesionI(),
				ct.getAlignmentI()
		);
				
		img = ImageLoader.agitatorImg.getImage();
		imgScale = (float)24 / img.getWidth(null);
		
		width = (int)(img.getWidth(null) * imgScale);
		height = (int)(img.getHeight(null) * imgScale);
		

		
		point_TopLeft = new float[2];
		point_TopLeft[0] = vPosition.x - (float)width/2;
		point_TopLeft[1] = vPosition.y - (float)height/2;
		personalSpace 	= new Ellipse2D.Double(point_TopLeft[0], point_TopLeft[1], width, height);
		
		name = "Agitator";
	}
	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);
		
		String str;
		
		int infectSrc[] = new int[5];
		
		str = configuration.getProperty("Alpha");
		infectSrc[0] = Integer.valueOf(str);
	
		str = configuration.getProperty("Beta");
		infectSrc[1] = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_iSeparation"+i);
		infectSrc[2] = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_iCohesion"+i);
		infectSrc[3] = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_iAlignment"+i);
		infectSrc[4] = Integer.valueOf(str);
		
		setInfectiveSource(
				infectSrc[0],
				infectSrc[1],
				infectSrc[2],
				infectSrc[3],
				infectSrc[4]
		);
	}
	
	
	
	@Override
	public Map<String, String> getAttributes(int i){
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrName = "ag_type"+i;
		value = String.valueOf(name);
		attrMap.put(attrName, value);
		
		attrName = "ag_alpha"+i;
		value = String.valueOf(infectiveSource.getAlpha());
		attrMap.put(attrName, value);
		
		attrName = "ag_beta"+i;
		value = String.valueOf(infectiveSource.getBeta());
		attrMap.put(attrName, value);
		
		attrName = "ag_iSeparation"+i;
		value = String.valueOf(infectiveSource.getSeparationI());
		attrMap.put(attrName, value);

		attrName = "ag_iCohesion"+i;
		value = String.valueOf(infectiveSource.getCohesionI());
		attrMap.put(attrName, value);

		attrName = "ag_iAlignment"+i;
		value = String.valueOf(infectiveSource.getAlignmentI());
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
		approachToGoal();
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
		Vector2f vecMe2He = new Vector2f();				// relative position(in global space)			
		
		Agent he; 
	
		Vector2f hisPosition;
		
		if(agentList != null) {	
			
			/**********************************************
			 * check out other agents.
			 **********************************************/
			for (int i = 0; i < agentList.size(); i++) {		
				
				he = agentList.get(i);	
				
				if (he == this)
					continue;				
				
				if(!he.isAlive())
					continue;
				
				vecMe2He.set(0,0);
				
				hisPosition = he.getPosition();	
				
				vecMe2He.sub(vPosition, hisPosition);
				distanceMe2He = vecMe2He.length(); 
				
				/***********************************************************
				 * He is within my view field.
				 ***********************************************************/
				if (isAgentInView(he, distanceMe2He)) { 	 	
					
							
					accumulateSeparation(vecMe2He, 
										 totalSeparation, 
										 this.height, 
										 state.getFlockingWeight(State.SEPARATION, State.ALLIANCE));
					
					event.set(AgentManager.AM_INVIEW); 
				
					agentCollisionPredictor(he);

					isCollisionResponce = agentCollisionRespondant(he, true);
									
					reactToInvader(he);
					
				}
				
			}
		}	
			
		if(!isCollisionResponce){
			
			/********************************************
			 * Flocking
			 ********************************************/
			behavior.separation(totalSeparation, 5);
			
			/******************************************* 
			 * Collision Avoidance 
			 *******************************************/	
			agentCollisionAvoidance();

		}	

		/* Collision avoidance */
		interactWithObstacles();
		
				
		// TODO flow vector of the grids
		/* The flow vector which can affect the movement of agents */
		
		//Point2D flowgrid = gridManager.getFlowInGrid(testAgent);
		//Fs.add(new Vector2f((float)flowgrid.getX(), (float)flowgrid.getY(), 0.0f));
		//testAgent.setFa(Fs);
		
		return event;		
	}
	
	public void setInfectiveSource(int alpha, int beta, int iS, int iC, int iA){
		infectiveSource = new InfectiveSource(
				alpha,
				beta,
				new Boid(iS, iC, iA),
				Assembling.getInstance()
			);
	}
	
	
}
