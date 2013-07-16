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
package org.csstudio.scan.client;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanState;
import org.junit.Test;

/** JUnit demo of the scan client
 *
 *  <p>The Scan server must be running!
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanClientDemo
{
    /** Helper to dump info about all scans on server
     *  @param server
     *  @throws Exception
     */
    private void dumpInfos(final ScanServer server) throws Exception
    {
        System.out.println("----------------------");
        System.out.println("Scan Infos");
        final List<ScanInfo> infos = server.getScanInfos();
        for (ScanInfo info : infos)
            System.out.println(info);
        System.out.println("----------------------");
    }

    /** Test basic scan */
    @Test(timeout=10000)
    public void testScan() throws Exception
    {
        final ScanServer server = ScanServerConnector.connect();
        System.out.println(server.getInfo());

        final CommandSequence commands = new CommandSequence(
            new SetCommand("xpos", 2),
            new SetCommand("ypos", 3),
            new SetCommand("setpoint", 5),
            new DelayCommand(1.0),
            new LogCommand("xpos", "ypos", "setpoint", "readback"),
            new WaitCommand("readback", 5.0),
            new LogCommand("xpos", "ypos", "setpoint", "readback")
        );

        final long id = server.submitScan("Client Demo", commands.getXML());

        while (true)
        {
            final ScanInfo info = server.getScanInfo(id);
            System.out.println(info);
            if (info.getState().isDone())
                break;
            Thread.sleep(100);
        }
        dumpInfos(server);
        ScanServerConnector.disconnect(server);
    }

    /** Test pausing a scan */
    @Test(timeout=5000)
    public void testPause() throws Exception
    {
        final ScanServer server = ScanServerConnector.connect();

        final CommandSequence commands = new CommandSequence(
            new LoopCommand("xpos", 1, 5, 1, new LogCommand("xpos")) );

        final long id = server.submitScan("Pause Demo", commands.getXML());

        // Wait for it to run
        while (true)
        {
            final ScanInfo info = server.getScanInfo(id);
            if (info.getState() == ScanState.Running)
                break;
            Thread.sleep(10);
        }

        // Pause for 3 seconds
        server.pause(id);
        try
        {
            dumpInfos(server);
            for (int i=0; i<3; ++i)
            {
                final ScanInfo info = server.getScanInfo(id);
                System.out.println(info);
                assertEquals(ScanState.Paused, info.getState());
                Thread.sleep(1000);
            }
        }
        finally
        {   // Don't leave it paused on timeout!
            server.resume(id);
        }
        // Finish
        while (true)
        {
            final ScanInfo info = server.getScanInfo(id);
            System.out.println(info);
            if (info.getState().isDone())
                break;
            Thread.sleep(100);
        }
        dumpInfos(server);

        ScanServerConnector.disconnect(server);
    }

    /** Test aborting a scan */
    @Test(timeout=5000)
    public void testAbort() throws Exception
    {
        final ScanServer server = ScanServerConnector.connect();

        final CommandSequence commands = new CommandSequence(
            new LoopCommand("xpos", 1, 5, 1, new LogCommand("xpos")) );

        // Try to abort a scan right away
        long id = server.submitScan("Abort Demo1", commands.getXML());
        server.abort(id);
        dumpInfos(server);
        ScanInfo info = server.getScanInfo(id);
        assertEquals(ScanState.Aborted, info.getState());

        // Abort another scan after it ran for some time
        id = server.submitScan("Abort Demo2", commands.getXML());
        while (true)
        {
            info = server.getScanInfo(id);
            if (info.getPercentage() > 0)
                break;
            Thread.sleep(100);
        }
        server.abort(id);
        dumpInfos(server);
        info = server.getScanInfo(id);
        assertEquals(ScanState.Aborted, info.getState());

        ScanServerConnector.disconnect(server);
    }
}
