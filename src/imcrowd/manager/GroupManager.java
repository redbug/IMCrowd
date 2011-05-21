package imcrowd.manager;

import imcrowd.basicObject.SocialSkills;
import imcrowd.basicObject.agent.normalAgent.Follower;
import imcrowd.basicObject.agent.normalAgent.Leader;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.engine.Controller;
import imcrowd.engine.Engine;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;
import imcrowd.ui.ExperimentParameterTab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Random;


public class GroupManager implements Colleague{
	private Mediator mediator;
	
	private Engine engine;	
	private	AgentManager agentManager;
	
	static int idCounter = 0;			
	
	final int GAP = 3;
	final int STALL_NUM = 9;

	boolean isDrawTemplate;
	
	int groupNumber;
	
	Point[] stalls;
	BasicStroke basicStroke;
	
	
	Controller ct = Controller.getInstance();
	ExperimentParameterTab param = ExperimentParameterTab.getInstance();
	
	// for Engine
	public GroupManager(){
		isDrawTemplate = false;
		stalls = new Point[STALL_NUM];
		basicStroke = new BasicStroke(1);
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		agentManager = engine.getAgentManager();
	}
	
	public void setManagerMethod() {}
	
	public void reset(){
		idCounter = 0;
	}
	
	public boolean isDrawTemplate() {
		return isDrawTemplate;
	}

	public void setDrawTemplate(boolean isDrawTemplate) {
		this.isDrawTemplate = isDrawTemplate;
	}
	
	
	public void createGroup(Point cursorPos, int agentWidth){
		
		int groupId = getNewId();
		generateGroupNumber();
		
		int stallWidth = agentWidth + (2 * GAP);
		Point leftCornerPos = new Point(cursorPos.x - stallWidth, 
										cursorPos.y - stallWidth);
		
		for(int x=0; x<3; x++){    
			for(int y=0; y<3; y++){	 
				stalls[x * 3 + y] =  new Point(	leftCornerPos.x + (x * stallWidth), 
												leftCornerPos.y + (y * stallWidth)); 
			}
		}
		
		shuffle();
		

		SocialSkills sk[] = new SocialSkills[groupNumber];
		
		float total;
		float maxSocialSkill = 0;
		int leaderIndex = 0;
		
		for(int i=0; i<groupNumber; i++){
			sk[i] = new SocialSkills();
			total = sk[i].getTotal();
		
			if(maxSocialSkill < total){
				maxSocialSkill = total;
				leaderIndex = i;
			}
		}
		
		NormalAgent ag;
		
		/******************************
		 * Add a leader to AgentManager
		 ******************************/
		NormalAgent leader = new Leader( stalls[leaderIndex].x, stalls[leaderIndex].y, 270, groupId);
		leader.setSocialSkill(sk[leaderIndex]);
		agentManager.addAgent(leader);
		
		
		/******************************
		 * Add followers to AgentManager
		 ******************************/
		for(int i=0; i<groupNumber; i++){
			if(i != leaderIndex){			
				ag = new Follower(stalls[i].x, stalls[i].y, 270, groupId, leader);
				((Leader)leader).addFollower((Follower)ag);
				
				ag.setSocialSkill(sk[i]);
				agentManager.addAgent(ag);
			}
		}
	}
	
	private void generateGroupNumber(){

		int mean 	= param.getParameterValue(ExperimentParameterTab.GROUP_MEAN);
		int sd 		= param.getParameterValue(ExperimentParameterTab.GROUP_SD);
		
		
		Random rand = Engine.rand;
		
		groupNumber = (int)Math.round(rand.nextGaussian() * sd) + mean;		//normal distribution[MEAN,STANDER_DEVIATION]
		if(groupNumber < 1){
			groupNumber = 1;
		}
		else if(groupNumber > STALL_NUM){
			groupNumber = STALL_NUM;
		}
	}
	
	private void shuffle(){
		int n;
		Point tmp;
		Random rand = Engine.rand;
		
		for(int i=0; i<stalls.length; i++){
			n = rand.nextInt(9);
			
			tmp = stalls[i];
			stalls[i] = stalls[n];
			stalls[n] = tmp;
		}
	}
	
	static public int getCurrentId(){
		return idCounter;
	}
	
	static public void setId(int id){
		idCounter = id;
	}
	
	static public int getNewId(){
		return ++idCounter;
	}
	
	public void paintGroupTemplate(Point cursorPos, int agentWidth, Graphics2D g) {
		
		g.setStroke(basicStroke);
		
		int stallWidth = agentWidth + (2 * GAP);
		int stallWidthHalf = stallWidth /2;
		
		Point leftCornerPos = new Point(cursorPos.x -  stallWidthHalf * 3, 
										cursorPos.y -  stallWidthHalf * 3);
		
		int unit = STALL_NUM / 3;
		int lineNum = unit + 1;
		
//		g.setStroke(new BasicStroke(3));

		
		Line2D line = new Line2D.Double();
		
		/******************************
		 * draw shadow in green
		 ******************************/
		g.setColor(new Color(0, 139, 0, 50));
		g.fill3DRect(leftCornerPos.x, leftCornerPos.y, stallWidth*3, stallWidth*3, true);
	
		g.setColor(Color.green);
		/******************************
		 * draw horizontal line		
		 ******************************/
		int l_x, l_y, r_x, r_y;
		
		for(int i=0; i< lineNum; i++){
			l_x = leftCornerPos.x;
			l_y = leftCornerPos.y + i * stallWidth;
			r_x = l_x + stallWidth * unit;
			r_y = l_y;
			
			line.setLine(l_x, l_y, r_x, r_y);
			g.draw(line);
		}
		
		/******************************
		 * draw vertical line
		 ******************************/
		int t_x, t_y, b_x, b_y;
		
		for(int i=0; i <lineNum; i++){
			t_x = leftCornerPos.x + i * stallWidth;
			t_y = leftCornerPos.y;
			b_x = t_x;
			b_y = t_y + stallWidth * unit;
			
			line.setLine(t_x, t_y, b_x, b_y);
			g.draw(line);
		}
		
	}

}
