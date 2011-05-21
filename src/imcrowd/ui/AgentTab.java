package imcrowd.ui;

import imcrowd.basicObject.WeightParameters;
import imcrowd.basicObject.agent.Agent;
import imcrowd.engine.Engine;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class AgentTab extends JScrollPane implements ActionListener, ChangeListener, ItemListener, Colleague{
	  public static final String[] COLOR_STRING ={"RED","BLUE","GREEN","PINK","GRAY"};
	  
	  private static AgentTab agentTab = new AgentTab();
	  
	  public static final Color[] COLOR_ARRAY = {
		 Color.RED, Color.BLUE, new Color(0, 150, 0),
		 Color.MAGENTA, Color.DARK_GRAY
	  };
	  
	  
	  
	  private Mediator mediator;
	  
	  boolean random;
	  
	  int customize_BA;  
	  int speed_mode;
	  
	  int colorId;
	  
	  //JSlider
	  JSlider maxForceSlider;
	  JSlider maxNormalSpeedSlider;
	  JSlider activeFSlider;
	  JSlider vRadiusSlider;
	  JSlider vThetaSlider;
	  JSlider separationSlider;
	  JSlider cohesionSlider;
	  JSlider alignmentSlider;
	  JSlider iSeparationSlider;
	  JSlider iCohesionSlider;
	  JSlider iAlignmentSlider;
	  
	  //JLabel
	  JLabel  maxForceLabel;
	  JLabel  maxNormalSpeedLabel;
	  JLabel  activeFLabel;
	  JLabel  vRadiusLabel;
	  JLabel  vThetaLabel;
	  JLabel  sfLabel;
	  JLabel  cfLabel;
	  JLabel  afLabel;
	  JLabel  iSFLabel;
	  JLabel  iCFLabel;
	  JLabel  iAFLabel;
	  
	  //JTextField
	  JTextField massTField;
	  JTextField diameterTField;
	  
	  
	  //JRadioButton
	  JRadioButton[] BA_radio;
	  
	  //ButtonGroup
	  ButtonGroup BA_group;
	  ButtonGroup speed_group;
	  
	  //JCheckBox
	  JCheckBox randomCBox;
	  
	  //JComboBox
	  JComboBox agentColorCBox;
	  
//	  ImageIcon csButtonImg = new ImageIcon(ClassLoader.getSystemResource("res/crosshair1.gif"));
//	  ImageIcon crosshairImg = new ImageIcon(ClassLoader.getSystemResource("res/crosshair1.gif"));
//	  Image targetCursorImg = crosshairImg.getImage();	
	  
	  ImageIcon crosshairImg = ImageLoader.crosshairImg;
	  
	  Agent targetAgent;						// the agent who was selected by right-click mouse action.  
	  Engine engine;
	  
	  private AgentTab(){
		    maxForceSlider	= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_Force,		100);
		    maxNormalSpeedSlider	= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_NormalSpeed,		10);
		    activeFSlider	= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wActive,		5);
		    vRadiusSlider	= new JSlider(JSlider.HORIZONTAL, 5, WeightParameters.Max_ViewRadius,	100);
		    vThetaSlider	= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_ViewTheta,	220);
		    
		    separationSlider = new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wSeparation, 8);
		    cohesionSlider = new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wCohesion,	4);
		    alignmentSlider = new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wAlignment,	12);
		    
		    iSeparationSlider = new JSlider(JSlider.HORIZONTAL, -WeightParameters.Max_wSeparation,	WeightParameters.Max_wSeparation,	-5);
		    iCohesionSlider = new JSlider(JSlider.HORIZONTAL, -WeightParameters.Max_wCohesion, 	WeightParameters.Max_wCohesion,		-4);
		    iAlignmentSlider = new JSlider(JSlider.HORIZONTAL, -WeightParameters.Max_wAlignment,	WeightParameters.Max_wAlignment,	-3);
		    
		    
		    //JRadioButton 
			BA_radio = new JRadioButton[3];
			
			BA_radio[0] = new JRadioButton("STOP");
			BA_radio[1] = new JRadioButton("WRAP",true);
			customize_BA = 1;
			BA_radio[2] = new JRadioButton("LEAVE");
			
			BA_group = new ButtonGroup();
			
			for(int i=0;i<BA_radio.length;i++){
				BA_group.add(BA_radio[i]);
				BA_radio[i].addActionListener(this);
			}
		
			
			//JCheckBox
			randomCBox = new JCheckBox("Ranodm",false);
			random = false;
			randomCBox.addItemListener(this);
			
			
			//JComboBox
			agentColorCBox = new JComboBox(COLOR_STRING);
			agentColorCBox.addActionListener(this);
		    maxForceSlider.addChangeListener(this);
		    maxNormalSpeedSlider.addChangeListener(this);
		    activeFSlider.addChangeListener(this);
		    vRadiusSlider.addChangeListener(this);
		    vThetaSlider.addChangeListener(this);
		    separationSlider.addChangeListener(this);
		    cohesionSlider.addChangeListener(this);
		    alignmentSlider.addChangeListener(this);
		    iSeparationSlider.addChangeListener(this);
		    iCohesionSlider.addChangeListener(this);
		    iAlignmentSlider.addChangeListener(this);
		    
		    iSeparationSlider.setEnabled(false);
			iAlignmentSlider.setEnabled(false);
			iCohesionSlider.setEnabled(false);
			
			//JTextFiled
			massTField = new JTextField("10",2);
			diameterTField = new JTextField("10",2);
		
			createPanel();
	  }
	  
	  public void setAgentColorCBox(int index){
		  agentColorCBox.setSelectedIndex(index);
	  }
	  
	  public void disableSeparationISlider(){
		  iSeparationSlider.setEnabled(false);
	  }
	  
	  public void disableCohesionISlider(){
		  iCohesionSlider.setEnabled(false);
	  }
	  
	  public void disableAlignmentISlider(){
		  iAlignmentSlider.setEnabled(false);
	  }
	  
	  public static AgentTab getInstance() {
		  return agentTab;
	  }
	  
	  public void setMediator(Mediator mediator) {
			this.mediator = mediator;
			engine = (Engine)mediator;
	  }
	  
	  public void setManagerMethod() {}
	  
	  	  	
	  public void setTargetAgent(Agent ag) {
		  targetAgent = ag;
	  }
	  	  
	  public boolean isRandom(){
		  return random;
	  }

	  
	  public int getCustom_BA() {
		  return customize_BA;
	  }
	  
	  public int getSpeedMode() {
		  return speed_mode;
	  }

	  public float getMassTFieldValue() {
		  return Float.valueOf(massTField.getText());
	  }

	  public void setMassTFieldValue(String str) {
		  massTField.setText(str);
	  }
	  
	  public JTextField getMassTField(){
		  return massTField;
	  }
	  
	  public int getDiameterTFieldValue() {
		  return Integer.valueOf(diameterTField.getText());
	  }

	  public void setDiameterFieldValue(String str) {
		  diameterTField.setText(str);
	  }
	  
	  public JTextField getDiameterField(){
		  return diameterTField;
	  }
	  
	  public int getAlignment() {
		  return alignmentSlider.getValue();
	  }
	  
	  public void setAlignment(int a){
		  alignmentSlider.setValue(a);
	  }

	  public int getCohesion() {
		  return cohesionSlider.getValue();
	  }
	  
	  public void setCohesion(int c){
		  cohesionSlider.setValue(c);
	  }

	  public int getSeparation() {
		  return separationSlider.getValue();
	  }
	  
	  public void setSeparation(int s){
		  separationSlider.setValue(s);
	  }

	  public int getAlignmentI() {
		  return iAlignmentSlider.getValue();
	  }
	  
	  public void setAlignmentI(int a){
		  iAlignmentSlider.setValue(a);
	  }

	  public int getCohesionI() {
		  return iCohesionSlider.getValue();
	  }
	  
	  public void setCohesionI(int c){
		  iCohesionSlider.setValue(c);
	  }

	  public int getSpeparationI() {
		  return iSeparationSlider.getValue();
	  }
	  
	  public void setSeparationI(int s){
		  iSeparationSlider.setValue(s);
	  }

	  public JSlider getActiveFSlider() {
		  return activeFSlider;
	  }

	  public JSlider getMaxForceSlider() {
		  return maxForceSlider;
	  }
	  
	  public JSlider getMaxNormalSpeedSlider() {
		  return maxNormalSpeedSlider;
	  }

	  public JSlider getVRadiusSlider() {
		  return vRadiusSlider;
	  }

	  public JSlider getVThetaSlider() {
		  return vThetaSlider;
	  }

	  public int getAgentColor() {
		  return colorId;
	  }

	  public JRadioButton[] getBA_radio() {
		  return BA_radio;
	  }
	  
	  
