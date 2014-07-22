package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.FAValueIterator;
import org.csstudio.archive.reader.fastarchiver.exceptions.DataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;

/**
 * Class to communicate with Fast Archiver about archived data requests.
 * 
 * @author Friederike Johlinger
 */

public class FAArchivedDataRequest extends FARequest {

	// public enum ValueType {MEAN, MIN, MAX, STD}
	// ValueType defaultValueType;
	HashMap<String, int[]> bpmMapping;
	int sampleFrequency;
	int firstDecimation;
	int secondDecimation;

	/**
	 * 
	 * @param url
	 *            needs to start with "fads://" followed by the host name and
	 *            optionally a colon followed by a port number (default 8888)
	 * @param bpmMapping
	 *            a Hashmap containing the mapping from BPM names to BPM
	 *            numbers, coordinates and dataSet wanted. Can be obtained
	 *            through FAInfoRequest.createMapping();
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 */
	public FAArchivedDataRequest(String url, HashMap<String, int[]> bpmMapping)
			throws IOException {
		super(url);
		this.bpmMapping = bpmMapping;
		// defaultValueType = ValueType.MEAN;
		initialiseServerSettings();
	}

	// PUBLIC METHODS
	/**
	 * Used to get undecimated data out of the archiver.
	 * 
	 * @param name
	 *            name of BPM also specifying the coordinate and the data set wanted 
	 * @param start
	 *            timestamp of first sample
	 * @param end
	 *            timestamp of last sample
	 * @return a ValueIterator with the samples requested as VTypes
	 * @throws IOException when no connection can be made with the host (and port)
	 *             specified
	 * @throws DataNotAvailableException when data can not be retrieved from Archive
	 */
	public ValueIterator getRawValues(String name, Timestamp start,
			Timestamp end) throws DataNotAvailableException, IOException {
		// create request string
		int bpm = bpmMapping.get(name)[0];
		int coordinate = bpmMapping.get(name)[1];
		int dataSet = bpmMapping.get(name)[2];
		String request = translate(start, end, bpm, Decimation.UNDEC, dataSet);
		// make request, returning ValueIterator

		return getValues(request, start, end, coordinate, Decimation.UNDEC);

	}

	/**
	 * Used to get optimised data out of the archiver, decimation dependent on value of count
	 * 
	 * @param name
	 *            name of BPM also specifying the coordinate and the data set wanted 
	 * @param start
	 *            timestamp of first sample
	 * @param end
	 *            timestamp of last sample
	 * @param count the approximately maximum number of samples returned via the ValueIterator
	 * @return ValueIterator with the samples requested as VTypes
	 * @throws IOException when no connection can be made with the host (and port)
	 *             specified
	 * @throws DataNotAvailableException when data can not be retrieved from archive
	 */
	public ValueIterator getOptimisedValues(String name, Timestamp start,
			Timestamp end, int count) throws IOException, DataNotAvailableException {

		// create request string
		int bpm = bpmMapping.get(name)[0];
		int coordinate = bpmMapping.get(name)[1];
		int dataSet = bpmMapping.get(name)[2];
		Decimation dec = calculateDecimation(start, end, count);
		String request = translate(start, end, bpm, dec, dataSet);
		//System.out.print(request);
		
		
		return getValues(request, start, end, coordinate, dec);
	
	}

	// SOCKET METHODS
	
