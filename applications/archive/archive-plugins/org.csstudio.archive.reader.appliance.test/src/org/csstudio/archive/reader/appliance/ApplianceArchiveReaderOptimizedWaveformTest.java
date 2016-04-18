package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.Instant;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorRaw;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorWaveform;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.diirt.util.time.TimeDuration;
import org.junit.Test;

/**
 *
 * <code>ApplianceArchiveReaderOptimizedWaveformTest</code> test retrieval of waveform type PVs
 * using optimized algorithm.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceArchiveReaderOptimizedWaveformTest extends AbstractArchiverReaderTesting {

    @Override
    protected ArchiveReader getReader() {
        return new TestApplianceArchiveReader(false,false);
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)
     * method for a double waveform type PV.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalDouble() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_double",true,5, start, end);
        assertEquals("Number of values comparison", 5, vals.length);

        ArchiveVNumberArray val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            double[] array = new double[val.getData().size()];
            for (int j = 0; j < array.length; j++) {
                array[j] = val.getData().getDouble(j);
            }
            assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_DOUBLE[i*2],array,0.000001);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i*2,  val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i*2]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i*2]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)
     * method for a float waveform type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalFloat() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_float",true,5, start, end);
        assertEquals("Number of values comparison", 5, vals.length);

        ArchiveVNumberArray val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            double[] array = new double[val.getData().size()];
            for (int j = 0; j < array.length; j++) {
                array[j] = val.getData().getFloat(j);
            }
            assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_FLOAT[i*2],array,0.000001);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i*2, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i*2]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i*2]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)
     * method for an int waveform type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalInt() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_int",true,5, start, end);
        assertEquals("Number of values comparison", 5, vals.length);

        ArchiveVNumberArray val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            int[] array = new int[val.getData().size()];
            for (int j = 0; j < array.length; j++) {
                array[j] = val.getData().getInt(j);
            }
            assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_INT[i*2],array);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i*2, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i*2]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i*2]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)
     * method for a short waveform type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalShort() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_short",true,5, start,end);
        assertEquals("Number of values comparison", 5, vals.length);

        ArchiveVNumberArray val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            short[] array = new short[val.getData().size()];
            for (int j = 0; j < array.length; j++) {
                array[j] = val.getData().getShort(j);
            }
            assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_SHORT[i*2],array);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i*2, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i*2]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i*2]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)
     * method for a byte waveform type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalByte() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumberArray[] vals = getValuesNumberArray("test_pv_wave_byte",true,5,start,end);
        assertEquals("Number of values comparison", 5, vals.length);

        ArchiveVNumberArray val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            byte[] array = new byte[val.getData().size()];
            for (int j = 0; j < array.length; j++) {
                array[j] = val.getData().getByte(j);
            }
            assertArrayEquals("Value comparison", TestGenMsgIteratorWaveform.VALUE_BYTE[i*2],array);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i*2, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i*2]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i*2]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp, int)
     * method for a string wavefdorm type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalString() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        try {
            getValuesStringArray("test_pv_wave_string",true,5,start,end);
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
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        try {
            getValuesEnumArray("test_pv_wave_enum",true,5,start,end);
            fail();
        } catch (UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }
}
