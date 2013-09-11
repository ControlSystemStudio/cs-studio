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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServerInfo;
import org.csstudio.scan.server.ScanState;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.csstudio.utility.test.HamcrestMatchers.greaterThan;
import static org.csstudio.utility.test.HamcrestMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.equalTo;

/** JUnit demo of the {@link ScanClient}
 *
 *  <p>The Scan server must be running!
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanClientDemo
{
    private ScanClient getScanClient()
    {
        return new ScanClient();
    }


    @Test(timeout=10000)
    public void getServerInfo() throws Exception
    {
        final ScanClient client = getScanClient();
        final ScanServerInfo info = client.getServerInfo();
        System.out.println(info);
        assertThat(info.getMemoryPercentage(), greaterThan(0.0));
    }

    
    @Test(timeout=10000)
    public void listScanInfos() throws Exception
    {
        final ScanClient client = getScanClient();
        final List<ScanInfo> infos = client.getScanInfos();
        for (ScanInfo info : infos)
            System.out.println(info + " @ " + info.getCurrentCommand() + " (" + info.getCurrentAddress() + ")");
        
        assertThat(infos.size(), greaterThan(0));
        final ScanInfo other = client.getScanInfo(infos.get(0).getId());
        assertThat(other.getId(), equalTo(infos.get(0).getId()));
        assertThat(other.getName(), equalTo(infos.get(0).getName()));
        assertThat(other, not(sameInstance(infos.get(0))));
    }
    

    @Test(timeout=10000)
    public void getScanCommands() throws Exception
    {
        final ScanClient client = getScanClient();
        final List<ScanInfo> infos = client.getScanInfos();
        assertThat(infos.size(), greaterThan(0));
        try
        {
            final String xml = client.getScanCommands(infos.get(0).getId());
            System.out.println(xml);
            assertThat(xml, containsString("<commands>"));
        }
        catch (Exception ex)
        {
            // If server was just re-started and only contains logged scans,
            // there may not be any commands.
            assertThat(ex.getMessage(), containsString("not available"));
            assertThat(ex.getMessage(), containsString("logged"));
        }
    }


    @Test// (timeout=10000)
    public void getScanData() throws Exception
    {
        final ScanClient client = getScanClient();
        final List<ScanInfo> infos = client.getScanInfos();
        assertThat(infos.size(), greaterThan(0));

        final ScanData data = client.getScanData(infos.get(0).getId());
        System.out.println(Arrays.toString(data.getDevices()));
        
        final ScanDataIterator iterator = new ScanDataIterator(data);
        iterator.printTable(System.out);
    }

    
    @Test(timeout=10000)
    public void submitScan() throws Exception
    {
        final ScanClient client = getScanClient();
        
        final CommandSequence commands = new CommandSequence(
            new DelayCommand(1.0));
        final long id = client.submitScan("SubmitDemo", commands.getXML());
        
        ScanInfo info = client.getScanInfo(id);
        System.out.println(info);
        
        assertThat(info.getName(), equalTo("SubmitDemo"));
        assertThat(info.getId(), equalTo(id));
        
        // Wait for scan to finish
        while (!info.getState().isDone())
        {
            TimeUnit.SECONDS.sleep(1);
            info = client.getScanInfo(id);
            System.out.println(info);
        }
    }
    
    
    @Test(timeout=10000)
    public void controlScan() throws Exception
    {
        final ScanClient client = getScanClient();
        
        final CommandSequence commands = new CommandSequence(
            new LoopCommand("loc://x", 1, 1000, 1,
                new DelayCommand(1.0)
            ));
        final long id = client.submitScan("ControlDemo", commands.getXML());
        
        ScanInfo info = client.getScanInfo(id);
        System.out.println(info);
        assertThat(info.getName(), equalTo("ControlDemo"));
        assertThat(info.getId(), equalTo(id));
        // Wait for scan to start (may initially be Idle)
        while (info.getState() != ScanState.Running)
        {
            TimeUnit.SECONDS.sleep(1);
            info = client.getScanInfo(id);
            System.out.println(info);
        }
        
        client.pauseScan(id);
        info = client.getScanInfo(id);
        System.out.println(info);
        assertThat(info.getState(), equalTo(ScanState.Paused));

        client.resumeScan(id);
        info = client.getScanInfo(id);
        System.out.println(info);
        assertThat(info.getState(), equalTo(ScanState.Running));

        client.abortScan(id);
        info = client.getScanInfo(id);
        System.out.println(info);
        assertThat(info.getState(), equalTo(ScanState.Aborted));
    }
}
