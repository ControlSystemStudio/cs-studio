/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ArchiveConfigFactory;
import org.csstudio.archive.config.ChannelConfig;
import org.csstudio.archive.config.EngineConfig;
import org.csstudio.archive.config.GroupConfig;
import org.csstudio.archive.config.SampleMode;
import org.csstudio.data.values.TimestampFactory;

/** Export engine configuration as XML (to stdout)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLExport
{
	/** Initialize
     *  @throws Exception on error
     */
    public void export(final String engine_name) throws Exception
    {
    	final ArchiveConfig config = ArchiveConfigFactory.getArchiveConfig();
    	try
    	{
	        final EngineConfig engine = config.findEngine(engine_name);
	        if (engine == null)
	            throw new Exception("Unknown engine '" + engine_name + "'");
	        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
	        System.out.println("<!-- Created by ArchiveConfigTool -engine " + engine_name + " -export");
	        System.out.println("     " + TimestampFactory.now().toString());
	        System.out.println(" -->");
	        dumpEngine(config, engine);
    	}
    	finally
    	{
    		config.close();
    	}
    }

    private void dumpEngine(final ArchiveConfig config, final EngineConfig engine) throws Exception
    {
        System.out.println("<engineconfig>");
        final GroupConfig[] groups = config.getGroups(engine);
        for (GroupConfig group : groups)
            dumpGroup(config, group);
        System.out.println("</engineconfig>");
    }

    private void dumpGroup(final ArchiveConfig config, final GroupConfig group) throws Exception
    {
        System.out.println("  <group>");
        System.out.println("    <name>" + group.getName() + "</name>");
        final ChannelConfig[] channels = config.getChannels(group);
        for (ChannelConfig channel : channels)
            dumpChannel(channel);
        System.out.println("  </group>");
    }

    private void dumpChannel(final ChannelConfig channel)
    {
        System.out.print("      <channel>");
        System.out.print("<name>" + channel.getName() + "</name>");
        final SampleMode mode = channel.getSampleMode();
        System.out.print("<period>" + mode.getPeriod() + "</period>");
        if (mode.isMonitor())
        {
            if (mode.getDelta() != 0.0)
                System.out.print("<monitor>" + mode.getDelta() + "</monitor>");
            else
                System.out.print("<monitor/>");
        }
        else
            System.out.print("<scan/>");
        System.out.println("</channel>");
    }
}