/*	  
	  // checkBox of random 
	  public JCheckBox getRandomCBox() {
		  return randomCBox;
	  }
*/	  
	  public void createPanel(){

			JPanel outterPanel = new JPanel();
			outterPanel.setLayout(new BoxLayout(outterPanel,BoxLayout.Y_AXIS));
			JPanel innerPanel;
			
			GridBagConstraints gbc;
	
			JLabel argName;
				
			
		    /* Physical Properties */
			JPanel basicPropertyPanel = new JPanel();
			basicPropertyPanel.setLayout(new BoxLayout(basicPropertyPanel,BoxLayout.Y_AXIS));
			basicPropertyPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(),"Basic Properties ", TitledBorder.LEFT, TitledBorder.TOP));
			
			{
			    /* special, leader, leaderWeight */
			    innerPanel = new JPanel();
			    innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			    innerPanel.add(randomCBox);
			    
			    basicPropertyPanel.add(innerPanel);
	       	    
	       	    /* mass, diameter, color */
	       	    innerPanel = new JPanel();
	       	    innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			    argName = new JLabel("Mass:");
			    innerPanel.add(argName);
			    innerPanel.add(massTField);
			    innerPanel.add(Box.createHorizontalStrut(5));
			    
			    argName = new JLabel("Diameter:");
			    innerPanel.add(argName);
			    innerPanel.add(diameterTField);
			    innerPanel.add(Box.createHorizontalStrut(5));
			    
			    argName = new JLabel("Color:");
			    innerPanel.add(argName);
			    innerPanel.add(agentColorCBox);
			    //innerPanel.add(Box.createHorizontalStrut(5));
			
			    
			    basicPropertyPanel.add(innerPanel);
		    
			
		    
			    /* maximum steering force and maximum speed constraint */
				innerPanel = new JPanel();
				innerPanel.setLayout(new GridBagLayout());
				gbc =new GridBagConstraints();
				gbc.anchor = GridBagConstraints.WEST;
				gbc.insets = new Insets(1,1,2,2);
			
					/* maximum force */
					gbc.gridy=0;
					gbc.gridx=0;
				argName = new JLabel("Max Force");	
				innerPanel.add(argName,gbc);
					gbc.gridx=1;
				innerPanel.add(maxForceSlider);
					gbc.gridx=2;
				maxForceLabel = new JLabel(String.valueOf(maxForceSlider.getValue()));
				innerPanel.add(maxForceLabel);			
			
					/* maximum speed */
					gbc.gridy=2;
					gbc.gridx=0;
				argName = new JLabel("Max Normal Speed");
			    innerPanel.add(argName,gbc);
					gbc.gridx=1;
				innerPanel.add(maxNormalSpeedSlider,gbc);
					gbc.gridx=2;
				maxNormalSpeedLabel = new JLabel(String.valueOf(maxNormalSpeedSlider.getValue()));	
			    innerPanel.add(maxNormalSpeedLabel,gbc);
		    
					/* maximum active force */
					gbc.gridy=3;
					gbc.gridx=0;
				argName = new JLabel("Objective");
			    innerPanel.add(argName,gbc);
					gbc.gridx=1;
				innerPanel.add(activeFSlider,gbc);
					gbc.gridx=2;
				activeFLabel = new JLabel(String.valueOf(activeFSlider.getValue()));	
			    innerPanel.add(activeFLabel,gbc);		    
			    basicPropertyPanel.add(innerPanel);
			}
			
			outterPanel.add(basicPropertyPanel);
		    
		    
		    /* View Field */
		    innerPanel = new JPanel();
		    innerPanel.setLayout(new GridBagLayout());
		    innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(),"View Field", TitledBorder.LEFT, TitledBorder.TOP));
		    	   
			gbc =new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(1,1,2,2);
			
					/* perception distance of the view field */
					gbc.gridy=0;
					gbc.gridx=0;
			    argName = new JLabel("Radius");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(vRadiusSlider,gbc);
					gbc.gridx=2;
				vRadiusLabel = new JLabel(String.valueOf(vRadiusSlider.getValue()));
				innerPanel.add(vRadiusLabel,gbc);
				
					/* perception angle of the view field */
					gbc.gridy=1;
					gbc.gridx=0;
				argName = new JLabel("Degree");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(vThetaSlider,gbc);
					gbc.gridx=2;
				vThetaLabel = new JLabel(String.valueOf(vThetaSlider.getValue()));
				innerPanel.add(vThetaLabel,gbc);
		    
			outterPanel.add(innerPanel);

		    
			
			/*Reynold's Model*/
			innerPanel = new JPanel();
		    innerPanel.setLayout(new GridBagLayout());
		    innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(),"Flocking Weights", TitledBorder.LEFT, TitledBorder.TOP));
		    	   
			gbc =new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(1,1,2,2);
			
					/* separation weight */
					gbc.gridy=0;
					gbc.gridx=0;
			    argName = new JLabel("Separation force");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(separationSlider,gbc);
					gbc.gridx=2;
				sfLabel = new JLabel(String.valueOf(separationSlider.getValue()));
				innerPanel.add(sfLabel,gbc);
				
					/* cohesion weight */
					gbc.gridy=1;
					gbc.gridx=0;
				argName = new JLabel("Cohesion force");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(cohesionSlider,gbc);
					gbc.gridx=2;
				cfLabel = new JLabel(String.valueOf(cohesionSlider.getValue()));
				innerPanel.add(cfLabel,gbc);
				
					/* alignment weight */
					gbc.gridy=2;
					gbc.gridx=0;
				argName = new JLabel("Alignment force");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(alignmentSlider,gbc);
					gbc.gridx=2;
				afLabel = new JLabel(String.valueOf(alignmentSlider.getValue()));
				innerPanel.add(afLabel,gbc);
				
			outterPanel.add(innerPanel);	
		
			
			/* Infective Force */		
			innerPanel = new JPanel();
		    innerPanel.setLayout(new GridBagLayout());
		    innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(),"iFlocking Weights", TitledBorder.LEFT, TitledBorder.TOP));
		    	   
			gbc =new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(1,1,2,2);
			
					/* separation weight */
					gbc.gridy=0;
					gbc.gridx=0;
			    argName = new JLabel("Separation force");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(iSeparationSlider,gbc);
					gbc.gridx=2;
				iSFLabel = new JLabel(String.valueOf(iSeparationSlider.getValue()));
				innerPanel.add(iSFLabel,gbc);
				
					/* cohesion weight */
					gbc.gridy=1;
					gbc.gridx=0;
				argName = new JLabel("Cohesion force");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(iCohesionSlider,gbc);
					gbc.gridx=2;
				iCFLabel = new JLabel(String.valueOf(iCohesionSlider.getValue()));
				innerPanel.add(iCFLabel,gbc);
				
					/* alignment weight */
					gbc.gridy=2;
					gbc.gridx=0;
				argName = new JLabel("Alignment force");
			    innerPanel.add(argName,gbc);
			    	gbc.gridx=1;
				innerPanel.add(iAlignmentSlider,gbc);
					gbc.gridx=2;
				iAFLabel = new JLabel(String.valueOf(iAlignmentSlider.getValue()));
				innerPanel.add(iAFLabel,gbc);
		    
				
				outterPanel.add(innerPanel);
			
			
			
		    /* Border Reaction of Agents */
		    innerPanel = new JPanel();
		    innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		    innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(),"Border mode", TitledBorder.LEFT, TitledBorder.TOP));
		    	   
		    for(int i=0;i<BA_radio.length;i++){
		    	innerPanel.add(BA_radio[i]);
		    }
		    
		    outterPanel.add(innerPanel);
		    
		    this.getViewport().add(outterPanel);
	  }
	  
	  public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == randomCBox){
			if(randomCBox.isSelected()){
				random = true;
			} else {
				random = false;
			}
		}	  	  	
	}

	public void stateChanged(ChangeEvent e) {

		/* Agent Tab */
		maxForceLabel.setText(String.valueOf(maxForceSlider.getValue()));
		maxNormalSpeedLabel.setText(String.valueOf(maxNormalSpeedSlider.getValue()));
		activeFLabel.setText(String.valueOf(activeFSlider.getValue()));
		vRadiusLabel.setText(String.valueOf(vRadiusSlider.getValue()));
		vThetaLabel.setText(String.valueOf(vThetaSlider.getValue()));
		sfLabel.setText(String.valueOf(separationSlider.getValue()));
		cfLabel.setText(String.valueOf(cohesionSlider.getValue()));
		afLabel.setText(String.valueOf(alignmentSlider.getValue()));
		iSFLabel.setText(String.valueOf(iSeparationSlider.getValue()));
		iCFLabel.setText(String.valueOf(iCohesionSlider.getValue()));
		iAFLabel.setText(String.valueOf(iAlignmentSlider.getValue()));

		if (targetAgent != null) {

			if ((JSlider) e.getSource() == maxForceSlider) {
				targetAgent.setMaxForce(maxForceSlider.getValue());
			}
			else if ((JSlider) e.getSource() == maxNormalSpeedSlider) {
				targetAgent.setNormalSpeed(maxNormalSpeedSlider.getValue());
			}
			else if ((JSlider) e.getSource() == activeFSlider) {
				targetAgent.setActiveF(activeFSlider.getValue());
			}
			else if ((JSlider) e.getSource() == vRadiusSlider) {
				targetAgent.setVRadius(vRadiusSlider.getValue());
			}
			else if ((JSlider) e.getSource() == vThetaSlider) {
				targetAgent.setVTheta(vThetaSlider.getValue());
			}
			else if ((JSlider) e.getSource() == separationSlider) {
				targetAgent.setSeparationWeight(separationSlider.getValue());
			}
			else if ((JSlider) e.getSource() == cohesionSlider) {
				targetAgent.setCohesionWeight(cohesionSlider.getValue());
			}
			else if ((JSlider) e.getSource() == alignmentSlider) {
				targetAgent.setAlignmentWeight(alignmentSlider.getValue());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == agentColorCBox){
			colorId = agentColorCBox.getSelectedIndex();
			if (targetAgent != null) {
				targetAgent.setColorOfPS(colorId);
			}
		}
		else{
			JRadioButton RB[][] = {BA_radio};
			
			boolean flag = false;
			for (int i = 0; i < RB.length; i++) {	
				for(int j=0; j<RB[i].length; j++) {
					
					if(i == 0) {		//BA_radio
						if (e.getSource() == RB[0][j]) {
							customize_BA = j;
							if (targetAgent != null) {
								targetAgent.setBA(customize_BA);
							}
							flag = true;
							break;
						}
						continue;
					}
					
					if(i==1) {			//speed_radio
						if (e.getSource() == RB[1][j]) {
							speed_mode = j;							
							flag = true;
							break;
						}
						continue;
					}	
				}
				if(flag) {
					break;
				}
			}	
		}
	}
}
