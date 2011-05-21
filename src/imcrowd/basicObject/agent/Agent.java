package imcrowd.basicObject.agent;

import imcrowd.basicObject.ConfigurationIO;
import imcrowd.basicObject.InfectiveSource;
import imcrowd.basicObject.MyObject;
import imcrowd.basicObject.Neighborhood;
import imcrowd.basicObject.agent.behavior.groupMind.GroupMind;
import imcrowd.basicObject.agent.behavior.groupMind.riot.Riot;
import imcrowd.basicObject.agent.behavior.individualMind.Behavior;
import imcrowd.basicObject.agent.boid.Boid;
import imcrowd.basicObject.agent.normalAgent.Leader;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.agent.state.*;
import imcrowd.basicObject.goal.Goal;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.basicObject.obstacle.InteractiveObstacle;
import imcrowd.engine.Controller;
import imcrowd.engine.Engine;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.manager.AgentManager;
import imcrowd.manager.GoalManager;
import imcrowd.manager.GridManager;
import imcrowd.ui.AgentTab;
import imcrowd.ui.ExperimentParameterTab;
import imcrowd.util.MyMath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.vecmath.Vector2f;


public abstract class Agent extends MovingObject implements MyObject, ConfigurationIO{

	/**********************************
	 * 		Constants
	 **********************************/
	public static final float	TIMESTEP     =	0.05f;		   //delta t
	
	/* The behavior an agent may act when it attempts to cross the border of the canvas */
	public static final int 	BA_STOP = 0,		   
								BA_WRAP = 1,		   
								BA_LEAVE  = 2;

	/* Surrounding Situation */
	public static final int	 	PEACE			= 0,
								SUPERIORITY 	= 1,
								MINORITY		= 2,
								EQUAL_STRENGTH	= 3; 

	/* Collision Type */
	private final int  			CT_NO_COLLISION				= 0,
			   		   			CT_HEAD_ON_COLLISION		= 1,
			   		   			CT_SIDE_COLLISION			= 2,
			   		   			CT_REAR_END_COLLISION		= 3;	
	
	private final int 			SEPARATION_REGION_FACTOR = 10;	
	
	
	/* Collision Prediction */
	private final int 			CP_TIME_SPAN = 3;	   		//to predict the immediately moment. (as small as good)
	private final int 			CP_PREDICTION_RANGE = 10;	
		
	
	
	/****************************************
	 * 				Attributes
	 ****************************************/
	protected int id;						// The unique identity of agent.	
	protected int groupId;
	protected int colorId;
	
	protected int stamina;
	protected int width;				
	protected int height;
	
	protected int threshold;				// The number of others who must make one decision before I does so.
	protected int hesitate;					// the period of time when I hesitate before taking action (doesns't need to write out)
	protected int hesitateCounter;			// doesn't need to write out
	
	protected int viewField_Radius;			
	protected int viewField_Theta;		
	
	protected int boundsAction;				// The behavior an agent may act when it attempts to cross the border of the canvas
	
	protected float rationality;			// [0.1, 0.9]
	protected float currentRationValue;
	
	
	protected Goal goal;					// The goal where I should move toward. 
	protected Goal lastGoal;
	protected Goal home; 
	
	protected State	 state,					// States for communication model
			 		 cleanState,
			 		 latentState,
			 		 consensusState,
			 		 conflictState,
			 		 immuneState;
	
	protected Boid 					boid;							// The virtual force for flocking behavior
	protected GroupMind				collectiveBehavior;				// collective behaviors for engaged state
	protected InfectiveSource		infectiveSource;
	public 	  Behavior 				behavior;					   	// Individual Behavior

	
	
	/*****************************************
	 * 				Boolean Values 
	 *****************************************/
	protected boolean isDrawView;				
	protected boolean isAlive;					
	protected boolean isGoHome;
	protected boolean isNeighbor;				//am I a neighbor of the agent which was selected by user.
	protected boolean isSpecial;
	protected boolean isNearByPolice;
	protected boolean isInfectant;
	protected boolean isVictimRegistry;
	protected boolean isSuperiority;
	

	/****************************************
	 * 			Local Information
	 ****************************************/
	protected int num_sameParty;				// equals to num_brothers + num_alliance
	protected int num_diffParty;				// equals to num_hostile
	
	protected int num_brothers; 				// count the number of agents belonging to the same group
	protected int num_alliance;					// count the number of agents belonging to the same party
	protected int num_hostile;					// count the number of agents belonging to the different party
	protected int num_Green;
	protected int num_Yellow;

	protected int currentGridID;				// The id of the gird where I am.

	protected BitSet event;						// The result from interacting with the objects in the world
	
	protected Agent nearestTarget;				// The target agent of the pursuit or evading behavior during the engaged state.
	protected Agent evadeTarget;
	protected Obstacle targetOb;				// The target obstacle of the vandalism behavior during the engaged state.
	protected List<Obstacle> obsInView;			// Track of the objects within view.
	protected Neighborhood neighborhoodMark;
	
	
	
	/*************************************************
	 * 			 variables for computing
	 *************************************************/	
	/* The available field for the movement of me. */
	protected	int boundWidth,
					boundHeight;
	
	protected int groupColor_R,
				  groupColor_G,
				  groupColor_B;
	
	protected float[] point_TopLeft;				   //The left top point of me.
	
	/* for collision prediction */
	protected float shortestCollisionDistance;
	protected Agent firstCollisionAgent;
	protected Agent firstDifferGroupAgent;
	
	DecimalFormat df = new DecimalFormat(".##");
	
	
	
	/***************************************************
	 * 			Objects for drawing
	 ***************************************************/
	protected Ellipse2D 	personalSpace;		   		// my personal space
	protected Ellipse2D 	densityCircle;				// the circle within which Agent calculates the density.
	protected Line2D 		orientation;				
	protected Arc2D 		viewField;					
	protected Color 		colorOfPS;					// the color of my personal space
	
	/*  for painting wander circle */
	protected Ellipse2D 	wanderCircle;				// The wander circle
	protected BasicStroke   basicStroke;
	protected BasicStroke	borderStroke;
	
	/***************************************************
	 * 			External Information
	 ***************************************************/
	protected   Engine 		engine;
	protected AgentManager 	agentManager;
	protected GoalManager	goalManager;
	protected GridManager	gridManager;
	protected Controller	ct;
	protected ExperimentParameterTab param;
	
