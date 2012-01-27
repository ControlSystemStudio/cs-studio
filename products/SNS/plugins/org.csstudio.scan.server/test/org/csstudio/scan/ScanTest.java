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
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.server.internal.Scan;
import org.csstudio.scan.server.internal.ScanContextImpl;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link Scan}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanTest
{
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
        devices.addPVDevice("xpos", "motor_x");
        devices.addPVDevice("ypos", "motor_y");
        devices.addPVDevice("setpoint", "setpoint");
        devices.addPVDevice("readback", "readback");

        // Setup context
        final ScanContextImpl context = new ScanContextImpl(devices);

        // Configure a scan
        final LoopCommand command = new LoopCommand("xpos", 1.0, 5.0, 1.0,
                new LoopCommand("ypos", 1.0, 5.0, 1.0,
                        new SetCommand("setpoint", 0),
                        new WaitCommand("readback", Comparison.EQUALS, 0, 0.2, 0.0),
                        new SetCommand("setpoint", 0.5),
                        new WaitCommand("readback", Comparison.EQUALS, 0.5, 0.1, 0.0),
                        new LogCommand("xpos", "ypos", "readback")
                )
            );

        final Scan scan = new Scan("Scan Test", new LoopCommandImpl(command));
        final List<ScanCommand> commands = scan.getScanCommands();
        assertEquals(1, commands.size());
        assertSame(command, commands.get(0));

        // Check Idle state
        ScanInfo info = scan.getScanInfo();
        assertEquals(ScanState.Idle, info.getState());
        assertEquals(0, info.getPercentage());
        // Execute the scan
        scan.execute(context);
        // Check Finish state
        info = scan.getScanInfo();
        assertEquals(ScanState.Finished, info.getState());
        assertEquals(100, info.getPercentage());

        // Dump data
        new SpreadsheetScanDataIterator(context.getDataLogger().getScanData()).dump(System.out);
    }
}
