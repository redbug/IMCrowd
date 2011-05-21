package imcrowd.ui;

import imcrowd.io.imageIO.ImageLoader;
import imcrowd.manager.GoalManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;


public class GoalTab extends JPanel implements ItemListener {
	  private static GoalTab goalTab = new GoalTab();
	  
	  //JCheckBox
	  JCheckBox displayGoalCBox;
	  
	  //JToggleButton
	  JToggleButton flagTButton;
	  JToggleButton totemPoleTButton;
	  JToggleButton lamppostTButton;
	  ImageIcon pic1;
	  ImageIcon pic2;
	  ImageIcon pic3;
	  
	  ImageIcon currentImg;					
	  
	  ImageLoader imageLoader = ImageLoader.getInstance();
	  
	  private GoalTab(){
			currentImg = imageLoader.getGifImg(0);
		  
			//JCheckBox
			displayGoalCBox = new JCheckBox("Display Goal",true);
			displayGoalCBox.addItemListener(this);
			
			pic1 = imageLoader.getGifImg(0);
			flagTButton = new JToggleButton(pic1,true);
			flagTButton.addItemListener(this);
			
			pic2 = imageLoader.getPngImg(11);
			totemPoleTButton = new JToggleButton(pic2);
			totemPoleTButton.addItemListener(this);
			
			pic3 = imageLoader.getGifImg(1);
			lamppostTButton = new JToggleButton(pic3);
			lamppostTButton.addItemListener(this);
			
			createPanel();
	  }
	  
	  public static GoalTab getInstance(){
		  return goalTab;
	  }
	  	  
	  public ImageIcon getCurrentImg(){
		  return currentImg;
	  }
	  
	  public void createPanel(){
		  JPanel innerPanel;

		  this.setLayout(new BorderLayout());

		  /* goal-displayed checking box  */
		  innerPanel = new JPanel();
		  innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		  innerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Display", TitledBorder.LEFT,	TitledBorder.TOP));
		  
		  innerPanel.add(displayGoalCBox);
		  this.add(innerPanel, BorderLayout.NORTH);

		  /* goal icon buttons */
		  innerPanel = new JPanel();
		  innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		  innerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Type", TitledBorder.LEFT,TitledBorder.TOP));

		  innerPanel.add(flagTButton);
		  innerPanel.add(totemPoleTButton);
		  innerPanel.add(lamppostTButton);
		  this.add(innerPanel, BorderLayout.CENTER);
	  }
	  
	  public void itemStateChanged(ItemEvent e) {
		     //goal-displayed checking box
			 if(e.getSource() == displayGoalCBox){
				if (displayGoalCBox.isSelected())
					GoalManager.isDisplayGoal = true;
				else{			
					GoalManager.isDisplayGoal = false;
				}
			//1st goal button
			}else if(e.getSource() == flagTButton && flagTButton.isSelected()){	
				totemPoleTButton.setSelected(false);
				lamppostTButton.setSelected(false);
				currentImg = pic1;
			//2nd goal button	
			}else if(e.getSource() == totemPoleTButton && totemPoleTButton.isSelected()){
				lamppostTButton.setSelected(false);
				flagTButton.setSelected(false);
				currentImg = pic2;
			//3rd goal button	
			}else if(e.getSource() == lamppostTButton && lamppostTButton.isSelected()){
				flagTButton.setSelected(false);
				totemPoleTButton.setSelected(false);
				currentImg = pic3;
			}
	  }
	  
}
