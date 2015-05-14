package org.csstudio.platform.internal.dal;

import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * DAL Tests. This test class concentrates on functionalities that receive
 * characteristics synchronously via
 * {@link SimpleDALBroker#getValue(RemoteInfo)} and asynchronously via
 * {@link SimpleDALBroker#getValueAsync(ConnectionParameters, ResponseListener)}
 * .
 *
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
public class DataAccessLayerTest2 extends AbstractTestBase {

    public void testReceiveCharacteristics() throws Exception {
        doTestSyncChararacteristic(CharacteristicInfo.C_GRAPH_MIN, RECORD_LOPR);
        doTestSyncChararacteristic(CharacteristicInfo.C_GRAPH_MAX, RECORD_HOPR);
        doTestSyncChararacteristic(CharacteristicInfo.C_ALARM_MIN, RECORD_LOLO);
        doTestSyncChararacteristic(CharacteristicInfo.C_ALARM_MAX, RECORD_HIHI);
        doTestSyncChararacteristic(CharacteristicInfo.C_WARNING_MIN, RECORD_LOW);
        doTestSyncChararacteristic(CharacteristicInfo.C_WARNING_MAX, RECORD_HIGH);
    }

    public void testReceiveCharacteristicsAsync() throws Exception {
        doTestAsyncChararacteristic(CharacteristicInfo.C_GRAPH_MIN, RECORD_LOPR);
        doTestAsyncChararacteristic(CharacteristicInfo.C_GRAPH_MAX, RECORD_HOPR);
        doTestAsyncChararacteristic(CharacteristicInfo.C_ALARM_MIN, RECORD_LOLO);
        doTestAsyncChararacteristic(CharacteristicInfo.C_ALARM_MAX, RECORD_HIHI);
        doTestAsyncChararacteristic(CharacteristicInfo.C_WARNING_MIN, RECORD_LOW);
        doTestAsyncChararacteristic(CharacteristicInfo.C_WARNING_MAX, RECORD_HIGH);
    }

    private void doTestSyncChararacteristic(CharacteristicInfo characteristic, double expectedValue) throws Exception {
        RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", PV, characteristic.getName(), null);
        assertEquals(expectedValue, broker.getValue(ri));
    }

    private void doTestAsyncChararacteristic(CharacteristicInfo characteristic, double expectedValue) throws Exception {
        RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", PV, characteristic.getName(), null);
        Double value = receiveAsync(ri);
        assertNotNull(value);
        assertEquals(expectedValue, value);
    }
}
