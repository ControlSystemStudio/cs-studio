/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class OneReconnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * Now the behavior of reconnecting the same pv is tested. After reconnect
     * the state update callback must be called again.
     */
    public void testReconnect() throws Exception {
        ChannelListenerCallbackResults results = registerChannelListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // The behavior for connect is already tested, the asserts are not
        // repeated here

        deregisterListenerForPV(CONSTANT_PV);
        Thread.sleep(SLEEP_TIME_MSEC);

        results = registerChannelListenerForPV(CONSTANT_PV);
        Thread.sleep(SLEEP_TIME_MSEC);

        // the state update is called
        assertTrue(results.stateUpdate.wasCalled());

        // data update is delievered only once
        checkConnected(results.dataUpdate, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
