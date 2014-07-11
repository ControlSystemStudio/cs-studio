package from_fa_archiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar; // only imported for testing
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class with methods to request data from the fa-archiver. Assume undecimated
 * data for now. getServerErrorMessage not complete
 * */

public class FaArchiver {

	static final int TIMESTAMP_BYTE_LENGTH = 18;
	static final String CHAR_ENCODING = "US-ASCII";
	String host = "fa-archiver"; // "pc0044"
	int port = 8888;
	public enum Decimation {UNDEC, DEC, DOUBLE_DEC}; 

	public static void main(String[] args) throws UnsupportedEncodingException,
			IOException, EndOfStreamException, InterruptedException,
			DataNotAvailableException {

		FaArchiver fa1 = new FaArchiver();
		Calendar start = new GregorianCalendar(2014, 06, 07, 15, 19, 00);
		Calendar end = new GregorianCalendar(2014, 06, 07, 15, 19, 02);
		int bpm = 1;
		
		String[] allNames = fa1.getNames();
/* 
		 :) Testing translate 
		System.out.println(translate(start, end, bpm)); */
		// should give "RFM1T2014-07-03T17:19:00.000ET2014-07-03T17:19:00.000"

		/* :( Testing getData 
		start = new GregorianCalendar(2014, 06, 07, 15, 19, 00);
		end = new GregorianCalendar(2014, 06, 07, 15, 19, 1);
		bpm = 1;
		System.out.println(fa1.getData(start, end, bpm, Decimation.DEC));*/

		/*
		 * :) Testing longFromByteArray ByteBuffer bb = ByteBuffer.allocate(8);
		 * bb.order(ByteOrder.LITTLE_ENDIAN); byte[] bA =
		 * bb.putLong(1369560789999999641L).array();
		 * System.out.println(longFromByteArray(bA));
		 */
		// should give 1369560789999999641

		/*
		 * :) Testing dataAvailable fa1 = new FaArchiver(); // otherwise broken
		 * pipe System.out.println(fa1.dataAvailable(start, end)); // should be
		 * true System.out.println();*/
		 
	} 

	/**
	 * Get data from specified time interval and BPMs. !!Must decide on encoding
	 * for String decimation
	 */
	public FaData[] getData(Calendar start, Calendar end, int[] bpms, Decimation decimation)
			throws DataNotAvailableException {
		/* Collect data for each bpm and put them into an Array */
		FaData[] data = new FaData[bpms.length];
		for (int i = 0; i < bpms.length; i++) {
			data[i] = getData(start, end, bpms[i], decimation);
		}
		return data;
	}

	/**
	 * Asks fa-archiver for data and returns it in a usable format.
	 * @param start: 
	 * @param end
	 * @param bpm
	 * @return
	 * @throws DataNotAvailableException
	 */
	public FaData getData(Calendar start, Calendar end, int bpm, Decimation decimation)
			throws DataNotAvailableException {
		/* Check if requested data is available */
		// If at start of available time, data will be deleted quickly. Use "A"
		// flag generally when requesting data from server??
		if (!dataAvailable(start, end)) {
			throw new DataNotAvailableException(
					"Specified time outside available dataset");
		}

		/* Else translate given info into request for server */
		String request = translate(start, end, bpm, decimation);
		System.out.println(request); // checking

		byte[] buffer;
		try {
			buffer = getDataStream(request, decimation);
		} catch (IOException | EndOfStreamException e1) {
			e1.printStackTrace();
			throw new DataNotAvailableException(
					"Data could not be requested from archive");
		}

		/*
		 * Print out the hex of what we received, only needed for checking
		 * purposes
		 */
		System.out.println("Reply FA-Archiver: ");
		for (int i = 0; i < buffer.length ; i++) {
			System.out.format(" %02x", buffer[i]);
		}
		System.out.println();

		FaData data;
		/* Decode data into readable format */
		if (decimation == Decimation.UNDEC){
			int[][] dataArrays = makeCoordinatesArrayUndec(buffer, buffer.length / 8);
			data = new FaDataUndec(start, end, dataArrays[0], dataArrays[1], request);
		} else {
			int[][] dataArrays = makeCoordinatesArrayDec(buffer, buffer.length / 32);
			data = new FaDataDec(start, end, dataArrays, request);
		}
		/* Return to user */
		return data;
	}

