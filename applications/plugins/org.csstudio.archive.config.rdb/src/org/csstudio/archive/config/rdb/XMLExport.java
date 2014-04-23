/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.io.PrintStream;

import org.csstudio.apputil.time.SecondsParser;
import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.SampleMode;
import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.util.time.Timestamp;

/** Export engine configuration as XML (to stdout)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLExport
{
	/** Export configuration
	 *  @param out {@link PrintStream}
	 *  @param rdb_url
	 *  @param rdb_user
	 *  @param rdb_password
	 *  @param rdb_schema
     *  @param engine_name Name of engine configuration
	 *  @throws Exception on error
     */
    public void export(final PrintStream out,
    		final String rdb_url, final String rdb_user, final String rdb_password, final String rdb_schema,
    		final String engine_name) throws Exception
    {
    	final RDBArchiveConfig config = new RDBArchiveConfig(rdb_url, rdb_user, rdb_password, rdb_schema);
    	try
    	{
	        final EngineConfig engine = config.findEngine(engine_name);
	        if (engine == null)
	            throw new Exception("Unknown engine '" + engine_name + "'");
	        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
	        out.println("<!-- Created by ArchiveConfigTool -engine " + engine_name + " -export");
	        out.println("     " + TimestampHelper.format(Timestamp.now()));
	        out.println(" -->");
	        dumpEngine(out, config, engine);
    	}
    	finally
    	{
    		config.close();
    	}
    }

    private void dumpEngine(final PrintStream out, final ArchiveConfig config, final EngineConfig engine) throws Exception
    {
        out.println("<engineconfig>");
        final GroupConfig[] groups = config.getGroups(engine);
        for (GroupConfig group : groups)
            dumpGroup(out, config, group);
        out.println("</engineconfig>");
    }

    private void dumpGroup(final PrintStream out, final ArchiveConfig config, final GroupConfig group) throws Exception
    {
        out.println("  <group>");
        out.println("    <name>" + group.getName() + "</name>");
        final ChannelConfig[] channels = config.getChannels(group, true);
        for (ChannelConfig channel : channels)
            dumpChannel(out, channel, group.getEnablingChannel());
        out.println("  </group>");
    }

    private void dumpChannel(final PrintStream out, final ChannelConfig channel, final String enablingChannel)
    {
        out.print("      <channel>");
        out.print("<name>" + channel.getName() + "</name>");
        final SampleMode mode = channel.getSampleMode();
        out.print("<period>" + SecondsParser.formatSeconds(mode.getPeriod()) + "</period>");
        if (mode.isMonitor())
        {
            if (mode.getDelta() != 0.0)
                out.print("<monitor>" + mode.getDelta() + "</monitor>");
            else
                out.print("<monitor/>");
        }
        else
            out.print("<scan/>");
        if (channel.getName().equals(enablingChannel)) {
            out.print("<enable/>");
        }
        out.println("</channel>");
    }
}
