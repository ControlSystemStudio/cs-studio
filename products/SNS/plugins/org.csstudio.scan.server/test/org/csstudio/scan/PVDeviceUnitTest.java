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

import static org.junit.Assert.*;

import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.PVDevice;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.jca.JCADataSource;
import org.epics.pvmanager.sim.SimulationDataSource;
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
        final CompositeDataSource sources = new CompositeDataSource();
        sources.putDataSource("ca", new JCADataSource());
        sources.putDataSource("sim", new SimulationDataSource());
        sources.setDefaultDataSource("ca");
        PVManager.setDefaultDataSource(sources);
    }
    
    /** Check if device can be created, sends updates */
    @Test(timeout=5000)
    public void testPVDevice() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("sim://sine", "demo"));
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
    @Test(timeout=6000)
    public void testWrite() throws Exception
    {
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
            Thread.sleep(4000L + 1000L);
            // Should still only have the one update
            awaitUpdates(1);
        }
        finally
        {
            device.stop();
        }
    }

    /** Test write with callback */
    @Test(timeout=6000)
    public void testPutCallback() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("ca://callback_test" + PVDevice.PUT_CALLBACK_ANNOTATION, "callback_test"));
        device.start();
        try
        {
            device.addListener(this);
            // Wait for initial value
            awaitUpdates(1);
            
            // This should take about 4 seconds because it waits for the callback
            final long start = System.currentTimeMillis();
            device.write(Double.valueOf(1.0));
            final long end = System.currentTimeMillis();
            final double seconds = (end - start) / 1000.0;
            System.out.format("Write finished in %.2f seconds\n", seconds);
            assertTrue(Math.abs(4.0 - seconds) < 1.0);
        }
        finally
        {
            device.stop();
        }
    }
}