	//TODO using the Factory design pattern to replace this one someday.
	protected Agent(float x, float y, float theta, int groupId){
		
		super(new Vector2f(x,y),theta);
		
		engine 			= Engine.getInstance();
		agentManager 	= engine.getAgentManager();
		goalManager	 	= engine.getGoalManager();
		gridManager		= engine.getGridManager();
		ct 			 	= Controller.getInstance();
		param			= ExperimentParameterTab.getInstance();
		
		isDrawView 		= false;
		isAlive 		= true;
		isGoHome		= false;
		isNeighbor 		= false;
		isInfectant		= false;
		isNearByPolice	= false;
		isSuperiority	= false;
		
		stamina = 200;
		width   = 10;				
		height  = 10;
		
		this.boundWidth = engine.getWidth() -3;				//The edge width of frame.
		this.boundHeight = engine.getHeight()-3;			//The edge height of frame.
		this.boundsAction = ct.getCustom_BA();

		id = agentManager.getNewId();
		
		this.groupId = groupId;

		groupColor_R = (groupId * 17) % 128 + 128;
		groupColor_G = (groupColor_R * 11) % 128 + 128;
		groupColor_B = (groupColor_G * 19) % 128 + 128;
		
		shortestCollisionDistance = 100;
		
		resetThreshold();
		resetRationality();		
		
		if(engine.getGoalManager().getGoalList().size() != 0){
			goal = engine.getGoalManager().getNewGoal();
		}
		

		densityCircle 	= new Ellipse2D.Double(vPosition.x - viewField_Radius, vPosition.y - viewField_Radius, viewField_Radius*2, viewField_Radius*2);
		orientation 	= new Line2D.Double();
		viewField = new Arc2D.Double();
		colorOfPS = new Color(255,0,0);					//The default color of my personal space is red.
		
		infectiveSource = null;		
		nearestTarget = null;
		evadeTarget	= null;
		targetOb	= null;
				  
		wanderCircle = new Ellipse2D.Double();
		basicStroke	 = new BasicStroke(1);
		borderStroke  = new BasicStroke(2);
		
		behavior = new Behavior(this);			
		
		obsInView = new ArrayList<Obstacle>();
		
		cleanState		= CleanState.getInstance();
		latentState		= LatentState.getInstance();
		consensusState	= LatentState.getInstance();
		conflictState	= EngagedState.getInstance();
		immuneState		= DisenchantedState.getInstance();
		state = cleanState;		
		
		event = new BitSet();
		
		customizeAgent();
	}	
	abstract protected void action();
	abstract protected void dispatchAGoal();
	abstract public BitSet interactWithOthers(ArrayList<Agent> agentList);
	abstract public void paint(Graphics2D g);
	
	abstract public void cleanStateAction();
	abstract public void latentStateAction();
	abstract public void engagedAction();
	abstract public void disenchantedStateAction();
	
	
	public boolean isVictimRegistry() {
		return isVictimRegistry;
	}

	public void setVictimRegistry(boolean isVictimRegistry) {
		this.isVictimRegistry = isVictimRegistry;
	}
	
	public void resetThreshold(){
		float mean, sd;
		
		mean = param.getParameterValue(ExperimentParameterTab.THRESHOLD_MEAN);
		sd	 = param.getParameterValue(ExperimentParameterTab.THRESHOLD_SD);
		//threshold = engine.getRandNum().nextInt(THRESHOLD);		//uniform distribution [0,THRESHOLD]
		threshold =(int)(Math.round(Engine.rand.nextGaussian() * sd) + mean);		//normal distribution[MEAN,STANDER_DEVIATION]
		if (threshold < 0) threshold = 0;
	}
	
	public void resetRationality(){
		float mean, sd;
			
		mean = param.getParameterValue(ExperimentParameterTab.RATIONALITY_MEAN) * 0.1f;
		sd 	 = param.getParameterValue(ExperimentParameterTab.RATIONALITY_SD) * 0.1f ;
		rationality = (float)((Engine.rand.nextGaussian() * sd) + mean);		//normal distribution[MEAN,STANDER_DEVIATION]
		rationality = (rationality < 0.1)? 0.1f: ((rationality > 0.9)? 0.9f: rationality);
		
		rationality = Float.valueOf(df.format(rationality));
		currentRationValue = rationality;
	}
	
	
	
	@Override
	public void setAttributes(Properties configuration, int i){
		super.setAttributes(configuration, i);
		
		String str;
				
		str = configuration.getProperty("ag_id"+i);
		id = Integer.valueOf(str);
		
//		str = configuration.getProperty("ag_groupId"+i);
//		groupId = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_colorId"+i);
		colorId = Integer.valueOf(str);
		setColorOfPS(colorId);
		
		str = configuration.getProperty("ag_width"+i);
		width = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_height"+i);
		height = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_threshold"+i);
		threshold = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_viewField_Radius"+i);
		viewField_Radius = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_viewField_Theta"+i);
		viewField_Theta = Integer.valueOf(str);
		
//		str = configuration.getProperty("ag_boundsAction"+i);
//		boundsAction = Integer.valueOf(str);
		
		str = configuration.getProperty("ag_rationality"+i);
		rationality = Float.valueOf(str);
		currentRationValue = rationality;
		
		//Boolean		
		str = configuration.getProperty("ag_isAlive"+i);
		isAlive = Boolean.valueOf(str);
		
		str = configuration.getProperty("ag_isGoHome"+i);
		isGoHome = Boolean.valueOf(str);
		
		str = configuration.getProperty("ag_isSpecial"+i);
		isSpecial = Boolean.valueOf(str);
		
		str = configuration.getProperty("ag_isNearByPolice"+i);
		isNearByPolice = Boolean.valueOf(str);
		
		str = configuration.getProperty("ag_isInfectant"+i);
		isInfectant = Boolean.valueOf(str);
				
	}	
	
	@Override
	public Map<String, String> getAttributes(int i){
		Map<String, String> attrMap = super.getAttributes(i);
		String attrName, value;
		
		attrMap.putAll(boid.getAttributes(i));
				
		attrName = "ag_id"+i;
		value = String.valueOf(id);
		attrMap.put(attrName, value);
		
		attrName = "ag_groupId"+i;
		value = String.valueOf(groupId);
		attrMap.put(attrName, value);
		
		attrName = "ag_colorId"+i;
		value = String.valueOf(colorId);
		attrMap.put(attrName, value);
		
		attrName = "ag_width"+i;
		value = String.valueOf(width);
		attrMap.put(attrName, value);
		
		attrName = "ag_height"+i;
		value = String.valueOf(height);
		attrMap.put(attrName, value);
		
		attrName = "ag_threshold"+i;
		value = String.valueOf(threshold);
		attrMap.put(attrName, value);
		
		attrName = "ag_viewField_Radius"+i;
		value = String.valueOf(viewField_Radius);
		attrMap.put(attrName, value);
		
		attrName = "ag_viewField_Theta"+i;
		value = String.valueOf(viewField_Theta);
		attrMap.put(attrName, value);
		
		attrName = "ag_boundsAction"+i;
		value = String.valueOf(boundsAction);
		attrMap.put(attrName, value);
		
		attrName = "ag_rationality"+i;
		value = String.valueOf(rationality);
		attrMap.put(attrName, value);
		
		//boolean
		attrName = "ag_isAlive"+i;
		value = String.valueOf(isAlive);
		attrMap.put(attrName, value);
		
		attrName = "ag_isGoHome"+i;
		value = String.valueOf(isGoHome);
		attrMap.put(attrName, value);

		attrName = "ag_isSpecial"+i;
		value = String.valueOf(isSpecial);
		attrMap.put(attrName, value);
		
		attrName = "ag_isNearByPolice"+i;
		value = String.valueOf(isNearByPolice);
		attrMap.put(attrName, value);
		
		attrName = "ag_isInfectant"+i;
		value = String.valueOf(isInfectant);
		attrMap.put(attrName, value);
		
		return attrMap;
	}

