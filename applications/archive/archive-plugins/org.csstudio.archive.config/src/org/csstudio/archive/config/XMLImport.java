/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.apputil.time.SecondsParser;
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
    /** XML tag */
    final static String TAG_ENGINECONFIG = "engineconfig";

    /** XML tag */
    final protected static String TAG_GROUP = "group";

    /** XML tag */
    final protected static String TAG_CHANNEL = "channel";

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

    /** XML tag */
    final private static String TAG_RETAIN = "retain";

    /** Replace existing engine configuration? */
    final private boolean replace;

    /** Steal channels that currently belong to a different engine? */
    final private boolean steal_channels;

    /** Connection to RDB archive */
    final private ImportableArchiveConfig config;

    /** Accumulator for characters within a tag */
    final protected StringBuffer accumulator = new StringBuffer();

    /** States of the parser */
    protected enum State
    {
        /** Looking for the initial element */
        NEED_FIRST_ELEMENT,

        /** Reading all the initial parameters */
        PREAMBLE,

        /** Got start of a group, waiting for group name */
        GROUP,

        /** Got start of a channel, waiting for details */
        CHANNEL
    }

    /** Current parser state */
    protected State state = State.NEED_FIRST_ELEMENT;

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

    /** Engine */
    private EngineConfig engine;

    /** Current archive group */
    private GroupConfig group;

    /** Most recent 'retain' tag (contents) */
    private String retain = null;

    /**
     * Initialize
     *
     * @param config
     *            The config to import into
     * @param replace
     *            Replace existing engine configuration?
     * @param steal_channels
     *            Steal channels that currently belong to a different engine?
     * @throws Exception
     */
    public XMLImport(final ImportableArchiveConfig config,
            final boolean replace, final boolean steal_channels) throws Exception
    {
        this.config = config;
        this.replace = replace;
        this.steal_channels = steal_channels;
    }

    public void import_engine(final InputStream stream, final String engine_name, final String description,
            final String engine_url) throws Exception, XMLImportException {
        engine = config.createEngine(engine_name, description, engine_url);

        final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(stream, this);
    }

    /**
     * Parse an XML configuration into the config
     *
     * @param stream
     * @param engine_name
     * @param description
     * @param engine_url
     * @throws Exception
     */
    public void parse(final InputStream stream, final String engine_name, final String description,
            final String engine_url) throws Exception
    {
        engine = config.findEngine(engine_name);
        if (engine != null)
        {
            if (replace)
            {
                System.out.println("Replacing existing engine config " + engine_name);
                config.deleteEngine(engine);
            }
            else
                throw new XMLImportException("Error: Engine config '" + engine_name +
                "' already exists");
        }

        import_engine(stream, engine_name, description, engine_url);
    }

    /** Reset the accumulator at the start of each element */
    @Override
    public void startElement(final String uri, final String localName,
                    final String element, final Attributes attributes)
                    throws SAXException
    {
        if (state == State.NEED_FIRST_ELEMENT)
        {    // Check for 'engineconfig'
            if (! TAG_ENGINECONFIG.equals(element))
                throw new XMLImportException("Expected <" + XMLImport.TAG_ENGINECONFIG + "> but found <" + element + ">");
            state = State.PREAMBLE;
        }
        accumulator.setLength(0);
        if (element.equals(TAG_GROUP))
        {
            if (state != State.PREAMBLE)
                throw new XMLImportException("Unexpected group entry while in state " + state);
            // Wait for group stuff, reset values
            state = State.GROUP;
            name = null;
        }
        else if (element.equals(TAG_CHANNEL))
        {
            if (state != State.GROUP)
                throw new XMLImportException("Unexpected channel entry while in state " + state);
            // Wait for channel stuff, reset values/set defaults
            state = State.CHANNEL;
            name = null;
            period = 1.0;
            sample_value = 0.0;
            monitor = false;
            is_enabling = false;
        }
        else if (element.equals(TAG_RETAIN))
        {
            //TODO: support group retain tags (overridden by channel retain tags)
            //if (state != State.CHANNEL)
                //retain_parent_tag = TAG_CHANNEL;
            //else if (state == State.GROUP)
                //retain_parent_tag = TAG_GROUP;
            //else
            if (state != State.CHANNEL)
                throw new XMLImportException("Unexpected retain entry while in state " + state);
        }
    }

    /** Accumulate characters within (or also between) current element(s) */
    @Override
    public void characters(final char[] ch, final int start, final int length)
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
                    group = config.addGroup(engine, name);
                    // System.out.println("Import '" + engine.getName()
                    // + "', Group '" + name + "'");
                }
                catch (Exception ex)
                {
                    throw new SAXException("Error adding group " + name + " :"
                            + ex.getMessage(), ex);
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
            final String time_spec = accumulator.toString().trim();
            try
            {
                if (time_spec.contains(":"))
                    period = SecondsParser.parseSeconds(time_spec);
                else
                    period = PeriodFormat.parseSeconds(accumulator.toString());
            }
            catch (Exception ex)
            {
                throw new SAXException("Invalid 'period' for channel " + name, ex);
            }
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
                    throw new SAXException("Invalid monitor 'delta' for channel " + name, ex);
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
            if (group.getEnablingChannel() != null)
            {
                System.out.println("WARNING: Group " + group.getName() + " is already enabled by " + group.getEnablingChannel() +
                        ". Ignoring additional enabling channels for the same group");
            }
            else
                is_enabling = true;
        }
        else if (element.equals(TAG_DISABLE))
        {
            checkStateForTag(State.CHANNEL, element);
            throw new XMLImportException("<disable/> no longer supported, only <enable/>");
        }
        else if (element.equals(TAG_CHANNEL))
        {
            checkStateForTag(State.CHANNEL, element);
            state = State.GROUP;
            try
            {
                // System.out.println(group.getName() + " - " + name);
                // Check if channel is already in another group
                final GroupConfig other_group = config.getChannelGroup(name);
                if (other_group != null)
                {
                    final EngineConfig other_engine = config.getEngine(other_group);
                    // Don't proceed with this channel,
                    // but run on with the next channel so that we
                    // get all the errors once instead of having
                    // to run the tool error by error
                    if (steal_channels)
                        System.out.format("Channel '%s/%s - %s' already found in '%s/%s', moved to this engine\n",
                                engine.getName(), group.getName(), name,
                                other_engine.getName(), other_group.getName());
                    else
                    {
                        System.out.format("WARNING: Channel '%s/%s - %s' already found in '%s/%s', not added again to this engine\n",
                                engine.getName(), group.getName(), name,
                                other_engine.getName(), other_group.getName());
                        return;
                    }
                }

                final SampleMode mode = config.getSampleMode(monitor, sample_value, period);
                final ChannelConfig channel = config.addChannel(group, name, mode);
                if (is_enabling)
                {
                    config.setEnablingChannel(group, channel);
                    group.setEnablingChannel(channel);
                }
                if (retain != null && !retain.isEmpty())
                {
                    channel.setRetention(retain);
                    retain = null;
                }
            }
            catch (Exception ex)
            {    // Must convert to SAXException
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
        else if (element.equals(TAG_RETAIN))
        {
            retain = accumulator.toString().trim();
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
        throw new XMLImportException("Got " + tag + " in state " + state.name());
    }

    /** Show warning in log (default would have been to ignore it) */
    @Override
    public void warning(final SAXParseException ex)
    {
        System.out.println("Configuration file line " + ex.getLineNumber() + ":");
        ex.printStackTrace();
    }

    /** Show error in log (default would have been to ignore it) */
    @Override
    public void error(final SAXParseException ex)
    {
        System.out.println("Configuration file line " + ex.getLineNumber() + ":");
        ex.printStackTrace();
    }

    /** Must be called to reclaim RDB resources */
    public void close()
    {
        config.close();
    }
}
