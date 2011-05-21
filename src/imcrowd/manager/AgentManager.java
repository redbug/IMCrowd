package imcrowd.manager;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.GroupMind;
import imcrowd.basicObject.agent.behavior.groupMind.riot.Riot;
import imcrowd.basicObject.agent.state.State;
import imcrowd.engine.Engine;
import imcrowd.io.report.StatisticReport;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;
import imcrowd.ui.AgentTab;
import imcrowd.ui.GlobalTab;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

import java.util.Iterator;
import java.util.List;

import javax.vecmath.Vector2f;



public class AgentManager implements Colleague{

	private Mediator mediator;
	
	public static final int  AM_COLLISION_AG = 0,      // collision with another agent
							 AM_COLLISION_OB = 1,	   // collision with obstacle
							 AM_INVIEW 		 = 2;      // there is an agent in my view
	
	
	public static final int  NORMAL_MODE = 0,
	 						 INFO_MODE	 = 1,
	 						 GROUP_MODE	 = 2,
	 						 STATE_MODE	 = 3;

	/******************************************
	 * Five-number summary of the superiority
	 ******************************************/
	public static final int FIVE_MIN	= 0,
							FIVE_Q1		= 1,
							FIVE_MEDIAN	= 2,
							FIVE_Q3		= 3,
							FIVE_MAX	= 4;

	
	/*************************************
	 * Statistic of Party Information
	 *************************************/
	public static final int NUM_PARTY_INFO	= 8;	
	
	public static final int LEAVER_INFO			= 0,		//monotony info
							VICTIM_INFO 		= 1,		//monotony info
							PARTICIPANT_INFO	= 2,		//monotony info
							DPARTY_INFO			= 3,		//dynamic info(average)
							SPARTY_INFO			= 4,		//dynamic info(average)
							SUPERIORITY_INFO	= 5,		//dynamic info(counter)
							INFERIORITY			= 6,		//dynamic info
							INFERIORITY_POLICE	= 7;		//dynamic info
	
	public static final String partyInfoStrings[] =
	{
		"Leaver",
		"Victim",
		"Participant",
		"Different party",
		"Same party",
		"Superiority",
		"Inferiority",
		"inferiority nearby police"
	};
	
	public static int partyInfo[][] = new int[NUM_PARTY_INFO][AgentTab.COLOR_STRING.length];
	
	public static final int NUM_FIVE_SUMMARY	= 5;	
	
	public static final String fiveNumberStrings[] = 
	{
		"MIN",	
		"Q1",	
		"MEDIAN",
		"Q3",
		"MAX"
	};
	
	public static int fiveNumber[][] = new int[NUM_FIVE_SUMMARY][AgentTab.COLOR_STRING.length];
	
	int idCounter;								      
	int visualMode;
	
	/*****************************************************************************************************
	 * I maintain two lists. One of which for updating and the other one for traversal.
	 * The dirtyBit will be set to true if there is a modification on the agList_update.
	 * The function check() makes sure that the agList_traversal is consistent with the agList_update, 
	 * and sets the dirtyBit to false.
	 *****************************************************************************************************/
	boolean dirtyBit;		
	List<Agent> agList_update;
	List<Agent> agList_traversal;
	
	Engine engine;
	GridManager gridManager;	
	StatisticReport statisticReport;
		
	ArrayList<Agent> agInViewTmp;	 //	For testing the consistency between neighbors and all agents
		
	public AgentManager(){
		visualMode = GlobalTab.getInstance().getVisualMode();
		dirtyBit = false;		

		idCounter = 0;				
		agList_update 	= new ArrayList<Agent>();		//the agent list which for updating.
		agList_traversal = new ArrayList<Agent>();		//the agent list which for traversing.
		
		for(int i=0; i<NUM_PARTY_INFO; i++){
			partyInfo[i] = new int[AgentTab.COLOR_STRING.length]; 
		}
		
		//partyInfo[PARTICIPANT_INFO] = calculate_PartyInformation(); //get initial participant;
	
		statisticReport = new StatisticReport();
	}
		