	/**
	 * Used by constructor to initialise sampleFrequency and firstDecimation with information from the Fast Archiver
	 * @throws IOException when no connection can be made with the host (and port)
	 *             specified
	 */
	private void initialiseServerSettings() throws IOException {
		// create socket
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();

		// write request to archiver for sample frequency
		writeToArchive("CFdD\n", outToServer);

		// read message
		byte[] bA = new byte[200];
		int read = inFromServer.read(bA);
		String message = new String(Arrays.copyOfRange(bA, 0, read));
		socket.close();

		Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)\n([0-9]+)\n([0-9]+)\n");
		Matcher matcher = pattern.matcher(message);
		if (matcher.matches()) {
			sampleFrequency = Integer.parseInt(matcher.group(1));
			firstDecimation = Integer.parseInt(matcher.group(3));
			secondDecimation = firstDecimation * Integer.parseInt(matcher.group(4));
		} else
			throw new IOException("Pattern does not match String");
	}

	/**
	 * Creates a ValueIterator which returns the samples in the specified time interval as VTypes (ArchiveVNumbers)
	 * 
	 * @param request message to server with request for data
	 * @param start Timestamp of first sample
	 * @param end Timestamp of last sample
	 * @param coordinate 0 or 1 indicating the x or y coordinate, respectively data is
	 *            form [zero char][sample count(8b)][block size(4b)][offset(4b)]
	 *            ([timestamp(8b)][duration(4b)][datasets(8b*blocksize)])*N
	 * @return ValueIterator (FAValueIterator) 
	 * @throws IOException when no connection can be made with the host (and port)
	 *             specified
	 * @throws DataNotAvailableException when data can not be retrieved from Archive
	 */
	private ValueIterator getValues(String request, Timestamp start,
			Timestamp end, int coordinate, Decimation decimation) throws DataNotAvailableException, IOException {
		// create socket
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();

		//System.out.println(request);
		// write request to archiver
		writeToArchive(request, outToServer);

		/* Check if first byte reply is zero -> data is sent */
		byte[] firstChar;
		firstChar = readNumBytes(1, inFromServer);
		if (firstChar[0] != '\0') {
			String message = getServerErrorMessage(firstChar[0], inFromServer);
			socket.close();
			throw new DataNotAvailableException(message);
		}

		// get initial data out
		int lengthInitData = 16;
		ByteBuffer bb = ByteBuffer.wrap(readNumBytes(lengthInitData,
				inFromServer));
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		long sampleCount = bb.getLong(); // number of samples sent
		int blockSize = bb.getInt(); // samples per block
		int offset = bb.getInt(); // offset samples in first block

		
		/* Get actual data */
		ArchiveVDisplayType[] values;
		if (decimation == Decimation.UNDEC){
					int numBytesToRead = calcDataLengthUndec((int) sampleCount, blockSize,
				offset);
		// System.out.println("cast samplecount: "+ (int)sampleCount);
		// System.out.println("NBtR: "+numBytesToRead + ", samplecount: "
		// +sampleCount+ ", offset: "+offset);

		bb = ByteBuffer.wrap(readNumBytes(numBytesToRead, inFromServer));
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		socket.close();

		values = decodeDataUndec(bb, (int) sampleCount, blockSize, offset, coordinate);
		} else {
			int numBytesToRead = calcDataLengthDec((int) sampleCount, blockSize,
					offset);
			
			// take outside?
			bb = ByteBuffer.wrap(readNumBytes(numBytesToRead, inFromServer));
			bb.position(0);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			socket.close();
			
			values = decodeDataDec(bb, (int) sampleCount, blockSize,
					offset, coordinate, decimation);
			
		}

		return new FAValueIterator(values);
	}

	// METHODS USING SOCKET STREAMS



	/**
	 * Returns a Byte Array with length as specified filled with data from the
	 * InputStream specified
	 * 
	 * @param length number of bytes required
	 * @param inFromServer inFromServer InputStream from Socket connected to Archive
	 * 
	 * @return byte[]
	 * @throws DataNotAvailableException when requested number of bytes can not be read from the archive
	 */
	private static byte[] readNumBytes(int length, InputStream inFromServer)
			throws DataNotAvailableException {
		byte[] buffer = new byte[length];
		int read = 0;
		int lastRead = 0;

		try {
			while (read != length) {
				lastRead = inFromServer.read(buffer, read, (length - read));
				if (lastRead != -1) {
					read += lastRead;
				} else {
					throw new DataNotAvailableException("End of stream reached before expected");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * In case the message written to the server is invalid, it returns an error
	 * message. This function is used to convert this message to a String.
	 * Needs first Byte separately, as this is used before to check whether the server returned an error message or data
	 * @param firstChar first Byte read from the InputStream
	 * @param inFromServer InputStream from Socket connected to Archive
	 * @return Error message as a String
	 */
	private static String getServerErrorMessage(byte firstChar,
			InputStream inFromServer) {
		// read data from stream
		int length = 100; // !! What length should be used?
		byte[] buffer = new byte[length];
		buffer[0] = firstChar;

		int read = 1;
		int lastRead = 0;
		try {
			while (read != length) {
				lastRead = inFromServer.read(buffer, read, (length - read));
				if (lastRead != -1) {
					read += lastRead;
				} else {
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// convert to String and return
		String message = new String(buffer);

		return message;
	}
	
	// OTHER METHODS

	/**
	 * Uses the sample frequency, decimation factor and length of the time interval to calculate the right decimation factor to use for data request from Archive

	 * @param start
	 *            Timestamp of first sample
	 * @param end
	 *            Timestamp of last sample
	 * @param count the approximately maximum number of samples returned via the ValueIterator
	 * @return a value of the enum Decimation
	 */
	private Decimation calculateDecimation(Timestamp start, Timestamp end,
			int count) {
		int maxNoOfSamples = (int) (count * 1.1); // small margin
		// calculate total timeInterval requested
		long seconds = (start.durationBetween(end)).getSec();
		// System.out.printf("calculateDecimation: %d\n", seconds);
		// calculate for which decimation, approximately count samples are
		// returned
		if (seconds * sampleFrequency <= maxNoOfSamples)
			return Decimation.UNDEC;
		else if ((seconds * sampleFrequency) / firstDecimation <= maxNoOfSamples)
			return Decimation.DEC;
		else
			return Decimation.DOUBLE_DEC;

	}

	// STATIC METHODS
	/**
	 * Translates the given dates, BPM number, Decimation and datSet number into a String for a request to
	 * the Fast Archiver
	 */
	private static String translate(Timestamp start, Timestamp end, int bpm,
			Decimation dec, int dataSetNo) {
		// Needs format
		// "R[decimation]M[number of BPM][start time in seconds from epoch]E[end time in seconds from epoch]N[include sample count]A[all data available]\n"
		String decimation = "F"; // need way to make this variable always
									// dependent
		if (dec == Decimation.UNDEC) {
			decimation = "F";
		} else if (dec == Decimation.DEC) {
			decimation = "D";
		} else if (dec == Decimation.DOUBLE_DEC) {
			decimation = "DD";
		}
		// System.out.println("Decimation = "+ decimation);
		String dataSet = "";
		/*if (dec == Decimation.UNDEC)
			dataSet = "";
		else
			dataSet = String.format("F%d", dataSetNo);
		//return "RDF2M5S1405945635.711000000ES1405945675.347000000NATE\n"; // Used to find bug in time
		// System.out.println("dataSet = "+dataSet); */
		String request = String.format("R%s%sM%dS%d.%09dES%d.%09dNATE\n", decimation,
				dataSet, bpm, start.getSec(), start.getNanoSec(), end.getSec(),
				end.getNanoSec());
		return request;
	}

	/**
	 * Calculates the number of bytes to read from the InputStream for the data retrieval dependent on:
	 * @param sampleCount the total number of samples returned
	 * @param blockSize the general number of samples per data block
	 * @param offset the offset (number of missing samples) in the first datablock
	 * @return the number of bytes to read from the InputStream
	 */
	private static int calcDataLengthUndec(int sampleCount, int blockSize, int offset) {
		int blockLength = 12 + 8 * blockSize; // 8 bytes timestamp + 4 bytes
												// duration + (4 bytes * 2
												// coordinates) * blockSize
		int length = 0;
		if (sampleCount + offset > blockSize) { // more than one block of data
			length += 12 + (blockSize - offset) * 8; // length first block
			length += blockLength
					* ((sampleCount - (blockSize - offset)) / blockSize); // complete
																			// blocks
			if ((sampleCount + offset) % blockSize != 0)
				length += 12 + ((sampleCount + offset) % blockSize) * 8;
		} else
			length += 12 + 8 * sampleCount;
		return length;
	}
	

	private static int calcDataLengthDec(int sampleCount, int blockSize, int offset) {
		int blockLength = 12 + 8 * 4 * blockSize; // 8 bytes timestamp + 4 bytes
												// duration + (4 bytes * 2
												// coordinates * 4 dataSets) * blockSize
		int length = 0;
		if (sampleCount + offset > blockSize) { // more than one block of data
			length += 12 + (blockSize - offset) * 8 * 4; // length first block
			length += blockLength
					* ((sampleCount - (blockSize - offset)) / blockSize); // complete
			// blocks
			if ((sampleCount + offset) % blockSize != 0)
				length += 12 + ((sampleCount + offset) % blockSize) * 8 * 4;
		} else
			length += 12 + 8 * 4 * sampleCount;
		return length;
	}

	/**
	 * Decodes the raw data from the Archive from a Bytebuffer into an array of ArchiveVNumbers
	 * @param bb the ByteBuffer with the raw data
	 * @param sampleCount the total number of samples returned
	 * @param blockSize the general number of samples per data block
	 * @param offset the offset (number of missing samples) in the first datablock
	 * @param coordinate the index (0 or 1) of the coordinate wanted
	 * @return ArchiveVNumber[] that can be used to create a FAValueIterator
	 */
	private static ArchiveVNumber[] decodeDataUndec(ByteBuffer bb, int sampleCount,
			int blockSize, int offset, int coordinate) {
		ArchiveVNumber[] values = new ArchiveVNumber[(int) sampleCount];
		bb.position(0);
		
		//System.out.printf("DecodeDataUndec: \nsampleCount: %d, blockSize: %d, offset: %d, bufferLength: %d\n", sampleCount, blockSize, offset, bb.remaining());
		int value;
		double timestamp; // in microseconds
		//double oldTimestamp; // for checking
		double duration;
		Timestamp ts; 
		double timeInterval = 0.0; // for checking
		
		if (offset != 0) {
			timestamp = (double)bb.getLong();
			duration = (double)bb.getInt();
			timeInterval = duration / blockSize;
			timestamp += offset * timeInterval;
			//oldTimestamp = timestamp; // for checking
		} else {
			timestamp = 0;// should be really initialised later on
			duration = 0;
			//oldTimestamp = timestamp;

		}
		//  !! oldTimestamp and timestamp have equal value
		
		for (int indexValues = 0; indexValues < sampleCount; indexValues += 1) {
			// when to read in timeStamps and durations
			if ((indexValues + offset) % blockSize == 0) {
				timestamp = (double)bb.getLong();
				//System.out.println(timeStampFromMicroS((long)timestamp).toDate().toString());
				//if(timestamp - oldTimestamp > 4000000 && oldTimestamp != 0) 
					//System.out.printf("DecodeData: gap in time. Old time: %f, new time: %f\n", oldTimestamp, timestamp);
				//if (timestamp < 0) // for checking
					//System.out.println("Negative time");
				duration = (double)bb.getInt();
				timeInterval = duration / blockSize;
				//System.out.printf("DecodeData: timeInterval: %f, duration %f\n", timeInterval, duration);
			}
			if (coordinate == 0) {
				value = bb.getInt();
				bb.getInt();
			} else {
				bb.getInt();
				value = bb.getInt();
			}
			
			double valueDouble = value / 1000.0; // micrometers
			ts = timeStampFromMicroS((long)timestamp);
			values[indexValues] = new ArchiveVNumber(
					ts, AlarmSeverity.NONE,"status", null, valueDouble);

			//oldTimestamp = timestamp;			
			timestamp += timeInterval;
			//if(timestamp - oldTimestamp > 4000000) System.out.println("DecodeData: gap at first increment");
		}
		
		
		return values;
	}
	
	

	private ArchiveVDisplayType[] decodeDataDec(ByteBuffer bb, int sampleCount,
			int blockSize, int offset, int coordinate, Decimation decimation) {
		int count;
		if (decimation == Decimation.DEC) {
			count = firstDecimation;
		} else {
			count = secondDecimation;
		}

		ArchiveVStatistics[] values = new ArchiveVStatistics[(int) sampleCount];
		bb.position(0);

		System.out.printf("DecodeDataDec: \nsampleCount: %d, blockSize: %d, offset: %d, bufferLength: %d\n",
		sampleCount, blockSize, offset, bb.remaining());
		double mean, min, max, std;
		double timestamp; // in microseconds
		double duration;
		Timestamp ts;
		double timeInterval = 0.0; 
		
		if (offset != 0) {
			timestamp = (double) bb.getLong();
			duration = (double) bb.getInt();
			timeInterval = duration / blockSize;
			timestamp += offset * timeInterval;
		} else {
			timestamp = 0;// should be really initialised later on
			duration = 0;
			
		}

		for (int indexValues = 0; indexValues < sampleCount; indexValues += 1) {
			// when to read in timeStamps and durations
			if ((indexValues + offset) % blockSize == 0) {
				timestamp = (double) bb.getLong();

				duration = (double) bb.getInt();
				timeInterval = duration / blockSize;
			}
			if (coordinate == 0) {
				mean = bb.getInt()/1000.0; // micrometers
				bb.getInt();
				min = bb.getInt()/1000.0;
				bb.getInt();
				max = bb.getInt()/1000.0;
				bb.getInt();
				std = bb.getInt()/1000.0;
				bb.getInt();
			} else {
				bb.getInt();
				mean = bb.getInt()/1000.0;
				bb.getInt();
				min = bb.getInt()/1000.0;
				bb.getInt();
				max = bb.getInt()/1000.0;
				bb.getInt();
				std = bb.getInt()/1000.0;
			}

			ts = timeStampFromMicroS((long) timestamp);
			values[indexValues] = new ArchiveVStatistics(ts, AlarmSeverity.NONE, "status", null, mean, min, max, std, count);

			timestamp += timeInterval;
		}

		return values;

	}
/*
	{
		offset = ???;
		read_count = 0;
		while (read_count < target_count)
		{
			read timestamp;
			ix = offset;
			while (ix < block_size && read_count < target_count)
			{
				read sample;
				ix ++;
			}
			offset = 0;
		}
	}
*/
	
	/**
	 * Creates a new Timstamp object from a given value of microseconds from the epoch.
	 * @param timeInMicroS time in microseconds from epoch
	 * @return corresponding Timestamp
	 */
	private static Timestamp timeStampFromMicroS(long timeInMicroS) {
		long seconds = timeInMicroS / 1000000;
		int nanoseconds = (int) (timeInMicroS % 1000000) * 1000;
		//System.out.printf("timestamp: %d, seconds: %d, nanoSec: %d\n", timeInMicroS, seconds, nanoseconds);
		Timestamp ts = Timestamp.of(seconds, nanoseconds);
		if (ts.getSec() != seconds || ts.getNanoSec() != nanoseconds){
			System.out.println("TimestampFromMicros: wrong time conversion in method");
		}
		return ts;
	}

}
