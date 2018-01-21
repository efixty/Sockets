import java.net.Socket;
import java.net.ServerSocket;
import java.lang.*;
import java.util.*;
import java.io.*;

public class Client {

	private static Socket connection;
	private static PrintWriter pw;
	private static OutputStream out;
	private static InputStream in;
	private static BufferedReader reader;
	private static Scanner sc;

	public static void main(String[] args) {
		try {
			connection = new Socket("localhost", 2222);
			
			new Thread(new InputHandlerOnClient(connection)).start();
			new Thread(new OutputHandlerOnClient(connection)).start();

		} catch(Exception e) {
			System.out.println("Something went wrong: client");
		}
	}
}

class InputHandlerOnClient implements Runnable {

	private Socket connection;
	private BufferedReader reader;
	private InputStream in;

	InputHandlerOnClient(Socket connection) {
		this.connection = connection;
		try{
			in = connection.getInputStream();
		} catch(Exception e) {
			System.out.println("Problem getting InputStream");
		}
		reader = new BufferedReader(new InputStreamReader(in));
		String input;
		try {
			if((input = reader.readLine()).equals(null)) {
				System.out.println(input);
			}
		} catch (Exception e) {
			System.out.println("something wrong reading, client");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while(true) {
				String input;
				if((input = reader.readLine()) != null)
					System.out.println(input);
			}
		} catch (Exception e) {
			System.out.println("Exception while handling input from server");
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch(Exception e) {
				System.out.println("Couldn't close InputHandler's reader");
			}
		}
	}
}

class OutputHandlerOnClient implements Runnable {

	private Socket connection;
	private PrintWriter pw;
	private Scanner sc;
	private OutputStream out;

	OutputHandlerOnClient(Socket connection) {
		this.connection = connection;
		try{
			out = connection.getOutputStream();
		} catch(Exception e) {
			System.out.println("Problem getting OutputStream");
		}
		pw = new PrintWriter(out, true);
		sc = new Scanner(System.in);
	}

	@Override
	public void run() {
		try {
			while(true) {
				String output;
				if((output = sc.nextLine()) != null)
					if(!output.equals("END"))
						pw.println(output);
					else { connection.close(); System.exit(-116); }
			}
		} catch (Exception e) {
			System.out.println("Exception while handling output from server");
			e.printStackTrace();
		} finally {
			try {
				pw.close();
			} catch(Exception e) {
				System.out.println("Couldn't close OutputHandler's writer");
			}
		}
	}
}