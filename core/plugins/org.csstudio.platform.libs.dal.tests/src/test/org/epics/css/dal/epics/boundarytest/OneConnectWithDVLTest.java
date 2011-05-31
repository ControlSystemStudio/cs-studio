/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class OneConnectWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * A single pv is connected via DynamicValueListener.
     */
    public void testConnect() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // condition change is called 4 times, this is ok
        checkConnectedWithCondition(results.conditionChange, 4, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
