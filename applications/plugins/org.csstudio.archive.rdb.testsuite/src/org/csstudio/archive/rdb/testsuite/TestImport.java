/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.Retention;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.junit.Test;

@SuppressWarnings("nls")
public class TestImport
{
    @Test
    public void test() throws Exception
    {
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL);
        
        // Add engine
        final String engine_name = "LLRF Test";
        final SampleEngineConfig engine = archive.addEngine(
                engine_name, "LLRF Engine Test", "http://some_host:4812");
        System.out.println(engine);

        // This deletes the previous entry and all that's under it, then creates a new one
        final SampleEngineConfig engine2 = archive.addEngine(
                engine_name, "LLRF Engine Test", "http://some_host:4812");
        assertNotSame(engine, engine2);

        final SampleEngineConfig engine3 = archive.findEngine(engine_name);
        // This implementation doesn't cache, so we get a new info..
        assertNotSame(engine, engine3);
        // .. with the same content:
        assertEquals(engine, engine3);
        
        // Get Retention
        final Retention retention = archive.getRetention("Forever");
        assertEquals("Forever", retention.getName());
        final Retention retention2 = archive.getRetention(retention.getName());
        assertSame(retention, retention2); // expect the same from cache

        // Add Group
        final ChannelGroupConfig group = engine.addGroup("TestGroup");
        System.out.println(group);
        final ChannelGroupConfig group2 = engine.addGroup("TestGroup2");
        System.out.println(group2);
                
        // Add Channels to Group
        ChannelConfig channel = archive.createChannel("fred");
        channel.addToGroup(group);
        channel = archive.createChannel("freddy");
        channel.addToGroup(group);
        
        group.setEnablingChannel(channel);

        final ChannelGroupConfig[] groups = engine.getGroups();
        for (ChannelGroupConfig g : groups)
            System.out.println(g);
        
        archive.close();
    }
}
