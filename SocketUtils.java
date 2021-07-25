import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
public class SocketUtils {
	public static void writeToSocket(Socket socket, String message) throws IOException {
		writeToOutputStream(socket.getOutputStream(), message);
	}
	public static void writeToDataOutputStream(DataOutputStream dos, String message) throws IOException {
		dos.writeUTF(message);
		dos.flush();
	}
	public static void writeToOutputStream(OutputStream os, String message) throws IOException {
		writeToDataOutputStream(new DataOutputStream(os), message);
	}
}