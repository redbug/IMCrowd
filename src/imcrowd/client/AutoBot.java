package imcrowd.client;

import imcrowd.engine.Engine;
import imcrowd.io.textIO.ConfigurationIOHandler;
import imcrowd.manager.AgentManager;
import imcrowd.manager.GridManager;
import imcrowd.ui.GlobalTab;
import imcrowd.ui.MainView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AutoBot{
	
	String directory;
	String resultFile = System.getProperty("user.dir") + File.separator + "result.csv";
	
	Engine engine = Engine.getInstance();
	MainView mainView = MainView.getInstance();
	GlobalTab globalTab = GlobalTab.getInstance();
	
	public AutoBot(String directory){
		this.directory = directory;
		
		/*************** 
		 * Setting 
		 ***************/
		mainView.setRunStepFiledValue("30000");
		mainView.getSpeedSlider().setValue(10);
		globalTab.changePoliceStrategy(GridManager.HEATMAP_DENSITY);
		globalTab.changeVisualMode(AgentManager.STATE_MODE);
		
		traverse(directory);
	}
	
	
	public void write(File file){
		
		PrintWriter out1;
		
		try {
			out1 = new PrintWriter(
	    		new BufferedWriter(
	    				new FileWriter(resultFile, true)),true);
		
			if(file.isDirectory()){
				out1.println(file.getName());
				return;
			}
			
			ConfigurationIOHandler stateIO = ConfigurationIOHandler.getInstance();
			stateIO.readFile(file);
			
			mainView.play();
			
			while(engine.isPlay()){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
			
			
						
			int v[] = AgentManager.partyInfo[AgentManager.VICTIM_INFO];
			int p[] = AgentManager.partyInfo[AgentManager.PARTICIPANT_INFO];
			int total = 0;
			
			for(int i=0; i< v.length; i++){	
				if(p[i] != -1){
					total += v[i];
					out1.print(v[i] + ", ");
				}	
			}
			
			out1.print(total+ ",");
			out1.println();
			out1.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	//collecting file list by using DFS
	public void traverse(String str){
		File file = new File(str);
		File[] files = file.listFiles();
		
		for(int i = 0; i < files.length; i++) {     		
			if(files[i].isDirectory()) { 
				if(!files[i].getName().equals(".svn")){
					write(files[i]);
					traverse(files[i].getPath());
				}	
			}
			else {
				write(files[i]);
				//fList.add(files[i]);
			}
	
		} 
		
	}

}
