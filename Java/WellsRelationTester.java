import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.Scanner;

import com.jcraft.jsch.*;

/**
 * @author Michael Wells
 * 
 *         Program to test the WellsRelationSolver class. Performs tests by
 *         using a library called JSch to log into andrew.cs.fit.edu to access
 *         Dr. Gallagher's test generation programs.
 */
public class WellsRelationTester {
	public static enum Program {
		REFLEXIVE("reflex"), ONTO("onto"), ONE_TO_ONE("onetoone"), TRANSITIVE("trans"), FUNCION("func"), EQUALITY("eq"), SYMETRIC(
				"sym"), SYMETRIC_TRANSITIVE("sym.trans"), REFLEXIVE_SYMETRIC("ref.sym"), TESTGEN("testgen");

		public final String name;

		private Program(String name) {
			this.name = name;
		}

		public boolean chkRel(WellsRelation rel) {
			if (this == Program.ONE_TO_ONE)
				return WellsRelationSolver.isOneToOne(rel);
			else if (this == Program.REFLEXIVE)
				return WellsRelationSolver.isReflexive(rel);
			else if (this == Program.ONTO)
				return WellsRelationSolver.isOnto(rel);
			else if (this == Program.TRANSITIVE)
				return WellsRelationSolver.isTransitive(rel);
			else if (this == Program.FUNCION)
				return WellsRelationSolver.isFunction(rel);
			else if (this == Program.EQUALITY)
				return WellsRelationSolver.isSymetric(rel) && WellsRelationSolver.isTransitive(rel)
						&& WellsRelationSolver.isReflexive(rel);
			else if (this == Program.SYMETRIC)
				return WellsRelationSolver.isSymetric(rel);
			else if (this == Program.SYMETRIC_TRANSITIVE)
				return WellsRelationSolver.isSymetric(rel) && WellsRelationSolver.isTransitive(rel);
			else if (this == Program.REFLEXIVE_SYMETRIC)
				return WellsRelationSolver.isSymetric(rel) && WellsRelationSolver.isReflexive(rel);
			else if (this == Program.TESTGEN) {
				System.out.println("Generated randomized test relation:");
				System.out.println(rel);
				WellsRelationSolver.printData(rel);
				return true; // Always pass this test since we can't
								// automatically test it.
			}
			return false;
		}
	}

	public static class TesterThread implements Runnable {
		private String input;
		private Program type;
		private boolean pass = false;

		public TesterThread(String input, Program type) {
			this.input = input;
			this.type = type;
		}

		@Override
		public void run() {
			Scanner relationScan = new Scanner(new ByteArrayInputStream(input.getBytes()));
			WellsRelation rel = WellsRelationSolver.slurpRelation(relationScan);
			pass = type.chkRel(rel);
			if (!pass)
				System.err.println(rel);
		}

		public boolean passed() {
			return pass;
		}
	}

	public static class MonitorThread implements Runnable {
		private Thread subject;
		private TesterThread tester;
		public final LinkedList<Long> cpuTime = new LinkedList<>();
		public final LinkedList<Long> allocatedRam = new LinkedList<>();

		public MonitorThread(String input, Program type) {
			tester = new TesterThread(input, type);
			subject = new Thread(tester);
		}

		@Override
		public void run() {
			long lastCheck = System.currentTimeMillis();

			subject.start();
			while (true) {
				// Only collect data once every half second.
				if (lastCheck + 500 < System.currentTimeMillis())
					continue;

				long cpu = ManagementFactory.getThreadMXBean().getThreadCpuTime(subject.getId());

				if (cpu < 0)
					break;
				cpuTime.add(cpu);
				allocatedRam.add(Runtime.getRuntime().totalMemory());
				lastCheck = System.currentTimeMillis();
			}
		}

		public boolean passed() {
			return tester.passed();
		}
	}

	private static int scale = 10, iterations = 25;
	private static long[] averageCpu = new long[iterations];
	private static long[] averageRam = new long[iterations];
	private static int averageCount = 0;