	/**
	 * Checks if the specified time interval starts after oldest data and stops
	 * before latest data
	 * 
	 * @throws DataNotAvailableException
	 */
	// !! Need to find out format of data returned by CF and CU, does not work
	// yet
	private boolean dataAvailable(Calendar start, Calendar end)
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
		boolean tooEarly = (start.getTimeInMillis() / 1000) < earliestSampleTime;

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
		boolean tooLate = (end.getTimeInMillis() / 1000) > latestSampleTime;
		return (!tooEarly && !tooLate && start.getTimeInMillis() <= end
				.getTimeInMillis());
	}

	/**
	 * Translates the given dates and bpm number into a String for a request to
	 * the fa-archiver
	 */
	private static String translate(Calendar start, Calendar end, int bpm, Decimation dec) {
		// Needs format
		// "R[decimation]M[number of BPM]T2014-07-03T9:0:0(start time)ET2014-07-03T9:0:2(end time)N\n"
		String decimation = "F"; // need way to make this variable
		if (dec == Decimation.UNDEC){
			decimation = "F";
		} else if (dec == Decimation.DEC){
			decimation = "D";
		} else if (dec == Decimation.DOUBLE_DEC){
			decimation = "DD";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		return String.format("R%sM%dT%sET%sN\n", decimation, bpm,
				sdf.format(start.getTime()), sdf.format(end.getTime()));
	}

	/**
	 * Forms a long integer from a byte array
	 */
	private static long longFromByteArray(byte[] bA) {
		ByteBuffer bb = ByteBuffer.wrap(bA);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong();
	}

	/**
	 * Forms an integer from a byte array
	 */
	private static int intFromByteArray(byte[] bA) {
		ByteBuffer bb = ByteBuffer.wrap(bA);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	/** Creates int[] from the byteArray from the archiver */
	private static int[][] makeCoordinatesArrayUndec(byte[] buffer, int sampleCount) {
		int[][] dataArrays = new int[2][sampleCount];
		for (int i = 0; i < sampleCount * 2; i++) {
			byte[] coordinate = Arrays.copyOfRange(buffer, i*4, (i+1)*4);
			// even i -> x-coordinate, odd i -> y-coordinate
			dataArrays[i % 2][i / 2] = intFromByteArray(coordinate);
		}
		return dataArrays;
	}
	
	/** Creates int[] from the byteArray from the archiver */
	private static int[][] makeCoordinatesArrayDec(byte[] buffer, int sampleCount) {
		int[][] dataArrays = new int[8][sampleCount];
		for (int i = 0; i < sampleCount * 8; i++) {
			byte[] coordinate = Arrays.copyOfRange(buffer, i*4, (i+1)*4);
			// even i -> x-coordinate, odd i -> y-coordinate
			dataArrays[i%8][i / 8] = intFromByteArray(coordinate);
		}
		return dataArrays;
	}

	/* METHODS USING SOCKETS DIRECTLY */

	/**
	 * Requests data from the archiver and returns the data as a byte[]
	 * 
	 * @throws DataNotAvailableException
	 * @throws IOException
	 * @throws EndOfStreamException
	 * 
	 */
	private byte[] getDataStream(String request, Decimation decimation)
			throws DataNotAvailableException, IOException, EndOfStreamException {

		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();
		// sent request to archive
		writeToArchive(request, outToServer);
		/* Check if first byte reply is zero -> data is sent */
		// change into ifError(inFromServer);
		byte[] firstChar;
		firstChar = readNumBytes(1, inFromServer);
		// checking for error messages
		if (firstChar[0] != '\0') {
			String message = getServerErrorMessage(firstChar[0], inFromServer);
			socket.close();
			throw new DataNotAvailableException(message);
		}

		/* Get number of samples sent, first 8 bytes */
		byte[] sampleCountData;
		sampleCountData = readNumBytes(8, inFromServer);
		long sampleCount = longFromByteArray(sampleCountData);
		System.out.println("SampleCount (gDS): " + sampleCount);

		
		/* Get actual data */
		// Would sample number be bigger than int can represent?
		long dataLength;
		if (decimation == Decimation.UNDEC){
			dataLength = sampleCount * 8; // 4 bytes * 2 coordinates
		} else {
			dataLength = sampleCount * 32; // 4 bytes * 2 coordinates * 4 decimations
		}
		byte[] buffer = new byte[(int)dataLength];
		try {
			buffer = readNumBytes((int)dataLength, inFromServer);
		} catch (EndOfStreamException e) {
			e.printStackTrace();
		}
		socket.close();
		return buffer;
	}

	private byte[] getTimeStamp(String request) throws UnknownHostException,
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

	}
	
	
	/**
	 * To get text output from the archiver
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws EndOfStreamException 
	 */
	private String[] getNames() throws UnknownHostException, IOException, EndOfStreamException {
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
			buffer = new byte[50];
			allIds.append(infoFromServer);
			readNumBytes = inFromServer.read(buffer);
			infoFromServer = new String(buffer);
			
		}
		System.out.println("ID's: "+allIds);
		
		socket.close();
		String [] names = allIds.toString().split("\n");

		return names;
	}
	
	public int getNumOfIDs() throws UnknownHostException, IOException, EndOfStreamException{
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();		
		
		// get number of id's
		byte[] buffer = new byte[4];
		writeToArchive("CK\n", outToServer);
		buffer = readNumBytes(buffer.length, inFromServer);
		String numberAsString = new String(buffer);
		Scanner sc = new Scanner(numberAsString);
		int numOfIDs = sc.nextInt();
		
		socket.close();
		sc.close();
		return numOfIDs;
	}

	/* METHODS USING SOCKET STREAMS */

	/**
	 * Returns a byte Array with length as specified filled with data from the
	 * InputStream
	 * @return byte[]
	 */
	private byte[] readNumBytes(int length, InputStream inFromServer)
			throws EndOfStreamException {
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

	/**
	 * Writes a message to the fa-archiver to request data, only to be used by
	 * methods creating sockets
	 */
	private static void writeToArchive(String message, OutputStream outToServer) {
		try {
			outToServer.write(message.getBytes(CHAR_ENCODING));
			outToServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
