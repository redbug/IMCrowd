package imcrowd.ui;


import imcrowd.basicObject.MyObject;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.specialAgent.Agitator;
import imcrowd.basicObject.agent.specialAgent.Police;
import imcrowd.basicObject.goal.Goal;
import imcrowd.basicObject.obstacle.Monster;
import imcrowd.basicObject.obstacle.NormalObstacle;
import imcrowd.basicObject.obstacle.StreetPerformance;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.basicObject.obstacle.InteractiveObstacle;
import imcrowd.engine.Controller;
import imcrowd.engine.Engine;
import imcrowd.manager.AgentManager;
import imcrowd.manager.GoalManager;
import imcrowd.manager.GridManager;
import imcrowd.manager.GroupManager;
import imcrowd.manager.ObstacleManager;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.vecmath.Vector2f;



public class MyCanvas extends JPanel implements Colleague{
	public static boolean isShowText = true;
	
	private Mediator mediator;
	
	private Engine engine;	
	private	AgentManager agentManager;
	private	ObstacleManager obManager;
	private	GoalManager goalManager;
	private	GridManager gridManager;
	private ExperimentParameterTab experimentParmTab;
	
//private static MyCanvas canvas = new MyCanvas();
	boolean isDrag;				
	boolean isReplay; 				
	boolean isRender;
	
	int canvasWidth;				
	int canvasHeight;				
	long frameCounter;				
	
	Point clickPoint;
	Point dragPoint;
	
	MyObject target;
	MyObject lastTarget;				
	
	Agent selectedAgent;
	
	Controller ct;
	GroupManager groupManager;
	
	Font font = new Font("Arial", Font.ITALIC, 12);
	

