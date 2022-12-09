/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import java.net.InetAddress;

import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.model.ArchiveChannel;

import java.util.Iterator;

/** Provide web page with engine overview.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class AbstractMainResponse extends AbstractResponse
{
    /** Bytes in a MegaByte */
    protected final static double MB = 1024.0*1024.0;

    protected static String host = null;

    protected int totalChannelCount;
    protected int disconnectCount;

    public AbstractMainResponse(final EngineModel model)
    {
        super(model);

        if (host == null)
        {
            try
            {
                final InetAddress localhost = InetAddress.getLocalHost();
                host = localhost.getHostName();
            }
            catch (Exception ex)
            {
                host = "localhost";
            }
        }
    }

    protected void updateChannelCount() {
		final Iterator<ArchiveGroup> groupsIter = model.getAllGroupsIter();
		int connect_count = 0;
		totalChannelCount = 0;
		while (groupsIter.hasNext())
		{
			final ArchiveGroup group = groupsIter.next();
			final Iterator<ArchiveChannel> iter = group.getAllChannelsIter();
			long channel_count = 0;
			while (iter.hasNext())
			{
				if (iter.next().isConnected()) {
					++connect_count;
				}
				++channel_count;
			}
			totalChannelCount += channel_count;
		}

        disconnectCount = totalChannelCount - connect_count;
    }
}
