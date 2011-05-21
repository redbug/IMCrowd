package imcrowd.basicObject.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import imcrowd.basicObject.ConfigurationIO;
import imcrowd.util.MyMath;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;


public abstract class MovingObject implements ConfigurationIO{

	final float TOLERANT_ERR = 0.00001f; 
	
	/************************************
	 *  speed mode 
	 ************************************/
	public static final int SPEED_SLOW		= 0,
							SPEED_NORMAL	= 1,
							SPEED_HURRY		= 2,
							SPEED_STOP		= 3;	
	
	public final int vibrateThreshold = 10;
	
	private double lastAngle;
	private boolean lastTurnDirection;
	
	protected int    speedMode;
	
	protected Vector2f 	vPosition; 		 
	protected Vector2f  vVelocity;
	protected Vector2f  vHeading; 			//the heading vector (a normalized vector)
	protected Vector2f  vSide;				//a right-hand side vector perpendicular to the heading vector
//	Vector2f    vLinearAcc;			//linear acceleration.
//	Vector2f	vAngularAcc;		//angular acceleration.
	
	protected float       mass;
	protected float 	  speed;
//	float 		 facing;			//the facing direction(angle).
	protected float		 orientation;			//the moving direction(angle).
	
	/****************************
	 * constraint
	 ****************************/
	protected float       maxSpeed; 	
	protected float       maxForce;         
	protected float       maxTurnAngle = 120;
	protected float 	  normalSpeed;
	
	public MovingObject(Vector2f pos, float orient)
	{	
		vPosition = pos;
		orientation = orient;
		vVelocity = new Vector2f((float)Math.cos(Math.toRadians(orientation)), (float)Math.sin(Math.toRadians(orientation)));
		vHeading = new Vector2f();
		vSide = new Vector2f();
	}
	
	public void setAttributes(Properties configuration, int i){
		String str;
		String[] strArray;
		
		
		str = configuration.getProperty("ag_speedMode"+i);
		speedMode = Integer.valueOf(str);
		
//		str = configuration.getProperty("ag_vPosition"+i);
//		strArray = str.split(" ");
//		vPosition.x = Float.valueOf(strArray[0]);
//		vPosition.y = Float.valueOf(strArray[1]);
		
		str = configuration.getProperty("ag_vVelocity"+i);
		strArray = str.split(" ");
		vVelocity.x = Float.valueOf(strArray[0]);
		vVelocity.y = Float.valueOf(strArray[1]);

		str = configuration.getProperty("ag_vHeading"+i);
		strArray = str.split(" ");
		vHeading.x = Float.valueOf(strArray[0]);
		vHeading.y = Float.valueOf(strArray[1]);

		str = configuration.getProperty("ag_vSide"+i);
		strArray = str.split(" ");
		vSide.x = Float.valueOf(strArray[0]);
		vSide.y = Float.valueOf(strArray[1]);
	
		str = configuration.getProperty("ag_mass"+i);
		mass = Float.valueOf(str);
			
		str = configuration.getProperty("ag_speed"+i);
		speed = Float.valueOf(str);
		
//		str = configuration.getProperty("ag_orientation"+i);
//		orientation = Float.valueOf(str);
		
		str = configuration.getProperty("ag_maxSpeed"+i);
		maxSpeed = Float.valueOf(str);
	
		str = configuration.getProperty("ag_maxForce"+i);
		maxForce = Float.valueOf(str);
		
//		str = configuration.getProperty("ag_maxTurnRate"+i);
//		maxTurnRate = Float.valueOf(str);
		
		str = configuration.getProperty("ag_normalSpeed"+i);
		normalSpeed = Float.valueOf(str);
				
	}
	
	
	
