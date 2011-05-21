package imcrowd.basicObject;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.manager.GridManager;
import imcrowd.ui.AgentTab;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2f;


public class Grid {

	List<Agent> agentList = Collections.synchronizedList(new ArrayList<Agent>());
	List<Obstacle> obList = Collections.synchronizedList(new ArrayList<Obstacle>());
	
	private int id;
	private int colorIndex = -1;
	private float contextValue;
	private float aveRationality = 0;
	private float aveSuperiority = 0;
	private float entropy = 0;
	private float density = 0;

	private Point2D leftTopPoint;			//the left top corner of the grid.
//	public Point2D rightBottomPoint;		//the right bottom corner of the grid.
	private Point2D flowVector;				//the flow vector on the left top corner of the grid.
	
	DecimalFormat df = new DecimalFormat("##.##");
	
	/*
	int count;
	int floorX;
	int floorY;
	int ceilX;
	int ceilY;
	*/

	public Grid(int id, Point2D ltP,  Point2D vector){
		this.id = id;
		contextValue = 0;
		agentList = new ArrayList<Agent>(); 
		leftTopPoint 	= ltP;
//		rightBottomPoint = rbP;
		this.flowVector	= vector;
	}
	
	public Vector2f getCenterPosition(){
		int sideLen = GridManager.SIDE_LENGTH;
		return new Vector2f((float)leftTopPoint.getX() + sideLen * 0.5f, (float)leftTopPoint.getY() + sideLen * 0.5f);
	}
	
	public int getId(){
		return id;
	}
	
	public float getDensity(){
		return density;
	}
	
	public float getEntropy() {
		return entropy;
	}
	
	public float getAveRationality() {
		return aveRationality;
	}
	
