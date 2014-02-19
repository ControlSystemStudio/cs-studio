/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVFactory;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.jca.JCA_PV;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.epics.vtype.VType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** JUnit tests
 * 
 *  <p>These require a softIoc with
 *  org.csstudio.scan/examples/*.db 
 *  @author Kay Kasemir
 */
public class JCAPVTest implements PVListener
{
    final private CountDownLatch updates = new CountDownLatch(1);
    
    @Before
    public void setup()
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
        {
            handler.setLevel(Level.FINE);
            handler.setFormatter(new SimpleFormatter());
        }
        
        final PVFactory factory = new JCA_PVFactory();
        PVPool.addPVFactory(factory);
    }

    @Test
    public void testPool() throws Exception
    {
        // Create PV
        final PV pv1 = PVPool.getPV("motor_x");
        // Reference the same PV
        final PV pv2 = PVPool.getPV("motor_x");
        final PV pv3 = PVPool.getPV("motor_x");
        assertThat(pv1, sameInstance(pv2));
        assertThat(pv2, sameInstance(pv3));
        // Release all
        PVPool.releasePV(pv3);
        PVPool.releasePV(pv2);
        PVPool.releasePV(pv1);
        
        // Create _new_ PV
        final PV pv4 = PVPool.getPV("motor_x");
        assertThat(pv1, not(sameInstance(pv4)));
        PVPool.releasePV(pv4);
    }

    @Test
    public void testPrefix() throws Exception
    {
        // Create PV
        final PV pv1 = PVPool.getPV("ca://motor_x");
        assertThat(pv1.getName(), equalTo("ca://motor_x"));
        
        // Reference the same PV
        final PV pv2 = PVPool.getPV("ca://motor_x");
        assertThat(pv1, sameInstance(pv2));
        PVPool.releasePV(pv2);
        
        // Different name because of prefix
        final PV pv3 = PVPool.getPV("motor_x");
        assertThat(pv3.getName(), equalTo("motor_x"));
        assertThat(pv1, not(sameInstance(pv3)));
        PVPool.releasePV(pv3);
        
        PVPool.releasePV(pv1);
    }
    
    @Test(timeout=5000)
    public void testBasicRead() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testPlainRead() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x.PREC");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testShortArrayRead() throws Exception
    {
        final PV pv = PVPool.getPV("charge_hist");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test//(timeout=5000)
    public void testEnumRead() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x.SCAN");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testReadWithCallback() throws Exception
    {
        final JCA_PV pv = (JCA_PV) PVPool.getPV("motor_x");
        // A read _right away_ will fail because the channel is not connected
        try
        {
            pv.asyncRead();
        }
        catch (Exception ex)
        {
            System.out.println("Waiting for channel to connect");
        }
        while (pv.read() == null)
            Thread.sleep(10);
        System.out.println("Connected");
        
        for (int i=0; i<10; ++i)
        {
            final Future<VType> value = pv.asyncRead();
            if (value.isDone())
                System.out.println("Done right away??");
            System.out.println(value.get(1, TimeUnit.SECONDS));
            assertThat(value.isDone(), equalTo(true));
        }
        
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }

    @Test(timeout=5000)
    public void testRTYP() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x.RTYP");
        pv.addListener(this);
        updates.await();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }
    
    @Test(timeout=5000)
    public void testBasicWrite() throws Exception
    {
        final PV pv = PVPool.getPV("motor_x");
        pv.addListener(this);
        updates.await();
        
        pv.write(4.0);
        
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }
    
    @Test(timeout=5000)
    public void testAsyncWrite() throws Exception
    {
        final PV pv = PVPool.getPV("callback_test");
        pv.addListener(this);
        updates.await();

        long start = System.currentTimeMillis();
        final Future<?> result = pv.asyncWrite(6.0);
        result.get();
        long end = System.currentTimeMillis();
        double seconds = (end - start) / 1000.0;
        System.out.println("Write-callback took " + seconds + " seconds");
        assertTrue(Math.abs(seconds - 4.0) < 1.0);
        
        pv.removeListener(this);
        PVPool.releasePV(pv);
        System.out.println("Done.");
    }
    
    @Override
    public void permissionsChanged(PV pv, boolean readonly)
    {
        System.out.println("Permissions");        
    }

    @Override
    public void valueChanged(PV pv, VType value)
    {
        System.out.println("Update: " + value);
        updates.countDown();
    }

    @Override
    public void disconnected(PV pv)
    {
        System.out.println("Disconnected");
    }
}
