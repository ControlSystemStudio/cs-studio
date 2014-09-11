package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.FAValueIterator;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.util.time.Timestamp;

/**
 * Class to communicate with Fast Archiver about archived data requests.
 * 
 * @author FJohlinger
 */

public class FAArchivedDataRequest extends FARequest {

	HashMap<String, int[]> bpmMapping;
	int sampleFrequency;
	int firstDecimation;
	int secondDecimation;

	// Extra_Dec allows a further decimation by 100
	int extraDecimation = 100;
	
	protected enum Decimation {
		UNDEC, DEC, DOUBLE_DEC, EXTRA_DEC
	};

	/**
	 * @param url
	 *            needs to start with "fads://" followed by the host name and
	 *            optionally a colon followed by a port number (default 8888)
	 * @param bpmMapping
	 *            a HashMap containing the mapping from BPM names to BPM
	 *            numbers, coordinates and dataSet wanted. Can be obtained
	 *            through FAInfoRequest.createMapping();
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 * @throws FADataNotAvailableException
	 *             when the URL doesn't have the right format
	 */
	public FAArchivedDataRequest(String url, HashMap<String, int[]> bpmMapping)
			throws IOException, FADataNotAvailableException {
		super(url);
		this.bpmMapping = bpmMapping;
		initialiseServerSettings();
	}

	// PUBLIC METHODS
	/**
	 * Used to get undecimated data out of the archiver.
	 * 
	 * @param name
	 *            name of BPM also specifying the coordinate and the data set
	 *            wanted
	 * @param start
	 *            Timestamp of first sample
	 * @param end
	 *            Timestamp of last sample
	 * @return a ValueIterator with the samples requested as VTypes
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 * @throws FADataNotAvailableException
	 *             when data can not be retrieved from Archive
	 */
	public ValueIterator getRawValues(String name, Timestamp start,
			Timestamp end) throws FADataNotAvailableException, IOException {
		// create request string
		int bpm = bpmMapping.get(name)[0];
		int coordinate = bpmMapping.get(name)[1];
		String request = translate(start, end, bpm, Decimation.UNDEC);

		return getValues(request, start, end, coordinate, Decimation.UNDEC);

	}

	/**
	 * Used to get optimised data out of the archiver, decimation dependent on
	 * value of count
	 * 
	 * @param name
	 *            name of BPM also specifying the coordinate and the data set
	 *            wanted
	 * @param start
	 *            Timestamp of first sample
	 * @param end
	 *            Timestamp of last sample
	 * @param count
	 *            the approximately maximum number of samples returned via the
	 *            ValueIterator
	 * @return ValueIterator with the samples requested as VTypes
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 * @throws FADataNotAvailableException
	 *             when data can not be retrieved from archive
	 */
	public ValueIterator getOptimisedValues(String name, Timestamp start,
			Timestamp end, int count) throws IOException,
			FADataNotAvailableException {

		// create request string
		int bpm = bpmMapping.get(name)[0];
		int coordinate = bpmMapping.get(name)[1];
		Decimation dec = calculateDecimation(start, end, count);
		String request = translate(start, end, bpm, dec);
		return getValues(request, start, end, coordinate, dec);

	}

	/**
	 * Used by constructor to initialise sampleFrequency and firstDecimation
	 * with information from the Fast Archiver
	 * 
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 * @throws FADataNotAvailableException
	 *             when data from archiver has not expected format
	 */
	private void initialiseServerSettings() throws IOException,
			FADataNotAvailableException {
		String message = new String(fetchData("CFdD\n"));

		Pattern pattern = Pattern
				.compile("([0-9]+)\\.([0-9]+)\n([0-9]+)\n([0-9]+)\n");
		Matcher matcher = pattern.matcher(message);
		if (matcher.matches()) {
			sampleFrequency = Integer.parseInt(matcher.group(1));
			firstDecimation = Integer.parseInt(matcher.group(3));
			secondDecimation = firstDecimation
					* Integer.parseInt(matcher.group(4));
		} else
			throw new FADataNotAvailableException("Reply from Archiver does not match Pattern");
	}

