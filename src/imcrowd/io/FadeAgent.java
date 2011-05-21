package imcrowd.io;

import imcrowd.basicObject.agent.Agent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;


public class FadeAgent {
	public final float x;
	public final float y;
	public final float vx;
	public final float vy;
	
	int size = 10;
	int radius = 5;
	int state = 0;
	
	Ellipse2D personalSpace;

	public FadeAgent(float x, float y, float vx, float vy, int size, int state){
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.size = size;
		this.state = state;
		
		personalSpace =  new Ellipse2D.Double(x-size/2, y-size/2, size, size);
	}
		
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append(x+" ");
		str.append(y+" ");
		str.append(vx+" ");
		str.append(vy+" ");
		return new String(str);
	}
	
	public void paint(Graphics2D g){
		
		/*TODO
		if(state == InfSrcManager.special){
			g.setStroke(new BasicStroke(2));
			g.setPaint(Agent.state_color[state]);
		}else{
			g.setPaint(Agent.state_color[InfSrcManager.clean]);
		}
		g.draw(personalSpace);
		*/
		
		//TODO
//		if(state == InfSrcManager.infected_CollectiveBehavior || state == InfSrcManager.infected_IndividualBehavior){
			
//			g.setPaint(Agent.state_color[state]);
//			g.fill(personalSpace);
//		}

		g.setStroke(new BasicStroke(2));
		g.setPaint(Color.black);
		g.draw(new Line2D.Double(x,y,x+vx,y+vy));
		g.setStroke(new BasicStroke(1));
	}
	
	
}
