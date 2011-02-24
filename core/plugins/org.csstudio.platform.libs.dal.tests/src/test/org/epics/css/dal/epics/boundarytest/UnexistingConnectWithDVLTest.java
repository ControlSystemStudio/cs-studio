/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.context.ConnectionState;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
@SuppressWarnings("unchecked")
public class UnexistingConnectWithDVLTest extends AbstractDALBoundaryTest {

    /**
     * Testing connecting to unexisting EPICS channel using a dynamic value
     * listener.
     */
    public void testUnexistingChannel() throws Exception {
        DynamicValueListenerCallbackResults results = registerDynamicValueListenerForPV(DOES_NOT_EXIST_NAME);

        Thread.sleep(SLEEP_TIME_MSEC);

        assertTrue(results.conditionChange.wasCalled());
        // condition change is called two times
        assertEquals(2, results.conditionChange.count);

        // state is CONNECTION_FAILED
        assertEquals(ConnectionState.CONNECTION_FAILED, ((DynamicValueProperty) results.conditionChange.event
                .getProperty()).getConnectionState());

        // errorResponse is called when connection fails
        assertTrue(results.errorResponse.wasCalled());
        assertFalse(results.timelagStarts.wasCalled());
        assertFalse(results.timelagStops.wasCalled());
        assertFalse(results.timeoutStarts.wasCalled());
        assertFalse(results.timeoutStops.wasCalled());
        assertFalse(results.valueChanged.wasCalled());
        assertFalse(results.valueUpdated.wasCalled());
    }
}
