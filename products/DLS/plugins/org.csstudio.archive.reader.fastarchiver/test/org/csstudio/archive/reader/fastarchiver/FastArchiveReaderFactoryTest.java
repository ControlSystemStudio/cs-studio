package org.csstudio.archive.reader.fastarchiver;

import static org.junit.Assert.*;

import java.io.IOException;

import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FastArchiveReaderFactoryTest {
	private String correctUrl = "fads://fa-archiver:8888";
	private String incorrectHostUrl = "fads://wrong:8888";
	private String incorrectPrefixUrl = "fa://fa-archiver:8888";
	private static FastArchiveReaderFactory faArchRF;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		faArchRF = new FastArchiveReaderFactory();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetArchiverReaderCorrectUrl() throws Exception {
		FastArchiveReader far;
			try {
				far = (FastArchiveReader)faArchRF.getArchiveReader(correctUrl);
				assertNotNull(far);
			} catch (IOException | FADataNotAvailableException e) {
				fail(e.getMessage());
			} 
	}
	
	@Test(expected = FADataNotAvailableException.class)
	public void testGetArchiverReaderInCorrectPrefixUrl() throws Exception {
		faArchRF.getArchiveReader(incorrectPrefixUrl);

	}
	
	@Test(expected = IOException.class)
	public void testGetArchiverReaderIncorrectHostUrl() throws Exception {
		faArchRF.getArchiveReader(incorrectHostUrl);
	}


}
