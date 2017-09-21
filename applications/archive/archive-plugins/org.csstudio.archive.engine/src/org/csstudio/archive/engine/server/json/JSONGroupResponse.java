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
import org.csstudio.archive.engine.model.BufferStats;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.model.SampleBuffer;
import org.csstudio.archive.engine.server.AbstractGroupResponse;
import org.csstudio.archive.vtype.StringVTypeFormat;

/** Provide web page with detail for one group in JSON.
 *  @author Dominic Oram
 */
@SuppressWarnings("nls")
public class JSONGroupResponse extends AbstractGroupResponse
{
    public JSONGroupResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        getParams(req, resp);

        JSONWriter json = new JSONWriter(resp);

        // Basic group info
        json.writeObjectEntry(Messages.HTTP_Enabled, group.isEnabled());

        final ArchiveChannel ena_channel = group.getEnablingChannel();
        if (ena_channel != null)
        {
            json.writeObjectEntry(Messages.HTTP_EnablingChannel, ena_channel.getName());
        }

        // JSON object of all channels in the group
        JSONList channels = new JSONList();

        final int channel_count = group.getChannelCount();
        for (int j=0; j<channel_count; ++j)
        {
            final ArchiveChannel channel = group.getChannel(j);
            JSONObject JSONchannel = new JSONObject();
            JSONchannel.writeObjectEntry(Messages.HTTP_Channel, channel.getName());
            JSONchannel.writeObjectEntry(Messages.HTTP_Connected, channel.isConnected());
            JSONchannel.writeObjectEntry(Messages.HTTP_Mechanism, channel.getMechanism());
            JSONchannel.writeObjectEntry(Messages.HTTP_CurrentValue, channel.getCurrentValue(new StringVTypeFormat()));
            JSONchannel.writeObjectEntry(Messages.HTTP_LastArchivedValue, channel.getLastArchivedValue(new StringVTypeFormat()));
            JSONchannel.writeObjectEntry(Messages.HTTP_ReceivedValues, channel.getReceivedValues());

            final SampleBuffer buffer = channel.getSampleBuffer();
            final BufferStats stats = buffer.getBufferStats();
            JSONchannel.writeObjectEntry(Messages.HTTP_QueueLen, buffer.getQueueSize());
            JSONchannel.writeObjectEntry(Messages.HTTP_QueueAvg, stats.getAverageSize());
            JSONchannel.writeObjectEntry(Messages.HTTP_QueueMax, stats.getMaxSize());
            JSONchannel.writeObjectEntry(Messages.HTTP_QueueCapacity, buffer.getCapacity());
            JSONchannel.writeObjectEntry(Messages.HTTP_QueueOverruns, stats.getOverruns());

            channels.addObjectToList(JSONchannel);
        }

        json.writeObjectEntry(Messages.HTTP_Channels, channels);
        json.close();
    }
}
