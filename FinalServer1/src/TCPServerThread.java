
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.print.attribute.ResolutionSyntax;






public class TCPServerThread extends Thread{

	private Socket clientSocket;
	
	public TCPServerThread(Socket clientSocket, int port) {

		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {

		try {

			Socket sckt1 = clientSocket;
			

			
			// Set Input Output Streams and Buffer
			
			ObjectInputStream  ois = null; 
		    ObjectOutputStream oos = null;
			ois = new ObjectInputStream (sckt1.getInputStream());
			oos = new ObjectOutputStream(sckt1.getOutputStream());

			
			// Getting Client's hostname 
			String clienthostname = null;
			clienthostname = (ois.readObject()).toString();
			
		

				System.out.println("Connected to:" + clienthostname + ":" );
				
				//Send message to Client about successful connection
				oos.writeObject("HOST ADDED SUCCESSFULLY" + "\n");

				
				
				// Register client/Peer with the Server
				int port =1555;
				TCPServer.addPeer(new Peer(clienthostname, port));
				
				// Request Received from Client
				
				


				while (true) {
					
					String ClientRequest = (String) ois.readObject();
					//System.out.print(ClientRequest);
					String request[] = ClientRequest.split(" ");
						// // Process client input according to the request
						// command
						switch (request[0]){
						
					case "CLOSE":
					{
							
							
							
							System.out.println("Client Closing Socket ....");
							sckt1.close();
							
							break;
					}
						
						case "ADD":

						{
							
							String rfcNo = (String) ois.readObject();
							String version	= (String) ois.readObject();
							String hostName = (String) ois.readObject();
							int portNo = Integer.parseInt((String)ois.readObject());
							String rcfTitle = (String) ois.readObject();
							System.out.println("Test : rcf Title - " + rcfTitle );

							//clientPort = Integer.parseInt((String) ois.readObject());
							System.out.println("RFC Added successfully");
							oos.writeObject("RFC Added successfully");
							oos.flush();
							
							
		
							// Update peer List at the Server 
							Peer p = new Peer(hostName, portNo);
							TCPServer.addRFC(rfcNo,rcfTitle,p);
						


						} 
						break;
						
						case "LIST":
						{	
							System.out.println("Received LIST command");
							
							Vector<Index> indexList = TCPServer.getAllRFCs();
							
							Iterator<Index> indexItr=indexList.iterator();

							if (!(indexList.isEmpty()))
										
							  {
									oos.writeObject("P2P-CI/1.0 200 OK" + "\n");
		
									 while(indexItr.hasNext())  
								        {
										 	Index index = indexItr.next();
										 	oos.writeObject(index.getRfcNo()+" "+ index.getRfcTitle()+ " "+ index.getPeer().getHostname()+" "+index.getPeer().getPort());
		
								        }
									 oos.writeObject("end");
							  }
							else 
							{
								oos.writeObject("RFC List Empty");
								oos.writeObject("Error : 404 Not Found");
							}
							
						}
						break;
						case "LOOKUP" :
						{
							
							System.out.println("Received LOOKUP command");
							String rfcName = (String) ois.readObject();
							int rfcNo =  Integer.parseInt(rfcName);
							String rfcTitle = (String) ois.readObject();
							Vector<Peer> resList = TCPServer.findRFC(rfcNo);
							
							
							if (!(resList.isEmpty()))
								{
									oos.writeObject("P2P-CI/1.0 200 OK" + "\n");

									for(Peer p:resList){
										oos.writeObject(rfcName+" "+ rfcTitle+ " "+p.getHostname()+" "+Integer.toString(p.getPort()));
									}
							    }
							else 
								{
										oos.writeObject("Requested RFC Not Found");
										//oos.writeObject("Error : 404 Not Found");
								}
							
							
						} 
						break;
						default : 
							sckt1.close();

													
						}

				}

			
		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();
		}
	}

}

