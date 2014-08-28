package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Date;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FARequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FARequestTest {
	private static final String host = "fa-archiver";
	private static final int port = 8888;
	private static final String URL = "fads://" + host + ":" + port;
	private static FARequest far;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		far = new FARequest(URL) {
		};
	}

	@AfterClass
	public static void tearDownAfter() throws Exception {
	}

	@Test
	public void testHost() {
		String host = far.host;
		assertEquals(host, FARequestTest.host);
	}

	@Test
	public void testPort() {
		int port = far.port;
		assertEquals(port, FARequestTest.port);
	}

	@Test
	public void testFetchDataInvalidRequest() throws IOException {
		String invalidRequest = "ZZ"; // some random string, should return an
										// error message
		byte[] reply = far.fetchData(invalidRequest);
		assertNotSame("Error message never starts with a 0 byte", 0, reply[0]);
		String errorMessage = new String(reply);
		assertTrue(
				"For an invalid request the archiver returns an error message",
				errorMessage.length() > 1);
	}

	@Test
	public void testFetchDataValidRequest() throws IOException {
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		int bpm = 4;
		String validRequest = String.format("RFM%dS%dES%dNATE\n", bpm,
				start.getSec(), end.getSec());
		byte[] reply = far.fetchData(validRequest);
		assertEquals("Valid data reply always starts with a 0 byte", 0,
				reply[0]);
	}

	@Test
	public void testDecodeDataUndecValidInput() {
		int blockSize = 4;
		int[] values = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int offset = 0;
		ByteBuffer data = encodeUndec(blockSize, values, offset);
		data.position(0);
		ArchiveVNumber[] result;
		try {
			result = (ArchiveVNumber[]) FARequest.decodeDataUndec(data,
					values.length, blockSize, offset, 0);
			for (int i = 0; i < values.length; i++) {
				assertEquals(values[i] / 1000.0, result[i].getValue());
			}
		} catch (FADataNotAvailableException e) {
			fail("Valid input, should not throw an exception");
		}
	}

	@Test(expected = Exception.class)
	public void decodeDataUndecNullByteArrayTest() {
		int blockSize = 4;
		int offset = 0;
		ByteBuffer data = ByteBuffer.wrap(null);
		data.position(0);
		try {
			FARequest.decodeDataUndec(data, 0, blockSize, offset, 0);
		} catch (FADataNotAvailableException e) {
			fail("Should only throw an FADataNotAvailableException when coordinate is not 0 or 1");
		}
	}

	@Test(expected = FADataNotAvailableException.class)
	public void testDecodeDataUndecInvalidCoordinate()
			throws FADataNotAvailableException {
		int blockSize = 4;
		int[] values = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int offset = 0;
		ByteBuffer data = encodeUndec(blockSize, values, offset);
		data.position(0);
		FARequest.decodeDataUndec(data, 0, blockSize, offset, -1);
	}

	@Test
	public void testDecodeDataDecValidInput() {
		int blockSize = 4;
		int[][] values = new int[][] { { 0, 0, 5, 0 }, { 3, 1, 1, 1 },
				{ 2, 2, 2, 5 }, { 3, 2, 3, 3 }, { 4, 4, 4, 7 }, { 5, 3, 5, 5 },
				{ 1, 6, 6, 6 } };
		int offset = 2;
		ByteBuffer data = encodeDec(blockSize, values, offset);
		data.position(0);
		ArchiveVStatistics[] result;
		try {
			result = (ArchiveVStatistics[]) FARequest.decodeDataDec(data,
					values.length, blockSize, offset, 0, 0);
		} catch (FADataNotAvailableException e) {
			fail("Should only throw an FADataNotAvailableException when coordinate is not 0 or 1");
			return;
		}

		for (int i = 0; i < values.length; i++) {
			assertTrue(values[i][0] / 1000.0 == result[i].getAverage());
		}
		for (int i = 0; i < values.length; i++) {
			assertTrue(values[i][1] / 1000.0 == result[i].getMin());
		}
		for (int i = 0; i < values.length; i++) {
			assertTrue(values[i][2] / 1000.0 == result[i].getMax());
		}
		for (int i = 0; i < values.length; i++) {
			assertTrue(values[i][3] / 1000.0 == result[i].getStdDev());
		}
	}

	@Test(expected = Exception.class)
	public void testDecodeDataDecNullByteArray() {
		int blockSize = 4;
		int offset = 0;
		ByteBuffer data = ByteBuffer.wrap(null);
		data.position(0);
		try {
			FARequest.decodeDataDec(data, 0, blockSize, offset, 0, 0);
		} catch (FADataNotAvailableException e) {
			fail("Should only throw an FADataNotAvailableException when coordinate is not 0 or 1");
		}
	}

	@Test(expected = FADataNotAvailableException.class)
	public void testDecodeDataDecInvalidCoordinate()
			throws FADataNotAvailableException {
		int blockSize = 4;
		int[][] values = new int[][] { { 0, 0, 5, 0 }, { 3, 1, 1, 1 },
				{ 2, 2, 2, 5 }, { 3, 2, 3, 3 }, { 4, 4, 4, 7 }, { 5, 3, 5, 5 },
				{ 1, 6, 6, 6 } };
		int offset = 2;
		ByteBuffer data = encodeDec(blockSize, values, offset);
		data.position(0);
		FARequest.decodeDataDec(data, 0, blockSize, offset, -1, 0);
	}

	@Test
	public void testDecodeDataUndecToDecValidInput() {
		int blockSize = 4; // if changed need to recalculate the double arrays
		int decimation = 4;
		int[] values = new int[] { 2, 2, 4, 4, 2, 2, 6, 6, 2, 2, 8, 8 };
		// manually calculated
		double[] decimatedMean = new double[] { 3, 4, 5 };
		double[] decimatedMin = new double[] { 2, 2, 2 };
		double[] decimatedMax = new double[] { 4, 6, 8 };
		double[] decimatedStd = new double[] { 1, 2, 3 };
		int offset = 1;
		ByteBuffer data = encodeUndec(blockSize, values, offset);
		data.position(0);
		ArchiveVStatistics[] result;
		try {
			result = (ArchiveVStatistics[]) FARequest.decodeDataUndecToDec(
					data, values.length, blockSize, offset, 0, decimation);
		} catch (FADataNotAvailableException e) {
			fail("Should only throw an FADataNotAvailableException when coordinate is not 0 or 1");
			return;
		}

		for (int i = 0; i < decimatedMean.length; i++) {
			assertEquals(decimatedMean[i] / 1000.0, result[i].getAverage(),
					0.00001);
			assertEquals(decimatedMin[i] / 1000.0, result[i].getMin(), 0.00001);
			assertEquals(decimatedMax[i] / 1000.0, result[i].getMax(), 0.00001);
			assertEquals(decimatedStd[i] / 1000.0, result[i].getStdDev(),
					0.00001);
		}
	}

	@Test(expected = Exception.class)
	public void testDecodeDataUndecToDecNullByteArray() {
		int blockSize = 4;
		int offset = 0;
		ByteBuffer data = ByteBuffer.wrap(null);
		data.position(0);
		try {
			FARequest.decodeDataUndecToDec(data, 0, blockSize, offset, 0, 0);
		} catch (FADataNotAvailableException e) {
			fail("Should only throw an FADataNotAvailableException when coordinate is not 0 or 1");
		}
	}

	@Test(expected = FADataNotAvailableException.class)
	public void testDecodeDataUndecToDecInvalidCoordinate()
			throws FADataNotAvailableException {
		int blockSize = 4;
		int[][] values = new int[][] { { 0, 0, 5, 0 }, { 3, 1, 1, 1 },
				{ 2, 2, 2, 5 }, { 3, 2, 3, 3 }, { 4, 4, 4, 7 }, { 5, 3, 5, 5 },
				{ 1, 6, 6, 6 } };
		int offset = 2;
		ByteBuffer data = encodeDec(blockSize, values, offset);
		data.position(0);
		FARequest.decodeDataUndecToDec(data, 0, blockSize, offset, -1, 0);
	}

	@Test
	/**
	 * Important to test this private method, as the decoding methods depend on this
	 * method for the right output of new values.
	 */
	public void testTimestampFromMicros() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Timestamp now = Timestamp.of(new Date());
		int nanoSec = now.getNanoSec();
		long sec = now.getSec();
		long time = sec * 1000000 + nanoSec / 1000;
		// (long timeInMicroS)
		Method timestampFromMicros = setToAccess("timeStampFromMicroS",
				long.class);
		Timestamp result = (Timestamp) timestampFromMicros.invoke(far, time);
		assertEquals(now, result);
	}

	/**
	 * Use only for testing
	 * Only 0 coordinate is filled
	 */
	protected static ByteBuffer encodeDec(int blockSize, int[][] values, int offset) {
		int length = (values.length + offset) / blockSize * 12 + values.length
				* 8 * 4;
		if ((values.length + offset) % blockSize != 0)
			length += 12;
		byte[] data = new byte[length];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.position(0);
		long time = Timestamp.now().getSec();
		int duration = 6500000;
		if (offset != 0) {
			bb.putLong(time);
			bb.putInt(duration);
		}
		for (int i = 0; i < values.length; i++) {

			if ((offset + i) % blockSize == 0) {
				bb.putLong(time);
				bb.putInt(duration);
			}
			bb.putInt(values[i][0]);
			bb.putInt(0);
			bb.putInt(values[i][1]);
			bb.putInt(0);
			bb.putInt(values[i][2]);
			bb.putInt(0);
			bb.putInt(values[i][3]);
			bb.putInt(0);
		}
		return bb;
	}

	/**
	 * Use only for testing
	 * only 0 coordinate is filled
	 */
	protected static ByteBuffer encodeUndec(int blockSize, int[] values, int offset) {
		int length = (values.length + offset) / blockSize * 12 + values.length
				* 8;
		if ((values.length + offset) % blockSize != 0)
			length += 12;
		byte[] data = new byte[length];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.position(0);
		long time = Timestamp.now().getSec();
		int duration = 6500000;
		if (offset != 0) {
			bb.putLong(time);
			bb.putInt(duration);
		}
		for (int i = 0; i < values.length; i++) {

			if ((offset + i) % blockSize == 0) {
				bb.putLong(time);
				bb.putInt(duration);
			}
			bb.putInt(values[i]);
			bb.putInt(0);
		}
		return bb;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Method setToAccess(String methodName, Class... args)
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException {
		Class targetClass = FARequest.class;
		Method method = targetClass.getDeclaredMethod(methodName, args);
		method.setAccessible(true);
		return method;
	}

}
