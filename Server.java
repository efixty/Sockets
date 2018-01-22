import java.net.Socket;
import java.net.ServerSocket;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class Server {

	private static ServerSocket server;
	private static Socket client;
	private static List<Integer> ids;
	private static List<String> nicknames;
	private static Map<Integer, Socket> clients;

	public static String message;

    public static void main(String[] args) throws Exception {
		clients = new TreeMap<Integer, Socket>();
		ids = new ArrayList<Integer>();
		nicknames = new ArrayList<String>();
    	server = new ServerSocket(2222);
    	try{
    		while(true) {
    			client = server.accept();
    			// System.out.println(client);
				// new Thread(new InputHandlerOnServer(client)).start();
				// new Thread(new OutputHandlerOnServer(client)).start();
				new Thread(new ClientConnector(client)).start();
			}

    	} catch (Exception e) {
    		System.out.println("Something went wrong: server");
    		e.printStackTrace();
    	}
	}

	/*private static boolean checkClient(Socket client) {
		Iterator iterator = clients.iterator();
		while(iterator.hasNext()) {
			if(client.equals(iterator.next()))
				return true;
		}
		return false;
	}*/

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

	public synchronized static void addClient(int id, Socket client) {
		clients.put(id, client);
	}

	public synchronized static StringBuffer showClients() {
		StringBuffer clients_s = new StringBuffer();
		Set<Integer> ids = clients.keySet();
		Iterator iterator = ids.iterator();
		while(iterator.hasNext()){
			int id = (int) iterator.next();
			clients_s.append(String.format("The id of %s is %d", clients.get(id), id));
			if(iterator.hasNext())
				clients_s.append("\n");
		}
		return clients_s;
	}
	public static boolean isValidId(int id) {
		Set<Integer> ids = clients.keySet();
		Iterator iterator = ids.iterator();
		while(iterator.hasNext()){
			if((int)iterator.next() == id) return false;
		}
		return true;
	}

	public static Map<Integer, Socket> getClients() {
		return clients;
	}
}

class ClientConnector implements Runnable {
	private Socket client;
	private BufferedReader reader;
	private InputStream in;
	private PrintWriter pw;
	private Scanner sc;
	private OutputStream out;
	private int client2Id;
	private int client1Id;

	public ClientConnector(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try{
			out = client.getOutputStream();
		} catch(Exception e) {
			System.out.println("Problem getting OutputStream");
		}
		pw = new PrintWriter(out, true);
		sc = new Scanner(System.in);
		try{
			in = client.getInputStream();
		} catch(Exception e) {
			System.out.println("Problem getting InputStream");
		}
		reader = new BufferedReader(new InputStreamReader(in));
		try{
			client1Id = Integer.parseInt(reader.readLine());
			while(!Server.isValidId(client1Id)) {
				pw.println("Client with this id already exists, enter another one");
				client1Id = Integer.parseInt(reader.readLine());
			}
			Server.addClient(client1Id, client);
		} catch (Exception e) {
			System.out.println("Couldn't add an item with id");
			e.printStackTrace();
		} 
		pw.println(Server.showClients());
		pw.println("Choose someone by id to chat with");
		try {
			client2Id = Integer.parseInt(reader.readLine());
		} catch (Exception e) {
			System.out.println("Exception while getting chatmateId");
			e.printStackTrace();
		}
		// System.out.println(Server.showClients());
		// new Thread(new InputHandlerOnServer(client1Id)).start();
		new Thread(new InputHandlerOnServer(client2Id)).start();
		// new Thread(new OutputHandlerOnServer(client2Id)).start();
		new Thread(new OutputHandlerOnServer(client1Id)).start();
	}

}

class InputHandlerOnServer implements Runnable {

	private Socket client;
	private BufferedReader reader;
	private InputStream in;
	private int clientId;

	InputHandlerOnServer(int clientId) {
		this.clientId = clientId;
		this.client = Server.getClients().get(clientId);
		try{
			in = client.getInputStream();
		} catch(Exception e) {
			System.out.println("Problem getting InputStream");
		}
		reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public void run() {
		try {
			while(true) {
				String input;
				if((input = reader.readLine()) != null)
					// System.out.println(input);
					Server.message = input;
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
	private int clientId;

	OutputHandlerOnServer(int clientId) {
		this.clientId = clientId;
		this.client = Server.getClients().get(clientId);
		try{
			out = client.getOutputStream();
		} catch(Exception e) {
			System.out.println("Problem getting OutputStream");
			e.printStackTrace();
		}
		pw = new PrintWriter(out, true);
		sc = new Scanner(System.in);
	}

	@Override
	public void run() {
		try {
			while(true) {
				String output;
				/*if((output = sc.nextLine()) != null)*/
				if((output = Server.message) != null) {
					pw.println(output);
					Server.message = null;
				}
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