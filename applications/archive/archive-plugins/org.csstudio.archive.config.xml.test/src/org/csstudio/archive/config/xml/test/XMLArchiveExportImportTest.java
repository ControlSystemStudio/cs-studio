/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.xml.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.XMLExport;
import org.csstudio.archive.config.XMLImport;
import org.csstudio.archive.config.xml.XMLArchiveConfig;
import org.csstudio.archive.config.xml.XMLFileUtil;
import org.csstudio.archive.config.xml.XMLFileUtil.SingleURLMap;
import org.junit.Test;

/** JUnit demo of {@link XMLExport} and {@link XMLImport}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLArchiveExportImportTest
{
    private static String base_config_path = "../org.csstudio.archive.config.xml/xml";
    private static String base_engine_url = "http://localhost.test";

    // public static void setPluginPref(final String plugin, final String
    // preference, final String value) {
    // final IEclipsePreferences pref_node =
    // InstanceScope.INSTANCE.getNode(plugin);
    // pref_node.put(preference, value);
    // try {
    // pref_node.flush();
    // } catch (BackingStoreException e) {
    // System.err.println("Could not set plugin preference " + plugin + "/" +
    // preference + "=" + value);
    // }
    // }

    /**
     * Archive configurations for accelerator (snapshot only)
     * ics-srv02.ornl.gov:/usr/local/css/archive_configs/
     *
     * Beamlines (e.g. Mandi)
     * bl11b-dassrv1.sns.gov:/home/controls/bl11b/Makefile
     * archive:
        css/build_arch.sh > css/bl11b_arch.xml
     *
     * @throws Exception
     */
    // @Before
    // public void setPrefs() throws Exception
    // {
    // // setPluginPref("org.csstudio.archive.config.xml", "config_path",
    // // base_config_path);
    // // setPluginPref("org.csstudio.archive.config.xml", "engine_url",
    // // engine_url);
    // }

    // @After
    // public void close()
    // {
    // if (config != null)
    // config.close();
    // }

    private void confCheck(String engine_name, String engine_url, final ArchiveConfig config) throws Exception {
        EngineConfig engine = config.findEngine(engine_name);
        assertFalse(engine == null);
        assertTrue(engine.getURL().toString().equals(engine_url));
        assertTrue(engine.getName().equals(engine_name));

        GroupConfig[] groups = config.getGroups(engine);
        assertTrue(groups.length == 2);

        int total_chan = 0;
        for (GroupConfig group : groups) {
            System.out.println("Getting channels for group: " + group.getName());
            ChannelConfig[] chans = config.getChannels(group, true);
            total_chan += chans.length;

            for (ChannelConfig chan : chans) {
                System.out.println("Channel: " + chan.toString());
            }
        }
        assertTrue(total_chan == 4);

        System.out.println("Got engine: " + engine.getName() + ", with " + groups.length + " groups, " + total_chan
                + " channels at url: " + engine.getURL());
    }

    @Test
    public void testImport() throws Exception {
        String engine_name = "demo";
        String engine_url = base_engine_url + ".1";
        String engine_path = base_config_path;

        final ArchiveConfig config = new XMLArchiveConfig(engine_path, engine_url);

        confCheck(engine_name, engine_url, config);
    }

    @Test
    public void testImportUtil() throws Exception {
        String engine_name = "demo";
        String engine_url = base_engine_url + ".1";

        final XMLArchiveConfig config = new XMLArchiveConfig();
        final XMLFileUtil util = new XMLFileUtil(true);
        util.importAll(config, base_config_path + "/" + engine_name + ".xml", new SingleURLMap(engine_url));

        confCheck(engine_name, engine_url, config);
    }

    protected void multiConfCheck(XMLArchiveConfig config) throws Exception {
        EngineConfig[] engines = config.getEngines();
        assertTrue(engines.length == 3);

        int total_groups = 0;
        int total_chans = 0;
        for (EngineConfig e : engines) {
            GroupConfig[] groups = config.getGroups(e);
            total_groups += groups.length;

            System.out.println("Getting channels for engine: " + e.getName());

            for (GroupConfig g : groups) {
                System.out.println("Getting channels for group: " + g.getName());

                ChannelConfig[] chans = config.getChannels(g, true);
                total_chans += chans.length;

                for (ChannelConfig chan : chans) {
                    System.out.println("Channel: " + chan.toString());
                }
            }
        }

        assertTrue(total_groups == 6);
        assertTrue(total_chans == 12);
    }

    @Test
    public void testImportRecursive() throws Exception {
        final String[] names = new String[3];
        names[0] = "demo";
        names[1] = "demo2";
        names[2] = "demoS";

        final XMLArchiveConfig config = new XMLArchiveConfig();
        final XMLFileUtil util = new XMLFileUtil(true);
        util.importAll(config, base_config_path, new SingleURLMap(base_engine_url));
        assert (util.getImportedFiles().size() == 3);

        for (int idx = 0; idx < 3; idx++) {
            EngineConfig engine = config.findEngine(names[idx]);
            assertFalse(engine == null);
            assertTrue(engine.getURL().toString().equals(base_engine_url));
            assertTrue(engine.getName().equals(names[idx]));
        }

        multiConfCheck(config);
    }

    @Test
    public void testImportMulti() throws Exception {
        final String[] urls = new String[3];
        urls[0] = base_engine_url + ".0";
        urls[1] = base_engine_url + ".1";
        urls[2] = base_engine_url + ".2";

        final String[] names = new String[3];
        names[0] = "demo";
        names[1] = "demo2";
        names[2] = "demoS";

        final String[] paths = new String[3];
        paths[0] = base_config_path;
        paths[1] = base_config_path;
        paths[2] = base_config_path + "/subdir";

        final XMLArchiveConfig config = new XMLArchiveConfig();

        for (int idx = 0; idx < 3; idx++) {
            config.setParams(paths[idx], urls[idx]);
            EngineConfig engine = config.findEngine(names[idx]);
            assertFalse(engine == null);
        }

        for (int idx = 0; idx < 3; idx++) {
            EngineConfig engine = config.findEngine(names[idx]);
            assertFalse(engine == null);
            assertTrue(engine.getURL().toString().equals(urls[idx]));
            assertTrue(engine.getName().equals(names[idx]));
        }

        multiConfCheck(config);
    }

    /** Export the config to temporary xml
     * @throws Exception
     */
    @Test
    public void testExport() throws Exception
    {
        String engine_name = "demo";
        String engine_url = base_engine_url + ".1";
        String engine_path = base_config_path;

        final XMLArchiveConfig config = new XMLArchiveConfig(engine_path, engine_url);
        EngineConfig engine = config.findEngine(engine_name);

        final File tmp_file = File.createTempFile("InfluxDBConfigTest-out", ".xml");
        System.out.println("Using temporary file: " + tmp_file.getName());

        final String filename = tmp_file.getAbsolutePath();
        if (tmp_file.exists())
            tmp_file.delete();
        assertFalse(tmp_file.exists());

        config.exportEngine(filename, engine_name);

        assertTrue(tmp_file.exists());
        System.out.println("Created file " + tmp_file + ", " + tmp_file.length() + " bytes");

        final XMLArchiveConfig config_rb = new XMLArchiveConfig(tmp_file.getAbsolutePath(), engine_url);
        final EngineConfig engine_rb = config_rb.findEngine(engine_name);

        assertFalse(engine_rb == null);

        GroupConfig[] groups = config.getGroups(engine);
        GroupConfig[] groups_rb = config_rb.getGroups(engine_rb);

        assertTrue(groups.length == groups_rb.length);

        int chan_count = 0;
        int chan_count_rb = 0;

        for (GroupConfig group : groups) {
            ChannelConfig[] chans = config.getChannels(group, true);
            chan_count += chans.length;
        }

        for (GroupConfig group : groups_rb) {
            ChannelConfig[] chans = config_rb.getChannels(group, true);
            chan_count_rb += chans.length;
        }

        assertTrue(chan_count == chan_count_rb);
    }

    // @Test
    // public void testEngine() throws Exception
    // {
    // if (config == null)
    // return;
    // final EngineConfig engine = config.findEngine(engine_name);
    // assertNotNull("Cannot locate engine " + engine_name, engine);
    // System.out.println(engine.getName() + ": " + engine.getDescription() +
    // " @ " + engine.getURL());
    // assertEquals(engine_name, engine.getName());
    //
    // final GroupConfig[] groups = config.getGroups(engine);
    // for (GroupConfig group : groups)
    // System.out.println(group.getName());
    // assertTrue(groups.length > 0);
    //
    // for (GroupConfig group : groups)
    // {
    // final ChannelConfig[] channels = config.getChannels(group, false);
    // for (ChannelConfig channel : channels)
    // System.out.println(group.getName() + " - " + channel.getName() + " " +
    // channel.getSampleMode() +
    // ", last sample time: " + channel.getLastSampleTime());
    // }
    // }

    //    @Test
    //    public void testDelete() throws Exception
    //    {
    //        try
    //        {
    //            EngineConfig engine = config.findEngine(engine_name);
    //            assertNotNull(engine);
    //            config.deleteEngine(engine);
    //            engine = config.findEngine(engine_name);
    //            assertNull(engine);
    //        }
    //        finally
    //        {
    //            config.close();
    //        }
    //    }


}