	/**
	 * Creates a ValueIterator which returns the samples in the specified time
	 * interval as VTypes (ArchiveVstatistics or ArchiveVNumber)
	 * 
	 * @param request
	 *            message to server with request for data
	 * @param start
	 *            Timestamp of first sample
	 * @param end
	 *            Timestamp of last sample
	 * @param coordinate
	 *            0 or 1 indicating the x or y coordinate, respectively.
	 * @return ValueIterator (FAValueIterator)
	 * @throws IOException
	 *             when no connection can be made with the host (and port)
	 *             specified
	 * @throws FADataNotAvailableException
	 *             when data can not be retrieved from Archive
	 */
	private ValueIterator getValues(String request, Timestamp start,
			Timestamp end, int coordinate, Decimation decimation)
			throws FADataNotAvailableException, IOException {

		ByteBuffer bb = ByteBuffer.wrap(fetchData(request));
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		// data has form [zero char][sample count(8b)][block
		// size(4b)][offset(4b)]
		// ([timestamp(8b)][duration(4b)][blocks with data])*N

		/* Check if first byte reply is zero -> data is sent */
		byte firstChar = bb.get();
		if (firstChar != '\0') {
			String message = new String(bb.array());
			throw new FADataNotAvailableException(message);
		}

		// get initial data out
		long sampleCount = bb.getLong(); // number of samples sent
		int blockSize = bb.getInt(); // samples per block
		int offset = bb.getInt(); // offset samples in first block

		/* Get actual data */
		ArchiveVDisplayType[] values;
		if (decimation == Decimation.UNDEC) {
			int numBytesToRead = calcDataLengthUndec((int) sampleCount,
					blockSize, offset);
			if (bb.remaining() != numBytesToRead)
				throw new FADataNotAvailableException(
						"Data stream does not have expected length");
			values = decodeDataUndec(bb, (int) sampleCount, blockSize, offset,
					coordinate);
		} else {
			int numBytesToRead = calcDataLengthDec((int) sampleCount,
					blockSize, offset);

			if (bb.remaining() != numBytesToRead)
				throw new FADataNotAvailableException(
						"Data stream does not have expected length");
			if (decimation == Decimation.EXTRA_DEC){
				values = decodeDataDecToDec(bb, (int) sampleCount, blockSize,
						offset, coordinate, secondDecimation, extraDecimation);
			} else {
				int count;
				if (decimation == Decimation.DEC) {
					count = firstDecimation;
				} else {
					count = secondDecimation;
				}

				values = decodeDataDec(bb, (int) sampleCount, blockSize,
						offset, coordinate, count);
			}
			
			

		}

		return new FAValueIterator(values);
	}

	/**
	 * Uses the sample frequency, decimation factor and length of the time
	 * interval to calculate the right decimation factor to use for data request
	 * from Archive
	 * 
	 * @param start
	 *            Timestamp of first sample
	 * @param end
	 *            Timestamp of last sample
	 * @param count
	 *            the maximum number of samples returned via the
	 *            ValueIterator
	 * @return a value of the enum Decimation
	 */
	private Decimation calculateDecimation(Timestamp start, Timestamp end,
			int count) {
		int maxNoOfSamples = count;
		// calculate total timeInterval requested
		long seconds = (start.durationBetween(end)).getSec();
		if (seconds * sampleFrequency <= maxNoOfSamples)
			return Decimation.UNDEC;
		else if ((seconds * sampleFrequency) / firstDecimation <= maxNoOfSamples)
			return Decimation.DEC;
		else if ((seconds * sampleFrequency) / firstDecimation <= maxNoOfSamples || seconds < (3600*3)) // 3 hours of data
			return Decimation.DOUBLE_DEC;
		else
			return Decimation.EXTRA_DEC;

	}

	// STATIC METHODS
	/**
	 * Translates the given dates, BPM number and Decimation into a String for a
	 * request to the Fast Archiver
	 */
	private static String translate(Timestamp start, Timestamp end, int bpm,
			Decimation dec) {
		// Needs format:
		// "R[decimation]M[number of BPM][start time in seconds from epoch]
		// E[end time in seconds from epoch]N(include sample count)
		// A(all data available)TE(send with extended timestamps)\n"

		String decimation;
		if (dec == Decimation.UNDEC) {
			decimation = "F";
		} else if (dec == Decimation.DEC) {
			decimation = "D";
		} else {
			decimation = "DD";
		}
		String request = String.format("R%sM%dS%d.%09dES%d.%09dNATE\n",
				decimation, bpm, start.getSec(), start.getNanoSec(),
				end.getSec(), end.getNanoSec());
		return request;
	}

	/**
	 * Calculates the number of bytes to read from the InputStream for the
	 * undecimated data retrieval dependent on:
	 * 
	 * @param sampleCount
	 *            the total number of samples returned
	 * @param blockSize
	 *            the general number of samples per data block
	 * @param offset
	 *            the offset (number of missing samples) in the first datablock
	 * @return the number of bytes to read from the InputStream
	 */
	private static int calcDataLengthUndec(int sampleCount, int blockSize,
			int offset) {
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

	/**
	 * Calculates the number of bytes to read from the InputStream for the
	 * decimated data retrieval dependent on:
	 * 
	 * @param sampleCount
	 *            the total number of samples returned
	 * @param blockSize
	 *            the general number of samples per data block
	 * @param offset
	 *            the offset (number of missing samples) in the first data block
	 * @return the number of bytes to read from the InputStream
	 */
	private static int calcDataLengthDec(int sampleCount, int blockSize,
			int offset) {
		int blockLength = 12 + 8 * 4 * blockSize; // 8 bytes Timestamp + 4 bytes
													// duration + (4 bytes * 2
													// coordinates * 4 dataSets)
													// * blockSize
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

}
