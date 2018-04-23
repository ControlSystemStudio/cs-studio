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
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.BufferStats;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractResponse;

/** Provide web page with basic info for all the groups in HTML.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HTMLGroupsResponse extends AbstractResponse
{
    public HTMLGroupsResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Groups");

        html.openTable(1, new String[]
        {
            Messages.HTTP_Group,
            Messages.HTTP_Enabled,
            Messages.HTTP_ChannelCount,
            Messages.HTTP_Connected,
            Messages.HTTP_ReceivedValues,
            Messages.HTTP_QueueAvg,
            Messages.HTTP_QueueMax,
        });
        final int group_count = model.getGroupCount();
        int total_channels = 0;
        int total_connect = 0;
        long total_received_values = 0;
        // Per group lines
        for (int i=0; i<group_count; ++i)
        {
            final ArchiveGroup group = model.getGroup(i);
            final int channel_count = group.getChannelCount();
            int connect_count = 0;
            double queue_avg = 0;
            int queue_max = 0;
            long received_values = 0;
            for (int j=0; j<channel_count; ++j)
            {
                final ArchiveChannel channel = group.getChannel(j);
                if (channel.isConnected())
                    ++connect_count;
                received_values += channel.getReceivedValues();
                final BufferStats stats =
                    channel.getSampleBuffer().getBufferStats();
                queue_avg += stats.getAverageSize();
                if (queue_max < stats.getMaxSize())
                    queue_max = stats.getMaxSize();
            }
            if (channel_count > 0)
                queue_avg /= channel_count;
            total_channels += channel_count;
            total_connect += connect_count;
            total_received_values += received_values;

            final String connected = (channel_count == connect_count)
                ? Integer.toString(connect_count)
                : HTMLWriter.makeRedText(Integer.toString(connect_count));

            html.tableLine(new String[]
            {
                HTMLWriter.makeLink("group?name=" + group.getName(), group.getName()),
                group.isEnabled()
                  ? Messages.HTTP_Enabled : HTMLWriter.makeRedText(Messages.HTTP_Disabled),
                Integer.toString(channel_count),
                connected,
                Long.toString(received_values),
                String.format("%.1f", queue_avg),
                Integer.toString(queue_max),
            });
        }
        // 'Total' line
        final String connected = (total_channels == total_connect)
            ? Integer.toString(total_connect)
            : HTMLWriter.makeRedText(Integer.toString(total_connect));
        html.tableLine(new String[]
        {
            Messages.HTTP_Total,
            "",
            Integer.toString(total_channels),
            connected,
            Long.toString(total_received_values),
            "",
            "",
        });
        html.closeTable();

        html.close();
    }
}
