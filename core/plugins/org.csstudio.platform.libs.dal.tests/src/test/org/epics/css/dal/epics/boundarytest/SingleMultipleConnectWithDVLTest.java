/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class SingleMultipleConnectWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * A single pv will be registered multiple times.
     */
    public void testConnect() throws Exception {
        DynamicValueListenerCallbackResults results1 = registerDynamicValueListenerForPV(CONSTANT_PV);
        DynamicValueListenerCallbackResults results2 = registerDynamicValueListenerForPV(CONSTANT_PV);
        DynamicValueListenerCallbackResults results3 = registerDynamicValueListenerForPV(CONSTANT_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        // the condition change is called 4 times, this is ok
        checkConnectedWithCondition(results1.conditionChange, 4, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results2.conditionChange, 4, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results3.conditionChange, 4, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");

        // This is expected
        checkConnectedWithCondition(results1.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results2.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results3.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
    }
}
