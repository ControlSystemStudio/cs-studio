package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorRaw;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Test;

/**
 * 
 * <code>ApplianceRawArchiveReaderTest</code> tests raw data retrieval using
 * a dummy appliance server loopback. 
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceArchiveReaderRawTest extends AbstractArchiverReaderTesting {

	@Override
	protected ArchiveReader getReader() {
		return new TestApplianceArchiveReader(false);
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a double type PV.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalDouble() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumber[] vals = getValuesNumber("test_pv_double",false,0, start, end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", Double.valueOf(TestGenMsgIteratorRaw.VALUES_DOUBLE[i]),(Double)val.getValue(),0.0001);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a float type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalFloat() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumber[] vals = getValuesNumber("test_pv_float",false,0, start, end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", Float.valueOf(TestGenMsgIteratorRaw.VALUES_FLOAT[i]),(Float)val.getValue(),0.0001);
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for an int type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalInt() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumber[] vals = getValuesNumber("test_pv_int",false,0, start, end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", Integer.valueOf(TestGenMsgIteratorRaw.VALUES_INT[i]),(Integer)val.getValue());
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a short type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalShort() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumber[] vals = getValuesNumber("test_pv_short",false,0, start,end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", Short.valueOf(TestGenMsgIteratorRaw.VALUES_SHORT[i] ),(Short)val.getValue());
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a byte type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalByte() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVNumber[] vals = getValuesNumber("test_pv_byte",false,0,start,end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVNumber val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", Byte.valueOf(TestGenMsgIteratorRaw.VALUES_BYTE[i]) ,(Byte)val.getValue());
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for a string type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalString() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVString[] vals = getValuesString("test_pv_string",false,0,start,end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVString val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", "9554.0",val.getValue());
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
	
	/**
	 * Tests
	 * {@code ApplianceArchiveReader#getRawValues(int, String, Timestamp, Timestamp)}
	 * method for an enum type pv.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataRetrievalEnum() throws Exception {
		Timestamp end = Timestamp.now();
		Timestamp start = end.minus(TimeDuration.ofHours(24.0));
		ArchiveVEnum[] vals = getValuesEnum("test_pv_enum",false,0,start,end);
		assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);
		
		ArchiveVEnum val = null;
		for (int i = 0; i < vals.length; i++) {
			val = vals[i];
			assertEquals("Value comparison", "Enum <" + TestGenMsgIteratorRaw.VALUES_INT[i] + ">",val.getValue());
			assertEquals("Timestamp comparison", TimestampHelper.toMillisecs(start) + i,TimestampHelper.toMillisecs(val.getTimestamp()));
			assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
			assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
		}
	}
}