package org.csstudio.archive.reader.fastarchiver.archive_requests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class FAInfoRequestTest {

	private static final String URL = "fads://fa-archiver:8888";
	private static FAInfoRequest faIR;

	@Before
	public void setUpBefore() throws Exception {
		faIR = new FAInfoRequest(URL);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	// depends on getAllBPMs
	public void testCreateMapping() {
		HashMap<String, int[]> mapping = null;
		try {
			mapping = faIR.fetchMapping();
		} catch (IOException e) {
			fail("createMapping() throws an exception: " + e.getMessage());
		}
		assertNotNull(mapping);
		Collection<int[]> allValues = mapping.values();
		for (int[] value : allValues) {
			assertEquals(
					"Values of the mapping must have exactly two elements", 2,
					value.length);
			// check if coordinates in allowed range
			assertTrue("Coordinate must be 1 or 0, but is " + value[1],
					value[1] == 1 || value[1] == 0);
		}
	}

	@Test
	public void testGetName() {
		String name;
		try {
			name = faIR.getName();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
			name = "SR"; // dependent on url used, use "fads://fa-archiver:8888"
		}
		assertEquals("SR", name);
	}

}
