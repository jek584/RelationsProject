import java.util.LinkedList;

/**
 * @author Michael Wells
 * 
 *         Data structure to store relation data. I feel like there's a more
 *         computationally efficient method with linked lists in a hashtable,
 *         but I think that would be less RAM efficient. I decided that solving
 *         large problems slowly was better than not solving them at all, so
 *         I've also added in a linked list method of solving the problem that
 *         kicks in only if there isn't enough RAM for the double array.
 */
public class WellsRelation {
	public class Pair<T, U> {
		public final T x;
		public final U y;

		public Pair(T f, U s) {
			x = f;
			y = s;
		}
	}

	// Linked list requires more processing power, but array takes more RAM.
	private boolean[][] rel;
	private LinkedList<Pair<Integer, Integer>> relLst = new LinkedList<>();
	private int max;

	/**
	 * Creates a new relation object.
	 * 
	 * @param max The maximum size of the universe.
	 */
	public WellsRelation(int max) {
		this.max = max;
		try {
			// Use double array unless we can't allocate enough RAM for it.
			rel = new boolean[max][max];
		} catch (OutOfMemoryError ex) {
			System.out.println("Error allocating RAM for array storage. Switching to list storage. (very slow)");
			rel = null;
		}
	}

	/**
	 * Returns true if x is related to y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean get(int x, int y) {
		if (rel != null)
			return rel[x][y];
		
		// This gets massively slow for large universes. Goes for linear time in array storage
		// to x^2 time in list storage. Oh well, at least we can handle it (slowly).
		for (Pair<Integer, Integer> pair : relLst) {
			if (pair.x == x && pair.y == y)
				return true;
		}
		return false;
	}

	/**
	 * Sets x as being related to y.
	 * 
	 * @param x
	 * @param y
	 */
	public void set(int x, int y) {
		set(x, y, true);
	}

	
	/**
	 * Sets the relation from x to y to value.
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	public void set(int x, int y, boolean value) {
		if (x > max() || y > max())
			System.out.println("Error: Trying to set (" + x + "," + y + "), but universe is size " + max()
					+ ". Skipping value.");
		else if (rel != null)
			rel[x][y] = value;
		else
			relLst.add(new Pair<Integer, Integer>(y, x));
	}

	/**
	 * Returns the maximum universe size.
	 * 
	 * @return
	 */
	public int max() {
		return max;
	}

	/**
	 * Returns a string representation of the relation.
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		// Stringify each relation, starting with low values for x and y.
		for (int i = 0; i < rel.length; ++i) {
			for (int j = 0; j < rel.length; ++j) {
				if (rel[i][j])
					builder.append((i + 1) + ", " + (j + 1) + "\n");
			}
		}
		return builder.toString();
	}
}