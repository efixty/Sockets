import java.net.*;

public class User {
	private String nickname;
	private Socket connection;

	public User(Socket connection, String nickname) {
		this.connection = connection;
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public Socket getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		return nickname;
	}
}