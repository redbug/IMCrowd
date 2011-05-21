package imcrowd.engine;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Map;


public class FrameQueue {
	final int capacity = 20;
	LinkedList< Map<Integer, Point2D> > frameQueue;
	
	
	public FrameQueue(){
		frameQueue = new LinkedList< Map<Integer, Point2D> >();
	}
	
	public int size(){
		return frameQueue.size();
	}
	
	public boolean isFull(){
		return (frameQueue.size() >= capacity)? true: false;	
	}
	
	public boolean isEmpty(){
		return frameQueue.isEmpty();
	}

	public void enqueue(Map<Integer, Point2D> frame){
			frameQueue.addLast(frame);
	}
	public boolean isLessThanHelf(){
		return frameQueue.size()*2 < capacity;
	}
	public Map<Integer, Point2D> dequeue(){
		return frameQueue.pollFirst();
	}

}
