package imcrowd.basicObject.agent.normalAgent;


import imcrowd.basicObject.SocialSkills;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.riot.Riot;
import imcrowd.basicObject.agent.specialAgent.Police;
import imcrowd.basicObject.agent.state.State;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Properties;

import javax.vecmath.Vector2f;

public class Follower extends NormalAgent{
	boolean stepAside;
	
	Agent myLeader;
	
	public Follower(float x, float y, float theta, int groupId, Agent myLeader){
		
		super(x, y, theta, groupId);
		
		stepAside = false;
		this.myLeader = myLeader;
	}
	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);		
		
		socialSkill = new SocialSkills();
		socialSkill.setAttributes(configuration, i);
	}
	
	@Override
	public Map<String, String> getAttributes(int i){
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrMap.putAll(socialSkill.getAttributes(i));
		
		attrName = "ag_type"+i;
		value = String.valueOf("Follower");
		attrMap.put(attrName, value);
		
		attrName = "ag_myLeaderId"+i;
		value = String.valueOf(myLeader.getId());
		attrMap.put(attrName, value);
		
		return attrMap;
	}	
	
	
	
	@Override
	public void moveAroundRiot(){
		cleanStateAction();
	}
	
	
	public void cleanStateAction(){
		if(myLeader != null && myLeader.isAlive()){
			behavior.followLeader(myLeader, new Vector2f(-5, 0), 10);
		}
		else{
			goHome();
		}
	}
	
	public void latentStateAction(){
		cleanStateAction();
	}
	
	public void engagedAction(){
		collectiveBehavior.action(this);	
	}
	
	protected void dispatchAGoal(){
		
	}
	
	
	
	/************************************************************************
	 * collision avoidance, communication, flocking behavior
	 ************************************************************************/
	public BitSet interactWithOthers(ArrayList<Agent> agentList) {
		
		event.clear();
		
		boolean isCollisionResponce = false;
		int num_cohesion = 0;				
	
		float distanceMe2He;
		
		Vector2f aveHeading 		= new Vector2f(); 				// average velocity vector(for alignment)
		Vector2f avePosition 		= new Vector2f();	 			// average position of the same group
		Vector2f totalSeparation 	= new Vector2f();				// accumulate total separation force;
		Vector2f vecMe2He 			= new Vector2f();				// relative position (in global space)			

		float shortestHostileDistance = 1000;
		float shortestBullyDistance = 1000;
		Agent nearestHostileAgent = null;
		Agent nearestBullyAgent	= null;
		
		Agent he; 

		Vector2f hisPosition;
		Vector2f hisHeading;
						
		resetLocalInformation();
		
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
				
				boolean heIsBrother 	= false;
				boolean heIsAlliance 	= false;
				boolean heIsHostile  	= false;
				boolean heIsSpecial		= false;
				
				vecMe2He.set(0,0);
				
				hisPosition = he.getPosition();
				hisHeading = he.getHeading();
				
				vecMe2He.sub(vPosition, hisPosition);
				distanceMe2He = vecMe2He.length(); 
				
				/*********************************************************
				 * Calculate the number of each party in density circle. 
				 * (Counting myself into the number of the own party)
				 *********************************************************/
				if(isAgentInPerceptionCircle(he, distanceMe2He)){
					state.contactInCircle(this, he);
					
					if (this.colorId == he.getColorId()){
						
						if(this.groupId == he.getGroupId()){
							heIsBrother = true;
							num_brothers++;
							
							/*******************************
							 * step aside for my leader.
							 *******************************/
							if(myLeader != null && (he.getId() == myLeader.getId())){

								if(stepAside){
									accumulateSeparation(vecMe2He,
											 totalSeparation,
											 this.height, 2);
									stepAside = false;
								}
							}
						}	
						else{
							heIsAlliance = true;
							num_alliance++;							
						}
						
						
					}else{
						if(he.isSpecial()){
							if(he instanceof Police){
								this.setNearByPolice(true);
								
								if(this.getStateColor() == Color.GREEN){
									int behId = ((Riot)this.getCollectiveBehavior()).getId();
									if(behId != Riot.FLIGHT){
										behavior.flee(he.getPosition(), 50, 10);
									}
								}
								
							}
							heIsSpecial = true;
						}
						else{
							heIsHostile = true;
							num_hostile++;
							
							if(shortestHostileDistance > distanceMe2He){
								shortestHostileDistance = distanceMe2He;
								nearestHostileAgent = he;
							}
							
							
							if(he.getStateColor() == Color.GREEN){

								int behId = ((Riot)he.getCollectiveBehavior()).getId();
								if(behId == Riot.ASSAULT){
							
									if(shortestBullyDistance > distanceMe2He){
										shortestBullyDistance = distanceMe2He;
										nearestBullyAgent = he;
									}
								}
							}	
							
						}	
					}
				}	
				
				
				/***********************************************************
				 * He is also within my view field.
				 ***********************************************************/
				if (isAgentInView(he, distanceMe2He)) { 	 	
					
					state.contactInView(this, he);	
					
					
					/************************************************
					 * He is my brother
					 ************************************************/
					if (heIsBrother){

						num_cohesion++; 	
						
						// for computing cohesion
						avePosition.scaleAdd(state.getFlockingWeight(State.COHESION, State.BROTHERS), 
											 hisPosition, 
											 avePosition);
						
						// for computing alignment
						aveHeading.scaleAdd(state.getFlockingWeight(State.ALIGNMENT, State.BROTHERS),
											hisHeading, 
											aveHeading);
					
						// for computing separation
						accumulateSeparation(vecMe2He,
											 totalSeparation,
											 this.height, state.getFlockingWeight(State.SEPARATION, State.BROTHERS));
						
					}
					
					/************************************************
					 * He is my alliance
					 ************************************************/
					else if(heIsAlliance){					
							accumulateSeparation(vecMe2He,
												 totalSeparation, 
												 this.height, 
												 state.getFlockingWeight(State.SEPARATION, State.ALLIANCE));
					}
					
					/************************************************
					 * He is my hostile
					 ************************************************/
					else if(heIsHostile){
						accumulateSeparation(vecMe2He,
											 totalSeparation,
											 this.height,
											 state.getFlockingWeight(State.SEPARATION, State.HOSTILE));
					}					
				
					agentCollisionPredictor(he);

					isCollisionResponce = state.agentCollisionRespondant(this, he, heIsHostile);
									
					state.reactToInvader(this, he, heIsHostile);
					
				} 
				
			}	
		}
		
		
		if(nearestHostileAgent != null){
			setNearestTarget(nearestHostileAgent);
		}
		
		if(nearestBullyAgent != null){
			setEvadeTarget(nearestBullyAgent);
		}
		
		
		if( num_cohesion > 0){
			/******************************************************
			 * calculate the average position of the neighborhood
			 ******************************************************/
			avePosition.scale((float) 1 / num_cohesion);  								
			
			/******************************************************
			 * calculate the average heading of the neighborhood
			 ******************************************************/
			aveHeading.scale((float) 1 / num_cohesion); 						
		}	
				
		setNumSameParty(num_alliance + num_brothers + 1);
		setNumDiffParty(num_hostile);
		
		
		
		if(!isCollisionResponce){
			
			/********************************************
			 * Flocking
			 ********************************************/
			behavior.flocking(totalSeparation, avePosition, aveHeading, 5);
			

			/******************************************* 
			 * Collision Avoidance 
			 *******************************************/
			if(state.isDoAgentCollisionAvoidacne(this)){
				if(!this.isGoalInPerceptionCircle()){
					agentCollisionAvoidance();
				}	
			}	

		}	
		
		/* Obstacle Avoidance */
		interactWithObstacles();
		
		/* Infective Source Updating */
		state.updateInfectiveSource(this);				
		
		
				
		// TODO flow vector of the grids
		/* The flow vector which can affect the movement of agents */
		
		//Point2D flowgrid = gridManager.getFlowInGrid(testAgent);
		//Fs.add(new Vector2f((float)flowgrid.getX(), (float)flowgrid.getY(), 0.0f));
		//testAgent.setFa(Fs);
		
		return event;
	}
	
	public void setLeader(Leader ag){
		this.myLeader = ag;
	}
	
	public void stepAsideForLeader(){
		stepAside = true;
	}
	
	protected void drawBodyShape(Graphics2D g){
		super.drawBodyShape(g, false);
	}
	
	protected void drawTarget(Graphics2D g){
		g.setPaint(Color.BLUE);
		g.fill(behavior.getFollowLeaderTarget());
	}	
	
}
