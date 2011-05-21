package imcrowd.io.textIO;

import imcrowd.basicObject.agent.*;
import imcrowd.basicObject.agent.normalAgent.Follower;
import imcrowd.basicObject.agent.normalAgent.Leader;
import imcrowd.basicObject.agent.specialAgent.Agitator;
import imcrowd.basicObject.agent.specialAgent.Police;
import imcrowd.basicObject.goal.Goal;
import imcrowd.basicObject.obstacle.InteractiveObstacle;
import imcrowd.basicObject.obstacle.Monster;
import imcrowd.basicObject.obstacle.NormalObstacle;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.basicObject.obstacle.StreetPerformance;
import imcrowd.engine.Controller;
import imcrowd.engine.Engine;
import imcrowd.io.FileIcon;
import imcrowd.io.PropertiesFileFilter;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.io.report.ConfigurationReport;
import imcrowd.manager.GroupManager;
import imcrowd.ui.ExperimentParameterTab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.Date;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.vecmath.Vector2f;


public class ConfigurationIOHandler {
	private static ConfigurationIOHandler singleton = new ConfigurationIOHandler();
	final private String defaultDir = System.getProperty("user.dir")+ File.separator + "DataSet"; 
	
	boolean isTransformF2L = false;
	
	JFileChooser fileChooser;
	Properties configuration;				
	
	Engine engine = Engine.getInstance();
	ImageLoader imageLoader = ImageLoader.getInstance();
	ConfigurationReport configReport;
	
	private ConfigurationIOHandler(){		
		//fileChooser = new JFileChooser("." + File.separator + "bin" + File.separator + "save");
		//fileChooser = new JFileChooser(System.getProperty("user.dir") + File.separator + "bin" + File.separator + "save");  

		File file = new File(defaultDir);
		if(!file.exists()){
			file.mkdir();
		}
		
		fileChooser = new JFileChooser(defaultDir);
		fileChooser.addChoosableFileFilter(new PropertiesFileFilter("sim"));			//only display the file extension ".sim"
		fileChooser.setFileView(new FileIcon());										//display the fileIcon of .sim

		configuration = new Properties();
	}
	
	public static ConfigurationIOHandler getInstance(){
		return singleton;
	}
	
	public ConfigurationReport getConfigReport(){
		return configReport;
	}
	
	/* Agent */
	private void readInAgent(){
		float x,y,theta;
		int leaderId, groupId, agentId;
		
		String str;
		String[] strArray;
		String agent_type;
		Agent ag, myLeader;
		Map<Integer, Leader> leaderMap = new HashMap<Integer, Leader>();
		
		int agentNum = Integer.valueOf(configuration.getProperty("agentNum"));
		
		for(int i=0; i<agentNum; i++){
			
			agent_type = configuration.getProperty("ag_type"+i);
			
			str = configuration.getProperty("ag_vPosition"+i);
			strArray = str.split(" ");
			x = Float.valueOf(strArray[0]);
			y = Float.valueOf(strArray[1]);
			
			str = configuration.getProperty("ag_orientation"+i);
			theta = Float.valueOf(str);
			
			str = configuration.getProperty("ag_groupId"+i);
			groupId = Integer.valueOf(str);
			
			str = configuration.getProperty("ag_id"+i);
			agentId = Integer.valueOf(str);
			
			
			if(agent_type.equals("Leader")){
				
				if(isTransformF2L){	//All leader 
					ag = new Leader(x, y, theta, agentId);
				}
				else{
					ag = new Leader(x, y, theta, groupId);
					leaderMap.put(agentId, (Leader)ag);
				}
			}
			else if(agent_type.equals("Follower")){
				
				if(isTransformF2L){	//All leader 
					ag = new Leader(x, y, theta, agentId);
				}
				else{
					leaderId = Integer.valueOf(configuration.getProperty("ag_myLeaderId"+i));
					myLeader = leaderMap.get(leaderId);
					
					ag = new Follower(x, y, theta, groupId, myLeader);
					((Leader)myLeader).addFollower((Follower)ag);
				}
			}
			else if(agent_type.equals("Agitator")){
				
				ag = new Agitator(x, y, theta, groupId);
			}
			else{	//Police
				
				ag = new Police(x, y, theta, groupId);
			}
			
			ag.setAttributes(configuration, i);
			
			engine.getAgentManager().addAgent(ag);
		}
	}
	
