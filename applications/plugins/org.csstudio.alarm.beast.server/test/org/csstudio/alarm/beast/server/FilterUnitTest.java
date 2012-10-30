/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.csstudio.alarm.beast.server.Matchers.atLeast;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.util.time.TimeDuration.ofSeconds;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.loc.LocalDataSource;
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
        PVManager.setDefaultDataSource(new LocalDataSource());
        
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.ALL);
        
        Logger.getLogger("org.epics.pvmanager").setLevel(Level.SEVERE);
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
        final PV<Object, Object> x = PVManager.readAndWrite(channel("x")).synchWriteAndMaxReadRate(ofSeconds(1.0));
        final PV<Object, Object> y = PVManager.readAndWrite(channel("y")).synchWriteAndMaxReadRate(ofSeconds(1.0));
        x.write(1.0);
        y.write(2.0);
        
        final Filter filter = new Filter("x + y", this);
        filter.start();

        // Update 1?: May get initial update where vars are undefined, so filter computes NaN
        
        // Update 1 or 2: 1+2=3
        synchronized (this)
        {
            while (last_value != 3.0)
                wait();
        }
        assertThat(updates.get(), atLeast(0));
        
        // Update 2 or 3
        x.write(4.0);
        // Update 3 or 4
        y.write(6.0);

        synchronized (this)
        {
            while (last_value != 10.0)
                wait();
        }
        System.err.println("Received " + updates.get() + " updates");
        assertThat(updates.get(), atLeast(3));
        
        filter.stop();
    }
}
