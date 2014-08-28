package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FAArchivedDataRequestPerformanceTest {
	private TimeDuration timeInterval = TimeDuration.ofSeconds(60*30);
	
	private static final String URL = "fads://fa-archiver:8888"; // specific to DLS
	private static String name;
	private static HashMap<String, int[]> mapping;
	private FAArchivedDataRequest faadr;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mapping = new FAInfoRequest(URL).fetchMapping();
		name = (String)mapping.keySet().toArray()[0];
	}

	@Before
	public void setUp() throws Exception {
		faadr = new FAArchivedDataRequest(URL, mapping);
	}

	@After
	public void tearDown() throws Exception {
		faadr = null;
	}

	@Test
	public void testGetOptimisedValues() {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(timeInterval);
		int count = 100000;
		
		ValueIterator result = null;
		long before = 0;
		long after = 0;
		
		try {
			before = System.nanoTime();
			result = faadr.getOptimisedValues(name, start, end, count);
			after = System.nanoTime();
		} catch (IOException | FADataNotAvailableException e) {
			fail("URL, name, and time should be valid");
			return;
		}		
		
		System.out.printf("'getOptimisedValues' with count = %d takes %d nanoseconds\n", count, after - before); 
		assertNotNull("Did not return data", result);
	}
	
	@Test
	public void testGetRawValues() {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(timeInterval);
		
		ValueIterator result = null;
		long before = 0;
		long after = 0;
		
		try {
			before = System.nanoTime();
			result = faadr.getRawValues(name, start, end);
			after = System.nanoTime();
		} catch (IOException | FADataNotAvailableException e) {
			fail("URL, name, and time should be valid");
			return;
		}		
		
		System.out.printf("'getRawValues' takes %d nanoseconds\n", after - before); 
		assertNotNull("Did not return data", result);
	}

}
