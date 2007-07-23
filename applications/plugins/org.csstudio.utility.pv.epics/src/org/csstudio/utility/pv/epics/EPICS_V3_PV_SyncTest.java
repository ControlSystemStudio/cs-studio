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
        EPICS_V3_PV pv = new EPICS_V3_PV("fred");
        IValue value = pv.getValue(50000.0);
        System.out.println(value);
        pv.stop();
    }
}
