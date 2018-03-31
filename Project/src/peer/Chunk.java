package peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Chunk{
	
	public static int MAX_SIZE = 64*1000;
	
	private int chunk_no;
	private String file_id;
	private byte[] data;
	private int rep_degree;
	private String id;
	
	public static final String DIR  = "CHUNKS_" +Peer.getServerID() +"/";
	
	public Chunk(int chunk_no,String file_id, byte[] data, int rep_degree ) {
		this.chunk_no =chunk_no;
		this.file_id = file_id;
		this.data = data;
		this.rep_degree= rep_degree;
		this.id=chunk_no + "_" +file_id;
	}
	

	public int getChunkNo() {
		return chunk_no;
	}
	
	public String getFileId() {
		return file_id;
	}
	public byte[] getData() {
		return data;
	}
	public int getRepDegree() {
		return rep_degree;
	}
	public String getID() {
		return id;
	}
	
	public void store() {
		
		File folder = new File(DIR);

		 
		if (!(folder.exists() && folder.isDirectory()))
			folder.mkdir();

		FileOutputStream out;
		try {
			out = new FileOutputStream(DIR + id);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isStored() {
		File file = new File(DIR +id);
		
		return file.exists() && file.isFile();
	}
	
	public void backup() {
		long wait_time =1;
		int putchunk_sent=0;
		int stored =0;
		
		
		do {
			Peer.getMC().startSave(this);
			Peer.getMsgForwarder().sendPUTCHUNK(this);
			putchunk_sent++;
			
			try {
				TimeUnit.SECONDS.sleep(wait_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			stored = Peer.getMC().getSaves(this);
			
			wait_time *=2;
			
		}while(stored<rep_degree && putchunk_sent !=5);
		
	
		Peer.getMC().stopSave(this);
		
	}





}