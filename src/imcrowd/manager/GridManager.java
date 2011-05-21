package imcrowd.manager;

import imcrowd.basicObject.Grid;
import imcrowd.basicObject.Neighborhood;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.specialAgent.Police;
import imcrowd.basicObject.obstacle.InteractiveObstacle;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector2f;


public class GridManager implements Colleague{
	private Mediator mediator;

	public static int 	HEATMAP_NONE			=	0,
	  					HEATMAP_RATIONALITY		=	1,
	  					HEATMAP_SUPERIORITY		=	2,
	  					HEATMAP_ENTROPY			=	3,
	  					HEATMAP_DENSITY			=	4;
	
	public static boolean isDrawGrid = true;
	
	public static final int numColor = 200;
							
	public static final Color heatMapColors[] = new Color[numColor];
	
	public static final int SIDE_LENGTH = 40;
	
	public static boolean isDrawContextValue = false;
	public static int heatMapType = 0;
	
	final int 	gridNum_Width  = 20,				//Number of column.
	  		  	gridNum_Height = 16;				//Number of row.
	
	final float vWeight = 0.01f;
	int canvasWidth;				
	int canvasHeight;				
	
	boolean isPaintNeighbor;
	
	Grid[][] grids;											
	Neighborhood paintedNeighborhood;
	
	Random rand = new Random(System.currentTimeMillis());
	
	DecimalFormat df = new DecimalFormat("#.#");
	
