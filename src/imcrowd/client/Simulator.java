package imcrowd.client;

import imcrowd.engine.Engine;

import imcrowd.osgiBundle.Sim2Interface;
import imcrowd.ui.DockingWindows;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Simulator implements Runnable, Sim2Interface{
	Engine engine;
	Thread newThread; 
	
	public Simulator(String directory){
		engine = Engine.getInstance();

		start();
		
		SwingUtilities.invokeLater(new Runnable() {
			   public void run() {
			     DockingWindows.getInstance();
			   }
		});    
		
		if(directory != null){
			new AutoBot(directory);
		}
	}

	public void run(){
		while(newThread!=null){
			engine.update();
		}
	}	
	
	public void start(){
/*		
		for(int i=0; i<1;i++){
			Random rand = new Random(System.currentTimeMillis());	
			Point2D position = new Point2D.Float(Math.abs(rand.nextInt()%473), Math.abs(rand.nextInt()%417));
			
			SpecificAgent ag = new SpecificAgent((float)position.getX(), (float)position.getY(), Math.abs(rand.nextInt()%360),1 ,engine);
			engine.getAgentManager().addAgent(ag);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
*/
		newThread  = new Thread(this);
		newThread.start();
	}

	//for IMNet 
	public String exec(String[] args)
	{     
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e1){
			System.out.println("Look and Feel Exception from the main function of the Simulator.java yo~");
			System.exit(0);
		}
		
		if(args.length != 0){
			new Simulator(args[0]);
		}else{
			new Simulator(null);
		}
		
		return null; //return to IMNodeFactory
	}	
	
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e1){
			System.out.println("Look and Feel Exception from the main function of the Simulator.java yo~");
			System.exit(0);
		}
		if(args.length != 0){
			new Simulator(args[0]);
		}else{
			new Simulator(null);
		}
	}

}
