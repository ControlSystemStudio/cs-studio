/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.test;

import static org.junit.Assert.*;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.rdb.RDBArchiveConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test/demo of the {@link RDBArchiveConfig}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveConfigTest
{
    private String engine_name;
    private ArchiveConfig config;

    @Before
    public void connect() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString("archive_rdb_url");
        final String user = settings.getString("archive_rdb_user");
        final String schema = settings.getString("archive_rdb_schema");
        final String password = settings.getString("archive_rdb_password");
        engine_name = settings.getString("archive_config");
        if (url == null  ||  user == null  ||  password == null  ||  engine_name == null)
        {
            System.out.println("Skipping test, no archive_rdb_url, user, password");
            return;
        }

        config = new RDBArchiveConfig(url, user, password, schema);
    }

    @After
    public void close()
    {
        config.close();
    }

    @Test
    public void testEngine() throws Exception
    {
        if (config == null)
            return;
        final EngineConfig engine = config.findEngine(engine_name);
        assertNotNull("Cannot locate engine " + engine_name, engine);
        System.out.println(engine.getName() + ": " + engine.getDescription() +
                " @ " + engine.getURL());
        assertEquals(engine_name, engine.getName());

        final GroupConfig[] groups = config.getGroups(engine);
        for (GroupConfig group : groups)
            System.out.println(group.getName());
        assertTrue(groups.length > 0);

        for (GroupConfig group : groups)
        {
            final ChannelConfig[] channels = config.getChannels(group, false);
            for (ChannelConfig channel : channels)
                System.out.println(group.getName() + " - " + channel.getName() + " " + channel.getSampleMode() +
                        ", last sample time: " + channel.getLastSampleTime());
        }
    }
}
