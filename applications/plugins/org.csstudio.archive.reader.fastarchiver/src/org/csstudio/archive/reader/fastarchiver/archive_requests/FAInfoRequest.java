package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;

/**
 * Class to communicate with Fast Archiver about non-data requests.
 * 
 * @author FJohlinger
 */

public class FAInfoRequest extends FARequest {
	
	/** Prefix to determine right DataSource in the PVManager */
	private String prefix = "fa://";

	/**
	 * @param url 
	 * 			  needs to start with "fads://" followed by the host name and
	 *            optionally a colon followed by a port number (default 8888)
	 * @throws FADataNotAvailableException
	 *             when the url doesn't have the right format
	 */
	public FAInfoRequest(String url) throws FADataNotAvailableException {
		super(url);
	}

	/**
	 * Creates a Hashmap of all BPMs in the archiver.
	 * 
	 * @return Hashmap with names as keys and BPM number and coordinates in an
	 *         int array as values.
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified at construction
	 */
	public HashMap<String, int[]> fetchMapping() throws IOException {
		byte[] buffer = fetchData("CL\n");
		String[] allBPMs = new String(buffer).split("\n");
		
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
					bpmMapping.put(prefix + name + ":" + coordinate1, new int[]{bpmId, 0});
					bpmMapping.put(prefix + name + ":" + coordinate2, new int[]{bpmId, 1});
				}
			}
		}
		return bpmMapping;
	}
	
	/**
	 * Sends a request to the server to get the name of the archive
	 * @return name as a String
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified at construction
	 */
	public String getName() throws IOException {
		byte[] buffer = fetchData("CN\n");
		
		String[] allInfo = new String(buffer).split("\n");
		return allInfo[0];
	}
}
