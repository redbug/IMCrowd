package imcrowd.ui;


import imcrowd.basicObject.WeightParameters;
import imcrowd.basicObject.agent.Agent;
import imcrowd.engine.Engine;
import imcrowd.manager.AgentManager;
import imcrowd.manager.GridManager;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GlobalTab extends JScrollPane implements ActionListener, ChangeListener, ItemListener, Colleague{
	private static GlobalTab globalTab = new GlobalTab();
	private Mediator mediator;
						
	int visualMode;

	//JCheckBox
	JCheckBox drawGridCBOX;
	JCheckBox showTextCBOX;
	
	//JSlider
	JSlider maxForceSlider;
	JSlider maxNormalSpeedSlider;
	JSlider activeFSlider;
	JSlider radiusSlider;
	JSlider thetaSlider;
	JSlider separationSlider;
	JSlider cohesionSlider;
	JSlider alignmentSlider;
  
	//JLabel
	JLabel  maxForceLabel;
	JLabel  maxNormalSpeedLabel;
	JLabel  activeFLabel;
	JLabel  radiusLabel;
	JLabel  thetaLabel;
	JLabel  separationLabel;
	JLabel  cohesionLabel;
	JLabel  alignmentLabel;
	
	
	//JRadioButton
	JRadioButton[] heatMap_radio;
	JRadioButton[] visualMode_radio;
	
	//ButtonGroup
	ButtonGroup heatMap_group;
	ButtonGroup visualMode_group;
	
	//JComboBox
	JComboBox CommunStrategyCBox;
	String[] strategy ={"GateKeeper","Rational","Decreasing","Imitate"};
	Engine engine;
	AgentManager agentManager ;
	
	private GlobalTab(){
		drawGridCBOX			= new JCheckBox("Draw Grids",true);
		showTextCBOX			= new JCheckBox("Show Text",true);
		drawGridCBOX.addItemListener(this);
		showTextCBOX.addItemListener(this);
		
	    maxForceSlider 			= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_Force,			100);
	    maxNormalSpeedSlider	= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_NormalSpeed,			15);
	    activeFSlider 			= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wActive,			2);
	    radiusSlider 			= new JSlider(JSlider.HORIZONTAL, 5, WeightParameters.Max_ViewRadius,		100);
	    thetaSlider 			= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_ViewTheta,		220);
	    separationSlider 		= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wSeparation,		1);
	    cohesionSlider 			= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wCohesion,		1);
	    alignmentSlider 		= new JSlider(JSlider.HORIZONTAL, 0, WeightParameters.Max_wAlignment,		1);
	    
	    
	    //JRadioButton for states
	    heatMap_radio = new JRadioButton[5];
		

	    heatMap_radio[0] = new JRadioButton("None", true);
	    heatMap_radio[1] = new JRadioButton("Rationality");
		heatMap_radio[2] = new JRadioButton("Superiority");
		heatMap_radio[3] = new JRadioButton("Entropy");
		heatMap_radio[4] = new JRadioButton("Density");
		
		heatMap_group = new ButtonGroup();
		
		for(int i=0;i<heatMap_radio.length;i++){
			heatMap_group.add(heatMap_radio[i]);
			heatMap_radio[i].addActionListener(this);
		}
		
		
		//JRadioButton for visual modes
		visualMode_radio = new JRadioButton[4];
		
		visualMode = 0;
		visualMode_radio[0] = new JRadioButton("Normal", true);
		visualMode_radio[1] = new JRadioButton("Info");
		visualMode_radio[2] = new JRadioButton("Group");
		visualMode_radio[3] = new JRadioButton("State");
		
		visualMode_group = new ButtonGroup();
		
		for(int i=0;i<visualMode_radio.length;i++){
			visualMode_group.add(visualMode_radio[i]);
			visualMode_radio[i].addActionListener(this);
		}
		
		
	    
	    maxForceSlider.addChangeListener(this);
	    maxNormalSpeedSlider.addChangeListener(this);
	    activeFSlider.addChangeListener(this);
	    radiusSlider.addChangeListener(this);
	    thetaSlider.addChangeListener(this);
	    separationSlider.addChangeListener(this);
	    cohesionSlider.addChangeListener(this);
	    alignmentSlider.addChangeListener(this);
	    
		//JComboBox
		CommunStrategyCBox = new JComboBox(strategy);
		CommunStrategyCBox.setSelectedIndex(3);
		CommunStrategyCBox.addItemListener(this);
	    
	    createPanel();
	}
	
	public void changePoliceStrategy(int value){
		heatMap_radio[value].doClick();
	}
	
	public void changeVisualMode(int value){
		visualMode_radio[value].doClick();
	}
	
	public int getVisualMode(){
		return visualMode;
	}
	
	public static GlobalTab getInstance(){
		return globalTab;
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		agentManager = engine.getAgentManager();
	}
	public void setManagerMethod() {}
	
	
	
  	public void  createPanel(){
	  
		JPanel outterPanel = new JPanel();
		outterPanel.setLayout(new BoxLayout(outterPanel, BoxLayout.Y_AXIS));
		JPanel innerPanel;
		GridBagConstraints gbc;
	
		JLabel argName;
		
		//this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
	    outterPanel = new JPanel();
	    outterPanel.setLayout(new BoxLayout(outterPanel,BoxLayout.Y_AXIS));
	    
	    
	    /* Environment Setting */
	    innerPanel = new JPanel();
	    innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(),"Environment Setting", TitledBorder.LEFT, TitledBorder.TOP));
	    innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
	    innerPanel.add(drawGridCBOX);
	    innerPanel.add(showTextCBOX);
	    
	    argName = new JLabel("Strategy:");
	    innerPanel.add(argName);
	    innerPanel.add(CommunStrategyCBox);
	    	    
	    outterPanel.add(innerPanel);
	    

	    /* PoliceStrategy */
	    innerPanel = new JPanel();
	    innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(),"Police Strategy", TitledBorder.LEFT, TitledBorder.TOP));
	    innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
	    for(int i=0;i<heatMap_radio.length;i++){
	    	innerPanel.add(heatMap_radio[i]);
	    }
	    
	    outterPanel.add(innerPanel);
	    
	    
	    /* Visual Mode */
	    innerPanel = new JPanel();
	    innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(),"Visual Mode", TitledBorder.LEFT, TitledBorder.TOP));
	    innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
	    for(int i=0;i<visualMode_radio.length;i++){
	    	innerPanel.add(visualMode_radio[i]);
	    }
	    
	    outterPanel.add(innerPanel);
	    
	    
	    
	    
	    /* Physical Properties */
	    innerPanel = new JPanel();
	    innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(),"Physics properties", TitledBorder.LEFT, TitledBorder.TOP));
	    innerPanel.setLayout(new GridBagLayout());
	    
	    /* maximum steering force and maximum speed constraint */
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
				gbc.gridy=1;
				gbc.gridx=0;
			argName = new JLabel("Max Speed");
		    innerPanel.add(argName,gbc);
				gbc.gridx=1;
			innerPanel.add(maxNormalSpeedSlider,gbc);
				gbc.gridx=2;
			maxNormalSpeedLabel = new JLabel(String.valueOf(maxNormalSpeedSlider.getValue()));	
		    innerPanel.add(maxNormalSpeedLabel,gbc);
	    
		    	/* maximum active force */
				gbc.gridy=2;
				gbc.gridx=0;
			argName = new JLabel("Objective");
		    innerPanel.add(argName,gbc);
				gbc.gridx=1;
			innerPanel.add(activeFSlider,gbc);
				gbc.gridx=2;
			activeFLabel = new JLabel(String.valueOf(activeFSlider.getValue()));	
		    innerPanel.add(activeFLabel,gbc);		    
		    outterPanel.add(innerPanel);
		    this.add(outterPanel);
	    
	    
		/* View Field */
	    innerPanel = new JPanel();
	    innerPanel.setLayout(new GridBagLayout());
	    innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(),"View field", TitledBorder.LEFT, TitledBorder.TOP));
	    	   
		gbc =new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(1,1,2,2);
		
				/* perception distance of the view field */
				gbc.gridy=0;
				gbc.gridx=0;
		    argName = new JLabel("Radius");
		    innerPanel.add(argName,gbc);
		    	gbc.gridx=1;
			innerPanel.add(radiusSlider,gbc);
				gbc.gridx=2;
			radiusLabel = new JLabel(String.valueOf(radiusSlider.getValue()));
			innerPanel.add(radiusLabel,gbc);
			
				/* perception angle of the view field */
				gbc.gridy=1;
				gbc.gridx=0;
			argName = new JLabel("Degree");
		    innerPanel.add(argName,gbc);
		    	gbc.gridx=1;
			innerPanel.add(thetaSlider,gbc);
				gbc.gridx=2;
			thetaLabel = new JLabel(String.valueOf(thetaSlider.getValue()));
			innerPanel.add(thetaLabel,gbc);
	    
		this.add(innerPanel);
	    
		
		/*Reynold's Model*/
		innerPanel = new JPanel();
	    innerPanel.setLayout(new GridBagLayout());
	    innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(),"Reynold's Model", TitledBorder.LEFT, TitledBorder.TOP));
	    	   
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
			separationLabel = new JLabel(String.valueOf(separationSlider.getValue()));
			innerPanel.add(separationLabel,gbc);
			
				/* cohesion weight */
				gbc.gridy=1;
				gbc.gridx=0;
			argName = new JLabel("Cohesion force");
		    innerPanel.add(argName,gbc);
		    	gbc.gridx=1;
			innerPanel.add(cohesionSlider,gbc);
				gbc.gridx=2;
			cohesionLabel = new JLabel(String.valueOf(cohesionSlider.getValue()));
			innerPanel.add(cohesionLabel,gbc);
			
				/* alignment weight */
				gbc.gridy=2;
				gbc.gridx=0;
			argName = new JLabel("Alighment force");
		    innerPanel.add(argName,gbc);
		    	gbc.gridx=1;
			innerPanel.add(alignmentSlider,gbc);
				gbc.gridx=2;
			alignmentLabel = new JLabel(String.valueOf(alignmentSlider.getValue()));
			innerPanel.add(alignmentLabel,gbc);
			
		outterPanel.add(innerPanel);
		
		this.getViewport().add(outterPanel);
  }	

  	public void stateChanged(ChangeEvent e){
		maxForceLabel.setText(String.valueOf(maxForceSlider.getValue()));
		maxNormalSpeedLabel.setText(String.valueOf(maxNormalSpeedSlider.getValue()));
		activeFLabel.setText(String.valueOf(activeFSlider.getValue()));
		radiusLabel.setText(String.valueOf(radiusSlider.getValue()));
		thetaLabel.setText(String.valueOf(thetaSlider.getValue()));
		separationLabel.setText(String.valueOf(separationSlider.getValue()));
		cohesionLabel.setText(String.valueOf(cohesionSlider.getValue()));
		alignmentLabel.setText(String.valueOf(alignmentSlider.getValue()));	
		
		List<Agent> agList = agentManager.getAgentList();
		
		if((JSlider)e.getSource()== maxForceSlider){
			for(Agent ag:agList){
				ag.setMaxForce(maxForceSlider.getValue());
			}	  
		}
		else if((JSlider)e.getSource()== maxNormalSpeedSlider){
			for(Agent ag:agList){
				ag.setNormalSpeed(maxNormalSpeedSlider.getValue());
			}	
		}
		else if((JSlider)e.getSource()== activeFSlider){
			for(Agent a:agList){
				a.setActiveF(activeFSlider.getValue());
			}	
		}
		else if((JSlider)e.getSource()== radiusSlider){
			for(Agent a:agList){
				a.setVRadius(radiusSlider.getValue());
			}	
		}
		else if((JSlider)e.getSource()== thetaSlider){	 
			for(Agent a:agList){
				a.setVTheta(thetaSlider.getValue());
			}	
		}
		else if((JSlider)e.getSource()== separationSlider){
			for(Agent a:agList){
				a.setSeparationWeight(separationSlider.getValue());
			}
		}	
		else if((JSlider)e.getSource()== cohesionSlider){
			for(Agent a:agList){
				a.setCohesionWeight(cohesionSlider.getValue());
			}
		}	
		else if((JSlider)e.getSource()== alignmentSlider){
			for(Agent a:agList){
				a.setAlignmentWeight(alignmentSlider.getValue());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		
		JRadioButton RB[][] = {heatMap_radio, visualMode_radio};
		boolean flag = false;
		
		for(int i = 0; i < RB.length; i++){
			for(int j=0; j<RB[i].length; j++){
				
				/******************
				 * heatMap_radio		
				 ******************/
				if(i == 0) {		
					if (e.getSource() == RB[i][j]) {
						flag = true;
						GridManager.heatMapType = j;
						break;
					}
					continue;
				}
				
				
				
				/******************
				 * virtual_radio		
				 ******************/
				if(i==1) {			//speed_radio
					if (e.getSource() == RB[i][j]) {
						
						flag = true;
						
						switch(j){
							case AgentManager.NORMAL_MODE:
								visualMode = AgentManager.NORMAL_MODE;
								break;
								
							case AgentManager.INFO_MODE:
								visualMode = AgentManager.INFO_MODE;
								break;
								
							case AgentManager.GROUP_MODE:
								visualMode = AgentManager.GROUP_MODE;
								break;
								
							case AgentManager.STATE_MODE:
								visualMode = AgentManager.STATE_MODE;
								break;
						}
						
						agentManager.setVisualMode(visualMode);
						
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
  	
  	public void itemStateChanged(ItemEvent e) {
  		if (e.getSource() == CommunStrategyCBox) {
//  			if (e.getStateChange() == ItemEvent.SELECTED) {
//  				String s = (String) e.getItem();
//  				if (s.equals(strategy[0])) {
//  					engine.getAgentManager().setCommunicationStrategy(new GateKeeperStrategy());
//  				} else if (s.equals(strategy[1])) {
//  					engine.getAgentManager().setCommunicationStrategy(new RationalStrategy());
//  				} else if (s.equals(strategy[2])) {
//  					engine.getAgentManager().setCommunicationStrategy(new DecreasingStrategy());
//  				} else if (s.equals(strategy[3])) {
//  					engine.getAgentManager().setCommunicationStrategy(new ImitateStrategy());
//  				}
//  			}	
  		}else if(e.getSource() == drawGridCBOX){
			if (drawGridCBOX.isSelected())
				GridManager.isDrawGrid = true;
			else{			
				GridManager.isDrawGrid = false;
			}
		}else if(e.getSource() == showTextCBOX){
			if (showTextCBOX.isSelected())
				MyCanvas.isShowText = true;
			else{			
				MyCanvas.isShowText = false;
			}
		}
  	}
  			
}
