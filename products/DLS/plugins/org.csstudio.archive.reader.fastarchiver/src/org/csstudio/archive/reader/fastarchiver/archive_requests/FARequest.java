package org.csstudio.archive.reader.fastarchiver.archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;

/**
 * Class with common methods for communicating with the fast archiver and
 * decoding data streams.
 * 
 * @author FJohlinger
 */

public abstract class FARequest {

	protected static final int TIMESTAMP_BYTE_LENGTH = 18;
	protected static final String CHAR_ENCODING = "US-ASCII";
	protected String host;
	protected int port;
	protected String url;

	/**
	 * @param url
	 *            needs to start with "fads://" followed by the host name and
	 *            optionally a colon followed by a port number (default 8888)
	 * @throws FADataNotAvailableException
	 *             if the URL does not have the right format
	 */
	public FARequest(String url) throws FADataNotAvailableException {
		this.url = url;
		Pattern pattern = Pattern.compile("fads://([A-Za-z0-9-]+)(:[0-9]+)?");
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			this.host = matcher.group(1);
			if (matcher.group(2) == null)
				this.port = 8888;
			else
				this.port = Integer.parseInt(matcher.group(2).substring(1));
		} else {
			throw new FADataNotAvailableException("Invalid url");
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
	 *             when the connection to the archiver fails
	 */
	protected byte[] fetchData(String request) throws IOException {
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();

		outToServer.write(request.getBytes(CHAR_ENCODING));
		outToServer.flush();

		// Get data out of archive one buffer at a time. Append it to a list of
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

	// DATA STREAM DECODING METHODS
	/**
	 * Decodes the raw data from the Archive from a ByteBuffer into an array of
	 * ArchiveVNumbers
	 * 
	 * @param bb
	 *            the ByteBuffer with the raw data
	 * @param sampleCount
	 *            the total number of samples returned
	 * @param blockSize
	 *            the general number of samples per data block
	 * @param offset
	 *            the offset (number of missing samples) in the first data block
	 * @param coordinate
	 *            the index (0 or 1) of the coordinate wanted
	 * @return ArchiveVNumber[] that can be used to create a FAValueIterator
	 * @throws FADataNotAvailableException
	 *             when coordinate is not 0 or 1
	 */
	protected static ArchiveVNumber[] decodeDataUndec(ByteBuffer bb,
			int sampleCount, int blockSize, int offset, int coordinate)
			throws FADataNotAvailableException {

		if (!(coordinate == 0 || coordinate == 1))
			throw new FADataNotAvailableException(
					"Coordinate mapped to name is invalid");

		ArchiveVNumber[] values = new ArchiveVNumber[(int) sampleCount];

		int value;
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
			timestamp = 0;// Is initialised later on
			duration = 0;
		}
		for (int indexValues = 0; indexValues < sampleCount; indexValues += 1) {
			if ((indexValues + offset) % blockSize == 0) {
				timestamp = (double) bb.getLong();
				duration = (double) bb.getInt();
				timeInterval = duration / blockSize;
			}
			if (coordinate == 0) {
				value = bb.getInt();
				bb.getInt();
			} else {
				bb.getInt();
				value = bb.getInt();
			}

			double valueDouble = value / 1000.0; // micrometers
			ts = timeStampFromMicroS((long) timestamp);
			values[indexValues] = new ArchiveVNumber(ts, AlarmSeverity.NONE,
					"status", null, valueDouble);
			timestamp += timeInterval;
		}

		return values;
	}

	/**
	 * Decodes the raw data from the Archive from a ByteBuffer into an array of
	 * ArchiveVStatistics
	 * 
	 * @param bb
	 *            the ByteBuffer with the raw data
	 * @param sampleCount
	 *            the total number of samples returned
	 * @param blockSize
	 *            the general number of samples per data block
	 * @param offset
	 *            the offset (number of missing samples) in the first data block
	 * @param coordinate
	 *            the index (0 or 1) of the coordinate wanted
	 * @param count
	 *            the approximate decimation of the values
	 * @return ArchiveVStatistics[] that can be used to create a FAValueIterator
	 * @throws FADataNotAvailableException
	 *             when coordinate is not 0 or 1
	 */
	protected static ArchiveVStatistics[] decodeDataDec(ByteBuffer bb,
			int sampleCount, int blockSize, int offset, int coordinate,
			int count) throws FADataNotAvailableException {

		if (!(coordinate == 0 || coordinate == 1))
			throw new FADataNotAvailableException(
					"Coordinate mapped to name is invalid");

		ArchiveVStatistics[] values = new ArchiveVStatistics[(int) sampleCount];

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
			timestamp = 0;// Is initialised later on
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
				mean = bb.getInt() / 1000.0; // micrometers
				bb.getInt();
				min = bb.getInt() / 1000.0;
				bb.getInt();
				max = bb.getInt() / 1000.0;
				bb.getInt();
				std = bb.getInt() / 1000.0;
				bb.getInt();
			} else {
				bb.getInt();
				mean = bb.getInt() / 1000.0;
				bb.getInt();
				min = bb.getInt() / 1000.0;
				bb.getInt();
				max = bb.getInt() / 1000.0;
				bb.getInt();
				std = bb.getInt() / 1000.0;
			}

			ts = timeStampFromMicroS((long) timestamp);
			values[indexValues] = new ArchiveVStatistics(ts,
					AlarmSeverity.NONE, "status", null, mean, min, max, std,
					count);
			timestamp += timeInterval;
		}

