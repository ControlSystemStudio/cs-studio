/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.csstudio.vtype.pv.local.LocalPVFactory;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the {@link Filter}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FilterUnitTest implements FilterListener
{
    private AtomicInteger updates = new AtomicInteger();
    // Most recent value from filter.
    // SYNC on this
    private double last_value = Double.NaN;

    @Before
    public void setup()
    {    // Running as plain unit tests, so PVPool is not
        // initialized from extension point registry
        PVPool.addPVFactory(new LocalPVFactory());
        PVPool.addPVFactory(new JCA_PVFactory());
        PVPool.setDefaultType(LocalPVFactory.TYPE);

        // Configure logging to show 'all'
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.ALL);

        // Disable some messages
        Logger.getLogger("com.cosylab.epics").setLevel(Level.SEVERE);
    }

    @Override
    public void filterChanged(final double value)
    {
        System.err.println("Filter evaluates to " + value);
        updates.incrementAndGet();
        synchronized (this)
        {
            last_value  = value;
            notifyAll();
        }
    }

    @Test(timeout=8000)
    public void testFilter() throws Exception
    {
        // Create local PVs
        final PV x = PVPool.getPV("loc://x(1.0)");
        final PV y = PVPool.getPV("loc://y(2.0)");
        // Set initial value because another test may already have used those vars..
        x.write(1.0);
        y.write(2.0);

        final Filter filter = new Filter("'loc://x(1.0)' + 'loc://y(2.0)'", this);
        filter.start();

        // Await initial value
        synchronized (this)
        {
            while (last_value != 3.0)
                wait();
        }
        System.err.println("Received " + updates.get() + " updates");

        // May get update for this.. (2 or 3), or not
        x.write(4.0);
        // Definite update for both values: Anything from 2 to 4
        y.write(6.0);

        synchronized (this)
        {
            while (last_value != 10.0)
                wait();
        }
        System.err.println("Received " + updates.get() + " updates");

        filter.stop();
    }

    @Test(timeout=8000)
    public void testUpdates() throws Exception
    {
        // Create local PVs
        final PV x = PVPool.getPV("loc://x(1.0)");
        // Set initial value because another test may already have used those vars..
        x.write(1.0);

        final Filter filter = new Filter("'loc://x(1.0)' < 5 ? 1 : 2", this);
        filter.start();

        // Wait for initial value
        synchronized (this)
        {
            while (last_value != 1.0)
                wait();
        }
        final int received_updates = updates.get();
        System.err.println("Received " + received_updates + " updates");

        // Variable changes, but result of formula doesn't, so there shouldn't be an update
        x.write(2.0);
        TimeUnit.SECONDS.sleep(2);
        assertThat(updates.get(), equalTo(received_updates));

        // Once the value changes, there should be another update
        x.write(6.0);
        synchronized (this)
        {
            while (last_value != 2.0)
                wait();
        }
        System.err.println("Received " + updates.get() + " updates");
        assertThat(updates.get(), equalTo(received_updates + 1));

        filter.stop();
    }

    @Test(timeout=50000)
    public void testPVError() throws Exception
    {
        synchronized (this)
        {
            last_value = Double.NaN;
        }

        final Filter filter = new Filter("'ca://bogus_pv_name' * 2", this);
        filter.start();

        System.err.println("Waiting for timeout from bogus PV name...");

        // Default time out is 30 seconds
        // Should not get any updates while waiting for the connection...
        TimeUnit.SECONDS.sleep(30 / 2);
        assertThat(updates.get(), equalTo(0));

        synchronized (this)
        {    // Last value should remain unchanged
            assertThat(Double.isNaN(last_value), equalTo(true));
        }

        filter.stop();
    }
}
