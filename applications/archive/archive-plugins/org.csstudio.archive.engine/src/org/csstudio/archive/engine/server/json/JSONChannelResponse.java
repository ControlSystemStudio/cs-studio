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

        json.writeObjectKey(Messages.HTTP_ChannelInfo);
        json.openObject();

        json.writeObjectEntry(Messages.HTTP_Channel, channel_name);

        json.writeObjectEntry(Messages.HTTP_Connected, channel.isConnected());

        json.writeObjectEntry(Messages.HTTP_InternalState, channel.getInternalState());
        json.writeObjectEntry(Messages.HTTP_Mechanism, channel.getMechanism());
        json.writeObjectEntry(Messages.HTTP_CurrentValue, channel.getCurrentValue());
        json.writeObjectEntry(Messages.HTTP_LastArchivedValue, channel.getLastArchivedValue());
        json.writeObjectEntry(Messages.HTTP_Enablement, channel.getEnablement().toString());

        json.writeObjectEntry(Messages.HTTP_State, channel.isEnabled());

        SampleBuffer buffer = channel.getSampleBuffer();
        json.writeObjectEntry(Messages.HTTP_QueueLen, buffer.getQueueSize());

        BufferStats stats = buffer.getBufferStats();
        json.writeObjectEntry(Messages.HTTP_QueueAvg, stats.getAverageSize());
        json.writeObjectEntry(Messages.HTTP_QueueMax, stats.getMaxSize());
        json.writeObjectEntry(Messages.HTTP_QueueCapacity, buffer.getCapacity());
        json.writeObjectEntry(Messages.HTTP_QueueOverruns, stats.getOverruns());

        json.closeObject();
        json.listSeperator();

        json.writeObjectKey("Group Membership");
        json.openObject();

        for (int i=0; i<channel.getGroupCount(); ++i)
        {
            final ArchiveGroup group = channel.getGroup(i);
            json.writeObjectEntry(group.getName(), group.isEnabled());
        }
        json.closeObject();
        json.closeObject();
    }
}
