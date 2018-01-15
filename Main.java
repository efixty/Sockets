import java.net.Socket;

public class Main {

	private static Socket connection;
	private static long counter = 0;

	public static void main(String[] args) {

		try {
			while(true) {
				connection = new Socket("localhost", 8080);
				System.out.println(counter++);
			}
		} catch(Exception e) {
			System.out.println("Something went wrong: Main.java");
		}

	}
}