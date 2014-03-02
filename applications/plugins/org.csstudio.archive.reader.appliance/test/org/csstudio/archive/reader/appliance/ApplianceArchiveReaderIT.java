package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Test;

/**
 * Tests for {@code ApplianceArchiveReader} class, which requires a working appliance server instance.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class ApplianceArchiveReaderIT extends AbstractArchiverReaderTesting {

	private static final String URL = "http://127.0.0.1:17669/retrieval";
	private static final String CHANNEL = "double-counter-100Hz";
	private static final int KEY = 1;
	
	@Override
	protected ArchiveReader getReader() throws Exception {
		return new ApplianceArchiveReaderFactory().getArchiveReader(URL);
	}
	
	/**
	 * Tests {@code ApplianceArchiveReader#getNamesByPattern(int, String)}
	 * method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchByPattern() throws Exception {
		ArchiveReader reader = new ApplianceArchiveReaderFactory().getArchiveReader(URL);
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
		ArchiveReader reader = new ApplianceArchiveReaderFactory().getArchiveReader(URL);
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
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(40));
		Timestamp start = end.minus(TimeDuration.ofSeconds(40));
		ArchiveVNumber[] vals = getValuesNumber(CHANNEL, false, 1,start,end);
		//we don't know anything about the values
		assertTrue("Number of values", vals.length > 3000);
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
		Timestamp end = Timestamp.now().minus(TimeDuration.ofSeconds(40));
		Timestamp start = end.minus(TimeDuration.ofSeconds(39));
		ArchiveVNumber[] vals = getValuesNumber(CHANNEL, true, 40, start, end);
		//we don't know anything about the values, we just now how many there are
		assertEquals("Number of values", 40, vals.length);
	}
}