package org.csstudio.utility.pv.epics;

import org.junit.Test;
import static org.junit.Assert.*;

/** Test of the PV Context.
 *  @author Kay Kasemir
 */
public class PVContextTest
{
    @SuppressWarnings("nls")
    @Test
    public void testContext() throws Exception
    {
        assertTrue(PVContext.allReleased());
        final RefCountedChannel fred = PVContext.getChannel("fred");
        assertNotNull(fred);
        assertFalse(PVContext.allReleased());
        final RefCountedChannel jane = PVContext.getChannel("jane");
        assertNotNull(jane);
        final RefCountedChannel fred2 = PVContext.getChannel("fred");
        assertTrue(fred == fred2);
        assertFalse(PVContext.allReleased());
        
        PVContext.releaseChannel(fred2);
        assertFalse(PVContext.allReleased());
        PVContext.releaseChannel(jane);
        assertFalse(PVContext.allReleased());
        PVContext.releaseChannel(fred);
        assertTrue(PVContext.allReleased());
    }
}
