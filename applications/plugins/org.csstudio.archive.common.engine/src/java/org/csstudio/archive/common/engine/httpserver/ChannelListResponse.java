/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.EngineModel;

/** Provide web page with list of channels (by pattern).
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ChannelListResponse extends AbstractChannelResponse {

    private static String URL_BASE_PAGE;
    private static String URL_LIST_CHANNEL_ACTION;
    static {
        URL_LIST_CHANNEL_ACTION = "list";
        URL_BASE_PAGE = URL_CHANNEL_PAGE + "/" + URL_LIST_CHANNEL_ACTION;
    }
    private static final String PARAM_PATTERN = "pattern";

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ChannelListResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final String pat = req.getParameter(PARAM_PATTERN);
        if (pat == null) {
            resp.sendError(400, "Missing '" + PARAM_PATTERN + "' parameter for channel name pattern");
            return;
        }
        final Pattern pattern = Pattern.compile(pat);

        // HTML table similar to group's list of channels
        final HTMLWriter html = new HTMLWriter(resp, "Archive Channels for pattern '" + pat + "'");

        createChannelListTable(pattern, html);
        html.close();
    }

    private void createChannelListTable(@Nonnull final Pattern pattern,
                                        @Nonnull final HTMLWriter html) {
        html.openTable(1, new String[] {
            Messages.HTTP_CHANNEL,
            Messages.HTTP_STARTED,
            Messages.HTTP_CONNECTED,
            Messages.HTTP_CURRENT_VALUE,
        });

        for (final ArchiveChannelBuffer<?, ?> channel : getModel().getChannels()) {
            final String channelName = channel.getName();
            // Filter by channel name pattern
            if (!pattern.matcher(channelName).matches()) {
                continue;
            }
            html.tableLine(new String[]{
                                        ShowChannelResponse.linkTo(channelName),
                                        channel.isStarted() ? Messages.HTTP_YES :
                                                              HTMLWriter.makeRedText(Messages.HTTP_NO),
                                        channel.isConnected() ? Messages.HTTP_YES :
                                                                HTMLWriter.makeRedText(Messages.HTTP_NO),
                                        getValueAsString(channel.getMostRecentSample()),
                                       });
        }
        html.closeTable();
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }
}
