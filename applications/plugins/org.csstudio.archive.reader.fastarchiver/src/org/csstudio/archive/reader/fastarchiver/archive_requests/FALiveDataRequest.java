package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;

/**
 * Class to get new values for an item in the Fast Archiver. Right now using the
 * decimated data stream.
 * 
 * @author Friederike Johlinger
 *
 */
public class FALiveDataRequest extends FARequest {

	private int blockSize; // samples per block
	private int offset; // offset samples in first block
	private Socket socket;
	private String url;
	private String name;
	private HashMap<String, int[]> mapping;
	private BufferedInputStream inFromServer;

	/**
	 * 
	 * @param url
	 *            needs to start with "fads://" followed by the host name and
	 *            optionally a colon followed by a port number (default 8888)
	 * @param name
	 *            , PV for which data is fetched
	 * @param mapping
	 *            obtained from FAInfoRequest
	 * @throws FADataNotAvailableException
	 *             if the URL does not have the right format
	 * @throws IOException
	 *             when the connection to the Fast Archiver encounters a problem
	 */
	public FALiveDataRequest(String url, String name,
			HashMap<String, int[]> mapping) throws IOException,
			FADataNotAvailableException {
		super(url);

		this.url = url;
		this.name = name;
		this.mapping = mapping;

		// Make a connection to the Fast Archiver
		socket = new Socket(host, port);
		int bpm = mapping.get("fa://" + name)[0];
		String request = String.format("S%dTED\n", bpm);
		socket.getOutputStream().write(request.getBytes(CHAR_ENCODING));
		socket.getOutputStream().flush();
		inFromServer = new BufferedInputStream(socket.getInputStream(), 131072);

		decodeInitialData();
	}

	/**
	 * Used to get the initial data from the live data stream
	 * 
	 * @throws FADataNotAvailableException
	 *             when the server returns an error message
	 * @throws IOException
	 *             if the inputStream encounters a problem
	 */
	private void decodeInitialData() throws FADataNotAvailableException,
			IOException {
		/* Check if first byte reply is zero -> data is sent */
		inFromServer.mark(2);
		byte firstChar = (byte) inFromServer.read();
		if (firstChar != '\0') {
			inFromServer.reset();
			int available = inFromServer.available();
			byte[] message = new byte[available - 1];
			inFromServer.read(message);
			throw new FADataNotAvailableException(new String(message));
		}

		byte[] bA = new byte[8];
		inFromServer.read(bA);
		ByteBuffer init = ByteBuffer.wrap(bA);
		init.position(0);
		init.order(ByteOrder.LITTLE_ENDIAN);

		// get initial data out
		blockSize = init.getInt();
		offset = init.getInt();
	}

	/**
	 * @return New values from the live stream
	 * @throws FADataNotAvailableException
	 *             when no new values are available
	 * @throws IOException
	 *             when the fetch encounters a problem with the socket
	 */
	public ArchiveVDisplayType[] fetchNewValues(int decimation) throws IOException,
			FADataNotAvailableException {
		// Read out from BufferedInputStream into ByteBuffer
		int bytesToRead = calcNumBytesToRead();
		if (bytesToRead == 0)
			throw new FADataNotAvailableException("No new values available");
		byte[] newData = new byte[bytesToRead];
		inFromServer.read(newData);
		ByteBuffer bb = ByteBuffer.wrap(newData);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		// Process ByteBuffer as in FAArchivedDataRequest
		ArchiveVDisplayType[] newValues = decodeDataUndecToDec(bb,
				getSampleCount(bytesToRead), blockSize, offset,
				mapping.get("fa://" + name)[1], decimation);
		offset = 0;
		return newValues;
	}

	/**
	 * Should be called when the connection to the archiver becomes redundant
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the number of samples contained in a byteArray of length
	 * bytesToRead
	 */
	private int getSampleCount(int bytesToRead) {
		int samplesFirstBlock = 0;
		if (offset != 0)
			samplesFirstBlock = blockSize - offset;
		int samplesOtherBlocks = bytesToRead / (12 + blockSize * 8) * blockSize;
		return samplesFirstBlock + samplesOtherBlocks;
	}

	/**
	 * Calculates the number of bytes that can be read from the inputStream,
	 * using complete blocks
	 * 
	 * @throws IOException
	 *             if the stream has been closed
	 */
	private int calcNumBytesToRead() throws IOException {
		int numOfBytes;
		numOfBytes = inFromServer.available();
		int lengthFirstBlock = 0;
		if (offset != 0)
			lengthFirstBlock = 12 + (blockSize - offset) * 8;
		int lengthNormalBlock = 12 + blockSize * 8;
		// calculate number of bytes in the last incomplete block
		int bytesRemaining = (numOfBytes - lengthFirstBlock)
				% lengthNormalBlock;
		return numOfBytes - bytesRemaining;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass() + "\n");
		buffer.append("URL: " + url + ", name: " + name);
		return buffer.toString();
	}

}
