package imcrowd.engine;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.riot.Flight;
import imcrowd.graph.Graph;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.io.textIO.ExportFrameData;
import imcrowd.io.textIO.StatisticIOHandler;
import imcrowd.manager.AgentManager;
import imcrowd.manager.GoalManager;
import imcrowd.manager.GridManager;
import imcrowd.manager.GroupManager;
import imcrowd.manager.ObstacleManager;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;
import imcrowd.ui.AgentTab;
import imcrowd.ui.GlobalTab;
import imcrowd.ui.MainView;
import imcrowd.ui.MyCanvas;
import imcrowd.ui.StatisticInfoTab;

import java.awt.geom.Point2D;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Engine implements Mediator{	
	private static Engine engine = new Engine();
	
	public static final int SEED = 0730;
	
	public static final int canvasWidth  = 800,				
							canvasHeight = 640;				

	//public static Random rand = new Random(System.currentTimeMillis());
	public static Random rand = new Random(SEED);
	
	private final int perFrame = 20;
	
	
	private boolean isPlay = false;						
	private boolean isExport = false;				
	private boolean isRecord = false;			
	private boolean isLoginIMNET = false;
	
	
	private int sleepTime;								
	private int runStep = -1;
	private long frameCounter;	
	
	private AgentManager agentManager;
	private ObstacleManager obstacleManager;
	private GoalManager goalManager;
	private GridManager gridManager;
	
	private MyCanvas canvas;
	private MainView mainView;
	
	private ExportFrameData exporter;	
	private StatisticIOHandler recorder;
	
	private GlobalTab globaltab;
	private AgentTab agenttab;
	private StatisticInfoTab statisticTab;
	private Graph graph;
	private GroupManager groupManager;
	
	private ImageLoader imageLoader;
	
//	private IMNETutil imnetUtil;
	
	private FrameQueue frameQueue;
	
	private Engine(){
		frameCounter = 0;
		createColleagues();
	}
	
	public static Engine getInstance(){
		return engine;
	}
	
	public void createColleagues(){		
		agentManager = new AgentManager();
		obstacleManager = new ObstacleManager();
		goalManager = new GoalManager(); 
		groupManager = new GroupManager();
		gridManager = new GridManager(canvasWidth, canvasHeight);
		imageLoader = ImageLoader.getInstance();
		globaltab = GlobalTab.getInstance();
		agenttab = AgentTab.getInstance();
		statisticTab = StatisticInfoTab.getInstance();
		
		Controller.getInstance();

		canvas = new MyCanvas(canvasWidth, canvasHeight);
		mainView =MainView.getInstance();
		exporter = new ExportFrameData();
		recorder = StatisticIOHandler.getInstance();
		
		agentManager.setMediator(this);
		groupManager.setMediator(this);
		obstacleManager.setMediator(this);		
		//goalManager.setMediator(this);		
		//gridManager.setMediator(this);
		canvas.setMediator(this);
		mainView.setMediator(this);
		exporter.setMediator(this);
		recorder.setMediator(this);
		globaltab.setMediator(this);
		statisticTab.setMediator(this);
		agenttab.setMediator(this);
		
		sleepTime = 50/mainView.getSpeedSlider().getValue();	
	
	}
	
	public void colleaguesChanged(Colleague colleague) {}
		
	static public float RandomClamped() {
		return rand.nextFloat() - rand.nextFloat();
	}
	
	/* width & height */
	public int getWidth(){
		return canvasWidth;
	}

	public int getHeight(){
		return canvasHeight;
	}
	
	
	/* sleepTime */
	public int getSleepTime(){
		return sleepTime;
	}
	
	public void setSleepTime(int s){
		sleepTime = s;
	}
	
	/* isRecord */
	public boolean isRecord() {
		return isRecord;
	}
	
	public void setRecord(boolean isRecord) {
		recorder.setTotalFrame(runStep);
		this.isRecord = isRecord;
		
		if(isRecord){
			setPlay(true);
		}	
	}
	
	
	/* isExport */
	public boolean isExport(){
		return isExport;
	}
	
	public void setExport(boolean isExport){
		this.isExport = isExport;
	}
	
	/* isPlay */
	public boolean isPlay() {
		return isPlay;
	}

	public void setRunStep(int step){
		runStep = step;
	}
	
	public void setPlay(boolean isPlay) {		 
		this.isPlay = isPlay;
	}
	
	public GroupManager getGroupManager(){
		return groupManager;
	}
	
	public StatisticIOHandler getRecordIOHandler(){
		return recorder;
	}

	public AgentManager getAgentManager(){
		return agentManager;
	}
	
	public GoalManager getGoalManager(){
		return goalManager;
	}
	
	public ObstacleManager getObstacleManager(){
		return obstacleManager;
	}
/*	
	public FlowGrid getFlowGrid(){
		return flowGrid;
	}
*/	
	public GridManager getGridManager() {
		return gridManager;
	}
	
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	
	
	public Graph getGraph() {
		return graph;
	}
	
	public MyCanvas getCanvas() {
		return canvas;
	}
	
	public MainView getMainView() {
		return mainView;
	}
	
	
/* for IMNET Bundle	
public IMNETutil getIMNETutil(){
	return imnetUtil;
}

public void loginIMNET(){
	Agent ag;
	String id;
    if(!isLoginIMNET){
		imnetUtil = new IMNETutil(); 
		frameQueue = new FrameQueue();
		
    	for (Iterator<Agent> it = agentManager.getAgentList().iterator(); it.hasNext();){
    		ag = ((Agent) it.next());
		    id = imnetUtil.login();
		    try{
		    	Thread.sleep(1000);
		    }catch(InterruptedException e){
		    	e.printStackTrace();
		    }
		    imnetUtil.teleportTo(id, ag.getPosition().x, ag.getPosition().y);
		}
    	
       	isLoginIMNET = true;
       	IMNETbridge imnetBridge = new IMNETbridge(frameQueue);
       	imnetBridge.setMediator(this);
       	Thread thread = new Thread(imnetBridge);
       	thread.start();
//       	mainView.getPlayButton().doClick();
    }
}
*/
	public void pause(){
		setRecord(false);
		mainView.pause();
	}
	
	public void stop() {
		setPlay(false);
		rand.setSeed(SEED);
		Flight.counter = 0;
		frameCounter = 0;
		agentManager.reset();
		groupManager.reset();
		obstacleManager.reset();
		goalManager.reset();
		gridManager.reset();
		canvas.setSelectedAgent(null);
	}
	
	
	public void update(){
		if (isPlay) {
			if(runStep >= 0) {
				if (runStep == 0) {	
					pause();
					return;
				} 	
				runStep--;
			}
			
			canvas.setFrameCounter(++frameCounter);

			agentManager.updateAgent();
			gridManager.update();
			
			if(isExport){
				try {
					exporter.writeOut();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(isRecord){
				try {
					recorder.record();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(isLoginIMNET){
				Agent ag;
				List<Agent> agList = agentManager.getAgentList();
				Map<Integer, Point2D> frame = new HashMap<Integer,Point2D>();
				boolean flag = false;
				
				if(frameCounter%perFrame == 0){
			    	for (Iterator<Agent> it = agList.iterator(); it.hasNext();){
			    		ag = ((Agent) it.next());
			    		Point2D pos = new Point2D.Float(ag.getPosition().x, ag.getPosition().y);
			    		frame.put(ag.getId(), pos);
			    		
			//    		ag = ((Agent) it.next());
			//		    try{
			//		    	Thread.sleep(500);
			//		    }catch(InterruptedException e){
			//		    	e.printStackTrace();
			//		    }
			//		    imnetBundle.walkTo("user1_" + ag.getId(), ag.getPosition().x, ag.getPosition().y);
					}
			    	frameQueue.enqueue(frame);
			    	if(frameQueue.isFull()){
			    		flag = true;
			    		pause();
						System.out.println("FrameBuffer is full!!");
			    		while(!frameQueue.isLessThanHelf())   		
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
						}
			    	}
			    	if(flag == true){
			    		flag = false;
						//isPlay = true;
						mainView.play();
						//mainView.getPauseButton().setSelected(false);
			    	}	
				}	
			}			
		}
				
		if(!isPlay && !canvas.isRender()){
		}
		else{
			try {
				Thread.sleep(getSleepTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			canvas.repaint();
		}	
	}
}
