/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveChannel;
import org.csstudio.archive.engine.model.BufferStats;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.model.SampleBuffer;
import org.csstudio.archive.engine.server.AbstractGroupResponse;

/** Provide web page with detail for one group in HTML.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HTMLGroupResponse extends AbstractGroupResponse
{
    /** Maximum text length of last value that's displayed */
    private static final int MAX_VALUE_DISPLAY = 60;

    public HTMLGroupResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        getParams(req, resp);

        HTMLWriter html = new HTMLWriter(resp,
                        "Archive Engine Group " + group_name);

        // Basic group info
        html.openTable(2, new String[]
        {
            Messages.HTTP_Status
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_State,
            group.isEnabled() ? Messages.HTTP_Enabled : Messages.HTTP_Disabled
        });
        final ArchiveChannel ena_channel = group.getEnablingChannel();
        if (ena_channel != null)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_EnablingChannel,
                HTMLWriter.makeLink("channel?name=" + ena_channel.getName(),
                         ena_channel.getName())
            });
        }
        html.closeTable();

        html.h2(Messages.HTTP_Channels);

        // HTML Table of all channels in the group
        html.openTable(1, new String[]
        {
            Messages.HTTP_Channel,
            Messages.HTTP_Connected,
            Messages.HTTP_Mechanism,
            Messages.HTTP_CurrentValue,
            Messages.HTTP_LastArchivedValue,
            Messages.HTTP_ReceivedValues,
            Messages.HTTP_QueueLen,
            Messages.HTTP_QueueAvg,
            Messages.HTTP_QueueMax,
            Messages.HTTP_QueueCapacity,
            Messages.HTTP_QueueOverruns,
        });
        final int channel_count = group.getChannelCount();
        for (int j=0; j<channel_count; ++j)
        {
            final ArchiveChannel channel = group.getChannel(j);
            final String connected = channel.isConnected()
            ? Messages.HTTP_Connected : HTMLWriter.makeRedText(Messages.HTTP_Disconnected);
            final SampleBuffer buffer = channel.getSampleBuffer();
            final BufferStats stats = buffer.getBufferStats();
            final int overrun_count = stats.getOverruns();
            String overruns = Integer.toString(overrun_count);
            if (overrun_count > 0)
                overruns = HTMLWriter.makeRedText(overruns);

            String current_value = channel.getCurrentValueAsString();
            if (current_value.length() > MAX_VALUE_DISPLAY)
                current_value = current_value.substring(0, MAX_VALUE_DISPLAY);
            String last_value = channel.getLastArchivedValueAsString();
            if (last_value.length() > MAX_VALUE_DISPLAY)
                last_value = last_value.substring(0, MAX_VALUE_DISPLAY);
            html.tableLine(new String[]
            {
                HTMLWriter.makeLink("channel?name=" + channel.getName(), channel.getName()),
                connected,
                channel.getMechanism(),
                current_value,
                last_value,
                Long.toString(channel.getReceivedValues()),
                Integer.toString(buffer.getQueueSize()),
                String.format("%.1f", stats.getAverageSize()),
                Integer.toString(stats.getMaxSize()),
                Integer.toString(buffer.getCapacity()),
                overruns,
            });
        }
        html.closeTable();

        html.close();
    }
}
