package imcrowd.util;

import imcrowd.engine.Engine;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.vecmath.Vector2f;


public class MyMath {
	private static MyMath math = new MyMath();
	private static AffineTransform p2C_Matrix;
	
	static Random rand = new Random();
	
	private static final int canvasWidth = Engine.getInstance().getWidth(),
							 canvasHeight = Engine.getInstance().getHeight();
	
	private MyMath(){
		p2C_Matrix = new AffineTransform();
		
	}
	
	public static MyMath getInstance(){
		return math;
	}
	
	/***********************************************************************************
	 * Vertex Rotation and Translation 
	 * 		formula:
	 * 			x' = xcos@ - ysin@ + tx
	 * 			y' = xsin@ + ycos@ + ty
	 ***********************************************************************************/
	public static Point2D vertexTransform(double x, double y, double tx, double ty, double theta){
		return new Point2D.Double(
				x * Math.cos(Math.toRadians(theta)) - y * Math.sin(Math.toRadians(theta)) + tx,
				x * Math.sin(Math.toRadians(theta)) + y * Math.cos(Math.toRadians(theta)) + ty
		);
		
	}
	
	
	/***********************************************************************************
	 * Vertex Rotation and Translation 
	 * 		formula:
	 * 			x' = xcos@ - ysin@ + tx
	 * 			y' = yPlaner - (xsin@ + ycos@ + ty)
	 ***********************************************************************************/
	public static Point2D vertexTransform2(double x, double y, double tx, double ty, double theta, int yPlaner){
		return new Point2D.Double(
				x * Math.cos(Math.toRadians(theta)) - y * Math.sin(Math.toRadians(theta)) + tx,
				yPlaner - (x * Math.sin(Math.toRadians(theta)) + y * Math.cos(Math.toRadians(theta)) + ty)
		);
		
	}
	
	public static Vector2f rotation(double x, double y, double theta){
		return new Vector2f(
				(float)(x * Math.cos(Math.toRadians(theta)) - y * Math.sin(Math.toRadians(theta))) ,
				(float)(x * Math.sin(Math.toRadians(theta)) + y * Math.cos(Math.toRadians(theta)))
		);	
	}
	
	
	/*****************************************************************
	 * Coordination transformation from planer space to canvas space.
	 *****************************************************************/
	public static AffineTransform getP2CMatrix(Double[] initialConfig, float cHeight, float scaleX, float scaleY){
		p2C_Matrix.setTransform(1,0,0,-1,0,cHeight); //Cy = cHeight - Py.
		
		p2C_Matrix.scale(scaleX,scaleY);
		p2C_Matrix.translate(initialConfig[0],initialConfig[1]);
		p2C_Matrix.rotate(Math.toRadians(initialConfig[2]));
		return p2C_Matrix;
	}
	
	
	
	
	/*************************************************************************************************************
	 * Testing whether a point lies on one of the interior of the polygon or not.
	 * 		Solution:
	 * 			If the polygon is "convex" then one can consider the polygon as a "path" from the first vertex.(go around counterclockwise) 
	 * 			A point is on the interior of this polygon if it is always on the same side of all the line 
	 * 			segments making up the path. 
	 * 			
	 *************************************************************************************************************/
	public static boolean isInsideAPolygon(Vector2f p, ArrayList<Vector2f[]> obstaclePlanarSpace){
		Iterator<Vector2f[]> it2 = obstaclePlanarSpace.iterator();
		Vector2f[] obPolygon;
		Vector2f q1,q2;
		q1 = new Vector2f();
		q2 = new Vector2f();
		boolean flag = false;
		
		while(it2.hasNext()){
			obPolygon = it2.next();
			// all vertexes which make up the polygon
			for(int vertex=0; vertex < obPolygon.length;vertex++){
				flag = true;
				
				if(vertex == 0){								
					q1 = obPolygon[vertex];
				}
				else{
					q1=q2;										
				}	
				
				if(vertex == obPolygon.length-1){
					q2 = obPolygon[0];
				}
				else{
					q2 = obPolygon[vertex+1];
				}	
				
				//if the point lies on the right of a line segment, it doesn't lie on the interior of this polygon.
				if(crossProduct(vector(q2,q1),vector(p,q1)) < 0){
					flag = false;
					break;
				}
			}
			if(flag == true)
				return flag;	//point lies on one of the interior of polygons.
		}	
		return flag;			//always return false here!
	}
	
	
	/******************************************
	 * Constructing a polygon from vertexes.
	 ******************************************/
	public static void constructPolygon(Point2D[] vertexs, GeneralPath polygon, boolean isReset){
		if(isReset) polygon.reset();
		
		polygon.moveTo((float)(vertexs[0].getX()),(float)(vertexs[0].getY()));
		for(int j = 1; j < vertexs.length; j++){
			polygon.lineTo((float)(vertexs[j].getX()),(float)(vertexs[j].getY()));
		}
		polygon.closePath();
	}
	
	
	
