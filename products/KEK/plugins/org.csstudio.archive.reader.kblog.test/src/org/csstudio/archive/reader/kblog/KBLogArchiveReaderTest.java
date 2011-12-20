package org.csstudio.archive.reader.kblog;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.IValue.Quality;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KBLogArchiveReaderTest {
	
	// Switch whether debug message should be output to stdout.
	private boolean dump = true;
	
	//  Switch whether raw data obtained during test should be output.
	// If you turn on this switch, so many lines will be output that each unit test
	// takes very very long time. Usually this switch should be turned off.
	private boolean dump_detail = false;
	
	private KBLogArchiveReader reader = null;
	private ArchiveInfo infos[] = null;

	// Utility function to get subarchive key from name.
	private int getKey(String name) {
		for (ArchiveInfo info : infos) {
			if (info.getName().equals(name)) {
				return info.getKey();
			}
		}
		
		return -1;
	}
	
	// Utility function to get an ITimestamp instance from the given year, month, day, hour, minute and second.
	private ITimestamp getTimeStamp(int year, int month, int day, int hour, int min, int sec)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hour, min, sec);
		return TimestampFactory.fromCalendar(cal);
	}


	@Before
	public void initialize()
	{
		reader = new KBLogArchiveReader("kblog:///KEKBLog", "/usr/local/bin/kblogrd", "SYS/KEKBLog.list", "SYS/LCF", false);
		if (reader != null)
			infos = reader.getArchiveInfos();
	}
	
	@After
	public void close()
	{
		reader.close();
	}
	
	@Test
	public void testBasicInfo()
	{
		assertNotNull("The existence of KBLogArchiveReader instance", reader);
		assertNotNull("The existence of subarchives", infos);
		assertTrue("The number of ArchiveInfo instances", infos.length > 0);

		if (dump) {
			System.out.println("Found subarchives:");
			for (ArchiveInfo info : infos) {
				System.out.printf("%s (key = %d)\n" , info.getName(), info.getKey());
			}
			System.out.println("");
		}
	}
	
	@Test
	public void testSubArchive()
	{
		boolean found_subarchive = false;
		
		// Look for BM/DCCT subarchive
		for (ArchiveInfo info : infos) {
			if (info.getName().equals("BM/DCCT")) {
				found_subarchive = true;
			}
		}
		
		assertTrue("The existence of BM/DCCT subarchive", found_subarchive);
	}
	
	@Test
	public void testSearchPVsByPattern() throws Exception
	{		
		if (infos == null || infos.length <= 0)
			return;
		
		int key = getKey("BM/DCCT");
		if (key < 0)
			throw new Exception("Failed to find BM/DCCT subarchive.");	

		// Search PVs with the name which starts with "BM_DCCT:".
		String pvs[] = reader.getNamesByPattern(key, "BM_DCCT:*");
		assertTrue("The number of PVs \"BM_DCCT:*\" found in BM/DCCT", pvs.length > 0);
		
		if (dump) {
			System.out.println("PVs with the name which starts with BM_DCCT: found in BM/DCCT subarchive.");
			for (String pv : pvs) {
				System.out.println(" - " + pv);
			}
			System.out.println("");
		}
	}
	
	@Test
	public void testSearchPVsByRegex() throws Exception
	{		
		if (infos == null || infos.length <= 0)
			return;
		
		int key = getKey("BM/DCCT");
		if (key < 0)
			throw new Exception("Failed to find BM/DCCT subarchive.");	
		
		// Search PVs with the name which includes ".*HCUR.*".
		String pvs[] = reader.getNamesByRegExp(key, ".*HCUR.*");
		assertTrue("The number of PVs which include \"HCUR\" in their names found in BM/DCCT", pvs.length > 0);

		if (dump) {
			System.out.println("PVs which include \"HCUR\" in their names found in BM/DCCT subarchive.");
			for (String pv : pvs) {
				System.out.println(" - " + pv);
			}
			System.out.println("");
		}
	}
	
	@Test
	public void testRawDataDouble() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;
		
		final ITimestamp start_ts = getTimeStamp(2010, 5, 14, 0, 0, 0); // May, 14th 2010
		final ITimestamp end_ts = getTimeStamp(2010, 5, 16, 0, 0, 0); // May, 16th 2010
		int key = getKey("BM/DCCT");
		if (key < 0)
			throw new Exception("Failed to find BM/DCCT subarchive.");
		
		final ValueIterator values = reader.getRawValues(key, "BM_DCCT:HCUR", start_ts, end_ts);
		int count = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			assertTrue("Obtained value is double.", value instanceof IDoubleValue);
			
			if (value instanceof IDoubleValue) {
				final IDoubleValue dval = (IDoubleValue)value;
//				assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
				assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original);
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count++;
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump)
			System.out.println("The number of values obtained from BM_DCCT:HCUR in BM/DCCT ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count);

		assertTrue("The number of values obtained from BM_DCCT:HCUR.", count == 172795);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_DCCT:HCUR -t 20100514-20100516 -f free BM/DCCT | wc -l
	}

	@Test
	public void testOptimizedDataDouble() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;

		final ITimestamp start_ts = getTimeStamp(2010, 5, 14, 0, 0, 0); // May, 14th 2010
		final ITimestamp end_ts = getTimeStamp(2010, 5, 16, 0, 0, 0); // May, 16th 2010
		int key = getKey("BM/DCCT");
		if (key < 0)
			throw new Exception("Failed to find BM/DCCT subarchive.");
		
		final int desired_count = 800;
		final ValueIterator values = reader.getOptimizedValues(key, "BM_DCCT:HCUR", start_ts, end_ts, desired_count);
		int count = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			assertTrue("Obtained value is double with statistical information.", value instanceof IMinMaxDoubleValue);
			
			if (value instanceof IDoubleValue) {
				final IDoubleValue dval = (IDoubleValue)value;
				assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
				assertTrue("Obtained value is an optimized value.", dval.getQuality() == Quality.Interpolated);
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count++;
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump) {
			System.out.println("The number of values obtained from BM_DCCT:HCUR in BM/DCCT ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count);
		}
		assertTrue("The number of values obtained from BM_DCCT:HCUR.", count == desired_count);
	}

	@Test
	public void testAbnormalValuesFromRawData() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;

		final ITimestamp start_ts = getTimeStamp(2010, 5, 30, 0, 0, 0); // May, 30th 2010
		final ITimestamp end_ts = getTimeStamp(2010, 6, 1, 0, 0, 0); // June, 1st 2010
		int key = getKey("BM/DCCT");
		if (key < 0)
			throw new Exception("Failed to find BM/DCCT subarchive.");	
	
		final ValueIterator values = reader.getRawValues(key, "BM_DCCT:HCUR", start_ts, end_ts);
		int count_normal = 0;
		int count_connected = 0;
		int count_disconnected = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			assertTrue("Obtained value is double.", value instanceof IDoubleValue);
			
			if (value instanceof IDoubleValue) {
				final IDoubleValue dval = (IDoubleValue)value;
				
				if (dval.getStatus().equals(KBLogMessages.StatusConnected)) {
					assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original);
					assertFalse("Obtained value has a valid value.", dval.getSeverity().hasValue());
					assertTrue("Obtained value is in connected severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityConnected));
					count_connected++;					
				} else if (dval.getStatus().equals(KBLogMessages.StatusDisconnected)) {
					assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original);
					assertFalse("Obtained value has a valid value.", dval.getSeverity().hasValue());
					assertTrue("Obtained value is in disconnected severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityDisconnected));
					count_disconnected++;					
				} else {
					assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
					assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original);
					assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
					assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
					assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
					count_normal++;					
				}
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump) {
			System.out.println("The number of normal values obtained from BM_DCCT:HCUR in BM/DCCT ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_normal);
			System.out.println("The number of connected notifications obtained from BM_DCCT:HCUR in BM/DCCT ("
					+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_connected);
			System.out.println("The number of connected notifications obtained from BM_DCCT:HCUR in BM/DCCT ("
					+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_disconnected);
		}

		assertTrue("The number of normal values obtained from BM_DCCT:HCUR.", count_normal == 171450);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_DCCT:HCUR -t 20100530-20100601 -f free BM/DCCT | grep -v Connected | grep -v Disconnected | wc -l

		assertTrue("The number of connected notifications obtained from BM_DCCT:HCUR.", count_connected == 1);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_DCCT:HCUR -t 20100530-20100601 -f free BM/DCCT | grep Connected | wc -l
		
		assertTrue("The number of disconnected notifications obtained from BM_DCCT:HCUR.", count_disconnected == 1);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_DCCT:HCUR -t 20100530-20100601 -f free BM/DCCT | grep Disconnected | wc -l
	}
	
	@Test
	public void testAbnormalValuesFromOptimizedData() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;

		final ITimestamp start_ts = getTimeStamp(2010, 5, 30, 0, 0, 0); // May, 30th 2010
		final ITimestamp end_ts = getTimeStamp(2010, 6, 1, 0, 0, 0); // June, 1st 2010
		int key = getKey("BM/DCCT");
		if (key < 0)
			throw new Exception("Failed to find BM/DCCT subarchive.");	
	
		final int desired_count = 800;
		final ValueIterator values = reader.getOptimizedValues(key, "BM_DCCT:HCUR", start_ts, end_ts, desired_count);
		int count_normal = 0;
		int count_connected = 0;
		int count_disconnected = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			
			if (value instanceof IDoubleValue) {
				final IDoubleValue dval = (IDoubleValue)value;
				
				if (dval.getStatus().equals(KBLogMessages.StatusConnected)) {
					assertTrue("Obtained value is double.", value instanceof IDoubleValue);
					assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original); // Invalid status has always Original quality.
					assertFalse("Obtained value has a valid value.", dval.getSeverity().hasValue());
					assertTrue("Obtained value is in connected severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityConnected));
					count_connected++;					
				} else if (dval.getStatus().equals(KBLogMessages.StatusDisconnected)) {
					assertTrue("Obtained value is double.", value instanceof IDoubleValue);
					assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original); // Invalid status has always Original quality.
					assertFalse("Obtained value has a valid value.", dval.getSeverity().hasValue());
					assertTrue("Obtained value is in disconnected severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityDisconnected));
					count_disconnected++;					
				} else {
					assertTrue("Obtained value is double with statistical information.", value instanceof IMinMaxDoubleValue);
					assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
					assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Interpolated);
					assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
					assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
					assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
					count_normal++;					
				}
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump) {
			System.out.println("The number of normal values obtained from BM_DCCT:HCUR in BM/DCCT ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_normal);
			System.out.println("The number of connected notifications obtained from BM_DCCT:HCUR in BM/DCCT ("
					+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_connected);
			System.out.println("The number of connected notifications obtained from BM_DCCT:HCUR in BM/DCCT ("
					+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_disconnected);
		}

		assertTrue("The number of normal values obtained from BM_DCCT:HCUR.", count_normal > 0 && count_normal <= 800);
		// Because kblog failed to archive some values during the given term, the total number of normal values
		// should be less than 800.

		assertTrue("The number of connected notifications obtained from BM_DCCT:HCUR.", count_connected == 1);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_DCCT:HCUR -t 20100530-20100601 -f free BM/DCCT | grep Connected | wc -l
		
		assertTrue("The number of disconnected notifications obtained from BM_DCCT:HCUR.", count_disconnected == 1);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_DCCT:HCUR -t 20100530-20100601 -f free BM/DCCT | grep Disconnected | wc -l
	}
	
	
	@Test
	public void testRawDataInteger() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;
		
		final ITimestamp start_ts = getTimeStamp(2010, 6, 9, 0, 0, 0); // June, 9th 2010
		final ITimestamp end_ts = getTimeStamp(2010, 6, 11, 0, 0, 0); // June, 11th 2010
		int key = getKey("BT/BTMagnets");
		if (key < 0)
			throw new Exception("Failed to find BT/BTMagnets subarchive.");	
		
		final ValueIterator values = reader.getRawValues(key, "BTePS:HX01E:CREG", start_ts, end_ts);
		int count = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			assertTrue("Obtained value is long.", value instanceof ILongValue);
			
			if (value instanceof ILongValue) {
				final ILongValue dval = (ILongValue)value;
				assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
				assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original);
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count++;
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump)
			System.out.println("The number of values obtained from BTePS:HX01E:CREG in BT/BTMagnets ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count);

		assertTrue("The number of values obtained from BTePS:HX01E:CREG.", count == 244);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BTePS:HX01E:CREG -t 20100609-20100611 -f free BT/BTMagnets | wc -l
	}
	
	@Test
	public void testOptimizedDataInteger() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;
		
		final ITimestamp start_ts = getTimeStamp(2010, 6, 9, 0, 0, 0); // June, 9th 2010
		final ITimestamp end_ts = getTimeStamp(2010, 6, 11, 0, 0, 0); // June, 11th 2010
		int key = getKey("BT/BTMagnets");
		if (key < 0)
			throw new Exception("Failed to find BT/BTMagnets subarchive.");	

		final int desired_count = 800;
		final ValueIterator values = reader.getOptimizedValues(key, "BTePS:HX01E:CREG", start_ts, end_ts, desired_count);
		int count_optimized = 0;
		int count_raw = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			
			if (value instanceof ILongValue) {
				// In case only one value is archived in one time section, the raw value is returned.
				final ILongValue dval = (ILongValue)value;
				assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
				assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Original);
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count_raw++;
			} else if (value instanceof IMinMaxDoubleValue) {
				final IMinMaxDoubleValue dval = (IMinMaxDoubleValue)value;
				assertTrue("Obtained value is a scalar value.", dval.getValues().length == 1); // Scalar value
				assertTrue("Obtained value is an original value.", dval.getQuality() == Quality.Interpolated);
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid value.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count_optimized++;
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}

		if (dump) {
			System.out.println("The number of raw values obtained from BTePS:HX01E:CREG in BT/BTMagnets ("
					+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_raw);
			System.out.println("The number of optimized values obtained from BTePS:HX01E:CREG in BT/BTMagnets ("
					+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count_optimized);
		}

		final int count_total = count_raw + count_optimized;
		assertTrue("The number of values obtained from BTePS:HX01E:CREG.", count_total > 0 && count_total <= 244);
		// The total number of obtained values should be equal to or less than the number of raw data.
	}

	@Test
	public void testRawDataWaveform() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;
		
		final ITimestamp start_ts = getTimeStamp(2010, 6, 10, 0, 0, 0); // June, 10th 2010 AM 00:00
		final ITimestamp end_ts = getTimeStamp(2010, 6, 10, 1, 0, 0); // June, 10th 2010 AM 01:00
		int key = getKey("BM/BLM");
		if (key < 0)
			throw new Exception("Failed to find BM/BLM subarchive.");
		
		final ValueIterator values = reader.getRawValues(key, "BM_BLM:D1:ADC", start_ts, end_ts);
		int count = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			assertTrue("Obtained value is double.", value instanceof IDoubleValue);
			
			if (value instanceof IDoubleValue) {
				final IDoubleValue dval = (IDoubleValue)value;
				assertTrue("Obtained value is a waveform.", dval.getValues().length == 32); // Array
				assertTrue("Obtained value is not interpolated.", dval.getQuality() == Quality.Original);
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid array.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count++;
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump)
			System.out.println("The number of arrays obtained from BM_BLM:D1:ADC in BM/BLM ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count);

		assertTrue("The number of arrays obtained from BM_BLM:D1:ADC.", count == 57171);
		// This correct number is obtained from the following command:
		//  $ kblogrd -r BM_BLM:D1:ADC -t 201006100000-201006100100 BM/BLM | wc -l
	}
	
	@Test
	public void testOptimizedDataWaveform() throws Exception
	{	
		if (reader == null || infos == null || infos.length <= 0)
			return;
		
		final ITimestamp start_ts = getTimeStamp(2010, 6, 10, 0, 0, 0); // June, 10th 2010 AM 00:00
		final ITimestamp end_ts = getTimeStamp(2010, 6, 10, 1, 0, 0); // June, 10th 2010 AM 01:00
		int key = getKey("BM/BLM");
		if (key < 0)
			throw new Exception("Failed to find BM/BLM subarchive.");
		
		final int desired_count = 800;
		final ValueIterator values = reader.getOptimizedValues(key, "BM_BLM:D1:ADC", start_ts, end_ts, desired_count);
		int count = 0;
		while (values.hasNext()) {
			final IValue value = values.next();
			assertNotNull("Obtained value is not null.", value);
			assertTrue("Obtained value is double.", value instanceof IDoubleValue);
			
			if (value instanceof IDoubleValue) {
				final IDoubleValue dval = (IDoubleValue)value;
				assertTrue("Obtained value is a waveform.", dval.getValues().length == 32); // Array
				assertTrue("Obtained value is not interpolated.", dval.getQuality() == Quality.Original); // Array is not averaged, but sampled.
				assertTrue("Obtained value is in normal status.", dval.getStatus().equals(KBLogMessages.StatusNormal));
				assertTrue("Obtained value has a valid array.", dval.getSeverity().hasValue());
				assertTrue("Obtained value is in normal severity.", dval.getSeverity().toString().equals(KBLogMessages.SeverityNormal));
				count++;
			}
			
			if (dump_detail)
				System.out.println(value.toString());
		}
		
		if (dump)
			System.out.println("The number of arrays obtained from BM_BLM:D1:ADC in BM/BLM ("
						+ start_ts.toString() + " - " + end_ts.toString() + ") : " + count);

		assertTrue("The number of arrays obtained from BM_BLM:D1:ADC.", count < desired_count);
	}
		
	/* TODO: double values (Reduced) */
	/* TODO: non-values (Connected or Disconnected) in BM_DCCT:HCUR in BM/DCCT on 2010/05/31 (Reduced) */
	/* TODO: int values in BTePS:HX01E:CREG in BT/BTMagnets since 2010/0610 (Reduced) */	
	/* TODO: waveform (32 elements) in BM_BLM:D1:ADC -t 20100610 BM/BLM since 20100610 (Reduced) */
}

