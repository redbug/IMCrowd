package imcrowd.basicObject.agent.behavior.groupMind.riot;

import imcrowd.basicObject.agent.Agent;
import imcrowd.basicObject.agent.behavior.groupMind.GroupMind;
import imcrowd.basicObject.agent.normalAgent.NormalAgent;
import imcrowd.basicObject.obstacle.Obstacle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2f;

public abstract class Riot implements GroupMind{
	public static final int FLIGHT 		= 0;
	public static final int ASSEMBLING	= 1;
	public static final int BLUSTER		= 2;
	public static final int VANDALISM 	= 3;
	public static final int ASSAULT 	= 4;
	
	/*****************************************
	 * Weights of hysteria signal 
	 *****************************************/
	public final int wFlight			= -5;
	public final int wAssembling		= 1;
	public final int wBluster			= 3;
	public final int wVandalism			= 5;
	public final int wAssault			= 10;
	
	protected int behId;
	protected Image img;
	protected float imgScale;
	
	
	public void changeState(NormalAgent me) {
		int situation = me.situationAnalysis();
		int morale = Math.round((float)me.getNumGreen() / me.getNumSameParty());
				
		if(morale <= -3){
			me.changeCollectiveBehavior(Flight.getInstance());
		}
		else{
			float ration = me.getCurrentRationValue();
			
			switch(situation){
			
				case Agent.PEACE:
					if(me.getTargetOb() != null){
						if(ration <= 0.5){		
							if(me.isNearByPolice()){
								me.changeCollectiveBehavior(Assembling.getInstance());
							}
							else{
								me.changeCollectiveBehavior(Vandalism.getInstance());
							}	
						}else{
							me.changeCollectiveBehavior(Bluster.getInstance());
						}		
					}
					else{
						me.changeCollectiveBehavior(Assembling.getInstance());
					}	
					break;
					
				case Agent.MINORITY:
					me.changeCollectiveBehavior(Flight.getInstance());
					break;
					
					
				case Agent.EQUAL_STRENGTH:					
					
					if (morale >= 1){
						me.changeCollectiveBehavior(Bluster.getInstance());
					}
					else{
						me.changeCollectiveBehavior(Assembling.getInstance());
					}
						
					break;
					
					
				case Agent.SUPERIORITY:
					if(me.isNearByPolice()){
						me.changeCollectiveBehavior(Bluster.getInstance());
					}
					else{
						if(morale >= 2){
							if(ration <= 0.3){
									me.changeCollectiveBehavior(Assault.getInstance());
							}
							else if (ration <= 0.5){
								if(me.getTargetOb() != null){
									me.changeCollectiveBehavior(Vandalism.getInstance());
								}
								else{
									me.changeCollectiveBehavior(Bluster.getInstance());
								}
							}
							else{
								me.changeCollectiveBehavior(Bluster.getInstance());
							}
						}
						else if (morale >= 1){
							me.changeCollectiveBehavior(Bluster.getInstance());
						}
						else{
							me.changeCollectiveBehavior(Assembling.getInstance());
						}
					}
					break;
			}	
		}	
	}
	
	
	@Override
	public void contactInCircle_Engaged(NormalAgent me, Agent he) {
		
		if(!he.isSpecial()){
			
			if(he.getStateColor() == Color.GREEN){
				
				/*****************************************************************
				 * Only calculate the hysteria signals and morale of the own party.
				 *****************************************************************/
				if(me.getColorId() == he.getColorId()){
					int behId = ((Riot)he.getCollectiveBehavior()).getId();
					int degree = 0;		
	
					switch(behId){
						case FLIGHT:
							me.addBeta(wFlight, FLIGHT);
							degree = -5;
							break;
							
						case ASSEMBLING:
							me.addBeta(wAssembling, ASSEMBLING);
							degree = 1;
							break;
							
						case BLUSTER:
							me.addBeta(wBluster, BLUSTER);
							degree = 2;
							break;
							
						case VANDALISM:
							me.addBeta(wVandalism, VANDALISM);
							degree = 3;
							break;
							
						case ASSAULT:
							me.addBeta(wAssault, ASSAULT);
							degree = 4;
							break;
							
						default:
							System.err.println("error");
					}
					/**************
					 * adding morale 
					 **************/
					me.addNGreen(degree);
				}
			}	
		}
		
	}
	
