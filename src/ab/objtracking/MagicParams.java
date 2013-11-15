package ab.objtracking;

public final class MagicParams {
	public static final float AngleTolerance = 0.1f; // 15 degree
	public static final int VisionGap = 3;
	public static final int DiffTolerance = Integer.MAX_VALUE;
    public static final int MovementTolearance = 5; // within this value considered as static
    public static final int StrongMovementDist = 50;
    public static final int NormalMovementDist = 10;
    public static final int WeakMovementDist = 5;
    public static final int DebrisRadius = 5; // All the circles of radius smaller than 5 are considered as Debris
   
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}