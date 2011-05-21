package imcrowd.io.textIO;

import imcrowd.engine.Engine;
import imcrowd.io.FileIcon;
import imcrowd.io.PropertiesFileFilter;
import imcrowd.manager.AgentManager;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;
import imcrowd.ui.AgentTab;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;


public class StatisticIOHandler implements Colleague{
	private static StatisticIOHandler singleton;
	final private String defaultDir = System.getProperty("user.dir")+ File.separator + "DataSet"; 
	
	private Mediator mediator;
	boolean fileSelected = false;
	int totalFrame = 0;
	int frame = 1;
	AgentManager agentManager;
	Engine engine;
	JFileChooser fileChooser;
	File sateInfoFile;
	int currentPaintedFrame;
	
	private StatisticIOHandler(){
		File file = new File(defaultDir);
		if(!file.exists()){
			file.mkdir();
		}
		
		fileChooser = new JFileChooser(defaultDir);  
		fileChooser.setFileView(new FileIcon());										//display the icon for .plt extension
	}
	
	public static StatisticIOHandler getInstance(){
		if(singleton == null){
			singleton = new StatisticIOHandler();
		}
		return singleton;
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		agentManager = engine.getAgentManager();
	}
	
	public void setManagerMethod() {}
	
	
	public void reset(){
		fileSelected = false;
	}
	
	public void setTotalFrame(int t) {
		totalFrame = t;
	}
	
	public void setCurrentPantedFrame(int cur){
		currentPaintedFrame = cur;
	}
	
	public void record() throws IOException{
		if(!fileSelected) {
			File tmpfile;
			fileChooser.setDialogTitle("Save File");
			fileChooser.addChoosableFileFilter(new PropertiesFileFilter("plt")); 
			
			int state_of_fileChooser = fileChooser.showSaveDialog(null);
			
			if(state_of_fileChooser != JFileChooser.APPROVE_OPTION){
				engine.pause();
				return;
			}	
			fileSelected = true;
			tmpfile = fileChooser.getSelectedFile(); 								
			
			int index = tmpfile.getName().lastIndexOf(".");
			
			/*****************************************************************************
			 * ".plt" would be the default extension if user wouldn't input any extension. 
			 *****************************************************************************/
			if(index == -1){
				sateInfoFile = new File(tmpfile.getPath() + ".plt");
			}
			else{
				sateInfoFile = tmpfile; 
			}
			
			PrintWriter out1 =
		    	new PrintWriter(
		    		new BufferedWriter(
		    				new FileWriter(sateInfoFile,true)),true);
					
			
			out1.println("# 1.Frame 2.Clean 3.Latent 4.Engaged 5.Disenchanted");
			out1.println("# 6.MIN 7.Q1 8.Q2 9.Q3 10.MAX");
			out1.println("# 11.Leaver 12.Victims 13.Participant 14.D_Party 15.S_party 16.Superiority 17.Inferiority 18.Inferiority_nearby_Police");
			
			String fileName = sateInfoFile.getPath().replace("\\", "/");
			
			int cnt = 1;
			int yrange = ((agentManager.getNumberOfNormalAgent()+20)/10) * 10;
		
			out1.print("set term windows; set grid; set yrange [0:"+yrange+"]; set xlabel \"Simulation time steps\"; set ylabel \"Number of agents\";");
			out1.print("plot ");
			out1.print("\"" + fileName +"\"" + " using 1:"+ ++cnt + " title" + "\"Clean State\" with line, ");
			out1.print("\"" + fileName +"\"" + " using 1:"+ ++cnt + " title" + "\"Latent State\" with line, ");
			out1.print("\"" + fileName +"\"" + " using 1:"+ ++cnt + " title" + "\"Engaged State\" with line, ");
			out1.print("\"" + fileName +"\"" + " using 1:"+ ++cnt + " title" + "\"Disenchanted State\" with line; ");
			out1.print("set output " +"\"" + fileName.replace(".plt", ".png")+ "\"" + "; set term png truecolor font \"arial\" 14; set style fill transparent solid 0.4 noborder; replot; ");
			
//			out1.print("plot ");
//			out1.print("\"" + fileName +"\"" + " using 1:6 title" +"\"Flight\" with line, ");
//			out1.print("\"" + fileName +"\"" + " using 1:7 title" +"\"Aggregate\" with line, ");
//			out1.print("\"" + fileName +"\"" + " using 1:8 title" +"\"Roar\" with line, ");
//			out1.print("\"" + fileName +"\"" + " using 1:9 title" +"\"Vandalism\" with line, ");
//			out1.print("\"" + fileName +"\"" + " using 1:10 title" +"\"Attack\" with line, ");
//			out1.print("set output " +"\"" + fileName.replace(".plt", "_riotInfo.png")+ "\"" + "; set term png; replot;");
			
			int partyInfo[] = agentManager.initialize_PartyInformation();
			for(int i=0; i<AgentTab.COLOR_STRING.length; i++){
				if(partyInfo[i] > 0){
					
					yrange = partyInfo[i] + 20;
					out1.print("set yrange [-30:"+yrange+"];");
					out1.print("plot ");
										
					/************************
					 * five number summary
					 ************************/
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"DOO_Min\" with line, ");
					
					++cnt;
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " notitle with line lc rgb \"#99CC66\", ");	//Q1
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + ":" + (cnt+1) + " title" + "\"DOO_Q1-Q2\" with filledcu lc rgb \"#009999\", ");
					
					
					++cnt;
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " notitle with line lc rgb \"#99CC66\", ");	//Q2
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + ":" + (cnt+1) + " title" + "\"DOO_Q2-Q3\" with filledcu lc rgb \"#99CC44\", ");
					
					++cnt;
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " notitle with line lc rgb \"#99CC66\", ");	//Q3
					
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"DOO_MAX\" with line, ");
					
					
					/*************************
					 * party info
					 *************************/
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Leaver\" with line, ");
					
					++cnt;
					int victimNo = cnt;
					
					++cnt;
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Participant\" with line lc rgb \"#996633\", ");
					
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Different Party\" with line, ");
					
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Same Party\" with line, ");
					
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Superiority\" with line lc rgb \"orange\", ");
					
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " notitle with line lc rgb \"#999933\", ");	
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Inferiority\" with line lc rgb \"red\", ");
					
					++cnt;
					//out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " notitle with line lc rgb \"#999933\", ");	
					out1.print("\"" + fileName +"\"" + " using 1:"+ cnt + " title" + "\"Inferiority nearby Police\" with line lc rgb \"magenta\", ");
					
					out1.print("\"" + fileName +"\"" + " using 1:"+ victimNo + " title" + "\"Victim\" with line lc rgb \"black\" lw 2; ");
					
					
					out1.print("set output " +"\"" + fileName.replace(".plt", "_" + AgentTab.COLOR_STRING[i] + ".png")+ "\"" + "; set term png truecolor; replot;");
				}	
			}
			
			
			out1.println();
			out1.close();
		}
		
		PrintWriter out1 =
	    	new PrintWriter(
	    		new BufferedWriter(
	    				new FileWriter(sateInfoFile,true)),true);
				
		
		if(frame > totalFrame){
			fileSelected = false;
			engine.pause();
			return;
		}
		
		//out1.println(frame++ + " " + agentManager.report_State(false) + agentManager.report_Riot(false));
		out1.println(frame++ + " " + agentManager.report_State(false) + agentManager.report_PartyInfo(false));
		out1.close();
	}			
	
}
