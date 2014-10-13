package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FALiveDataRequestPerformanceTest {
	private FALiveDataRequest faLiveDR;
	private static final String URL = "fads://fa-archiver:8888"; // specific to DLS
	private int decimation = 1000;
	private int timeInterval = 3000;//in milliseconds 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		faLiveDR = new FALiveDataRequest(URL, 4, 0);		
	}

	@After
	public void tearDown() throws Exception {
		faLiveDR.close();
		faLiveDR = null;
	}

	@Test
	public void testFetchNewValues() throws InterruptedException {		
		long before = 0;
		long after = 0;
		ArchiveVDisplayType[] result;
		
		Thread.sleep(timeInterval);
		
		try {
			before = System.nanoTime();
			result = faLiveDR.fetchNewValues(decimation);
			after = System.nanoTime();
		} catch (IOException | FADataNotAvailableException e) {
			fail("URL, name, and time should be valid");
			return;
		}		
		
		System.out.printf("'fetchNewValues' takes %d nanoseconds\n", after - before); 
		assertNotNull("Did not return data", result);
	}

}
