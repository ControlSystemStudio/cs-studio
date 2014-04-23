package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorRaw;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorWaveform;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Test;

/**
 * 
 * <code>ApplianceArchiverReaderRawWaveformTest</code> test retrieval of waveform type PVs
 * using non optimized algorithm.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceArchiveReaderRawWaveformTest extends AbstractArchiverReaderTesting {
	
	@Override
	protected ArchiveReader getReader() {
		return new TestApplianceArchiveReader(false);
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a double waveform type PV.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalDouble() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_double",false,0, start, end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumberArray val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			double[] array = new double[val.getData().size()];
			for (int j = 0; j < array.length; j++) {
				array[j] = val.getData().getDouble(j);
			}
			assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_DOUBLE,array,0.000001);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a float waveform type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalFloat() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_float",false,0, start, end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumberArray val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			double[] array = new double[val.getData().size()];
			for (int j = 0; j < array.length; j++) {
				array[j] = val.getData().getFloat(j);
			}
			assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_FLOAT,array,0.000001);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for an int waveform type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalInt() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_int",false,0, start, end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumberArray val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			int[] array = new int[val.getData().size()];
			for (int j = 0; j < array.length; j++) {
				array[j] = val.getData().getInt(j);
			}
			assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_INT,array);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a short waveform type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalShort() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_short",false,0, start,end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumberArray val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			short[] array = new short[val.getData().size()];
			for (int j = 0; j < array.length; j++) {
				array[j] = val.getData().getShort(j);
			}
			assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_SHORT,array);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a byte waveform type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalByte() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_byte",false,0,start,end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumberArray val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			byte[] array = new byte[val.getData().size()];
			for (int j = 0; j < array.length; j++) {
				array[j] = val.getData().getByte(j);
			}
			assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_BYTE,array);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a string wavefdorm type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalString() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		try {
			getValuesStringArray("test_pv_wave_string",false,0,start,end);
			fail();
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}		
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for an enum waveform type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalEnum() throws Exception {
		//this doesn't seem to be supported on the IOC side
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		try {
			getValuesEnumArray("test_pv_wave_enum",false,0,start,end);
			fail();
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}	
	}
}
