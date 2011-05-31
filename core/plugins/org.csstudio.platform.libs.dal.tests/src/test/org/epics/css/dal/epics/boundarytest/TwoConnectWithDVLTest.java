/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class TwoConnectWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * The behavior when connecting two channels at the same pv is tested here.
     */
    public void testConnect2Channels() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(CONSTANT_PV);
        DynamicValueListenerCallbackResults results2 = registerDynamicValueListenerForPV(CONSTANT_PV2);

        Thread.sleep(SLEEP_TIME_MSEC);

        // the condition change is called 4 times, this is ok
        checkConnectedWithCondition(results.conditionChange, 4, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results2.conditionChange, 4, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");

        // This is expected
        checkConnectedWithCondition(results.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results2.valueChanged, 1, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");
    }
}
