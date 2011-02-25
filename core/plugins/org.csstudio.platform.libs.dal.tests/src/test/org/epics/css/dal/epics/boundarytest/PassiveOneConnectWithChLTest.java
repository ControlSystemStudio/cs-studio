/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

import org.epics.css.dal.context.ConnectionState;


/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class PassiveOneConnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * A single passive pv is connected via ChannelListener.
     *
     * The scan property of the tested pv is set to 'Passive'.
     *
     */
    public void testConnect() throws Exception {
        ChannelListenerCallbackResults results = registerChannelListenerForPV(PASSIVE_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // is called 4 times, this is ok
        assertEquals(4, results.stateUpdate.count);

        assertEquals(PASSIVE_PV, results.stateUpdate.channel.getUniqueName());

        assertFalse(results.stateUpdate.channel.getProperty().getCondition().hasValue());

        assertEquals("0", results.stateUpdate.channel.getData().stringValue());

        assertEquals(ConnectionState.CONNECTED, results.stateUpdate.channel.getProperty().getConnectionState());
        assertEquals("CONNECTED", results.stateUpdate.channel.getStateInfo());

        assertEquals("ERROR", results.stateUpdate.channel.getData().getSeverity().getSeverityInfo());
        
        assertEquals("UDF_ALARM", results.stateUpdate.channel.getData().getSeverity().descriptionToString());


        // Now we are checking the data update
        assertEquals(1, results.dataUpdate.count);
        assertEquals(PASSIVE_PV, results.dataUpdate.channel.getUniqueName());

        assertFalse(results.dataUpdate.channel.getProperty().getCondition().hasValue());

        assertEquals("0", results.dataUpdate.channel.getData().stringValue());

        assertEquals(ConnectionState.CONNECTED, results.stateUpdate.channel.getProperty().getConnectionState());
        assertEquals("CONNECTED", results.dataUpdate.channel.getStateInfo());

        assertEquals("ERROR", results.dataUpdate.channel.getData().getSeverity().getSeverityInfo());

        assertEquals("UDF_ALARM", results.dataUpdate.channel.getData().getSeverity().descriptionToString());
    }
}
