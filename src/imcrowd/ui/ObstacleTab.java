package imcrowd.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.manager.GridManager;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;



public class ObstacleTab extends JPanel implements ItemListener {
  	  private static ObstacleTab obstacleTab = new ObstacleTab();
  	  
	  //JCheckBox
	  JCheckBox displayContextCBox;
  	  
  	  final int NUM_BUTTON = 8; 
	  
	  //JToggleButton
	  JToggleButton[] tButtons;
	  JToggleButton lastSelectedButton;
	  
	  ImageIcon currentImg;					
	  ImageIcon[] pics;
	  
	  boolean isInteractive;
	  int interactiveId;
	  
	  
	  Obstacle targetObstacle;					

	  ImageLoader imageLoader = ImageLoader.getInstance();
	  
	  private ObstacleTab(){
		  	isInteractive = false;	
			currentImg = imageLoader.getGifImg(3);
						
			displayContextCBox = new JCheckBox("Display Context Value", false);
			displayContextCBox.addItemListener(this);
			
			
			
			tButtons = new JToggleButton[NUM_BUTTON];
			pics	= new ImageIcon[NUM_BUTTON];
			
			pics[0] = imageLoader.getGifImg(3);
			pics[1] = imageLoader.getPngImg(0);
			pics[2] = imageLoader.getGifImg(4);
			pics[3] = imageLoader.getGifImg(2);
			pics[4] = imageLoader.getPngImg(3);
			pics[5] = imageLoader.getPngImg(6);
			pics[6] = imageLoader.getPngImg(8);
			pics[7] = imageLoader.getPngImg(11);
			 
			for(int i=0; i < NUM_BUTTON; i++){
				if(i==0){
					tButtons[i] = new JToggleButton(pics[i], true);
					lastSelectedButton = tButtons[i];
				}else{
					tButtons[i] = new JToggleButton(pics[i], false);
				}
				
				tButtons[i].addItemListener(this);
			}
			
			createPanel();
	  }
	  

	  public static ObstacleTab getInstance(){
		  return obstacleTab;
	  }
	  
	  public boolean isInteractive() {
			return isInteractive;
	  }
	  
	  public int getInteractiveId(){
		  return interactiveId;
	  }
	  
	  public  void setTargetObstacle(Obstacle ob){
		  targetObstacle = ob;
	  }
	  
	  public ImageIcon getCurrentImg(){
		  return currentImg;
	  }
	  
	  
	  public void createPanel(){
		  JPanel innerPanel;
		  JLabel argName;
		  
		  this.setLayout(new BorderLayout());
		 
		  innerPanel = new JPanel();
		  innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		  innerPanel.setBorder(BorderFactory.createTitledBorder(
				  			   BorderFactory.createEtchedBorder(),"Control factor", TitledBorder.LEFT, TitledBorder.TOP));
		  
		  innerPanel.add(displayContextCBox);
		  

		  
		  this.add(innerPanel,BorderLayout.NORTH);
		  
		  innerPanel = new JPanel();
		  innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		  innerPanel.setBorder(BorderFactory.createTitledBorder(
				  			   BorderFactory.createEtchedBorder(),"Type", TitledBorder.LEFT, TitledBorder.TOP));
		  
		  String str = "null";
		  JPanel picsPanel;
		  for(int k=0; k < 3; k++){
			  /* Different Obstacle select */ 
			  picsPanel = new JPanel();
			  
			  switch(k){
			  	case 0:
			  		str = "Normal";
			  		for(int i=0; i<3; i++){
			  			picsPanel.add(tButtons[i]);  
			  		}
			  		break;
			  		
			  	case 1:
			  		str = "Interactive";
			  		for(int i=3; i<7; i++){
			  			picsPanel.add(tButtons[i]);
			  		}
			  		break;
			  		
			  	case 2:
			  		str = "Other";
			  		for(int i=7; i<8; i++){
			  			picsPanel.add(tButtons[i]);
			  		}
			  		break;
			  }
			  
			  picsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			  picsPanel.setBorder(BorderFactory.createTitledBorder(
					  	BorderFactory.createEtchedBorder(), str, TitledBorder.LEFT, TitledBorder.TOP));
			  
			  innerPanel.add(picsPanel,BorderLayout.CENTER);
		  }
		  this.add(innerPanel, BorderLayout.CENTER);
	  }
	  
	  public void itemStateChanged(ItemEvent e) {
		  
		  if(e.getSource() == displayContextCBox){
				if (displayContextCBox.isSelected())
					GridManager.isDrawContextValue = true;
				else{			
					GridManager.isDrawContextValue = false;
				}
		  }
		  else{
			  for(int i=0; i<NUM_BUTTON; i++){
				  if(e.getSource() == tButtons[i] && tButtons[i].isSelected()){	
					  	
					  	if(i >= 3 && i <= 6){
					  		isInteractive = true;
					  		interactiveId = i - 3;				  		
					  	}
					  	else{
					  		isInteractive = false;
					  	}
					  	
						currentImg = pics[i];
						lastSelectedButton.setSelected(false);
						lastSelectedButton = tButtons[i];
						break;
				  }
			  }
		  }
	  }
	  
}