	public MyCanvas(int width, int height){
		isReplay = false;
		isRender = true;
		ct = Controller.getInstance();
		experimentParmTab = ExperimentParameterTab.getInstance();
		canvasWidth = width;
		canvasHeight = height;
		setPreferredSize(new Dimension(canvasWidth, canvasHeight));  
	}
/*	
	public static MyCanvas getInstance(){
		return canvas;
	}
*/	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		
		agentManager = engine.getAgentManager();
		obManager = engine.getObstacleManager();
		goalManager = engine.getGoalManager();
		gridManager = engine.getGridManager();
		groupManager = engine.getGroupManager();
//		graph = engine.getGraph();
		initial();
	}
	public void setManagerMethod() {}
		
	public boolean isRender(){
		return isRender;
	}
	
	public void setRender(boolean b){
		isRender = b;
	}
	
	
	public void setSelectedAgent(Agent ag){
		selectedAgent = ag;
	}
	
	public void setReplay(boolean op){
		isReplay = op;
	}
	
	public int getWidth(){
		return canvasWidth;
	}

	public int getHeight(){
		return canvasHeight;
	}
	
	public void setFrameCounter(long fc) {
		this.frameCounter = fc;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;      
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  
	/*	
		if(agentManager.getAgentListDirty()) {
			ArrayList<Agent> agentList = new ArrayList<Agent>();  
			agentList.addAll(agentManager.getAgentList());
			agentManager.setAgentListDirty(false);
		}
	*/	
		
		if(isRender){
			gridManager.paintAllGrids(g2);
			agentManager.paintAllAgents(g2);
			obManager.paintAllObstacles(g2);
			
			if(dragPoint != null && groupManager.isDrawTemplate()){
				groupManager.paintGroupTemplate(dragPoint, ct.getDiameterTFieldValue(), g2);
			}
			
			goalManager.paintAllGoals(g2);
			
			
			/************************
			 * for drawing infoBoard
			 ************************/
			if(selectedAgent != null){
				selectedAgent.paint(g2);
			}
		}
		
		g2.setFont(font);
		g2.setColor(Color.blue);
		g2.drawString(String.valueOf(frameCounter), 10, canvasHeight - 10);
		
		
		
		if(isShowText){
			g2.setColor(Color.BLACK);
			
			int x_Leaver = canvasWidth - 150;
			int y_Leaver = 20;
			
			int x_Victim = x_Leaver;
			int y_Victim = y_Leaver + 30;
			
			int xMargin = 60;
			int xRange = 30;
			
			g2.drawString("Victims:", x_Leaver, y_Leaver);
			g2.drawString("Leavers:", x_Victim, y_Victim);
			
			int v[] = AgentManager.partyInfo[AgentManager.VICTIM_INFO];
			int l[] = AgentManager.partyInfo[AgentManager.LEAVER_INFO];
			
			int cnt = 0;
			boolean flag;
			
			for(int i=0; i< v.length; i++){
				flag = false;
				
				if(v[i] != 0){
					flag = true;
					g2.setColor(AgentTab.COLOR_ARRAY[i]);
					g2.drawString(String.valueOf(v[i]), x_Leaver + xMargin + cnt * xRange, y_Leaver);
				}	
				
				if(l[i] != 0){
					flag = true;
					g2.setColor(AgentTab.COLOR_ARRAY[i]);
					g2.drawString(String.valueOf(l[i]), x_Victim + xMargin + cnt * xRange, y_Victim);
				}	
				
				if(flag == true) cnt++;
			}
		
		}
		
//		float frameRate = frameCounter
//graph.drawEdges(g2);
	}

	public void initial(){		
		
		addMouseListener(new MouseAdapter(){	
			public void mousePressed(MouseEvent e){
				requestFocus();										
				
				target = agentManager.isPointInAgent(clickPoint = e.getPoint());
				
				if(target == null){
					target = obManager.isPointInObstacle(clickPoint);
				}
				
				if(target == null){
					target = goalManager.isPointInGoal(clickPoint);
				}
				
				if(target != null){
					groupManager.setDrawTemplate(false);
				}
				
				DockingWindows dockingWindows = DockingWindows.getInstance();
				
				//Right click
				if(e.getModifiers() == MouseEvent.META_MASK){
					if(lastTarget != null){
						lastTarget.setSelect(false);
					}
					ct.setTarget(target);
					
					if(target != null){
						target.setSelect(true);		
								
						//Right click on an Agent
						if (target instanceof Agent){
							selectedAgent = (Agent)target;
							dockingWindows.getTabWindow1().setSelectedTab(0);		
							requestFocus();											
							
							/* update the information of the agent on the controller panel */
							ct.setAgentColorCBox(selectedAgent.getColorId());
							ct.getMaxForceSlider().setValue((int)selectedAgent.getMaxForce());		
							ct.getMaxNormalSpeedSlider().setValue((int)selectedAgent.getNormalSpeed());
							ct.getActiveFSlider().setValue((int)(selectedAgent.getActiveF()));
							ct.getVRadiusSlider().setValue((int)selectedAgent.getVRadius());
							ct.getVThetaSlider().setValue((int)selectedAgent.getVTheta());
							
							ct.setSeparation(selectedAgent.getSeparationWeight());
							ct.setCohesion(selectedAgent.getCohesionWeight());
							ct.setAlignment(selectedAgent.getAlignmentWeight());
							
							ct.setSeparationI(selectedAgent.getSeparationI());
							ct.setCohesionI(selectedAgent.getCohesionI());
							ct.setAlignmentI(selectedAgent.getAlignmentI());
																			
							ct.getBA_radio()[selectedAgent.getBA()].setSelected(true);
							ct.setMassTFieldValue(String.valueOf((int)selectedAgent.getMass()));
							ct.setDiameterFieldValue(String.valueOf((int)selectedAgent.getLength()));
							
							ct.disableAlignmentISlider();
							ct.disableCohesionISlider();
							ct.disableSeparationISlider();

							ct.getMassTField().setEditable(false);
							ct.getDiameterField().setEditable(false);
//System.out.println("currentGridId:"+targetAgent.getCurrentGridID());
						}
						//Right click on a Obstacle
						else if(target instanceof Obstacle){
							selectedAgent = null;
							Obstacle targetObstacle = (Obstacle)target;							
							dockingWindows.getTabWindow1().setSelectedTab(2);		
							requestFocus();											
						}
						//Right click on a Goal.
						else if(target instanceof Goal){
							selectedAgent = null;
							dockingWindows.getTabWindow1().setSelectedTab(1);		
							requestFocus();											
						}
				    }
					//Right click on the canvas
					else{
						selectedAgent = null;
						ct.getMassTField().setEditable(true);
						ct.getDiameterField().setEditable(true);
					}
					lastTarget = target;
				}
				//Left click
				else{
					//Left click on the canvas
					if(target == null){

						String currentTab = dockingWindows.getTabWindow1().getSelectedWindow().toString();
						Vector2f position  = new Vector2f((float)clickPoint.getX(), (float)clickPoint.getY());
					
						int state;
						if((state = experimentParmTab.getSelectedEvent()) != ExperimentParameterTab.NONE){
							
							Obstacle ob;
							Agent ag;
							
							switch(state){
								case ExperimentParameterTab.RIOT:
									ag = new Agitator(clickPoint.x, clickPoint.y, 270, 0);
									agentManager.addAgent(ag);
									break;
									
								case ExperimentParameterTab.GATHER:
									ob  = new StreetPerformance(new Vector2f(clickPoint.x, clickPoint.y));
									obManager.addObstacle(ob);
									break;
									
								case ExperimentParameterTab.PANIC:
									ob  = new Monster(new Vector2f(clickPoint.x, clickPoint.y));
									obManager.addObstacle(ob);
									break;
								case ExperimentParameterTab.POLICE:
									ag = new Police(clickPoint.x, clickPoint.y, 270, 0);
									agentManager.addAgent(ag);
									break;
								
							}
							experimentParmTab.resetButtons();
							
						}	
						else{
							if(currentTab.equals("Agent")){
								groupManager.createGroup(clickPoint, ct.getDiameterTFieldValue());
							}
							else if(currentTab.equals("Goal")){
					 			goalManager.addGoal(new Goal(position, ct.getCurrentGoalImg()));
							}
							else if(currentTab.equals("Obstacle")){
								Obstacle ob;
								if(ct.isInteractiveOB()){
									ob = new InteractiveObstacle(position, ct.getCurrentOBImg(), ct.getInteractiveId());	
								}
								else{
									ob = new NormalObstacle(position, ct.getCurrentOBImg());
								}
						
								obManager.addObstacle(ob);
							}

						}
					}
					else{
						isDrag = true;    
					}
				}
			}
			
			public void mouseReleased(MouseEvent e){
				isDrag= false;          
			}
			
			public void mouseExited(MouseEvent e){
//				dragPoint.setLocation(-50, -50);
				groupManager.setDrawTemplate(false);
			}
			
		});
		
		addMouseMotionListener(new MouseMotionAdapter(){
			double dx,dy;
			public void mouseDragged(MouseEvent e){
				if(isDrag == false) return;	
				Vector2f targetPosition = target.getPosition();
				
				dragPoint = e.getPoint();
				//for middle button of the mouse
				if(e.getModifiers() == MouseEvent.META_MASK){
					
				}
				//for left button of the mouse
				else{
					target.setPosition((float)dragPoint.getX(), (float)dragPoint.getY());
				}
			}	
			
			public void mouseMoved(MouseEvent e){
				DockingWindows dockingWindows = DockingWindows.getInstance();
				String currentTab = dockingWindows.getTabWindow1().getSelectedWindow().toString();
				
				if(currentTab.equals("Agent")){
					if(experimentParmTab.getSelectedEvent() == ExperimentParameterTab.NONE){
						groupManager.setDrawTemplate(true);
					}	
					dragPoint = e.getPoint();
				}else{
					groupManager.setDrawTemplate(false);
				}
			} 
			
		});
		
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){

				if(e.getKeyCode() == KeyEvent.VK_DELETE){
					if(target != null){
						if(target instanceof Goal){
							engine.getGoalManager().removeOne((Goal)target);
						}
						else if (target instanceof Agent){
							engine.getAgentManager().removeOne((Agent)target);
							selectedAgent = null;
							//((Agent)target).setIsAlive(false);
						}
						else if (target instanceof Obstacle){
							engine.getObstacleManager().removeOne((Obstacle)target);
						}
					}
					else if (lastTarget != null){
						if(lastTarget instanceof Goal){
							engine.getGoalManager().removeOne((Goal)lastTarget);
						}
						else if (lastTarget instanceof Agent){
							engine.getAgentManager().removeOne((Agent)lastTarget);
							//((Agent)target).setIsAlive(false);
						}
						else if (lastTarget instanceof Obstacle){
							engine.getObstacleManager().removeOne((Obstacle)lastTarget);
						}
					}
				}
				// ctrl + a
				else if(e.getKeyCode() == KeyEvent.VK_A){
				    if (e.isControlDown()){
						agentManager.setVisualMode(AgentManager.INFO_MODE);
			        }
				}
				// ctrl + g
				else if(e.getKeyCode() == KeyEvent.VK_G){
				    if (e.isControlDown()){
						agentManager.setVisualMode(AgentManager.GROUP_MODE);
			        }
				}
				
				else if(e.getKeyCode() == KeyEvent.VK_S){
					if (e.isControlDown()){
						agentManager.setVisualMode(AgentManager.STATE_MODE);
			        }
				}
				
				if(target != null && target instanceof Agent){	
					Agent ag = (Agent)target;

					if(e.getKeyCode() == KeyEvent.VK_RIGHT){			//right arrow
						ag.turnRight(3);
					}
					else if(e.getKeyCode() == KeyEvent.VK_LEFT){		//left arrow
						ag.turnLeft(3);
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN){		//up arrow
						//TODO
						//ag.modulateThrust(false,1);
					}
					else if(e.getKeyCode() == KeyEvent.VK_UP){			//bottom arrow
						//TODO
						//ag.modulateThrust(true,0);
					}
				}
			}
			public void keyReleased(KeyEvent e){
				
				if(e.getKeyCode() == KeyEvent.VK_A ||
				   e.getKeyCode() == KeyEvent.VK_G ||
				   e.getKeyCode() == KeyEvent.VK_S 
				){
					agentManager.setVisualMode(AgentManager.NORMAL_MODE);
				}  	
			}
		});
		
		setFocusable(true);
	}

}
