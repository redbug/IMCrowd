package imcrowd.basicObject.agent.behavior.individualMind;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.MovingObject;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.engine.Engine;
import imcrowd.util.Transformation;

import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2f;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Behavior implements Cloneable{

	final float TOLERANT_ERR = 0.00001f;
	
	/************************************
	 * the properties for arrival
	 ************************************/
	int activeF;					//active force, the initial value comes from the control panel.
	
	/************************************
	 * the properties for wander
	 ************************************/
	final int WANDER_JITTER =			500;	//the jitter range for the wander target.  
	final int WANDER_RADIUS	=			30;		//the radius of the wander circle.
	final int WANDER_DISTANCE =			50;		//the shift distance of the wander circle.
//	final int WANDER_TIME_CONSTRAINT = 	10;		//the constraint for the wander timer. 
	
//	int wanderTimer;					//a time span for a wander target.
	Vector2f wanderTarget_b4Shift;		//the wander target which before shifting to WANDER_DISTANCE. 
	Vector2f wanderTarget;				//the wander target.
	Vector2f wanderCircleCenter;		//the circle center of the wander circle.
	Ellipse2D wanderCircle;				//the circle where wander target move around.
	
	Ellipse2D followLeaderTarget;	
	
	/*****************************************
	 * the properties for obstacleAvoidance
	 *****************************************/ 
	final float BREAKING_WEIGHT = 0.2f;
	final float LATERAL_WEIGHT	= 3;
	final int SAFTETY_DIST_TO_OBSTACLE = 4;
	
	
	/*****************************************
	 * the properties for collisionAvoidance
	 *****************************************/
	final int COLLISION_RESPONSE_WEIGHT = 100;
//	final int safetyDisToCollide = 4;
	
	
	final int EVADE_DISTANCE = 300;
	
	/*******************************************************************************
	 * Primitive Behaviors
	 * 		The order is sorted by the priority of all the primitive behaviors.
	 *******************************************************************************/
	final String BEHAVIOR_TYPE_ARRAY[] = 
			{"speedUp", 			"slowDown", 			"stop",
			 "flee",				"evade",				"collisionResponse",	"obstacleAvoidance",				
			 "sideCA",				"headOnCA",				"rearEndCA",
			 "seek", 			 	"arrive", 				"separation",
			 "alignment", 			"cohesion",				"wander",
			 "flocking",			"followLeader",			"pursuit", 				"vandalize"
			 };
	
	/******************************************************************************************
	 * Cooling down:
	 * The time span which an agent takes to recharge after that a behavior has been activated.
	 * The behavior will not be allowed to activate again during the cooling down period.
	 ******************************************************************************************/
	final int COOLDOWN_ARRAY[] =
			{0,					0,						20,
			 0,					0,						5,						0,
			 0,					10,						0,
			 0,					0,						0,
			 0,					0,						0,
			 0,					0,						0,						0
			 };
	
	/****************************************************************************
	 * Weights of steering behaviors
	 * (mutable)
	 ****************************************************************************/
	int behaviorWeight[] =
			{0,					0,						0,
			 5,					5,						1,						1,
			 1,					3,						1,
			 1,					1,						1,
			 1,					1,						10,
			 1,					3,						1,						10
			 };
	
	
	int sId;
	int cId;
	int aId;
	int slowDownId;
	int speedUpId;
	int stopId;
	int headOnCAId;
	int sideCAId;
	int rearEndCAId;
	int seekId;
	int arriveId;
	int wanderId;
	int obstacleAvoidanceId;
	int collisionResponseId;
	int followLeaderId;
	int pursuitId;
	int evadeId;
	int vandalizeId;
	
	Transformation transformation;
	
	private Map<String, Integer> 	behaviorIdMap;						//mapping behavior names to behavior IDs.
	private Map<Integer, String>	behaviorNameMap;					//mapping behavior IDS to names.
	private boolean 				behaviorActivation[];				//tracking the activation state of all behaviors.
	private int						behaviorTimeStep[];					//tracking the performing periods of all behaviors.
	private int 					behaviorCooldown[];					//tracking the current cooling-down value of all behaviors.
	private Vector2f 				behaviorSteeringForce[];			//tracking the steering forces of all behaviors.
	   			   	
	private Agent hostAgent;
	private Agent myLeader = null;
	
	private Agent targetAgent;
	private Obstacle targetOb;
	
	private Vector2f totalSteeringForce;
//	private Vector2f shiftVec;
	
	private Vector2f offsetLeader;
	
	static Logger logger = Logger.getLogger(Behavior.class);
	
	
	
	public Behavior(Agent ag) {
		hostAgent = ag;
		
		int behNum = BEHAVIOR_TYPE_ARRAY.length;
		
		behaviorIdMap 				= new HashMap<String, Integer>();
		behaviorNameMap				= new HashMap<Integer, String>();
		behaviorActivation 			= new boolean[behNum];
		behaviorTimeStep			= new int[behNum];			
		behaviorCooldown			= new int[behNum];			
		behaviorSteeringForce		= new Vector2f[behNum];
		
		for(int i = 0; i < behNum; i++){
			behaviorIdMap.put(BEHAVIOR_TYPE_ARRAY[i], i);
			behaviorNameMap.put(i, BEHAVIOR_TYPE_ARRAY[i]);
			behaviorActivation[i] 		= false;
			behaviorTimeStep[i] 		= 0;
			behaviorCooldown[i]			= 0;
			behaviorSteeringForce[i]	= new Vector2f();
		}
	  	
		sId					= behaviorIdMap.get("separation");
		cId					= behaviorIdMap.get("cohesion");
		aId					= behaviorIdMap.get("alignment");
		slowDownId			= behaviorIdMap.get("slowDown");
		speedUpId			= behaviorIdMap.get("speedUp");
		stopId				= behaviorIdMap.get("stop");
		headOnCAId			= behaviorIdMap.get("headOnCA");
		sideCAId			= behaviorIdMap.get("sideCA");
		rearEndCAId			= behaviorIdMap.get("rearEndCA");
		seekId				= behaviorIdMap.get("seek");
		arriveId			= behaviorIdMap.get("arrive");
		wanderId			= behaviorIdMap.get("wander");
		obstacleAvoidanceId = behaviorIdMap.get("obstacleAvoidance");
		collisionResponseId	= behaviorIdMap.get("collisionResponse");
		followLeaderId		= behaviorIdMap.get("followLeader");
		pursuitId			= behaviorIdMap.get("pursuit");
		evadeId				= behaviorIdMap.get("evade");
		vandalizeId			= behaviorIdMap.get("vandalize");
		
	  	totalSteeringForce 	= new Vector2f();
//	  	shiftVec			= new Vector2f();
	  	
        double theta = Engine.rand.nextFloat() * 2 * Math.PI;	             
     	wanderTarget_b4Shift = new Vector2f((float)(WANDER_RADIUS * Math.cos(theta)), (float)(WANDER_RADIUS * Math.sin(theta)));
     	wanderTarget = new Vector2f();
//	  	wanderTimer = 0;
     	wanderCircleCenter = new Vector2f();
     	wanderCircle = new Ellipse2D.Double();
     	followLeaderTarget = new Ellipse2D.Double();	  	
     	transformation = new Transformation();
		
		logger.setLevel(Level.DEBUG);
	}

	
	private boolean isBehaviorActived(int bId){
		return behaviorActivation[bId];
	}
	
	private boolean isBehaviorAvailable(int bId){
		if(behaviorCooldown[bId] == 0)
			return true;
		else
			return false;
	}
	
	private boolean isValid(int bId){
		if(!isBehaviorActived(bId) && isBehaviorAvailable(bId))
			return true;
		else
			return false;
	}
		
	private void activateBehavior(int bId, int timeStep){
		behaviorActivation[bId] = true;
		behaviorTimeStep[bId] 	= timeStep;
	}
	
	private void inactivateBehavior(int bId){
		behaviorActivation[bId] = false;
		behaviorTimeStep[bId]	= 0;
		behaviorSteeringForce[bId].set(0,0);
		behaviorCooldown[bId]	= COOLDOWN_ARRAY[bId];		//cooling down
		
		if(bId == pursuitId){		
			hostAgent.setNearestTarget(null);
		}else if(bId == evadeId){
			hostAgent.setEvadeTarget(null);
		}else if(bId == vandalizeId){
			hostAgent.setTargetOb(null);
		}
	}
	
	private void updateBehaviorTS(int bId){	
		if(behaviorTimeStep[bId] == 0){
			inactivateBehavior(bId);	//inactive the behavior.
			return;
		}
		else{
			behaviorTimeStep[bId]--;
		}
	}
	
	private void update(){
		for(int i = 0; i < behaviorActivation.length; i++){
			//update the performing period of the behavior
			if(isBehaviorActived(i)){
				updateBehaviorTS(i);
			}
			
			//update the current cooling-down value of the behavior
			if(behaviorCooldown[i] != 0){
				behaviorCooldown[i]--;
			}	
			
			int w = 1000;
			if(i == sId){
				w = hostAgent.getState().getSeparationWeight(hostAgent);
			}else if(i == cId){
				w = hostAgent.getState().getCohesionWeight(hostAgent);
			}else if(i == aId){
				w = hostAgent.getState().getAlignmentWeight(hostAgent);
			}
			
			if(w != 1000){
				if(w >= 0){
					behaviorWeight[i] = w;
				}
				else{
					w = 0;
				}
			}
			
		}
	}
	
	public void inactivateAllCA(){
		inactivateBehavior(rearEndCAId);
		inactivateBehavior(headOnCAId);
		inactivateBehavior(sideCAId);
	}
	
	
	/****************************************
	 * the stopping behavior
	 ****************************************/
	public boolean stop(int timeStep){
		int bId = behaviorIdMap.get("stop");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
			
			/***************************************************
			 * inactive the slow-down and the speed-up behavior
			 ***************************************************/			
			if(isBehaviorActived(slowDownId)){
				inactivateBehavior(slowDownId);
			}
			if(isBehaviorActived(speedUpId)){
				inactivateBehavior(slowDownId);
			}
			
			return true;
		}
		else{
			return false;
		}	
	}
	
	/****************************************
	 * the speed-up behavior
	 ****************************************/	
	public boolean speedUp(int timeStep){
		/**********************************************************
		 * the speed-up behavior is mutual exclusive with the 
		 * slowDown behavior and the stop behavior.
		 **********************************************************/
		if(isBehaviorActived(slowDownId) || isBehaviorActived(stopId))
			return false;
		
		int bId = behaviorIdMap.get("speedUp");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/****************************************
	 * the slow-down behavior
	 ****************************************/
	public boolean slowDown(int timeStep){
		int bId = behaviorIdMap.get("slowDown");
		if(isValid(bId)){
			if(isBehaviorActived(stopId))
				return false;

			activateBehavior(bId, timeStep);
			
			if(isBehaviorActived(speedUpId)){
				inactivateBehavior(speedUpId);
			}
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/****************************************
	 * the seeking behavior
	 ****************************************/
	public boolean seek(Vector2f target, int timeStep)
	{
		int bId = behaviorIdMap.get("seek");
		
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
		
			Vector2f desiredVelocity = new Vector2f(target);
			desiredVelocity.sub(hostAgent.getPosition());
			
			if(desiredVelocity.length() > TOLERANT_ERR){
				desiredVelocity.normalize();
				desiredVelocity.scale(hostAgent.getMaxSpeed());
				desiredVelocity.sub(hostAgent.getVelocity());
			}else{
				desiredVelocity.set(0,0);
			}
		
			behaviorSteeringForce[bId].set(desiredVelocity);

			return true;
		}
		else{
			return false;
		}	
	}
	

	
	/****************************************
	 * the fleeing behavior
	 ****************************************/	
	public boolean flee(Vector2f target, int panicDistance, int timeStep)
	{
		int bId = behaviorIdMap.get("flee");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);

			Vector2f desiredVelocity = new Vector2f(hostAgent.getPosition());
			desiredVelocity.sub(target);
			
			if(desiredVelocity.lengthSquared() > panicDistance * panicDistance) {
				behaviorSteeringForce[bId].set(0,0);
			}
			else{
				
				if(desiredVelocity.length() > TOLERANT_ERR){
					desiredVelocity.normalize();
					desiredVelocity.scale(hostAgent.getMaxSpeed());
				}else{
					desiredVelocity.set(0,0);
				}
				
				desiredVelocity.sub(hostAgent.getVelocity());
				
				behaviorSteeringForce[bId].set(desiredVelocity);
			}	
			
			return true;
		}
		else{
			return false;
		}
	}
	
	private void pursuitLoop(){

		Vector2f target;
		Vector2f toTargetAg = new Vector2f();
		toTargetAg.sub(targetAgent.getPosition(), hostAgent.getPosition());
		
		  
		double relativeHeading = hostAgent.getHeading().dot(targetAgent.getHeading()); 
		
		/*********************************************************************
		 * The pursuit target is ahead and facing me then I can just seek
		 * for the its current position.
		 *********************************************************************/		
		if( toTargetAg.dot(hostAgent.getHeading()) > 0 &&
		relativeHeading < Math.cos(160)){
			  
			  target = targetAgent.getPosition();
		}
		/*******************************************************************************
		 * Otherwise, I should predict where the target will be.
		 * the lookahead time is proportional to the distance between the target and me;
		 * and is inversely proportional to the sum of the target's and my velocities 
		 *******************************************************************************/
		else{
			float lookAheadTime = toTargetAg.length() / (hostAgent.getMaxSpeed() + targetAgent.getSpeed());
			target = new Vector2f(targetAgent.getVelocity());
			target.scaleAdd(lookAheadTime, targetAgent.getPosition());
		}

		seek(target, 1);
		
		int seekId = behaviorIdMap.get("seek");
		
		behaviorSteeringForce[pursuitId].set(behaviorSteeringForce[seekId]);
		inactivateBehavior(seekId);
	}
	
	private void vandalize_loop(){
		arrive(targetOb.getPosition(), 1, 1);
		
		behaviorSteeringForce[vandalizeId].set(behaviorSteeringForce[arriveId]);
		inactivateBehavior(arriveId);
	}
	
	public boolean vandalism(Obstacle ob, int timeStep){
		
		int bId = behaviorIdMap.get("vandalize");
		if(isValid(bId)){		
			targetOb = ob;
			activateBehavior(bId, timeStep);
			vandalize_loop();
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public boolean pursuit(Agent targetAg, int timeStep){
				
		int bId = behaviorIdMap.get("pursuit");
		if(isValid(bId)){			
			activateBehavior(bId, timeStep);
			targetAgent = targetAg;
			pursuitLoop();
			
			return true;
		}
		else{
			return false;
		}
	}
	
	private void evadeLoop(){
		Vector2f target = new Vector2f();
		Vector2f toPursuer = new Vector2f();
		
		toPursuer.sub(targetAgent.getPosition(), hostAgent.getPosition());
		if (toPursuer.lengthSquared()> EVADE_DISTANCE * EVADE_DISTANCE){
			behaviorSteeringForce[evadeId].set(target);
		}	
		else{		  
			float lookAheadTime = toPursuer.length() / (hostAgent.getMaxSpeed() + targetAgent.getSpeed());
			target.set(targetAgent.getVelocity());
			target.scaleAdd(lookAheadTime, targetAgent.getPosition());
			flee(target, EVADE_DISTANCE, 1);
			
			int fleeId = behaviorIdMap.get("flee");
			behaviorSteeringForce[evadeId].set(behaviorSteeringForce[fleeId]);
			inactivateBehavior(fleeId);
		}	
		
	}
	
	public boolean evade(Agent targetAg, int timeStep){
		
		int bId = behaviorIdMap.get("evade");
		if(isValid(bId)){			
			activateBehavior(bId, timeStep);
			targetAgent = targetAg;
			evadeLoop();
			
			return true;
		}
		else{
			return false;
		}
		
	}
	
	
	
	/****************************************
	 * the arrival behavior
	 ****************************************/	
	public boolean arrive(Vector2f target, int deceleration, int timeStep)
	{	  
		int bId = behaviorIdMap.get("arrive");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);

			Vector2f toTarget = new Vector2f();
			toTarget.sub(target, hostAgent.getPosition());
			  
			final float decelerationTweaker = 0.8f;

			float dist = toTarget.length();
			float speed =  dist / (deceleration * decelerationTweaker);
			  
			speed = hostAgent.truncateSpeed(speed);
			  
			Vector2f desiredVelocity = new Vector2f();
			desiredVelocity.scale(speed/dist, toTarget);
			desiredVelocity.sub(hostAgent.getVelocity());
			behaviorSteeringForce[bId].set(desiredVelocity);

			return true;
		}
		else{
			return false;
		}
				
	}
	
	
	/****************************************
	 * the wander behavior
	 ****************************************/	
	public boolean wander(int timeStep)
	{ 
		int bId = behaviorIdMap.get("wander");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);

			Vector2f desiredVelocity = new Vector2f();
			desiredVelocity.sub(calculateWanderTarget(), hostAgent.getPosition());
			
			if(desiredVelocity.length() > TOLERANT_ERR){
				desiredVelocity.normalize();
			}else{
				desiredVelocity.set(0,0);
			}	
			
			behaviorSteeringForce[bId].set(desiredVelocity);
			
			return true;
		}
		else{
			return false;
		}	
	}

	/********************************************
	 *  the helper function of the wander().
	 ********************************************/
	public Vector2f calculateWanderTarget(){
		float JitterThisTimeSlice = WANDER_JITTER * Agent.TIMESTEP;
		 
		wanderTarget_b4Shift.add(new Vector2f( Engine.RandomClamped()* JitterThisTimeSlice,
				  						 Engine.RandomClamped() * JitterThisTimeSlice));
		
		if(wanderTarget_b4Shift.length() > TOLERANT_ERR){
			wanderTarget_b4Shift.normalize();
			wanderTarget_b4Shift.scale(WANDER_RADIUS); 
		}else{
			wanderTarget_b4Shift.set(0,0);
		}
		
		wanderTarget.set(0,0);
		wanderTarget.add(wanderTarget_b4Shift, new Vector2f(WANDER_DISTANCE, 0));
		
		transformation.reset();
		  
		wanderTarget = 
			  transformation.pointToWorldSpace(wanderTarget, hostAgent.getHeading(), hostAgent.getSide(), hostAgent.getPosition());
		  
		return wanderTarget;
	}
	
	public Vector2f getWanderTarget(){
		return wanderTarget;
	}

	
	/*******************************************************************************
	 * For painting the wander circle.
	 * (The display of the wander circle would lag behind the position it really is,
	 *  if you weren't called this function in paint() of the canvas.) 
	 *******************************************************************************/
	public Vector2f getWanderCircleCenter(){
		wanderCircleCenter.set(hostAgent.getHeading());
		wanderCircleCenter.scaleAdd(WANDER_DISTANCE, hostAgent.getPosition());
		
		return wanderCircleCenter;
	}
	
	public Ellipse2D getWanderCircle() {
		wanderCircle.setFrame(wanderCircleCenter.x - WANDER_RADIUS, 
							  wanderCircleCenter.y - WANDER_RADIUS, 
							  WANDER_RADIUS * 2,
							  WANDER_RADIUS * 2);
		return wanderCircle;
	}
	
	public boolean hasLeader(){
		return (myLeader != null);
	}
	
	public Agent getMyLeader(){
		return myLeader;
	}
	
	
	
	public Ellipse2D getFollowLeaderTarget() {
		return followLeaderTarget;
	}
		
	public boolean followLeader(Agent leader, Vector2f offset, int timeStep)
	{
		this.myLeader = leader;
		this.offsetLeader = offset;
		
		int bId = behaviorIdMap.get("followLeader");
		
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
			offsetFollow();
			return true;
		}
		else{
			return false;
		}	
	}
	
	
	private void offsetFollow(){
		transformation.reset();
		Vector2f worldOffsetPos = transformation.pointToWorldSpace(offsetLeader,
                    myLeader.getHeading(),
                    myLeader.getSide(),
                    myLeader.getPosition());

		Vector2f toOffset = new Vector2f();
		toOffset.sub(worldOffsetPos, hostAgent.getPosition());

		float lookAheadTime = toOffset.length() / (hostAgent.getSpeed() + myLeader.getSpeed());

		/* target = worldOffsetPos + leader.velocity * lookAheadTime */
		Vector2f target = new Vector2f(myLeader.getVelocity());
		target.scaleAdd(lookAheadTime, worldOffsetPos);
		followLeaderTarget.setFrame(target.x, target.y, 3, 3);		
		arrive(target, 10, 10);			
		
		behaviorSteeringForce[followLeaderId].set(behaviorSteeringForce[arriveId]);
		inactivateBehavior(arriveId);
	}
	
	
	
	
	
	/****************************************
	 * the separation behavior
	 ****************************************/	
	public boolean separation(Vector2f vectorToAgent, int timeStep){		
		int bId = behaviorIdMap.get("separation");
		if(isValid(bId)){			
			activateBehavior(bId, timeStep);
			vectorToAgent.scale(10 /* * hostAgent.speed / hostAgent.getMaxSpeed()*/);
			behaviorSteeringForce[bId].set(vectorToAgent);
		
			return true;
		}
		else{
			return false;
		}
		
	}
	
	
	/****************************************
	 * the cohesion behavior
	 ****************************************/
	public boolean cohesion(Vector2f avePos, int timeStep){
		int bId = behaviorIdMap.get("cohesion");
		if(isValid(bId)){			
			activateBehavior(bId, timeStep);
			seek(avePos, timeStep);
			
			int seekId = behaviorIdMap.get("seek");
			
			if(behaviorSteeringForce[seekId].length() > TOLERANT_ERR){
				behaviorSteeringForce[seekId].normalize();
			}else{
				behaviorSteeringForce[seekId].set(0,0);
			}	
			
			behaviorSteeringForce[bId].set(behaviorSteeringForce[seekId]);
			inactivateBehavior(seekId);

			return true;
		}
		else{
			return false;
		}
	}
	 
	
	/****************************************
	 * the alignment behavior
	 ****************************************/
	public boolean alignment(Vector2f aveVel, int timeStep){
		int bId = behaviorIdMap.get("alignment");
		if(isValid(bId)){			
			activateBehavior(bId, timeStep);

			Vector2f alignmentForce = new Vector2f();
			alignmentForce.sub(aveVel, hostAgent.getHeading());
			
			behaviorSteeringForce[bId].set(alignmentForce);
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public boolean flocking(Vector2f repulseForce, Vector2f avePos, Vector2f aveHeading, int timeStep){
		int bId = behaviorIdMap.get("flocking");
		if(isValid(bId)){			
			
			activateBehavior(bId, timeStep);
			
			if(repulseForce.length() != 0){
				separation(repulseForce, timeStep);
			}
			
			if(avePos.length() != 0){
				cohesion(avePos, timeStep);
			}
			
			if(aveHeading.length() != 0){
				alignment(aveHeading, timeStep);
			}	
			
			return true;
		}
		else{ 
			return false;
		}
	}
	
	
	/****************************************
	 * the collision-respondence behavior
	 ****************************************/
	public boolean collisionResponse(Vector2f collideePos, int timeStep){
		int bId = behaviorIdMap.get("collisionResponse");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);

			
			Vector2f localPosOfCollide = caculateLocalPosOfCollide(collideePos);
			Vector2f collisionResponseForce = new Vector2f();
			Vector2f shiftVec = new Vector2f();
			
			if(localPosOfCollide.x >= 0){
				//calculating the lateral force
				
				float r = Engine.rand.nextFloat();
				
				if(localPosOfCollide.y >= 0){
					collisionResponseForce.y = COLLISION_RESPONSE_WEIGHT * -1 ;
					shiftVec.set(r * -0.25f, r * -0.25f);
				}	
				else{
					collisionResponseForce.y = COLLISION_RESPONSE_WEIGHT;
					shiftVec.set(r * -0.25f, r * 0.25f);
				}		
				//calculating the braking force
				collisionResponseForce.x = -30;	
			}
			
			transformation.reset();
			shiftVec = transformation.vectorToWorldSpace(shiftVec, hostAgent.getHeading(), hostAgent.getSide());
			hostAgent.shiftPosition(shiftVec);	//shift a little bit.
			
			transformation.reset();
			collisionResponseForce = transformation.vectorToWorldSpace(collisionResponseForce, hostAgent.getHeading(), hostAgent.getSide());
			
			behaviorSteeringForce[bId].set(collisionResponseForce);
	
			return true;
		}
		else{
			return false;
		}
	}
	
	public void offCollisionResponse(){
		inactivateBehavior(collisionResponseId);
	}
	
	
	/***********************************************************
	 * collision-avoidance strategy for the rear-end collision 
	 ************************************************************/
	public boolean rearEndCA(Agent collidee, int timeStep){
		int bId = behaviorIdMap.get("rearEndCA");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
			slowDown(100);

			
//			Vector2f collideePosition = collidee.getPosition();
//			int collideeWidth = collidee.getWidth();
//			int collideeId = collidee.getId();			
//			if(hostAgent.getSpeed() > collidee.getSpeed()){
//				if(hostAgent.getId() < collideeId){			
//					slowDown(100);
//				}
//			}
			
			
//			Vector2f localPosOfCollide = caculateLocalPosOfCollide(collideePosition);
//			
//			headOnCA(localPosOfCollide, collideeWidth, timeStep);
//			
//			behaviorSteeringForce[bId].set(behaviorSteeringForce[headOnCAId]);
//	
//			inactivateBehavior(headOnCAId);
			
			return true;
		}
		else{
			return false;
		}		
	}
	
	
	/***********************************************************
	 * collision-avoidance strategy for the side collision 
	 ************************************************************/
	public boolean sideCA(Agent collidee, int timeStep){
		int bId = behaviorIdMap.get("sideCA");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
			
			Vector2f collideePosition = collidee.getPosition(); 
			float collideeSpeed = collidee.getSpeed();
			int collideeId = collidee.getId();
			//int collideeWidth = collidee.getWidth();
			Vector2f collidePosition = collidee.getPosition();
			
			float mySpeed = hostAgent.getSpeed();
			int myId = hostAgent.getId();
			
			Vector2f relPos = new Vector2f();
			relPos.sub(collidePosition, hostAgent.getPosition());
			
			if(relPos.length() > TOLERANT_ERR){
				relPos.normalize();
			}else{
				relPos.set(0,0);
			}
		
			
			double includedAngle = Math.abs(hostAgent.getHeading().dot(relPos));  
			
			
			if(includedAngle >= 0.5){	//when the included angle is more than 45 degrees.
				if(mySpeed > collideeSpeed || ( (collideeSpeed == mySpeed)&&(myId > collideeId) )){
					speedUp(50);
				}
				else{
					slowDown(50);
				}
			}
			else{
				if(myId > collideeId){
					speedUp(15);
				}else{
					slowDown(50);
					Vector2f localPosOfCollidee = caculateLocalPosOfCollide(collideePosition);
					if(localPosOfCollidee.y > 0){
						hostAgent.turnLeft(5);
					}else{
						hostAgent.turnRight(5);
					}
						
					
//					localPosOfCollidee.y *= -2;
//					localPosOfCollidee.x = 0;
					
//					behaviorSteeringForce[bId].set(localPosOfCollidee);
			
//					inactivateBehavior(headOnCAId);
				}				
			}
			
			return true;
		}
		else{
			return false;
		}			
	}
	
	
	
	/***********************************************************
	 * collision-avoidance strategy for the head-on collision 
	 ************************************************************/
	public boolean headOnCA(Vector2f localPosOfCollide, int collideWidth, int timeStep){
		int bId = behaviorIdMap.get("headOnCA");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);
			
			int minDBLength = hostAgent.getVRadius()/2;
			double detectionBoxLength = minDBLength * (1 + (hostAgent.getSpeed() / hostAgent.getMaxSpeed()));
			double expandedRadius;
			double cx, cy;							
			double sqrtPart;						
			double ip;
			boolean flag = false;
			
			transformation.reset(); 
			
			if(localPosOfCollide.x > 0){
				flag = true;
				expandedRadius =  (collideWidth + hostAgent.getWidth()) * 0.5 + SAFTETY_DIST_TO_OBSTACLE;
				if (Math.abs(localPosOfCollide.y) < expandedRadius){					
					cx = localPosOfCollide.x;
					cy = localPosOfCollide.y;
					sqrtPart = Math.sqrt(expandedRadius * expandedRadius - cy * cy);
			        ip = cx - sqrtPart;
					
					if (ip <= 0.0)
					{
					  ip = cx + sqrtPart;
					}      
				}
			}
			
			double multiplier;
			Vector2f caForce = new Vector2f();
			
			if(flag){
				multiplier = LATERAL_WEIGHT + (detectionBoxLength - localPosOfCollide.x) / detectionBoxLength;
				
				float y = (Math.abs(localPosOfCollide.y) < 0.1)? 1: localPosOfCollide.y;
				
				//calculating the lateral force
				caForce.y = (float)(y * multiplier * -1);					
			}
	 
			caForce = transformation.vectorToWorldSpace(caForce, hostAgent.getHeading(), hostAgent.getSide());
			behaviorSteeringForce[bId].set(caForce);
		
			return true;
		}
		else{
			return false;
		}
	}
	
		
	public boolean obstacleAvoidance(List<Obstacle> obslist, int timeStep){
		int bId = behaviorIdMap.get("obstacleAvoidance");
		if(isValid(bId)){
			activateBehavior(bId, timeStep);

			int minDBLength = hostAgent.getVRadius() / 2;
			
			/****************************************************************************
			 * The length of detection Box of agent is proportion to the speed of agent. 
			 ****************************************************************************/
			double detectionBoxLength = minDBLength * (1 + (hostAgent.getSpeed() / hostAgent.getMaxSpeed())); 
			
			Obstacle cio = null;					//the Closest Intersecting Obstacle(CIO)
			double distToCIO = 9999;				//track the distance to the CIO.
			Vector2f localPosOfCIO = null;			//record the local coordinates of the CIO.
			transformation.reset();
			double expandedRadius;					//for detecting intersection with my heading direction.
			double cx, cy;							//the center of the circumscribed circle of obstacle. 
			double sqrtPart;						
			double ip;
			Vector2f localPosOb;
			
			for(Obstacle ob:obslist){		
				if(ob == hostAgent.getTargetOb()){
					continue;
				}	
					
				localPosOb = transformation.pointToLocalSpace(
								ob.getPosition(), hostAgent.getHeading(), hostAgent.getSide(), hostAgent.getPosition()
							 ); 

				 //if the local position has a negative x value then it must lay behind me. 
				if(localPosOb.x >= 0){
					expandedRadius =  (ob.getWidth() + hostAgent.getWidth()) / 2 + SAFTETY_DIST_TO_OBSTACLE;
					
					if (Math.abs(localPosOb.y) < expandedRadius){					
						cx = localPosOb.x;
						cy = localPosOb.y;
						sqrtPart = Math.sqrt(expandedRadius * expandedRadius - cy * cy);
				        ip = cx - sqrtPart;
						
						if (ip <= 0.0)
						{
						  ip = cx + sqrtPart;
						}

						//test to see if this is the closest so far
						if (ip < distToCIO)
						{
							distToCIO = ip;
							cio = ob;
							localPosOfCIO = localPosOb;
						}         
					}
				}
			}
			
			
			double multiplier;
			float cioRadius;
			Vector2f obstacleAvoidanceForce = new Vector2f();
			
			//calculating a steering force, if we found an intersecting obstacle.
			if(cio != null){
				multiplier = LATERAL_WEIGHT + (detectionBoxLength - localPosOfCIO.x) / detectionBoxLength;
				cioRadius = cio.getWidth()/2;
				
				float y = (Math.abs(localPosOfCIO.y) < 0.1)? 1: localPosOfCIO.y;
								
				//calculating the lateral force
				obstacleAvoidanceForce.y = (float)(y * multiplier * -1);
				
				//calculating the braking force
				obstacleAvoidanceForce.x = (cioRadius - localPosOfCIO.x) * BREAKING_WEIGHT;					
			}
	 
			obstacleAvoidanceForce = transformation.vectorToWorldSpace(obstacleAvoidanceForce, hostAgent.getHeading(), hostAgent.getSide());

			behaviorSteeringForce[bId].set(obstacleAvoidanceForce);
			
			return true;
		}
		else{
			return false;
		}	
	}		
	
	public void offObstacleAvoidance(){
		inactivateBehavior(obstacleAvoidanceId);
	}
	
	
	
	boolean accumulateForce(Vector2f forceToAdd)
	{
		/***********************************************************************
		 * calculate how much steering force remains to be used by me
		 ***********************************************************************/
		float magnitudeSoFar = totalSteeringForce.length();
		float magnitudeRemaining = hostAgent.getMaxForce() - magnitudeSoFar;
		if (magnitudeRemaining <= 0.0) return false;
		
		double magnitudeToAdd = forceToAdd.length();
		  
		if (magnitudeToAdd < magnitudeRemaining){
			totalSteeringForce.add(forceToAdd);
		}
		else{
			if(forceToAdd.length() > TOLERANT_ERR){
				forceToAdd.normalize();  
				forceToAdd.scale(magnitudeRemaining);
			}else{
				forceToAdd.set(0,0);
			}	
			
			totalSteeringForce.add(forceToAdd); 
		}
		
		return true;
	}
	
	
	public Vector2f calculate(){
		totalSteeringForce.set(0,0);
		
//		targetAgent = hostAgent.getTargetAgent();
		targetOb = hostAgent.getTargetOb();
		
//		hostAgent.shiftPosition(shiftVec);
		
		update();
		
		Vector2f steeringForce = new Vector2f();
		
		
		if( isBehaviorActived(behaviorIdMap.get("stop")) ){
			hostAgent.setSpeedMode(MovingObject.SPEED_STOP);
		}
		else if( isBehaviorActived(behaviorIdMap.get("slowDown")) ){
			hostAgent.setSpeedMode(MovingObject.SPEED_SLOW);
		}
		else if( isBehaviorActived(behaviorIdMap.get("speedUp")) ){
			hostAgent.setSpeedMode(MovingObject.SPEED_HURRY);
		}
		else{
			hostAgent.setSpeedMode(MovingObject.SPEED_NORMAL);
		}
				
		if(hostAgent.isDrawView()){	
			logger.debug("============ Total Force ============");				
		}
		
		for(int i = 0; i < behaviorActivation.length; i++){
			if(isBehaviorActived(i) && behaviorSteeringForce[i].length() != 0){
						
				steeringForce.set(behaviorSteeringForce[i]);
				steeringForce.scale(behaviorWeight[i]);
			
				if (!accumulateForce(steeringForce)){
					if(hostAgent.isDrawView()){	
						logger.debug("** Run out of the Force ** : " + behaviorNameMap.get(i));
					}
					return totalSteeringForce;
				}
				
				
				/******************************************************
				 * debug
				 ******************************************************/
				if(hostAgent.isDrawView()){	
					//logger.debug(behaviorNameMap.get(i)+": " + behaviorSteeringForce[i].length());
					logger.debug(behaviorNameMap.get(i)+": " + steeringForce.length());
				}				
			}	
		}
		if(hostAgent.isDrawView()){	
			logger.debug("============ End Total Force ============");
		}
		
//		shiftVec.set(0,0);
		return totalSteeringForce; 
		
	}
	
	
	/* Active force */
	public int getActiveF(){
		return behaviorWeight[arriveId];
	}
	
	public void setActiveF(int a){
		behaviorWeight[arriveId] = a;
	}
	
	
	public void setHost(Agent ag) {
		hostAgent = ag;
	}
	
	
	public Vector2f caculateLocalPosOfCollide(Vector2f collidePos){
		transformation.reset();
		Vector2f localPosOfCollide;
		
		localPosOfCollide = transformation.pointToLocalSpace(
								collidePos, hostAgent.getHeading(), hostAgent.getSide(), hostAgent.getPosition()
			 				); 
		return localPosOfCollide;
	}
	
	public Object clone(){
		Object o = null;
		try{
			o=super.clone();
		}catch(CloneNotSupportedException e){
			System.err.println("Behavior doesn't clone");
		}
		return o;
	}
}
