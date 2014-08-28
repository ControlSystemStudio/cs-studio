package org.csstudio.utility.pvmanager.fa;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FALiveDataRequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.ValueCacheImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FAChannelHandlerTest {
	private static HashMap<String, int[]> bpmMapping;
	private static final String URL = "fads://fa-archiver:8888";
	FAChannelHandler faCH;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bpmMapping = new FAInfoRequest(URL).fetchMapping();	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		String name = (String)bpmMapping.keySet().toArray()[0];
		faCH = new FAChannelHandler(name, URL, bpmMapping.get(name));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testWrite() {
		faCH.write(new Object(), new ChannelWriteCallback() {
			@Override
			public void channelWritten(Exception ex) {
				// NOP	
			}
		});
	}
	
	@Test
	public void testFindTypeAdapter() throws IOException, FADataNotAvailableException{
		FALiveDataRequest connection = new FALiveDataRequest(URL, 4, 0);
		ValueCache<ArchiveVDisplayType> cache = new ValueCacheImpl<ArchiveVDisplayType>(ArchiveVDisplayType.class);
		assertTrue(faCH.findTypeAdapter(cache, connection) instanceof FADataSourceTypeAdapter);
	}
	
	

}
