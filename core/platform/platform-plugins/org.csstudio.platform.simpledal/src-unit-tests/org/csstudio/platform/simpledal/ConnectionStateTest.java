/**
 *
 */
package org.csstudio.platform.simpledal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for {@link ConnectionState}.
 *
 * @author Sven Wende
 *
 */
public class ConnectionStateTest {

    /**
     * Test method for
     * {@link org.csstudio.platform.simpledal.ConnectionState#getDalState()}.
     */
    @Test
    public void testGetDalState() {
        for (ConnectionState s : ConnectionState.values()) {
            if (s == ConnectionState.UNKNOWN) {
                assertNull(s.getDalState());
            } else {
                assertNotNull(s.getDalState());
            }
        }
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.simpledal.ConnectionState#translate(org.csstudio.dal.context.ConnectionState)}.
     */
    @Test
    public void testTranslate() {
        for (ConnectionState s : ConnectionState.values()) {
            assertEquals(s, ConnectionState.translate(s.getDalState()));
        }
    }
}
