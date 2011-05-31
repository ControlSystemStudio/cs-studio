/**
 *
 */
package org.epics.css.dal.epics.boundarytest;


/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class TwoReconnectWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * Now two channels are deregistered and registered again.
     */
    public void testReconnect2Channels() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(CONSTANT_PV);
        DynamicValueListenerCallbackResults results2 = registerDynamicValueListenerForPV(CONSTANT_PV2);

        Thread.sleep(SLEEP_TIME_MSEC);

        // the asserts after registering are not repeated here

        deregisterListenerForPV(CONSTANT_PV);
        deregisterListenerForPV(CONSTANT_PV2);
        Thread.sleep(SLEEP_TIME_MSEC);

        results = registerDynamicValueListenerForPV(CONSTANT_PV);
        results2 = registerDynamicValueListenerForPV(CONSTANT_PV2);

        Thread.sleep(SLEEP_TIME_MSEC);

        // This is expected
        checkConnectedWithCondition(results.conditionChange, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results2.conditionChange, 1, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");
        checkConnectedWithCondition(results.valueChanged, 1, CONSTANT_PV, "66", "WARNING", "HIGH_ALARM");
        checkConnectedWithCondition(results2.valueChanged, 1, CONSTANT_PV2, "14", "ALARM", "LOLO_ALARM");
    }
}
