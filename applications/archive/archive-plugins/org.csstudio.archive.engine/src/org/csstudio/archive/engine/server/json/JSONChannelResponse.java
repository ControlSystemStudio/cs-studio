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
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.BufferStats;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.model.SampleBuffer;
import org.csstudio.archive.engine.server.AbstractChannelResponse;
import org.csstudio.archive.vtype.StringVTypeFormat;

/** Provide JSON with detail for one channel.
 *  @author Dominic Oram
 */
@SuppressWarnings("nls")
public class JSONChannelResponse extends AbstractChannelResponse
{
    public JSONChannelResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        getParams(req, resp);

        final JSONWriter json = new JSONWriter(resp);

        JSONObject channelInfo = new JSONObject();

        channelInfo.writeObjectEntry(Messages.HTTP_Channel, channel_name);

        channelInfo.writeObjectEntry(Messages.HTTP_Connected, channel.isConnected());

        channelInfo.writeObjectEntry(Messages.HTTP_InternalState, channel.getInternalState());
        channelInfo.writeObjectEntry(Messages.HTTP_Mechanism, channel.getMechanism());
        channelInfo.writeObjectEntry(Messages.HTTP_CurrentValue, channel.getCurrentValue(new StringVTypeFormat()));
        channelInfo.writeObjectEntry(Messages.HTTP_LastArchivedValue, channel.getLastArchivedValue(new StringVTypeFormat()));
        channelInfo.writeObjectEntry(Messages.HTTP_Enablement, channel.getEnablement().toString());

        channelInfo.writeObjectEntry(Messages.HTTP_State, channel.isEnabled());

        SampleBuffer buffer = channel.getSampleBuffer();
        channelInfo.writeObjectEntry(Messages.HTTP_QueueLen, buffer.getQueueSize());

        BufferStats stats = buffer.getBufferStats();
        channelInfo.writeObjectEntry(Messages.HTTP_QueueAvg, stats.getAverageSize());
        channelInfo.writeObjectEntry(Messages.HTTP_QueueMax, stats.getMaxSize());
        channelInfo.writeObjectEntry(Messages.HTTP_QueueCapacity, buffer.getCapacity());
        channelInfo.writeObjectEntry(Messages.HTTP_QueueOverruns, stats.getOverruns());

        json.writeObjectEntry(Messages.HTTP_ChannelInfo, channelInfo);

        JSONObject groups = new JSONObject();

        for (int i=0; i<channel.getGroupCount(); ++i)
        {
            final ArchiveGroup group = channel.getGroup(i);
            groups.writeObjectEntry(group.getName(), group.isEnabled());
        }

        json.writeObjectEntry("Group Membership", groups);

        json.close();
    }
}
