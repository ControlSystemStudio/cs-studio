package org.csstudio.utility.pv.epics;

import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

import org.junit.Test;
import static org.junit.Assert.*;

/** Test of the PV Context.
 *  @author Kay Kasemir
 */
public class PVContextTest
{
    ConnectionListener conn_callback = new ConnectionListener()
    {
        public void connectionChanged(ConnectionEvent ev)
        {
            // Ignore
        }
    };
        
    @Test
    @SuppressWarnings("nls")
    public void testContext() throws Exception
    {
        assertTrue(PVContext.allReleased());
        final RefCountedChannel fred = PVContext.getChannel("fred", conn_callback);
        assertNotNull(fred);
        assertFalse(PVContext.allReleased());
        final RefCountedChannel jane = PVContext.getChannel("jane", conn_callback);
        assertNotNull(jane);
        final RefCountedChannel fred2 = PVContext.getChannel("fred", conn_callback);
        assertTrue(fred == fred2);
        assertFalse(PVContext.allReleased());
        
        PVContext.releaseChannel(fred2, conn_callback);
        assertFalse(PVContext.allReleased());
        PVContext.releaseChannel(jane, conn_callback);
        assertFalse(PVContext.allReleased());
        PVContext.releaseChannel(fred, conn_callback);
        assertTrue(PVContext.allReleased());
    }
}