	public int getNumberOfNormalAgent(){
		int num = 0;
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				num++;
			}
		}	
		return num;
	}
	
	public void resetThreshold(){
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				ag.resetThreshold();
			}
		}
	}
	
	public void resetRationality(){
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				ag.resetRationality();
			}
		}
	}
	
	
	public int getVisualMode() {
		return visualMode;
	}


	public void setVisualMode(int visualMode) {
		this.visualMode = visualMode;
	}


	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		gridManager = engine.getGridManager();
		//graph = engine.getGraph();
	}
	
	public void setManagerMethod() {}

	
	public int[] initialize_PartyInformation(){
		//int[] partyNumArray = new int[AgentTab.COLOR_STRING.length];
		
		int colorId;
		
		for(int i=0; i<AgentTab.COLOR_STRING.length; i++){
			partyInfo[PARTICIPANT_INFO][i] = 0;
		}
		
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				colorId = ag.getColorId();
				if(colorId >= 0){
					partyInfo[PARTICIPANT_INFO][colorId]++;
				}
			}
		}
		
		for(int i=0; i<AgentTab.COLOR_STRING.length; i++){
			if(partyInfo[PARTICIPANT_INFO][i] == 0){
				partyInfo[PARTICIPANT_INFO][i] = -1;
			}	
		}
		
		return partyInfo[PARTICIPANT_INFO];
	}
	
	public double[] calculate_CrowdStructure(){
		int gIdArray[] = new int[1000];
		
		int gId;
		
		double mean = 0;			//mean
		double sd = 0;				//standard deviation
		
		int numAgent = 0;			//the number of agent
		int numGroup = 0;			//the number of group
		
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				gId = ag.getGroupId();
				gIdArray[gId]++;
			}	
		}
		
		for(int i=0; i < gIdArray.length; i++ ){
			if(gIdArray[i] != 0){
				numGroup++;	
				numAgent += gIdArray[i];
				//report.append("Group" + i +" :" + gIdArray[i] + "\n");
			}
		}
		
		mean = (double)numAgent/numGroup;  
		
		double var;
		for(int i=0; i < gIdArray.length; i++ ){
			if(gIdArray[i] != 0){			
				var = gIdArray[i] - mean;
				sd += var * var;
			}
		}
		
		/********************
		 * variance
		 ********************/
		sd /= numGroup;
		
		/********************
		 * Standard Deviation
		 ********************/
		sd = Math.sqrt(sd);
		
		double result[] = new double[4];
		result[0] = numAgent;
		result[1] = numGroup;
		result[2] = mean;
		result[3] = sd;

		return result;
		
	}
	
	public double[] calculate_Attribute(){		
		double mean_rationality = 0;
		double mean_threshold 	= 0;
		
		double sd_rationality 	= 0;				
		double sd_threshold		= 0;
		int num = 0;
		
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				num++;
				mean_rationality += ag.getCurrentRationValue();
				mean_threshold += ag.getThreshold();
			}	
		}
		
		mean_rationality /= num;
		mean_threshold /= num; 
		
		double var;
		for (Agent ag: getAgentList()) {
			if(!ag.isSpecial()){
				var	= ag.getCurrentRationValue() - mean_rationality;
				sd_rationality += var * var;
				
				var	= ag.getThreshold() - mean_threshold;
				sd_threshold += var * var;
			}	
		}
		
		/****************************
		 * 	variance
		 ****************************/
		sd_rationality /= num;
		sd_threshold /= num;
		

		/***************************
		 * Standard Deviation
		 ***************************/
		sd_rationality = Math.sqrt(sd_rationality);
		sd_threshold = Math.sqrt(sd_threshold);
		
		double result[] = new double[4];
		result[0] = mean_rationality;
		result[1] = sd_rationality;
		result[2] = mean_threshold;
		result[3] = sd_threshold;
		
		return result;
	}
				
	
	public void reset()
	{
		idCounter = 0;
		agList_update.clear();
		agList_traversal.clear();
		for( int i=0; i<NUM_PARTY_INFO; i++){
			for (int j=0; j<AgentTab.COLOR_STRING.length; j++){
				partyInfo[i][j] = 0;				
			}
		}
	}
	
	public void resetDynamicInfo(){
		for (int i=0; i<AgentTab.COLOR_STRING.length; i++){
			partyInfo[DPARTY_INFO][i] = 0;
			partyInfo[SPARTY_INFO][i] = 0;
			partyInfo[INFERIORITY][i] = 0;
			partyInfo[INFERIORITY_POLICE][i] = 0;
			partyInfo[SUPERIORITY_INFO][i] = 0;
			
			fiveNumber[FIVE_MIN][i] 	= 0;
			fiveNumber[FIVE_Q1][i] 		= 0;
			fiveNumber[FIVE_MEDIAN][i] 	= 0;
			fiveNumber[FIVE_Q3][i] 		= 0;
			fiveNumber[FIVE_MAX][i] 	= 0;
		}		
	}
	
	/******************************** 
	 * Id Counter 
	 ********************************/
	public int getNewId(){
		return ++idCounter;
	}
	
	synchronized public List<Agent> getAgentList(){
		check();
		List<Agent> list =new ArrayList<Agent>();
		list.addAll(agList_traversal);
		return list;
	}

	synchronized public void addAgent(Agent ag) {
		if (ag != null) {
			dirtyBit = true;
			agList_update.add(ag);
			gridManager.addAgent(ag);
		}	
		else
			System.out.println("agent added filed!!");
	}
	
	synchronized public void removeOne(Agent ag){	
		dirtyBit =true;
		agList_update.remove(ag);					
		gridManager.removeAgent(ag);	
	}
	
	/*******************************************************
	 * This function make sure that the agList_traversal 
	 * is consistent with the agList_update. 
	 *******************************************************/
	private void check() {
		if(dirtyBit) {
			agList_traversal.clear();
			agList_traversal.addAll(agList_update);
			dirtyBit = false;
		}
	}
	
	
	public Agent isPointInAgent(Point2D p)
	{
		Agent ag;
		//At first, find the agents in grid.
		List<Agent> agListInGrid = gridManager.getAgentInGrid(p.getX(), p.getY());

		//Secondly, traverse the agents in grid.
		for (Iterator<Agent> it = agListInGrid.iterator(); it.hasNext();) {
				ag = it.next();
				if (ag.isPointInside(p)) {
					return ag;
				}
		}
	    return null;
	}
	
	
	//	For testing the consistency between neighbor and all agents - 2008/10/11
