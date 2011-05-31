/**
 *
 */
package org.epics.css.dal.epics.boundarytest;


/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class TypeCheckStringWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * Testing connecting to EPICS channel using a dynamic value listener and String type.
     */
    public void testStringType() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(STRING_IN_PV);

        Thread.sleep(SLEEP_TIME_MSEC);

        checkConnected(results.conditionChange, 4, STRING_IN_PV, "This is my string", "NORMAL", "NO_ALARM");

        assertFalse(results.errorResponse.wasCalled());
        assertFalse(results.timelagStarts.wasCalled());
        assertFalse(results.timelagStops.wasCalled());
        assertFalse(results.timeoutStarts.wasCalled());
        assertFalse(results.timeoutStops.wasCalled());
        assertTrue(results.valueChanged.wasCalled());
        assertFalse(results.valueUpdated.wasCalled());

    }
}