	public Map<String, String> getAttributes(int i){
		
		HashMap<String, String> attrMap = new HashMap<String, String>();
		String attrName, value;
		
		attrName = "ag_speedMode"+i;
		value = String.valueOf(speedMode);
		attrMap.put(attrName, value);
		
		attrName = "ag_vPosition"+i;
		value = vPosition.toString().replace("(","").replace(")","").replace(",","");
		attrMap.put(attrName, value);
		
		attrName = "ag_vVelocity"+i;
		value = vVelocity.toString().replace("(","").replace(")","").replace(",","");
		attrMap.put(attrName, value);
		
		attrName = "ag_vHeading"+i;
		value = vHeading.toString().replace("(","").replace(")","").replace(",","");
		attrMap.put(attrName, value);

		attrName = "ag_vSide"+i;
		value = vSide.toString().replace("(","").replace(")","").replace(",","");
		attrMap.put(attrName, value);
				
		attrName = "ag_mass"+i;
		value = String.valueOf(mass);
		attrMap.put(attrName, value);		
		
		attrName = "ag_speed"+i;
		value = String.valueOf(speed);
		attrMap.put(attrName, value);		

		attrName = "ag_orientation"+i;
		value = String.valueOf(orientation);
		attrMap.put(attrName, value);		

		
		attrName = "ag_maxSpeed"+i;
		value = String.valueOf(maxSpeed);
		attrMap.put(attrName, value);	
		
		attrName = "ag_maxForce"+i;
		value = String.valueOf(maxForce);
		attrMap.put(attrName, value);	
		
//		attrName = "ag_maxTurnRate"+i;
//		value = String.valueOf(maxTurnRate);
//		attrMap.put(attrName, value);	
		
		attrName = "ag_normalSpeed"+i;
		value = String.valueOf(normalSpeed);
		attrMap.put(attrName, value);	
		
		return attrMap;
	}
	
	/* speedMode */
	public int getSpeedMode() {
		return speedMode;
	}

	public void setSpeedMode(int speedMode) {
		this.speedMode = speedMode;
	
		switch(speedMode) {
			case SPEED_SLOW:
				maxSpeed = normalSpeed * 0.5f;			
				break;
			case SPEED_NORMAL:
				maxSpeed = normalSpeed;
				break;
			case SPEED_HURRY:
				maxSpeed = normalSpeed * 1.25f;
				break;
			case SPEED_STOP:
				maxSpeed = 0;
				break;
		}	
	}
		
	public void turnRight(float angle){
		vHeading = MyMath.rotation(vHeading.x, vHeading.y, angle);
		if(vHeading.length() > TOLERANT_ERR){
			vHeading.normalize();
		}	

		vVelocity.scale(speed, vHeading);
	}
		
	public void turnLeft(float angle){
		vHeading = MyMath.rotation(vHeading.x, vHeading.y, -angle);
		if(vHeading.length() > TOLERANT_ERR){
			vHeading.normalize();
		}	

		vVelocity.scale(speed, vHeading);
	}
	
	
	public Vector2f getPosition() {
		return vPosition;
	}

	public void setPosition(Vector2f position) {
		vPosition = position;
	}
	
	public void shiftPosition(Vector2f pos){
		vPosition.add(pos);
	}
	
	public Vector2f getVelocity() {
		return vVelocity;
	}

	public void setVelocity(Vector2f velocity) {
		vVelocity = velocity;
	}
	
	public void setVelocity(float x, float y) {
		vVelocity.set(x, y);
	}

	public Vector2f getHeading() {
		return vHeading;
	}

	public void setHeading(Vector2f new_heading) {
		if((new_heading.lengthSquared() - 1.0) > 0.00001){
			 vHeading = new_heading;
			 vSide = MyMath.getRightHandSideNormal(vHeading);
		 }else{
			 System.out.println("the length of the heading vecotr is zero.");
		 }
	}

	
	public double getMass() {
		return mass;
	}
	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public Vector2f getSide() {
		return vSide;
	}

	public void setSide(Vector2f side) {
		vSide = side;
	}
		
	/* MaxForce */
	public float getMaxForce() {
		return maxForce;
	}
	public void setMaxForce(float maxForce) {
		this.maxForce = maxForce;
	}
	
	/* MaxSpeed */
	public float getMaxSpeed() {
		return maxSpeed;
	}
	
	public float getNormalSpeed() {
		return normalSpeed;
	}

	public void setNormalSpeed(float normalSpeed) {
		this.normalSpeed = normalSpeed;
	}

	/* MaxTurnAngle */
	public double getMaxTurnAngle(){
		return maxTurnAngle;	
	}
	
