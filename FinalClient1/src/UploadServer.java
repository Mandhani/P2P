import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadServer implements Runnable {
	private volatile boolean exit = false;
	private ServerSocket uploadSocket;
	private int port;

	public UploadServer(ServerSocket uploadSocket, int port) {

		this.uploadSocket = uploadSocket;
		this.port = port;
	}

	@Override
	public void run() {

		System.out.println("Starting Upload server at:" + uploadSocket.getInetAddress().getHostName() + ":" + port);
		Socket socket;

		while (!exit) {
			try {
				System.out.println("Waiting for the request..");
				socket = uploadSocket.accept();
				System.out.println("request recieved");

				// File file = new File("TCP.txt");

				// Setting Streams
				ObjectInputStream ois = null;
				ois = new ObjectInputStream(socket.getInputStream());

				ObjectOutputStream oos = null;
				oos = new ObjectOutputStream(socket.getOutputStream());

				System.out.println("Abhishek: I am here");

				// Peer Request

				String peerRequest = null;

				try {
					peerRequest = (String) ois.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Extract Information
				String request[] = peerRequest.split(" ");

				// oos.writeObject("Before reading file");
				File file = new File(request[2] + ".txt");

				// Getting meta data about file

				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();
				System.out.println(dateFormat.format(date));

				oos.writeObject("P2P-CI/1.0 200 OK" + "\n" + "Date: " + dateFormat.format(date) + "\n" + "OS: "
						+ System.getProperty("os.name") + "\n" + "Last-Modified: "
						+ dateFormat.format(new Date(file.lastModified())) + "\n" + "Content-Length: "
						+ (int) file.length() + "\n" + "Content-Type: text/plain" + "\n");

				// Reading Contents of File
				byte[] content = Files.readAllBytes(file.toPath());
				oos.writeObject(content);

			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Upload Server exiting now...");

	}
	
	public void stop(){
        exit = true;
    }
}
