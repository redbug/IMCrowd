package imcrowd.io.report;


public class StatisticReport {
	public static final int NUM_STATE 	= 4;
	public static final int NUM_RIOT	= 5;
	
	int states[] = new int[NUM_STATE];
	int riots[] = new int[NUM_RIOT];
	
	String stateStrings[] = 
	{
		"Clean State",
		"Latent State",
		"Engaged State",
		"Disenchanted State"
	};	
	
	String riotStrings[] =
	{
		"Flight",
		"Assembling",
		"Bluster",
		"Vandalism",
		"Assault",
	};
	
	
	public StatisticReport(){
		
	}
	
	public void record_RiotInfo(int riotId){
		riots[riotId]++;
	}
	
	public void record_StateInfo(int stateId){
		states[stateId]++;
	}
	
	public String report_State(int stateId, boolean isTitle){
		if(isTitle){
			if(stateId != 3){
				return String.format("%s \t\t%d\n", stateStrings[stateId] + ":", states[stateId]);
			}
			else{ // only one tab before the string "Disenchanted State".  
				return String.format("%s \t%d\n", stateStrings[stateId] + ":", states[stateId]);
			}
		}
		else{
			return states[stateId] + " ";
		}
	}	
	
	public String report_Riot(int riotId, boolean isTitle){
		if(isTitle){
			return String.format("%s \t\t%d\n", riotStrings[riotId] + ":", riots[riotId]); 
		}
		else{
			return riots[riotId] + " ";
		}
	}	
	
	
	public void reset(){
		for(int i=0; i<states.length; i++){
			states[i] = 0;
		}
		
		for(int i=0; i<riots.length; i++){
			riots[i] = 0;
		}
	}
}