	public GridManager(int canvasWidth, int canvasHeight){
		this.canvasWidth = canvasWidth;					
		this.canvasHeight = canvasHeight;				
		
		short stepUnit = 5;
		int red = 255, green = stepUnit, blue = stepUnit;
		
		for(int i=0; i< numColor; i++){
			heatMapColors[i] = new Color(red, green, blue, 100);	
			
			if(i < 50){
				green += stepUnit;
			}else if (i < 100){
				red -= stepUnit;
			}else if (i < 150){
				blue += stepUnit;
			}else if (i < 200){
				green -= stepUnit;
			}	 
		}
		
		
		/**************************************
		 * Grid Initialization 
		 **************************************/
		grids = new Grid[gridNum_Width][gridNum_Height];
	
		Point2D p1,  v;	
			
		for(int i=0; i<gridNum_Width; i++){
			for(int j=0; j<gridNum_Height; j++){
				p1 = new Point2D.Double(i*SIDE_LENGTH,j*SIDE_LENGTH);						//The left top corner of the grid. 
				v = new Point2D.Double(i*SIDE_LENGTH+rand.nextFloat()*SIDE_LENGTH,  		//The flow vector of the grid
									   j*SIDE_LENGTH+rand.nextFloat()*SIDE_LENGTH);
				int id  = j * gridNum_Width + i;											//The grid id
				grids[i][j] = new Grid(id, p1, v);
			}
		}
		
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
	
	public void setManagerMethod() {}
		
	
	/************************************************************
	 *	update all the flow grid. 
	************************************************************/
	public void update() {
		if(heatMapType != HEATMAP_NONE){
			for(int i = 0; i < gridNum_Width; i++) {
				for(int j = 0; j < gridNum_Height; j++) {
					if(heatMapType == HEATMAP_RATIONALITY){
						grids[i][j].computeAveRationality();
					}
					else if(heatMapType == HEATMAP_SUPERIORITY){
						grids[i][j].computeSuperiority();
					}
					else if(heatMapType == HEATMAP_ENTROPY){
						grids[i][j].computeEntropy();
					}
					else if(heatMapType == HEATMAP_DENSITY){
						grids[i][j].computeDensity();
					}
				}
			}
		}	
	}
	
	
	/************************************************************
	 *  add new entity into grids
	 ************************************************************/
	public void addObstacle(Obstacle ob) {
		int x = (int)Math.floor(ob.getPosition().x/SIDE_LENGTH);
		int y = (int)Math.floor(ob.getPosition().y/SIDE_LENGTH);
		
		if(x < 0 || y <0 || x >=gridNum_Width || y>=gridNum_Height)
			return;
		
		if(!grids[x][y].addObstacle(ob))
			System.out.println("add obstacle into grid failed!");
		ob.setCurrentGridID( y * gridNum_Width + x);
	}

	
	/************************************************************
	 *  remove agent from  a grid
	 ************************************************************/
	public void removeObstacle(Obstacle ob) {
		//use the current grid id to find out the original grid position.
		int oldGridId = ob.getCurrentGridID();							
		int oldy = oldGridId / gridNum_Width;
		int oldx = oldGridId % gridNum_Width;
		grids[oldx][oldy].removeObstacle(ob);
		cleanNeighborhoodContext(ob);
	}
	
	
	
	/************************************************************
	 *  add a new agent into a grid
	 ************************************************************/
	public void addAgent(Agent agent) {
		int x = (int)Math.floor(agent.getPosition().x/SIDE_LENGTH);
		int y = (int)Math.floor(agent.getPosition().y/SIDE_LENGTH);
		
		if(x < 0 || y <0 || x >=gridNum_Width || y>=gridNum_Height)
			return;
		
		if(!grids[x][y].addAgent(agent))
			System.out.println("add agent into grid failed!");
		agent.setCurrentGridID( y * gridNum_Width + x);
	}

	
	/************************************************************
	 *  remove a agent from  a grid
	 ************************************************************/
	public void removeAgent(Agent agent) {
		//use the current grid id to find out the original grid position.
		int oldGridId = agent.getCurrentGridID();							
		int oldy = oldGridId / gridNum_Width;
		int oldx = oldGridId % gridNum_Width;
		grids[oldx][oldy].removeAgent(agent);
	}
	
	
	public Point2D getFlowInGrid(Agent agent) {
		int oldGridId = agent.getCurrentGridID();							
		int oldy = oldGridId / gridNum_Width;
		int oldx = oldGridId % gridNum_Width;
		return grids[oldx][oldy].getFlowVector();
	}
	
	public List<Obstacle> getObstacleInGrid(double x, double y){
		double p_x = x / SIDE_LENGTH;
		double p_y = y / SIDE_LENGTH;
		
		int floorX = (int)Math.floor(p_x);
		int floorY = (int)Math.floor(p_y);
		return grids[floorX][floorY].getObstacleList();
	}
	
	public List<Agent> getAgentInGrid(double x, double y){
		double p_x = x / SIDE_LENGTH;
		double p_y = y / SIDE_LENGTH;
		
		int floorX = (int)Math.floor(p_x);
		int floorY = (int)Math.floor(p_y);
		return grids[floorX][floorY].getAgentList();
	}
	
	
	
	
	/************************************************************
	 *	Determine which grid would be occupied by a obstacle.	
	************************************************************/
	public void occupiedGrid(Obstacle ob){
		double x = ob.getPosition().x / SIDE_LENGTH;
		double y = ob.getPosition().y / SIDE_LENGTH;
		
		int floorX = (int)Math.floor(x);
		int floorY = (int)Math.floor(y);
		
		/***********************************
		 *			 margin check
		 ************************************/
		//cross east margin
		if(x < 0){
			floorX =  gridNum_Width -1;
		}
		//cross west margin
		else if(x > gridNum_Width-1){
			floorX = gridNum_Width -1;
		}
		//cross north margin
		if(y < 0){
			floorY = gridNum_Height -1;
		}
		//cross south margin
		else if(y > gridNum_Height-1){
			floorY = gridNum_Height -1;
		}
		
		/************************************************
		 *	Update the grid id of the obstacle  
		 ************************************************/
		int newGridId = floorY * gridNum_Width + floorX;				//the new grid where the obstacle will move.  
		int oldGridId = ob.getCurrentGridID();							//the old grid from which the obstacle moved out. 
		
		if(oldGridId != newGridId) {
			int oldy = oldGridId / gridNum_Width;
			int oldx = oldGridId % gridNum_Width;
			grids[oldx][oldy].removeObstacle(ob);
			grids[floorX][floorY].addObstacle(ob);
			
			ob.setCurrentGridID(newGridId);
		}
	}
	
	
	/************************************************************
	 *	Determine which grid would be occupied by a agent.	
	************************************************************/
	public void occupiedGrid(Agent agent){
		double x = agent.getPosition().x / SIDE_LENGTH;
		double y = agent.getPosition().y / SIDE_LENGTH;
		float velx = agent.getVelocity().x;
		float vely = agent.getVelocity().y;
		
		int floorX = (int)Math.floor(x);
		int floorY = (int)Math.floor(y);
		int ceilX = (int)Math.ceil(x);
		int ceilY = (int)Math.ceil(y);
		
		/***********************************
		 *			 margin check
		 ************************************/
		//cross east margin
		if(x < 0){
			floorX =  gridNum_Width -1;
			ceilX = 0;
		}
		//cross west margin
		else if(x > gridNum_Width-1){
			ceilX = 0;
			floorX = gridNum_Width -1;
		}
		//cross north margin
		if(y < 0){
			floorY = gridNum_Height -1;
			ceilY = 0;
		}
		//cross south margin
		else if(y > gridNum_Height-1){
			ceilY = 0;
			floorY = gridNum_Height -1;
		}
		
		/************************************************
		 *	Update the grid id of the agent  
		 ************************************************/
		int newGridId = floorY * gridNum_Width + floorX;				//the new grid where the agent will move.   
		int oldGridId = agent.getCurrentGridID();						//the old grid from which the agent moved out. 
		
		if(oldGridId != newGridId) {
			int oldy = oldGridId / gridNum_Width;
			int oldx = oldGridId % gridNum_Width;
			grids[oldx][oldy].removeAgent(agent);
			grids[floorX][floorY].addAgent(agent);
			
			agent.setCurrentGridID(newGridId);
		}
		
		
		/************************************************
		 *	Compute and update the flow vector on the grid.
		 ************************************************/
/*		Grid grid = grids[floorX][floorY];
	
		double originX = grid.flowVector.getX()-floorX * side;
		double originY = grid.flowVector.getY()-floorY * side;
		grids[floorX][floorY].flowVector.setLocation(floorX*side + (1-vWeight) * originX + vWeight * velx,
															  			floorY*side + (1-vWeight) * originY + vWeight * vely);
		
		
		grid = grids[floorX][ceilY];
		originX = grid.flowVector.getX()-floorX * side;
		originY = grid.flowVector.getY()-ceilY * side;
		grids[floorX][ceilY].flowVector.setLocation(floorX*side + (1-vWeight) * originX + vWeight * velx,
															  		  ceilY*side + (1-vWeight) * originY + vWeight * vely);
		
		
		grid = grids[ceilX][floorY];
		originX = grid.flowVector.getX()-ceilX * side;
		originY = grid.flowVector.getY()- floorY * side;
		grids[ceilX][floorY].flowVector.setLocation(ceilX*side + (1-vWeight) * originX + vWeight * velx,
																	   floorY*side +(1-vWeight) * originY + vWeight * vely);
		
		grid = grids[ceilX][ceilY];
		originX = grid.flowVector.getX()-ceilX * side;
		originY = grid.flowVector.getY()- ceilY * side;
		grids[ceilX][ceilY].flowVector.setLocation(ceilX*side + (1-vWeight) * originX + vWeight * velx,
														  			 ceilY*side + (1-vWeight) * originY + vWeight * vely);	
*/
	}
	
	
	
	/***********************************************************
	 *	Compute the neighborhood of a obstacle.
	 ***********************************************************/
	private Neighborhood computeNeighborhood(Obstacle ob) {
		
		/*************************************************
		 *	To figure out the range of the neighborhood. 
		 ************************************************/
		Rectangle2D bbox;
		if(ob.isInteracitve()){
			bbox = ((InteractiveObstacle)ob).getPollutionCircleBBox();
		}else{
			bbox = ob.getRegion().getBounds2D();
		}
		
		int max_X = (int)Math.ceil(bbox.getMaxX());
		int max_Y = (int)Math.ceil(bbox.getMaxY());
		int min_X = (int)Math.floor(bbox.getMinX());
		int min_Y = (int)Math.floor(bbox.getMinY());

		/*********************************** 
		 *	Margin Calculation 
		 ************************************/
		if(min_X > canvasWidth) {
			 return null;
		} else 	if(min_X < 0){
			min_X = 0;
		}
		
		if(min_Y > canvasHeight) {
			return null;
		}else if (min_Y < 0) {
			min_Y = 0;
		}
		
		if(max_X < 0) {
			 return null; 
		}else if (max_X >= canvasWidth) {
			max_X = canvasWidth-1;
		}
		
		if(max_Y < 0) {
			 return null; 
		}else if (max_Y >= canvasHeight) {
			max_Y = canvasHeight-1;
		}		
		
		Neighborhood neighborhood = new Neighborhood();
		neighborhood.LT_X  = (int)Math.floor(min_X /= SIDE_LENGTH);
		neighborhood.LT_Y  = (int)Math.floor(min_Y /= SIDE_LENGTH);
		neighborhood.BR_X = (int)Math.floor(max_X /= SIDE_LENGTH);
		neighborhood.BR_Y = (int)Math.floor(max_Y /= SIDE_LENGTH);

		return neighborhood;
	}
	
	public void cleanNeighborhoodContext(Obstacle ob){
		if(ob.isInteracitve()){
			Neighborhood neighborhood = ob.getNeighborhoodMark();
		
			float pollutionValue = ((InteractiveObstacle)ob).getPollutionValue();
			
			if(neighborhood != null){
				for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
					for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
						grids[i][j].subtractContextValue(pollutionValue);
					}
				}	
			}
		}	
	}
	
	
	public void addNeighborhoodContext(Obstacle ob){
		if(ob.isInteracitve()){
			Neighborhood neighborhood;
			
			
			if((neighborhood = computeNeighborhood(ob)) ==null) {
				ob.setNeighborHoodMark(null);
				return;
			}
			
			ob.setNeighborHoodMark(neighborhood);
		
			
			float pollutionValue = ((InteractiveObstacle)ob).getPollutionValue();
		
			for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
				for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
					grids[i][j].addContextValue(pollutionValue);
				}
			}	
						
		}	
	}
	
	
	public void updateNeighborContext(Obstacle ob){		
		
		Neighborhood neighborhood;
		
		/*******************************************
		 * Interactive Obstacle
		 *******************************************/
		if(ob.isInteracitve() && !((InteractiveObstacle)ob).isHitable()){
			cleanNeighborhoodContext(ob);
			addNeighborhoodContext(ob);
		}
		/*******************************************
		 * Non-Interactive Obstacle
		 *******************************************/
		else{
			if((neighborhood = computeNeighborhood(ob)) ==null) {
				ob.setNeighborHoodMark(null);
				return;
			}
			
			ob.setNeighborHoodMark(neighborhood);	
		}
			
	}
	
	
	
	/***********************************************************
	 *	To compute the neighborhood of an agent's view field.
	 ***********************************************************/
	public Neighborhood computeNeighborhood(Agent agent) {
		
		/**************************************************
		 *	To collect all of the agents in neighborhood.
		 **************************************************/
	    //Rectangle2D bbox = agent.getViewFieldBBox();
		Rectangle2D bbox = agent.getDensityCircleBBox();
		
		int max_X = (int)Math.ceil(bbox.getMaxX());
		int max_Y = (int)Math.ceil(bbox.getMaxY());
		int min_X = (int)Math.floor(bbox.getMinX());
		int min_Y = (int)Math.floor(bbox.getMinY());

		/******************************************************************** 
		 *	Margin Calculation. 
		 *	Agents who out of the canvas aren't counted in the neighborhood.
		 ********************************************************************/
		if(min_X > canvasWidth) {
			 return null;
		} else 	if(min_X < 0){
			min_X = 0;
		}
		
		if(min_Y > canvasHeight) {
			return null;
		}else if (min_Y < 0) {
			min_Y = 0;
		}
		
		if(max_X < 0) {
			 return null; 
		}else if (max_X >= canvasWidth) {
			max_X = canvasWidth-1;
		}
		
		if(max_Y < 0) {
			 return null; 
		}else if (max_Y >= canvasHeight) {
			max_Y = canvasHeight-1;
		}		
		
		Neighborhood neighborhood = new Neighborhood();
		neighborhood.LT_X  = (int)Math.floor(min_X /= SIDE_LENGTH);
		neighborhood.LT_Y  = (int)Math.floor(min_Y /= SIDE_LENGTH);
		neighborhood.BR_X = (int)Math.floor(max_X /= SIDE_LENGTH);
		neighborhood.BR_Y = (int)Math.floor(max_Y /= SIDE_LENGTH);

		return neighborhood;
	}
	
	/*********************************************************************************
	 *  The police will choose the next best position based on the policing strategy.
	 *********************************************************************************/
	public Vector2f queryBestPosition(Agent agent){
		
		if(heatMapType == HEATMAP_NONE)
			return null;
		
		
		Neighborhood neighborhood = new Neighborhood();
		if((neighborhood = computeNeighborhood(agent)) == null) {
			agent.setNeighborHoodMark(null);
			return null;
		}
		
		agent.setNeighborHoodMark(neighborhood);
		
		float highestValue = -1f;
		float lowestValue = 1000f;
		
		float value;
		
		Grid bestGrid = null;
				
		if(heatMapType == HEATMAP_RATIONALITY){
			for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
				for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
					value = grids[i][j].getAveRationality();
					
					if(value != 0){
						if(lowestValue > value){
							 lowestValue = value;
							 bestGrid = grids[i][j];
						}
						else if(lowestValue == value && value != 0){
							int gid = agent.getCurrentGridID();
	
							//find nearest grid
							if(Math.abs(grids[i][j].getId() - gid) < Math.abs(bestGrid.getId() - gid)){
								bestGrid = grids[i][j];	
							}
						}
					}
				}
			}	
		}
		
		else if(heatMapType == HEATMAP_SUPERIORITY){	
			for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
				for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
					value = grids[i][j].getAveSuperiority();
					
					if(value != 0){
						if(highestValue < value){
							 highestValue = value;
							 bestGrid = grids[i][j];
						}
						else if(highestValue == value && value != 0){
							int gid = agent.getCurrentGridID();
	
							//find nearest grid
							if(Math.abs(grids[i][j].getId() - gid) < Math.abs(bestGrid.getId() - gid)){
								bestGrid = grids[i][j];	
							}
						}
					}	
				}
			}	
		}
		
		else if(heatMapType == HEATMAP_ENTROPY){
			for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
				for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
					value = grids[i][j].getEntropy();
					
					if(value != 0){
						if(highestValue < value){
							 highestValue = value;
							 bestGrid = grids[i][j];
						}
						else if(highestValue == value && value != 0){
							int gid = agent.getCurrentGridID();
	
							//find nearest grid
							if(Math.abs(grids[i][j].getId() - gid) < Math.abs(bestGrid.getId() - gid)){
								bestGrid = grids[i][j];	
							}
						}
					}
				}
			}	
		}
		
		else if(heatMapType == HEATMAP_DENSITY){
			for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
				for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
					value = grids[i][j].getDensity();
					
					if(value != 0){
						if(highestValue < value){
							 highestValue = value;
							 bestGrid = grids[i][j];
						}
						else if(highestValue == value && value != 0){
							int gid = agent.getCurrentGridID();
	
							//find nearest grid
							if(Math.abs(grids[i][j].getId() - gid) < Math.abs(bestGrid.getId() - gid)){
								bestGrid = grids[i][j];	
							}
						}
					}
				}
			}	
		}
		
		
				
		if(bestGrid == null){
			return null;
		}else{
			return bestGrid.getCenterPosition();	
		}	
	}
	
	
	
	/****************************************************************
	 *	To find out the neighbors within the neighborhood of a agent.
	 ****************************************************************/
	public ArrayList<Agent> getNeighbor(Agent agent){		
		
		Neighborhood neighborhood = new Neighborhood();
		if((neighborhood = computeNeighborhood(agent)) ==null) {
			agent.setNeighborHoodMark(null);
			return null;
		}
		
		agent.setNeighborHoodMark(neighborhood);
		
		ArrayList<Agent> neighbors = new ArrayList<Agent>();

		for(int i = neighborhood.LT_X; i <= neighborhood.BR_X; i++) {
			for(int j = neighborhood.LT_Y; j <= neighborhood.BR_Y; j++) {
				try {
					neighbors.addAll(grids[i][j].getAgentList());
				}catch(NullPointerException ex) {
					System.out.println("I got you!!");
					System.exit(1);
				}	
			}
		}	
		
		return neighbors;
	}
	
	public void reset() {
		for(int i = 0; i < gridNum_Width; i++) {
			for(int j = 0; j < gridNum_Height; j++) {
				grids[i][j].clearAgentList();
				grids[i][j].clearObstacleList();
			}
		}	
	}
	
	
	/************************************************
	 *	To paint all grids.
	 ************************************************/
	public void paintAllGrids(Graphics2D g) {
		Line2D line = new Line2D.Double();
		
		if(isDrawGrid){
			/*************************
			 * Draw horizontal line
			 *************************/
			for(int i=1; i<gridNum_Height; i++){
				line.setLine(0, i*SIDE_LENGTH, 800, i*SIDE_LENGTH);
				g.draw(line);
			}
			
			/*************************
			 * Draw vertical line
			 *************************/
			for(int i=1; i <gridNum_Width; i++){
				line.setLine(i*SIDE_LENGTH, 0, i*SIDE_LENGTH, 640 );
				g.draw(line);
			}
		}
		
		
		/*************************************
		 * Paint context value or heat map
		 *************************************/
		if(isDrawContextValue || heatMapType != HEATMAP_NONE){
			for(int i=0; i<gridNum_Width; i++){	
				for(int j=0; j<gridNum_Height; j++){
					grids[i][j].paintGrid(g);		
				}
			}
		}
		
		/*************************************
		 * Draw heat-map color bar
		 *************************************/
		if(heatMapType != HEATMAP_NONE){
		
			int x = 760,
				y = 427,
				width = 15,
				height = 200;
			
			g.setColor(Color.WHITE);
			g.fill3DRect(x, y, width, height, true);
			
		    Font font = new Font("Arial", Font.BOLD, 10);
		    g.setFont(font);
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
		    int spacing;
			float tick;
		    
			if(heatMapType == HEATMAP_RATIONALITY){
				spacing = 25; 	// 200 was divided by 8
				tick = 0f;
			}
			else if (heatMapType == HEATMAP_SUPERIORITY){
				spacing = 22;	// 200 was divided by 9
				tick = 1f;	
			}
			//ENTROPY
			else{
				spacing = 22;	// 200 was divided by 9
				tick = 1f;
			}

			for(int i=0; i<numColor; i++){
				
				line.setLine(x, y+i, x + width, y+i);
				g.setColor(heatMapColors[i]);
				g.draw(line);
					
				if(heatMapType == HEATMAP_RATIONALITY){
					if(i % spacing == 0 || i == numColor -1){
						tick += 0.1;
						g.setPaint(Color.BLACK);
						g.drawString(df.format(tick), x + 20 , y + 5 + i);
					}
				}
				else if (heatMapType == HEATMAP_SUPERIORITY){
					if(i % spacing == 0){
						g.setPaint(Color.BLACK);
						g.drawString(df.format(tick), x + 20 , y + 5 + i);
						tick -= 0.1;
					}	
				}
				else if (heatMapType == HEATMAP_ENTROPY){
					if(i % spacing == 0){
						g.setPaint(Color.BLACK);
						g.drawString(df.format(tick), x + 20 , y + 5 + i);
						tick -= 0.1;
					}
				}
				else if (heatMapType == HEATMAP_DENSITY){
					if(i % spacing == 0){
						g.setPaint(Color.BLACK);
						g.drawString(df.format(tick), x + 20 , y + 5 + i);
						tick -= 0.1;
					}
				}
			}
		}
		
		
		/****************************
		 * Draw the flow vector
		 ****************************/
//		g.setPaint(Color.blue);

//		for(int i=0; i<gridNum_Width; i++){	
//			for(int j=0; j<gridNum_Height; j++){
//				grids[i][j].paintGrid(g);		
//			}
//		}
		
	
		//for testing
//		System.out.println("This is(3,5):");		
//		grids[3][5].printAgentId();
//		System.out.println("This is(1,0):");
//		grids[1][0].printAgentId();
		
	}
	
}
