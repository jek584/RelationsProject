import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class WellsShellInterface {
	// Class for info prompts.
	public static class MyUserInfo implements UserInfo {
		private Scanner scanner = new Scanner(System.in);

		public String getPassword() {
			return null;
		}

		public boolean promptYesNo(String message) {
			System.out.println(message + " (yes/no)");

			char ans = scanner.next().toLowerCase().charAt(0);

			if (ans == 'y')
				return true;
			if (ans == 'n')
				return false;
			return this.promptYesNo(message);
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return false;
		}

		public boolean promptPassword(String message) {
			return false;
		}

		public void showMessage(String message) {
			System.out.println(message);
		}
	}

	public class WellsShellRepeater implements Runnable {
		private PipedOutputStream repeatOut;
		private PipedInputStream repeatIn;
		private boolean outputFinished = false;
		private boolean isInputLine = false;
		private String line = "";

		public WellsShellRepeater(PipedInputStream connectIn, PipedOutputStream connectOut) {
			this.repeatIn = connectIn;
			this.repeatOut = connectOut;
		}

		@Override
		public void run() {
			while (true) {
				try {
					// Echo out anything the server sends us to standard error.
					int data = repeatIn.read();

					line += "" + (char) data;

					// Filter out prompt data. (eg, wellsm2011@andrew: ~$)
					if ((char) data == '$') {
						outputFinished = true;
						isInputLine = true;
						print(".\n");
					} else if ((char) data == '\n') {
						if (!isInputLine)
							print(line);
						isInputLine = false;
						line = "";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void print(String str) throws IOException {
			if (REPEAT_SHELL_OUTPUT)
				System.err.print(str);

			// Pipe data further down for use in the program.
			for (char i : str.toCharArray())
				repeatOut.write((int) i);
		}

		public boolean isOutputFinished() {
			if (outputFinished) {
				outputFinished = false;
				return true;
			}
			return false;
		}
	}

	public static final boolean REPEAT_SHELL_OUTPUT = false;
	public static final boolean REPEAT_FLUSHED_OUTPUT = false;

	private Session session;
	private BufferedReader reader;
	private PipedOutputStream writer;
	private String username;
	private String host;
	private String pass;
	private WellsShellRepeater repeater;

	public WellsShellInterface(String user, String host, String pass) throws JSchException {
		this.username = user;
		this.host = host;
		this.pass = pass;
	}

	/**
	 * Attempts to connect to the shell with the information obtained by the
	 * constructor.
	 * 
	 * @throws JSchException
	 *             If the connection is rejected.
	 * @throws IOException
	 *             If the network fails.
	 */
	public void connect() throws JSchException, IOException {
		session = new JSch().getSession(username, host, 22);
		session.setPassword(pass);
		UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);

		System.out.println("Logging in as " + username + "@" + host + "...");

		// Disable key checking.
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect(30000); // making a connection with timeout 30s.
		Channel channel = session.openChannel("shell");

		// Pipes for shell.
		PipedOutputStream shellOut = new PipedOutputStream();
		PipedInputStream shellIn = new PipedInputStream();

		// Pipe for program.
		PipedOutputStream progOut = new PipedOutputStream(shellIn);
		PipedInputStream progIn = new PipedInputStream();

		connectShellOutput(shellOut, progIn);

		// Program reader and writers.
		reader = new BufferedReader(new InputStreamReader(progIn));
		writer = progOut;

		channel.setInputStream(shellIn);
		channel.setOutputStream(shellOut);

		channel.connect(3000);

		System.out.println(reader.readLine());
	}

	private void connectShellOutput(PipedOutputStream shellOut, PipedInputStream progIn) throws IOException {
		// Pipes for repeater.
		PipedOutputStream repeatOut = new PipedOutputStream(progIn);
		PipedInputStream repeatIn = new PipedInputStream(shellOut);

		// Creates a new repeater to parse input and echo it to stdout if needed.
		repeater = new WellsShellRepeater(repeatIn, repeatOut);
		Thread repeaterThread = new Thread(repeater);
		repeaterThread.setDaemon(true);
		repeaterThread.setName("Repeater/parser for shell thread.");
		repeaterThread.start();
	}

	public BufferedReader getReader() {
		return reader;
	}

	/**
	 * Flushes the input stream, removing any excess data.
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException {
		while (reader.ready()) {
			if (REPEAT_FLUSHED_OUTPUT)
				System.err.print((char) reader.read());
			else
				reader.read();
		}
	}

	public void write(String txt) throws IOException {
		writer.write(txt.getBytes());

		// Wait for output to finish (searches for a $).
		while (!repeater.isOutputFinished())
			;
	}

	public void writeCmd(String txt) throws IOException {
		write(txt + "\n");
	}
}