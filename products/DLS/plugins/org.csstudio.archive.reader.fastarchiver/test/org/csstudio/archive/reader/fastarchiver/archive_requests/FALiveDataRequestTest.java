package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.util.time.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Most parts of the class are hard to test, so create an instance and therefore
 * an connection to the live stream use methods in normal circumstances,
 * simulating the use by the DataBrowser.
 * 
 * Tested with large gap between calls to fetchNewValues, tested with small
 * decimation. Reconnecting has been tested in use in DataBrowser, by
 * interrupting the Fast Archiver using a debug command, but not using J-Unit
 * tests.
 */
public class FALiveDataRequestTest {

	private static FALiveDataRequest faLive;

	// bpm and url are valid for DLS, other facilities might need to change
	// these values
	private static int bpm = 4;
	private static int coordinate = 0;
	private static String url = "fads://fa-archiver:8888";

	@Before
	public void before() throws IOException, FADataNotAvailableException {
		// make a connection to the live stream
		faLive = new FALiveDataRequest(url, bpm, coordinate);
	}

	@After
	public void after() {
		faLive.close();
	}

	@Test
	public void testFetchNewValuesStandardFetch() {
		ArchiveVDisplayType[] newValues = null;
		int decimation = 100;
		try {
			newValues = faLive.fetchNewValues(decimation);
		} catch (IOException e) {
			fail(e.getMessage());
			return;
		} catch (FADataNotAvailableException e) {
			fail("Should only throw this when the closed() method has been called or an invalid coordinate has been specified");
			return;
		}

		// check newValues, may be empty, may not be null
		assertNotNull(
				"fetchValues may return an emty array, but not a null value",
				newValues);
	}

	@Test
	// wait for 60 sec between fetches, see if Timestamps still align (within 1
	// sec of each other)
	public void testFetchNewValuesTimeGap() throws InterruptedException {
		int timeout = 30 * 1000;
		ArchiveVDisplayType[] newValuesBeforeGap = null;
		ArchiveVDisplayType[] newValuesAfterGap = null;
		int decimation = 100;

		Thread.sleep(1000);
		// first fetch
		try {
			newValuesBeforeGap = faLive.fetchNewValues(decimation);
		} catch (IOException e) {
			fail(e.getMessage());
			return;
		} catch (FADataNotAvailableException e) {
			fail("Should only throw this when the closed() method has been called or an invalid coordinate has been specified");
			return;
		}

		Thread.sleep(timeout);
		
		// second fetch
		try {
			newValuesAfterGap = faLive.fetchNewValues(decimation);
			assertTrue(newValuesAfterGap.length>0);
		} catch (IOException e) {
			fail (e.getMessage());
			return;
		} catch (FADataNotAvailableException e) {
			fail("Should only throw this when the closed() method has been called or an invalid coordinate has been specified");
			return;
		}

		// compare timestamps
		Timestamp lastTimeBeforeGap = newValuesBeforeGap[newValuesBeforeGap.length-1].getTimestamp();
		Timestamp firstTimeAfterGap = newValuesAfterGap[0].getTimestamp();
		assertTrue(lastTimeBeforeGap.durationBetween(firstTimeAfterGap).toNanosLong() < 1000000000);
		
	}

	@Test
	public void testFetchNewValuesNoDecimation() {
		ArchiveVDisplayType[] newValues = null;
		int decimation = 0;
		try {
			newValues = faLive.fetchNewValues(decimation);
		} catch (IOException e) {
			fail(e.getMessage());
			return;
		} catch (FADataNotAvailableException e) {
			fail("Should only throw this when the closed() method has been called or an invalid coordinate has been specified");
			return;
		}

		// check newValues, may be empty, may not be null
		assertNotNull(
				"fetchValues may return an emty array, but not a null value",
				newValues);
	}

}
