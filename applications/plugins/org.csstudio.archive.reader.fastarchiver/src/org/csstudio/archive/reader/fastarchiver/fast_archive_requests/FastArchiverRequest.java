package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import from_fa_archiver.EndOfStreamException;

/**
 * Class with common methods for communicating with the fast archiver 
 * @author pvw24041
 *
 */

public abstract class FastArchiverRequest {
	
	protected static final int TIMESTAMP_BYTE_LENGTH = 18;
	protected static final String CHAR_ENCODING = "US-ASCII";
	protected String host; // "pc0044"
	protected int  port;
	protected enum Decimation {UNDEC, DEC, DOUBLE_DEC}; 
	protected HashMap<String,Integer> bpmMapping;
	protected String[] allNames;
	
	//need to change for optional host????
	public FastArchiverRequest(String url){
		Pattern pattern = Pattern.compile("fads://([A-Za-z0-9-]+)(:[0-9]+)?");
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			this.host = matcher.group(1);
			System.out.println("Host: " + host);
			if (matcher.group(2) == null)
				this.port = 8888;
			else
				this.port = Integer.parseInt(matcher.group(2).substring(1));
		} else {
			//
			System.out.println("URL did not match pattern");
		}
		try {
			createMapping();
		} catch (IOException | EndOfStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// METHODS USING SOCKET STREAMS

	/**
	 * Writes a message to the fa-archiver to request data, only to be used by
	 * methods creating sockets
	 */
	protected static void writeToArchive(String message, OutputStream outToServer) {
		try {
			outToServer.write(message.getBytes(CHAR_ENCODING));
			outToServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// METHODS USING SOCKETS
	/**
	 * creates a list of all BPMs in the archiver
	 * @return String[] of all names
	 */
	protected String[] getAllBPMs() throws UnknownHostException, IOException, EndOfStreamException {
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();		
		
		// get String Array of fa-ids
		writeToArchive("CL\n", outToServer);
		
		byte[] buffer;
		StringBuffer allIds = new StringBuffer();
		String infoFromServer = "";
		int readNumBytes = 0;
		while (readNumBytes != -1){
			buffer = new byte[16384];
			allIds.append(infoFromServer);
			readNumBytes = inFromServer.read(buffer);
			infoFromServer = new String(buffer);
			//System.out.println(infoFromServer);
			//System.out.println();
			
		}
		//System.out.println("ID's: "+allIds);
		
		socket.close();
		String [] names = allIds.toString().split("\n");

		// to work around trailing newline char
		return Arrays.copyOfRange(names, 0, names.length-1);
	}
	
	// OTHER METHODS

	private void createMapping() throws UnknownHostException, IOException, EndOfStreamException {
		String[] allBPMs = getAllBPMs();
		String[] allNames = new String[allBPMs.length *2]; // each BPM records two coordinates
		HashMap<String, Integer> bpmMapping = new HashMap<String, Integer>();
		
		// split descriptions
		boolean stored;
		int bpmId;
		String coordinate1;
		String coordinate2;
		String name;
		String newName;
		
		
		
		int index = 0;
		Pattern pattern = Pattern.compile("(\\*| )([0-9]+) ([^ ]+) ([^ ]+) ([^ ]*)");
		
		for (String description: allBPMs){
			//get data out of description and into values from which names can be created 
			
			Matcher matcher = pattern.matcher(description);
			if (matcher.matches()) {
				stored = matcher.group(1).equals("*");
				bpmId = Integer.parseInt(matcher.group(2));
				coordinate1 = matcher.group(3);
				coordinate2 = matcher.group(4);
				name = matcher.group(5);
				if (name.equals(""))
					name = new String("FA-ID-"+bpmId);
				
				if (stored){
					newName = new String(name+":"+coordinate1);
					allNames[index*2] = newName;
					bpmMapping.put(newName, bpmId);
					
					newName = new String(name+":"+coordinate2);
					allNames[index*2+1] = newName;
					bpmMapping.put(newName, bpmId);
					index++;
				}
				
				
			} else {
				System.out.println("BPM did not have valid name");
			}
			
		}
		// if not all archives were stored, allNames is not completely filled
		if(index != allBPMs.length){
			allNames = Arrays.copyOfRange(allNames, 0, index*2);
		}
		this.bpmMapping = bpmMapping;
		this.allNames = allNames;
	}

	
}
