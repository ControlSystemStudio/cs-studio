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
	private TimeDuration[] timeIntervals = new TimeDuration[] {
			TimeDuration.ofSeconds(10), TimeDuration.ofSeconds(20),
			TimeDuration.ofSeconds(30), TimeDuration.ofSeconds(40),
			TimeDuration.ofSeconds(50), TimeDuration.ofSeconds(60),
			TimeDuration.ofSeconds(90), TimeDuration.ofSeconds(120),
			TimeDuration.ofSeconds(300), TimeDuration.ofSeconds(900),
			TimeDuration.ofSeconds(1800), TimeDuration.ofSeconds(3600),
			TimeDuration.ofSeconds(10800), TimeDuration.ofSeconds(21600),
			TimeDuration.ofSeconds(43200), TimeDuration.ofSeconds(86400),
			TimeDuration.ofSeconds(259200), TimeDuration.ofSeconds(518400),
			TimeDuration.ofSeconds(1036800), TimeDuration.ofSeconds(1382400) };
	
	private TimeDuration[] timeIntervalsRaw = new TimeDuration[] {
			TimeDuration.ofSeconds(10), TimeDuration.ofSeconds(20),
			TimeDuration.ofSeconds(30), TimeDuration.ofSeconds(40),
			TimeDuration.ofSeconds(50), TimeDuration.ofSeconds(60),
			TimeDuration.ofSeconds(90), TimeDuration.ofSeconds(120),
			TimeDuration.ofSeconds(300), TimeDuration.ofSeconds(900),
			TimeDuration.ofSeconds(1800)};
	
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
		Timestamp end;
		Timestamp start;
		int count = 8000;
		
		for (TimeDuration timeInterval: timeIntervals){
			end = Timestamp.now();
			start = end.minus(timeInterval);

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

			System.out
					.printf("'getOptimisedValues' with count = %d, time interval %d, takes %d nanoseconds\n",
							count, timeInterval.getSec(), after - before);
			assertNotNull("Did not return data", result);
		}

	}
	
	@Test
	public void testGetRawValues() {
		Timestamp start;
		Timestamp end;
		
		for (TimeDuration timeInterval : timeIntervalsRaw) {
			end = Timestamp.now();
			start = end.minus(timeInterval);

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

			System.out.printf("for time interval %d 'getRawValues' takes %d nanoseconds\n", timeInterval.getSec(), after
					- before);
			assertNotNull("Did not return data", result);
		}
	}

}
