package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.fastarchiver.exceptions.DataNotAvailableException;

/**
 * Class with common methods for communicating with the fast archiver
 * 
 * @author Friederike Johlinger
 *
 */

public abstract class FARequest {

	protected static final int TIMESTAMP_BYTE_LENGTH = 18;
	protected static final String CHAR_ENCODING = "US-ASCII";
	protected String host;
	protected int port;

	public FARequest(String url) throws DataNotAvailableException {
		Pattern pattern = Pattern.compile("fads://([A-Za-z0-9-]+)(:[0-9]+)?");
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			this.host = matcher.group(1);
			if (matcher.group(2) == null)
				this.port = 8888;
			else
				this.port = Integer.parseInt(matcher.group(2).substring(1));
		} else {
			throw new DataNotAvailableException("Invalid url");
		}
	}

	/**
	 * Takes a string to write to the server and returns the complete response
	 * as a byte[]
	 * 
	 * @param request
	 *            String to be written to the server
	 * @return byte[] containing the complete response
	 * @throws IOException
	 */
	protected byte[] fetchData(String request) throws IOException {
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();

		try {
			outToServer.write(request.getBytes(CHAR_ENCODING));
			outToServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// get data out of archive one buffer at a time. Append it to a list of
		// buffers.
		byte[] buffer = new byte[4096];
		List<byte[]> allBuffers = new LinkedList<byte[]>();
		int readNumBytes = 0;
		int totalLength = 0;

		while (true) {
			readNumBytes = inFromServer.read(buffer);
			if (readNumBytes == -1)
				break;
			allBuffers.add(Arrays.copyOf(buffer, readNumBytes));
			totalLength += readNumBytes;
		}
		socket.close();

		// Return a byte[] containing all data.
		byte[] allData = new byte[totalLength];
		int allDataIndex = 0;
		for (byte[] bA : allBuffers) {
			for (byte b : bA) {
				allData[allDataIndex] = b;
				allDataIndex++;
			}
		}

		return allData;
	}

}
