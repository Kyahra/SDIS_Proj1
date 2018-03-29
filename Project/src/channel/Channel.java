package channel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import common.Utils;
import peer.Peer;


public class Channel implements Runnable{
	
	public MulticastSocket mcast_socket;
	public InetAddress mcast_addr;
	public int mcast_port;
	
	public Channel(String address, String port) throws IOException{
		
		this.mcast_addr = InetAddress.getByName(address);
		this.mcast_port = Integer.parseInt(port);
		
		mcast_socket = new MulticastSocket(mcast_port);
		mcast_socket.setTimeToLive(1);
		mcast_socket.joinGroup(mcast_addr);
		
				
	}

	public void run(){

		byte[] buf = new byte[1024];
		boolean done = false;
		
		// receive messages
		while (!done) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				mcast_socket.receive(packet);
				String[] header_tokens = Utils.parseHeader(packet);
				if(Integer.parseInt(header_tokens[2]) == Peer.getServerID()) 
					System.out.println("ignored");
				else
					System.out.println(header_tokens);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// close socket
		mcast_socket.close();
		
	}

	public synchronized void sendMessage(byte[] msg) {

	        DatagramPacket packet = new DatagramPacket(msg, msg.length, mcast_addr, mcast_port);

	        try {
	            mcast_socket.send(packet);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}
	

	
	
}