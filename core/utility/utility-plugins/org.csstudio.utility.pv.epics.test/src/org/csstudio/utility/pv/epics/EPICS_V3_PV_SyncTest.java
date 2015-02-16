/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.csstudio.data.values.IValue;
import org.csstudio.utility.pv.PV;
import org.junit.Test;

/** These tests require the soft-IOC database from lib/test.db.
 *  <p>
 *  When using the JNI CA libs, one also needs ((DY)LD_LIBRARY_)PATH.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV_SyncTest
{
    @Test
    public void testSyncGet() throws Exception
    {
        final PV fred = TestUtil.getPV("fred");
        final IValue value = fred.getValue(10.0);
        System.out.println(value);
        fred.stop();
    }

    @Test(timeout=10000)
    public void testTimeout() throws Exception
    {
        final PV none = TestUtil.getPV("does_not_exist");
        final long start = System.currentTimeMillis();
        final int timeout_secs = 5;
        try
        {
            final IValue value = none.getValue(timeout_secs);
            fail("Expected a timeout, but got " + value);
        }
        catch (final Exception ex)
        {
            assertEquals("PV does_not_exist connection timeout",
                         ex.getMessage());
        }
        finally
        {   // Even if we never succeeded, there are resources to clean up
            none.stop();
        }
        final long end = System.currentTimeMillis();
        // Did we wait according to timeout +- 1 sec?
        assertTrue(Math.abs((end-start) - timeout_secs * 1000) < 1000);
        none.stop();
    }

    @Test
    public void testSyncGetMultiple() throws Exception
    {
        // This is for the JUnit test within the EPICS_V3_PV
        // implementation.
        // A 'real' program would use
        // PV fred = new PVFactory.createPV("fred");
        final PV fred = TestUtil.getPV("fred");
        final PV janet = TestUtil.getPV("janet");
        final PV longs = TestUtil.getPV("longs");

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
