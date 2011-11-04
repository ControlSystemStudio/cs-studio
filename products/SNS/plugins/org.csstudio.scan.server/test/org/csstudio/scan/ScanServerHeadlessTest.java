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

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.condition.WaitForDevicesCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.PVDevice;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanServerImpl;
import org.csstudio.scan.server.ScanState;
import org.junit.Test;


/** [Headless] JUnit Plug-in test of the {@link ScanServer}
 *
 *  <p>Starts its own copy of the scan server,
 *  connects as client,
 *  performs some operations,
 *  then stops the scan server.
 *
 *  @author Kay Kasemir
 */
public class ScanServerHeadlessTest implements Runnable
{
    /** This 'Runnable' is executed by a demo client thread */
    @Override
    public void run()
    {
        System.out.println("--- Client starts ---");
        try
        {
            // Pre-set xpos
            final Device pv = new PVDevice("xpos", "motor_x");
            pv.start();
            new WaitForDevicesCondition(pv).await();
            pv.write(0.0);

            // Connect to scan server
            final Registry registry = LocateRegistry.getRegistry("localhost", ScanServer.RMI_PORT);
            final ScanServer server = (ScanServer) registry.lookup(ScanServer.RMI_SCAN_SERVER_NAME);
            System.out.println("Client connected to " + server.getInfo());
            assertTrue(server.getInfo().length() > 0);

            // Submit two scans, holding on to the second one
            final CommandSequence commands = createCommands();
            server.submitScan("My Test", commands.getCommands());
            long id = server.submitScan("My Test", commands.getCommands());

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
            {
                System.out.println(info);
                assertEquals(ScanState.Finished, info.getState());
            }

            // Also wait for scan to end by monitoring xpos (not really useful)
            System.out.println("Client waiting for PV to reach final value...");
            new DeviceValueCondition(pv, 5.0, 0.1).await();


            // Submit scan again, and pause it early on
            id = server.submitScan("My Test", commands.getCommands());
            server.pause(id);
            System.out.println("All Scans on server:");
            infos = server.getScanInfos();
            for (ScanInfo info : infos)
                System.out.println(info);
            for (int i=0; i<3; ++i)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println("Poll: " + info);
                assertEquals(ScanState.Paused, info.getState());
                Thread.sleep(1000);
            }

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

            pv.stop();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        System.out.println("--- Client ends ---");
    }

    /** @return Demo scan sequence */
    private CommandSequence createCommands() throws Exception
    {
        final CommandSequence commands = new CommandSequence();
        commands.loop("xpos", 1, 5, 1,
                new LogCommand("xpos"));
        return commands;
    }

    /** JUnit test that runs server and client */
    @Test(timeout=10000)
    public void runScanServer() throws Exception
    {
        final ScanServerImpl server = new ScanServerImpl();
        try
        {
        	server.start();
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

        // In another thread, try the client
        final Thread client = new Thread(this, "Client");
        client.start();
        // Wait for client to finish
        client.join();

        System.out.println("Scan Server exiting.");
        server.stop();
    }
}
