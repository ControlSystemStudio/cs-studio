package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.time.Timestamp;


/**
 * Class to communicate with Fast Archiver about archived data requests. 
 * @author pvw24041
 */

public class FastArchiverArchivedDataRequest extends FastArchiverRequest{
	
	//need to change for optional host????
	public FastArchiverArchivedDataRequest(String url) {
		super(url);
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
				 
		// TODO Auto-generated method stub
		return null; //getValues(name, start, end, );
	}
	
	// SOCKET METHODS
	
	// STATIC METHODS
	
	/**
	 * Translates the given dates and bpm number into a String for a request to
	 * the fa-archiver
	 */
	private static String translate(Timestamp start, Timestamp end, int bpm, Decimation dec) {
		// Needs format
		// "R[decimation]M[number of BPM][start time in seconds from epoch]E[end time in seconds from epoch]N\n"
		String decimation = "F"; // need way to make this variable
		if (dec == Decimation.UNDEC){
			decimation = "F";
		} else if (dec == Decimation.DEC){
			decimation = "D";
		} else if (dec == Decimation.DOUBLE_DEC){
			decimation = "DD";
		}
		return String.format("R%sM%dT%sET%sN\n", decimation, bpm, start.getSec(), end.getSec());
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

}
