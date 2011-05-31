/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class SingleMultipleReconnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * A single pv will be registered multiple times.
     */
    public void testReconnect() throws Exception {
        ChannelListenerCallbackResults results1 = registerChannelListenerForPV(CONSTANT_PV);
        ChannelListenerCallbackResults results2 = registerChannelListenerForPV(CONSTANT_PV);
        ChannelListenerCallbackResults results3 = registerChannelListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // The behavior for connect is already tested, the asserts are not
        // repeated here

        deregisterListenerForPV(CONSTANT_PV);
        deregisterListenerForPV(CONSTANT_PV);
        deregisterListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        results1 = registerChannelListenerForPV(CONSTANT_PV);
        results2 = registerChannelListenerForPV(CONSTANT_PV);
        results3 = registerChannelListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // This is expected
        checkConnected(results1.stateUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.stateUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results3.stateUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");

        checkConnected(results1.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results3.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
