package imcrowd.ui;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import imcrowd.basicObject.WeightParameters;
import imcrowd.engine.Engine;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ExperimentParameterTab extends JScrollPane implements ActionListener, ItemListener, ChangeListener{
	private static ExperimentParameterTab singleton = new ExperimentParameterTab();
	
	/********************************
	 * constants for spinner
	 ********************************/
	public static final int GROUP_MEAN 					= 0,			
							GROUP_SD   					= 1,		
							THRESHOLD_MEAN				= 2,				
							THRESHOLD_SD				= 3,
							RATIONALITY_MEAN			= 4,
							RATIONALITY_SD				= 5,
							SOCIAL_MARVEN_MEAN			= 6,
							SOCIAL_MARVEN_SD			= 7,
							SOCIAL_CONNECTOR_MEAN		= 8,
							SOCIAL_CONNECTOR_SD			= 9,
							SOCIAL_SALESMAN_MEAN		= 10,
							SOCIAL_SALESMAN_SD			= 11;
	
	public static final int NONE		= 0,
							RIOT		= 1,
							GATHER		= 2,
							PANIC		= 3,
							POLICE		= 4;

	
	final int NUM_SPINNER = 12;
	final int NUM_TBUTTON = 5;
	
	int selectedEvent;
	
	
	//JSpinner
	JSpinner spinners[];

	//JSlider
	JSlider iSeparationSlider;
	JSlider iCohesionSlider;
	JSlider iAlignmentSlider;
	
	//JLabel
	JLabel  iSFLabel;
	JLabel  iCFLabel;
	JLabel  iAFLabel;
	
	//JTextField
	JTextField betaTField;
	JTextField alphaTField;
	
	//JButton
	JButton resetThresholdButton;
	JButton resetRationalityButton;
	JButton resetGroupButton;
	
	//JToggleButton
	ButtonGroup tButtonGroup;
	JToggleButton tButtons[];
	
	//JCheckBox
	JCheckBox renderCBox;
	
	
	private ExperimentParameterTab(){	
		spinners = new JSpinner[NUM_SPINNER];
		SpinnerNumberModel model;
		
		model = new SpinnerNumberModel(3, 0, 9, 1);		
		spinners[GROUP_MEAN] 		= new JSpinner(model);
		
		model = new SpinnerNumberModel(1, 0, 5, 1);	
		spinners[GROUP_SD] 		 	= new JSpinner(model);
		
		model = new SpinnerNumberModel(10, 0, 25, 1);	
		spinners[THRESHOLD_MEAN] 	= new JSpinner(model);
		
		model = new SpinnerNumberModel(4, 0, 25, 1);
		spinners[THRESHOLD_SD]		= new JSpinner(model);
		
		
		model = new SpinnerNumberModel(6, 0, 10, 1);	
		spinners[RATIONALITY_MEAN] 	= new JSpinner(model);
		
		model = new SpinnerNumberModel(2, 0, 10, 1);
		spinners[RATIONALITY_SD]	= new JSpinner(model);
		
		
		model = new SpinnerNumberModel(5, 0, 10, 1);
		spinners[SOCIAL_MARVEN_MEAN]	= new JSpinner(model); 
		
		model = new SpinnerNumberModel(1, 0, 10, 1);
		spinners[SOCIAL_MARVEN_SD]		= new JSpinner(model);
		
		model = new SpinnerNumberModel(5, 0, 10, 1);
		spinners[SOCIAL_CONNECTOR_MEAN]	= new JSpinner(model);
		
		model = new SpinnerNumberModel(1, 0, 10, 1);
		spinners[SOCIAL_CONNECTOR_SD]	= new JSpinner(model);
		
		model = new SpinnerNumberModel(5, 0, 10, 1);
		spinners[SOCIAL_SALESMAN_MEAN]	= new JSpinner(model);
		
		model = new SpinnerNumberModel(1, 0, 10, 1);
		spinners[SOCIAL_SALESMAN_SD]	= new JSpinner(model);
		
		
		for(int i=0; i<spinners.length; i++){
			spinners[i].addChangeListener(this);
			spinners[i].setPreferredSize(new Dimension(15,20));
		}
				
		tButtonGroup = new ButtonGroup();
		
		tButtons = new JToggleButton[NUM_TBUTTON];
		
		for(int i=0; i < tButtons.length; i++){
			switch(i){
				case RIOT:
					tButtons[i] = new JToggleButton("Riot");
					break;
				case GATHER:
					tButtons[i] = new JToggleButton("Gather");
					break;
				case PANIC:
					tButtons[i] = new JToggleButton("Panic");
					break;
				case POLICE:
					tButtons[i] = new JToggleButton("Police");
					break;
				default:
					tButtons[i] = new JToggleButton("None");
			}
			tButtonGroup.add(tButtons[i]);
			tButtons[i].addItemListener(this);
		}
		
		selectedEvent = NONE;
		tButtons[NONE].setSelected(true);
		
		
		resetThresholdButton = new JButton("Reset");
		resetThresholdButton.addActionListener(this);
		
		resetRationalityButton = new JButton("Reset");
		resetRationalityButton.addActionListener(this);
		
		resetGroupButton = new JButton("Reset");
		resetGroupButton.addActionListener(this);
		
		
		
	    iSeparationSlider 	= new JSlider(JSlider.HORIZONTAL, -WeightParameters.Max_wSeparation,	WeightParameters.Max_wSeparation,	-5);
	    iCohesionSlider 	= new JSlider(JSlider.HORIZONTAL, -WeightParameters.Max_wCohesion, 		WeightParameters.Max_wCohesion,		-4);
	    iAlignmentSlider	= new JSlider(JSlider.HORIZONTAL, -WeightParameters.Max_wAlignment,		WeightParameters.Max_wAlignment,	-3);
	    iSeparationSlider.addChangeListener(this);
	    iCohesionSlider.addChangeListener(this);
	    iAlignmentSlider.addChangeListener(this);
		
		
		betaTField = new JTextField("10000",5);
		alphaTField = new JTextField("1000",5);
	    
		
		renderCBox = new JCheckBox("Render",true);
		renderCBox.addItemListener(this);
		
		createPanel();
	}
	
	public static ExperimentParameterTab getInstance(){
		return singleton;
	}
	
	
	public int getSelectedEvent(){
		return selectedEvent;
	}	
	
	//Beta
	public int getBeta() {
		return Integer.valueOf(betaTField.getText());
	}
	 
	public void setBeta(String str) {
		betaTField.setText(str);  
	}
	  
	//Alpha
	public int getAlpha() {
		return Integer.valueOf(alphaTField.getText());
	}
	  
	public void setAlpha(String str) {
		alphaTField.setText(str);  
	}
	
	
	private JPanel createNormalDistributionPanel(int mean, int sd, String name, boolean isMiddle){
		JPanel innerPanel = new JPanel();
		JLabel argName;

		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		if(isMiddle){
			innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(), name, TitledBorder.CENTER, TitledBorder.TOP));
		}else{
			innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(), name, TitledBorder.LEFT, TitledBorder.TOP));
		}
		

		
		int strutWidth = 50;
		
		if(name.equals("Threshold")){
			innerPanel.add(resetThresholdButton);
			strutWidth = 20;
		}
		else if(name.equals("Rationality")){
			innerPanel.add(resetRationalityButton);
			strutWidth = 20;
		}
		else if(name.equals("Crowd Structure")){
			innerPanel.add(resetGroupButton);
			strutWidth = 20;
		}
		
		innerPanel.add(Box.createHorizontalStrut(strutWidth));
		
		
		
		 /* Mean */
		argName = new JLabel("Mean");	
		innerPanel.add(argName);
		
		innerPanel.add(Box.createHorizontalStrut(10));
		innerPanel.add(spinners[mean]);
			
		
					
		/* Stander Deviation */
		innerPanel.add(Box.createHorizontalStrut(80));
		
		argName = new JLabel("SD");	
		innerPanel.add(argName);
		
		innerPanel.add(Box.createHorizontalStrut(10));
		innerPanel.add(spinners[sd]);
		
		innerPanel.add(Box.createHorizontalStrut(strutWidth));
		
		return innerPanel;
	}
	
	
	/*****************************************
	 * Flocking Weights Panel
	 *****************************************/
	private JPanel create_iFlockingPanel(){

		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel argName;
		
		
		innerPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(), "Flocking Weights", TitledBorder.LEFT, TitledBorder.TOP));

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
		
			/* cohesion weight*/
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
		
		return innerPanel;
	}
	
	
	/*****************************************
	 * Collective Mind Panel
	 *****************************************/
	private JPanel create_GroupMindPanel(){
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.X_AXIS));
		innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(), "Group Mind", TitledBorder.LEFT, TitledBorder.TOP));
		
		for(int i=0; i < tButtons.length; i++){
			innerPanel.add(tButtons[i]);
		}
		
		return innerPanel;
	}
	
	/*****************************************
	 * Suggestive Message Panel
	 *****************************************/
	private JPanel create_ParameterPanel(){    
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.Y_AXIS));
		innerPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(), "Parameters", TitledBorder.LEFT, TitledBorder.TOP));
		
		JPanel tmpPanel;
		JLabel argName;
		
		
		/***********************************************
		 * Alpha & Beta
		 ***********************************************/
		tmpPanel = new JPanel();
		tmpPanel.setLayout(new BoxLayout(tmpPanel,BoxLayout.X_AXIS));
				
		argName = new JLabel("Alpha");
		tmpPanel.add(argName);
		tmpPanel.add(Box.createHorizontalStrut(5));
		tmpPanel.add(alphaTField);
		
		tmpPanel.add(Box.createHorizontalStrut(20));
		
		argName = new JLabel("Beta");
		tmpPanel.add(argName);
		tmpPanel.add(Box.createHorizontalStrut(5));
		tmpPanel.add(betaTField);
		
		innerPanel.add(tmpPanel);
		innerPanel.add(Box.createVerticalStrut(10));	
		
		
		/***********************************************
		 * Threshold
		 ***********************************************/		
		tmpPanel = createNormalDistributionPanel(THRESHOLD_MEAN, THRESHOLD_SD, "Threshold", false);
		innerPanel.add(tmpPanel);	
		
		return innerPanel;
	}
	
	public void createPanel(){
		JPanel outterPanel = new JPanel();
		outterPanel.setLayout(new BoxLayout(outterPanel,BoxLayout.Y_AXIS));
		outterPanel.add(Box.createVerticalStrut(10));
		
		outterPanel.add(renderCBox);
		 
		JPanel innerPanel;
		
		/*******************************************
		 * Event Panel
		 *******************************************/
		JPanel eventPanel = new JPanel();
		eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
		
		eventPanel.setBorder(BorderFactory.createTitledBorder(
					  BorderFactory.createEtchedBorder(), "Event", TitledBorder.CENTER, TitledBorder.TOP));
		

		innerPanel = create_GroupMindPanel();
		eventPanel.add(innerPanel);
		eventPanel.add(Box.createVerticalStrut(10));
		
		innerPanel = create_ParameterPanel();
		eventPanel.add(innerPanel);
		eventPanel.add(Box.createVerticalStrut(10));

		innerPanel = create_iFlockingPanel();
		eventPanel.add(innerPanel);
		

		
		outterPanel.add(eventPanel);
		outterPanel.add(Box.createVerticalStrut(10));
		

		
		
		
		
		
		innerPanel = createNormalDistributionPanel(GROUP_MEAN, GROUP_SD, "Crowd Structure", true);
		outterPanel.add(innerPanel);
		outterPanel.add(Box.createVerticalStrut(10));

		innerPanel = createNormalDistributionPanel(RATIONALITY_MEAN, RATIONALITY_SD, "Rationality", true);
		outterPanel.add(innerPanel);
		outterPanel.add(Box.createVerticalStrut(10));
		
		
		JPanel socialPanel = new JPanel();
		socialPanel.setLayout(new BoxLayout(socialPanel,BoxLayout.Y_AXIS));
		socialPanel.setBorder(BorderFactory.createTitledBorder(
				  BorderFactory.createEtchedBorder(), "Social Skill", TitledBorder.CENTER, TitledBorder.TOP));
		
		innerPanel = createNormalDistributionPanel(SOCIAL_MARVEN_MEAN, SOCIAL_MARVEN_SD, "Marven", false);
		socialPanel.add(innerPanel);
		
		innerPanel = createNormalDistributionPanel(SOCIAL_CONNECTOR_MEAN, SOCIAL_CONNECTOR_SD, "Connector", false);
		socialPanel.add(innerPanel);
		
		innerPanel = createNormalDistributionPanel(SOCIAL_SALESMAN_MEAN, SOCIAL_SALESMAN_SD, "Salesman", false);
		socialPanel.add(innerPanel);
	
		outterPanel.add(socialPanel);
		
		
		
		this.getViewport().add(outterPanel);
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
	

	
	public int getCohesionI() {
	  return iCohesionSlider.getValue();
	}
  
	public int getSpeparationI() {
	  return iSeparationSlider.getValue();
	}
  
	public int getAlignmentI() {
	  return iAlignmentSlider.getValue();
	}
	
	
	public int getParameterValue(int index){
		return (Integer)(spinners[index].getValue());
	}
	

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == renderCBox){
			if (renderCBox.isSelected())
				Engine.getInstance().getCanvas().setRender(true);
			else{
				Engine.getInstance().getCanvas().setRender(false);
			}
		}	
		else if(e.getStateChange() == ItemEvent.SELECTED){
			for(int i=0; i < tButtons.length; i++){
				if(e.getSource() == tButtons[i]){			 	
						 selectedEvent = i;
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == resetThresholdButton){
			Engine.getInstance().getAgentManager().resetThreshold();
		}
		else if (e.getSource() == resetRationalityButton){
			Engine.getInstance().getAgentManager().resetRationality();
		}
		else if (e.getSource() == resetGroupButton){
			System.out.println("3");	
		}
	}	
	
	
	public void resetButtons(){
		tButtons[NONE].setSelected(true);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
