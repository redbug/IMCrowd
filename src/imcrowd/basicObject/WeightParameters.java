package imcrowd.basicObject;


public class WeightParameters {
	public static final float  SteeringForceTweaker = 5.0f;
	  	
	public static final int
	Max_wSeek = 			1,
	Max_wFlee = 			1,
	Max_wArrive = 			5,
	Max_wPursuit = 			1,
	Max_wWander			=	1,
	Max_wOffsetPursuit =	1,
	Max_wInterpose =		1,
	Max_wHide =				1,
	Max_wEvade =			1,
	Max_wFollowPath 	=		1,
	Max_wSeparation 	=		15,
	Max_wCohesion 		=		15,
	Max_wAlignment 		= 		15,
	Max_wActive			= 		10,
	Max_wObAvoidance	=		3,
	Max_wTowardCA		=		9,
	Max_Force			=		100,
	Max_NormalSpeed		=		25,
	Max_ViewRadius		=		200,
	Max_ViewTheta		=		360;
	
	
	private WeightParameters(){}
}
