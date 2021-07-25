import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
public class ChatServer {
	public static final String EXIT = "exit";
	public static final int PORT = 8888;
	static Map<String, Socket> nickNameSocketMap = new HashMap<>();
	public static void main(String[] args) {
		try (ServerSocket ss = new ServerSocket(PORT)) {
			System.out.println("The chat room server has been started and is listening" + PORT + "port");
			while (true) {
				try {
					Socket socket = ss.accept();
					System.out.println("A new user is connected to the server, the information is:" + socket);
					new Thread(new ChatServerRunnable(socket)).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}