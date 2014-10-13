package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FARequestPerformanceTest {
	FARequest far;
	private static final String URL = "fads://fa-archiver:8888";
	private static int datalength = 10000000;

	@Before
	public void setUp() throws Exception {
		far = new FARequest(URL) {
		};
	}

	@After
	public void tearDown() throws Exception {
		far = null;
	}

	@Test
	public void testFetchData() throws IOException {
		// get double decimated data for the last 16 days
		int days = 16;
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24 * days));
		int bpm = 4;
		String request = String.format("RDDM%dS%d.%09dES%d.%09dNATE\n", bpm,
				start.getSec(), start.getNanoSec(), end.getSec(),
				end.getNanoSec());
		// get time before method call
		long before = System.nanoTime();

		byte[] data = far.fetchData(request);

		// get time after method call
		long after = System.nanoTime();
		
		System.out.printf("'fetchData' takes %d nanoseconds\n", after - before); 
		assertEquals("Did not return data but an error message", 0, data[0]);
	}

	@Test
	public void testDecodeDataUndec() {
		// Create large bytebuffer
		int[] values = new int[datalength]; 
		for (int i = 0; i < values.length; i++){
			values[i] = i*13/7;
		}
		int blockSize = 612;
		int offset = 50;
		ByteBuffer input = FARequestTest.encodeUndec(blockSize, values, offset);
		input.position(0);
		
		ArchiveVDisplayType[] output = null;
		long before = 0;
		long after = 0;
		
		try {
			before = System.nanoTime();
			output = FARequest.decodeDataUndec(input, values.length, blockSize, offset, 0);
			after = System.nanoTime();
		} catch (FADataNotAvailableException e) {
			fail("Throws Exception for valid input");
			return;
		}
		
		System.out.printf("'decodeDataUndec' takes %d nanoseconds\n", after - before); 
		assertNotNull("Did not decode", output);
		
	}

	@Test
	public void testDecodeDataDec() {
		// Create large bytebuffer
		int[][] values = new int[datalength][4]; 
		for (int i = 0; i < values.length; i++){
			values[i] = new int[]{i*13/7, i/7, i*13, i/37};
		}
		int blockSize = 612;
		int offset = 50;
		ByteBuffer input = FARequestTest.encodeDec(blockSize, values, offset);
		input.position(0);
		
		ArchiveVDisplayType[] output = null;
		long before = 0;
		long after = 0;
		
		try {
			before = System.nanoTime();
			output = FARequest.decodeDataDec(input, values.length, blockSize, offset, 0, 3);
			after = System.nanoTime();
		} catch (FADataNotAvailableException e) {
			fail("Throws Exception for valid input");
			return;
		}
		
		
		System.out.printf("'decodeDataDec' takes %d nanoseconds\n", after - before); 
		assertNotNull("Did not decode", output);
	}

	@Test
	public void testDecodeDataUndecToDec() {
		// Create large bytebuffer
		int[] values = new int[datalength]; 
		for (int i = 0; i < values.length; i++){
			values[i] = i*13/7;
		}
		int blockSize = 612;
		int offset = 50;
		ByteBuffer input = FARequestTest.encodeUndec(blockSize, values, offset);
		input.position(0);
		
		ArchiveVDisplayType[] output = null;
		long before = 0;
		long after = 0;
		
		try {
			before = System.nanoTime();
			output = FARequest.decodeDataUndecToDec(input, values.length, blockSize, offset, 0, 10000);
			after = System.nanoTime();
		} catch (FADataNotAvailableException e) {
			fail("Throws Exception for valid input");
			return;
		}
		
		
		System.out.printf("'decodeDataUndecToDec' takes %d nanoseconds\n", after - before); 
		assertNotNull("Did not decode", output);
	}

}
