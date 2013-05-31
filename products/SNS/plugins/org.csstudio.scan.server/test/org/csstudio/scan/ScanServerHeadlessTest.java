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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.condition.NumericValueCondition;
import org.csstudio.scan.condition.WaitForDevicesCondition;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.PVDevice;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanServerInfo;
import org.csstudio.scan.server.ScanState;
import org.csstudio.scan.server.internal.ScanServerImpl;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link ScanServer}
 *
 *  <p>Starts its own copy of the scan server,
 *  performs some operations from a different thread,
 *  then stops the scan server.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerHeadlessTest implements Runnable
{
    private ScanServer server = null;
    
    private volatile Throwable client_error = null;

    /** @return Demo scan sequence */
    private CommandSequence createCommands() throws Exception
    {
        final CommandSequence commands = new CommandSequence(
            new LogCommand("ypos", "readback"),
            new LoopCommand("xpos", 1, 5, 1,
                new LogCommand("xpos"))
        );
        return commands;
    }

    /** This 'Runnable' is executed by a demo client thread */
    @Override
    public void run()
    {
        System.out.println("--- Client starts ---");
        try
        {
            // Pre-set xpos
            final Device pv = new PVDevice(new DeviceInfo("motor_x", "xpos"));
            pv.start();
            new WaitForDevicesCondition(pv).await();
            pv.write(0.0);

            // Connect to scan server
            final ScanServerInfo server_info = server.getInfo();
			System.out.println("Thread using " + server_info);

            // Submit two scans, holding on to the second one
            final CommandSequence commands = createCommands();
            long id = server.submitScan("My Test 1", commands.getXML());
            // Poll 1st scan until it finishes
            while (true)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println("Poll: " + info + " @ " + info.getCurrentCommand());
                if (info.getState() == ScanState.Finished)
                    break;
                Thread.sleep(100);
            }

            // Submit second scan
            id = server.submitScan("My Test 2", commands.getXML());

            System.out.println("All Scans on server:");
            List<ScanInfo> infos = server.getScanInfos();
            for (ScanInfo info : infos)
                System.out.println(info);

            // Poll 2nd scan until it finishes
            while (true)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println("Poll: " + info);
                if (info.getState() == ScanState.Finished)
                    break;
                Thread.sleep(100);
            }

            // Show finished scans
            System.out.println("All Scans on server:");
            infos = server.getScanInfos();
            for (ScanInfo info : infos)
                System.out.println(info);
            
            // Check if scan that just finished shows up as such
            assertEquals(ScanState.Finished, server.getScanInfo(id).getState());
    
            // Also wait for scan to end by monitoring xpos (not really useful)
            System.out.println("Client waiting for PV to reach final value...");
            new NumericValueCondition(pv, Comparison.EQUALS, 5.0, 0.1, null).await();


            // Submit scan again, and pause it early on
            id = server.submitScan("My Test 3", commands.getXML());
            // Wait for thread to start
            while (true)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println("Started? " + info);
                if (info.getState() != ScanState.Idle)
                    break;
                Thread.sleep(100);
            }
            // Pause it
            server.pause(id);
            System.out.println("All Scans on server:");
            infos = server.getScanInfos();

            // Only that one scan should be paused
            for (ScanInfo info : infos)
            {
                System.out.println(info);
                if (info.getId() == id)
                    assertEquals(ScanState.Paused, info.getState());
                else
                    assertTrue(ScanState.Paused != info.getState());
            }

            // Resume 'all' and pause 'all' should again pause the running scan
            server.resume(-1);
            server.pause(-1);
            System.out.println("All Scans on server:");
            infos = server.getScanInfos();
            // Only that one scan should be paused
            for (ScanInfo info : infos)
            {
                System.out.println(info);
                if (info.getId() == id)
                    assertEquals(ScanState.Paused, info.getState());
                else
                    assertTrue(ScanState.Paused != info.getState());
            }

            // Should stay paused
            for (int i=0; i<3; ++i)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println("Poll: " + info);
                assertEquals(ScanState.Paused, info.getState());
                Thread.sleep(1000);
            }
            System.out.println("Resume, wait to finish");
            server.resume(id);
            // Poll scan until it finishes
            while (true)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println("Poll: " + info);
                if (info.getState() == ScanState.Finished)
                    break;
                Thread.sleep(100);
            }

            // Fetch data
            System.out.println("Logged data:");
            final ScanData data = server.getScanData(id);
            final List<String> devices = Arrays.asList(data.getDevices());
            System.out.println("Devices: " + devices);
            assertTrue(devices.contains("xpos"));
            assertTrue(devices.contains("ypos"));
            assertTrue(devices.contains("readback"));
            new ScanDataIterator(data).printTable(System.out);

            pv.stop();
        }
        catch (Exception ex)
        {
            client_error = ex;
        }
        System.out.println("--- Client ends ---");
    }

    /** JUnit test that runs server and client */
    @Test(timeout=20000)
    public void runScanServer() throws Exception
    {
        TestSettings.init();
        final ScanServerImpl server_impl = new ScanServerImpl();
        try
        {
            server_impl.start();
        }
        catch (Exception ex)
        {
        	if (ex.getMessage().startsWith("Cannot start"))
        	{
        		System.out.println("Server probably alreay running, skipping ScanServerHeadlessTest:");
        		ex.printStackTrace();
        		return;
        	}
        }
        System.out.println("Scan Server running...");
        server = server_impl;

        // In another thread, try the client
        final Thread client = new Thread(this, "Client");
        client.start();
        // Wait for client to finish
        client.join();

        if (client_error != null)
        {
            client_error.printStackTrace();
            fail();
        }

        System.out.println("Scan Server exiting.");
        server_impl.stop();
    }
}