	private void writeOutAgent(){
		int i;
		Agent ag;
		List<Agent> agList = engine.getAgentManager().getAgentList();
		
		Map<String, String> attrMap;
		
		for(i=0; i<agList.size(); i++){
			ag = agList.get(i);
			
			attrMap = ag.getAttributes(i);
			
			for(String key: attrMap.keySet()){
				configuration.setProperty(key, attrMap.get(key));
			}
		}
		configuration.setProperty("agentNum", String.valueOf(i));				
	}
	
	/* Goal */
	private void readInGoal(){
		float x,y;
		String str;
		String[] strArray;
		Goal goal;
		
		int goalNum = Integer.valueOf(configuration.getProperty("goalNum"));

		for(int i=0; i<goalNum; i++){
			str = configuration.getProperty("goal_vPosition"+i);
			strArray = str.split(" ");
			x = Float.valueOf(strArray[0]);
			y = Float.valueOf(strArray[1]);
			
			String imgName = configuration.getProperty("goal_IMG"+i);
			int index = imgName.lastIndexOf('.');	
			int imgNum = Integer.valueOf(imgName.substring(0, index));
			
			if(imgName.endsWith("png")){
				goal = new Goal(new Vector2f(x,y), imageLoader.getPngImg(imgNum)); 
			}
			else{
				goal = new Goal(new Vector2f(x,y), imageLoader.getGifImg(imgNum));
			}
			engine.getGoalManager().addGoal(goal);
		}
	}
	
	private void writeOutGoal(){
		int i;
		Goal goal;
		List<Goal> goalList = engine.getGoalManager().getGoalList();
		
		Map<String, String> attrMap;
		
		for(i=0; i<goalList.size(); i++){
			goal = goalList.get(i);
			attrMap = goal.getAttributes(i);
			for(String key: attrMap.keySet()){
				configuration.setProperty(key, attrMap.get(key));
			}
		}
		configuration.setProperty("goalNum", String.valueOf(i));				
		
	}
	
	/* Obstacle */
	private void readInObstacle(){
		float x,y;
		String str;
		String[] strArray;
		String ob_type;
		Obstacle ob;
		
		int obNum = Integer.valueOf(configuration.getProperty("obstacleNum"));
	
		
		for(int i=0; i<obNum; i++){
			
			ob_type = configuration.getProperty("ob_type"+i);
			
			str = configuration.getProperty("ob_vPosition"+i);
			strArray = str.split(" ");
			x = Float.valueOf(strArray[0]);
			y = Float.valueOf(strArray[1]);
								
			
			if(ob_type.equals("NormalOb")){
				
				String imgName = configuration.getProperty("ob_IMG"+i);
				int index = imgName.lastIndexOf('.');	
				int imgNum = Integer.valueOf(imgName.substring(0, index));
				
				if(imgName.endsWith("png")){
					ob = new NormalObstacle(new Vector2f(x,y), imageLoader.getPngImg(imgNum)); 
				}
				else{
					ob = new NormalObstacle(new Vector2f(x,y), imageLoader.getGifImg(imgNum));
				}
			}
			else if(ob_type.equals("InteractiveOb")){
				
				int interactiveId = Integer.valueOf(configuration.getProperty("ob_interactiveId"+i));
				
				String imgName = configuration.getProperty("ob_IMG"+i);
				int index = imgName.lastIndexOf('.');	
				int imgNum = Integer.valueOf(imgName.substring(0, index));
				
				if(imgName.endsWith("png")){
					ob = new InteractiveObstacle(new Vector2f(x,y), imageLoader.getPngImg(imgNum), interactiveId); 
				}
				else{
					ob = new InteractiveObstacle(new Vector2f(x,y), imageLoader.getGifImg(imgNum), interactiveId);
				}				
			}
			else if(ob_type.equals("Monster")){
				ob = new Monster(new Vector2f(x,y));
				
			}
			else{	//StreetPerformance
				
				ob = new StreetPerformance(new Vector2f(x,y));
			}
			
			ob.setAttributes(configuration, i);
			
			engine.getObstacleManager().addObstacle(ob);
		}			
		
		
	}
	
