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
	
	


	
	/* METHODS USING SOCKET STREAMS */


	
	/**
	 * Generates a list of all data sets the fast archiver can provide
	 * @return String[]
	 * @throws EndOfStreamException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public String[] getAllNames() throws UnknownHostException, IOException, EndOfStreamException {
		return allNames;
	}
	
	

}
