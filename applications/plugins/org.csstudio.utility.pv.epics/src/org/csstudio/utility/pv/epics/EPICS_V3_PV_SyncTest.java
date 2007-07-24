package org.csstudio.utility.pv.epics;

import junit.framework.TestCase;

import org.csstudio.platform.data.IValue;
import org.csstudio.utility.pv.PV;
import org.junit.Test;

/** These tests require the soft-IOC database from lib/test.db.
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV_SyncTest extends TestCase
{
    @Test
    public void testSyncGet() throws Exception
    {
        PV fred = new EPICS_V3_PV("fred");
        IValue value = fred.getValue(50000.0);
        System.out.println(value);
        fred.stop();
    }

    @Test
    public void testTimeout() throws Exception
    {
        PV none = new EPICS_V3_PV("does_not_exist");
        final long start = System.currentTimeMillis();
        try
        {
            IValue value = none.getValue(5.0);
            fail("Expected a timeout, but got " + value);
        }
        catch (Exception ex)
        {
            assertEquals("Connection timeout: PV does_not_exist",
                         ex.getMessage());
        }
        final long end = System.currentTimeMillis();
        // Did we wait about 5 secs +- 1 sec?
        assertTrue(Math.abs((end-start) - 5000) < 1000);
        none.stop();
    }

    @Test
    public void testSyncGetMultiple() throws Exception
    {
        // This is for the JUnit test within the EPICS_V3_PV
        // implementation.
        // A 'real' program would use
        // PV fred = new PVFactory.createPV("fred");
        PV fred = new EPICS_V3_PV("fred");
        PV janet = new EPICS_V3_PV("janet");
        PV longs = new EPICS_V3_PV("longs");
        
        final double timeout_secs = 5.0;
        System.out.println(fred.getValue(timeout_secs));
        System.out.println(janet.getValue(timeout_secs));
        System.out.println(longs.getValue(timeout_secs));
        System.out.println(fred.getValue(timeout_secs));
        
        longs.stop();
        fred.stop();
        janet.stop();
        assertTrue(PVContext.allReleased());
    }
}
