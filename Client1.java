import java.net.Socket;
import java.net.ServerSocket;
import java.lang.*;
import java.util.*;
import java.io.*;

public class Client1 {

	private static Socket connection;
	private static PrintWriter pw;
	private static OutputStream out;
	private static InputStream in;
	private static BufferedReader reader;
	private static Scanner sc;

	public static void main(String[] args) {
		try {
			while(true){
				connection = new Socket("localhost", 8080);
				out = connection.getOutputStream();
				in = connection.getInputStream();
				pw = new PrintWriter(out, true);
				sc = new Scanner(System.in);
				String input = sc.nextLine();
				pw.println(input);
				reader = new BufferedReader(new InputStreamReader(in));
				String answer = reader.readLine();
				System.out.println(answer);
			}
		} catch(Exception e) {
			System.out.println("Something went wrong: client1");
		} finally {
			pw.flush();
			pw.close();
			try {
				reader.close();
			} catch(Exception e) {
				System.out.println("Couldn't close Client1's reader ");
			}
		}
	}
}