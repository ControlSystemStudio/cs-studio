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

        json.writeObjectKey(Messages.HTTP_Connected);
        json.write(channel.isConnected());
        json.listSeperator();

        json.writeObjectEntry(Messages.HTTP_InternalState, channel.getInternalState());
        json.writeObjectEntry(Messages.HTTP_Mechanism, channel.getMechanism());
        json.writeObjectEntry(Messages.HTTP_CurrentValue, channel.getCurrentValue());
        json.writeObjectEntry(Messages.HTTP_LastArchivedValue, channel.getLastArchivedValue());
        json.writeObjectEntry(Messages.HTTP_Enablement, channel.getEnablement().toString());

        json.writeObjectKey(Messages.HTTP_State);
        json.write(channel.isEnabled());
        json.listSeperator();

        SampleBuffer buffer = channel.getSampleBuffer();
        json.writeObjectKey(Messages.HTTP_QueueLen);
        json.write(buffer.getQueueSize());
        json.listSeperator();

        BufferStats stats = buffer.getBufferStats();
        json.writeObjectKey(Messages.HTTP_QueueAvg);
        json.write(stats.getAverageSize());
        json.listSeperator();

        json.writeObjectKey(Messages.HTTP_QueueMax);
        json.write(stats.getMaxSize());
        json.listSeperator();

        json.writeObjectKey(Messages.HTTP_QueueCapacity);
        json.write(buffer.getCapacity());
        json.listSeperator();

        json.writeObjectKey(Messages.HTTP_QueueOverruns);
        json.write(stats.getOverruns());

        json.closeObject();
        json.listSeperator();

        json.writeObjectKey("Group Membership");
        json.openObject();

        for (int i=0; i<channel.getGroupCount(); ++i)
        {
            final ArchiveGroup group = channel.getGroup(i);
            json.writeObjectKey(group.getName());
            json.write(group.isEnabled());
            if (i != channel.getGroupCount()-1) {
                json.listSeperator();
            }
        }
        json.closeObject();
        json.closeObject();
    }
}
