/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.Messages;
import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.BufferStats;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBuffer;
import org.csstudio.domain.desy.time.IHasTimeStamp;

/**
 * Provide web page with detail for one group.
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
@SuppressWarnings("nls")
class GroupResponse extends AbstractResponse {

    private static final long serialVersionUID = 1L;

    /** Maximum text length of last value that's displayed */
    private static final int MAX_VALUE_DISPLAY = 60;

    GroupResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        // Locate the group
        final String groupName = req.getParameter("name");
        if (groupName == null) {
            resp.sendError(400, "Missing group name");
            return;
        }
        final ArchiveGroup group = _model.getGroup(groupName);
        if (group == null) {
            resp.sendError(400, "Unknown group " + groupName);
            return;
        }

        final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Group " + groupName);

        createBasicInfoTable(group, html);

        html.h2(Messages.HTTP_Channels + " (Last write time: " + _model.getLastWriteTime()  + ")");

        createChannelsTable(group, html);

        html.close();
    }

    private void createBasicInfoTable(@Nonnull final ArchiveGroup group,
                                      @Nonnull final HTMLWriter html) {
        // Basic group info
        html.openTable(2, new String[] {
            Messages.HTTP_Status
        });
        html.tableLine(new String[] {
            Messages.HTTP_State,
            group.isEnabled() ? Messages.HTTP_Enabled : Messages.HTTP_Disabled
        });
        html.closeTable();
    }

    private void createChannelsTable(@Nonnull final ArchiveGroup group,
                                     @Nonnull final HTMLWriter html) {
        // HTML Table of all channels in the group
        html.openTable(1, new String[] {
            Messages.HTTP_Channel,
            Messages.HTTP_Connected,
            Messages.HTTP_Mechanism,
            Messages.HTTP_CurrentValue,
            Messages.HTTP_TIMESTAMP,
            Messages.HTTP_ReceivedValues,
            Messages.HTTP_QueueLen,
            Messages.HTTP_QueueAvg,
            Messages.HTTP_QueueMax
        });
        for (final ArchiveChannel<?, ?> channel : group.getChannels()) {
            try {

            final String connected = channel.isConnected() ? Messages.HTTP_Connected :
                                                             HTMLWriter.makeRedText(Messages.HTTP_Disconnected);
            final SampleBuffer<?, ?, ?> buffer = channel.getSampleBuffer();
            final BufferStats stats = buffer.getBufferStats();

            String curVal = channel.getCurrentValueAsString();
            if (curVal.length() > MAX_VALUE_DISPLAY) {
                curVal = curVal.substring(0, MAX_VALUE_DISPLAY);
            }
            String lastVal = channel.getLastArchivedValue();
            if (lastVal.length() > MAX_VALUE_DISPLAY) {
                lastVal = lastVal.substring(0, MAX_VALUE_DISPLAY);
            }
            final IHasTimeStamp curValTimestamp = channel.getMostRecentValue();
            html.tableLine(new String[] {
                HTMLWriter.makeLink("channel?name=" + channel.getName(), channel.getName()),
                connected,
                channel.getMechanism(),
                curVal,
                curValTimestamp.getTimestamp().formatted(),
                Long.toString(channel.getReceivedValues()),
                Integer.toString(buffer.size()),
                String.format("%.1f", stats.getAverageSize()),
                Integer.toString(stats.getMaxSize()),
            });
            } catch (final Throwable t) {
                System.out.println("");
            }
        }
        html.closeTable();
    }
}
