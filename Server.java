import java.net.Socket;
import java.net.ServerSocket;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class Server {

	private static ServerSocket server;
	private static Socket client;
	private static List<Socket> clients;
	private static List<Integer> ids;
	private static List<String> nicknames;

    public static void main(String[] args) throws Exception {
		clients = new ArrayList<Socket>();
		ids = new ArrayList<Integer>();
		nicknames = new ArrayList<String>();
    	server = new ServerSocket(2222);
    	try{
    		while(true) {
    			do {
    				client = server.accept();	
    			} while(checkClient(client));
    			System.out.println(client);
				new Thread(new InputHandlerOnServer(client)).start();
				new Thread(new OutputHandlerOnServer(client)).start();
			}
    	} catch (Exception e) {
    		System.out.println("Something went wrong: server");
    		e.printStackTrace();
    	}

	}

	private static boolean checkClient(Socket client) {
		Iterator iterator = clients.iterator();
		while(iterator.hasNext()) {
			if(client.equals(iterator.next()))
				return true;
		}
		return false;
	}

	public synchronized static void addNickname(String nickname) {
		nicknames.add(nickname);
	}

	public synchronized static void addId(int id) {
		ids.add(id);
	}

	public static int getNicknamesSize() {
		return nicknames.size();
	}

	public static int getIdSize() {
		return ids.size();
	}
}

class InputHandlerOnServer implements Runnable {

	private Socket client;
	private BufferedReader reader;
	private InputStream in;

	InputHandlerOnServer(Socket client) {
		this.client = client;
		try{
			in = client.getInputStream();
		} catch(Exception e) {
			System.out.println("Problem getting InputStream");
		}
		reader = new BufferedReader(new InputStreamReader(in));
		String nickname;
		try{
			if((nickname = reader.readLine()) != null) {
				Server.addNickname(nickname);
			}
			Integer id;
			if((id = Integer.parseInt(reader.readLine())) != null) {
				Server.addId(id);
			}
		} catch(Exception e) {
			System.out.println("Something wrong with reading, server");
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

class OutputHandlerOnServer implements Runnable {

	private Socket client;
	private PrintWriter pw;
	private Scanner sc;
	private OutputStream out;

	OutputHandlerOnServer(Socket client) {
		this.client = client;
		try{
			out = client.getOutputStream();
		} catch(Exception e) {
			System.out.println("Problem getting OutputStream");
		}
		pw = new PrintWriter(out, true);
		sc = new Scanner(System.in);
		String output = "Please choose a nickname (String)";
		pw.println(output);
		System.out.println("sent 1st");
		while(!(Server.getNicknamesSize() == Server.getIdSize())) {
			pw.println("Please choose an id (int)");
		}

	}

	@Override
	public void run() {
		try {
			while(true) {
				String output;
				if((output = sc.nextLine()) != null)
					pw.println(output);
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