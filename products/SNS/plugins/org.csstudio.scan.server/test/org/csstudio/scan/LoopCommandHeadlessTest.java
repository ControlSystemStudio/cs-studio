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

import org.csstudio.data.values.ValueUtil;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.commandimpl.LoopCommandImpl;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.internal.Scan;
import org.csstudio.scan.server.internal.ScanContextImpl;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the {@link LoopCommand}
 *  and its work unit computation
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoopCommandHeadlessTest
{
    @Test(timeout=5000)
    public void testLoopCommand() throws Throwable
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("counter", "loc://counter");
        final Device counter = devices.getDevice("counter");
        devices.startDevices();

        counter.write(2.0);
        assertEquals(2.0, ValueUtil.getDouble(counter.read()), 0.1);

        final ScanContext context = new ScanContextImpl(devices);
        final ScanCommandImpl<?> loop = new LoopCommandImpl(
                new LoopCommand("counter", 1.0, 5.0, 1.0, 0.0,
                    new LogCommand("counter")));
        System.out.println(loop);

        context.execute(loop);
        assertEquals(5.0, ValueUtil.getDouble(counter.read()), 0.1);

        devices.stopDevices();
    }


    @Test(timeout=5000)
    public void testLoopCommandWorkunits() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("counter", "loc://counter");
        devices.addPVDevice("other", "loc://other");

        final ScanContextImpl context = new ScanContextImpl(devices);

        final LoopCommandImpl loop1 = new LoopCommandImpl(new LoopCommand("counter", 1.0, 5.0, 1.0, 0.0));
        assertEquals(5, loop1.getWorkUnits());

        final LoopCommandImpl loop2 = new LoopCommandImpl(
            new LoopCommand("counter", 1.0, 5.0, 1.0, 0.0,
                new SetCommand("other", 1.0),
                new SetCommand("other", 2.0)));
        assertEquals(10, loop2.getWorkUnits());

        final Scan scan = new Scan("Loop Test", loop1, loop2);
        assertEquals(0, context.getWorkPerformed());
        scan.execute(context);

        // 1 WaitForDevicesCommand + loop1 + loop2
        assertEquals(16, context.getWorkPerformed());
    }


    @Test(timeout=5000)
    public void testOtherLoops() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("counter", "loc://counter");
        final Device counter = devices.getDevice("counter");
        devices.startDevices();

        final ScanContext context = new ScanContextImpl(devices);

        // Downward loop 5, 4, 3, 2, 1
        counter.write(4.0);
        assertEquals(4.0, ValueUtil.getDouble(counter.read()), 0.1);
        LoopCommandImpl loop = new LoopCommandImpl(
            new LoopCommand("counter", 5.0, 1.0, -1.0, 0.0,
                new LogCommand("counter")));
        System.out.println(loop);
        context.execute(loop);
        assertEquals(1.0, ValueUtil.getDouble(counter.read()), 0.1);

        // Step 2: 1, 3, 5, 7, 9
        loop = new LoopCommandImpl(
            new LoopCommand("counter", 1.0, 10.0, 2.0, 0.0,
                new LogCommand("counter")));
        System.out.println(loop);
        context.execute(loop);
        assertEquals(9.0, ValueUtil.getDouble(counter.read()), 0.1);

        // Down 3: 8, 5, 2
        loop = new LoopCommandImpl(
            new LoopCommand("counter", 8.0, 0.0, -3.0, 0.0,
                new LogCommand("counter")));
        System.out.println(loop);
        context.execute(loop);
        assertEquals(2.0, ValueUtil.getDouble(counter.read()), 0.1);

        devices.stopDevices();
    }


    @Test(timeout=5000)
    public void testReversingLoop() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("counter", "loc://counter");
        final Device counter = devices.getDevice("counter");
        devices.startDevices();

        // 1 .. 5, but stepping down -> Creates 'reversing' loop
        LoopCommandImpl loop = new LoopCommandImpl(
                new LoopCommand("counter", 1.0, 5.0, -1.0, 0.0,
                    new LogCommand("counter")));
        System.out.println(loop);

        final ScanContext context = new ScanContextImpl(devices);

        // Downward loop 5, 4, 3, 2, 1
        counter.write(4.0);
        assertEquals(4.0, ValueUtil.getDouble(counter.read()), 0.1);
        context.execute(loop);
        assertEquals(1.0, ValueUtil.getDouble(counter.read()), 0.1);

        // On the next iteration, the loop toggles to an upward 1...5
        context.execute(loop);
        assertEquals(5.0, ValueUtil.getDouble(counter.read()), 0.1);

        // And then again down 5...1
        context.execute(loop);
        assertEquals(1.0, ValueUtil.getDouble(counter.read()), 0.1);

        devices.stopDevices();
    }
}
