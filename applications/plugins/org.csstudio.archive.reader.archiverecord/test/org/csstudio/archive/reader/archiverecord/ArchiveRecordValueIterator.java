package org.csstudio.archive.reader.archiverecord;

import static org.junit.Assert.assertNotNull;

import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.archiverecord.ArchiveRecordReader;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.junit.Before;
import org.junit.Test;

public class ArchiveRecordValueIterator {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testGetData() throws UnknownChannelException, Exception {
		ITimestamp start = TimestampFactory.createTimestamp(1262875070, 0);
		ITimestamp end = TimestampFactory.createTimestamp(1262875270, 0);
		ArchiveRecordReader reader = new ArchiveRecordReader("archiveRecord://");
		ValueIterator valueIterator = reader.getRawValues(0, 
				"krykWeather:vWindBoe_ai", start, end);
		assertNotNull(valueIterator);
//		printoutRaw(rawValues);
	}

}
