
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class TCPServer {

	static final int counter = 7734;
	static Vector<Peer> peerList;
	static Vector<Index> rfcList;
	
	TCPServer()
	{
		
	}
	
	
	public static void main(String args[]) throws IOException, InterruptedException {
		peerList = new Vector<Peer>();
		rfcList = new Vector<Index>();
		
		ServerSocket conn0 = new ServerSocket(counter);
		while (true) {
			Socket sckt0 = conn0.accept();
			TCPServerThread tcpserver = new TCPServerThread(sckt0,counter);
			tcpserver.start(); 
			System.out.println("Server Started :");

			Thread.sleep(100);

			// InputStreamReader inputReader = new
			// InputStreamReader(sckt0.getInputStream());
			// BufferedReader bufferReader = new BufferedReader(inputReader);
//			DataOutputStream outputStream = new DataOutputStream(sckt0.getOutputStream());
			// client_input = bufferReader.readLine();
			// System.out.println("Received: " + client_input);
//			outputStream.writeBytes(counter + "\n");
			
		}

	}
	
	public static void addRFC(String rfcNo,String rfcTitle,Peer peer){
		
		Index in = new Index();
		in.setRfcNo(rfcNo);
		in.setRfcTitle(rfcTitle);
		in.setPeer(peer);
		rfcList.add(in);
	}
	
	public static void addPeer(Peer peer){
		peerList.add(peer);
	}
	
	public static Vector<Peer> findRFC(int rfcNo){
		
		Vector<Peer> results = new Vector<Peer>();
		for(Index i:rfcList){
			if(Integer.parseInt(i.getRfcNo()) == rfcNo)
				results.add(i.getPeer());
		}
		return results;
	}
	
	public static Vector<Index> getAllRFCs(){
		return rfcList;
	}
}


