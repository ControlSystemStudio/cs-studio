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

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBuffer;
import org.csstudio.archive.common.engine.model.SampleBufferStatistics;
import org.csstudio.domain.desy.system.ISystemVariable;

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
        final ArchiveGroup group = getModel().getGroup(groupName);
        if (group == null) {
            resp.sendError(400, "Unknown group " + groupName);
            return;
        }

        final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Group " + groupName);

        createBasicInfoTable(group, html);

        html.h2(Messages.HTTP_CHANNELS + " (Last write time: " + getModel().getLastWriteTime()  + ")");

        createChannelsTable(group, html);

        html.close();
    }

    private void createBasicInfoTable(@Nonnull final ArchiveGroup group,
                                      @Nonnull final HTMLWriter html) {
        // Basic group info
        html.openTable(2, new String[] {
            Messages.HTTP_STATUS,
        });
        html.tableLine(new String[] {
            Messages.HTTP_STARTED,
            group.isStarted() ? Messages.HTTP_YES : HTMLWriter.makeRedText(Messages.HTTP_NO),
        });
        html.closeTable();
    }

    private void createChannelsTable(@Nonnull final ArchiveGroup group,
                                     @Nonnull final HTMLWriter html) {
        // HTML Table of all channels in the group
        html.openTable(1, new String[] {
            Messages.HTTP_CHANNEL,
            Messages.HTTP_COLUMN_CONNECTED,
            Messages.HTTP_CURRENT_VALUE,
            Messages.HTTP_TIMESTAMP,
            Messages.HTTP_COLUMN_RECEIVEDVALUES,
            Messages.HTTP_QUEUELEN,
            Messages.HTTP_COLUMN_QUEUEAVG,
            Messages.HTTP_COLUMN_QUEUEMAX,
        });
        for (final ArchiveChannelBuffer<?, ?> channel : group.getChannels()) {
            try {

            final String connected = channel.isConnected() ? Messages.HTTP_YES :
                                                             HTMLWriter.makeRedText(Messages.HTTP_NO);
            final SampleBuffer<?, ?, ?> buffer = channel.getSampleBuffer();
            final SampleBufferStatistics stats = buffer.getBufferStats();
            final ISystemVariable<?> mostRecentSample = channel.getMostRecentSample();

            final String curVal = limitLength(getValueAsString(mostRecentSample), MAX_VALUE_DISPLAY);

            final String curValTimestamp =
                mostRecentSample != null ? mostRecentSample.getTimestamp().formatted() :
                                          "null";

            html.tableLine(new String[] {
                HTMLWriter.makeLink(ShowChannelResponse.getUrl() + "?" + ShowChannelResponse.PARAM_NAME + "=" + channel.getName(), channel.getName()),
                connected,
                curVal,
                curValTimestamp,
                Long.toString(channel.getReceivedValues()),
                Integer.toString(buffer.size()),
                String.format("%.1f", stats.getAverageSize()),
                Integer.toString(stats.getMaxSize()),
            });
            } catch (final Throwable t) {
                System.out.println(channel.getName());
            }
        }
        html.closeTable();
    }
}
