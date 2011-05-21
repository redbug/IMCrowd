package imcrowd.basicObject.obstacle;

import imcrowd.basicObject.ConfigurationIO;
import imcrowd.basicObject.MyObject;
import imcrowd.basicObject.Neighborhood;
import imcrowd.engine.Engine;
import imcrowd.io.imageIO.ImageLoader;
import imcrowd.manager.GridManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.vecmath.Vector2f;


public abstract class Obstacle implements MyObject, ConfigurationIO {
	protected boolean isSelected;				
	protected boolean isInteractive;			
	protected boolean isInfective;
	
	protected int height, width;						//The height and width of the obstacle's image.
	protected int separationFactor;						//Separation weight of the obstacle.
	protected int currentGridID;
	
	protected ImageIcon img;							//The image of the obstacle.
//	static ImageIcon selected = new ImageIcon(ClassLoader.getSystemResource("res/arrw03_33a.gif"));;	//標示選取的圖示.
	protected ImageIcon selected;
	protected Ellipse2D region;							//The circumscribed circle of the obstacle.
	
	protected Neighborhood neighborhoodMark;
	
	protected Vector2f vPosition;						//The position of the obstacle
	GridManager gridManager = Engine.getInstance().getGridManager();
	
	protected Obstacle(Vector2f p, ImageIcon img){
		isSelected = false;
		selected = ImageLoader.selectedImg; 
		vPosition = new Vector2f(p);
		
		this.img = img;
		height = img.getIconHeight();
		width = img.getIconWidth();
			
		region = new Ellipse2D.Double(vPosition.x - width/2, vPosition.y - height/2, width, height);
	
	}
	
	protected Obstacle(Vector2f p){
		isSelected = false;
		selected = ImageLoader.selectedImg; 
		vPosition = new Vector2f(p);
	}
	
	
	public void setAttributes(Properties configuration, int i){
		
	}
	
	public Map<String, String> getAttributes(int i){
		
		HashMap<String, String> attrMap = new HashMap<String, String>();
		String attrName, value;
		
		attrName = "ob_vPosition"+i;
		value = vPosition.toString().replace("(","").replace(")","").replace(",","");
		attrMap.put(attrName, value);
		
		return attrMap;
	}

	public Neighborhood getNeighborhoodMark(){
		return neighborhoodMark;
	}
	
	public void setNeighborHoodMark(Neighborhood n){
		this.neighborhoodMark = n;
	}
	
	/* currentGridID */
	public int getCurrentGridID() {
		return currentGridID;
	}
	
	public void setCurrentGridID(int id) {
		currentGridID = id;
	}
	
	public boolean isInteracitve(){
		return isInteractive;
	}
	
	public boolean isInfectant(){
		return isInfective;
	}
	
	
	/* drawSelect */
	public void setSelect(boolean bool){
		isSelected = bool;
	}
	
	/* Evaluates the obstacle to see if it overlaps or intersects with the point */
	public boolean isPointInside(Point2D p)
	{ 
	  return region.contains(p); 
	}
	
	/* obImg */
	public ImageIcon getImg(){
		return img;
	}
	
	/* region */
	public Ellipse2D getRegion(){
		return region;
	} 
	
	@Override
	public void setRegion(){
		region.setFrame(vPosition.x - width/2, vPosition.y - height/2, width, height);
		gridManager.occupiedGrid(this);
		gridManager.updateNeighborContext(this);
	}
	
	/* vPosition */
	public Vector2f getPosition(){
		return new Vector2f(vPosition);
	}
	
	public void setPosition(float x, float y){
		vPosition.x = x;
		vPosition.y = y;
		setRegion();
	}
	
	public void setPosition(Vector2f p){
		vPosition = p;
		setRegion();
	}
	
	/* Height, Width */
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	/* separation factor */
	public int getSFactor(){
		return separationFactor;
	}
	
	public void setSFactor(int f){
		separationFactor =f;
	}
	
	public Object clone(){
		Object o = null;
		try{
			o=super.clone();
		}catch(CloneNotSupportedException e){
			System.err.println("MyObject can't clone");
		}
		return o;
	}
	
	
	
	/************************************************
	 *	To draw the neighborhood for testing. 
	 ************************************************/
	protected void drawNeighborHoodMark(Graphics2D g) {
		if(neighborhoodMark == null)
			return;

		g.setPaint(new Color(250, 0, 0));
		
		int side = GridManager.SIDE_LENGTH;
		int rectWidth = 10;
		int halfRectWidth = (int)(rectWidth * 0.5);
		
		//Left Top Corner
		g.fillRect(neighborhoodMark.LT_X * side - halfRectWidth, 
				   neighborhoodMark.LT_Y * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);
		
		//Left Bottom Corner
		g.fillRect(neighborhoodMark.LT_X * side - halfRectWidth, 
				  (neighborhoodMark.BR_Y+1) * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);
		

		//Right Top Corner
		g.fillRect((neighborhoodMark.BR_X+1) * side - halfRectWidth, 
				   neighborhoodMark.LT_Y * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);

		//Right Bottom Corner
		g.fillRect((neighborhoodMark.BR_X+1) * side - halfRectWidth, 
				   (neighborhoodMark.BR_Y+1) * side - halfRectWidth, 
				   rectWidth, 
				   rectWidth);
		
	}
	
	@Override
	public void paint(Graphics2D g){
		g.drawImage(img.getImage(), (int)(vPosition.x - width/2), (int)(vPosition.y - height/2), null);
		//g.draw(region);    
		if(isSelected){
			g.drawImage(selected.getImage(), (int)(vPosition.x - selected.getIconWidth()/2),
					    (int)(vPosition.y - height/2 -selected.getIconHeight()-5), null);
			
			drawNeighborHoodMark(g);
		}
	}
}
