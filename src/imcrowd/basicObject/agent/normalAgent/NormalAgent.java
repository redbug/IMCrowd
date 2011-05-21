package imcrowd.basicObject.agent.normalAgent;

import imcrowd.basicObject.SocialSkills;
import imcrowd.basicObject.agent.Agent;
import imcrowd.manager.AgentManager;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public abstract class NormalAgent extends Agent{	

	protected SocialSkills 			socialSkill;

	protected NormalAgent(float x, float y, float theta, int groupId){
		super(x, y, theta, groupId);
		isSpecial = false;
		
		point_TopLeft = new float[2];
		point_TopLeft[0] = vPosition.x - (float)width/2;
		point_TopLeft[1] = vPosition.y - (float)height/2;
		personalSpace 	= new Ellipse2D.Double(point_TopLeft[0], point_TopLeft[1], width, height);
	}
	
	protected abstract void drawTarget(Graphics2D g);
	protected abstract void drawBodyShape(Graphics2D g);
	
	public void setSocialSkill(SocialSkills s){
		this.socialSkill = s;
	}
	
	public  abstract void moveAroundRiot();
	
	public void disenchantedStateAction(){
		goHome();
	}
	
	protected void action()
	{
		state.loadFlockingWeights(this);
		state.action(this);
	}
	
	public void paint(Graphics2D g){
		
		g.setStroke(basicStroke);
		
		switch(agentManager.getVisualMode()){
			case AgentManager.NORMAL_MODE:
				
				if(isDrawView){
					
					drawTarget(g);
				    
					/* draw the view field */
					drawViewField(g);			
					
					
					/* draw the wander circle.*/
				    drawWanderCircle(g);
				    
				    /* draw the boundary markers of the neighborhood */ 
				    drawNeighborHoodMark(g);
				}
				
				drawBodyShape(g);
				
				break;
				
			case AgentManager.INFO_MODE:

				drawAgentId(g);
				
				if(isDrawView){
					drawInfoBoard(g);		
				}
				
				break;
				
			case AgentManager.GROUP_MODE:
				
				drawGroupId(g);
				
				break;
				
			case AgentManager.STATE_MODE:
				if(this.isAlive)
					state.drawFeatures(g, this);
				else
					drawBodyShape(g);
				break;
		
		}	
	}	
		
}