	private void writeOutObstacle(){
		int i;
		Obstacle ob;
		List<Obstacle> obList  = engine.getObstacleManager().getObList();
		
		Map<String, String> attrMap;
		
		for(i=0; i<obList.size(); i++){
			ob = obList.get(i);
			attrMap = ob.getAttributes(i);
			for(String key: attrMap.keySet()){
				configuration.setProperty(key, attrMap.get(key));
			}
		}
		
		configuration.setProperty("obstacleNum", String.valueOf(i));			
	}
		
	
	public void readFile(File file){
		try{
			configuration.load(new FileInputStream(file));
		}catch(FileNotFoundException e1){
			e1.printStackTrace();
		}catch(IOException e2){
			e2.printStackTrace();
		}
		
		engine.stop();
		
		int alpha, beta;
		String str;
		
		str = configuration.getProperty("Alpha");
		ExperimentParameterTab.getInstance().setAlpha(str);
		alpha = Integer.valueOf(str);
		
		str = configuration.getProperty("Beta");
		ExperimentParameterTab.getInstance().setBeta(str);
		beta = Integer.valueOf(str);			
		
		str = configuration.getProperty("GroupId");
		GroupManager.setId(Integer.valueOf(str));
		
		readInGoal();
		readInObstacle();
		readInAgent();
		
		configReport = new ConfigurationReport(alpha, beta);
		
	}
	
	public void readFile(){
		File file;
		//fileChooser.setApproveButtonText("Confirm");
		engine.pause();
		fileChooser.setDialogTitle("Open File");
		int state_of_fileChooser = fileChooser.showOpenDialog(null);
		
		if(state_of_fileChooser == JFileChooser.APPROVE_OPTION){
			file = fileChooser.getSelectedFile();

			int option = JOptionPane.showConfirmDialog(null, "Transform all followers into Leader?"); 
			if(option == JOptionPane.YES_OPTION){
				isTransformF2L = true;
			}
			else if(option == JOptionPane.NO_OPTION){			
				isTransformF2L = false;
			}
			else if(option == JOptionPane.CANCEL_OPTION){
				isTransformF2L = false;
				return;
			}
			
			try{
				configuration.load(new FileInputStream(file));
			}catch(FileNotFoundException e1){
				e1.printStackTrace();
			}catch(IOException e2){
				e2.printStackTrace();
			}
			
			engine.stop();
			
			int alpha, beta;
			String str;
			
			str = configuration.getProperty("Alpha");
			ExperimentParameterTab.getInstance().setAlpha(str);
			alpha = Integer.valueOf(str);
			
			str = configuration.getProperty("Beta");
			ExperimentParameterTab.getInstance().setBeta(str);
			beta = Integer.valueOf(str);			
			
			str = configuration.getProperty("GroupId");
			GroupManager.setId(Integer.valueOf(str));
			
			readInGoal();
			readInObstacle();
			readInAgent();
			
			configReport = new ConfigurationReport(alpha, beta);
			
		}
	}
	
	public void writeFile(){
		File file;
		
		engine.pause();
		
		fileChooser.setDialogTitle("Save File");	
		
		int state_of_fileChooser = fileChooser.showSaveDialog(null); 
		
		
		if(state_of_fileChooser == JFileChooser.APPROVE_OPTION){
			file = fileChooser.getSelectedFile(); 				
			
			File tmpFile;
			int index = file.getName().lastIndexOf(".");
			
			/*****************************************************************************
			 * ".sim" would be the default extension if user wouldn't input any extension. 
			 *****************************************************************************/
			if(index == -1){
				tmpFile = new File(file.getPath() + ".sim");
			}
			else{
				tmpFile = file; 
			}
			
			try{
				
			    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			    Date date = new Date();

			    configuration.setProperty("Alpha", String.valueOf(Controller.getInstance().getAlpha()));
				configuration.setProperty("Beta", String.valueOf(Controller.getInstance().getBeta()));
				configuration.setProperty("GroupId", String.valueOf(GroupManager.getCurrentId()));
				
				writeOutAgent();
				writeOutGoal();
				writeOutObstacle();
			    
				configuration.store(new FileOutputStream(tmpFile), dateFormat.format(date));
			}catch(IOException ioe){
					ioe.printStackTrace();
			}
		}
	}
}	
	