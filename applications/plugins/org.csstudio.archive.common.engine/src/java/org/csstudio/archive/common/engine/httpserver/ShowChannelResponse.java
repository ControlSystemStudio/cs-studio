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
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

/** Provide web page with detail for one channel.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ShowChannelResponse extends AbstractChannelResponse {

    private static String URL_BASE_PAGE;
    private static String URL_SHOW_CHANNEL_ACTION;
    static {
        URL_SHOW_CHANNEL_ACTION = "show";
        URL_BASE_PAGE = URL_CHANNEL_PAGE + "/" + URL_SHOW_CHANNEL_ACTION;
    }

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ShowChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final EpicsChannelName name = parseEpicsNameOrConfigureRedirectResponse(req, resp);
        if (name == null) {
            return;
        }
        final ArchiveChannelBuffer<?, ?> channel = getModel().getChannel(name.toString());
        if (channel == null) {
            resp.sendError(400, "Unknown channel " + name.toString());
            return;
        }

        // HTML table similar to group's list of channels
        final HTMLWriter html = new HTMLWriter(resp, "Archive Engine Channel");

        createChannelTable(channel, html);

        html.close();
    }


    private void createChannelTable(@Nonnull final ArchiveChannelBuffer<?, ?> channel,
                                    @Nonnull final HTMLWriter html) {
        html.openTable(2, new String[] {Messages.HTTP_CHANNEL_INFO});

        html.tableLine(new String[] {Messages.HTTP_CHANNEL, channel.getName()});
        html.tableLine(new String[] {
                           Messages.HTTP_STARTED,
                           channel.isStarted() ? Messages.HTTP_YES : HTMLWriter.makeRedText(Messages.HTTP_NO),
                       });
        final String connected = channel.isConnected()
                        ? Messages.HTTP_YES
                        : HTMLWriter.makeRedText(Messages.HTTP_NO);
        html.tableLine(new String[] {Messages.HTTP_CONNECTED, connected});
        html.tableLine(new String[] {Messages.HTTP_INTERNAL_STATE, channel.getInternalState()});
        html.tableLine(new String[] {Messages.HTTP_CURRENT_VALUE, getValueAsString(channel.getMostRecentSample())});

        final SampleBuffer<?, ?, ?> buffer = channel.getSampleBuffer();
        html.tableLine(new String[] {Messages.HTTP_QUEUELEN, Integer.toString(buffer.size())});
        final SampleBufferStatistics stats = buffer.getBufferStats();
        html.tableLine(new String[] {Messages.HTTP_COLUMN_QUEUEAVG, String.format("%.1f", stats.getAverageSize())});

        html.tableLine(new String[] {Messages.HTTP_COLUMN_QUEUEMAX, Integer.toString(stats.getMaxSize())});
        if (channel.isStarted()) {
            html.tableLine(new String[] {
                                         Messages.HTTP_STOP_CHANNEL,
                                         StopChannelResponse.linkTo(channel.getName()),
                                         });
        } else if (channel.isEnabled()) {
            html.tableLine(new String[] {
                    Messages.HTTP_ACTION,
                    StartChannelResponse.linkTo(channel.getName()),
            });
            html.tableLine(new String[] {
                    Messages.HTTP_ACTION,
                    PermanentDisableChannelResponse.linkTo(channel.getName()),
            });
        } else {
            html.tableLine(new String[] {
                    Messages.HTTP_ACTION,
                    StartChannelResponse.linkTo(channel.getName()),
            });
        }
        html.closeTable();
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name) {
        return linkTo(name, name);
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name, @Nonnull final String linkText) {
        return new Url(baseUrl()).with(PARAM_NAME, name).link(linkText);
    }
    @Nonnull
    public static String urlTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).url();
    }
}
