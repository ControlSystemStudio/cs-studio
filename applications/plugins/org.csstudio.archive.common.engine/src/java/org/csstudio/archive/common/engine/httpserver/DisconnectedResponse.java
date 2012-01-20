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

/**
 * Provide web page with list of disconnected channels
 *  @author Kay Kasemir
 */
class DisconnectedResponse extends AbstractChannelResponse {

    private static String URL_BASE_PAGE;
    private static String URL_DISC_CHANNEL_ACTION;
    static {
        URL_DISC_CHANNEL_ACTION = "disconnected";
        URL_BASE_PAGE = URL_CHANNEL_PAGE + "/" + URL_DISC_CHANNEL_ACTION;
    }

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    DisconnectedResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_DISCONNECTED_TITLE);

        html.openTable(1, new String[] {"#", Messages.HTTP_CHANNEL, Messages.HTTP_COLUMN_GROUP});
        final int disconnected = createTableRows(html);
        html.closeTable();

        if (disconnected == 0) {
            html.h2("All channels are connected");
        }

        html.close();
    }

    private int createTableRows(@Nonnull final HTMLWriter html) {
        int disconnected = 0;
        for (final ArchiveGroup group : getModel().getGroups()) {
            for (final ArchiveChannelBuffer<?, ?> channel : group.getChannels()) {
                if (channel.isConnected()) {
                    continue;
                }
                ++disconnected;
                html.tableLine(new String[]
                                          {
                                           Integer.toString(disconnected),
                                           ShowChannelResponse.linkTo(channel.getName()),
                                           ShowGroupResponse.linkTo(group.getName()),
                                          } );
            }
        }
        return disconnected;
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }

    @Nonnull
    public static String linkTo(@Nonnull final String linkText) {
        return new Url(baseUrl()).link(linkText);
    }
    @Nonnull
    public static String linkTo() {
        return linkTo(baseUrl());
    }
}
