package ab.vision;

import java.util.LinkedList;
import java.util.Random;

@SuppressWarnings("serial")
public class ABList extends LinkedList<ABObject> {
	Random r = null;
	public static ABList newList()
	{
		return new ABList();
	}
	public ABObject random()
	{
		if(r == null)
			r = new Random();
		if(isEmpty())
			return null;
		else
			return this.get(r.nextInt(size()));
	}
}
