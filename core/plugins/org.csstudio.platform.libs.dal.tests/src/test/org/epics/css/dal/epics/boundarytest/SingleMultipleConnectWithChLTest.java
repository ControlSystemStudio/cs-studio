/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class SingleMultipleConnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * A single pv will be registered multiple times.
     */
    public void testConnect() throws Exception {
        ChannelListenerCallbackResults results1 = registerChannelListenerForPV(CONSTANT_PV);
        ChannelListenerCallbackResults results2 = registerChannelListenerForPV(CONSTANT_PV);
        ChannelListenerCallbackResults results3 = registerChannelListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // the first registration receives as many events as the others, this is ok
        checkConnected(results1.stateUpdate, 5, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.stateUpdate, 5, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results3.stateUpdate, 5, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");

        // This is expected
        checkConnected(results1.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results2.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results3.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
