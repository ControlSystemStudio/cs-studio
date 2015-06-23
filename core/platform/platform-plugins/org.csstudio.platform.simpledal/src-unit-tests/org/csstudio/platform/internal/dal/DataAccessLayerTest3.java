package org.csstudio.platform.internal.dal;

import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * DAL Tests. This test class concentrates on functionalities that receive
 * {@link AnyData} via a {@link ChannelListener} registered using DALs narrow
 * interface:
 * {@link SimpleDALBroker#registerListener(ConnectionParameters, ChannelListener)}
 * .
 *
 * The record under test is:
 *
 * <pre>
 * record(ai,'Chiller:Pressure:1') {
 *             field(DESC,'DEMO')
 *             field(SCAN,'.1 second')
 *             field(INP,'2.22')
 *             field(HIHI,'35.00')
 *             field(HIGH,'30.00')
 *             field(LOW,'10.00')
 *             field(LOLO,'5.00')
 *             field(HOPR,'40.00')
 *             field(LOPR,'1.11')
 *             field(HHSV,'NO_ALARM')
 *             field(LLSV,'NO_ALARM')
 *             field(HSV,'NO_ALARM')
 *             field(LSV,'NO_ALARM')
 *     }
 * </pre>
 *
 * @author Sven Wende
 *
 */
public class DataAccessLayerTest3 extends AbstractTestBase {
    private RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", PV, null, null);

    public void testAnyData() throws Exception {
        doTestAnyData();

        // .. we do this twice to ensure, that additional listeners that
        // are registered for the same channel get correct information, too
        Thread.sleep(1000);
        doTestAnyData();
    }

    private void doTestAnyData() throws Exception {
        // .. we check the latest received data
        AnyData data = receiveViaListener(ri);
        assertNotNull(data);

        // .. metadata has to be there and correctly filled
        MetaData meta = data.getMetaData();
        assertNotNull(meta);
        assertEquals(RECORD_LOPR, meta.getDisplayLow());
        assertEquals(RECORD_HOPR, meta.getDisplayHigh());
        assertEquals(RECORD_LOLO, meta.getAlarmLow());
        assertEquals(RECORD_HIHI, meta.getAlarmHigh());
        assertEquals(RECORD_LOW, meta.getWarnLow());
        assertEquals(RECORD_HIGH, meta.getWarnHigh());
    }
}
