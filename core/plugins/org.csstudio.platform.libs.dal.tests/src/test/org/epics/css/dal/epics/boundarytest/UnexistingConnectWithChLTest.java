/**
 *
 */
package org.epics.css.dal.epics.boundarytest;

import org.epics.css.dal.context.ConnectionState;

/**
 * Test to demonstrate the behavior of the simple dal api.
 *
 * This test must not be run together with other simple dal api tests, because of the simple dal broker singleton.
 */
public class UnexistingConnectWithChLTest extends AbstractDALBoundaryTest {

    /**
     * Testing connecting to unexisting EPICS channel using a channel listener.
     */
    public void testUnexistingChannel() throws Exception {
        ChannelListenerCallbackResults results = registerChannelListenerForPV(DOES_NOT_EXIST_NAME);

        Thread.sleep(SLEEP_TIME_MSEC);

        assertFalse(results.dataUpdate.wasCalled());

        // state update is called 3 times, this is ok
        assertEquals(3, results.stateUpdate.count);

        // state is CONNECTION_FAILED
        assertEquals(ConnectionState.CONNECTION_FAILED, results.stateUpdate.channel.getProperty()
                .getConnectionState());
    }
}
