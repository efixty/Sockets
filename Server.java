import java.net.Socket;
import java.net.ServerSocket;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class Server {

	private static ServerSocket server;
	private static Socket client;

    public static void main(String[] args) throws Exception {
		
    	server = new ServerSocket(8080);
    	try{
	    	while(true){
    			client = server.accept();
    			System.out.println(client);
	    		new Thread(new MyRunnable(client)).start();
			}			
    	} catch (Exception e) {
    		System.out.println("Something went wrong on server");
    	}

	}
}

class MyRunnable implements Runnable {

	private BufferedReader reader;
	private String input;
	private PrintWriter pw;
	private int a, b;
	private Socket client;

	MyRunnable(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		  	pw = new PrintWriter(client.getOutputStream(), true);
		    pw.println("Enter 2 numbers separated with spacebar");
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
