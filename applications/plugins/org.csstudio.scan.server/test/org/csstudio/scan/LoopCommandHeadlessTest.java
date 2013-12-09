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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.csstudio.utility.test.HamcrestMatchers.*;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.commandimpl.LoopCommandImpl;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.internal.ExecutableScan;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the {@link LoopCommand}
 *  and its work unit computation
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoopCommandHeadlessTest
{
    private DeviceContext getDemoContext() throws Exception
    {
        final DeviceContext context = new DeviceContext();
        context.addPVDevice(new DeviceInfo("loc://counter(0)", "counter"));
        context.addPVDevice(new DeviceInfo("loc://other(0)", "other"));
        context.addPVDevice(new DeviceInfo("loc://other2(0)", "other2"));
        return context;
    }

    private static void waitForValue(final Device device, final double value) throws Exception
    {
    	int tens = 0;
    	while (device.readDouble() != value)
    	{
    		Thread.sleep(100);
    		++tens;
    		if (tens > 20)
    			throw new TimeoutException();
    	}
    }
    
    @Test(timeout=5000)
    public void testLoopCommand() throws Throwable
    {
        final DeviceContext devices = getDemoContext();
        final Device counter = devices.getDevice("counter");
        devices.startDevices();
        try
        {
            counter.write(2.0);
            waitForValue(counter, 2.0);

            final ScanCommandImpl<?> loop = new LoopCommandImpl(
                    new LoopCommand("counter", 1.0, 5.0, 1.0,
                        new LogCommand("counter")));
            System.out.println(loop);

            // Note that the scan doesn't actually hold any commands.
            // If we executed the loop within the scan,
            // it would start and stop the devices,
            // and then we could no longer read the device
            // once the scan is done.
            // Instead, we start and stop the devices outside
            // of the scan and only use the scan to execute
            // an individual command.
            final ExecutableScan scan = new ExecutableScan("test", devices);
            scan.execute(loop);
            assertEquals(5.0, counter.readDouble(), 0.1);
        }
        finally
        {
            devices.stopDevices();
        }
    }


    @Test(timeout=5000)
    public void testLoopCommandWorkunits() throws Exception
    {
        final DeviceContext devices = getDemoContext();

        final LoopCommandImpl loop1 = new LoopCommandImpl(
                new LoopCommand("counter", 1.0, 5.0, 1.0));
        assertThat(loop1.getWorkUnits(), equalTo(5l));

        final LoopCommandImpl loop2 = new LoopCommandImpl(
            new LoopCommand("counter", 1.0, 5.0, 1.0,
                new SetCommand("other", 1.0),
                new SetCommand("other2", 2.0)));
        assertThat(loop2.getWorkUnits(), equalTo(10l));

        final ScanContext dummy = new ExecutableScan("dummy", devices);
        final String[] names = loop2.getDeviceNames(dummy.getMacros());
        assertEquals(3, names.length);
        Arrays.sort(names);
        assertThat(Arrays.binarySearch(names, "counter"), greaterThanOrEqualTo(0));
        assertThat(Arrays.binarySearch(names, "other"), greaterThanOrEqualTo(0));
        assertThat(Arrays.binarySearch(names, "other2"), greaterThanOrEqualTo(0));

        final ExecutableScan scan = new ExecutableScan("Loop Test", devices, loop1, loop2);
        assertEquals(0, scan.getScanInfo().getPerformedWorkUnits());
        scan.call();

        // loop1 + loop2
        assertThat(scan.getScanInfo().getPerformedWorkUnits(), equalTo(15L));
    }


    @Test(timeout=5000)
    public void testOtherLoops() throws Exception
    {
        final DeviceContext devices = getDemoContext();
        final Device counter = devices.getDevice("counter");
        devices.startDevices();
        try
        {
            // Downward loop 5, 4, 3, 2, 1
            counter.write(4.0);
            waitForValue(counter, 4.0);

            LoopCommandImpl loop = new LoopCommandImpl(
                new LoopCommand("counter", 5.0, 1.0, -1.0,
                    new LogCommand("counter")));
            System.out.println(loop);
            final ExecutableScan scan = new ExecutableScan("test", devices);
            scan.execute(loop);
            assertEquals(1.0, counter.readDouble(), 0.1);

            // Step 2: 1, 3, 5, 7, 9
            loop = new LoopCommandImpl(
                new LoopCommand("counter", 1.0, 10.0, 2.0,
                    new LogCommand("counter")));
            System.out.println(loop);
            scan.execute(loop);
            assertEquals(9.0, counter.readDouble(), 0.1);

            // Down 3: 8, 5, 2
            loop = new LoopCommandImpl(
                new LoopCommand("counter", 8.0, 0.0, -3.0,
                    new LogCommand("counter")));
            System.out.println(loop);
            scan.execute(loop);
            assertEquals(2.0, counter.readDouble(), 0.1);
        }
        finally
        {
            devices.stopDevices();
        }
    }


    @Test(timeout=5000)
    public void testReversingLoop() throws Exception
    {
        final DeviceContext devices = getDemoContext();
        final Device counter = devices.getDevice("counter");
        devices.startDevices();
        try
        {
            // 1 .. 5, but stepping down -> Creates 'reversing' loop
            LoopCommandImpl loop = new LoopCommandImpl(
                    new LoopCommand("counter", 1.0, 5.0, -1.0,
                        new LogCommand("counter")));
            System.out.println(loop);

            // Downward loop 5, 4, 3, 2, 1
            counter.write(4.0);
            waitForValue(counter, 4.0);
            assertEquals(4.0, counter.readDouble(), 0.1);
            final ExecutableScan scan = new ExecutableScan("test", devices, loop);
            scan.execute(loop);
            assertEquals(1.0, counter.readDouble(), 0.1);

            // On the next iteration, the loop toggles to an upward 1...5
            scan.execute(loop);
            assertEquals(5.0, counter.readDouble(), 0.1);

            // And then again down 5...1
            scan.execute(loop);
            assertEquals(1.0, counter.readDouble(), 0.1);
        }
        finally
        {
            devices.stopDevices();
        }
    }
}
