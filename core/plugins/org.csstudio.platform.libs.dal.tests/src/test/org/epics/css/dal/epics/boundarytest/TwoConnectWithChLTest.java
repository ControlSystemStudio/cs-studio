/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class TwoConnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * The behavior when connecting two channels at different pvs is tested here.
     */
    public void testConnect2Channels() throws Exception {
        ChannelListenerCallbackResults results = registerChannelListenerForPV(CONSTANT_PV);
        ChannelListenerCallbackResults results2 = registerChannelListenerForPV(CONSTANT_PV2);

        Thread.sleep(SLEEP_TIME_MSEC);

        /* 
         * State update is usually called 4 times, but it can also be 5 times due to threading.
         * For the last event the hasValue() returns true in both cases.
         */
        checkConnected(results.stateUpdate, 4, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.stateUpdate, 4, 1, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");

        // This is expected
        checkConnected(results.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.dataUpdate, 1, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");
    }
}
