package imcrowd.manager;

import imcrowd.basicObject.obstacle.Obstacle;
import imcrowd.engine.Engine;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ObstacleManager implements Colleague{
	private Mediator mediator;
	List<Obstacle> obList;
	
	GridManager gridManager;
	Engine engine;
	
	public ObstacleManager(){
		obList = Collections.synchronizedList(new ArrayList<Obstacle>());
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
		engine = (Engine)mediator;
		gridManager = engine.getGridManager();
	}
	public void setManagerMethod() {}
	
	
	public void addObstacle(Obstacle ob) {
		if (ob != null){
			obList.add(ob);
			gridManager.addObstacle(ob);
		}
		else
			System.out.println("obstacle added filed!!");
	}
	
	public void removeOne(Obstacle ob){
		synchronized(obList){
			obList.remove(ob);
			gridManager.removeObstacle(ob);	
		}
	}
	
	public void reset()
	{
		obList.clear();
	}
	
	public List<Obstacle> getObList(){
		return obList;
	}
	
	public Obstacle isPointInObstacle(Point2D p)
	{
		Obstacle ob;
		synchronized(obList){
			for (Iterator it = obList.iterator(); it.hasNext();) {
				ob = (Obstacle) it.next();
				if (ob.isPointInside(p)) {
					return ob;
				}
			}
		}
	    return null;
	}    	
	
	public void paintAllObstacles(Graphics2D g) {
		synchronized (obList) {
			if(obList.size() != 0) {
				for (Iterator it = obList.iterator(); it.hasNext();) {
					((Obstacle) it.next()).paint(g);
				}
			}
		}
	}
}
