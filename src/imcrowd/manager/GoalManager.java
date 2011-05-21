package imcrowd.manager;

import imcrowd.basicObject.goal.Goal;
import imcrowd.engine.Engine;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Vector2f;


public class GoalManager implements Colleague{
	public static boolean isDisplayGoal = true;
	private Mediator mediator;
	List<Goal> goalList;
	List<Goal> exitList;
	
	public GoalManager(){
		
		goalList = Collections.synchronizedList(new ArrayList<Goal>());
		exitList = new ArrayList<Goal>();
		
		Vector2f pos = new Vector2f();
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		int shift = 20;
		
		//Eastern exit
		pos.set(Engine.canvasWidth - shift, Engine.canvasHeight * 0.5f);
		exitList.add(new Goal(pos, imageLoader.getPngImg(2)));
		
		//Western exit
		pos.set(0 + 2 * shift, Engine.canvasHeight * 0.5f);
		exitList.add(new Goal(pos, imageLoader.getPngImg(2)));

		//Northern exit
		pos.set(Engine.canvasWidth * 0.5f, 0 + shift);
		exitList.add(new Goal(pos, imageLoader.getPngImg(2)));

		//Southern exit
		pos.set(Engine.canvasWidth * 0.5f, Engine.canvasHeight - 2 * shift);
		exitList.add(new Goal(pos, imageLoader.getPngImg(2)));
		
	/*	Point p;
		for(int i=0;i<1;i++){
			p = new Point(Math.abs(engine.getRandNum().nextInt()%(engine.canvasWidth-3)),
		               Math.abs(engine.getRandNum().nextInt()%(engine.canvasHeight-3)));
			goalList.add(new Goal(p, engine.getImg(0)));
		}
	*/		
	}
	
	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
	
	public void setManagerMethod() {}
	
	
	public boolean isEmpty(){
		return goalList.isEmpty();
	}
	
	public int size(){
		return goalList.size();
	}
	
	public void addGoal(Goal goal) {
		if (goal != null){
			synchronized (goalList) {	
				goalList.add(goal);
			}
		}	
		else
			System.out.println("goal added filed!!");
	}
	
	public void removeOne(Goal g){
		synchronized(goalList){
			goalList.remove(g);
		}
	}
	
	public void reset(){
		goalList.clear();
	}
	
	public Goal getNearestExit(Vector2f agPos){
		float minDist = 5000;
		float len;
		
		Vector2f vec = new Vector2f();
		Goal targetGoal = null;
		
		for(Goal exit:exitList){
			vec.sub(exit.getPosition(), agPos);
			if((len = vec.length()) < minDist){
				minDist = len;
				targetGoal = exit;
			}		
		}
		return targetGoal;
	}
	
	public Goal getNewGoal(){
		synchronized(goalList){
			return (Goal)goalList.get(Math.abs(Engine.rand.nextInt()) % goalList.size());
		}
	}
	
	public List<Goal> getGoalList(){
		synchronized(goalList){
			return goalList;
		}
	}
	
	public Goal isPointInGoal(Point2D p)
	{
		Goal g;
		synchronized(goalList){
			for (Iterator<Goal> it = goalList.iterator(); it.hasNext();) {
				g = it.next();
				if (g.isPointInside(p)) {
					return g;
				}
			}
		}
	    return null;
	}    	
	
	public void paintAllGoals(Graphics2D g){
		if(isDisplayGoal){
			synchronized (goalList) {
				if(goalList.size() != 0) {
					for (Goal goal: goalList) {
						goal.paint(g);
					}
				}	
			}
			
			for(Goal exitGoal: exitList){
				exitGoal.paint(g);
			}
		}
	}
}
