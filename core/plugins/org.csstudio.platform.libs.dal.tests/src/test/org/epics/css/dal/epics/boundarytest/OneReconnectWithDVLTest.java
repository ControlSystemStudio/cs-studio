/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class OneReconnectWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * A single pv is reconnected via ChannelListener.
     */
    public void testReconnect() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // The behavior for connect is already tested, the asserts are not
        // repeated here

        deregisterListenerForPV(CONSTANT_PV);
        Thread.sleep(SLEEP_TIME_MSEC);

        results = registerDynamicValueListenerForPV(CONSTANT_PV);
        Thread.sleep(SLEEP_TIME_MSEC);

        // This is expected
        checkConnectedWithCondition(results.conditionChange, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
