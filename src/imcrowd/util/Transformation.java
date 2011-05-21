package imcrowd.util;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector2f;


public class Transformation {
	Matrix3f matrix;
	
	public Transformation(){
		matrix = new Matrix3f();
		matrix.setIdentity();
	}
	
	public void reset(){
		matrix.setIdentity();
	}
	
	void rotate(Vector2f fwd, Vector2f side)
	{
	  Matrix3f mat = new Matrix3f(
		fwd.x,  fwd.y,  0,
		side.x, side.y, 0,
		0, 		0, 		1
	  );
	  
	  matrix.mul(mat);
	}
	

	void translate(float x, float y)
	{
	  Matrix3f mat = new Matrix3f(
		1, 0, 0,
		0, 1, 0,
		x, y, 1
	  );
	  
	  matrix.mul(mat);
	}
	
	
	/*****************************************************************
	 * to apply the transformation matrix to a 2D vector.
	 *****************************************************************/
	void transformTo2DVector(Vector2f vPoint)
	{	
		float tempX = (matrix.m00 * vPoint.x) + (matrix.m10 * vPoint.y) + matrix.m20;
		float tempY = (matrix.m01 * vPoint.x) + (matrix.m11 * vPoint.y) + matrix.m21;
  
		vPoint.x = tempX;
		vPoint.y = tempY;
	}
	
	
	/*****************************************************************
	 * to apply the transformation matrix to a 2D vector.
	 *****************************************************************/
	void transformTo2DVector(Matrix3f mat, Vector2f vPoint)
	{	
		float tempX = (mat.m00 * vPoint.x) + (mat.m10 * vPoint.y) + mat.m20;
		float tempY = (mat.m01 * vPoint.x) + (mat.m11 * vPoint.y) + mat.m21;
  
		vPoint.x = tempX;
		vPoint.y = tempY;
	}
	
	
	
	/**********************************************************************
	 *	Transforms a point from the world space into agent's local space
	 **********************************************************************/
	public Vector2f pointToLocalSpace
	(Vector2f point, Vector2f agentHeading, Vector2f agentSide, Vector2f agentPosition)
	{
		Vector2f transPoint = new Vector2f(point);
	  
		Matrix3f matTransform = new Matrix3f();
		matTransform.setIdentity();

		float tx = -agentPosition.dot(agentHeading);
		float ty = -agentPosition.dot(agentSide);

		matTransform.setM00(agentHeading.x); matTransform.setM01(agentSide.x);
		matTransform.setM10(agentHeading.y); matTransform.setM11(agentSide.y);
		matTransform.setM20(tx); 			 matTransform.setM21(ty);
		
		transformTo2DVector(matTransform, transPoint);
	  return transPoint;
	}
	
	
	
	/**********************************************************************
	 *	Transforms a point from the agent's local space into world space
	 **********************************************************************/
	public Vector2f pointToWorldSpace
	(Vector2f point, Vector2f agentHeading, Vector2f agentSide, Vector2f agentPosition)
	{
		Vector2f transPoint = new Vector2f(point);

		Matrix3f matTransform = new Matrix3f();
		matTransform.setIdentity();

		rotate(agentHeading, agentSide);
		translate(agentPosition.x, agentPosition.y);
		transformTo2DVector(transPoint);
		return transPoint;
	}
	
	
	/**********************************************************************
	 *	Transforms a vector from the agent's local space into world space
	 **********************************************************************/
	public Vector2f vectorToWorldSpace
	(Vector2f vec, Vector2f agentHeading, Vector2f agentSide)
	{
		Vector2f transVec = new Vector2f(vec);
	  
		Matrix3f matTransform = new Matrix3f();
		matTransform.setIdentity();

		rotate(agentHeading, agentSide);

		transformTo2DVector(transVec);

		return transVec;
	}
	
	
}