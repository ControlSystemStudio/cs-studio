/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class TwoReconnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * Now two channels are deregistered and registered again.
     */
    public void testReconnect2Channels() throws Exception {
        ChannelListenerCallbackResults results = registerChannelListenerForPV(CONSTANT_PV);
        ChannelListenerCallbackResults results2 = registerChannelListenerForPV(CONSTANT_PV2);

        Thread.sleep(SLEEP_TIME_MSEC);

        // the asserts after registering are not repeated here

        deregisterListenerForPV(CONSTANT_PV);
        deregisterListenerForPV(CONSTANT_PV2);
        Thread.sleep(SLEEP_TIME_MSEC);

        results = registerChannelListenerForPV(CONSTANT_PV);
        results2 = registerChannelListenerForPV(CONSTANT_PV2);

        Thread.sleep(SLEEP_TIME_MSEC);

        // state update is called
        assertTrue(results.stateUpdate.wasCalled());
        assertTrue(results2.stateUpdate.wasCalled());

        // data update is called once
        checkConnected(results.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.dataUpdate, 1, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");
    }
}
