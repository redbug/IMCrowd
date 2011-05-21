package imcrowd.graph;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Graph {
	List<Edge> edges;
	public Graph() {
		edges = Collections.synchronizedList( new ArrayList<Edge>());
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	public void drawEdges(Graphics2D g) {
		synchronized(edges) {
			for(Edge e:edges) {
				e.drawLine(g);
			}
		}
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
	}
	
	public void clearEdges() {
		edges.clear();
	}
	
}
