import java.io.DataInputStream;
import java.io.IOException;
public class ClientMessageReceiver implements Runnable {
	private DataInputStream dis;
	private boolean timeToStop = false;
	public ClientMessageReceiver(DataInputStream dis) {
		this.dis = dis;
	}
	@Override
	public void run() {
		while (!timeToStop) {
			try {
				System.out.println(dis.readUTF());
			} catch (IOException e) {
				if ("Quit".equals(e.getMessage())) {
					System.out.println("The connection to the server has been interrupted!");
					break;
				}
				if (!timeToStop) {
					e.printStackTrace();					
				}
			}
		}
	}
	public void stop() {
		timeToStop = true;
	}
}