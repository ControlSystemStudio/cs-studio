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
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBuffer;
import org.csstudio.archive.common.engine.model.SampleBufferStatistics;

/** Provide web page with detail for one channel.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ChannelResponse extends AbstractResponse {
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    static final String URL_CHANNEL_PAGE = "channel";
    static final String PARAM_NAME = "name";

    ChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final String channelName = req.getParameter(PARAM_NAME);
        if (channelName == null) {
            resp.sendError(400, "Missing parameter '" + PARAM_NAME + "'.");
            return;
        }
        final ArchiveChannelBuffer<?, ?> channel = getModel().getChannel(channelName);
        if (channel == null) {
            resp.sendError(400, "Unknown channel " + channelName);
            return;
        }

        // HTML table similar to group's list of channels
        final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Channel");

        createChannelTable(channelName, channel, html);

        html.close();
    }


    private void createChannelTable(@Nonnull final String channelName,
                                    @Nonnull final ArchiveChannelBuffer<?, ?> channel,
                                    @Nonnull final HTMLWriter html) {
        html.openTable(2, new String[] {Messages.HTTP_CHANNEL_INFO});

        html.tableLine(new String[] {Messages.HTTP_CHANNEL, channelName});

        final String connected = channel.isConnected()
                        ? Messages.HTTP_YES
                        : HTMLWriter.makeRedText(Messages.HTTP_NO);
        html.tableLine(new String[] {Messages.HTTP_COLUMN_CONNECTED, connected});

        html.tableLine(new String[] {Messages.HTTP_INTERNAL_STATE, channel.getInternalState()});

        html.tableLine(new String[] {Messages.HTTP_CURRENT_VALUE, getValueAsString(channel.getMostRecentSample())});

        final SampleBuffer<?, ?, ?> buffer = channel.getSampleBuffer();
        html.tableLine(new String[] {Messages.HTTP_QUEUELEN, Integer.toString(buffer.size())});

        final SampleBufferStatistics stats = buffer.getBufferStats();
        html.tableLine(new String[] {Messages.HTTP_COLUMN_QUEUEAVG, String.format("%.1f", stats.getAverageSize())});

        html.tableLine(new String[] {Messages.HTTP_COLUMN_QUEUEMAX, Integer.toString(stats.getMaxSize())});

        html.closeTable();
    }
}
