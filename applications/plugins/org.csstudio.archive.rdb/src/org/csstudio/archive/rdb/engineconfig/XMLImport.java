/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.engineconfig;

import java.io.InputStream;
import java.util.logging.Level;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.archive.rdb.Activator;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.SampleMode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/** SAX-type parser for reading model info from XML into RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLImport extends DefaultHandler
{
    // private static final String DEFAULT_RETENTION = "Forever";

    /** XML tag */
    final private static String TAG_GROUP = "group";

    /** XML tag */
    final private static String TAG_CHANNEL = "channel";

    /** XML tag */
    final private static String TAG_NAME = "name";

    /** XML tag */
    final private static String TAG_PERIOD = "period";

    /** XML tag */
    final private static String TAG_MONITOR = "monitor";

    /** XML tag */
    final private static String TAG_SCAN = "scan";

    /** XML tag */
    final private static String TAG_DISABLE = "disable";

    /** XML tag */
    final private static String TAG_ENABLE = "enable";

    final boolean steal_channels;

    /** Connection to RDB archive */
    final private RDBArchive archive;

    /** Engine info entry */
    final private SampleEngineConfig engine;

    /** Sample modes */
    final private SampleMode monitor_mode, scan_mode;

    /** Accumulator for characters within a tag */
    final private StringBuffer accumulator = new StringBuffer();

    /** States of the parser */
    private enum State
    {
        /** Reading all the initial parameters */
        PREAMBLE,

        /** Got start of a group, waiting for group name */
        GROUP,

        /** Got start of a channel, waiting for details */
        CHANNEL
    }

    /** Current parser state */
    private State state = State.PREAMBLE;

    /** Most recent 'name' tag */
    private String name;

    /** Most recent 'period' tag */
    private double period;

    /** Most recent 'monitor' tag */
    private boolean monitor;

    /** Most recent sample mode value, for example the optional monitor delta */
    private double sample_value;

    /** Is current channel enabling the group ? */
    private boolean is_enabling;

    /** Current archive group */
    private ChannelGroupConfig group;

    /** Construct handler for given model
     *  @param RDB_URL URL of RDB
     *  @param user User name
     *  @param password password
     *  @param engine_name
     *  @param engine_description
     *  @param engine_url
     *  @param replace_existing_engineconfig Replace existing engine config or stop?
     *  @param steal_channels
      * @throws Exception on error
      */
    public XMLImport(final String RDB_URL,
            final String user, final String password,
            final String engine_name,
            final String engine_description, final String engine_url,
            final boolean replace_existing_engineconfig,
            final boolean steal_channels)
        throws Exception
    {
        this.steal_channels = steal_channels;
        archive = RDBArchive.connect(RDB_URL, user, password);

        final SampleEngineConfig found = archive.findEngine(engine_name);
        if (found != null)
        {
            if (replace_existing_engineconfig)
                Activator.getLogger().log(Level.WARNING, "Replacing existing engine config {0}", found);
            else
                throw new Exception("Engine config '" + engine_name +
                                    "' already exists");
        }

        engine = archive.addEngine(engine_name, engine_description, engine_url);
        // retention = archive.getRetention(DEFAULT_RETENTION);
        final SampleMode[] modes = archive.getSampleModes();
        monitor_mode = findSampleMode(modes, SampleMode.MONITOR);
        scan_mode = findSampleMode(modes, SampleMode.SCAN);
    }


    private SampleMode findSampleMode(final SampleMode[] modes,
            final String desired_mode) throws Exception
    {
        for (SampleMode mode : modes)
            if (mode.getName().equalsIgnoreCase(desired_mode))
                return mode;
        throw new Exception("Unknown sample mode " + desired_mode);
    }

    /** Must be called to reclaim DB resources */
    public void close() throws Exception
    {
        archive.close();
    }

    /** Configure model by parsing given input stream */
    public void parse(final InputStream stream) throws Exception
    {
        final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(stream, this);
    }

    /** Reset the accumulator at the start of each element */
    @Override
    public void startElement(final String uri, final String localName,
                    final String element, final Attributes attributes)
                    throws SAXException
    {
        accumulator.setLength(0);
        if (element.equals(TAG_GROUP))
        {
            if (state != State.PREAMBLE)
                throw new SAXException("Unexpected group entry");
            // Wait for group stuff, reset values
            state = State.GROUP;
            name = null;
        }
        else if (element.equals(TAG_CHANNEL))
        {
            if (state != State.GROUP)
                throw new SAXException("Unexpected channel entry");
            // Wait for channel stuff, reset values/set defaults
            state = State.CHANNEL;
            name = null;
            period = 1.0;
            sample_value = 0.0;
            monitor = false;
            is_enabling = false;
        }
    }

    /** Accumulate characters within (or also between) current element(s) */
    @Override
    public void characters(final char ch[], final int start, final int length)
    {
        accumulator.append(ch, start, length);
    }

    /** Handle the data for the completed element */
    @Override
    public void endElement(final String uri, final String localName,
                    final String element)
                    throws SAXException
    {
        if (element.equals(TAG_NAME))
        {
            // Remove leading/trailing spaces
            name = accumulator.toString().trim();
            // Chop the ".arReq" off old archive request file names
            final int arReq = name.lastIndexOf(".arReq");
            if (arReq > 0)
                name = name.substring(0, arReq);
            if (state == State.GROUP)
            {   // Fetch the group for the following channels
                try
                {
                    group = engine.addGroup(name);
                    Activator.getLogger().info("Import '" + engine.getName()
                            + "', Group '" + name + "'");
                }
                catch (Exception e)
                {
                    throw new SAXException("Adding group " + name + " :"
                            + e.getMessage());
                }
                name = null;
            }
            else if (state != State.CHANNEL)
                throw new SAXException("Got 'name' '" + name
                                + " in state " + state.name());
            // else: just remember the name while collecting channel info
        }
        else if (element.equals(TAG_PERIOD))
        {
            checkStateForTag(State.CHANNEL, element);
            period = PeriodFormat.parseSeconds(accumulator.toString());
        }
        else if (element.equals(TAG_MONITOR))
        {
            checkStateForTag(State.CHANNEL, element);
            // Did the monitor tag contain a 'delta' for the scan mode value?
            final String arg = accumulator.toString().trim();
            if (arg.length() > 0)
            {
                try
                {
                    sample_value = Double.parseDouble(arg);
                }
                catch (NumberFormatException ex)
                {
                    throw new SAXException("Invalid monitor 'delta' for channel " + name);
                }
            }
            monitor = true;
        }
        else if (element.equals(TAG_SCAN))
        {
            checkStateForTag(State.CHANNEL, element);
            monitor = false;
        }
        else if (element.equals(TAG_ENABLE))
        {
            checkStateForTag(State.CHANNEL, element);
            if (group.getEnablingChannelId() > 0)
                throw new SAXException("More than one 'enable' channel");
            is_enabling = true;
        }
        else if (element.equals(TAG_DISABLE))
        {
            checkStateForTag(State.CHANNEL, element);
            throw new SAXException("Disable no longer supported, only 'enable'");
        }
        else if (element.equals(TAG_CHANNEL))
        {
            checkStateForTag(State.CHANNEL, element);
            state = State.GROUP;
            try
            {
                final ChannelConfig channel = archive.createChannel(name);
                // Check if channel is already in another group
                if (channel.getGroupId() > 0)
                {
                    final ChannelGroupConfig other_group =
                        archive.findGroup(channel.getGroupId());
                    final SampleEngineConfig other_engine =
                        archive.findEngine(other_group.getEngineId());
                    final String warning = String.format(
                        "Channel '%s/%s/%s' already in '%s/%s'",
                        engine.getName(), group.getName(), name,
                        other_engine.getName(), other_group.getName());
                    Activator.getLogger().warning(warning);
                    if (!steal_channels)
                    {
                        // Don't proceed with this channel,
                        // but run on with the next channel so that we
                        // get all the errors once instead of having
                        // to run the tool error by error
                        Activator.getLogger().warning(warning);
                        return;
                    }
                }
                channel.addToGroup(group);
                channel.setSampleMode(monitor ? monitor_mode : scan_mode, sample_value, period);
                if (is_enabling)
                    group.setEnablingChannel(channel);
            }
            catch (Exception ex)
            {	// Must convert to SAXException
                final StackTraceElement[] trace = ex.getStackTrace();
                throw new SAXException("Cannot add channel '" + name
                                + "' to group '" + group.getName()
                                + "', Engine '" + engine.getName()
                                + "':\n" + ex.getMessage()
                                + " (" + trace[0].getFileName()
                                + ", " + trace[0].getLineNumber() + ")",
                                ex);
            }
        }
        else if (element.equals(TAG_GROUP))
        {
            group = null;
            state = State.PREAMBLE;
        }
        // else: Ignore the unknown element
    }

    /** Check if we are in the correct state
     *  @param expected Expected state
     *  @param tag Current tag
     *  @throws SAXException on error
     */
    private void checkStateForTag(final State expected, final String tag)
        throws SAXException
    {
        if (state == expected)
            return;
        throw new SAXException("Got " + tag + " in state " + state.name());
    }

    /** Show warning in log (default would have been to ignore it) */
    @Override
    public void warning(final SAXParseException ex)
    {
        Activator.getLogger().log(Level.WARNING, "Configuration file line " + ex.getLineNumber() + ":", ex);
    }

    /** Show error in log (default would have been to ignore it) */
    @Override
    public void error(final SAXParseException ex)
    {
        Activator.getLogger().log(Level.SEVERE, "Configuration file line " + ex.getLineNumber() + ":", ex);
    }
}
