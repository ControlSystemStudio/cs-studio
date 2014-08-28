package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;

/**
 * Class to get new values for an item in the Fast Archiver. Uses the
 * undecimated data stream. Stores the data for approx. 3 seconds between
 * fetches.
 * 
 * @author FJohlinger
 *
 */
public class FALiveDataRequest extends FARequest {

	private int blockSize; // samples per block
	private int offset; // offset samples in first block
	private Socket socket;
	private int bpm;
	private int coordinate;
	private int noNewValuesOccurence = 0;
	private int reconnectAfter = 10;
	private int buffersize = 32768;
	private boolean closed = true;

	private BufferedInputStream inFromServer;

	/**
	 * 
	 * @param url
	 *            needs to start with "fads://" followed by the host name and
	 *            optionally a colon followed by a port number (default 8888)
	 * @param name
	 *            PV for which data is fetched
	 * @param bpm
	 *            number of the BPM in the archive
	 * @param coordinate
	 *            either 0 (X-coordinate) or 1 (Y-coordinate)
	 * @throws FADataNotAvailableException
	 *             if the URL does not have the right format
	 * @throws IOException
	 *             when the connection to the Fast Archiver encounters a problem
	 */
	public FALiveDataRequest(String url, int bpm, int coordinate)
			throws IOException, FADataNotAvailableException {
		super(url);
		this.bpm = bpm;
		this.coordinate = coordinate;

		// Make a connection to the Fast Archiver
		socket = new Socket(host, port);
		String request = String.format("S%dTE\n", bpm);
		socket.getOutputStream().write(request.getBytes(CHAR_ENCODING));
		socket.getOutputStream().flush();
		inFromServer = new BufferedInputStream(socket.getInputStream(),
				buffersize);
		closed = false;

		decodeInitialData();
	}

	/**
	 * reconnectAfter is the number of times the archiver tries to fetch data,
	 * without actually returning new values, before trying to reconnect to the
	 * archiver.
	 * 
	 * @return the current value for reconnectAfter
	 */
	public int getReconnectAfter() {
		return reconnectAfter;
	}

	/**
	 * reconnectAfter is the number of times the archiver tries to fetch data,
	 * without actually returning new values, before trying to reconnect to the
	 * archiver. Default value is {@value #noNewValuesOccurence}, should be
	 * higher for more frequent requests.
	 * 
	 * @param reconnectAfter
	 */
	public synchronized void setReconnectAfter(int reconnectAfter) {
		this.reconnectAfter = reconnectAfter;
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
		// Otherwise an error message is sent
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
	 * @param decimation
	 *            the approximate number the samples are reduced by
	 * @return New values from the live stream
	 * @throws FADataNotAvailableException
	 *             when the socket has been closed or an invalid coordinate has
	 *             been specified during the construction
	 * @throws IOException
	 *             when the fetch encounters a problem with the connection
	 */
	public synchronized ArchiveVDisplayType[] fetchNewValues(int decimation)
			throws IOException, FADataNotAvailableException {
		if (closed)
			throw new FADataNotAvailableException("Socket has been closed");
		// Read out from BufferedInputStream into ByteBuffer
		int bytesToRead = calcNumBytesToRead();
		if (bytesToRead == 0) {
			noNewValuesOccurence++;
			// check whether we have gotten no new values more than
			// "reconnectAfter" times
			if (noNewValuesOccurence >= reconnectAfter) {
				reconnect();
			}
			return new ArchiveVDisplayType[0];
		}

		byte[] newData = new byte[bytesToRead];
		inFromServer.read(newData);
		ByteBuffer bb = ByteBuffer.wrap(newData);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		// Process ByteBuffer and return new values
		ArchiveVDisplayType[] newValues = decodeDataUndecToDec(bb,
				getSampleCount(bytesToRead), blockSize, offset, coordinate,
				decimation);

		offset = 0;
		noNewValuesOccurence = 0;
		return newValues;
	}

	private void reconnect() throws IOException {
		// close old socket and inputStream
		inFromServer.close();
		socket.close();

		// Make a connection to the Fast Archiver
		socket = new Socket(host, port);
		String request = String.format("S%dTE\n", bpm);
		socket.getOutputStream().write(request.getBytes(CHAR_ENCODING));
		socket.getOutputStream().flush();
		inFromServer = new BufferedInputStream(socket.getInputStream(),
				buffersize);

		try {
			decodeInitialData();
		} catch (FADataNotAvailableException e) {
			return; // try connecting again on next call of fetchNewValues(int)
		}

		noNewValuesOccurence = 0;
	}

	/**
	 * Should be called when the connection to the archiver becomes redundant
	 */
	public synchronized void close() {
		try {
			inFromServer.close();
			socket.close();
			closed = true;
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

	/**
	 * Returns the class of this object followed by the given URL, BPM number
	 * and coordinate.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass() + "\n");
		buffer.append("URL: " + url + ", BPM number: " + bpm + ", coordinate: "
				+ coordinate);
		return buffer.toString();
	}

}
