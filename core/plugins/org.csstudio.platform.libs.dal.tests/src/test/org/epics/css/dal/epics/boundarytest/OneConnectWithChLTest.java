/**
 *
 */
package org.epics.css.dal.epics.boundarytest;


/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class OneConnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * A single pv is connected via ChannelListener.
     */
    public void testConnect() throws Exception {
        ChannelListenerCallbackResults results = registerChannelListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // state update is called 5 times, this is ok
        checkConnected(results.stateUpdate, 5, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnected(results.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
