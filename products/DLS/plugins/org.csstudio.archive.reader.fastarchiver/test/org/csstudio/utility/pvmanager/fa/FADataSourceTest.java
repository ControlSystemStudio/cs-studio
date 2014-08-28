package org.csstudio.utility.pvmanager.fa;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FADataSourceTest {
	private static HashMap<String, int[]> bpmMapping;
	private static FADataSource fads;
	// URL dependent on facility, valid for DLS
	private static final String URL = "fads://fa-archiver:8888";

	
	@BeforeClass
	public static void beforeClass() throws IOException, FADataNotAvailableException{
		bpmMapping = new FAInfoRequest(URL).fetchMapping();	
		fads = new FADataSource();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateChannelWithValidName() {
		String name = (String)bpmMapping.keySet().toArray()[0];
		name = name.substring(5, name.length()); //strip off fa://
		
		FAChannelHandler result = (FAChannelHandler)fads.createChannel(name);
		assertNotNull("Valid name, must return an FAChannelHandler", result);
	}
	
	@Test
	public void testCreateChannelWithInvalidName() {
		String name = "Invalid Name";
		
		FAChannelHandler result = (FAChannelHandler)fads.createChannel(name);
		assertNull("Valid name, must return an FAChannelHandler", result);
	}	

}
