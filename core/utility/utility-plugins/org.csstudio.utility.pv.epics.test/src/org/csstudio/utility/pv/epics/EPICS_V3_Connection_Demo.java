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

import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.data.values.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** JUnit Demo or command-line test of PV connect/disconnect.
 *
 *  Meant to be executed in JProfiler or while
 *  watching memory usage of process in OS.
 *
 *  These tests require the soft-IOC database from lib/test.db.
 *
 *  When using the JNI CA libs, one also needs ((DY)LD_LIBRARY_)PATH.
 *
 *  When run with use_pure_java = true, JProfiler shows periodic GC.
 *  JVM might grow memory up to the -Xmx limit but doesn't run out of mem.
 *  When monitoring overall memory use with "ps" on OS X and Linux in
 *  a long running test, the RSS sometimes grew for a day, but would
 *  then actually go back down again. Total VSIZE of the process did not
 *  show a long-term growth.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_Connection_Demo implements PVListener
{
    /** PV Name.
     *  "fred" 1Hz
     *  "janet" 10Hz
     *  "longs" array
     */
    private static final String PV_NAME = "longs";


    /** Test runs (connect, ..., disconnect) to perform */
    private static final int TEST_RUNS = 10; // Integer.MAX_VALUE;

    /** Values to receive within each run */
    private static final int VALUE_UPDATES = 2; // 1000;

    /** Updates received from PV */
    final AtomicInteger updates = new AtomicInteger(0);

    // PVListener
    @Override
    public void pvValueUpdate(final PV pv)
    {
        final IValue v = pv.getValue();
        System.out.println(pv.getName() + ": " + v);
        assertEquals(true, pv.isConnected());
        updates.incrementAndGet();
    }

    // PVListener
    @Override
    public void pvDisconnected(final PV pv)
    {
        System.out.println(pv.getName() + " disconnected");
        assertEquals(false, pv.isConnected());
    }

    @Test
    public void testConnections() throws Exception
    {
        for (int i=1; i<TEST_RUNS; ++i)
        {
            log(i);
            updates.set(0);
            final PV pv = TestUtil.getPV(PV_NAME);
            pv.addListener(this);
            pv.start();
            // Allow about 2 seconds for each value
            for (int wait=0; wait<(VALUE_UPDATES*2*1000/200); ++wait)
            {
                if (updates.get() >= VALUE_UPDATES)
                    break;
                Thread.sleep(200);
            }
            assertTrue(updates.get() >= VALUE_UPDATES);
            pv.removeListener(this);
            pv.stop();
        }
    }

    /** Log run and memory usage */
    private void log(final int i)
    {
        final double MB = 1024.0*1024.0;
        final Runtime runtime = Runtime.getRuntime();
        final long max = runtime.maxMemory();
        final long total = runtime.totalMemory();
        System.out.format("%5d - Using %.1f / %.1f MB\n", i, total/MB, max/MB);
    }

    /** Allow invocation from command-line */
    public static void main(String[] args) throws Exception
    {
        new EPICS_V3_Connection_Demo().testConnections();
    }
}
