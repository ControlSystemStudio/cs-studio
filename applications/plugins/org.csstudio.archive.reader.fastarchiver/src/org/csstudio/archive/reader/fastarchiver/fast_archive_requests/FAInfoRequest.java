package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.fastarchiver.exceptions.DataNotAvailableException;

/**
 * Class to communicate with Fast Archiver about non-data requests.
 * 
 * @author Friederike Johlinger
 */

public class FAInfoRequest extends FARequest {

	public FAInfoRequest(String url) {
		super(url);
	}

	/* METHODS USING SOCKETS DIRECTLY */
	/**
	 * Creates a list of all BPMs in the archiver, as returned by the archiver
	 * 
	 * @return String[] of all names
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 * @throws DataNotAvailableException
	 *             when data can not be retrieved from archive
	 */
	private String[] getAllBPMs() throws IOException {
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();

		// get String Array of fa-ids
		writeToArchive("CL\n", outToServer);

		byte[] buffer;
		StringBuffer allIds = new StringBuffer();
		int readNumBytes = 0;

		while (true) {
			buffer = new byte[4096];
			readNumBytes = inFromServer.read(buffer);
			if (readNumBytes == -1)
				break;
			allIds.append(new String(buffer).substring(0, readNumBytes));

		}
		socket.close();

		return allIds.toString().split("\n");
	}

	/* OTHER METHODS */
	/**
	 * Creates a Hasmap of all BPMs in the archiver, with names as keys and BPM
	 * number and coordinates in an int array as values.
	 * 
	 * @return Hashmap with names as keys and BPM number and coordinates in an
	 *         int array as values.
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 */
	public HashMap<String, int[]> createMapping() throws IOException {
		String[] allBPMs = getAllBPMs();
		HashMap<String, int[]> bpmMapping = new HashMap<String, int[]>();

		// split descriptions
		boolean stored;
		int bpmId;
		String coordinate1;
		String coordinate2;
		String name;

		Pattern pattern = Pattern
				.compile("(\\*| )([0-9]+) ([^ ]+) ([^ ]+) ([^ ]*)");

		for (String description : allBPMs) {
			Matcher matcher = pattern.matcher(description);
			if (matcher.matches()) {
				stored = matcher.group(1).equals("*");
				bpmId = Integer.parseInt(matcher.group(2));
				coordinate1 = matcher.group(3);
				coordinate2 = matcher.group(4);
				name = matcher.group(5);
				if (name.equals(""))
					name = new String("FA-ID-" + bpmId);

				if (stored) {
					if (coordinate1.equals(coordinate2)) {
						coordinate1 = coordinate1 + "1";
						coordinate2 = coordinate2 + "2";
					}
					bpmMapping.put((name + ":" + coordinate1), new int[]{bpmId, 0});
					bpmMapping.put((name + ":" + coordinate2), new int[]{bpmId, 1});
				}

			} else {
				System.out.println("BPM did not have valid name");
			}

		}

		return bpmMapping;
	}

}
