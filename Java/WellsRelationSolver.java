import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Michael Wells
 * 
 *         Simple program to read in and evaluate relations. Able to evaluate
 *         relations as being onto, reflexive, symetric, transitive, one to one,
 *         and functional. If a relation is symetric, transitive and reflexive,
 *         this program is also able to output the groupings of elements found.
 */
public class WellsRelationSolver {
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		try {
			WellsRelation rel;

			Scanner scanner;
			// If an argument is passed, try to open a file by that name.
			// Otherwise, read from stdin.
			if (args.length < 1) {
				System.out.println("Input universe size followed by relation pairs teminated with a period: ");
				scanner = new Scanner(System.in);
			} else
				scanner = new Scanner(new File(args[0]));

			// Accept tons of crazy input. Terminate on a period.
			scanner.useDelimiter("[^0-9\\.]+");

			rel = slurpRelation(scanner);
			if (rel == null)
				return;

			if (args.length > 1)
				System.out.println(rel);
			printData(rel);

			//System.out.println("Exiting program in 5 seconds...");
			//Thread.sleep(5000);
			System.exit(0);
		} catch (InputMismatchException ex) {
			System.out.println("Invalid input detected. Relation input must be numeric.");
		}
	}

	/**
	 * Prints out the results of a relation to standard out.
	 * 
	 * @param rel
	 */
	public static void printData(WellsRelation rel) {
		//boolean isFunction = isFunction(rel);
		boolean isFunction = false;
		boolean isReflexive = isReflexive(rel);
		boolean isOneToOne = false;
		boolean isOnto = false;
		boolean isSymetric = isSymetric(rel);
		boolean isTransitive = isTransitive(rel);

		System.out.println("One to One: " + isOneToOne);
		System.out.println("Onto: " + isOnto);
		System.out.println("Reflexive: " + isReflexive);
		System.out.println("Symetric: " + isSymetric);
		System.out.println("Transitive: " + isTransitive);
		if (isFunction) {
			System.out.println("Function One to One: " + isOneToOne);
			System.out.println("Function Onto: " + isOnto);
		}
		if (isReflexive && isSymetric && isTransitive) {
			System.out.println("Group Mapping: ");
			printSections(rel);
		}
	}

	/**
	 * Inputs relation data from a scanner and stores it in the relation.
	 * 
	 * @param input
	 * @param rel
	 */
	public static WellsRelation slurpRelation(Scanner input) {
		WellsRelation rel = new WellsRelation(input.nextInt());

		while (input.hasNextInt())
			rel.set(input.nextInt() - 1, input.nextInt() - 1);

		input.close();
		return rel;
	}

	/**
	 * Returns true if the function is one to one, and false otherwise.
	 * 
	 * @param rel
	 * @return
	 */
	public static boolean isOneToOne(WellsRelation rel) {
		boolean[] x = new boolean[rel.max()];
		boolean[] y = new boolean[rel.max()];

		// Ensure there is at most one connection involving each element of the
		// relation.
		for (int i = 0; i < x.length; ++i) {
			for (int j = 0; j < y.length; ++j) {
				if (rel.get(i, j)) {
					// Return false if multiple connections.
					if (x[i] || y[j])
						return false;
					x[i] = true;
					y[j] = true;
				}
			}
		}

		return true;
	}

	/**
	 * Returns true if the relation is onto for all elements.
	 * 
	 * @param rel
	 * @return
	 */
	public static boolean isOnto(WellsRelation rel) {
		// Return true if all Y in a relation (X,Y) have at least one
		// connection.
		mainLoop: for (int i = 0; i < rel.max(); ++i) {
			for (int j = 0; j < rel.max(); ++j) {
				if (rel.get(j, i))
					continue mainLoop;
			}
			// Oops, there's a row with no connections.
			return false;
		}

		return true;
	}

	/**
	 * Returns true if the relation is reflexive for all elements.
	 * 
	 * @param rel
	 * @return
	 */
	public static boolean isReflexive(WellsRelation rel) {
		// Return true iff each element relates to itself.
		for (int i = 0; i < rel.max(); ++i) {
			if (!rel.get(i, i))
				return false;
		}
		return true;
	}

	/**
	 * Returns true if the given relation is symetric.
	 * 
	 * @param rel
	 * @return
	 */
	public static boolean isSymetric(WellsRelation rel) {
		// Return true iff each connection is two-way.
		for (int i = 0; i < rel.max(); ++i) {
			for (int j = i + 1; j < rel.max(); ++j) {
				if (rel.get(i, j) != rel.get(j, i))
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the given relation is transitive for the given element.
	 * 
	 * @param rel
	 *            The relation to evaluate.
	 * @param focus
	 *            The element to evaluate.
	 * @param range
	 *            A list of all other transitive elements in this sequence.
	 * @return
	 */
	public static boolean isTransitive(WellsRelation rel, int focus, LinkedList<Integer> range) {
		// Check to make sure all previous elements are related to the focus.
		for (int i : range) {
			if (!rel.get(i, focus))
				return false;
		}

		// Loop through and check for new elements the focus is related to.
		for (int i = 0; i < rel.max(); ++i) {
			if (rel.get(focus, i) && !range.contains(i)) {
				range.add(focus);
				// Check to see if further recursion is still consistent with
				// transitivity.
				if (!isTransitive(rel, i, range))
					return false;
				range.remove((Integer)i);
			}
		}

		// No inconsistencies found.
		return true;
	}

	/**
	 * Returns true if the relation is transitive for all elements.
	 * 
	 * @param rel
	 * @return
	 */
	public static boolean isTransitive(WellsRelation rel) {
		for (int i = 0; i < rel.max(); ++i) {
			for (int j = 0; j < rel.max(); ++j) {
				if (rel.get(i, j)) {
					LinkedList<Integer> range = new LinkedList<Integer>();
					range.add(i);

					if (!isTransitive(rel, j, range))
						return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns true if the given relation is a function.
	 * 
	 * @param rel
	 * @return
	 */
	public static boolean isFunction(WellsRelation rel) {
		for (int i = 0; i < rel.max(); ++i) {
			boolean found = false;
			// Search for exactly one pair in each row i.
			for (int j = 0; j < rel.max(); ++j) {
				if (rel.get(i, j)) {
					if (found)
						return false;
					found = true;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	/**
	 * Prints out all of the sections of equivalence in a relation that is
	 * symmetric, reflexive and transitive.
	 * 
	 * @param rel
	 */
	public static void printSections(WellsRelation rel) {
		boolean[] used = new boolean[rel.max()];
		int sectionCount = 0;

		// Start with the first number and print all other numbers connected to
		// it.
		// Then, move on to the second, third, etc. and print out connections
		// iff they are not
		// connected to an already printed number.
		for (int i = 0; i < rel.max(); ++i) {
			// Already printed this set.
			if (used[i])
				continue;

			int lineCount = 1;
			// 10 groups max
			if (sectionCount++ <= 10)
				System.out.print(i + 1);

			for (int j = i + 1; j < rel.max(); ++j) {
				if (rel.get(i, j)) {
					used[j] = true;
					// Only print 20 numbers per group and 10 total groups max.
					if (lineCount++ <= 20 && sectionCount <= 10)
						// Convert back to base 1 array type.
						System.out.print(", " + (j + 1));
				}
			}
			// Print out how many numbers in the group were hidden.
			if (lineCount > 20)
				System.out.print("... [" + (lineCount - 20) + " more nodes]");
			if (sectionCount <= 10)
				System.out.println();
		}
		// Print out how many groups were hidden.
		if (sectionCount > 10)
			System.out.println("\n... [" + (sectionCount - 10) + " more sets]");
	}
}
