package messages;

import java.io.File;
import java.net.DatagramPacket;
import java.util.Random;

import common.Utils;
import peer.Chunk;
import peer.Peer;

public class MsgHandler implements Runnable{

	DatagramPacket packet;
	
	String[] header;
	
	public MsgHandler(DatagramPacket packet){
		this.packet = packet;
	}
	
	@Override
	public void run() {
		
		header = Utils.parseHeader(packet);
		
		int server_id = Integer.parseInt(header[2]);
		
		
		// if message comes from self ignore it 
		if(server_id == Peer.getServerID()) return;
		
		String operation = header[0];
		
		switch(operation){
		case "PUTCHUNK":
			handlePUCHUNK();
			break;
		case "STORED":
			handleSTRORED();
			break;
			
		}
		
	}

	private void handleSTRORED() {
		System.out.println("STORED RECEIVED");
		String file_id=header[3];
		int chunk_no = Integer.parseInt(header[4]);
		
		Chunk chunk = new Chunk(chunk_no,file_id,new byte[0], 0);
		
		Peer.getMC().save(chunk, Peer.getServerID());
		
	}

	private void handlePUCHUNK() {
		System.out.println("PUTCHUNK RECEIVED");
		
		// chunk info from header
		String file_id=header[3];
		int chunk_no = Integer.parseInt(header[4]);
		int rep_degree= Integer.parseInt(header[5]);
		
		// chunk data from body
		byte[] chunk_data =Utils.parseBody(packet);
		
		// create chunk 
		Chunk chunk = new Chunk(chunk_no,file_id,chunk_data, rep_degree);
		
		// stored chunk if not stored already
		if(!chunk.isStored()) {
			chunk.store();
		}
		
		// wait a random delay
		Random rand = new Random();
		int  n = rand.nextInt(400) + 1;
		
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		// send STORED message
		Peer.getMsgForwarder().sendSTORED(chunk);
		
		
		
	}

}