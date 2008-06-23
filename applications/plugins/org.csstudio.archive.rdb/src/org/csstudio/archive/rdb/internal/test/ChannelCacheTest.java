package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.*;

import java.sql.Statement;

import org.csstudio.archive.rdb.TestSetup;
import org.csstudio.archive.rdb.internal.ChannelCache;
import org.csstudio.archive.rdb.internal.ChannelConfigImpl;
import org.csstudio.archive.rdb.internal.RDBArchiveImpl;
import org.junit.Test;

/** Test of ChannelCache
 *  @author Kay Kasemir
 */
public class ChannelCacheTest
{
    @SuppressWarnings("nls")
    @Test
    public void testChannelCache() throws Exception
    {
        final RDBArchiveImpl archive = new RDBArchiveImpl(TestSetup.URL);
        final ChannelCache channels = new ChannelCache(archive);
        
        final String name = "does_not_exist";
        ChannelConfigImpl channel = channels.find(name);
        assertNull(channel);
        
        channel = channels.findOrCreate(name);
        assertEquals(name, channel.getName());
        System.out.println(channel);
        
        // Hit the cache
        final ChannelConfigImpl channel2 = channels.findOrCreate(name);
        assertSame(channel, channel2);
        
        // Hack for Unit test: Delete the entry
        Statement statement = archive.getRDB().getConnection().createStatement();
        int rows = statement.executeUpdate(
                "DELETE FROM channel WHERE channel_id=" + channel.getId());
        statement.close();
        assertEquals(1, rows);
        archive.getRDB().getConnection().commit();

        // It's actually still in the cache...
        final ChannelConfigImpl channel3 = channels.findOrCreate(name);
        assertSame(channel, channel3);
        
        channels.dispose();
        archive.close();
    }
}