	@Override	//from MovingObject
	public void setPosition(Vector2f position) {
		super.setPosition(position);
		setRegion();
	}
	
	@Override	//from MyObejct
	public void setPosition(float x, float y){
		vPosition.x = x;
		vPosition.y = y;
		setRegion();
	}
	
	public void setSuperiority(boolean b){
		isSuperiority = b;
	}	
	
	public boolean isSuperiority(){
		return isSuperiority;
	}
	
	public void setNearByPolice(boolean b){
		isNearByPolice = b;
	}
	
	public boolean isNearByPolice(){
		return isNearByPolice;
	}
	
	public boolean isInfectant(){
		return isInfectant;
	}
	
	public void setInfectant(boolean b){
		isInfectant = b;
	}
	
	
	public float getFlockingWeight(int force, int relation){
		return state.getFlockingWeight(force, relation);
	}
	
	public void setFlockingWeight(int force, int relation, int value){
		state.setFlockingWeight(force, relation, value);
	}
	
	
	public void hitBy(Agent hitter){
//		hitter.setAttacking(true);
		
		stamina--;
		behavior.stop(5);
		if(stamina == 0){
			dead();
		}	
	}
	
	public Neighborhood getNeighborhoodMark(){
		return neighborhoodMark;
	}
	
	public void setNeighborHoodMark(Neighborhood n){
		this.neighborhoodMark = n;
	}
	
	public void resetLocalInformation(){
		targetOb = null;
		nearestTarget = null;
		evadeTarget	  = null;
		isNearByPolice	= false;
		num_sameParty	= 0;
		num_diffParty	= 0;
		num_brothers	= 0; 
		num_alliance	= 0;
		num_hostile		= 0;
		num_Green		= 0;
		num_Yellow		= 0;
	}
	
	
	public boolean isTakeAction(){
		if(hesitateCounter <= 0){
			return true;
		}
		
		if( num_Green >= this.threshold){
			hesitateCounter--;
		}
//		else{
//			if(hesitateCounter < hesitate){
//				hesitateCounter++;
//			}
//		}
		
		return false;
	}	
	
	public int getNumGreen(){
		return num_Green;
	}
	
	public int getNumYellow(){
		return num_Yellow;
	}
	
	public void resetNumGreen(){
		num_Green = 0;
	}
	
	public void resetNumYellow(){
		num_Yellow = 0;
	}
	
	public void addOneGreen(){
		num_Green++;
	}
	
	public void addNGreen(int n){
		num_Green += n;
	}
	
	public void addOneYellow(){
		num_Yellow++;
	}
	
	public void addBeta(int weight, int behIndenx){
		infectiveSource.addBeta(currentRationValue, weight, behIndenx);
	}
	
	public void initializeInfectiveSource(){
		infectiveSource.initialize(rationality);
	}
	
	public GroupMind getCollectiveBehavior(){
		return collectiveBehavior;
	}
	
	public void setCollectiveBehavior(GroupMind collectiveBeh) {
		this.collectiveBehavior = collectiveBeh;
	}
/*	
	public void incrBetaCounter(){
		infectiveSource.incrBetaCounter();
	}
*/
	public void descBetaCounter(){
		infectiveSource.descBetaCounter();
	}
	
	public void resetBetaCounter() {
		infectiveSource.resetBetaCounter();
	}
	
	public void incrAlphaCounter(){
		infectiveSource.incrAlphaCounter();
	}
	
	public void descAlphaCounter(){
		infectiveSource.descAlphaCounter();
	}
	
	public void resetAlphaCounter(){
		infectiveSource.resetAlphaCounter();
	}
	
	
	public boolean isPurge(){
		return infectiveSource.isPurge();
	}
	
	
	/********************************************** 
	 * true:  latent state (emotion aroused) 
	 * false: clean state.
	 **********************************************/
	public boolean isEmotionArousal() {
		return infectiveSource.isEmotionArousal();
	}
	
	
	
	public InfectiveSource getInfectiveSource() {
		return infectiveSource;
	}

	public void setInfectiveSource(InfectiveSource infectiveSource) {
		this.infectiveSource = infectiveSource;
		this.collectiveBehavior = infectiveSource.getCollectiveBehavior();
		hesitate = threshold * this.collectiveBehavior.getHesitateWeight();
		hesitateCounter = hesitate;
	}
	
	public void changeCollectiveBehavior(Riot cb){
		this.collectiveBehavior = cb;
	}
	
	public boolean isSpecial() {
		return isSpecial;
	}
	
	public Obstacle getTargetOb() {
		return targetOb;
	}

	public void setTargetOb(Obstacle targetOb) {
		this.targetOb = targetOb;
	}

	public Agent getNearestTarget(){
		return nearestTarget;
	}
	
	public Agent getEvadeTarget(){
		return evadeTarget;
	}
	
	public void setNearestTarget(Agent ag) {
		nearestTarget = ag;
	}
	
	public void setEvadeTarget(Agent ag) {
		evadeTarget = ag;
	}

	private void customizeAgent(){
		setWidth(ct.getDiameterTFieldValue());				
		setLength(ct.getDiameterTFieldValue());
		setMass(ct.getMassTFieldValue());

		setVRadius(ct.getVRadiusSlider().getValue());
		setVTheta(ct.getVThetaSlider().getValue());
		setMaxForce(ct.getMaxForceSlider().getValue());
		setSpeedMode(MovingObject.SPEED_NORMAL);
		
		setNormalSpeed(ct.getMaxNormalSpeedSlider().getValue());
		setActiveF(ct.getActiveFSlider().getValue());					
		
		boid = new Boid(
					ct.getSpeparation(),
					ct.getCohesion(),
					ct.getAlignment()	
					);
		setSeparationWeight(ct.getSpeparation());
		setCohesionWeight(ct.getCohesion());
		setAlignmentWeight(ct.getAlignment());
		
		setColorOfPS(ct.getAgentColor());
	} 
	
	public int getNumSameParty() {
		return num_sameParty;
	}

	public void setNumSameParty(int numOwnParty) {
		this.num_sameParty = numOwnParty;
	}

