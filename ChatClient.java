import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
public class ChatClient {
	private static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
		try (Socket socket = new Socket("127.0.0.1", 8888);
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
			ClientMessageReceiver messageReceiver = new ClientMessageReceiver(dis);
			new Thread(messageReceiver).start();
			String input = null;
			do {();
				write(dos, input);
				input = scanner.nextLine
			} while (!ChatServer.EXIT.equals(input));
			messageReceiver.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void write(DataOutputStream dos, String message) throws IOException {
		dos.writeUTF(message);
		dos.flush();
	}
}