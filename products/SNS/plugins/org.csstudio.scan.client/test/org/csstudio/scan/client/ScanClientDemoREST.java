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

import java.util.List;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.server.ScanInfo;
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
public class ScanClientDemoREST
{
    private ScanClient getScanClient()
    {
        return new ScanClient();
    }

    
    @Test(timeout=10000)
    public void listScanInfos() throws Exception
    {
        final ScanClient client = getScanClient();
        final List<ScanInfo> infos = client.getScanInfos();
        for (ScanInfo info : infos)
            System.out.println(info);
        
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
        final String xml = client.getScanCommands(infos.get(0).getId());
        System.out.println(xml);
        assertThat(xml, containsString("<commands>"));
    }


    @Test(timeout=10000)
    public void getSubmit() throws Exception
    {
        final ScanClient client = getScanClient();
        
        final CommandSequence commands = new CommandSequence(
            new DelayCommand(5.0));
        final long id = client.submitScan("Demo", commands.getXML());
        
        final ScanInfo info = client.getScanInfo(id);
        System.out.println(info);
        
        assertThat(info.getName(), equalTo("Demo"));
        assertThat(info.getId(), equalTo(id));
    }

}
