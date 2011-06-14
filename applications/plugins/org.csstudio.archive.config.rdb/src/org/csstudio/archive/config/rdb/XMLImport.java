/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.io.InputStream;

import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;

/** SAX-type parser for reading model info from XML into RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLImport
{
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

    /** Replace existing engine configuration? */
    final private boolean replace;
    
    /** Steal channels that currently belong to a different engine? */
    final private boolean steal_channels;

    /** Connection to RDB archive */
    final private RDBArchiveConfig config;
    
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
    private RDBGroupConfig group;
    
    /** Initialize
     *  @param rdb_url
     *  @param rdb_user
     *  @param rdb_password
     *  @param rdb_schema
     *  @param replace Replace existing engine configuration?
     *  @param steal_channels Steal channels that currently belong to a different engine?
     *  @throws Exception
     */
	public XMLImport(final String rdb_url, final String rdb_user, final String rdb_password,
			final String rdb_schema,
			final boolean replace, final boolean steal_channels) throws Exception
    {
		this.replace = replace;
		this.steal_channels = steal_channels;
		config = new RDBArchiveConfig(rdb_url, rdb_user, rdb_password, rdb_schema);
    }

	/** Parse an XML configuration into the RDB
	 *  @param stream
	 *  @param engine_name
	 *  @param description
	 *  @param engine_url
	 *  @throws Exception 
	 */
    public void parse(final InputStream stream, final String engine_name, final String description,
			final String engine_url) throws Exception
    {
	    // TODO Auto-generated method stub
		EngineConfig engine = config.findEngine(engine_name);
		if (engine != null)
		{
			if (replace)
			{
				System.out.println("Replacing existing engine config " + engine_name);
				config.deleteEngine(engine);
			}
			else
				throw new Exception("Engine config '" + engine_name +
                "' already exists");
		}
		engine = config.createEngine(engine_name, description, engine_url);
    }

	/** Must be called to reclaim RDB resources */
	public void close()
	{
		config.close();
	}
}
