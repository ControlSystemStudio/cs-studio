package org.csstudio.archive.rdb.engineconfig.test;

import static org.junit.Assert.*;

import java.net.URL;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.Retention;
import org.csstudio.archive.rdb.TestSetup;
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
                engine_name, "LLRF Engine Test", new URL("http://some_host:4812"));
        System.out.println(engine);

        // This deletes the previous entry and all that's under it, then creates a new one
        final SampleEngineConfig engine2 = archive.addEngine(
                engine_name, "LLRF Engine Test", new URL("http://some_host:4812"));
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
        final ChannelGroupConfig group = engine.addGroup("TestGroup", retention);
        System.out.println(group);
        final ChannelGroupConfig group2 = engine.addGroup("TestGroup2", retention);
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
