package ab.objtracking.representation;

import ab.objtracking.MagicParams;
import ab.vision.ABObject;

public class Movement {
	
	public static final int POSITIVE = 1;
	public static final int NEGATIVE = -1;
	public static final int NONSHIFT = 0;
	
	public static final int StrongMovement = 3;
	public static final int NormalMovement = 2;
	public static final int WeakMovement = 1;
	public static final int NoMovement = 0;
	
	public static final int MAX_SCOPE = Integer.MAX_VALUE;
	public static final int BOUNDING_SCOPE = 0;
	public static final int NOT_ALLOWED = -1;
	
	public int[] allowedXDirection, allowedYDirection;// value: 0: allow, -1: forbid. array[0]: allow Negative shift, array[1]: allow Non shift, array[2]: allow positive shift.
	public int movementType;
	
	public int xDirection;
	public int yDirection;
	
	public ABObject object;
	public boolean remainStatic = false;
	public boolean landMarkMovement = false; // The movement has a higher confidence to be correct
	public int correctXshift;
	public int correctYshift;
	public void setCorrectMovement(int correctXshift, int correctYshift)
	{
		this.correctXshift = correctXshift;
		this.correctYshift = correctYshift;
	}
	public void resetCorrectMovement()
	{
		this.correctXshift = xDirection;
		this.correctYshift = yDirection;
	}
	public void setAllowedXDirection(int[] allowedXDirection)
	{
		this.allowedXDirection = allowedXDirection;
	}
	public void setAllowedXDirection(int allowPositive, int allowNegative, int allowStatic)
	{
		if (allowPositive == BOUNDING_SCOPE)
		{
			allowPositive = object.getBounds().width/2;
		}
		if (allowNegative == BOUNDING_SCOPE)
		{
			allowNegative = object.getBounds().width/2;
		}

		allowedXDirection[2] = allowPositive;
		allowedXDirection[0] = allowNegative;
		allowedXDirection[1] = allowStatic;
	}
	
	public void setAllowedYDirection(int allowPositive, int allowNegative, int allowStatic)
	{
		if (allowPositive == BOUNDING_SCOPE)
		{
			allowPositive = object.getBounds().height/2;
		}
		if (allowNegative == BOUNDING_SCOPE)
		{
			allowNegative = object.getBounds().height/2;
		}
		allowedYDirection[2] = allowPositive;
		allowedYDirection[0] = allowNegative;
		allowedYDirection[1] = allowStatic;
	}
	
	public void setAllowedYDirection (int[] allowedYDirection)
	{
		this.allowedYDirection = allowedYDirection;
	}
	public Movement(ABObject object) 
	{
		super();
		this.object = object;
		allowedXDirection = new int[3];
		allowedYDirection = new int[3];
		for (int i = 0; i < 3; i++)
		{
			allowedXDirection[i] = MAX_SCOPE;
			allowedYDirection[i] = MAX_SCOPE;
		}
	}
	public Movement(int xshift, int yshift, ABObject object) {
		super();
		this.object = object;
		allowedXDirection = new int[3];
		allowedYDirection = new int[3];
		for (int i = 0; i < 3; i++)
		{
			allowedXDirection[i] = MAX_SCOPE;
			allowedYDirection[i] = MAX_SCOPE;
		}
	    setDirectionAndType(xshift, yshift);
	    
	}
	public Movement(Movement movement, ABObject object)
	{
		this.object = object;
		int count = -1;
		allowedXDirection = new int[3];
		allowedYDirection = new int[3];
		for (int scope : movement.allowedXDirection){
			count ++;
			if (scope == NOT_ALLOWED)
				allowedXDirection[count] = NOT_ALLOWED;
			else
				allowedXDirection[count] = MAX_SCOPE;
			
		}
		count = -1;
		for (int scope : movement.allowedYDirection){
			count ++;
			if (scope == NOT_ALLOWED )
				allowedYDirection[count] = NOT_ALLOWED;
			else
				allowedYDirection[count] = MAX_SCOPE;
			
		}
		movementType = movement.movementType;
	}
	public void generateInertia(ABObject obj)
	{
		int xshift = (int) (this.object.getCenterX() - obj.getCenterX());
		int yshift = (int) (this.object.getCenterY() - obj.getCenterY());
		double distance = Math.sqrt(xshift * xshift + yshift * yshift);
		int xdirection = getDirection(xshift);
		int ydirection = getDirection(yshift);
		
		movementType = getMovementType(distance);
		System.out.println(" initial " + obj + "  y " + this.object.getCenterY() + " oy: " + obj.getCenterY() +" yshift " + yshift + " y direction: " + ydirection);
		if (movementType > NormalMovement)
		{	
			if(xdirection > 0 )
				setAllowedXDirection(MAX_SCOPE, NOT_ALLOWED, MAX_SCOPE);
			else
				setAllowedXDirection(NOT_ALLOWED, MAX_SCOPE, MAX_SCOPE);
		
			if(ydirection > 0 )
				setAllowedYDirection(MAX_SCOPE, NOT_ALLOWED, MAX_SCOPE);
			else
				setAllowedYDirection(NOT_ALLOWED, MAX_SCOPE, MAX_SCOPE);
	
		} 
		else if (movementType > WeakMovement)
		{
				if(xdirection > 0 )
					setAllowedXDirection(MAX_SCOPE, BOUNDING_SCOPE, MAX_SCOPE);
				else
					setAllowedXDirection(BOUNDING_SCOPE, MAX_SCOPE, MAX_SCOPE);
			
				if(ydirection > 0 )
					setAllowedYDirection(MAX_SCOPE, BOUNDING_SCOPE, MAX_SCOPE);
				else
					setAllowedYDirection(BOUNDING_SCOPE, MAX_SCOPE, MAX_SCOPE);
		
			
		}
	}
	public void setDirectionAndType(int xshift, int yshift)
	{
		setXDirection(xshift);
		setYDirection(yshift);
		double distance = Math.sqrt(xshift * xshift + yshift * yshift);
		movementType = getMovementType(distance);
		remainStatic = (xDirection== NONSHIFT) && (yDirection == NONSHIFT);
	}
	public int getMovementType(double distance)
	{
		if (distance > MagicParams.StrongMovementDist)
			return StrongMovement;
		else
			if(distance > MagicParams.NormalMovementDist)
				return NormalMovement;
			else if (distance > MagicParams.WeakMovementDist)
					return WeakMovement;
			else
				return NoMovement;
	}
	private void setXDirection(int xshift)
	{
		this.xDirection = getDirection(xshift);
	}
	private void setYDirection(int yshift)
	{
		this.yDirection = getDirection(yshift); 	
		}
	
