package imcrowd.ui;

import imcrowd.engine.Engine;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.io.textIO.ConfigurationIOHandler;
import imcrowd.io.textIO.StatisticIOHandler;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MainView extends JPanel implements ActionListener,ItemListener,ChangeListener, Colleague {
	private static MainView mainView = new MainView();
	JPanel canvas;								
//	JPanel firstPanel;							//first line of bottom
//	JPanel secondPanel;							//second line of bottom
	JPanel bottomOutter;
	JPanel bottomInner;
	JPanel contorlPanel;								
	
	JToggleButton playButton;					
	JToggleButton pauseButton;					
	JToggleButton stopButton;					
	JToggleButton recordButton;					
	
	JButton writeButton;						
	JButton readButton;							
	JButton imnetButton;									
		
	JTextField runStepTField;
	JSlider speedSlider;						// the speed of repainting the canvas
	
	Engine engine;
	Mediator mediator;
	StatisticIOHandler recordIO;
	ImageLoader imageLoader = ImageLoader.getInstance();
	
	private MainView(){
		
		contorlPanel = new JPanel(); 
		
		bottomOutter = new JPanel();
		bottomOutter.setLayout(new BoxLayout(bottomOutter,BoxLayout.Y_AXIS));

		
		playButton = new JToggleButton(imageLoader.getGifImg(7));
		pauseButton = new JToggleButton(imageLoader.getGifImg(8), true);
		stopButton = new JToggleButton(imageLoader.getGifImg(9));
		recordButton = new JToggleButton(imageLoader.getPngImg(12));
		
		readButton = new JButton(imageLoader.getGifImg(10));
		writeButton = new JButton(imageLoader.getGifImg(11));
		imnetButton = new JButton(imageLoader.getPngImg(13));
		
		runStepTField = new JTextField(4);
		
		playButton.addItemListener(this);
		pauseButton.addItemListener(this);
		stopButton.addItemListener(this);
		recordButton.addItemListener(this);
		readButton.addActionListener(this);
		writeButton.addActionListener(this);
		imnetButton.addActionListener(this);
		
		//speedSlider bar
		speedSlider = new JSlider(JSlider.HORIZONTAL,1,10,1);
		speedSlider.addChangeListener(this);	
	    speedSlider.setPaintTicks(true);
	    speedSlider.setMajorTickSpacing(1);
	    speedSlider.setPaintLabels( true );
		
   
	    bottomInner = new JPanel();
	    bottomInner.setLayout(new FlowLayout(FlowLayout.LEFT));
	    bottomInner.add(new JLabel("Run Steps:"));
	    bottomInner.add(runStepTField);
	    bottomInner.add(recordButton);
	    bottomInner.add(playButton);
	    bottomInner.add(pauseButton);
	    bottomInner.add(stopButton);
	    bottomInner.add(speedSlider);
	    bottomInner.add(readButton);
	    bottomInner.add(writeButton);
	    bottomInner.add(imnetButton);
	    
	    bottomOutter.add(bottomInner);
	    
	    bottomInner = new JPanel();
	    bottomInner.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
	    bottomOutter.add(bottomInner);
	    
	    contorlPanel.add(bottomOutter); 
	   
	}
	
	public static MainView getInstance(){
		return mainView;
	}	
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		canvas = engine.getCanvas();
		recordIO = engine.getRecordIOHandler();
		canvas.setBorder(BorderFactory.createEtchedBorder(SoftBevelBorder.RAISED));
		
		this.add(canvas, BorderLayout.CENTER);			
		this.add(contorlPanel, BorderLayout.SOUTH);
		 
		 
	}
	
	public void setManagerMethod() {}
	
	
	public void setRunStepFiledValue(String steps){
		runStepTField.setText(steps);
	}
	
    public int getRunStepTFieldValue() {
    	String text = runStepTField.getText();
    	char charArray[] = text.toCharArray();
    	
    	if(runStepTField.getText().equals("")) {
    		return -1;
    	}else {
	    	for(int i=0; i<charArray.length; i++) {
	    		if(!Character.isDigit(charArray[i])) {
	    			runStepTField.setText("");
	    			return 0;
	    		}
	    	}
    		return Integer.valueOf(runStepTField.getText());
    	}		
    }
	
    
    public void play(){
    	playButton.doClick();
    }
      
    public void pause(){
    	pauseButton.doClick();
    }
    
    public void stop(){
    	stopButton.doClick();
    }
    
	public JSlider getSpeedSlider(){
		return speedSlider;
	}
	
	public void actionPerformed(ActionEvent e){
		/* JButton */
		playButton.setSelected(false);				
		ConfigurationIOHandler stateIO = ConfigurationIOHandler.getInstance();
		
		if(e.getSource()== readButton){
			stateIO.readFile();
		}
		else if(e.getSource() == writeButton){
			stateIO.writeFile();
		}
/*		for IMNET Bundle
  		else if(e.getSource() == imnetButton){
			engine.loginIMNET();
			playButton.doClick();
		}*/
		
		pauseButton.setSelected(true);
		canvas.repaint();
		canvas.requestFocus();									
	}	

	public void itemStateChanged(ItemEvent e) {
		Engine engine = Engine.getInstance();
		/* JToggleButton */
		if(e.getStateChange() == ItemEvent.SELECTED){
			//playButton
			if (e.getSource() == playButton) {			
				pauseButton.setSelected(false);
				stopButton.setSelected(false);
				recordButton.setSelected(false);
				
				engine.setRunStep(getRunStepTFieldValue());
				engine.setPlay(true);
				
			/* pauseButton */	
			} else if (e.getSource() == pauseButton) {
				playButton.setSelected(false);
				stopButton.setSelected(false);
				recordButton.setSelected(false);
				
				engine.setPlay(false);
				
			/* stopButton */	
			} else if (e.getSource() == stopButton) {
				playButton.setSelected(false);
				pauseButton.setSelected(false);
				recordButton.setSelected(false);
				
				engine.stop();
				canvas.repaint();
			/* recordButton */
			} else if(e.getSource() == recordButton) {
				pauseButton.setSelected(false);
				stopButton.setSelected(false);
				playButton.setSelected(false);
				
				engine.setRunStep(getRunStepTFieldValue());
				
				StatisticIOHandler.getInstance().reset();
				engine.setRecord(true);
			}	
		}
		
		canvas.requestFocus();											
	}
	
	public void stateChanged(ChangeEvent e){
	   if(e.getSource()== speedSlider){
			Engine.getInstance().setSleepTime(10/speedSlider.getValue());	  
	   }
	   canvas.requestFocus();
	}
	
}
