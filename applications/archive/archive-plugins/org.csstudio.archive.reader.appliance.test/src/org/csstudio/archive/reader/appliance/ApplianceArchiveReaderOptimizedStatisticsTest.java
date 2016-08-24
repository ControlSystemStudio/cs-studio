package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorOptimized;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorRaw;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.util.time.TimeDuration;
import org.junit.Test;

/**
 *
 * <code>ApplianceArchiveReaderOptimizedStatisticsTest</code> tests the
 * optimized data retrieval using a dummy loopback appliance server.
 * Data is requested to be provided with statistical info.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceArchiveReaderOptimizedStatisticsTest extends AbstractArchiverReaderTesting {

    @Override
    protected ArchiveReader getReader() {
        return new TestApplianceArchiveReader(true,false);
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a double type PV where the requested number of points is greater
     * than total number of points.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalWhenThereAreNotManyPoints() throws Exception{
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_double",1800,start,end);
        assertEquals("Number of values comparison", 10, vals.length);

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVNumber)vals[i];
            assertEquals("Value comparison", Double.valueOf(TestGenMsgIteratorRaw.VALUES_DOUBLE[i%TestGenMsgIteratorRaw.VALUES_DOUBLE.length]),(Double)val.getValue(),0.0001);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a double type PV.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalDouble() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_double",100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;

        ArchiveVStatistics val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVStatistics) vals[i];
            Double v = TestGenMsgIteratorOptimized.VALUES_DOUBLE[i%TestGenMsgIteratorOptimized.VALUES_DOUBLE.length];
            Integer c = TestGenMsgIteratorOptimized.VALUES_COUNT[i%TestGenMsgIteratorOptimized.VALUES_COUNT.length];
            assertEquals("Average value comparison", v,val.getAverage(),0.0001);
            assertEquals("STD value comparison", v,val.getStdDev(),0.0001);
            assertEquals("Min value comparison", v,val.getMin(),0.0001);
            assertEquals("Max value comparison", v,val.getMax(),0.0001);
            assertEquals("Number of points comparison", c, val.getNSamples());
            assertEquals("Timestamp comparison", startM + i*step,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i%TestGenMsgIteratorOptimized.STATUS.length]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a float type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalFloat() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_float",100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;


        ArchiveVStatistics val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVStatistics) vals[i];
            Double v = TestGenMsgIteratorOptimized.VALUES_DOUBLE[i%TestGenMsgIteratorOptimized.VALUES_DOUBLE.length];
            Integer c = TestGenMsgIteratorOptimized.VALUES_COUNT[i%TestGenMsgIteratorOptimized.VALUES_COUNT.length];
            assertEquals("Average value comparison", v,val.getAverage(),0.0001);
            assertEquals("STD value comparison", v,val.getStdDev(),0.0001);
            assertEquals("Min value comparison", v,val.getMin(),0.0001);
            assertEquals("Max value comparison", v,val.getMax(),0.0001);
            assertEquals("Number of points comparison", c, val.getNSamples());
            assertEquals("Timestamp comparison", startM + i*step,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i%TestGenMsgIteratorOptimized.STATUS.length]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for an int type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalInt() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_int",100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;


        ArchiveVStatistics val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVStatistics) vals[i];
            Double v = TestGenMsgIteratorOptimized.VALUES_DOUBLE[i%TestGenMsgIteratorOptimized.VALUES_DOUBLE.length];
            Integer c = TestGenMsgIteratorOptimized.VALUES_COUNT[i%TestGenMsgIteratorOptimized.VALUES_COUNT.length];
            assertEquals("Average value comparison", v,val.getAverage(),0.0001);
            assertEquals("STD value comparison", v,val.getStdDev(),0.0001);
            assertEquals("Min value comparison", v,val.getMin(),0.0001);
            assertEquals("Max value comparison", v,val.getMax(),0.0001);
            assertEquals("Number of points comparison", c, val.getNSamples());
            assertEquals("Timestamp comparison", startM + i*step,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i%TestGenMsgIteratorOptimized.STATUS.length]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a short type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalShort() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_short",100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;


        ArchiveVStatistics val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVStatistics) vals[i];
            Double v = TestGenMsgIteratorOptimized.VALUES_DOUBLE[i%TestGenMsgIteratorOptimized.VALUES_DOUBLE.length];
            Integer c = TestGenMsgIteratorOptimized.VALUES_COUNT[i%TestGenMsgIteratorOptimized.VALUES_COUNT.length];
            assertEquals("Average value comparison", v,val.getAverage(),0.0001);
            assertEquals("STD value comparison", v,val.getStdDev(),0.0001);
            assertEquals("Min value comparison", v,val.getMin(),0.0001);
            assertEquals("Max value comparison", v,val.getMax(),0.0001);
            assertEquals("Number of points comparison", c, val.getNSamples());
            assertEquals("Timestamp comparison", startM + i*step,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i%TestGenMsgIteratorOptimized.STATUS.length]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a byte type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalByte() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_byte",100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;


        ArchiveVStatistics val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVStatistics) vals[i];
            Double v = TestGenMsgIteratorOptimized.VALUES_DOUBLE[i%TestGenMsgIteratorOptimized.VALUES_DOUBLE.length];
            Integer c = TestGenMsgIteratorOptimized.VALUES_COUNT[i%TestGenMsgIteratorOptimized.VALUES_COUNT.length];
            assertEquals("Average value comparison", v,val.getAverage(),0.0001);
            assertEquals("STD value comparison", v,val.getStdDev(),0.0001);
            assertEquals("Min value comparison", v,val.getMin(),0.0001);
            assertEquals("Max value comparison", v,val.getMax(),0.0001);
            assertEquals("Number of points comparison", c, val.getNSamples());
            assertEquals("Timestamp comparison", startM + i*step,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i%TestGenMsgIteratorOptimized.STATUS.length]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a string type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalString() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_string",100,start,end);
        assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);

        ArchiveVString val = null;
        //string values cannot be loaded as statistics
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVString)vals[i];
            assertEquals("Value comparison", "9554.0",val.getValue());
            assertEquals("Timestamp comparison", start.toEpochMilli() + i,val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i]), val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for an enum type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalEnum() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv_enum",100,start,end);
        assertEquals("Number of values comparison", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);

        ArchiveVEnum val = null;
        //string values cannot be loaded as statistics
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVEnum)vals[i];
            assertEquals("Value comparison", "Enum <" + (i+1) + ">",val.getValue());
            assertEquals("Timestamp comparison", start.toEpochMilli() + i, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorOptimized.STATUS[i]), val.getAlarmName());
        }
    }
}
