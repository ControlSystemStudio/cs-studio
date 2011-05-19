/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.sql.Statement;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.internal.ChannelCache;
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
        final RDBArchive archive = RDBArchive.connect(TestSetup.URL, TestSetup.USER, TestSetup.PASSWORD);
        final ChannelCache channels = new ChannelCache(archive);
        
        final String name = "does_not_exist";
        ChannelConfig channel = channels.find(name);
        assertNull(channel);
        
        channel = channels.findOrCreate(name);
        assertEquals(name, channel.getName());
        System.out.println(channel);
        
        // Hit the cache
        final ChannelConfig channel2 = channels.findOrCreate(name);
        assertSame(channel, channel2);
        
        // Hack for Unit test: Delete the entry
        Statement statement = archive.getRDB().getConnection().createStatement();
        int rows = statement.executeUpdate(
                "DELETE FROM channel WHERE channel_id=" + channel.getId());
        statement.close();
        assertEquals(1, rows);
        archive.getRDB().getConnection().commit();

        // It's actually still in the cache...
        final ChannelConfig channel3 = channels.findOrCreate(name);
        assertSame(channel, channel3);
        
        channels.dispose();
        archive.close();
    }
}
