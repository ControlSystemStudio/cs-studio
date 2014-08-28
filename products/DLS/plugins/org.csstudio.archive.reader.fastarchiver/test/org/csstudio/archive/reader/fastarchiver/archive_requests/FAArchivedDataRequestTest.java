package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAArchivedDataRequest;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAArchivedDataRequest.Decimation;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FAArchivedDataRequestTest {
	private static final String URL = "fads://fa-archiver:8888";
	private static FAArchivedDataRequest faadr;
	private static HashMap<String, int[]> bpmMapping;
	private static String pvName;
	String request;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bpmMapping = new FAInfoRequest(URL).fetchMapping();
		pvName = (String) (bpmMapping.keySet().toArray()[0]);
	}

	@Before
	public void setUpBefore() throws Exception {
		faadr = new FAArchivedDataRequest(URL, bpmMapping);
	}

	@Test
	public void testGetRawValues() throws FADataNotAvailableException,
			IOException {
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		ValueIterator vi = faadr.getRawValues(pvName, start, end);
		assertNotNull(vi);
	}

	@Test
	public void testGetOptimisedValues() throws IOException,
			FADataNotAvailableException {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofSeconds(10));
		int count = 5000;
		ValueIterator vi = faadr.getOptimisedValues(pvName, start, end, count);
		assertNotNull(vi);
	}

	@Test
	public void testCalculateDecimation() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		int count = 100000;
		Method calculateDecimation = setToAccess("calculateDecimation",
				Timestamp.class, Timestamp.class, int.class);
		Decimation result = (Decimation) calculateDecimation.invoke(faadr,
				start, end, count);

		// Depends on the data rate from the archiver. At DLS undecimated data
		// is 10000 Hz, so 5 sec * 10000 = 50000 samples, which is smaller than
		// count, the maximum number of samples.
		// I.e. we need undecimated data.
		Decimation expected = Decimation.UNDEC;

		assertEquals(expected, result);
	}

	@Test
	public void testGetValuesValidInput() throws ClassNotFoundException,
			InvocationTargetException, IllegalAccessException,
			IllegalArgumentException, NoSuchMethodException, SecurityException {
		// Test with different decimations and coordinates, all valid
		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));

		String request;
		ValueIterator valIt;
		for (Decimation dec : Decimation.values()) {
			for (int coordinate : new int[] { 0, 1 }) {
				request = translate(start, end, 4, dec);
				valIt = (ValueIterator) getValues.invoke(faadr, request, start,
						end, coordinate, dec);
				assertNotNull("Fails for decimation: " + dec + ", coordinate: "
						+ coordinate, valIt);
			}
		}
	}

	@Test(expected = InvocationTargetException.class)
	public void testGetValuesInvalidBPMnumber() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		// standard, valid input
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		int coordinate = 0;
		Decimation decimation = Decimation.DEC;
		String request;

		int invalidBpm = bpmMapping.size() + 1000;

		request = translate(start, end, invalidBpm, decimation);
		getValues.invoke(faadr, request, start, end, coordinate, decimation);
	}

	@Test(expected = InvocationTargetException.class)
	public void testGetValuesStartTooLate() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		// standard, valid input
		int coordinate = 0;
		int bpm = 4;
		Decimation decimation = Decimation.DEC;
		String request;

		// invalid input
		Timestamp startTooLate = Timestamp.now().plus(
				TimeDuration.ofMinutes(1.0));
		Timestamp endForStartTooLate = startTooLate.plus(TimeDuration
				.ofMinutes(1.0));
		request = translate(startTooLate, endForStartTooLate, bpm, decimation);
		getValues.invoke(faadr, request, startTooLate, endForStartTooLate,
				coordinate, decimation);
	}

	@Test(expected = InvocationTargetException.class)
	public void testGetValuesEndTooEarly() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		// standard, valid input
		int coordinate = 0;
		int bpm = 4;
		Decimation decimation = Decimation.DEC;
		
		// invalid input
		Timestamp endTooEarly = Timestamp.now().minus(
				TimeDuration.ofHours(30 * 24));
		Timestamp startForEndTooEarly = endTooEarly.minus(TimeDuration
				.ofMinutes(1.0));
		request = translate(startForEndTooEarly, endTooEarly, bpm, decimation);
		getValues.invoke(faadr, request, startForEndTooEarly, endTooEarly,
				coordinate, decimation);

	}

	@Test(expected = InvocationTargetException.class)
	public void testGetValuesEndBeforeStart() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		// standard, valid input
		int coordinate = 0;
		int bpm = 4;
		Decimation decimation = Decimation.DEC;

		// invalid input
		Timestamp endBeforeStart = Timestamp.now();
		Timestamp startAfterEnd = endBeforeStart.plus(TimeDuration
				.ofMinutes(1.0));
		request = translate(startAfterEnd, endBeforeStart, bpm, decimation);
		getValues.invoke(faadr, request, startAfterEnd, endBeforeStart,
				coordinate, decimation);

	}

	@Test(expected = InvocationTargetException.class)
	public void testGetValuesInvalidRequest() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		// standard, valid input
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		int coordinate = 0;
		Decimation decimation = Decimation.DEC;

		// invalid input
		String invalidRequest = "wrongFormatRequest\n";
		getValues.invoke(faadr, invalidRequest, start, end, coordinate,
				decimation);

	}

	@Test(expected = InvocationTargetException.class)
	public void testGetValuesInvalidCoordinate() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method getValues = setToAccess("getValues", String.class,
				Timestamp.class, Timestamp.class, int.class, Decimation.class);

		// standard, valid input
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(5));
		Timestamp start = end.minus(TimeDuration.ofSeconds(5));
		int bpm = 4;
		Decimation decimation = Decimation.DEC;
		String request;

		// invalid input
		int invalidCoordinate = 2;

		request = translate(start, end, bpm, decimation);
		getValues.invoke(faadr, request, start, end, invalidCoordinate,
				decimation);
	}

	@Test
	// Checks whether the method creates valid requests for valid input by
	// sending the request to the archiver and checking for an error message
	public void testTranslate() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException,
			IOException, FADataNotAvailableException {
		Method translate = setToAccess("translate", Timestamp.class,
				Timestamp.class, int.class, Decimation.class);
		FARequest far = new FARequest(URL) {
		};
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofMinutes(5));
		// valid numbers for BPMS dependent on facility
		for (int bpm : new int[] { 4, 10, 55 }) {
			for (Decimation dec : Decimation.values()) {
				String request = (String) translate.invoke(faadr, start, end,
						bpm, dec);
				// dependent on FARequest.fetchValues(String) being correct
				byte[] reply = far.fetchData(request);
				byte firstChar = reply[0];
				if (firstChar != 0) {
					fail(new String(reply));
				}
			}
		}
	}

	@Test
	public void testCalcDataLengthUndec() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		// sampleCount, blockSize, offset, expected value (calculate manually)
		int[][] inputAndExpectedOutput = { { 5, 4, 1, 2 * 12 + 5 * 8 },
				{ 3, 4, 1, 12 + 3 * 8 }, { 16, 3, 2, 6 * 12 + 16 * 8 } };

		Method calcDataLength = setToAccess("calcDataLengthUndec", int.class,
				int.class, int.class);
		int result;
		for (int[] input : inputAndExpectedOutput) {
			result = (int) calcDataLength.invoke(faadr, input[0], input[1],
					input[2]);
			assertEquals(input[3], result);
		}
	}

	@Test
	public void testCalcDataLengthDec() throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		// sampleCount, blockSize, offset, expected value (calculate manually)
		int[][] inputAndExpectedOutput = { { 5, 4, 1, 2 * 12 + 5 * 8 * 4 },
				{ 3, 4, 1, 12 + 3 * 8 * 4 }, { 16, 3, 2, 6 * 12 + 16 * 8 * 4 } };

		Method calcDataLength = setToAccess("calcDataLengthDec", int.class,
				int.class, int.class);
		int result;
		for (int[] input : inputAndExpectedOutput) {
			result = (int) calcDataLength.invoke(faadr, input[0], input[1],
					input[2]);
			assertEquals(input[3], result);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Method setToAccess(String methodName, Class... args)
			throws NoSuchMethodException, SecurityException,
			ClassNotFoundException {

		Class targetClass = faadr.getClass();
		Method method = targetClass.getDeclaredMethod(methodName, args);
		method.setAccessible(true);
		return method;
	}

	private String translate(Timestamp start, Timestamp end, int bpm,
			Decimation decimation) throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Method translate = setToAccess("translate", Timestamp.class,
				Timestamp.class, int.class, Decimation.class);
		return (String) translate.invoke(faadr, start, end, bpm, decimation);
	}

}