		return values;

	}

	/**
	 * Decodes the raw data from the Archive from a ByteBuffer into an array of
	 * ArchiveVNumbers adding a decimation.
	 * 
	 * @param bb
	 *            the ByteBuffer with the raw data
	 * @param sampleCount
	 *            the total number of samples returned
	 * @param blockSize
	 *            the general number of samples per data block
	 * @param offset
	 *            the offset (number of missing samples) in the first data block
	 * @param coordinate
	 *            the index (0 or 1) of the coordinate wanted
	 * @param decimation
	 *            the decimation required by the user
	 * @return ArchiveVStatistics[] that can be used to create a FAValueIterator
	 * @throws FADataNotAvailableException
	 *             when coordinate is not 0 or 1
	 */
	protected static ArchiveVStatistics[] decodeDataUndecToDec(ByteBuffer bb,
			int sampleCount, int blockSize, int offset, int coordinate,
			int decimation) throws FADataNotAvailableException {

		if (!(coordinate == 0 || coordinate == 1))
			throw new FADataNotAvailableException(
					"Coordinate mapped to name is invalid");

		// decode bytes
		double[] values = new double[sampleCount];
		double[] timestamps = new double[sampleCount];

		int value;
		double timestamp; // in microseconds
		double duration;
		double timeInterval = 0.0;

		if (offset != 0) {
			timestamp = (double) bb.getLong();
			duration = (double) bb.getInt();
			timeInterval = duration / blockSize;
			timestamp += offset * timeInterval;
		} else {
			timestamp = 0;// Is initialised later on
			duration = 0;
		}
		for (int valuesIndex = 0; valuesIndex < sampleCount; valuesIndex += 1) {
			if ((valuesIndex + offset) % blockSize == 0) {
				timestamp = (double) bb.getLong();
				duration = (double) bb.getInt();
				timeInterval = duration / blockSize;
			}
			if (coordinate == 0) {
				value = bb.getInt();
				bb.getInt();
			} else {
				bb.getInt();
				value = bb.getInt();
			}

			values[valuesIndex] = value / 1000.0; // micrometers
			timestamps[valuesIndex] = timestamp;
			timestamp += timeInterval;
		}

		// decimate data
		double sum = 0;
		double min = values[0];
		double max = min;

		int newSampleCount = sampleCount / decimation;
		if (sampleCount % decimation != 0)
			newSampleCount++;
		double[][] decimatedValues = new double[newSampleCount][4];
		int indexDec = 0;

		for (int i = 0; i < sampleCount; i++) {
			sum += values[i];
			if (values[i] < min)
				min = values[i];
			else if (values[i] > max)
				max = values[i];

			if ((i + 1) % decimation == 0) {
				decimatedValues[indexDec][0] = sum / decimation;
				decimatedValues[indexDec][1] = min;
				decimatedValues[indexDec][2] = max;
				decimatedValues[indexDec][3] = timestamps[i]
						- (timestamps[i] - timestamps[i - decimation + 1]) / 2;
				indexDec += 1;

				sum = 0;
				// in case of having looped over values[] completely
				try {
					min = values[i + 1];
					max = values[i + 1];
				} catch (ArrayIndexOutOfBoundsException e) {
				}

			}
		}
		if (sampleCount % decimation != 0) {
			decimatedValues[indexDec][0] = sum / (sampleCount % decimation);
			decimatedValues[indexDec][1] = min;
			decimatedValues[indexDec][2] = max;
			decimatedValues[indexDec][3] = timestamps[sampleCount - 1]
					- (timestamps[sampleCount - 1] - timestamps[sampleCount
							- (sampleCount % decimation)]) / 2;
		}

		// calculate standard deviation
		indexDec = 0;
		sum = 0;
		ArchiveVStatistics[] newValues = new ArchiveVStatistics[newSampleCount];
		for (int i = 0; i < sampleCount; i++) {
			sum += Math.pow((values[i] - decimatedValues[indexDec][0]), 2);

			if ((i + 1) % decimation == 0) {
				newValues[indexDec] = new ArchiveVStatistics(
						timeStampFromMicroS((long) decimatedValues[indexDec][3]),
						AlarmSeverity.NONE, "status", null,
						decimatedValues[indexDec][0],
						decimatedValues[indexDec][1],
						decimatedValues[indexDec][2], Math.sqrt(sum
								/ decimation), decimation);
				indexDec += 1;
				sum = 0;
			}
		}
		if (sampleCount % decimation != 0)
			newValues[indexDec] = new ArchiveVStatistics(
					timeStampFromMicroS((long) decimatedValues[indexDec][3]),
					AlarmSeverity.NONE, "status", null,
					decimatedValues[indexDec][0], decimatedValues[indexDec][1],
					decimatedValues[indexDec][2], Math.sqrt(sum
							/ (sampleCount % decimation)), decimation);
		return newValues;
	}

	/**
	 * Decodes decimated data from the Archive from a ByteBuffer into an array of
	 * ArchiveVNumbers, adding an extra decimation
	 * 
	 * @param bb
	 *            the ByteBuffer with the raw data
	 * @param sampleCount
	 *            the total number of samples returned
	 * @param blockSize
	 *            the general number of samples per data block
	 * @param offset
	 *            the offset (number of missing samples) in the first data block
	 * @param coordinate
	 *            the index (0 or 1) of the coordinate wanted
	 * @param originalDecimation
	 *            the decimation of the input data
	 * @param extraDecimation
	 *            the decimation added by this method
	 * @return ArchiveVStatistics[] that can be used to create a FAValueIterator
	 * @throws FADataNotAvailableException
	 *             when coordinate is not 0 or 1
	 */
	protected static ArchiveVStatistics[] decodeDataDecToDec(ByteBuffer bb,
			int sampleCount, int blockSize, int offset, int coordinate,
			int originalDecimation, int extraDecimation) throws FADataNotAvailableException {
		// get data out of stream
		if (!(coordinate == 0 || coordinate == 1))
			throw new FADataNotAvailableException(
					"Coordinate mapped to name is invalid");

		double[][] data = new double[sampleCount][4];
		double[] times = new double[sampleCount];

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
			timestamp = 0;// Is initialised later on
			duration = 0;
		}

		for (int indexValues = 0; indexValues < sampleCount; indexValues++) {
			// when to read in timeStamps and durations
			if ((indexValues + offset) % blockSize == 0) {
				timestamp = (double) bb.getLong();

				duration = (double) bb.getInt();
				timeInterval = duration / blockSize;
			}
			if (coordinate == 0) {
				mean = bb.getInt() / 1000.0; // micrometers
				bb.getInt();
				min = bb.getInt() / 1000.0;
				bb.getInt();
				max = bb.getInt() / 1000.0;
				bb.getInt();
				std = bb.getInt() / 1000.0;
				bb.getInt();
			} else {
				bb.getInt();
				mean = bb.getInt() / 1000.0;
				bb.getInt();
				min = bb.getInt() / 1000.0;
				bb.getInt();
				max = bb.getInt() / 1000.0;
				bb.getInt();
				std = bb.getInt() / 1000.0;
			}

			data[indexValues][0] = mean;
			data[indexValues][1] = min;
			data[indexValues][2] = max;
			data[indexValues][3] = std;
			times[indexValues] = timestamp;			

			timestamp += timeInterval;
		}

		// add Decimation
		int newSampleCount = sampleCount/extraDecimation;
		if(sampleCount%extraDecimation != 0) newSampleCount++;
		ArchiveVStatistics[] values = new ArchiveVStatistics[newSampleCount];

		double meanDec = data[0][0];
		double minDec = data[0][1];
		double maxDec = data[0][2];
		double stdDec = Math.pow((data[0][3]), 2);
		double startTime = times[0];
		int indexValues = 0;
		
		for (int i = 1; i < data.length; i++){
			meanDec += data[i][0];
			stdDec += Math.pow((data[i][3]), 2);
			if(data[i][1] < minDec) minDec = data[i][1];
			if(data[i][2] > maxDec) maxDec = data[i][2];
			
			// add decimated value to array
			if ((i+1)%extraDecimation == 0){
				meanDec = meanDec/extraDecimation;
				stdDec = Math.sqrt(stdDec);
				timestamp = (startTime + times[i])/2;
				ts = timeStampFromMicroS((long) timestamp);
				values[indexValues] =  new ArchiveVStatistics(ts,
						AlarmSeverity.NONE, "status", null, meanDec, minDec, maxDec, stdDec,
						extraDecimation*originalDecimation);
				indexValues++;
				
				if (i+1 < data.length){
					meanDec = 0;
					minDec = data[i+1][1];
					maxDec = data[i+1][2];
					stdDec = 0;
					startTime = times[i+1];
				}
			}
		}
		// add last incomplete block
		int leftoverSamples = sampleCount%extraDecimation;
		if (leftoverSamples != 0){
			meanDec = meanDec/leftoverSamples;
			stdDec = Math.sqrt(stdDec);
			timestamp = (startTime + times[times.length - 1])/2;
			ts = timeStampFromMicroS((long) timestamp);
			values[indexValues] =  new ArchiveVStatistics(ts,
					AlarmSeverity.NONE, "status", null, meanDec, minDec, maxDec, stdDec,
					extraDecimation);
		}
		return values;
	}
	
	/**
	 * Creates a new Timestamp object from a given value of microseconds from
	 * the epoch.
	 * 
	 * @param timeInMicroS
	 *            time in microseconds from epoch
	 * @return corresponding Timestamp
	 */
	private static Timestamp timeStampFromMicroS(long timeInMicroS) {
		long seconds = timeInMicroS / 1000000;
		int nanoseconds = (int) (timeInMicroS % 1000000) * 1000;
		return Timestamp.of(seconds, nanoseconds);
	}

}
