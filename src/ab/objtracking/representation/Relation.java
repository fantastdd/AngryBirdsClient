package ab.objtracking.representation;

public enum Relation {
	Invalid,Invalid_1, Invalid_2,
	Unknown,
	Unassigned,
	//GR relations
	S1_S3, S1_S4, S1_S5, S1_S6, S1_S7,
	
	S2_S5, S2_S6, S2_S7,

	S3_S1, S3_S5, S3_S6, S3_S7, S3_S8,
	
	S4_S7, S4_S8, S4_S1,
	
	S5_S1, S5_S2, S5_S3, S5_S7, S5_S8,
	
	S6_S1, S6_S2, S6_S3,
	
	S7_S1, S7_S2, S7_S3, S7_S4, S7_S5,
	
	S8_S3, S8_S4, S8_S5,
	
	//GR-Poly, GR-Circle Relations: only need to specify the contact sector of the GR. treat poly as Circle
	S1, S2, S3, S4, S5, S6, S7, S8,
	
	//Boxes Relations
	Above, Under, Left, Right, Above_Left, Above_Right, Under_Left, Under_Right;
	
	public static Relation inverseRelation(Relation relation)
	{
		switch(relation)
		{
			case S1_S3: return S3_S1;
			case S1_S4: return S4_S1;
			case S1_S5: return S5_S1;
			case S1_S6: return S6_S1;
			case S1_S7: return S7_S1;
			case S2_S5: return S5_S2;
			case S2_S6: return S6_S2;
			case S2_S7: return S7_S2;
			case S3_S1: return S1_S3;
			case S3_S5: return S5_S3;
			case S3_S6: return S6_S3;
			case S3_S7: return S7_S3;
			case S3_S8: return S8_S3;
			case S4_S7: return S7_S4;
			case S4_S8: return S8_S4;
			case S4_S1: return S1_S4;
			case S5_S1: return S1_S5;
			case S5_S2: return S2_S5;
			case S5_S3: return S3_S5;
			case S5_S7: return S7_S5;
			case S5_S8: return S8_S5;
			case S6_S1: return S1_S6;
			case S6_S2: return S2_S6;
			case S6_S3: return S3_S6;
			case S7_S1: return S1_S7;
			case S7_S2: return S2_S7;
			case S7_S3: return S3_S7;
			case S7_S4: return S4_S7;
			case S7_S5: return S5_S7;
			case S8_S3: return S3_S8;
			case S8_S4: return S4_S8;
			case S8_S5: return S5_S8;
			case Above: return Under;
			case Under: return Above;
			case Left: 	return Right;
			case Right: return Left;
			case Above_Left: return Under_Right;
			case Above_Right: return Under_Left;
			case Under_Left: return Above_Right;
			case Under_Right: return Above_Left;
			case Unknown: return Unknown;
			case Invalid: return Invalid;
			case Invalid_1: return Invalid_1;
			case Invalid_2: return Invalid_2;
			default: return Unassigned;
		}
		
	}
	public static Relation getRelation(int sector1, int sector2)
	{
		switch(sector1)
		{
			case 0: 
				{
					switch(sector2)
					{
						case 2: return Relation.S1_S3;
						case 3: return Relation.S1_S4;
						case 4: return Relation.S1_S5;
						case 5: return Relation.S1_S6;
						case 6: return Relation.S1_S7;
						default: return Relation.Invalid;
					}
				}
			case 2: 
			{
				switch(sector2)
				{
					case 0: return Relation.S3_S1;
					case 4: return Relation.S3_S5;
					case 5: return Relation.S3_S6;
					case 6: return Relation.S3_S7;
					case 7: return Relation.S3_S8;
					default: return Relation.Invalid;
				}
			}
			case 4: 
			{
				switch(sector2)
				{
					case 0: return Relation.S5_S1;
					case 1: return Relation.S5_S2;
					case 2: return Relation.S5_S3;
					case 6: return Relation.S5_S7;
					case 7: return Relation.S5_S8;
					default: return Relation.Invalid;
				}
			}
			case 6: 
			{
				switch(sector2)
				{
					case 0: return Relation.S7_S1;
					case 1: return Relation.S7_S2;
					case 2: return Relation.S7_S3;
					case 3: return Relation.S7_S4;
					case 4: return Relation.S7_S5;
					default: return Relation.Invalid;
				}
			}
			case 1: 
			{
				switch(sector2)
				{
					case 4: return Relation.S2_S5;
					case 5: return Relation.S2_S6;
					case 6: return Relation.S2_S7;
					default: return Relation.Invalid;
				}
			}
			case 3: 
			{
				switch(sector2)
				{
					case 6: return Relation.S4_S7;
					case 7: return Relation.S4_S8;
					case 0: return Relation.S4_S1;
					default: return Relation.Invalid;
				}
			}
			case 5: 
			{
				switch(sector2)
				{
					case 0: return Relation.S6_S1;
					case 1: return Relation.S6_S2;
					case 2: return Relation.S6_S3;
					default:return Relation.Invalid;
				}
			}
			case 7: 
			{
				switch(sector2)
				{
					case 2: return Relation.S8_S3;
					case 3: return Relation.S8_S4;
					case 4: return Relation.S8_S5;
					default: return Relation.Invalid;
				}
			}
			default: return Relation.Invalid;
			
		}
	}
}