	public int getNumDiffParty() {
		return num_diffParty;
	}

	public void setNumDiffParty(int numOtherParty) {
		this.num_diffParty = numOtherParty;
	}
	
	public Agent getMyLeader(){
		return behavior.getMyLeader();
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	
	private void resetCollisionInfomation(){
		//firstCollisionTime = 99999;					//the nearest collision time.
		shortestCollisionDistance = 100;
		firstCollisionAgent = null;
		firstDifferGroupAgent = null;
	}
	
	private boolean isHeadOnCollision(Agent collidee){
		if(this.vVelocity.dot( collidee.getVelocity() )< 0){
			return true;
		}
		return false;
	}
	
	private boolean isSideCollision(Agent collidee){
		Vector2f relPosVec = new Vector2f();
		relPosVec.sub(collidee.getPosition(), this.vPosition);
		
		Vector2f perpendicularVec = MyMath.getRightHandSideNormal(relPosVec);
		 
		if(MyMath.crossProduct(this.vHeading, perpendicularVec) * MyMath.crossProduct(collidee.vHeading, perpendicularVec) < 0){
			return true;		//collision
		}else{
			return false;
		}	
		
		
	}

	private boolean isRearEndCollision(Agent collidee){		
		Vector2f relPosVec = new Vector2f();
		relPosVec.sub(collidee.getPosition(), this.vPosition);
	
		Vector2f perpendicularVec = MyMath.getRightHandSideNormal(relPosVec);
		
		if(MyMath.crossProduct(this.vHeading, perpendicularVec) * MyMath.crossProduct(collidee.vHeading, perpendicularVec) > 0){
			return true;		//collision
		}else{
			return false;
		}	
	}
	
	private void collisionAvoidance(int collisionType, Agent collidee){
		switch(collisionType){
			case CT_NO_COLLISION:
				behavior.inactivateAllCA();
				break;
					
			case CT_HEAD_ON_COLLISION:
				behavior.headOnCA(collidee.getPosition(), collidee.getWidth(), 20);
				break; 
			
			case CT_SIDE_COLLISION:
				behavior.sideCA(collidee, 30);
				break;
				
			case CT_REAR_END_COLLISION:
				behavior.rearEndCA(collidee, 30);
				break;
		}	
	}
	
	/* fLength */
	public int getLength() {
		return height;
	}

	public void setLength(int length) {
		this.height = length;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void changeState(State s){
		this.state = s;
	}
	
	public State getState(){
		return state;
	}
	
	public Color getStateColor(){
		return state.getColor();
	}
	
	public void setIsNeighbor() {
		isNeighbor = true;
	}
	
	public boolean isNeighbor(){
		return isNeighbor;
	}
	
	private void dead(){
		isAlive = false;
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public void setIsGoHome(boolean go){
		isGoHome = go;
	}
	
	public boolean isGoHome(){
		return isGoHome;
	}
		

	/* goal */
	public void setGoal(Goal g){
		goal = g;
	}
	
	public Goal getGoal(){
		return goal;
	}
	
	public boolean hasGoal(){
		return goal != null;
	}
		
	public boolean isPointInside(Point2D p)
	{ 
	  return personalSpace.contains(p); 
	}
		
	public boolean isAgentInPerceptionCircle(Agent otherAgent, float distance){	
		return (distance <= viewField_Radius + otherAgent.getWidth()/2)? true: false;
	}
	
	public boolean isAgentInView(Agent otherAgent, float distance){
		if(isAgentInPerceptionCircle(otherAgent, distance)){
			return viewField.intersects(otherAgent.getRegion().getBounds2D());	
		}
		return false;
	}
	
	public boolean isObstacleInView(Obstacle testObstacle){
		//only test the bounding box of the obstacle.
		return viewField.intersects(testObstacle.getRegion().getBounds2D());
	}
	
	public boolean isGoalInView(){
		return viewField.contains(goal.getPosition().x, goal.getPosition().y);
	}
	
	public boolean isGoalInPerceptionCircle(){
		Vector2f vec = new Vector2f();
		float distance;
		
		if(goal != null){
			vec.sub(goal.getPosition(), vPosition);
			distance = vec.length();
			if(distance <= viewField_Radius) return true;
		}
		
		if(lastGoal != null){
			vec.sub(lastGoal.getPosition(), vPosition);
			distance = vec.length();
			if(distance <= viewField_Radius) return true;
		}	
		
		return false;
	}
	
	public boolean testObstacleCollision(MyObject ob){
		
		double x1 = ob.getPosition().x;
		double y1 = ob.getPosition().y;
		double r1 = ob.getRegion().getWidth()/2;
		double x2 = vPosition.x;
		double y2 = vPosition.y;
		double r2 = width/2;
	    
		/* the distance between two agents > the sum of radias of two agents  */	
		if(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)) > ((r2+r1)*(r2+r1)+30)){
			return false;					//no collision.
		}
		return true;						//collision!!.
		
	}
	
		
	/* drawView */
	public boolean isDrawView(){
		return isDrawView;
	}
	
	public void setSelect(boolean bool){
		isDrawView = bool;
	}
	
	
	/* bounds action */
	public int getBA(){
		return boundsAction;
	}
	
	public void setBA(int ba){
		boundsAction = ba;
	}
	
	
	/* viewField radius */
	public int getVRadius(){
		return viewField_Radius;
	}
	
	public void setVRadius(int r){
		viewField_Radius = r;
		densityCircle.setFrame(vPosition.x - viewField_Radius, vPosition.y - viewField_Radius, viewField_Radius*2, viewField_Radius*2);
	}
	
	public Rectangle2D getViewFieldBBox() {
		return viewField.getBounds2D();
	}
	
	public Rectangle2D getDensityCircleBBox() {
		return densityCircle.getBounds2D();
	}
	
	/* viewField theta */
	public int getVTheta(){
		return viewField_Theta;
	}
	
	public void setVTheta(int t){
		viewField_Theta = t;
	}
	
	/* id */
	public int getId(){
		return id;
	}
	
	public void  setId(int id){
		this.id = id;
	}
	
	/* threshold */
	public int getThreshold(){
		return threshold;
	}
	
	public void  setThreshold(int t){
		threshold = t;
	}
	
	/* currentGridID */
	public int getCurrentGridID() {
		return currentGridID;
	}
	
	public void setCurrentGridID(int id) {
		currentGridID = id;
	}
	
	/* color of Personal Space */
	public void setColorOfPS(int cId){
		if(cId >= 0){
			colorId = cId;
			colorOfPS = AgentTab.COLOR_ARRAY[cId];
		}	
	}
	
	public Color getColorOfPs(){
		return colorOfPS;
	}
	
	public int getColorId(){
		return colorId;
	}
	
	/* personalSpace */
	public Ellipse2D getRegion(){
		return personalSpace;
	}
	
	public void setRegion(){
		personalSpace.setFrame(vPosition.x - width * 0.5, vPosition.y - height * 0.5, width, height);	
		densityCircle.setFrame(vPosition.x - viewField_Radius, vPosition.y - viewField_Radius, viewField_Radius*2, viewField_Radius*2);
		
		double theta = Math.atan2(vHeading.y,vHeading.x);						 
		viewField.setArcByCenter(vPosition.x, vPosition.y,viewField_Radius,Math.toDegrees(-theta) - viewField_Theta * 0.5f,viewField_Theta,Arc2D.PIE );
		
		point_TopLeft[0] = vPosition.x - width * 0.5f;
		point_TopLeft[1] = vPosition.y - height * 0.5f;
		
		gridManager.occupiedGrid(this);
		neighborhoodMark = gridManager.computeNeighborhood(this);
	}
	
	
	/* Boid Adapter */
		
	/* Separation Force */
	public int getSeparationWeight(){
		return boid.getSeparationWeight();
	}
	
	public void setSeparationWeight(int sf){
		boid.setSeparationWeight(sf);
	}
	
	/* Cohesion Force */
	public int getCohesionWeight(){
		return boid.getCohesionWeight();
	}
	
	public void setCohesionWeight(int cf){
		boid.setCohesionWeight(cf);
	}
	
	/* Alignment Force */
	public int getAlignmentWeight(){
		return boid.getAlignmentWeight();
	}
	
	public void setAlignmentWeight(int af){
		boid.setAlignmentWeight(af);
	}
	
	
	
	/* Infective Separation Force */
	public int getSeparationI(){
		if(infectiveSource == null){
			return 0;
		}
		return infectiveSource.getSeparationI();
	}
	
	/* Infective Cohesion Force */
	public int getCohesionI(){
		if(infectiveSource == null){
			return 0;
		}
		return infectiveSource.getCohesionI();
	}
	
	/* Infective Alignment Force */
	public int getAlignmentI(){
		if(infectiveSource == null){
			return 0;
		}
		return infectiveSource.getAlignmentI();
	}
	

	public int getBetaCounter(){
		if(infectiveSource == null){
			return -1;
		}
		return infectiveSource.getBetaCounter();
	}
	
	public void setBetaCounter(int t){
		infectiveSource.setBetaCounter(t);
	}
	
	public int getAlphaCounter(){
		if(infectiveSource == null){
			return -1;
		}
		return infectiveSource.getAlphaCounter();
			
	}
	
	public void setAlphaCounter(int t){
		infectiveSource.setAlphaCounter(t);
	}	
	
	public int getEmotionArousalPoint(){
		if(infectiveSource == null){
			return -1;
		}
		return infectiveSource.getEmotionArousalPoint();
	}



		
	/* Active force */
	public int getActiveF(){
		return behavior.getActiveF();
	}
	
	public void setActiveF(int a){
		behavior.setActiveF(a);
	}	

	public void addCurrentRationValue(float r){
		currentRationValue = Float.valueOf(df.format(currentRationValue += r));
		if(currentRationValue < 0.1) currentRationValue = 0.1f;
	}
	
	public void subtractCurrentRationValue(float r){
		currentRationValue = Float.valueOf(df.format(currentRationValue -= r));
		currentRationValue = (currentRationValue > rationality )? rationality: currentRationValue;
	}
	
	public float getCurrentRationValue() {
		return currentRationValue;
	}
	
	
	/* The vector from q to p. */
	private Point2D vector(Point2D p ,Point2D q){
		Point2D v = new Point2D.Double();
		v.setLocation(p.getX() - q.getX(), p.getY() - q.getY());
		return v;
	}
	
	/* cross product. */
	private double cross(Point2D p, Point2D q){
		return p.getX()* q.getY() - p.getY()* q.getX();
	}
	
	
	//Euler Integration
	private void newtonEulerOneIntegraion(float dt){

	    Vector2f steeringForce = behavior.calculate();

	    
	    //Acceleration = Force/Mass
	    Vector2f acceleration = new Vector2f();
	    acceleration.scale(1/mass, steeringForce);

	    //update velocity
	    Vector2f toAdd = new Vector2f();
	    toAdd.scale(dt, acceleration);  
	    vVelocity.add(toAdd);
	     

	    //make sure I'm not gonna exceed the maximum velocity
	    this.truncateVelocity(vVelocity);

	    speed = vVelocity.length();
    	    
	    //update the heading if I have a non-zero velocity
	    if (vVelocity.lengthSquared() > 0.00000001)
	    {    
	    	Vector2f newVec = new Vector2f();
	    	newVec.normalize(vVelocity);
	    	truncateTurnAngle(vHeading, newVec);
	    		    	
		    //update the position
		    toAdd.scale(dt, vVelocity);
		    vPosition.add(toAdd);
	    	
		    vHeading.normalize(vVelocity);
	    	
	    	
	    	vSide = MyMath.getRightHandSideNormal(vHeading);
	    	//vSide = MyMath.perp(vHeading);
	    }
	}	
	
	/*********************************************************************
	 *  The most important function for an agent
	 *  true: still in the game.
	 *  false: leave from the game.
	 * *******************************************************************/
	public boolean updateState(){
		if(isGoHome == true)
			return false;
		
		if(!isAlive)			// it doesn't do any thing.
			return true;
		
		float	dt = TIMESTEP;
		double theta;							//The angle between the vVelocity and x-axis.

		action();
			
		newtonEulerOneIntegraion(dt);			
		
		point_TopLeft[0] = vPosition.x - width * 0.5f;
		point_TopLeft[1] = vPosition.y - height * 0.5f;
	
				
		/* My action when I reach the border of the canvas. */ 
		/* wrap */
		if(boundsAction == BA_WRAP){
			//After disappearing in the left border, I will appear in the right border.
			if(point_TopLeft[0] + width < 0)
		      vPosition.x = boundWidth + width * 0.5f;
			//After disappearing in the right border, I will appear in the left border. 
		    else if (point_TopLeft[0] > boundWidth)
		      vPosition.x =0 - width * 0.5f;
			//After disappearing in the top border, I will appear in the bottom border. 
		    if ((point_TopLeft[1] + height) < 0)
		      vPosition.y = boundHeight + height * 0.5f;
		    //After disappearing in the bottom border, I will appear in the top border. 
		    else if (point_TopLeft[1] > boundHeight)
		      vPosition.y = 0 - height * 0.5f;	      
		}
		
		/* die */
		else if (boundsAction == BA_LEAVE)
		{
		    if ((point_TopLeft[0] + width) < 0 ||point_TopLeft[0] > boundWidth ||
		        (point_TopLeft[1] + height) < 0 || point_TopLeft[1] > boundHeight){
		    	     return false;					//inform the AgentManager that the agent has dead.
		    }				
		}
		/* stop (default behavior) */
		else
		{
		    //Stop in the right or the left border.
		    if (point_TopLeft[0]  < 0 || point_TopLeft[0] > (boundWidth - width))
		    {  
		      point_TopLeft[0] = Math.max(0, Math.min(point_TopLeft[0], boundWidth - width));
		      vPosition.x = point_TopLeft[0] + width * 0.5f;
		      setVelocity(0,0);
		    }
		    //Stop in the top or the bottom border.  
		    if (point_TopLeft[1]  < 0 || point_TopLeft[1] > (boundHeight - height))
		    {
		      point_TopLeft[1] = Math.max(0, Math.min(point_TopLeft[1], boundHeight - height));
		      vPosition.y = point_TopLeft[1] + height * 0.5f;
		      setVelocity(0,0);
		    }
		 }
		 
		 personalSpace.setFrame(point_TopLeft[0],point_TopLeft[1],width,height);	
		 densityCircle.setFrame(vPosition.x - viewField_Radius, vPosition.y - viewField_Radius, viewField_Radius*2, viewField_Radius*2);
				
		 theta = Math.atan2(vHeading.y,vHeading.x);						 
		 
		 //update the view field. 
		 viewField.setArcByCenter(vPosition.x, vPosition.y,viewField_Radius,Math.toDegrees(-theta) - viewField_Theta * 0.5f,viewField_Theta,Arc2D.PIE );
		 
		 dispatchAGoal();
		 
		 return true;                          
	}
		
	
	public int situationAnalysis(){
		
		if(num_sameParty < 0.4 * (num_sameParty + num_diffParty)){
			if(num_diffParty >= 3){
				return MINORITY;
			}else{
				return PEACE;
			}
		}
		else{
			if(num_sameParty > 0.6 * (num_sameParty + num_diffParty)){
				if(num_sameParty >= 3 && num_diffParty >0 ){
					return SUPERIORITY;
				}else{
					return PEACE;
				}
			}
			else{
				if(num_sameParty >= 3 && num_diffParty >= 3){
					return EQUAL_STRENGTH;
				}else{
					return PEACE;
				}
				
			}
		}
	}
			
	
	public boolean testAttackable(Agent ag){
		double x1 = ag.getPosition().x;
		double y1 = ag.getPosition().y;
		double r1 = ag.width/2;
		double x2 = vPosition.x;
		double y2 = vPosition.y;
		double r2 = width/2;
		
		/* the distance between two agents > the sum of two agents's radius  */	
		if(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)) > ((r2+r1)*(r2+r1)+30)){
			return false;					//no collision.
		}
		return true;						//collision!!.
		
	}
	
	/*For testing the collision with another agents. */
	public boolean testAgentCollision(Agent ag){
		double x1 = ag.getPosition().x;
		double y1 = ag.getPosition().y;
		double r1 = ag.width/2;
		double x2 = vPosition.x;
		double y2 = vPosition.y;
		double r2 = width/2;
		
		/* the distance between two agents > the sum of two agents's radius  */		
		if(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)) > ((r2+r1)*(r2+r1)+25)){
			return false;					//no collision.
		}
		return true;						//collision!!.
	}
		
	
	/* collision detection(only check my orientation vector with another agent's to see if they are cross each other or not). */
	protected boolean lineIntersection(Agent ag){
		int factor = 3;			//the multiplier of direction vector.
		Point2D p1 = new Point2D.Double(vPosition.x,vPosition.y);
		Point2D p2 = new Point2D.Double(vPosition.x + factor*vVelocity.x, vPosition.y + factor*vVelocity.y);
		Point2D q1 = new Point2D.Double(ag.getPosition().x, ag.getPosition().y);
		Point2D q2 = new Point2D.Double(q1.getX() + factor*ag.getVelocity().x, q1.getY() + factor*ag.getVelocity().y);
	
		if(cross(vector(p1,q1),vector(q2,q1))* cross(vector(p2,q1),vector(q2,q1)) <=0 &&
				   cross(vector(q1,p1),vector(p2,p1))* cross(vector(q2,p1),vector(p2,p1)) <=0)	
					return true;					//maybe collision in a while!!
		return false;								//no collision.
	}
	
	/****************************************************** 
	 * To check the agent whether reach a goal or not. 
	 ******************************************************/
	protected boolean isReachGoal(Goal g){
		
		double x1 = g.getPosition().x;
		double y1 = g.getPosition().y;
		double r1 = g.getRegion().getWidth()/2;
		double x2 = vPosition.x;
		double y2 = vPosition.y;
		double r2 = width/2;
	    
		/* the distance between two agents > the sum of radias of two agents  */	
		if(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)) > ((r2+r1)*(r2+r1))){
			return false;					//no reach.
		}
		return true;						//reach!!.
	}
	
	/****************************************************
	 * to compute the first collidee of the tested Agent.
	 ****************************************************/
	protected void agentCollisionPredictor(Agent he){
		/*******************************************************
		 * predict my future position after a certain time span.
		 *******************************************************/
		Vector2f myFuturePos = new Vector2f(this.vPosition);
		Vector2f tempVec = new Vector2f(this.vVelocity);
		tempVec.scale(CP_TIME_SPAN);
		myFuturePos.add(tempVec);		// the distance of prediction will in proportion to the velocity.
		
		/*********************************************************
		 * predict collidee's future position after the time span.
		 *********************************************************/
		Vector2f collideFuturePos = new Vector2f(he.getPosition());
		tempVec = new Vector2f(he.getVelocity());
		tempVec.scale(CP_TIME_SPAN);
		collideFuturePos.add(tempVec);
		
		
		/**************************************************************
		 * if (myFuturePos - collideFuturePos) < the safety distance,
		 * there will be a collision occur.  
		 **************************************************************/
		collideFuturePos.sub(myFuturePos);
		
		float distance = collideFuturePos.length();
		
		if(distance < CP_PREDICTION_RANGE + (2 * (float)he.getWidth()/this.width)){		
			if(distance < ( shortestCollisionDistance * (this.speed / this.maxSpeed) ) ){
				shortestCollisionDistance = distance; 
				firstCollisionAgent = he;
				
				if(this.groupId != he.getGroupId()){
					firstDifferGroupAgent = he;
				}
				
			}		
		}
	}
	

	/**************************************************************
	 * to check whether collide with an agent or not, 
	 * I should turn away when collision occurs. 
	 **************************************************************/
	public boolean agentCollisionRespondant(Agent otherAgent, boolean isLeaveAway){
		
		boolean isCollide = false;
		if (testAgentCollision(otherAgent)) {
			isCollide = true;
			
			event.set(AgentManager.AM_COLLISION_AG);
			
			if(isLeaveAway){
				return behavior.collisionResponse(otherAgent.getPosition(), 3);
			}	
		}
		
		return isCollide;		
	}
	
	
	/****************************************************
	 * Collision Avoidance State Machine
	 ****************************************************/	
	protected void agentCollisionAvoidance(){
		Agent targetAg = null;
		
		if(firstCollisionAgent != null){

			/*****************************************************
			 * avoid to collide with the members of other group.
			 ****************************************************/
			if(firstDifferGroupAgent != null){
				targetAg = firstDifferGroupAgent;
			}
			else{
				targetAg = null;
			}
			
			if(targetAg == null){	
				collisionAvoidance(CT_NO_COLLISION, targetAg);
			}else{	
	
				
				/*	Head-on Collision testing */
				if(isHeadOnCollision(targetAg)){
					collisionAvoidance(CT_HEAD_ON_COLLISION, targetAg);
				}
				
				/*  Side Collision testing */
				else if (isSideCollision(targetAg)){
					collisionAvoidance(CT_SIDE_COLLISION, targetAg);
				}
				
				/*  Rear-end Collision testing */
				else if (isRearEndCollision(targetAg)){
					collisionAvoidance(CT_REAR_END_COLLISION, targetAg);
				}
			}
		}
		else{
			collisionAvoidance(CT_NO_COLLISION, targetAg);		//targetAgent == null
		}
			
		resetCollisionInfomation();
	}
	
	
	
	/**************************************************************
	 * to check whether an agent runs into my safety region or not. 
	 * if he did it in that way, I'd better react to him properly.
	 **************************************************************/
	public void reactToInvader(Agent invader){
		if(!isSafety(invader.getPosition())){
			if(this.id < invader.getId()){
				behavior.stop(5);
//				turnRight(3);
			}else{
				behavior.slowDown(10);
//				turnRight(3);
			}
		}
	}
	
	
	protected void goHome(){
		
		if(home == null){
			home = goalManager.getNearestExit(vPosition);
		}
		
		if(isReachGoal(home)){
			if(this instanceof Leader){
				((Leader)this).cleanFollower();
			}
			
			isGoHome = true;
			return;
		}
		
		this.behavior.arrive(home.getPosition(), 2, 10);
			
	}
	
	protected void approachToGoal(){
		if(goal != null){
			this.behavior.arrive(goal.getPosition(), 1, 10);
		}else{
			this.behavior.wander(10);
		}	
	}
	
	/********************************************************
	 * accumulate total separation forces
	 ********************************************************/	
	protected void accumulateSeparation(Vector2f vecMe2He, Vector2f totalSepration, int agentLength, float weight){
		Vector2f vec = new Vector2f(vecMe2He);
		float length = vec.length();
		if(length < agentLength * weight * SEPARATION_REGION_FACTOR){
			vec.normalize();
			vec.scale(weight / length);	//separation force is negative proportion to the distance between the pair agents.
			totalSepration.add(vec);
		}	
	}
		
	protected boolean isSafety(Vector2f thrillerPos){
		Vector2f thrillerRelPosLocal = behavior.caculateLocalPosOfCollide(thrillerPos);
		
		if(thrillerRelPosLocal.x > 0 && thrillerRelPosLocal.x < width * 1.5){
			if(Math.abs(thrillerRelPosLocal.y) < width){
				return false;
			}
		}
		return true;
	}
	
	
	protected void interactWithObstacles(){
		List<Obstacle> obList = engine.getObstacleManager().getObList();
		Obstacle ob;
		obsInView.clear();
		
		for (int i = 0; i < obList.size(); i++) {
			ob = obList.get(i);

			if (isObstacleInView(ob)) {
				
				if(!this.isSpecial && ob.isInfectant()){
					state.contactInView((NormalAgent)this, ob);
				}
				
				obsInView.add(ob);
				
				/**********************************************************
				 * targeting the special obstacle
				 **********************************************************/
				if(this.state.isTargetSpOb()){
					if( ob.isInteracitve()){
							InteractiveObstacle spob = (InteractiveObstacle)ob;
							
							if(spob.isHitable()){
								setTargetOb(spob);
							}	
					}else if(ob.isInfectant()){
						setTargetOb(ob);
					}
				}
				
				/*****************************************
				 * collide with an obstacle
				 *****************************************/
				if (testObstacleCollision(ob)) {
					event.set(AgentManager.AM_COLLISION_OB);
					state.obCollisionResponse(this, ob);
				}
			}
		}
		
		if(obsInView.size() != 0){
			behavior.obstacleAvoidance(obsInView, 2);
		}
		else{
			behavior.offObstacleAvoidance();
		}	
	}
	
	
	protected void drawViewField(Graphics2D g){
		g.setStroke(basicStroke);
		
		g.setPaint(new Color(180, 180, 220, 50));
		g.fill(viewField);
		
		g.setPaint(new Color(211, 211, 211, 50));
		g.fill(densityCircle);
		
		g.setPaint(Color.GRAY);
		g.draw(getViewFieldBBox());
		g.draw(viewField);	
	}
	
	
	protected void drawWanderCircle(Graphics2D g){
		g.setStroke(basicStroke);
		
		Vector2f wCC = behavior.getWanderCircleCenter();
		Vector2f wanderTarget = behavior.getWanderTarget();
		
		if(wanderTarget.length() == 0){
			return;
		}
			   
		wanderCircle = behavior.getWanderCircle();
		
		g.setColor(new Color(32, 178, 170 ));
		g.setStroke(new BasicStroke(1));

		//draw wander Center and Circle
		g.fillOval((int)wCC.x -1 , (int)wCC.y -1, 2, 2);
		g.draw(wanderCircle);

		//draw wander target
		g.setColor(new Color(58, 95, 205 ));
		g.fillOval((int)wanderTarget.x-2, (int)wanderTarget.y-2, 4, 4);
	}
	
	
	protected void drawBodyShape(Graphics2D g, boolean isBorder){
		if(!isAlive){
			AffineTransform at = new AffineTransform();
			Image img = ImageLoader.deadImg.getImage();
			float imgScale = 1;
			at.translate(vPosition.x - (img.getWidth(null) * 0.5 * imgScale), (vPosition.y - img.getHeight(null) * 0.5 * imgScale ));
			//at.scale(imgScale, imgScale);
			g.drawImage(img, at, null);
		}
		else{
			if(isBorder){
				g.setStroke(borderStroke);
			}else{
				g.setStroke(basicStroke);
			}	
			
			/**************************************************************************************
			 * If I am within the neighborhood of the agent which has being selected, 
			 * I will highlight myself in pink color.
			 *************************************************************************************/
			if(isNeighbor) {		
				
				Rectangle rect = this.getRegion().getBounds();
				rect.setFrame(rect.getX() - 2, rect.getY() - 2, rect.getWidth()+4, rect.getHeight()+4);
				
				g.setPaint(new Color(0, 0, 0));
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
				
				g.setPaint(new Color(0, 240, 0));
				g.fill3DRect(rect.x, rect.y, rect.width, rect.height, true);
				isNeighbor = false;
			}	
			
			
			
			g.setPaint(state.getColor());
			g.fill(personalSpace);

			g.setPaint(colorOfPS);		
			g.draw(personalSpace);
			
	
			/*******************************************		
			 * draw the direction vector of the agent.
			 *******************************************/
			g.setPaint(Color.BLACK);
			g.setStroke(new BasicStroke(2));	
			orientation.setLine(vPosition.x, vPosition.y, vPosition.x + vVelocity.x, vPosition.y + vVelocity.y);		
			g.draw(orientation);
		}	
	}
	
	
	protected void drawGroupId(Graphics2D g){
		
		g.setPaint(getColorOfPs());
		g.fill3DRect((int)(vPosition.x - width * 0.5), (int)vPosition.y + 10, width+3, 5, true);
		
		g.setStroke(basicStroke);
		
		//draw rectangle
		g.setPaint(Color.BLACK);
		g.draw3DRect((int)point_TopLeft[0], (int)point_TopLeft[1], width+3, height+3, false);
		
		g.setPaint(new Color(groupColor_R, groupColor_G, groupColor_B, 200));
		g.fill3DRect((int)point_TopLeft[0], (int)point_TopLeft[1], width+3, height+3, true);
		
		
		//draw number
		g.setPaint(Color.black);
	    Font font = new Font("Arial", Font.BOLD, 10);
	    g.setFont(font);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    if(groupId >= 10){
	    	g.drawString(String.valueOf(groupId), point_TopLeft[0] + width/2 - 5, point_TopLeft[1] + height/2 + 4);
	    }else{
	    	g.drawString(String.valueOf(groupId), point_TopLeft[0] + width/2 - 1, point_TopLeft[1] + height/2 + 4);
	    }	
	}
	
	protected void drawAgentId(Graphics2D g){
		
		g.setPaint(getColorOfPs());
		g.fill3DRect((int)(vPosition.x - width * 0.5), (int)vPosition.y + 10, width+3, 5, true);
		
		g.setStroke(basicStroke);
		
		//draw rectangle
		g.setPaint(Color.GREEN);
		g.draw3DRect((int)point_TopLeft[0], (int)point_TopLeft[1], width+3, height+3, false);
		
		g.setPaint(new Color(0, 0, 0, 200));
		g.fill3DRect((int)point_TopLeft[0], (int)point_TopLeft[1], width+3, height+3, true);
		
		
		//draw number
		g.setPaint(Color.GREEN);
		Font font;
		
		if(id < 100){
			font = new Font("Arial", Font.BOLD, 10);
		}else{
			font = new Font("Arial", Font.BOLD, 8);
		}	
		
	    g.setFont(font);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    if(id >= 10){
	    	g.drawString(String.valueOf(id), point_TopLeft[0] + width/2 - 5, point_TopLeft[1] + height/2 + 4);
	    }else{
	    	g.drawString(String.valueOf(id), point_TopLeft[0] + width/2 - 1, point_TopLeft[1] + height/2 + 4);
	    }	
	}
	
	
	/************************************************
	 *	To display the neighborhood for testing. 
	 ************************************************/
	protected void drawNeighborHoodMark(Graphics2D g) {
		if(neighborhoodMark == null)
			return;
		
		g.setStroke(basicStroke);

		//draw the grid which around the last agent position.
		g.setPaint(new Color(250, 0, 0));
		
		
		int side = GridManager.SIDE_LENGTH;
		int rectWidth = 10;
		int halfRectWidth = (int)(rectWidth * 0.5);
		
		//Left Top Corner
		g.fillRect(neighborhoodMark.LT_X * side - halfRectWidth, 
				   neighborhoodMark.LT_Y * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);
		
		//Left Bottom Corner
		g.fillRect(neighborhoodMark.LT_X * side - halfRectWidth, 
				  (neighborhoodMark.BR_Y+1) * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);
		

		//Right Top Corner
		g.fillRect((neighborhoodMark.BR_X+1) * side - halfRectWidth, 
				   neighborhoodMark.LT_Y * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);

		//Right Bottom Corner
		g.fillRect((neighborhoodMark.BR_X+1) * side - halfRectWidth, 
				   (neighborhoodMark.BR_Y+1) * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);
		
		drawVSRatio(neighborhoodMark.LT_X * side, neighborhoodMark.LT_Y * side, g);
	}
	
	
	protected void drawInfoBoard(Graphics2D g){
		
		g.setStroke(basicStroke);
		
		Rectangle infoBoard = new Rectangle((int)vPosition.x + 15, (int)vPosition.y - 85, 120, 90);
		
		g.setColor(new Color(0, 20, 0));
		g.setStroke(new BasicStroke(2));
		g.drawRoundRect(infoBoard.x, infoBoard.y, infoBoard.width, infoBoard.height, 10, 10);
				
		g.setColor(new Color(0, 60, 0, 240));
		g.fillRoundRect(infoBoard.x, infoBoard.y, infoBoard.width, infoBoard.height, 10, 10);
		
		g.setColor(new Color(0, 250, 0));
		Font font = new Font("Arial", Font.PLAIN, 12);
		g.setFont(font);
		
		int leftTopX = 10;
		int leftTopY = 20;
		int shiftY	 = 15;
		g.drawString("stamina:           " + String.valueOf(stamina),infoBoard.x + leftTopX, infoBoard.y + leftTopY);
		//g.drawString("point:           " + String.valueOf(getEmotionArousalPoint()),infoBoard.x + leftTopX, infoBoard.y + leftTopY);
		g.drawString("alpha:          " +  String.valueOf(getAlphaCounter()), 		infoBoard.x + leftTopX, infoBoard.y + leftTopY + shiftY *1);
		g.drawString("beta:            " + String.valueOf(getBetaCounter()), 		infoBoard.x + leftTopX, infoBoard.y + leftTopY + shiftY *2);
		g.drawString("threshold:   " +	   String.valueOf(threshold), 				infoBoard.x + leftTopX, infoBoard.y + leftTopY + shiftY *3);
		g.drawString("rationality:   " +   String.valueOf(currentRationValue),		infoBoard.x + leftTopX, infoBoard.y + leftTopY + shiftY *4);
	}

	protected void drawVSRatio(int ltX, int ltY, Graphics2D g){
		g.setStroke(basicStroke);
	    Font font = new Font("Arial", Font.ITALIC, 16);
	    g.setFont(font);
	    g.setPaint(new Color(0, 0, 60));
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.drawString(String.valueOf(num_sameParty) + "  v.s.  " + String.valueOf(num_diffParty), ltX + 10, ltY);
	}
	
}
