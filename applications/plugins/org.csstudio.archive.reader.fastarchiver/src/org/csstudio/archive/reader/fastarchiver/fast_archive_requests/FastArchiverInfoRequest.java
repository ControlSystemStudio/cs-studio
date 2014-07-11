package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.ArchiveInfo;

import from_fa_archiver.EndOfStreamException;

/**
 * Class to communicate with Fast Archiver about non-data requests. 
 * @author pvw24041
 */

// Needs to be implemented, similar to Fa-Archiver
public class FastArchiverInfoRequest extends FastArchiverRequest{

	public FastArchiverInfoRequest(String url) {
		super(url);
	}



	/**
	 * Creates an Array of ArchiveInfo about all archives at the url
	 * @return ArchiveInfo[]
	 */
	public ArchiveInfo[] getArchiveInfos() {
		int numOfArchives = 1;
		ArchiveInfo[] archiveInfo = new ArchiveInfo[numOfArchives];
		archiveInfo[0] = new ArchiveInfo("Fast Archiver", "Fast Archiver of DLS", 1);
		return archiveInfo;
	}

	/* METHODS USING SOCKETS DIRECTLY */
	
	/**
	 * creates a list of all BPMs in the archiver
	 * @return String[] of all names
	 */
	private String[] getAllBPMs() throws UnknownHostException, IOException, EndOfStreamException {
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
			buffer = new byte[160];
			allIds.append(infoFromServer);
			readNumBytes = inFromServer.read(buffer);
			infoFromServer = new String(buffer);
			System.out.println(infoFromServer);
			System.out.println();
			
		}
		System.out.println("ID's: "+allIds);
		
		socket.close();
		String [] names = allIds.toString().split("\n");

		// to work around trailing newline char
		return Arrays.copyOfRange(names, 0, names.length-1);
	}
	


	
	/* METHODS USING SOCKET STREAMS */


	
	/**
	 * Generates a list of all data sets the fast archiver can provide
	 * @return String[]
	 * @throws EndOfStreamException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public String[] getAllNames() throws UnknownHostException, IOException, EndOfStreamException {
		String[] allInfo = getAllBPMs();
		String[] allNames = new String[allInfo.length *2]; // each BPM records two coordinates
		
		// split descriptions
		boolean stored;
		int bpmId;
		String coordinate1;
		String coordinate2;
		String name;
		
		
		int index = 0;
		Pattern pattern = Pattern.compile("(\\*| )([0-9]+) ([^ ]+) ([^ ]+) ([^ ]*)");
		
		for (String description: allInfo){
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
				System.out.printf("%b %d %s %s %s\n", stored, bpmId, coordinate1, coordinate2, name);
				
				if (stored){
					allNames[index*2] = new String(name+":"+coordinate1);
					allNames[index*2+1] = new String(name+":"+coordinate2);
					index++;
				}
			} else {
				//
				System.out.println("BPM did not have valid name");
			}
			
		}
		// if not all archives were stored, allNames is not completely filled
		if(index != allInfo.length){
			allNames = Arrays.copyOfRange(allNames, 0, index*2);
		}
		
		return allNames;
	}
	
	

}
