/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.hamcrest.CoreMatchers.*;
import static org.csstudio.utility.test.HamcrestMatchers.*;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.util.time.TimeDuration.ofSeconds;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.jca.JCADataSource;
import org.epics.pvmanager.loc.LocalDataSource;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
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
    {
        final CompositeDataSource source = new CompositeDataSource();
        source.putDataSource("loc", new LocalDataSource());
        source.putDataSource("ca", new JCADataSource());
        PVManager.setDefaultDataSource(source);
        
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.ALL);
        
        // Disable most messages from PVManager and JCA
        Logger.getLogger("org.epics.pvmanager").setLevel(Level.SEVERE);
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
        final PV<Object, Object> x = PVManager.readAndWrite(channel("loc://x(1.0)")).synchWriteAndMaxReadRate(ofSeconds(1.0));
        final PV<Object, Object> y = PVManager.readAndWrite(channel("loc://y(2.0)")).synchWriteAndMaxReadRate(ofSeconds(1.0));
        
        final Filter filter = new Filter("'loc://x' + 'loc://y'", this);
        filter.start();

        // Update 1?: May get initial update where vars are undefined, so filter computes NaN
        
        // Update 1 or 2: 1+2=3
        synchronized (this)
        {
            while (last_value != 3.0)
                wait();
        }
        assertThat(updates.get(), greaterThanOrEqualTo(1));
        
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
        assertThat(updates.get(), greaterThanOrEqualTo(2));
        
        filter.stop();
    }

    @Test(timeout=8000)
    public void testUpdates() throws Exception
    {
        // Create local PVs
        final PV<Object, Object> x = PVManager.readAndWrite(channel("loc://x(1.0)")).synchWriteAndMaxReadRate(ofSeconds(1.0));
        final Filter filter = new Filter("'loc://x' < 5 ? 1 : 2", this);
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
        final Timestamp start = Timestamp.now();
        final Filter filter = new Filter("'ca://bogus_pv_name' * 2", this);
        filter.start();

        System.err.println("Waiting for timeout from bogus PV name...");

        // Default time out is 30 seconds
        // Should not get any updates while waiting for the connection...
        TimeUnit.SECONDS.sleep(30 / 2);
        assertThat(updates.get(), equalTo(0));
        
        // .. but then there should be an update
        synchronized (this)
        {
            while (updates.get() < 1)
                wait();
            System.err.println("Received value " + last_value);
        }
        System.err.println("Received " + updates.get() + " updates");
        assertThat(updates.get(), greaterThanOrEqualTo(1));

        final Timestamp end = Timestamp.now();
        final TimeDuration duration = end.durationFrom(start);
        System.err.println("Timeout was " + duration);
        assertThat(duration.toSeconds(), greaterThanOrEqualTo(30 * 0.8));
        
        filter.stop();
    }
}