//	public int testAgentInView(Agent testAgent, ArrayList<Agent> agentList){
//		int N=0;
//		Agent otherAgent; 
//		agInViewTmp = new ArrayList<Agent>();
//		if(agentList != null) {
//			for (int i = 0; i < agentList.size(); i++) {
//				otherAgent = agentList.get(i);
//				if (otherAgent == testAgent)
//					continue;									
//				
//				if (testAgent.isAgentInView(otherAgent)) { 	 	
//					N++;
//					agInViewTmp.add(otherAgent);
//				}
//			}
//		}	
//		return N;
//	}
	
	//	For testing the consistency between neighbor and all agents - 2008/10/11
//	public void debug_gridManager(Agent ag, ArrayList<Agent> neighbor){
//					
//		int aa = testAgentInView(ag, neighbor);				
//		int b = testAgentInView(ag, (ArrayList)getAgentList());	
//		
//		if (aa!=b){
//			ag.selected(true);
//			engine.setPlay(false);
//			System.out.println("GidManager: "+aa);
//			System.out.println("Whole: "+b);
//			for(Agent k: agInViewTmp){
//				System.out.print(k.getId()+",");
//			}
//			System.out.println();
//			for(Agent gg: neighbor){
//				System.out.print(gg.getId()+",");
//			}
//		}	
//	}
		
	public StatisticReport getStatisticReport(){
		return statisticReport;
	}
	
	public String report_State(boolean isTitle){
	
		StringBuffer report = new StringBuffer();
		
		for(int i=0; i < StatisticReport.NUM_STATE ;i++){
			report.append(statisticReport.report_State(i, isTitle));
		}
		
		return report.toString();
		
	}
	
	public String report_Riot(boolean isTitle){

		StringBuffer report = new StringBuffer();
		
		for(int i=0; i < StatisticReport.NUM_RIOT ;i++){
			report.append(statisticReport.report_Riot(i, isTitle));
		}
		return report.toString();
	}	
		
	public String report_PartyInfo(boolean isTitle){
		StringBuffer report = new StringBuffer();
		
		
		for(int i=0; i< AgentTab.COLOR_STRING.length; i++){	
			if(partyInfo[PARTICIPANT_INFO][i] >= 0){	// available color 
				
				//print five number summary here
				for(int j=0; j<NUM_FIVE_SUMMARY; j++){	// five number summary
					if(isTitle){
						report.append(String.format("%s \t\t%d\n", fiveNumberStrings[j] + ":", fiveNumber[j][i]));
					}else{
						report.append(fiveNumber[j][i] + " ");
					}
				}
				
				for(int j=0; j<NUM_PARTY_INFO; j++){	// all kind of info
					if(isTitle){
						report.append(String.format("%s \t\t%d\n", partyInfoStrings[j] + ":", partyInfo[j][i]));
					}else{
						report.append(partyInfo[j][i] + " ");
					}
				}
				
			}
		}
		
	
		return report.toString();
	}


    public void updateAgent()
	{   
		int stateId;
	    
		Vector2f oldPosition;	      
	    ArrayList<Agent> neighbor;   
	    
	    statisticReport.reset();
	    resetDynamicInfo();
	    
	    ArrayList<Integer> superiorityDegreeList[] = new ArrayList[AgentTab.COLOR_STRING.length];
	    
	    for(Agent ag: getAgentList()){
					
			oldPosition = new Vector2f(ag.getPosition()); 	
			
			boolean isExist = ag.updateState(); 					
			
			int colorId = ag.getColorId();
			
			// leaver
			if(!isExist){				
				partyInfo[LEAVER_INFO][colorId]++;
				partyInfo[PARTICIPANT_INFO][colorId]--;
								
				removeOne(ag);
				continue;
			}
			
			gridManager.occupiedGrid(ag);
			

			// get my neighbors 
			neighbor  = gridManager.getNeighbor(ag);

			//debug_gridManager(ag,neighbor);  //	For testing the consistency between neighbor and all agents - 2008/10/11
			
			//highlight my neighbors.
			if(ag.isDrawView()){
				if(neighbor != null) {
					for(Agent a: neighbor) {
						if(a != ag) a.setIsNeighbor();
					}
				}	
			}
			
			// only check my neighbors
			BitSet event = ag.interactWithOthers(neighbor);

			if(event.get(AM_COLLISION_AG) || event.get(AM_COLLISION_OB)){
				ag.setPosition(oldPosition);
			}

			/**************************************************
			 * update the statistic report
			 **************************************************/
			if(!ag.isSpecial()){					
				if(ag.isAlive()){
					int sParty = ag.getNumSameParty();
					int dParty = ag.getNumDiffParty();
					int diff   = sParty - dParty;
					partyInfo[SPARTY_INFO][colorId] += sParty;
					partyInfo[DPARTY_INFO][colorId] += dParty;
					
					if(superiorityDegreeList[colorId] == null){
						superiorityDegreeList[colorId] = new ArrayList<Integer>();
					}
					superiorityDegreeList[colorId].add(diff);					
					
					if(diff < 0){
						partyInfo[INFERIORITY][colorId]++;
						if(ag.isNearByPolice()){
							partyInfo[INFERIORITY_POLICE][colorId]++;
						}
					}
										
					if(ag.situationAnalysis() == Agent.SUPERIORITY){
						partyInfo[SUPERIORITY_INFO][colorId]++;
						ag.setSuperiority(true);
					}
					else{
						ag.setSuperiority(false);
					}
					
					
					/*********************************
					 * record state information
					 *********************************/
					stateId = ag.getState().getId();
					statisticReport.record_StateInfo(stateId);
					
					if(stateId == State.ENGAGED){
					
						/*********************************
						 * record riot information
						 *********************************/
						GroupMind collectiveBeh = ag.getCollectiveBehavior(); 
						
						if(collectiveBeh.getName().equals("Riot") ){
							int rId = ((Riot)collectiveBeh).getId();
							statisticReport.record_RiotInfo(rId);						
						}
					}
				}
				else{ //victim
					if(!ag.isVictimRegistry()){
						partyInfo[VICTIM_INFO][colorId]++;
						partyInfo[PARTICIPANT_INFO][colorId]--;
						ag.setVictimRegistry(true);
					}
				}	
			}	
		}
	    
	    for(int i=0; i< AgentTab.COLOR_STRING.length; i++){
	    	
	    	if(partyInfo[PARTICIPANT_INFO][i] > 0){
	    		
	    		/****************************************************
		    	 *  calculating the mean value of dParty and sParty.
		    	 ****************************************************/
	    		partyInfo[DPARTY_INFO][i] /= partyInfo[PARTICIPANT_INFO][i]; 
				partyInfo[SPARTY_INFO][i] /= partyInfo[PARTICIPANT_INFO][i];
				
				
				
				/****************************************************
		    	 *  calculating the five number of superiority
		    	 *  15 = 7 8th 7
		    	 *  14 = 7  |  7
		    	 *  13 = 6 7th 6
		    	 *  12 = 6  |  6
		    	 *  11 = 5 6th 5
		    	 *  10 = 5  |  5
		    	 ****************************************************/
		    	int size = superiorityDegreeList[i].size();
		    	
			    if(size >= 3 ){
			    	
			    	int mI, q1I, q3I, half;
				    
				    Collections.sort(superiorityDegreeList[i]);
				   
				    /**********************
				     * Min && Max
				     **********************/
				    fiveNumber[FIVE_MIN][i] = superiorityDegreeList[i].get(0);
				    fiveNumber[FIVE_MAX][i] = superiorityDegreeList[i].get(size-1);
				    
				    /**********************
				     * Median
				     **********************/
				    //odd
				    if(size % 2 != 0){
				    	mI = (size + 1) / 2;
				    	half = mI - 1;
				    	fiveNumber[FIVE_MEDIAN][i] = superiorityDegreeList[i].get(mI - 1);
				    }
				    //even
				    else{
				    	mI = size / 2;
				    	half = mI;
				    	fiveNumber[FIVE_MEDIAN][i] = (superiorityDegreeList[i].get(mI-1) + superiorityDegreeList[i].get(mI)) / 2;
				    }
				    
				    /*********************
				     * Q1 && Q3
				     *********************/
				    if(half % 2 != 0){
				    	q1I = (half + 1 ) / 2;
				    	q3I = mI + q1I;
				    	fiveNumber[FIVE_Q1][i] = superiorityDegreeList[i].get(q1I - 1);
				    	fiveNumber[FIVE_Q3][i] = superiorityDegreeList[i].get(q3I - 1);
				    }
				    else{
				    	q1I = half / 2;
				    	q3I = mI + q1I;
				    	fiveNumber[FIVE_Q1][i] = (superiorityDegreeList[i].get(q1I-1) + superiorityDegreeList[i].get(q1I)) / 2; 
				    	fiveNumber[FIVE_Q3][i] = (superiorityDegreeList[i].get(q3I-1) + superiorityDegreeList[i].get(q3I)) / 2;	
				    }
			    }    
	    	}	
	    }
	    
	}
	
	public void paintAllAgents(Graphics2D g) {
		if(agList_update.size() != 0) {
			for (Iterator<Agent> it = getAgentList().iterator(); it.hasNext();) {
				it.next().paint(g);
			}
		}	
	}

}