	/**************************************************************************
	 * The collective action in riot situation is very sensitive to the 
	 * context of surrounding environment because those who are engaged in 
	 * a riot situation must remain vigilant in case of being attacked or 
	 * caught by the hostile party or the police. Hence, we assumed that 
	 * the agent extends its perception view from fan-shaped region to a 
	 * 360-degree circle by modifying the perception angle if it is 
	 * engaged in riot situation. This means that the agent can turn its 
	 * head to look around the situation before taking any collective action 
	 * in a riot. Nonetheless, when agent collects information to avoid 
	 * obstacles, it still does not consider the information of its rear part. 
	 **************************************************************************/
	@Override
	public void contactInCircle_Latent(NormalAgent me, Agent he){
		
		/*************************************************
		 * count both number of green and yellow agents
		 *************************************************/
		
		if(he.isSpecial() && he.getInfectiveSource() != null){
			me.addOneYellow();
		}
		else{
		
			Color c = he.getStateColor();
			
			if(c == Color.YELLOW){
				me.addOneYellow();
			}
			
			/***************************************************************
			 * calculate the bandwagon pressure signals of the both parties.
			 ***************************************************************/
			else if(c == Color.GREEN){
				int behId = ((Riot)he.getCollectiveBehavior()).getId();
				int degree = 0;
				
				switch(behId){
				
					case Riot.ASSEMBLING:
						degree = 1;
						break;
						
					case Riot.BLUSTER:
						degree = 2;
						break;
						
					case Riot.VANDALISM:
						degree = 3;
						break;
						
					case Riot.ASSAULT:
						degree = 4;
						break;
						
					case Riot.FLIGHT:
						degree = 5;
						break;
						
					default:
						System.err.println("error");
				}

				/******************************
				 * adding conformity pressure signal 
				 ******************************/
				me.addNGreen(degree);
			}
			
		}

	}
	
	
	
	
	@Override
	public void contactInView_Engaged(NormalAgent me, Agent he) {

	}
	
	@Override
	public void contactInView_Latent(NormalAgent me, Agent he) {

	}
	
	@Override
	public void contactInView_Latent(NormalAgent me, Obstacle ob){
		
	}
	
	
//	@Override
//	public boolean agentCollisionRespondant(Agent me, Agent he, boolean isHostile) {
//		if(isHostile){
//			return me.agentCollisionRespondant(he, false);
//		}else{
//			return me.agentCollisionRespondant(he, true);
//		}
//	}
	
	@Override
	public void reactToInvader(Agent me, Agent he, boolean isHostile) {
		if(!isHostile){
			me.reactToInvader(he);
		}	
	}
	
	
	public int getId(){
		return behId;
	}
	
	
	@Override
	public void drawFeatures(Graphics2D g, Agent ag) {
		Vector2f pos = ag.getPosition();
		g.setPaint(ag.getColorOfPs());
		g.fill3DRect((int)(pos.x - ag.getWidth() * 0.5), (int)pos.y + 10, ag.getWidth(), ag.getWidth(), true);
		
		AffineTransform at = new AffineTransform();
		at.translate(pos.x - (img.getWidth(null) * 0.5 * imgScale), (pos.y - img.getHeight(null) * 0.5 * imgScale));
		at.scale(imgScale, imgScale);
		g.drawImage(img, at, null);
	}
	
	@Override
	public String getName(){
		return "Riot";
	}
	
	@Override
	public int getHesitateWeight() {
		return 50;
	}
	
}
