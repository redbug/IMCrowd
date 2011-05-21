package imcrowd.basicObject.agent.normalAgent;


import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.BitSet;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import imcrowd.basicObject.SocialSkills;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.riot.Riot;
import imcrowd.basicObject.agent.specialAgent.Police;
import imcrowd.basicObject.agent.state.State;
import imcrowd.basicObject.goal.Goal;



import javax.vecmath.Vector2f;


public class Leader extends NormalAgent{
	List<Follower> followerList;
	
	final int NUM_GOAL = 5;
	
	protected int achievedGoal;
	boolean isWorkDone;
	
	/**********************************************
	 * Average cohesion position
	 **********************************************/
	Vector2f avePos_alliance;							// average position of alliance
	Vector2f avePos_hostile;							// average position of hostile
	
	public Leader(float x, float y, float theta, int groupId){
		
		super(x, y, theta, groupId);
		
		achievedGoal = 0;
		isWorkDone = false;
		
		followerList = new ArrayList<Follower>();
		
		avePos_alliance = new Vector2f();		
		avePos_hostile = new Vector2f();				
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
		value = String.valueOf("Leader");
		attrMap.put(attrName, value);	
		
		return attrMap;
	}	
	
	
	public void addFollower(Follower follower){
		followerList.add(follower);
	}
	
	public void cleanFollower(){
		
		for(Follower f: followerList){
			f.setLeader(null);
		}
		
		followerList.clear();
	}
	
	
	@Override
	public void moveAroundRiot(){
		Vector2f target = new Vector2f();
		Vector2f mp_ownParty = new Vector2f();
		Vector2f mp_hostileParty = new Vector2f();	
		
		
		/**************************************************
		 * compute the center point of the alliance
		 **************************************************/
		if(avePos_alliance.x != 0 && avePos_alliance.y != 0){
			mp_ownParty.add(vPosition, avePos_alliance);
			mp_ownParty.scale(0.5f);
			target.add(mp_ownParty);
			
			
			/**************************************************
			 * compute the center point of the hostile
			 **************************************************/
			if(avePos_hostile.x != 0 && avePos_hostile.y != 0){
				mp_hostileParty.add(vPosition, avePos_hostile);
				mp_hostileParty.scale(0.5f);
				
				/***************************************************
				 *  (3 * mp_ownParty + 1 * mp_hostileParty) / 4
				 ***************************************************/
				target.scale(3);
				target.add(mp_hostileParty);
				
				target.scale(0.25f);
			}
			
			behavior.arrive(target, 2, 10);
		}
		else{
			behavior.wander(10);
		}
		
	}
	

	
	public void cleanStateAction(){
		if(isWorkDone){
			goHome();
		}
		else{
			approachToGoal();
		}	
	}
		
	public void latentStateAction(){
		cleanStateAction();
	}
	
	public void engagedAction(){
		if(isWorkDone){
			goHome();
		}
		else{
			collectiveBehavior.action(this);
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
				 
				 achievedGoal++;
				 
				 if(achievedGoal >= NUM_GOAL){
					 isWorkDone = true;
					 return;
				 }
				 
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
	
	public void resetLocalInformation(){
		super.resetLocalInformation();

		avePos_alliance.set(0,0);
		avePos_hostile.set(0,0);
	}
	
	
	/************************************************************************
	 * collision avoidance, communication, flocking behavior
	 ************************************************************************/
	public BitSet interactWithOthers(ArrayList<Agent> agentList) {
		
		event.clear();
		
		boolean isCollisionResponce = false;
		 
		float brothersDistance = 0;
		float distanceMe2He;
		
		Vector2f totalSeparation = new Vector2f();			// accumulate total separation force;
		Vector2f vecMe2He = new Vector2f();				// relative position(global space)			
		
		Agent he;
		
		float shortestDistance = 1000;
		float shortestBullyDistance = 1000;
		Agent nearestHostileAgent = null;
		Agent nearestBullyAgent	= null;
		
		Vector2f hisPosition;
		
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
				
				vecMe2He.sub(this.vPosition, hisPosition);
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
							
							/*********************************************************
							 * Computing group distance
							 *********************************************************/
							brothersDistance += distanceMe2He;
						}	
						else{
							heIsAlliance = true;
							num_alliance++;
							
							avePos_alliance.scaleAdd(state.getFlockingWeight(State.COHESION, State.ALLIANCE),
													 hisPosition, 
													 avePos_alliance);
							
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
							
							
							if(shortestDistance > distanceMe2He){
								shortestDistance = distanceMe2He;
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
							
							
							avePos_hostile.scaleAdd(state.getFlockingWeight(State.COHESION, State.HOSTILE),
													hisPosition, 
													avePos_hostile);
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
						//do nothing
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
					
					
					if(he.getGroupId() == this.groupId){	
						if(!isSafety(he.getPosition())){
							((Follower)he).stepAsideForLeader();
						}	
					}
					else{
						state.reactToInvader(this, he, heIsHostile);
					}
					
				}
			}
		}
		
		
		if(nearestHostileAgent != null){
			setNearestTarget(nearestHostileAgent);
		}
		
		if(nearestBullyAgent != null){
			setEvadeTarget(nearestBullyAgent);
		}
				
		
		if(num_alliance > 0){
			avePos_alliance.scale((float) 1 / num_alliance);				
		}
		
		if( num_hostile > 0){
			avePos_hostile.scale((float) 1 / num_hostile);
		}
		
		if(brothersDistance > 40 ){
			behavior.slowDown(10);
		}	
		
		setNumSameParty(num_alliance + num_brothers + 1);
		setNumDiffParty(num_hostile);
					

		
		if(!isCollisionResponce){
			
			/********************************************
			 * Flocking
			 ********************************************/
			behavior.separation(totalSeparation, 5);
			
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
	
	protected void drawBodyShape(Graphics2D g){
		super.drawBodyShape(g, true);
	}
	
	/****************************************************************
	 * plot both average positions of the alliances and the hostile
	 ****************************************************************/
	protected void drawTarget(Graphics2D g){
		
		if(avePos_alliance != null){
			g.setPaint(Color.ORANGE);
			g.fillOval((int)avePos_alliance.x, (int)avePos_alliance.y, 10, 10);			
		}

		if(avePos_hostile != null){
			g.setPaint(Color.CYAN);
			g.fillOval((int)avePos_hostile.x, (int)avePos_hostile.y, 10, 10);
		}
		
	}
	
}