	/*********************************************************************************
	 * Using crossProduct to test line intersection.
	 * Two line intersect each other, if both conditions below are satisfied.
	 * 		p1 and p2 are located in different sides of line from q1 to q2.
	 * 		q1 and q2 are located in different sides of line from p1 to p2.
	 *********************************************************************************/
	public static boolean detectLineIntersection(Vector2f p1, Vector2f p2, Vector2f q1, Vector2f q2){
		return (crossProduct(vector(p1,q1),vector(q2,q1))* crossProduct(vector(p2,q1),vector(q2,q1)) <=0 &&
				crossProduct(vector(q1,p1),vector(p2,p1))* crossProduct(vector(q2,p1),vector(p2,p1)) <=0);
	}
	
	
	
	Vector2f GetCrossPoint(Vector2f p1, Vector2f p2, Vector2f q1, Vector2f q2)
	{
		if(!detectLineIntersection(p1, p2, q1, q2)){
			return null;   //no intersection
		}
		else{
			/****************************
			 *  solve linear equation
			 ****************************/
			Vector2f crossPoint = new Vector2f();
			float tempLeft,tempRight;
			
			//x coordination
			tempLeft = (q2.x - q1.x) * (p1.y - p2.y) - (p2.x - p1.x) * (q1.y - q2.y);
			tempRight = (p1.y - q1.y) * (p2.x - p1.x) * (q2.x - q1.x) + q1.x * (q2.y - q1.y) * (p2.x - p1.x) - p1.x * (p2.y - p1.y) * (q2.x - q1.x);
		    crossPoint.x = (float)( tempRight / tempLeft );
			
		    //y coordination	
			tempLeft = (p1.x - p2.x) * (q2.y - q1.y) - (p2.y - p1.y) * (q1.x - q2.x);
			tempRight = p2.y * (p1.x - p2.x) * (q2.y - q1.y) + (q2.x- p2.x) * (q2.y - q1.y) * (p1.y - p2.y) - q2.y * (q1.x - q2.x) * (p2.y - p1.y);
			crossPoint.y = (float)( tempRight / tempLeft );
			
			return crossPoint;			
		}
	}
	
	
	
	
	/***************************************************
	 * input: two vector -> p,q
	 * output: p cross q > 0, counterclockwise
	 * 		   p cross q < 0, clock wise 
	 ***************************************************/
	public static double crossProduct(Vector2f p, Vector2f q){
		return p.x * q.y - p.y * q.x;
	}
	
	
	
	/***************************************************
	 * Vector p->q.
	 ***************************************************/
	public static Vector2f vector(Vector2f p ,Vector2f q){
		Vector2f v = new Vector2f();
		v.sub(q,p);
		return v;
	}
	
	/****************************************************
	 * return the length of a vector.
	 ****************************************************/
	public static double vectorLength(Point2D vec){
		double x = vec.getX();
		double y = vec.getY();
		
		return Math.sqrt( x * x + y * y);
	}
	
	/****************************************************
	 * vector normalization 
	 ****************************************************/
	public static Point2D normalize(Point2D vec){
		double length = vectorLength(vec);
		return new Point2D.Double(vec.getX()/length, vec.getY()/length);
	}
	
	/*********************************************************************
	 * finding the normal vector on the clockwise direction of inputing vector.
	 *********************************************************************/
	public static Point2D getRightHandSideNormal(Point2D vec){
		return new Point2D.Double(vec.getY(), -vec.getX());
	}
	
	public static Vector2f getRightHandSideNormal(Vector2f vec){
		return new Vector2f(vec.y, -vec.x);
	}
	
	public static Vector2f perp(Vector2f v)	{
		  return new Vector2f(-v.y, v.x);
	}
	
	
	/***************************************************
	 * Transforming nodeId to Point.
	 ***************************************************/
	public static Point nodeId2Point(int nodeId){
		return new Point(nodeId % canvasWidth, nodeId / canvasWidth);
	}
	
	
	/***************************************************
	 * Transforming Point to nodeId. 
	 ***************************************************/	
	public static int point2NodeId(Point p){
		return p.y * canvasWidth + p.x;
	}
	
	
}
