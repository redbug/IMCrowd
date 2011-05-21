package imcrowd.graph;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


public class Edge {
	Line2D edge;
	public Edge(Point2D parent, Point2D child) {
		edge = new Line2D.Double(parent, child);
	}
	
	public void drawLine(Graphics2D g) {
		g.draw(edge);
	} 
	
}
