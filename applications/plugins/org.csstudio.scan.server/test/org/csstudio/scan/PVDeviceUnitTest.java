/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertTrue;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.PVDevice;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.csstudio.vtype.pv.local.LocalPVFactory;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.VType;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the {@link PVDevice}
 * 
 *  <p>Requires
 *  <code>softIoc -s -d org.csstudio.scan/examples/simulation.db</code>
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVDeviceUnitTest implements DeviceListener
{
    /** Updates with non-null, connected value */
    private int updates = 0;
    
    private synchronized void awaitUpdates(final int desired_updates) throws InterruptedException
    {
        while (updates != desired_updates)
            wait();
    }
    
    @Before
    public void setup()
    {
        final Logger root = Logger.getLogger("");
        root.setLevel(Level.FINE);
        for (Handler handler : root.getHandlers())
            handler.setLevel(Level.FINE);
        PVPool.addPVFactory(new LocalPVFactory());
        PVPool.addPVFactory(new JCA_PVFactory());
    }

    /** Read with listener */
    @Test(timeout=5000)
    public void testRead() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("motor_x", "demo"));
        device.start();
        try
        {
            device.addListener(this);
            // Wait for initial value
            awaitUpdates(1);
        }
        finally
        {
            device.stop();
        }
    }
    
    /** Read with listener for long text */
    @Test(timeout=5000)
    public void testLongStringRead() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("text", "demo"));
        device.start();
        try
        {
            device.addListener(this);
            // Wait for initial value
            awaitUpdates(1);
        }
        finally
        {
            device.stop();
        }
    }
    
    /** Read with get-callback */
    @Test(timeout=5000)
    public void testReadCallback() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("motor_x.SCAN", "demo"));
        device.start();
        // Active read requires that device is connected
        while (! device.isReady())
            Thread.sleep(100);
        try
        {
            final VType value = device.read(TimeDuration.ofSeconds(5.0));
            System.out.println("Get-callback value: " + value);
            System.out.println("Last value        : " + device.read());   
        }
        finally
        {
            device.stop();
        }
    }
    
    // DeviceListener
    @Override
    public void deviceChanged(final Device device)
    {
        try
        {
            final VType value = device.read();
			System.out.println(device + " = " + value);
			if (device.isReady())
			    synchronized (this)
                {
                    ++updates;
                    notifyAll();
                }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /** Test write w/o callback */
    @Test(timeout=10000)
    public void testWrite() throws Exception
    {
        System.out.println("\nTest Writing");
        final PVDevice device = new PVDevice(new DeviceInfo("ca://callback_test", "callback_test"));
        device.start();
        try
        {
            device.addListener(this);
            // Wait for initial value
            awaitUpdates(1);
            final long start = System.currentTimeMillis();
            device.write(Double.valueOf(1.0));
            final long end = System.currentTimeMillis();
            final double seconds = (end - start) / 1000.0;
            System.out.format("Write finished in %.2f seconds\n", seconds);
            // There's no update from the SEQ record, no write confirmation,
            // and the write returns right away.
            assertTrue(seconds < 0.5);
            // But we happen to know that the record processing takes ~4 seconds,
            // and a follow-up write-callback cannot start until these 4 seconds pass,
            // so wait at least that long, plus some head room.
            Thread.sleep(4000L + 2000L);
            // Should still only have the one update
            awaitUpdates(1);
        }
        finally
        {
            device.stop();
        }
    }

    /** Test write with callback */
    @Test(timeout=10000)
    public void testPutCallback() throws Exception
    {
        System.out.println("\nTest Writing with callback");

        final PVDevice device = new PVDevice(new DeviceInfo("ca://callback_test", "callback_test"));
        device.start();
        device.addListener(this);
        // Wait for initial value
        awaitUpdates(1);

        // This should take about 4 seconds because it waits for the callback
        final long start = System.currentTimeMillis();
        device.write(Double.valueOf(1.0), TimeDuration.ofSeconds(10.0));
        final long end = System.currentTimeMillis();
        final double seconds = (end - start) / 1000.0;
        System.out.format("Write finished in %.2f seconds\n", seconds);
        // assertTrue(Math.abs(4.0 - seconds) < 1.0);
    }
    
    /** Test write with callback to 'local' device */
    @Test(timeout=5000)
    public void testPutCallbackToLocal() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("loc://x(42)", "x"));
        device.start();
        try
        {
            device.addListener(this);
            // Wait for initial value
            awaitUpdates(1);
            
            // This should finish right away
            final long start = System.currentTimeMillis();
            device.write(Double.valueOf(1.0), TimeDuration.ofSeconds(2.0));
            final long end = System.currentTimeMillis();
            final double seconds = (end - start) / 1000.0;
            System.out.format("Write finished in %.2f seconds\n", seconds);
            assertTrue(seconds < 1.0);
        }
        finally
        {
            device.stop();
        }
    }
}
