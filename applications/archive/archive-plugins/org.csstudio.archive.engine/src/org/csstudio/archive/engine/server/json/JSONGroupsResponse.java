/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveChannel;
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.BufferStats;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractResponse;

/** Provide web page with basic info for all the groups in JSON.
 *  @author Dominic Oram
 */
@SuppressWarnings("nls")
public class JSONGroupsResponse extends AbstractResponse
{
    public JSONGroupsResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final JSONRoot json = new JSONRoot(resp);

        JSONList groups = new JSONList();

        final int group_count = model.getGroupCount();
        int total_channels = 0;
        int total_connect = 0;
        long total_received_values = 0;
        // Per group objects
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

            JSONObject JSONgroup = new JSONObject();

            JSONgroup.writeObjectEntry(Messages.HTTP_Group, group.getName());
            JSONgroup.writeObjectEntry(Messages.HTTP_Enabled, group.isEnabled());
            JSONgroup.writeObjectEntry(Messages.HTTP_ChannelCount, channel_count);
            JSONgroup.writeObjectEntry(Messages.HTTP_Connected, connect_count);
            JSONgroup.writeObjectEntry(Messages.HTTP_ReceivedValues, received_values);
            JSONgroup.writeObjectEntry(Messages.HTTP_QueueAvg, queue_avg);
            JSONgroup.writeObjectEntry(Messages.HTTP_QueueMax, queue_max);

            groups.addObjectToList(JSONgroup);
        }

        // 'Total' object
        JSONObject total = new JSONObject();
        total.writeObjectEntry(Messages.HTTP_Group, Messages.HTTP_Total);
        total.writeObjectEntry(Messages.HTTP_ChannelCount, total_channels);
        total.writeObjectEntry(Messages.HTTP_Connected, total_connect);
        total.writeObjectEntry(Messages.HTTP_ReceivedValues, total_received_values);
        groups.addObjectToList(total);

        json.writeObjectEntry("Archive Engine Groups", groups);;
        json.close();
    }
}
