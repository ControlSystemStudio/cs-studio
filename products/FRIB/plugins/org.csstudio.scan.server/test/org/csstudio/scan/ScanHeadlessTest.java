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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.commandimpl.LoopCommandImpl;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.server.internal.ExecutableScan;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link ExecutableScan}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanHeadlessTest
{
    @Test(timeout=3000)
    public void testAutoDeviceAddition() throws Exception
    {
        // Empty device context
        final DeviceContext devices = new DeviceContext();

        // Scan that requires 2 devices
        final LoopCommand command = new LoopCommand("motor_x", 1.0, 5.0, 1.0,
                    new LogCommand("setpoint"));
        final ExecutableScan scan = new ExecutableScan("Scan Device Test", devices, new LoopCommandImpl(command));

        // Execute the scan
        assertEquals(0, devices.getDevices().length);
        scan.call();

        // Devices should have been added by scan as needed
        final Device[] device_infos = devices.getDevices();
        boolean got_motor_x = false;
        boolean got_setpoint = false;
        for (Device device : device_infos)
        {
            final String alias = device.getInfo().getAlias();
            System.out.println(alias);
            if ("motor_x".equals(alias))
                got_motor_x = true;
            if ("setpoint".equals(alias))
                got_setpoint = true;
        }
        assertEquals(2, device_infos.length);
        assertTrue(got_motor_x);
        assertTrue(got_setpoint);
    }


    /** Scan takes about 20 seconds */
    @Test(timeout=40000)
    public void testScanner() throws Exception
    {
        // Logger setup
        Logger.getLogger("").setLevel(Level.FINE);
        for (Handler handler : Logger.getLogger("").getHandlers())
            handler.setLevel(Level.ALL);

        // Configure devices for beamline
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice(new DeviceInfo("motor_x", "xpos", true, true));
        devices.addPVDevice(new DeviceInfo("motor_y", "ypos", true, true));
        devices.addPVDevice(new DeviceInfo("setpoint", "setpoint", true, true));
        devices.addPVDevice(new DeviceInfo("readback", "readback", true, true));

        // Configure a scan
        final LoopCommand command = new LoopCommand("xpos", 1.0, 5.0, 1.0,
                new LoopCommand("ypos", 1.0, 5.0, 1.0,
                        new SetCommand("setpoint", 0),
                        new WaitCommand("readback", Comparison.EQUALS, 0, 0.2, 0.0),
                        new SetCommand("setpoint", 1.0, "readback", true, 0.1, 0.0),
                        new LogCommand("xpos", "ypos", "readback")
                )
            );

        final ExecutableScan scan = new ExecutableScan("Scan Test", devices, new LoopCommandImpl(command));
        final List<ScanCommand> commands = scan.getScanCommands();
        assertEquals(1, commands.size());
        assertSame(command, commands.get(0));

        // Check addressing and updating of command property
        final SetCommand set = (SetCommand)scan.getCommandByAddress(4);
        assertEquals(1.0, (Double)set.getValue(), 0.1);
        scan.updateScanProperty(4, "value", 0.5);
        assertEquals(0.5, (Double)set.getValue(), 0.1);

        // Check Idle state
        ScanInfo info = scan.getScanInfo();
        assertEquals(ScanState.Idle, info.getState());
        assertEquals(0, info.getPercentage());
        // Execute the scan
        scan.call();
        // Check Finish state
        info = scan.getScanInfo();
        assertEquals(ScanState.Finished, info.getState());
        assertEquals(100, info.getPercentage());

        // Dump data
        final ScanData data = scan.getScanData();
        new ScanDataIterator(data).printTable(System.out);
        assertTrue(data.getSamples("xpos").size() > 1);
        assertTrue(data.getSamples("ypos").size() > 1);
    }
}
