 package ab.objtracking;
 import java.util.LinkedList;
 import java.util.Random;
 
 public class ABStableMarriageAlgo
 {
	     // Number of objs
	     private int n ;
	 
	     // Preference tables (size n x |prefs|)
	     private int[][] manPref;
	     private int[][] womanPref;
	 
	     private static final boolean DEBUGGING = false;
	     private Random rand = new Random();
	 
	     /**
      * Creates and solves a random stable marriage problem of
      * size n, where n is given on the command line.
      */
	     public static void main(String[] args1) {
	    	String[] args = {"8"};
		 	if (args.length != 1) {
			 	    System.out.println("Usage: java StableMarriage n");
			 	    return;
			 	}
		 	int n = Integer.parseInt(args[0]);
		 	ABStableMarriageAlgo sm = new ABStableMarriageAlgo(n);
		 	if (n <= 10)
			 	    sm.printPrefTables();
		 	int[] marriage = sm.stable();
		 	if (n <= 10)
			 	    sm.printMarriage(marriage);
		     }
	 
	     /**
      * Creates a marriage problem of size n with random preferences.
      */
	     public ABStableMarriageAlgo(int n) {
		 	this.n = n;
		 	manPref = new int[n][];
		 	womanPref = new int[n][];
		 	for (int i = 0; i < n; i++) {
			 	    manPref[i] = new int[n];
			 	    createRandomPrefs(manPref[i]);
			 	    womanPref[i] = new int[n];
			 	    createRandomPrefs(womanPref[i]);
			 	}
		     }
	 
	     /**
      * Puts the numbers 0 .. v.length   1 in the vector v
      * in random order.
      */
	     private void createRandomPrefs(int[] v) {
		 	// Create a vector with the values 0, 1, 2, ...
		 	for (int i = 0; i < v.length; i++)
			 	    v[i] = i;
		 	// Create a random permutation of this vector.
		 	for (int i = v.length - 1; i > 0; i--  ) {
			 	    // swap v[i] with a random element v[j], j <= i.
			 	    int j = rand.nextInt(i+1);
			 	    int temp = v[i];
			 	    v[i] = v[j];
			 	    v[j] = temp;
			 	}
		     }
	 
	     /**
      * Returns a stable marriage in the form an int array v,
      * where v[i] is the man married to woman i.
      */
	     public int[] stable() {
		 	// Indicates that woman i is currently engaged to
		 	// the man v[i].
		 	int[] current = new int[n];
		 	final int NOT_ENGAGED =  1;
		 	for (int i = 0; i < current.length; i++)
			 	    current[i] = NOT_ENGAGED;
		 
		 	// List of men that are not currently engaged.
		 	LinkedList<Integer> freeMen = new LinkedList<Integer>();
		 	for (int i = 0; i < current.length; i++)
			 	    freeMen.add(i);
		 
		 	// next[i] is the next woman to whom i has not yet proposed.
		 	int[] next = new int[n];
		 
		 	//computeRanking();
		 	while (!freeMen.isEmpty()) {
			 	    int m = freeMen.remove();
			 	    int w = manPref[m][next[m]];
			 	    next[m]++;
			 	    printDebug("m=" + m + " w=" + w);
			 	    if (current[w] == NOT_ENGAGED) {
				 		current[w] = m;
				 	    } else {
					 		int m1 = current[w];
					 		if (prefers(w, m, m1)) {
						 		    current[w] = m;
						 		    freeMen.add(m1);
						 		} else {
							 		    freeMen.add(m);
							 		}
					 	    }	    
			 	}
		 	return current;	
		     }
	 
	     /**
      * Returns true iff w prefers x to y.
      */
	     private boolean prefers(int w, int x, int y) {
		 	for (int i = 0; i < n; i++) {
			 	    int pref = womanPref[w][i];
			 	    if (pref == x)
				 		return true;
			 	    if (pref == y)
				 		return false;	    
			 	}
		 	// This should never happen.
		 	System.out.println("Error in womanPref list " + w);
		 	return false;
		     }
	 
	     public void printPrefTables() {
		 	System.out.println("manPref:");
		 	printMatrix(manPref);
		 	System.out.println("womanPref:");
		 	printMatrix(womanPref);
		     }
	 
	     private void printMarriage(int[] m) {
		 	System.out.println("Married couples (woman + man): ");
		 	for (int i = 0; i < m.length; i++)
			 	    System.out.println(i + " + " + m[i]);
		     }
	 
	     private void printDebug(String s) {
		 	if (DEBUGGING) {
			 	    System.out.println(s);
			 	}
		     }
	 
	     /**
      * Prints the matrix v.
      */
	     private void printMatrix(int[][] v) {
		 	if (v == null) {
			 	    System.out.println("<null>");
			 	    return;
			 	}
		 	for (int i = 0; i < v.length; i++) {
			 	    for (int j = 0; j < v[i].length; j++)
				 		System.out.print(v[i][j] + " ");
			 	    System.out.println();
			 	}
		     }
	 }
