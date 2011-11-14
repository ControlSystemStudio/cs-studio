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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.scan.command.DelayCommandImpl;
import org.csstudio.scan.command.LogCommandImpl;
import org.csstudio.scan.command.LoopCommandImpl;
import org.csstudio.scan.command.SetCommandImpl;
import org.csstudio.scan.command.WaitForValueCommandImpl;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanEngine;
import org.csstudio.scan.server.ScanState;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link ScanEngine}
 *  @author Kay Kasemir
 */
public class ScanEngineTest
{
    private void waitForState(final Scan scan, final ScanState state) throws InterruptedException
    {
        do
        {
            Thread.sleep(200);
        }
        while (scan.getScanInfo().getState() != state);
    }

    /** Test scans with pause/resume (15 secs) */
    @Test(timeout=30000)
    public void testScanEngine() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("xpos", "motor_x");
        devices.addPVDevice("ypos", "motor_y");

        final Scan scan_x = new Scan("Scan Motor X",
                new LoopCommandImpl("xpos", 1.0, 5.0, 1.0,
                    new DelayCommandImpl(1.0),
                    new LogCommandImpl("xpos")
                )
        );

        final Scan scan_y = new Scan("Scan Motor Y",
                new LoopCommandImpl("ypos", 1.0, 5.0, 1.0,
                    new DelayCommandImpl(1.0),
                    new LogCommandImpl("ypos")));

        final ScanEngine engine = new ScanEngine();
        engine.start();

        engine.submit(devices, scan_x);
        engine.submit(devices, scan_y);

        // List scans and their state
        List<Scan> scans = engine.getScans();
        assertEquals(2, scans.size());

        // Second scan should be idle.
        assertEquals(ScanState.Idle, scans.get(1).getScanInfo().getState());
        assertEquals("Scan Motor Y", scans.get(1).getScanInfo().getName());

        // Wait for 1st scan to start
        do
        {
            scans = engine.getScans();
            System.out.println(scans.get(0).getScanInfo());
            assertSame(scan_x, scans.get(0));
            assertFalse(engine.isIdle());
            Thread.sleep(200);
        }
        while (scan_x.getScanInfo().getState() != ScanState.Running);

        // Pause it
        scan_x.pause();

        for (int i=0; i<4; ++i)
        {
            System.out.println(scan_x.getScanInfo());
            assertFalse(engine.isIdle());
            assertEquals(ScanState.Paused, scan_x.getScanInfo().getState());
            Thread.sleep(200);
        }

        // Resume, wait for 1st scan to finish
        scan_x.resume();
        do
        {
            scans = engine.getScans();
            System.out.println(scans.get(0).getScanInfo());
            assertSame(scan_x, scans.get(0));
            assertFalse(engine.isIdle());
            Thread.sleep(200);
        }
        while (scan_x.getScanInfo().getState() != ScanState.Finished);
        System.out.println(scan_x.getScanInfo());

        // Wait for 2nd scan to finish
        do
        {
            scans = engine.getScans();
            System.out.println(scans.get(1).getScanInfo());
            assertSame(scan_y, scans.get(1));
            Thread.sleep(200);
        }
        while (! engine.isIdle());

        System.out.println(scan_x.getScanInfo());
        System.out.println(scan_y.getScanInfo());

        engine.stop();
        scans = engine.getScans();
        assertEquals(0, scans.size());
    }

    @Test(timeout=10000)
    public void testErrors() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("xpos", "motor_x");

        final Scan scan = new Scan("Scan Motor X",
                new LoopCommandImpl("xpos", 1.0, 5.0, 1.0,
                    new LogCommandImpl("xpos")
                )
        );

        final ScanEngine engine = new ScanEngine();
        engine.start();
        engine.submit(devices, scan);

        waitForState(scan, ScanState.Finished);

        // Submit same scan again, which causes error
        engine.submit(devices, scan);

        // Wait for failure...
        waitForState(scan, ScanState.Failed);

        final Throwable error = scan.getScanInfo().getError();
        assertNotNull(error);
        System.out.println("Received expected error: " + error.getMessage());
        assertTrue(error.getMessage().toLowerCase().contains("cannot"));
        assertTrue(error.getMessage().toLowerCase().contains("finished"));

        engine.stop();
    }

    @Test(timeout=10000)
    public void testEngineStop() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("xpos", "motor_x");

        // Scan that will hang
        final Scan scan = new Scan("Scan Motor X",
                new SetCommandImpl("xpos", 2.0),
                new WaitForValueCommandImpl("xpos", 2.0, 0.1),
                new LogCommandImpl("xpos"),
                new WaitForValueCommandImpl("xpos", 10.0, 0.1)
        );

        final ScanEngine engine = new ScanEngine();
        engine.start();
        engine.submit(devices, scan);

        // Wait for scan to start
        waitForState(scan, ScanState.Running);
        // Allow it to get hung up
        Thread.sleep(1000);

        // Stop engine
        engine.stop();
        // Thread should not continue...
        Thread.sleep(1000);

        final List<Scan> scans = engine.getScans();
        assertEquals(0, scans.size());

        System.out.println(scan.getScanInfo());
        assertEquals(ScanState.Aborted, scan.getScanInfo().getState());
    }

    @Test(timeout=10000)
    public void testAbort() throws Exception
    {
        final DeviceContext devices = new DeviceContext();
        devices.addPVDevice("xpos", "motor_x");

        // Scan that will hang
        final Scan scan = new Scan("Scan Motor X",
                new SetCommandImpl("xpos", 2.0),
                new WaitForValueCommandImpl("xpos", 2.0, 0.1),
                new LogCommandImpl("xpos"),
                new WaitForValueCommandImpl("xpos", 10.0, 0.1)
        );

        final ScanEngine engine = new ScanEngine();
        engine.start();
        engine.submit(devices, scan);

        // Wait for scan to start
        waitForState(scan, ScanState.Running);
        // Allow it to do a little work
        Thread.sleep(1000);

        engine.abortScan(scan);

        // Thread should not continue...
        Thread.sleep(1000);

        System.out.println(scan.getScanInfo());
        assertEquals(ScanState.Aborted, scan.getScanInfo().getState());

        engine.stop();
    }
}
