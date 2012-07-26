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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.commandimpl.LogCommandImpl;
import org.csstudio.scan.commandimpl.LoopCommandImpl;
import org.csstudio.scan.commandimpl.SetCommandImpl;
import org.csstudio.scan.commandimpl.WaitCommandImpl;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.server.internal.ExecutableScan;
import org.csstudio.scan.server.internal.LoggedScan;
import org.csstudio.scan.server.internal.ScanEngine;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link ScanEngine}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanEngineHeadlessTest
{
    private void waitForState(final ExecutableScan scan, final ScanState state) throws InterruptedException
    {
        do
        {
            Thread.sleep(200);
        }
        while (scan.getScanInfo().getState() != state);
    }


    private DeviceContext getDemoDevices() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice(new DeviceInfo("motor_x", "xpos", true, true));
        devices.addPVDevice(new DeviceInfo("motor_y", "ypos", true, true));
        return devices;
    }

    /** @param engine
     *  @return <code>true</code> if all scans on engine are 'done'
     */
    private static boolean isIdle(final ScanEngine engine)
    {
        for (LoggedScan scan : engine.getScans())
            if (!scan.getScanState().isDone())
                return false;
        return true;
    }

    /** Test scans with pause/resume (15 secs) */
    @Test(timeout=30000)
    public void testScanEngine() throws Exception
    {
        final DeviceContext devices = getDemoDevices();

        final ExecutableScan scan_x = new ExecutableScan("Scan Motor X",
            devices,
            new LoopCommandImpl(
                new LoopCommand("xpos", 1.0, 5.0, 1.0,
                    new DelayCommand(1.0),
                    new DelayCommand(1.0),
                    new LogCommand("xpos")
                )
            )
        );

        final ExecutableScan scan_y = new ExecutableScan("Scan Motor Y",
                devices,
            new LoopCommandImpl(
                new LoopCommand("ypos", 1.0, 5.0, 1.0,
                    new DelayCommand(1.0),
                    new LogCommand("ypos"))));

        final ScanEngine engine = new ScanEngine();
        engine.start(false);

        engine.submit(scan_x);
        engine.submit(scan_y);

        // List scans and their state
        List<ExecutableScan> scans = engine.getExecutableScans();
        assertEquals(2, scans.size());

        // Second scan should be idle.
        assertEquals(ScanState.Idle, scans.get(1).getScanInfo().getState());
        assertEquals("Scan Motor Y", scans.get(1).getScanInfo().getName());

        // Wait for 1st scan to start
        do
        {
            scans = engine.getExecutableScans();
            System.out.println(scans.get(0).getScanInfo());
            assertSame(scan_x, scans.get(0));
            assertFalse(isIdle(engine));
            Thread.sleep(200);
        }
        while (scan_x.getScanInfo().getState() != ScanState.Running);

        // Pause it
        scan_x.pause();

        for (int i=0; i<4; ++i)
        {
            System.out.println(scan_x.getScanInfo());
            assertFalse(isIdle(engine));
            assertEquals(ScanState.Paused, scan_x.getScanInfo().getState());
            Thread.sleep(200);
        }

        System.out.println("Monitoring process of this:");
        XMLCommandWriter.write(System.out, scan_x.getScanCommands());

        // Resume, wait for 1st scan to finish
        scan_x.resume();
        do
        {
            scans = engine.getExecutableScans();
            final ScanInfo info = scans.get(0).getScanInfo();
            System.out.println(info + ", command " + info.getCurrentCommand() + " @ " + info.getCurrentAddress());

            // Address of the 'delay' commands should be 1 or 2
            if (info.getCurrentCommand().contains("Delay"))
                assertTrue(info.getCurrentAddress() == 1  ||
                           info.getCurrentAddress() == 2);
            assertSame(scan_x, scans.get(0));
            assertFalse(isIdle(engine));
            Thread.sleep(200);
        }
        while (scan_x.getScanInfo().getState() != ScanState.Finished);
        System.out.println(scan_x.getScanInfo());

        // Wait for 2nd scan to finish
        do
        {
            scans = engine.getExecutableScans();
            System.out.println(scans.get(1).getScanInfo());
            assertSame(scan_y, scans.get(1));
            Thread.sleep(200);
        }
        while (! isIdle(engine));

        System.out.println(scan_x.getScanInfo());
        System.out.println(scan_y.getScanInfo());

        engine.stop();
        scans = engine.getExecutableScans();
        assertEquals(0, scans.size());
    }

    @Test(timeout=10000)
    public void testErrors() throws Exception
    {
        final DeviceContext devices = getDemoDevices();

        final ExecutableScan scan = new ExecutableScan("Scan Motor X",
            devices,
            new LoopCommandImpl(
                new LoopCommand("xpos", 1.0, 5.0, 1.0,
                    new LogCommand("xpos")
                )
            )
        );

        final ScanEngine engine = new ScanEngine();
        engine.start(false);
        engine.submit(scan);

        waitForState(scan, ScanState.Finished);

        // Submit same scan again, which causes error
        try
        {
            engine.submit(scan);
            fail("Submit scan twice?");
        }
        catch (IllegalStateException ex)
        {
            System.out.println("Received expected error: " + ex.getMessage());
            assertTrue(ex.getMessage().contains("submitted"));
        }

        engine.stop();
    }

    @Test(timeout=10000)
    public void testEngineStop() throws Exception
    {
        final DeviceContext devices = getDemoDevices();

        // Scan that will hang
        final ExecutableScan scan = new ExecutableScan("Scan Motor X",
                devices,
                new SetCommandImpl(new SetCommand("xpos", 2.0)),
                new WaitCommandImpl(new WaitCommand("xpos", Comparison.EQUALS, 2.0, 0.1, 0.0)),
                new LogCommandImpl(new LogCommand("xpos")),
                new WaitCommandImpl(new WaitCommand("xpos", Comparison.EQUALS, 10.0, 0.1, 0.0))
        );

        final ScanEngine engine = new ScanEngine();
        engine.start(false);
        engine.submit(scan);

        // Wait for scan to start
        waitForState(scan, ScanState.Running);
        // Allow it to get hung up
        Thread.sleep(1000);

        // Stop engine
        engine.stop();
        // Thread should not continue...
        Thread.sleep(1000);

        final List<ExecutableScan> scans = engine.getExecutableScans();
        assertEquals(0, scans.size());

        System.out.println(scan.getScanInfo());
        assertEquals(ScanState.Aborted, scan.getScanInfo().getState());
    }

    @Test(timeout=10000)
    public void testAbort() throws Exception
    {
        final DeviceContext devices = getDemoDevices();

        // Scan that will hang
        final ExecutableScan scan = new ExecutableScan("Scan Motor X",
                devices,
                new SetCommandImpl(new SetCommand("xpos", 2.0)),
                new WaitCommandImpl(new WaitCommand("xpos", Comparison.EQUALS, 2.0, 0.1, 0.0)),
                new LogCommandImpl(new LogCommand("xpos")),
                new WaitCommandImpl(new WaitCommand("xpos", Comparison.EQUALS, 10.0, 0.1, 0.0))
        );

        final ScanEngine engine = new ScanEngine();
        engine.start(false);
        engine.submit(scan);

        // Wait for scan to start
        waitForState(scan, ScanState.Running);
        // Allow it to do a little work
        Thread.sleep(1000);

        scan.abort();

        // Thread should not continue...
        Thread.sleep(1000);

        System.out.println(scan.getScanInfo());
        assertEquals(ScanState.Aborted, scan.getScanInfo().getState());

        engine.stop();
    }
}
