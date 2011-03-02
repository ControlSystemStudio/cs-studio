/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.Messages;
import org.csstudio.archive.common.engine.model.AbstractArchiveChannel;
import org.csstudio.archive.common.engine.model.BufferStats;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBuffer;

/** Provide web page with detail for one channel.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ChannelResponse extends AbstractResponse
{
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ChannelResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {   // Locate the group
        final String channel_name = req.getParameter("name");
        if (channel_name == null) {
            resp.sendError(400, "Missing channel name");
            return;
        }
        final AbstractArchiveChannel<?, ?> channel = _model.getChannel(channel_name);
        if (channel == null) {
            resp.sendError(400, "Unknown channel " + channel_name);
            return;
        }

        // HTML table similar to group's list of channels
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Channel");
        html.openTable(2, new String[]
        { Messages.HTTP_ChannelInfo });

        html.tableLine(new String[]
        { Messages.HTTP_Channel, channel_name });

        final String connected = channel.isConnected()
                        ? Messages.HTTP_Connected
                        : HTMLWriter.makeRedText(Messages.HTTP_Disconnected);
        html.tableLine(new String[]
        { Messages.HTTP_Connected, connected });

        html.tableLine(new String[]
        { Messages.HTTP_InternalState, channel.getInternalState() });

        html.tableLine(new String[]
        { Messages.HTTP_Mechanism, channel.getMechanism() });

        html.tableLine(new String[]
        { Messages.HTTP_CurrentValue, channel.getCurrentValueAsString() });

        html.tableLine(new String[]
        {
            Messages.HTTP_State,
            channel.isEnabled() ? Messages.HTTP_Enabled
                                : HTMLWriter.makeRedText(Messages.HTTP_Disabled)
        });

        final SampleBuffer<?, ?, ?> buffer = channel.getSampleBuffer();
        html.tableLine(new String[]
        { Messages.HTTP_QueueLen, Integer.toString(buffer.size()) });

        final BufferStats stats = buffer.getBufferStats();
        html.tableLine(new String[]
        {
            Messages.HTTP_QueueAvg,
            String.format("%.1f", stats.getAverageSize())
        });

        html.tableLine(new String[]
        { Messages.HTTP_QueueMax, Integer.toString(stats.getMaxSize()) });

        html.closeTable();

        html.close();
    }
}
