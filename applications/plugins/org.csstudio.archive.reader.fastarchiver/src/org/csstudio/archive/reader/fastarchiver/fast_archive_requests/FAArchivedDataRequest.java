package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.FAValueIterator;
import org.csstudio.archive.reader.fastarchiver.exceptions.DataNotAvailableException;
import org.csstudio.archive.reader.fastarchiver.exceptions.EndOfStreamException;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;

/**
 * Class to communicate with Fast Archiver about archived data requests. 
 * @author Friederike Johlinger
 */

public class FAArchivedDataRequest extends FARequest{
	
	//public enum ValueType {MEAN, MIN, MAX, STD}
	//ValueType defaultValueType;
	HashMap<String, int[]> bpmMapping;
	int sampleFrequency;
	int firstDecimation;
	
	public FAArchivedDataRequest(String url, HashMap<String, int[]> bpmMapping) throws IOException
	{
		super(url);
		this.bpmMapping = bpmMapping;
		//defaultValueType = ValueType.MEAN;
		initialiseServerSettings();
	}
	
	
	// PUBLIC METHODS
	/**
	 * Used to get undecimated data out of the archiver.
	 * @param name String 
	 * @param start timestamp of first sample
	 * @param end timestamp of last sample
	 * @return
	 */
	public ValueIterator getRawValues(String name, Timestamp start,
			Timestamp end) {
		// create request string
		int bpm = bpmMapping.get(name)[0];
		int coordinate = bpmMapping.get(name)[1];
		int dataSet = bpmMapping.get(name)[2];
		String request = translate(start, end, bpm, Decimation.UNDEC, dataSet);
		// make request, returning ValueIterator 

		try {
			return getValues(request, start, end, coordinate);
		} catch (IOException | EndOfStreamException | DataNotAvailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ValueIterator getOptimisedValues(String name, Timestamp start,
			Timestamp end, int count) throws IOException {
		
		// create request string
		int bpm = bpmMapping.get(name)[0];
		int coordinate = bpmMapping.get(name)[1];
		int dataSet = bpmMapping.get(name)[2];
		Decimation dec = calculateDecimation(start, end, count);
		String request = translate(start, end, bpm, dec, dataSet);
		// make request, returning ValueIterator 

		try {
			
			return getValues(request, start, end, coordinate);
		} catch (IOException | EndOfStreamException | DataNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	// SOCKET METHODS
	
	//! need to implement
	private Decimation calculateDecimation(Timestamp start, Timestamp end,
			int count) throws IOException {
		int maxNoOfSamples = (int) (count * 1.1); // small margin
		// calculate total timeInterval requested (and available?)
		long seconds = (start.durationBetween(end)).getSec();
		//System.out.printf("calculateDecimation: %d\n", seconds);
		// calculate for which decimation, approximately count samples are returned
		if (seconds*sampleFrequency <= maxNoOfSamples)
			return Decimation.UNDEC;
		else if ((seconds*sampleFrequency)/firstDecimation <= maxNoOfSamples)
			return Decimation.DEC;
		else return Decimation.DOUBLE_DEC;
		
	}
	
	private void initialiseServerSettings() throws IOException {
		// create socket
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();
		
		// write request to archiver for sample frequency
		writeToArchive("CFd\n", outToServer);
		
		//read message
		byte[] bA = new byte[200];
		int read = inFromServer.read(bA);
		String message = new String(Arrays.copyOfRange(bA, 0, read));
		socket.close();
		
		Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)\n([0-9]+)\n");
		Matcher matcher = pattern.matcher(message);
		if (matcher.matches()) {
			sampleFrequency = Integer.parseInt(matcher.group(1));
			firstDecimation = Integer.parseInt(matcher.group(3));
		}
		else throw new IOException("Pattern does not match String");
	}

	/**
	 * @param coordinate 0 or 1 indicating the x or y coordinate, respectively
	 * data is form [zero char][sample count(8b)][block size(4b)][offset(4b)] ([timestamp(8b)][duration(4b)][datasets(4b*blocksize)])*N 
	 */
	private ValueIterator getValues(String request, Timestamp start, Timestamp end, int coordinate) throws UnknownHostException, IOException, EndOfStreamException, DataNotAvailableException {
		// create socket
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();
		
		// write request to archiver
		writeToArchive(request, outToServer);
		
		/* Check if first byte reply is zero -> data is sent */
		byte[] firstChar = readNumBytes(1, inFromServer);
		if (firstChar[0] != '\0') {
			String message = getServerErrorMessage(firstChar[0], inFromServer);
			socket.close();
			throw new DataNotAvailableException(message);
		}

		// get initial data out
		int lengthInitData = 16;
		ByteBuffer bb = ByteBuffer.wrap(readNumBytes(lengthInitData, inFromServer));
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		

		long sampleCount = bb.getLong(); //number of samples sent
		int blockSize = bb.getInt(); // samples per block
		int offset = bb.getInt(); // offset samples in first block

		/* Get actual data */
		int numBytesToRead = calcDataLength((int)sampleCount, blockSize, offset);
		//System.out.println("cast samplecount: "+ (int)sampleCount);
		//System.out.println("NBtR: "+numBytesToRead + ", samplecount: " +sampleCount+ ", offset: "+offset);

		bb = ByteBuffer.wrap(readNumBytes(numBytesToRead, inFromServer));
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		socket.close();
		
		ArchiveVNumber[] values = decodeData(bb, (int)sampleCount, blockSize, offset, coordinate);
		return new FAValueIterator(values); 
	}
		
	private static ArchiveVNumber[] decodeData(ByteBuffer bb,int sampleCount, int blockSize, int offset, int coordinate){
		ArchiveVNumber[] values = new ArchiveVNumber[(int)sampleCount];
		
		bb.position(0);
		int value;
		long timestamp;
		//long newTimestamp;
		int duration;
		
		if (offset != 0) {
			timestamp = bb.getLong();
			duration = bb.getInt();
			timestamp += offset*duration/blockSize;
			//System.out.println("first timestamp");
			//System.out.println("TimeStamp: "+timestamp+", Duration: "+duration+", offset: "+offset);
		} else {
			timestamp = 0;//should be really initialised later on
			duration = 0;
		}
		for(int indexValues = 0; indexValues < sampleCount; indexValues += 1){
			//when to read in timeStamps and durations
			if ((indexValues+offset)%blockSize == 0){
				timestamp = bb.getLong();
				/*newTimestamp = bb.getLong();
				if (newTimestamp - timestamp > 15000000) System.out.println("DecodeData: Gap in time");
				timestamp = newTimestamp;*/
				if (timestamp < 0){
					System.out.println("Negative time");
				}
				duration = bb.getInt();
				//System.out.println("next timestamp");
			}
			if (coordinate == 0){
				value = bb.getInt();
				bb.getInt();
			} else {
				bb.getInt();
				value = bb.getInt();
			}
			
			//System.out.println("Value: "+value+", TimeStamp: "+timestamp+", Duration: "+duration);
			double valueDouble = value/1000.0; //micrometers
			values[indexValues] = new ArchiveVNumber(timeStampFromMicroS(timestamp), AlarmSeverity.NONE, "status", null, valueDouble);
			timestamp += duration/blockSize;
		}
		
		return values;
	}

	/*private byte[] getTimeStamp(String request) throws UnknownHostException,
			IOException {
		// get time for oldest data (seconds from Unix UTC epoch)
		byte[] buffer = new byte[TIMESTAMP_BYTE_LENGTH];
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();
		// sent request to archive
		writeToArchive(request, outToServer);
		inFromServer.read(buffer);
		socket.close();
		return buffer;

	}*/
	
	// METHODS USING SOCKET STREAMS

	/**
	 * Returns a byte Array with length as specified filled with data from the
	 * InputStream
	 * @return byte[]
	 */
	private static byte[] readNumBytes(int length, InputStream inFromServer)
			throws EndOfStreamException {
		//System.out.println("length: "+length);
		byte[] buffer = new byte[length];
		int read = 0;
		int lastRead = 0;

		try {
			while (read != length) {
				lastRead = inFromServer.read(buffer, read, (length - read));
				if (lastRead != -1) {
					read += lastRead;
				} else {
					throw new EndOfStreamException("Not enough bytes to read");
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
	 */
	private String getServerErrorMessage(byte firstChar,
			InputStream inFromServer) {
		// read data from stream
		int length = 100; // !! What length should be used?
		byte[] buffer = new byte[length];
		buffer[0] = firstChar;

		int read = 1;
		int lastRead = 0;
		/*try {
			read = inFromServer.read(buffer);
			buffer = Arrays.copyOfRange(buffer, 0, read);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
		
	// STATIC METHODS
	
	/**
	 * Translates the given dates and bpm number into a String for a request to
	 * the fa-archiver
	 */
	private static String translate(Timestamp start, Timestamp end, int bpm, Decimation dec, int dataSetNo) {
		// Needs format
		// "R[decimation]M[number of BPM][start time in seconds from epoch]E[end time in seconds from epoch]N[include sample count]A[all data available]\n"
		String decimation = "F"; // need way to make this variable always dependent
		if (dec == Decimation.UNDEC){
			decimation = "F";
		} else if (dec == Decimation.DEC){
			decimation = "D";
		} else if (dec == Decimation.DOUBLE_DEC){
			decimation = "DD";
		}
		//System.out.println("Decimation = "+ decimation);
		String dataSet;
		if (dec == Decimation.UNDEC) dataSet = "";
		else dataSet = String.format("F%d", dataSetNo);
		//System.out.println("dataSet = "+dataSet); //RFM1S1405096100ES1405096101
		return String.format("R%s%sM%dS%d.%dES%d.%dNATE\n", decimation, dataSet, bpm, start.getSec(), start.getNanoSec(), end.getSec(), end.getNanoSec());
		//return "RDDF1M4S1405417272ES1405417274NATE\n";
	}
	
	/**
	 * Forms a long integer from a byte array
	 */
	/*private static long longFromByteArray(byte[] bA) {
		ByteBuffer bb = ByteBuffer.wrap(bA);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong();
	}*/

	/**
	 * Forms an integer from a byte array
	 */
	/*
	private static int intFromByteArray(byte[] bA) {
		ByteBuffer bb = ByteBuffer.wrap(bA);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}*/
	
	private static int calcDataLength(int sampleCount, int blockSize, int offset) {
		int sampleLength = 12 + 8 * blockSize;  // 8 bytes timestamp + 4 bytes duration + (4 bytes * 2 coordinates) * blockSize 
		int length = 0;
		if (sampleCount + offset > blockSize) { // more than one block of data 
			length += 12 + (blockSize - offset) * 8; // length first block
			length += sampleLength
					* ((sampleCount - (blockSize - offset)) / blockSize); // complete
																			// blocks
			if ((sampleCount + offset) % blockSize != 0)
				length += 12 + ((sampleCount + offset) % blockSize) * 8;
		} else
			length += 12 + 8 * sampleCount;
		return length;
	}

	private static Timestamp timeStampFromMicroS(long timeInMicroS) {
		long seconds = timeInMicroS/1000000;
		int nanoseconds = (int)(timeInMicroS%1000000)*1000;
		return Timestamp.of(seconds, nanoseconds);
	}

	// OTHER METHODS
	
	/**
	 * Checks if the specified time interval starts after oldest data and stops
	 * before latest data
	 * 
	 * @throws DataNotAvailableException
	 */
	/*private boolean dataAvailable(Timestamp start, Timestamp end)
			throws DataNotAvailableException {

		// get time for oldest data (seconds from Unix UTC epoch)
		byte[] buffer;
		try {
			buffer = getTimeStamp("CT\n");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataNotAvailableException("Could not get timestamp");
		}
		// Convert to long and compare to wanted start
		String StringFromBuffer = new String(buffer);
		long earliestSampleTime = new Long(StringFromBuffer.substring(0, 10));
		boolean tooEarly = (start.getSec()) < earliestSampleTime;

		// get time for latest data (seconds from Unix UTC epoch)
		try {
			buffer = getTimeStamp("CU\n");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataNotAvailableException("Could not get timestamp");
		}
		// Convert to long and compare to wanted end
		StringFromBuffer = new String(buffer);
		long latestSampleTime = new Long(StringFromBuffer.substring(0, 10));
		boolean tooLate = (end.getSec()) > latestSampleTime;
		return (!tooEarly && !tooLate && start.getSec() <= end.getSec());
	}
	*/
}
