import java.net.Socket;
import java.net.ServerSocket;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class Server {

	public static ArrayList<Socket> clients;
	private static ServerSocket server;
	private static Socket client;
	private static int counter = 0;

    public static void main(String[] args) throws Exception {
		
    	clients = new ArrayList<>();
    	server = new ServerSocket(8080, 1);
    	try{
	    	while(true){
    			client = server.accept();
    			System.out.println(client);
    			clients.add(client);
	    		// new Thread(new MyRunnable(), "" + counter).start();
			}			
    	} catch (Exception e) {
    		System.out.println("Something went wrong on server");
    	}

	}
}

class MyRunnable implements Runnable {

	private static BufferedReader reader;
	private static String input;
	private static PrintWriter pw;
	private static int a, b;
	private static Socket client = Server.clients.get(Server.clients.size() - 1);

	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		  	pw = new PrintWriter(client.getOutputStream(), true);
		    if((input = reader.readLine()) != null) {
		    	a = Integer.parseInt(input.split(" ")[0]);
		    	b = Integer.parseInt(input.split(" ")[1]);
		    	System.out.println(a + b);
		    	pw.println(a + b);
		    	System.out.println("answer sent");
		    }
		} catch(Exception e) {
			System.out.println("Something went wrong in thread");
			e.printStackTrace();
		} finally {
			pw.flush();
			pw.close();
			try {
				reader.close();
			} catch(Exception e) {
				System.out.println("Couldn't close Server's reader");
			}
		}
	}
}