
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;


public class Client {

	@SuppressWarnings("deprecation")
	public static void main(String argv[]) throws Exception {



		//Version
		final String version = "P2P-CI/1.0";

		
		InetAddress localAddr = InetAddress.getLocalHost();
		//ServerSocket uploadSocket = new ServerSocket(0, 20, localAddr);
		ServerSocket uploadSocket = new ServerSocket(0, 20, localAddr);

		String hostName = uploadSocket.getInetAddress().getHostAddress();
		int localPortInt = uploadSocket.getLocalPort();
		String localPort = Integer.toString(localPortInt);
		String sentence = hostName + ":::" + localPort;
		String Server = "192.168.1.149";
		
		
		Thread t = new Thread(new UploadServer(uploadSocket, localPortInt));
		t.start();
		
	
		//Connecting to Server
		Socket clientSocket = new Socket(Server, 7734);
		//Socket clientSocket = new Socket(localAddr, 7734);
		
		
		
		// Set Input Output Streams and Buffer

		ObjectOutputStream out = null;
		ObjectInputStream oin = null;
		String resp;
		
		
		
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		oin = new ObjectInputStream(clientSocket.getInputStream());
		
		out.writeObject(hostName.toString());



		//Response from server
		resp = ((String) oin.readObject()).trim();
		System.out.println(resp);


		// 1st send the port no info to server
		// modifiedSentence = sendToServer(clientSocket, sentence);
		// System.out.println("SERVER recievd port no " + modifiedSentence);
		 
		 
		Scanner input = new Scanner(System.in);
		whileLoop: while (true) {
			
			System.out.println("INPUT: 0-EXIT 1-GET RFC 2-ADD RFC 3-LOOKUP 4-LIST");
			int i = input.nextInt();

			switch (i) {
			case 0:
				break whileLoop;
			case 1: 
			{
				Scanner in = new Scanner(System.in);
				System.out.print("Input RFC #:");
				int rfcNo = in.nextInt();
				System.out.print("Input Host:");
				String host1 = in.next(); //"rpatel16-Lenovo-Y40-80"
				//String host1 = "192.168.1.149";
				System.out.print("Input Port #:");
				int portNo = in.nextInt();
				Socket peerSocket = new Socket(host1, portNo);
				//Socket peerSocket = new Socket("192.168.1.112", portNo);
				downloadRFC(peerSocket,host1, rfcNo);
			}

				break;
			case 2: {
				Scanner in = new Scanner(System.in);
				System.out.print("Input RFC #:");
				int rfcNoInt = in.nextInt();
				String rfcNo = Integer.toString(rfcNoInt);
				
				System.out.print("Input RFC Title:");
				String rfcTitle = in.next();
				

				//Send To Server
				
			

				out.writeObject("ADD"+ " "+"RFC" + " "+rfcNo + 
								" " + version + 
								"\n HOST:"+ hostName +
								"\n PORT:" + localPort +
								"\n TITLE:" + rfcTitle + "\n");

				out.writeObject(rfcNo);
				out.writeObject(version);
				out.writeObject(hostName);
				out.writeObject(localPort);
				out.writeObject(rfcTitle);
				//out.writeObject(localPort);
				
				

				
				//Response from Server 
				
				String response = (String) oin.readObject();
				System.out.println(response);
				break;
			}
			case 3: 
			{
				Scanner in = new Scanner(System.in);

				System.out.println("Enter the RFC to lookup:");
				String rfcNo = in.next();
				System.out.println("Enter the title of the RFC:");
				String rfcTitle = in.next();
				out.writeObject("LOOKUP RFC " + rfcNo + " " + version + "\n HOST: "
						+ hostName + "\n PORT: " + localPort + "\n TITLE: " + rfcTitle
						+ "\n");
				out.writeObject(rfcNo);
				out.writeObject(rfcTitle);
				
				//Response from Server
				
				String response = null;
				
				//Error Case
				
				boolean error = false;

				
				response = ((String) oin.readObject()).trim();
				if (resp.contains("Requested RFC Not Found"))
				
					{
						System.out.println(resp);
						error =true;
					}
				
				
				//response = ((String) oin.readObject()).trim();
				//System.out.println(response);
				if (!error)
				 {
					response = ((String) oin.readObject()).trim();
					System.out.println(response);
				 }		
				
				
			}
				
				break;
		
			case 4: 
				{
				
				out.writeObject("LIST ALL " + version + "\n HOST: " + hostName
						+ "\n PORT: " + localPort + "\n");
				
				//Different Version
				
				boolean error = false;
				
				
				//Compatible Version
				
				
				resp = ((String) oin.readObject()).trim();
				if (resp.contains("RFC List Empty"))
				
					{
						System.out.println(resp);
						error =true;
						resp = ((String) oin.readObject()).trim();
						System.out.println(resp);

					}
				
				
				if (!error)
				 { 
					System.out.println(resp);
					resp = (String) oin.readObject();
					while (resp.equalsIgnoreCase("end") == false) {
						System.out.print(resp+"\n");
						resp = (String) oin.readObject();
					  }
				 }
				
				}
				break;
		
			
			default: {
				System.out.println("Error: 400 Bad Request");
				System.out.println("Invalid Input");
				break;
			}
			}

		}

		//Thread.sleep(4000);
		
		// close the socket
		
		out.writeObject("CLOSE");
		System.out.println("Client Closing Socket ....");
		t.stop();
		t.join();
		clientSocket.close();
		System.out.println("Client Closed Socket!");
		System.exit(0);
	}

	
	private static String downloadRFC(Socket peerSocket, String hostName, int rfcNo) {
		
		
		//Send the Information about RFC to be downloaded to peer
		
		System.out.print("in downloadRFC \n");

		
		//Setting Output Stream
	    ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(peerSocket.getOutputStream());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String sentence = "GET RFC "+ rfcNo +" P2P-CI/1.0";
		try {
			oos.writeObject(sentence);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		
		
		
		
		//Response from peer Server
		
		
		
		ObjectInputStream  ois = null; 
		try {
			ois = new ObjectInputStream (peerSocket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String peer_resp = null;
		try {
			peer_resp = ((String) ois.readObject()).trim();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(peer_resp);

		
			
			//File directory = new File("RFC");
			//File newFile = new File(directory.getCanonicalPath() + "UDP.txt");
			File newFile = new File(rfcNo+"new.txt");

			//newFile.createNewFile();
		
		
			try {
				byte[] content = (byte[]) ois.readObject();
				System.out.println(content);
				
				//write contents to file
				Files.write(newFile.toPath(), content);
			} 
			catch (ClassNotFoundException | IOException eof) {
				System.out.println("End of file");
			}

			return("Success");
		
	}



}