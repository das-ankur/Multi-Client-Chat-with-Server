import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
public class ChatServerRunnable implements Runnable {
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private String currentUserNickName;
	public ChatServerRunnable(Socket socket) throws IOException {
		this.socket = socket;
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.dis = new DataInputStream(socket.getInputStream());
	}
	@Override
	public void run() {
		try {
			write("Welcome to the chat room!");
			login();
			System.out.println(currentUserNickName + "User login successfully");
			write(currentUserNickName + ", you are logged in.\nEnter [list users] to view the list of currently logged in users\nEnter [to all message content] to send messages in groups\nEnter [to a user message content] to send messages to specified users\n Enter [exit] to exit the chat");
			String input = dis.readUTF();
			while (!ChatServer.EXIT.equals(input)) {
				System.out.println(currentUserNickName + "Entered" + input);
				if (input.startsWith("to ")) {
					sendMessage(input);
				} else if ("list users".equals(input)) {
					showOnlineUsers();
				} else {
					write("The command you entered is illegal, please re-enter!");
				}
				input = dis.readUTF();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ChatServer.nickNameSocketMap.remove(currentUserNickName);
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void login() throws IOException {
		write("Please enter your nickname:");
		while (true) {
			String nickName = dis.readUTF();
			System.out.println("The user has entered a nickname:" + nickName);
			synchronized (ChatServerRunnable.class) {
				if (!ChatServer.nickNameSocketMap.containsKey(nickName)) {
					currentUserNickName = nickName;
					ChatServer.nickNameSocketMap.put(nickName, socket);
					break;
				} else {
					write("The nickname you entered already exists, please re-enter:");
				}
			}
		}
	}
	private void sendMessage(String input) throws IOException {
		int receiverEndIndex = input.indexOf(" ", 3);
		String receiver = input.substring(3, receiverEndIndex);
		String message = input.substring(receiverEndIndex + 1);
		if ("all".equals(receiver)) {
			broadcast(message);
		} else {
			sendIndividualMessage(receiver, message);
		}
	}
	private void sendIndividualMessage(String receiver, String orignalMessage) throws IOException {
		Socket receiverSocket = ChatServer.nickNameSocketMap.get(receiver);
		if (receiverSocket != null) {
			SocketUtils.writeToSocket(receiverSocket, formatMessage("you", orignalMessage));
		} else {
			write("The user you want to chat with separately [" + receiver + "] does not exist or has been offline");
		}
	}
	private String formatMessage(String receiver, String originalMessage) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(currentUserNickName).append(" Correct ").append(receiver).append("Say:\n")
				.append(originalMessage).append("\nSend time:")
				.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		return messageBuilder.toString();
	}
	private void broadcast(String orignalMessage) throws IOException {
		for (Map.Entry<String, Socket> entry : ChatServer.nickNameSocketMap.entrySet()) {
			if (!currentUserNickName.equals(entry.getKey())) {
				SocketUtils.writeToSocket(entry.getValue(), formatMessage("Everyone", orignalMessage));
			}
		}
	}
	private void showOnlineUsers() throws IOException {
		StringBuilder users = new StringBuilder();
		users.append("Currently online users are:\n");
		for (String nickName : ChatServer.nickNameSocketMap.keySet()) {
			users.append("【").append(nickName).append("】\n");
		}
		write(users.toString());
	}
	private void write(String message) throws IOException {
		SocketUtils.writeToDataOutputStream(dos, message);
	}
}