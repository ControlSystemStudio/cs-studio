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

import com.google.common.base.Strings;

/**
 * Provide web page with detail for one group.
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
@SuppressWarnings("nls")
class ShowGroupResponse extends AbstractGroupResponse {

    private static String URL_SHOW_GROUP_ACTION;
    private static String URL_SHOW_GROUP_PAGE;
    static {
        URL_SHOW_GROUP_ACTION = "show";
        URL_SHOW_GROUP_PAGE = URL_GROUP_PAGE + "/" + URL_SHOW_GROUP_ACTION;
    }

    /** Maximum text length of last value that's displayed */
    private static final int MAX_VALUE_DISPLAY = 60;

    private static final long serialVersionUID = 5113743574714463726L;

    ShowGroupResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final String name = req.getParameter(PARAM_NAME);
        if (Strings.isNullOrEmpty(name)) {
            redirectToErrorPage(resp, "Required parameter '" + PARAM_NAME + "' is either null or empty!");
            return;
        }

        final ArchiveGroup group = getModel().getGroup(name);
        if (group == null) {
            resp.sendError(400, "Unknown group " + name);
            return;
        }

        final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Group " + name);

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
            Messages.HTTP_STARTED,
            Messages.HTTP_CONNECTED,
            Messages.HTTP_CURRENT_VALUE,
            Messages.HTTP_TIMESTAMP,
            Messages.HTTP_COLUMN_RECEIVEDVALUES,
            Messages.HTTP_QUEUELEN,
            Messages.HTTP_COLUMN_QUEUEAVG,
            Messages.HTTP_COLUMN_QUEUEMAX,
        });
        for (final ArchiveChannelBuffer<?, ?> channel : group.getChannels()) {
            try {

                final String started = channel.isStarted() ? Messages.HTTP_YES :
                                                             HTMLWriter.makeRedText(Messages.HTTP_NO);
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
                            ShowChannelResponse.linkTo(channel.getName()),
                            started,
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

    @Nonnull
    public static String baseUrl() {
        return URL_SHOW_GROUP_PAGE;
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).link(name);
    }
    @Nonnull
    public static String urlTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).url();
    }
}