	public float getAveSuperiority(){
		return aveSuperiority;
	}
	
	
	public void computeDensity(){
		int size = agentList.size();
		float newDensity = 0f;
		
		if(density == 0){
			// 00
			if(size == 0){
				colorIndex = -1;
				return;
			}
			// 01
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){					
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							newDensity++;
						}else{ //victim
							size--;
						}
					}
				}
				
				if(size != 0){					
					newDensity /=  16;
					
					density = newDensity;				
				}else{
					colorIndex = -1;
					return;
				}		
			}	
		}	
		else{
			// 10
			if(size == 0){
				density *= 0.95;
			}
			// 11
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){					
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							newDensity++;
						}else{ //victim
							size--;
						}
					}
				}
				
				
				if(size != 0){
					newDensity /=  16;
					
					density = (float) (newDensity * 0.1 + density * 0.9);
				}else{
					density *= 0.95;
				}
			}	
		}
		
		if(density < 0.001){
			density = 0;
			colorIndex = -1;
			return;
		}
				
		//colorIndex = (int)(entropy * 199 );
		colorIndex = (int)( (100 - (density * 100)) * 2 );
	}
	
	
	public void computeEntropy(){
		int size = agentList.size();
		float newEntropy = 0f;
		int[] partyNumArray = new int[AgentTab.COLOR_STRING.length];
		
		if(entropy == 0){
			// 00
			if(size == 0){
				colorIndex = -1;
				return;
			}
			// 01
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){					
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							partyNumArray[ag.getColorId()]++;
						}else{ //victim
							size--;
						}
					}
				}
				
				if(size != 0){					
					for(int i=0; i < partyNumArray.length; i++){
						if(partyNumArray[i] != 0){
							float probability = (float)partyNumArray[i]/size;
							newEntropy +=  probability * Math.log(probability);	
						}
					}
					newEntropy *= -1;
					
					entropy = newEntropy;				
				}else{
					colorIndex = -1;
					return;
				}		
			}	
		}	
		else{
			// 10
			if(size == 0){
				//entropy *= 1.005;
				entropy *= 0.95;
			}
			// 11
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){					
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							partyNumArray[ag.getColorId()]++;
						}else{ //victim
							size--;
						}
					}
				}
				
				
				if(size != 0){
					for(int i=0; i < partyNumArray.length; i++){
						if(partyNumArray[i] != 0){
							float probability = (float)partyNumArray[i]/size;
							newEntropy +=  probability * Math.log(probability);	
						}
					}
					newEntropy *= -1;
					
					entropy = (float) (newEntropy * 0.1 + entropy * 0.9);
				}else{
					//entropy *= 1.005;
					entropy *= 0.95;
				}
			}	
		}
		
		if(entropy < 0.001){
			entropy = 0;
			colorIndex = -1;
			return;
		}
		
		//colorIndex = (int)(entropy * 199 );
		colorIndex = (int)( (100 - (entropy * 100)) * 2 );
	}
	
	
	
	public void computeSuperiority(){
		int size = agentList.size();
		float newSuperiority = 0;
		
		if(aveSuperiority == 0){
			// 00
			if(size == 0){
				colorIndex = -1;
				return;
			}
			// 01
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){					
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							if(ag.isSuperiority()){
								newSuperiority++;
							}
						}else{ //victim
							size--;
						}
					}
				}
				
				if(size != 0){					
					newSuperiority /= size;
					aveSuperiority = newSuperiority;					
				}else{
					colorIndex = -1;
					return;
				}		
			}	
		}
		else{
			// 10
			if(size == 0){
				aveSuperiority *= 0.95;
			}
			// 11
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){					
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							if(ag.isSuperiority()){
								newSuperiority++;
							}
						}else{ //victim
							size--;
						}
					}
				}
				
				
				if(size != 0){
					newSuperiority /= size;
					aveSuperiority = (float) (newSuperiority * 0.1 + aveSuperiority * 0.9);
				}else{
					aveSuperiority *= 0.95;
				}
			}	
		}
		
		
		if(aveSuperiority <= 0.05){
			aveSuperiority = 0;
			colorIndex = -1;
			return;
		}
		
		colorIndex = (int)( (100 - (aveSuperiority * 100)) * 2 );
	}
	
	public void computeAveRationality(){
		int size = agentList.size();
		float newAveRationality = 0;
		
		
		if(aveRationality == 0){
			// 00
			if(size == 0){
				colorIndex = -1;
				return;
			}
			// 01
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							newAveRationality += ag.getCurrentRationValue();
						}else{ //victim
							size--;
						}
					}
				}
				
				if(size != 0){
					newAveRationality /= size;
					aveRationality = newAveRationality;
				}else{
					colorIndex = -1;
					return;
				}		
			}	
		}
		else{
			// 10
			if(size == 0){
				aveRationality *= 1.005;
			}
			// 11
			else{
				/*******************************************************
				 * don't need to count in any special agent and victim
				 *******************************************************/
				for(Agent ag:agentList){
					if(ag.isSpecial()){
						size--;
					}
					else{
						if(ag.isAlive()){
							newAveRationality += ag.getCurrentRationValue();
						}else{ //victim
							size--;
						}
					}
				}
				
				if(size != 0){
					newAveRationality /= size;
					aveRationality = (float) (newAveRationality * 0.1 + aveRationality * 0.9);
				}else{
					aveRationality *= 1.005;
				}
			}	
		}
		
		if(aveRationality > 0.9){
			aveRationality = 0;
			colorIndex = -1;
			return;
		}
		
		
		// aveRationality can not less than 0.1
		colorIndex = (int)((aveRationality * 1000 - 100) * 199 / 800 );
		
	}
	
	public void addContextValue(float v){	
		for(Agent ag:agentList){
			ag.addCurrentRationValue(v);
		}
		contextValue = Float.valueOf(df.format(contextValue += v));
	}
	
	public void subtractContextValue(float v){
		for(Agent ag:agentList){
			ag.subtractCurrentRationValue(v);
		}
		contextValue = Float.valueOf(df.format(contextValue -= v));
	}
	
	public boolean addObstacle(Obstacle ob) {
		if(obList.contains(ob))
			return false;
		else {
			obList.add(ob);			
			return true;
		}
	}
	
	public void removeObstacle(Obstacle ob) {
		obList.remove(ob);
	}
	
	public void clearObstacleList() {
		contextValue = 0;
		obList.clear();
	}
	
	public List<Obstacle> getObstacleList()
	{
		return obList;
	}
	
	
	
	public boolean addAgent(Agent ag) {
		if(agentList.contains(ag))
			return false;
		else {
			if(contextValue != 0){
				ag.addCurrentRationValue(contextValue);
			}	
			agentList.add(ag);
			return true;
		}
	}
	
	public void removeAgent(Agent ag) {
		if(contextValue != 0){
			ag.subtractCurrentRationValue(contextValue);
		}	
		agentList.remove(ag);
	}
	
	public void clearAgentList() {
		agentList.clear();
	}
	
	public Point2D getFlowVector() {
		return flowVector;
	}
	
	public List<Agent> getAgentList()
	{
		return agentList;
	}
	
	/***********************************************
	 * flow vector will fade gradually
	 ***********************************************/
	public void fade() {
		double vx = flowVector.getX();
		double vy = flowVector.getY();
		double px = leftTopPoint.getX();
		double py = leftTopPoint.getY();
		
		flowVector.setLocation(vx - (vx - px)  * 0.01, 
							   vy - (vy - py)  * 0.01 );
	}
	
	//for testing
	public void printAgentId() {
		for(Agent a: agentList) {
			System.out.print(a.getId()+" ");	
		}
		System.out.println("");
	}
	
	public void paintGrid(Graphics2D g) {

		if(GridManager.heatMapType != GridManager.HEATMAP_NONE){
			if(colorIndex != -1){
				g.setPaint(GridManager.heatMapColors[colorIndex]);
				g.fillRect((int)leftTopPoint.getX(), (int)leftTopPoint.getY(), GridManager.SIDE_LENGTH, GridManager.SIDE_LENGTH);
			}
		}
		
		if(GridManager.isDrawContextValue){
		    Font font = new Font("Arial", Font.ITALIC, 10);
		    g.setFont(font);
		    g.setPaint(Color.GRAY);
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g.drawString(String.valueOf(contextValue), (int)leftTopPoint.getX() + GridManager.SIDE_LENGTH - 28 , (int)leftTopPoint.getY() + GridManager.SIDE_LENGTH -2);
		    
		    // for testing rationality and superiority
		    // g.drawString(String.valueOf(colorIndex), (int)leftTopPoint.getX() + GridManager.SIDE_LENGTH - 28 , (int)leftTopPoint.getY() + GridManager.SKE_LENGTH -2);
		}
		/********************************
		 * For Drawing Flow Vector
		 ********************************/
		
//		Line2D line = new Line2D.Double();
//		
//		g.setPaint(new Color(0, 0, 250));
//		g.setStroke(new BasicStroke(3));
//		line.setLine(leftTopPoint, leftTopPoint);
//		g.draw(line);
//
//		fade();			//vector fading gradually.
//		
//		
//		/***********************
//		 * directional vector
//		 ************************/
//		//g.setPaint(new Color(0, 0, 250));
//		line.setLine(leftTopPoint, flowVector);
//		g.setStroke(new BasicStroke(1));
//		g.draw(line);
	}
	
}
