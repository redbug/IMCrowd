package imcrowd.osgiBundle;

import imcrowd.engine.Engine;
import imcrowd.engine.FrameQueue;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.geom.Point2D;
import java.util.Map;


public class IMNETbridge implements Runnable, Colleague{
	private Mediator mediator;
	private FrameQueue frameQueue;
	Map<Integer, Point2D> currentFrame;
	Engine engine;
	IMNETutil imnetUtil;
	public IMNETbridge(FrameQueue fq){
		frameQueue = fq;
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		//imnetUtil = engine.getIMNETutil();
	}
	
	public void setManagerMethod() {}
	
	
	public void run() {
/*		for IMNET Bundle
  		while(this != null){
			if(!frameQueue.isEmpty()){
				currentFrame = frameQueue.dequeue();
				imnetUtil.walkTo(currentFrame);			
			}else{
				System.out.println("FrameBuffer has empty!");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
*/		
	}
	
}