	public static void main(String... cheese) throws JSchException, IOException, InterruptedException {
		String user, pass, host = "andrew.cs.fit.edu"; // Default host.
		WellsShellInterface shell;
		Scanner scanner = new Scanner(System.in);

		if (cheese.length == 0) {
			System.out.println("Usage: java WellsRelationTester <username>[@<host>] [<password>]");
			return;
		}

		// Check to see if a server was provided. If not, assume andrew.
		user = cheese[0];
		if (user.contains("@")) {
			host = user.substring(user.indexOf('@') + 1);
			user = user.substring(0, user.indexOf('@'));
		}

		if (cheese.length < 2) {
			System.out.println("Enter password for " + user + "@" + host + ": ");
			pass = scanner.nextLine();
			scanner.close();
		} else
			pass = cheese[1];

		shell = new WellsShellInterface(user, host, pass);
		shell.connect();
		shell.writeCmd("cd ../kgallagher/public_html/sampleprogs");

		/*
		 * while (true) {
		 * System.out.println("Enter: <test> [<universe size> [<relations>]]");
		 * System.out.print("Available tests: auto"); for (Program prog :
		 * Program.values()) System.out.print(", " + prog.name()); String prog =
		 * scanner.next(); break; }
		 */

		int totalPassed = 0, totalTests = 0;
		for (Program prog : Program.values()) {
			if (prog == Program.TESTGEN)
				continue;
			System.out.print("Testing " + prog + " with universes of size " + scale + "*i for i=1 to " + iterations
					+ "... ");
			int passed = 0, tested = 0;
			for (int i = scale; i <= scale * iterations; i += scale) {
				tested += 1;
				if (runTest(shell, prog, i, i))
					passed += 1;
			}
			averageCount += 1;
			totalPassed += passed;
			totalTests += tested;
			System.out.println(passed + "/" + tested + " PASSED");
		}
		System.out.println("Testing complete. " + totalPassed + "/" + totalTests + " PASSED");

		// Print out RAM and CPU usage.
		System.out.println("Average CPU time and RAM usage for tests by universe size:");
		for (int i = 0; i < iterations; i++)
			// CPU Time in nanosecond scale, RAM in byte scale. Convert to %CPU
			// and mb of RAM.
			System.out.println("Universe of " + ((i + 1) * scale) + ": " + (averageCpu[i] / (500 * 1000 * 1000))
					+ "% CPU and " + (averageRam[i] / 1024) + "mb RAM.");

		System.out.println("Exiting program in 5 seconds...");
		Thread.sleep(5000);
		System.exit(0);
	}

	// Programs: onetoone, relfex, onto, trans, func, eq, sym, sym.trans,
	// ref.sym, testgen

	/**
	 * Runs a test with a given relation.
	 * 
	 * @param type
	 * @param universeSize
	 * @param relations
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean runTest(WellsShellInterface shell, Program type, int universeSize, int relations)
			throws IOException, InterruptedException {
		StringBuilder data = new StringBuilder();

		shell.flush();
		shell.writeCmd("./" + type.name + " " + universeSize + " " + relations);

		Scanner remoteScan = new Scanner(shell.getReader());
		remoteScan.useDelimiter("[^0-9\\.]+"); // Skip non-numeric data like
												// control codes or echos.

		while (!remoteScan.hasNextInt())
			remoteScan.next();

		// Read in relation from shell.
		data.append(remoteScan.nextInt());
		while (remoteScan.hasNextInt())
			data.append("\n" + remoteScan.nextInt() + " " + remoteScan.nextInt());

		MonitorThread monitor = new MonitorThread(data.toString(), type);
		Thread thread = new Thread(monitor);
		thread.start();
		thread.join(); // Wait for thread to finish collecting data.

		// Collect performance data.
		int index = universeSize / scale - 1;
		long totalCpu = 0;
		for (long cpu : monitor.cpuTime)
			totalCpu += cpu;
		if (monitor.cpuTime.size() > 0)
			averageCpu[index] = ((averageCount * averageCpu[index]) + totalCpu / monitor.cpuTime.size())
					/ (averageCount + 1);

		long totalRam = 0;
		for (long ram : monitor.allocatedRam)
			totalRam += ram;
		if (monitor.allocatedRam.size() > 0)
		averageRam[index] = ((averageCount * averageRam[index]) + totalRam / monitor.allocatedRam.size())
				/ (averageCount + 1);

		return monitor.passed();
	}
}
