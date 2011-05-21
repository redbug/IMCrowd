package imcrowd.engine;

import imcrowd.basicObject.MyObject;
import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.ui.AgentTab;
import imcrowd.ui.ExperimentParameterTab;
import imcrowd.ui.GoalTab;
import imcrowd.ui.ObstacleTab;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class Controller {
  private static Controller controller = new Controller();
  AgentTab agentTab; 
  GoalTab goalTab; 
  ObstacleTab obTab;
  ExperimentParameterTab experimentTab;
	
  private Controller() {
	  agentTab = AgentTab.getInstance();
	  goalTab = GoalTab.getInstance();
	  obTab = ObstacleTab.getInstance();
	  experimentTab = ExperimentParameterTab.getInstance();
  }
  
  public static Controller getInstance(){
	  return controller;
  }
  
  /* Notify AgentTab and ObstacleTab to renew the information of the control panel */
  public  void setTarget(MyObject obj){
	  if(obj == null){
		  agentTab.setTargetAgent((Agent)obj);
		  obTab.setTargetObstacle((Obstacle)obj);
	  }else{ 
		  if(obj instanceof Agent){
			  agentTab.setTargetAgent((Agent)obj);
			  obTab.setTargetObstacle(null);
		  }	  
		  else if(obj instanceof Obstacle){
			  obTab.setTargetObstacle((Obstacle)obj);
			  agentTab.setTargetAgent(null);
		  }	
	  }
  }
  
  	/* AgentTab */
	public void setAgentColorCBox(int index){
		agentTab.setAgentColorCBox(index);
	}
  
	public boolean isRandom(){
		return agentTab.isRandom();
	}
	
  
	//Bound Action.
	public int getCustom_BA(){
		return agentTab.getCustom_BA();
	}
	
	//Speed Mode
	public int getSpeedMode() {
		return agentTab.getSpeedMode();
	}
	
	//Mass.
	public float getMassTFieldValue(){
		return agentTab.getMassTFieldValue();
	}
	
	public void setMassTFieldValue(String str){
		agentTab.setMassTFieldValue(str);
	}
	
	public JTextField getMassTField(){
		return agentTab.getMassTField();
	}
  
	//Diameter.
	public int getDiameterTFieldValue(){
		return agentTab.getDiameterTFieldValue();
	}
	
	public void setDiameterFieldValue(String str){
		agentTab.setDiameterFieldValue(str);
	}
	
	public JTextField getDiameterField(){
		return agentTab.getDiameterField();
	}
	
	
	 //Beta
	 public int getBeta() {
		 return experimentTab.getBeta();
	 }
	  
	 public void setBeta(String str) {
		 experimentTab.setBeta(str);  
	 }
	  
	 //Alpha
	 public int getAlpha() {
		 return experimentTab.getAlpha();
	 }
	  
	 public void setAlpha(String str) {
		 experimentTab.setAlpha(str);
	 }
	
	
  	//Sliders for Reynold's Model
  	public int getAlignment() {
		return agentTab.getAlignment();
	}
  	
  	public void setAlignment(int a){
  		agentTab.setAlignment(a);
  	}

	public int getCohesion() {
		return agentTab.getCohesion();
		
	}
	
	public void setCohesion(int c){
		agentTab.setCohesion(c);
	}

	public int getSpeparation() {
		return agentTab.getSeparation();
	}
	
	public void setSeparation(int s){
		agentTab.setSeparation(s);
	}
	
	

	
	//Sliders for Infective resource.
	public int getAlignmentI() {
		return agentTab.getAlignmentI();
	}
	
	public void setAlignmentI(int a){
		agentTab.setAlignmentI(a);
	}

	public int getCohesionI() {
		return agentTab.getCohesionI();
	}
	
	public void setCohesionI(int c){
		agentTab.setCohesionI(c);
	}

	public int getSpeparationI() {
		return agentTab.getSpeparationI();
	}
	
	public void setSeparationI(int s){
		agentTab.setSeparationI(s);
	}

	
	public void disableSeparationISlider(){
	  agentTab.disableSeparationISlider();
	}
		  
	public void disableCohesionISlider(){
		agentTab.disableCohesionISlider();
	}
		  
	public void disableAlignmentISlider(){
		agentTab.disableAlignmentISlider();
	}
	
	
	
	//slider for active force. 
	public JSlider getActiveFSlider() {
		return agentTab.getActiveFSlider();
	}

	//slider for max steering force. 
	public JSlider getMaxForceSlider() {
		return agentTab.getMaxForceSlider();
	}

	//slider for speed.
	public JSlider getMaxNormalSpeedSlider() {
		return agentTab.getMaxNormalSpeedSlider();
	}

	//sliders for view field.
	public JSlider getVRadiusSlider() {
		return agentTab.getVRadiusSlider();
	}

	public JSlider getVThetaSlider() {
		return agentTab.getVThetaSlider();
	}  
	
	//color
 	public int getAgentColor() {
		return agentTab.getAgentColor();
	}
 	
 	//Ba_radio
 	public JRadioButton[] getBA_radio() {
		return agentTab.getBA_radio();
	}
 		
	
	/* ObstacleTab */
	//image
	public ImageIcon getCurrentOBImg(){
		return obTab.getCurrentImg();
	}
 	
	public boolean isInteractiveOB(){
		return obTab.isInteractive();
	}
	
	public int getInteractiveId(){
		return obTab.getInteractiveId();
	}

 	/* GoalTab */

 	
 	/* ObstacleTab */
 	
 	//image
	public ImageIcon getCurrentGoalImg(){
		return goalTab.getCurrentImg();
	}
}
