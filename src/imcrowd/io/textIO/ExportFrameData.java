package imcrowd.io.textIO;

import imcrowd.basicObject.agent.Agent;
import imcrowd.engine.Engine;
import imcrowd.manager.AgentManager;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;


public class ExportFrameData implements Colleague{
	private Mediator mediator;
	final int totalFrame = 1024;
	int frame = 1;
	AgentManager agentManager;
	Engine engine;
//String filename;
	
//	public ExportFrameData(String filename){
	public ExportFrameData(){
//this.filename = filename;
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		agentManager = engine.getAgentManager();
	}
	public void setManagerMethod() {}
	
	
	
	public void writeOut() throws IOException{
		PrintWriter out1 =
	    	new PrintWriter(
	    		new BufferedWriter(
	    			//new FileWriter("c:/temp.dat",true)),true);
	    				new FileWriter("/home/redbug/Desktop/frameDate.dat",true)),true);
		
		List<Agent> agentList = agentManager.getAgentList();
		if(frame == 1){
			out1.println(totalFrame);
			out1.println(1);
			out1.println(agentList.size());
		}
		Agent ag;
//		Vector3f velocity;
//		float theta;
		if(agentList != null){
			if(frame > totalFrame){
				engine.setExport(false);
				System.out.println("end of export!!");
				return;
			}
//			synchronized (agentList) {	
				out1.println(frame++);
				for (Iterator<Agent> it = agentList.iterator(); it.hasNext();) {
				    ag = it.next();
//				    velocity = ag.getVelocity();
//				    theta = (float)Math.toDegrees(Math.atan2(velocity.y, velocity.x));
			
//				    out1.println(ag.getPosition().x /10.0 + " " +ag.getPosition().y/10.0 + " " + (ag.getOrientation()-180));
				    
				    //TODO
				    //out1.println(ag.getPosition().x /5.0 + " " +ag.getPosition().y/5.0 + " " + -ag.getOrientation());
				    
//				    out1.println(ag.getPosition().x /10.0 + " " +ag.getPosition().y/10.0 + " " + theta);
				}
//			}
		}
		out1.close();
	}			
}
