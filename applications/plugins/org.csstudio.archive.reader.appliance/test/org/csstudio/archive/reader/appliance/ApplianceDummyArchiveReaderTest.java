package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.junit.Test;

/**
 * Tests for {@code ApplianceArchiveReader} class.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class ApplianceDummyArchiveReaderTest {

	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRawDataRetrieval() throws Exception {
		TestApplianceArchiveReader reader = new TestApplianceArchiveReader();
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ValueIterator rawIterator = reader.getRawValues(1, "testPV", start, end);
		ArrayList<ArchiveVNumber> vals = new ArrayList<ArchiveVNumber>();
		while(rawIterator.hasNext()) {
			vals.add((ArchiveVNumber)rawIterator.next());
		}
		rawIterator.close();
		assertEquals("There should be 10 values all together", 10, vals.size());
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.size(); i++) {
			val = vals.get(i);
			assertEquals("Value comparison", i+1,val.getValue().doubleValue(),0.0001);
			assertEquals("Timestamp comparison", 1000000*i,val.getTimestamp().getNanoSec());
			if (i == 0) {
				assertEquals("Status", AlarmSeverity.INVALID, val.getAlarmSeverity());
			} else if (i == 1 || i == 2 || i == 8) {
				assertEquals("Status", AlarmSeverity.MAJOR, val.getAlarmSeverity());
			} else if (i == 3 || i == 7) {
				assertEquals("Status", AlarmSeverity.MINOR, val.getAlarmSeverity());
			} else if (i == 4 || i == 5 || i == 6) {
				assertEquals("Status", AlarmSeverity.NONE, val.getAlarmSeverity());
			} else if (i == 9) {
				assertEquals("Status", AlarmSeverity.UNDEFINED, val.getAlarmSeverity());
			}
		}
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
		TestApplianceArchiveReader reader = new TestApplianceArchiveReader();
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ValueIterator optimizedIterator = reader.getOptimizedValues(1, "testPV", start, end, 5);
		ArrayList<ArchiveVNumber> vals = new ArrayList<ArchiveVNumber>();
		while(optimizedIterator.hasNext()) {
			vals.add((ArchiveVNumber)optimizedIterator.next());
		}
		optimizedIterator.close();
		assertEquals("There should be 10 values all together", 10, vals.size());
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.size(); i++) {
			val = vals.get(i);
			assertEquals("Value comparison", i+1,val.getValue().doubleValue(),0.0001);
			assertEquals("Timestamp comparison", 1000000*i,val.getTimestamp().getNanoSec());
			if (i == 0) {
				assertEquals("Status", AlarmSeverity.INVALID, val.getAlarmSeverity());
			} else if (i == 1 || i == 2 || i == 8) {
				assertEquals("Status", AlarmSeverity.MAJOR, val.getAlarmSeverity());
			} else if (i == 3 || i == 7) {
				assertEquals("Status", AlarmSeverity.MINOR, val.getAlarmSeverity());
			} else if (i == 4 || i == 5 || i == 6) {
				assertEquals("Status", AlarmSeverity.NONE, val.getAlarmSeverity());
			} else if (i == 9) {
				assertEquals("Status", AlarmSeverity.UNDEFINED, val.getAlarmSeverity());
			}
		}
		// optimization is done on the server therefore appliance reader will return 10 values
		// which are hard-coded in TestGenMsgIterator class
		assertEquals("There should be 10 values alltogether", 10, vals.size());
	}
}