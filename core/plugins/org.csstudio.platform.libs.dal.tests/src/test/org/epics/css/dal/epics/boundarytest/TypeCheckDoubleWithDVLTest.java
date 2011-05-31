/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class TypeCheckDoubleWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * Testing connecting to EPICS channel using a dynamic value listener and Double type.
     */
    public void testDoubleType() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(STRING_IN_PV, Double.class);

        Thread.sleep(SLEEP_TIME_MSEC);

        assertTrue(results.errorResponse.wasCalled());
        assertFalse(results.timelagStarts.wasCalled());
        assertFalse(results.timelagStops.wasCalled());
        assertFalse(results.timeoutStarts.wasCalled());
        assertFalse(results.timeoutStops.wasCalled());
        
        assertTrue(results.valueChanged.wasCalled());
        assertFalse(results.valueUpdated.wasCalled());

        assertEquals(4, results.conditionChange.count);
        assertEquals(STRING_IN_PV, results.conditionChange.event.getProperty().getUniqueName());
        
        assertTrue(results.conditionChange.event.getProperty().getCondition().hasValue());
        assertEquals(Double.NaN, results.conditionChange.event.getData().doubleValue());
        assertEquals("NORMAL", results.conditionChange.event.getProperty().getCondition().getSeverityInfo());
        assertEquals("NORMAL", results.conditionChange.event.getData().getSeverity().getSeverityInfo());
        assertEquals("NO_ALARM", results.conditionChange.event.getData().getStatus());

    }
}
