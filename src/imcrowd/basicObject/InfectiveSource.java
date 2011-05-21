package imcrowd.basicObject;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.GroupMind;
import imcrowd.basicObject.agent.boid.Boid;


public class InfectiveSource implements Cloneable /*, ConfigurationIO*/{
	/***********************
	 * 		Attributes
	 ***********************/
	private final int alpha;		//Incubation (The smaller the £\ parameter is, the faster the emotion contagion proceeds)
	private final int beta;			//stickiness (The larger the £] parameter is, the longer the collective activities last)
	private final Boid iboid;
	private GroupMind collectiveBehavior; 		
	private int cBehStimulusCounter[];

	/***********************
	 * 		Counter
	 ***********************/
	private int alphaCounter;				// 0 	-> alpha		
	private int betaCounter;				// beta -> 0			
	private int emotionArousalPoint;		// depend on agent's ration and alpha
	
	Agent hostAgent;

	public InfectiveSource(int alpha, int beta, Boid iboid, GroupMind collectiveBeh){
		this.alpha = alpha;
		this.beta  = beta;
		this.iboid = iboid;
		this.collectiveBehavior = collectiveBeh;
		
		String cBehaviorName = collectiveBehavior.getName(); 
		
		if(cBehaviorName.equals("Riot")){
			cBehStimulusCounter = new int[5];	//five actions
		}
		else if (cBehaviorName.equals("Gathering")){
			cBehStimulusCounter = new int[1];	//one action
		}
		else if (cBehaviorName.equals("Panic")){
			cBehStimulusCounter = new int[1];	//one action
		}
		for(int i=0; i < cBehStimulusCounter.length; i++){
			cBehStimulusCounter[i] = 0;
		}
		
		alphaCounter = 0;
		betaCounter  = beta;
	}
	
	public void initialize(float rationality){
		alphaCounter = 0;
		betaCounter  = beta;
		emotionArousalPoint = (int)(alpha * rationality);
		
		for(int i=0; i<cBehStimulusCounter.length; i++){
			cBehStimulusCounter[i] = 0;
		}
	}
	
	public Object clone(){
		Object o = null;
		try{
			o=super.clone();		
			((InfectiveSource)o).cBehStimulusCounter = cBehStimulusCounter.clone();
		}catch(CloneNotSupportedException e){
			System.err.println("MyObject can't clone");
		}
		return o;
	}
	
	public void setHostAgent(Agent ag){
		hostAgent = ag;
	}
	
	public void addBeta(float rationality, int weight, int behIndenx){
		int n = cBehStimulusCounter[behIndenx]++;
		
		//marginal beta = weight * (1 - r) ^ ( n / 100)
		long margin = Math.round(weight * Math.pow((1- rationality), (float)n/100));
		
//if(hostAgent.isDrawView()){
//	System.out.println("agId:" + hostAgent.getId() + " index:" + behIndenx + " nStimulus:"+cBehStimulusCounter[behIndenx] + "margin:"+margin);
//}
		betaCounter += margin;
	}
	
	public GroupMind getCollectiveBehavior(){
		return collectiveBehavior;
	}
	
	public int getAlphaCounter() {
		return alphaCounter;
	}

	public void setAlphaCounter(int alphaCounter) {
		this.alphaCounter = alphaCounter;
	}

	public int getBetaCounter() {
		return betaCounter;
	}

	public void setBetaCounter(int betaCounter) {
		this.betaCounter = betaCounter;
	}
	
	
/*	
	public void incrBetaCounter(){
		if(betaCounter < beta){
			betaCounter++;
		}	
		else{
			betaCounter = beta;
		}
	}
*/
	
	public void descBetaCounter(){
		if(betaCounter > 0){
			betaCounter--;
		}
		else{
			betaCounter = 0;
		}
	}
	
	public void resetBetaCounter() {
		betaCounter = beta;
	}
	
	/**************************************************************
	 * |=================| 	alpha (the ceiling of the alpha counter)
	 * |=====|			 	alphaCounter
	 *************************************************************/
	public void incrAlphaCounter(){
		if(alphaCounter < alpha){
			alphaCounter++;
		}	
		else{
			alphaCounter = alpha;
		}
	}
	
	public void descAlphaCounter(){
		if(alphaCounter > 0){
			alphaCounter--;
		}
		else{
			alphaCounter = 0;
		}
	}
	
	public void resetAlphaCounter(){
		alphaCounter = 0;
	}
	
	
	public boolean isPurge(){
		if(betaCounter <= 0)
			return true;
		else
			return false;
	}
	
	
	
	public int getSeparationI(){
		return iboid.getSeparationWeight();
	} 
	
	public int getCohesionI(){
		return iboid.getCohesionWeight();
	}
	
	public int getAlignmentI(){
		return iboid.getAlignmentWeight();
	}
	
	/* 
	 * true: emotion aroused. (Infected state)
	 * false: latent state.
	 */
	public boolean isEmotionArousal() {
		if(alphaCounter >= emotionArousalPoint) {
			return true;
		}
		return false;
	}
	
	public int getEmotionArousalPoint(){
		return emotionArousalPoint;
	}
	
	public int getAlpha(){
		return alpha;
	}
	
	public int getBeta(){
		return beta;
	}

	/*
	
	@Override
	public Map<String, String> getAttributes(int i) {
		
		Map<String, String> attrMap =  new HashMap<String, String>();
		String attrName, value;
		
		attrName = "ag_alpha"+i;
		value = String.valueOf(alpha);
		attrMap.put(attrName, value);
		
		attrName = "ag_beta"+i;
		value = String.valueOf(beta);
		attrMap.put(attrName, value);
		
		attrName = "ag_iSeparation"+i;
		value = String.valueOf(getSeparationI());
		attrMap.put(attrName, value);

		attrName = "ag_iCohesion"+i;
		value = String.valueOf(getCohesionI());
		attrMap.put(attrName, value);

		attrName = "ag_iAlignment"+i;
		value = String.valueOf(getAlignmentI());
		attrMap.put(attrName, value);		

		
		attrName = "ag_cBehavior"+i;
		value = collectiveBehavior.getName(); 
		attrMap.put(attrName, value);
		
		
		attrName = "ag_cBehStimulusCounter"+i;
	
		StringBuffer str = new StringBuffer();
		for(int j=0; j<cBehStimulusCounter.length; j++){
			str.append(cBehStimulusCounter[j]);
			if(j != cBehStimulusCounter.length -1){
				str.append(",");
			}	
		}
		value = str.toString();
		attrMap.put(attrName, value);
		
		
		
		attrName = "ag_alphaCounter"+i;
		value = String.valueOf(alphaCounter);
		attrMap.put(attrName, value);
		
		attrName = "ag_betaCounter"+i;
		value = String.valueOf(betaCounter); 
		attrMap.put(attrName, value);
		
		attrName = "ag_emotionArousalPoint"+i;
		value = String.valueOf(emotionArousalPoint); 
		attrMap.put(attrName, value);
	
		return attrMap;
	}

	@Override
	public void setAttributes(Properties configuration, int i) {
		// TODO Auto-generated method stub
		
	}
	
	*/	
}