	public void setMaxTurnAngle(float maxTurnAngle){
		this.maxTurnAngle = maxTurnAngle;
	}

	public float getSpeed(){
		return speed;
	}
	
	public float getSpeedSq() {
		return speed * speed;
	}
	
	public float truncateSpeed(float s){
		if(s > maxSpeed){
			return maxSpeed;
		}
		return s;
	}

	protected void truncateTurnAngle(Vector2f oldVec, Vector2f newVec){
		boolean isClockWise;
		
		if(oldVec.length() == 0){
			return;
		}
		
		isClockWise = (MyMath.crossProduct(newVec, oldVec) < 0)? true: false;
		double angle = Math.toDegrees(oldVec.angle(newVec));
		
		/* Vibration Avoidance */
		if(lastTurnDirection != isClockWise){
			if(angle + lastAngle < vibrateThreshold){
				vVelocity.scale(speed, vHeading);
				
				lastAngle = angle;
				lastTurnDirection = isClockWise;
				
				return;
			}
		}
		
		
		/* Max turning angle constraint */
		if(angle > maxTurnAngle){
			if(isClockWise){
				turnRight(maxTurnAngle);
			}else{
				turnLeft(maxTurnAngle);
			}
		}else{
			if(isClockWise){
				turnRight((float)angle);
			}else{
				turnLeft((float)angle);
			}
		}
		
		lastAngle = angle;
		lastTurnDirection = isClockWise;
		
	}
	
	
	public void truncateForce(Vector2f steeringforce) {
		float length = steeringforce.length();
		
		if (length > maxForce) {
			if(length > TOLERANT_ERR){
				steeringforce.normalize();
				steeringforce.scale(maxForce);
			}
		}
	}
	
	public void truncateVelocity(Vector2f vel){
		float length = vel.length();
		
		if(length > maxSpeed){
			if(length > TOLERANT_ERR){
				vel.normalize();
				vel.scale(maxSpeed);
			}
		}
	}
	 
	 
	/***************************************************************************
	 * Using cross product to compute the relative direction of relV based on baseV. 
	 * return value: 
	 * 				 1, clockwise 
	 * 				-1, counterclockwise
	 **************************************************************************/
	private int relativeDirection(Vector2f baseV, Vector2f relV){
		if(relV.x* baseV.y > relV.y * baseV.x)
			return -1;				//counterclockwise
		else 
			return 1;				//clockwise
	}	
		
	
	/****************************************************************************************
	 *  try to rotate the heading vector to the given target within the turn rate constraint,
	 *  return value:
	 *  		 true, heading vector is facing the target.
	 *  		false, heading vector hasn't face to the target. 
	 *****************************************************************************************/	
 	 public boolean rotateHeadingToFacePosition(Vector2f target)
 	  {
 		 Vector2f toTarget = new Vector2f(target); 
 		 toTarget.sub(this.vPosition);
 		 
 		 if(toTarget.length() > TOLERANT_ERR){
 			 toTarget.normalize();
 		 }else{
 			 toTarget.set(0,0);
 		 }
 		 
 		 
 		 float angle = (float)Math.acos(vHeading.dot(toTarget));

 		 if (angle < 0.00001) return true;		

 		 //	  clamp the amount to turn to the max turn rate
 		 if (angle > maxTurnAngle) angle = maxTurnAngle;

 		 Matrix3f rotationMatrix = new Matrix3f();
 		 rotationMatrix.setIdentity();
 		 rotationMatrix.rotZ(angle * relativeDirection(vHeading,toTarget));

 		 Vector3f head = new Vector3f(vHeading.x, vHeading.y, 0);
 		 rotationMatrix.transform(head);
 		 vHeading.x = head.x;
 		 vHeading.y = head.y;

 		 Vector3f vel = new Vector3f(vVelocity.x, vVelocity.y, 0);
 		 rotationMatrix.transform(vel);
 		 vVelocity.x = vel.x;
 		 vVelocity.y = vel.y;
	  	
 		 vSide = MyMath.getRightHandSideNormal(vHeading); 		 
 		 return false;		//hasn't face to the target yet.
 	  }
	  
}
