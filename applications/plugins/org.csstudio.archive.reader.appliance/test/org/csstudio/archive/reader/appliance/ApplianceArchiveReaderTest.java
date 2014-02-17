package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.*;
import java.util.Arrays;

import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Test;

/**
 * Tests for {@code ApplianceArchiveReader} class.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class ApplianceArchiveReaderTest {

	private static final String URL = "http://127.0.0.1:17669/retrieval";
	private static final String CHANNEL = "double-counter-100Hz";
	private static final int KEY = 1;
	
	/**
	 * Tests {@code ApplianceArchiveReader#getNamesByPattern(int, String)}
	 * method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchByPattern() throws Exception {
		ApplianceArchiveReader reader = new ApplianceArchiveReader(URL);
		String names[] = reader.getNamesByPattern(KEY, CHANNEL);
		assertTrue(Arrays.asList(names).contains(CHANNEL));
		assertEquals(names.length, 1);
	}
	
	/**
	 * Tests {@code ApplianceArchiveReader#getNamesByRegExp(int, String)}
	 * method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchByRegExp() throws Exception {
		ApplianceArchiveReader reader = new ApplianceArchiveReader(URL);
		String names[] = reader.getNamesByRegExp(KEY, "double-counter-[0-9]*Hz");
		assertTrue(Arrays.asList(names).contains(CHANNEL));
		assertTrue(names.length > 0);
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRawDataRetrieval() throws Exception {
		ApplianceArchiveReader reader = new ApplianceArchiveReader(URL);
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ValueIterator rawIterator = reader.getRawValues(KEY, CHANNEL, start, end);
		int counter  = 0;
		while(rawIterator.hasNext()) {
			counter++;
			rawIterator.next();
		}
		rawIterator.close();
		assertNotSame(counter, 0);
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)}
	 * method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOptimizedDataRetrieval() throws Exception {
		ApplianceArchiveReader reader = new ApplianceArchiveReader(URL);
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ValueIterator optimizedIterator = reader.getOptimizedValues(KEY, CHANNEL, start, end, 500);
		int counter  = 0;
		while(optimizedIterator.hasNext()) {
			counter++;
			optimizedIterator.next();
		}
		optimizedIterator.close();
		assertNotSame(counter, 0);
	}
	
	/**
	 * Tests if size of optimized data is smaller than size of raw data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataSize() throws Exception {
		ApplianceArchiveReader reader = new ApplianceArchiveReader(URL);
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ValueIterator rawIterator = reader.getRawValues(KEY, CHANNEL, start, end);
		ValueIterator optimizedIterator = reader.getOptimizedValues(KEY, CHANNEL, start, end, 5000);
		int rawDataCounter  = 0;
		while(rawIterator.hasNext()) {
			rawDataCounter++;
			rawIterator.next();
		}
		rawIterator.close();
		int optimizedDataCounter = 0;
		while(optimizedIterator.hasNext()) {
			optimizedDataCounter++;
			optimizedIterator.next();
		}
		optimizedIterator.close();
		assertTrue(rawDataCounter > optimizedDataCounter);
	}
}