	private int getDirection(int shift)
	{
		if(shift > 0 )//MagicParams.MovementTolearance)
	    	 return POSITIVE;
	    else if (shift < 0 )// - MagicParams.MovementTolearance)
	    	return  NEGATIVE;
	    	else
	    		return  NONSHIFT;
	}
	
	public boolean isValidMovement(int xshift, int yshift, boolean checkMovementType)
	{
		int xDirection, yDirection;
		xDirection = getDirection(xshift);
		yDirection = getDirection(yshift);
		//System.out.println(" shift " + xshift + " " +xDirection + "  " + allowedXDirection[xDirection + 1]);
		int distance = (int) Math.sqrt(xshift * xshift + yshift * yshift);
		if(allowedXDirection[xDirection + 1] > Math.abs(xshift) && allowedYDirection[yDirection + 1] > Math.abs(yshift) && (!checkMovementType || getMovementType(distance) == movementType))
			return true;
		else
			return false;
		
	}
	
	public boolean isConflictDirection(Movement movement)
	{
		if (movement.xDirection + xDirection == 0 || movement.yDirection + yDirection == 0)
			return true;
		else
			if ( movement.remainStatic != this.remainStatic)
				return true;
			
			return false;
	}
	public boolean isConflictVelocity(Movement movement)
	{
		return movement.movementType != this.movementType;
	}
	public boolean isSameMovement(Movement movement)
	{
		return !isConflictDirection(movement) || !isConflictVelocity(movement);
	}

   public String toString()
   {
	   StringBuilder result = new StringBuilder();
	   result.append(object);

		   if(allowedXDirection[0] == -1)
			   result.append(" Unallowed Negative X ");
		   else
			   result.append(" Allowed Negative X: " + allowedXDirection[0]);
		   
		   if(allowedXDirection[1] == -1)
				   result.append(" Unallowed Static X ");
		   else
			   result.append(" Allowed Static X" );
		   
		   if(allowedXDirection[2] == -1)
			   		result.append(" Unallowed Positive X ");
		   else
			   result.append(" Allowed Positive X: " + allowedXDirection[2]);
	
		   if(allowedYDirection[0] == -1)
			   result.append(" Unallowed Negative Y ");
		   else
			   result.append(" Allowed Negative Y: " + allowedYDirection[0]);
		   if(allowedYDirection[1] == -1)
				   result.append(" Unallowed Static Y ");
		   else
			   result.append(" Allowed Static Y: ");
		   
		   if(allowedYDirection[2] == -1)
			   		result.append(" Unallowed Positive Y ");
		   else
			   result.append(" Allowed Positive Y: " + allowedYDirection[2]);
	
	   switch (movementType)
	   {
		   case StrongMovement: result.append(" Strong Movement");break;
		   case NormalMovement: result.append(" Normal Movement"); break;
		   case WeakMovement: result.append(" Weak Movement"); break;
		   default: result.append(" Unclear Movement "); break;
	   }
	   return result.toString();
   }
public static void main(String[] args) {
		boolean b1 = false;
		boolean b2 = true;
		System.out.println(b1 == b2);

	}

}
