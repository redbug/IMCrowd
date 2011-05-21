package imcrowd.io.report;

import imcrowd.engine.Engine;
import imcrowd.manager.AgentManager;
import imcrowd.ui.AgentTab;

public class ConfigurationReport {
	int alpha = -1;
	int beta = -1;
	
	/*****************************
	 * Crowd Structure
	 *****************************/
	int numAgent;
	int numGroup;
	double mean_group;
	double sd_group;
	
	/*****************************
	 * Individual Attributes
	 *****************************/
	double mean_rationality;
	double sd_rationality;		
	
	double mean_threshold;	
	double sd_threshold;
	
	/****************************
	 * party info
	 *****************************/
	int[] partyNumArray = new int[AgentTab.COLOR_STRING.length];
	
	public ConfigurationReport(int alpha, int beta){
		this.alpha = alpha;
		this.beta = beta;
		
		AgentManager agentManager = Engine.getInstance().getAgentManager(); 
		partyNumArray = agentManager.initialize_PartyInformation();
		
		double[] result = agentManager.calculate_CrowdStructure();
		numAgent 	= (int)result[0];
		numGroup 	= (int)result[1];
		mean_group 	= result[2];
		sd_group 	= result[3];
		
		result = agentManager.calculate_Attribute();
		mean_rationality = result[0];
		sd_rationality = result[1];
		mean_threshold = result[2];
		sd_threshold = result[3];
		
	}
	
	public String report_AlphaBeta(){
		StringBuffer report = new StringBuffer();
		report.append(String.format("%s \t%d\n", "Alpha:", alpha));
		report.append(String.format("%s \t%d\n", "Beta:", beta));	
		
		return report.toString();
	}
	
	public String report_Attribute(){
		StringBuffer report = new StringBuffer();
		report.append("Rationality\n");
		report.append(String.format("%s \t%f\n", "Mean:", mean_rationality));
		report.append(String.format("%s \t%f\n", "SD:", sd_rationality));
		
		report.append("\n");
		
		report.append("Threshold\n");
		report.append(String.format("%s \t%f\n", "Mean:", mean_threshold)); 
		report.append(String.format("%s \t%f\n", "SD:", sd_threshold)); 
		
		return report.toString();
	}
	
	public String report_CrowdStructure(){
		StringBuffer report = new StringBuffer();
		
		report.append(String.format("%s \t%d\n", "Agents:", numAgent));
		report.append(String.format("%s \t%d\n", "Groups:", numGroup));
		
		report.append("\n");
		
		report.append("Group Size\n");
		report.append(String.format("%s \t%f\n", "Mean:", mean_group)); 
		report.append(String.format("%s \t%f\n", "SD:", sd_group)); 
	
		return report.toString();
	}
		
	
	public String report_PartyInformation(){
		
		StringBuffer report = new StringBuffer();
		
		for(int i=0; i<partyNumArray.length; i++){
			if(partyNumArray[i] > 0){
				report.append(String.format("%s \t%d\n", AgentTab.COLOR_STRING[i] + ": ",  partyNumArray[i]));
			}
		}
		
		return report.toString();
	}
	
}
