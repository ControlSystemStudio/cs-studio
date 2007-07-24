package org.csstudio.utility.pv.epics;

import junit.framework.TestCase;

import org.csstudio.platform.data.IValue;
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
        EPICS_V3_PV fred = new EPICS_V3_PV("fred");
        IValue value = fred.get(50000.0);
        System.out.println(value);
        fred.stop();
    }

    @Test
    public void testSyncGetMultiple() throws Exception
    {
        EPICS_V3_PV fred = new EPICS_V3_PV("fred");
        EPICS_V3_PV janet = new EPICS_V3_PV("janet");
        EPICS_V3_PV longs = new EPICS_V3_PV("longs");
        
        IValue f = fred.get(50000.0);
        System.out.println(f);
        IValue j = janet.get(50000.0);
        System.out.println(j);
        IValue l = longs.get(50000.0);
        System.out.println(l);
        
        longs.stop();
        fred.stop();
        janet.stop();
        assertTrue(PVContext.allReleased());
    }
}
