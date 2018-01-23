import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class Server {

	private static ServerSocket server;
	private static Socket client;
	private static List<Integer> ids;
	private static List<String> nicknames;
	private static Map<Integer, User> clients;

	public static String message;

    public static void main(String[] args) throws Exception {
		clients = new TreeMap<Integer, User>();
		ids = new ArrayList<Integer>();
		nicknames = new ArrayList<String>();
    	server = new ServerSocket(2222);
    	try{
    		while(true) {
    			client = server.accept();
    			new Thread(new ClientConnector(client)).start(); // nra hamar vor ete araji klienty 80 tarekan papi linelov anun greluc uti serveri tang 60 ropen, urish klient kpnelu vra eti chazdi
			}

    	} catch (Exception e) {
    		System.out.println("Something went wrong: server");
    		e.printStackTrace();
    	}
	}

	public synchronized static void addClient(int id, Socket client, String nickname) {
		User temp = new User(client, nickname);
		clients.put(id, temp);
	}

	public synchronized static String showClients() {
		StringBuffer clients_s = new StringBuffer();
		Set<Integer> ids = clients.keySet();
		Iterator iterator = ids.iterator();
		while(iterator.hasNext()){
			int id = (int) iterator.next();
			clients_s.append(String.format("The id of %s is %d", ((User) clients.get(id)).getNickname(), id));
			if(iterator.hasNext())
				clients_s.append("\n");
		}
		return "All connected clients are:\n" + clients_s + "\n////////////";
	}
	
	public static boolean isValidID(int id) {
		Set<Integer> ids = clients.keySet();
		Iterator iterator = ids.iterator();
		while(iterator.hasNext()){
			if((int)iterator.next() == id) return false;
		}
		return true;
	}

	public static boolean isValidNickname(String nickname) {
		Set<Integer> ids = clients.keySet();
		Iterator iterator = ids.iterator();
		while(iterator.hasNext()){
			if(clients.get(iterator.next()).getNickname().equals(nickname)) return false;
		}
		return true;
	}

	public static Map<Integer, User> getClients() {
		return clients;
	}

	public synchronized static void removeClient(int id) {
		clients.remove(id);
	}
}

class ClientConnector implements Runnable {
	private Socket client;
	private BufferedReader reader;
	private InputStream in;
	private PrintWriter pw;
	private Scanner sc;
	private OutputStream out;
	private int clientID;
	private String nickname;

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
			nickname = reader.readLine();
			while(!Server.isValidNickname(nickname)) {
				pw.println("Client with this nickname already exists, enter another one");
				nickname = reader.readLine();
			}
			do {
				clientID = new Random().nextInt(99)+1;
			} while(!Server.isValidID(clientID));
			Server.addClient(clientID, client, nickname);
		} catch (Exception e) {
			System.out.println("Couldn't add an item with id");
			e.printStackTrace();
		}
		pw.println(String.format("A new user was created with:\nNickname: %s\nID: %d\n////////////", nickname, clientID));
		pw.println(Server.showClients());
		pw.println("Choose someone to chat with. The messages must contain the actual message and the id of client separated with spacebar.\n////////////");
		new Thread(new InputHandlerOnServer(clientID)).start();
	}
}

class InputHandlerOnServer implements Runnable {

	private Socket client;
	private BufferedReader reader;
	private InputStream in;
	private int clientID;
	private boolean isStopped = false;
	private PrintWriter pw;
	private OutputStream out;

	InputHandlerOnServer(int clientID) {
		this.clientID = clientID;
		this.client = Server.getClients().get(clientID).getConnection();
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
			while(!isStopped) {
				try {
					out = client.getOutputStream();
				} catch (Exception  e) {
					e.printStackTrace();
				}
				pw = new PrintWriter(out, true);
				String input = null;
				input = reader.readLine();
				if(input == null) {
					System.out.printf("Disconnected client: removing from map (id == %d)\n", clientID);
					Server.removeClient(clientID);
					stopMe();
				} else switch(input) {
					case "list":
						pw.println(Server.showClients());
						break;

					case "END":
						Server.removeClient(clientID);
						stopMe();
						break;
					
					default:
						sendMessage(input);
						break;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception while handling input from client");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch(Exception e) {
				System.out.println("Couldn't close InputHandler's reader");
			}
		}
	}

	private void stopMe() {
		this.isStopped = true;
	}

	private int getIDFromInput(String input) {
		return Integer.parseInt(input.split(" ")[input.split(" ").length - 1]);
	}

	private String getMessageFromInput(String input) {
		StringBuffer sb = new StringBuffer();
		String[] messageArr = input.split(" ");
		for(int i = 0; i < messageArr.length - 1; i++) {
			sb.append(messageArr[i] + " ");
		}
		return sb.toString();
	}

	private void sendMessage(String input) {
		int id;
		try {
			id = getIDFromInput(input);
		} catch(NumberFormatException nfe) {
			pw.println("Bad input. Message must contain at least one number at it's end, e. g. hello 72");
			return;
		}
		if(Server.isValidID(id)) {
			pw.println("ID not found, type \"list\" to see all connected clients");
			return;
		}
		String message = getMessageFromInput(input);
		Socket client = Server.getClients().get(id).getConnection();
		try {
			out = client.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(out, true);
		try {
			pw.println("" + Server.getClients().get(clientID).getNickname() + "(" + clientID + "): " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}