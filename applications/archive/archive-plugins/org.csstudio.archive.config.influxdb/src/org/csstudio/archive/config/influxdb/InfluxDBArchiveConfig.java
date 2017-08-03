/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.influxdb;

import java.time.Instant;
import java.util.logging.Level;

import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.xml.XMLArchiveConfig;
import org.csstudio.archive.config.xml.XMLGroupConfig;
import org.csstudio.archive.influxdb.InfluxDBArchivePreferences;
import org.csstudio.archive.influxdb.InfluxDBQueries;
import org.csstudio.archive.influxdb.InfluxDBQueries.DBNameMap;
import org.csstudio.archive.influxdb.InfluxDBQueries.DefaultDBNameMap;
import org.csstudio.archive.influxdb.InfluxDBResults;
import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.influxdb.InfluxDB;

/** InfluxDB implementation of {@link ArchiveConfig}
 *
 *  <p>Provides read access via {@link ArchiveConfig} API,
 *  may in future allow write access via additional InfluxDB-only methods.
 *
 *  @author Kay Kasemir
 *  @author Megan Grodowitz - InfuxDB implementation
 */
@SuppressWarnings("nls")
public class InfluxDBArchiveConfig extends XMLArchiveConfig
{
    /** InfluxDB connection */
    final private InfluxDB influxdb;

    /** InfluxDB statements */
    final private InfluxDBQueries influxQuery;

    final static private DBNameMap dbnames = new DefaultDBNameMap();

    /** Initialize.
     *  This constructor will be invoked when an {@link ArchiveConfig}
     *  is created via the extension point.
     *  @throws Exception on error, for example InfluxDB connection error
     */
    public InfluxDBArchiveConfig() throws Exception
    {
        this(InfluxDBArchivePreferences.getURL(), InfluxDBArchivePreferences.getUser(),
                InfluxDBArchivePreferences.getPassword());
    }

    /** Initialize.
     *  This constructor can be invoked by test code.
     *  @param url InfluxDB URL
     *  @param user .. user name
     *  @param password .. password
     *  @param schema Schema/table prefix, ending in ".". May be empty
     *  @throws Exception on error, for example InfluxDB connection error
     */
    public InfluxDBArchiveConfig(final String url, final String user, final String password) throws Exception
    {
        super();
        influxdb = InfluxDBUtil.connect(url, user, password);
        influxQuery = new InfluxDBQueries(influxdb, dbnames);
    }

    /** {@inheritDoc} */
    @Override
    public ChannelConfig[] getChannels(final GroupConfig the_group, final boolean skip_last) throws Exception
    {
        final XMLGroupConfig group = (XMLGroupConfig) the_group;

        if (skip_last)
        {
            return group.getChannelArray();
        }

        final ChannelConfig[] old_channels = group.getChannelArray();

        for (ChannelConfig channel : old_channels)
        {
            final Instant last_sample_time = InfluxDBResults.getTimestamp(influxQuery.get_newest_channel_samples(channel.getName(), null, null, 1L));
            if (last_sample_time == null)
            {
                Activator.getLogger().log(Level.WARNING, "Failed to get last sample time for channel " + channel.getName());
            }
            else if (!last_sample_time.equals(channel.getLastSampleTime()))
            {
                group.updateChannelLastTime(channel.getName(), last_sample_time);
            }
        }
        return group.getChannelArray();
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        influxdb.close();
    }

    // public static void Test()
    // {
    // String archive_url = "http://diane.ornl.gov:8086";
    // String user = null;
    // String password = null;
    // String tmp_file = File.createTempFile("InfluxDBConfigTest-out", ".xml");
    // String input_file = new
    // File("../org.csstudio.archive.config.influxdb/xml/demo.xml");
    // String engine_name = "demo";
    // ArchiveConfig config;
    //
    // if (archive_url == null || tmp_file == null || input_file == null ||
    // engine_name == null)
    // {
    // System.out.println("Skipping test, missing one of: archive_url, tmp_file,
    // input_file, engine_name");
    // config = null;
    // return;
    // }
    //
    // System.out.println("Using temporary file: " + tmp_file.getName());
    //
    // if (user == null || password == null)
    // {
    // System.out.println("Trying connections with no username or
    // password....");
    // user = null;
    // password = null;
    // }
    //
    // config = new InfluxDBArchiveConfig(archive_url, user, password);
    //
    // assertTrue(input_file.exists());
    // final XMLImport importer = new XMLImport(config, true, false);
    // final InputStream stream = new FileInputStream(input_file);
    // System.out.println("Reading file " + input_file + ", " +
    // input_file.length() + " bytes");
    //
    // importer.parse(stream, engine_name, "Demo", "http://localhost:4813");
    // }

}
