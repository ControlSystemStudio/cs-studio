package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorRaw;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorWaveform;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.util.time.TimeDuration;
import org.junit.Test;

/**
 *
 * <code>ApplianceArchiveReaderNewOptimizedTest</code> tests the new optimized data retrieval using a dummy loopback
 * appliance server. This class test the retrieval methods, if they decode the data correctly. It doesn't tests if data
 * makes sense.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceArchiveReaderNewOptimizedTest extends AbstractArchiverReaderTesting {

    @Override
    protected ArchiveReader getReader() {
        return new TestApplianceArchiveReader(true, true);
    }

    /**
     * Tests {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)} method for a double
     * type PV where the requested number of points is greater than total number of points.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalWhenThereAreNotManyPoints() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv", true, 2000, start, end);
        assertEquals("Number of values comparison", 100, vals.length);

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVNumber) vals[i];
            assertEquals(
                    "Value comparison",
                    Double.valueOf(TestGenMsgIteratorRaw.VALUES_DOUBLE[i % TestGenMsgIteratorRaw.VALUES_DOUBLE.length]),
                    (Double) val.getValue(), 0.0001);
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i%TestGenMsgIteratorRaw.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", String.valueOf(TestGenMsgIteratorRaw.STATUS[i%TestGenMsgIteratorRaw.SEVERITIES.length]), val.getAlarmName());
        }
    }

    /**
     * Tests {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)} method for a double
     * type PV.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalDouble() throws Exception {
    	Instant end = Instant.now();
    	Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVType[] vals = getValuesStatistics("test_pv", 10, start, end);
        assertEquals("Number of values comparison", 10, vals.length);

        long startM = start.toEpochMilli();

        ArchiveVStatistics val = null;
        for (int i = 0; i < vals.length; i++) {
            val = (ArchiveVStatistics)vals[i];
            assertEquals(
                    "Mean Value comparison",
                    TestGenMsgIteratorWaveform.VALUE_DOUBLE[i % TestGenMsgIteratorWaveform.VALUE_DOUBLE.length][0],
                    val.getAverage().doubleValue(), 0.0001);
            assertEquals(
                    "STD Value comparison",
                    TestGenMsgIteratorWaveform.VALUE_DOUBLE[i % TestGenMsgIteratorWaveform.VALUE_DOUBLE.length][1],
                    val.getStdDev().doubleValue(), 0.0001);
            assertEquals(
                    "Min Value comparison",
                    TestGenMsgIteratorWaveform.VALUE_DOUBLE[i % TestGenMsgIteratorWaveform.VALUE_DOUBLE.length][2],
                    val.getMin().doubleValue(), 0.0001);
            assertEquals(
                    "Max Value comparison",
                    TestGenMsgIteratorWaveform.VALUE_DOUBLE[i % TestGenMsgIteratorWaveform.VALUE_DOUBLE.length][3],
                    val.getMax().doubleValue(), 0.0001);
            assertEquals(
                    "Count Value comparison",
                    (int)TestGenMsgIteratorWaveform.VALUE_DOUBLE[i % TestGenMsgIteratorWaveform.VALUE_DOUBLE.length][4],
                    val.getNSamples().intValue());
            assertEquals("Timestamp comparison", startM + i, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorWaveform.SEVERITIES[i
                    % TestGenMsgIteratorWaveform.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status",
                    String.valueOf(TestGenMsgIteratorWaveform.STATUS[i % TestGenMsgIteratorWaveform.STATUS.length]),
                    val.getAlarmName());
        }
    }
}
