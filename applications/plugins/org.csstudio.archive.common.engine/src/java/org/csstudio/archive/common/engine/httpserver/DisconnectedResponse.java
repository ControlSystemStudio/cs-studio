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

import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;

/**
 * Provide web page with list of disconnected channels
 *  @author Kay Kasemir
 */
class DisconnectedResponse extends AbstractResponse {
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
            for (final ArchiveChannel<?,?> channel : group.getChannels()) {
                if (channel.isConnected()) {
                    continue;
                }
                ++disconnected;
                html.tableLine(new String[]
                                          {
                                           Integer.toString(disconnected),
                                           HTMLWriter.makeLink("channel?name=" + channel.getName(), channel.getName()),
                                           HTMLWriter.makeLink("group?name=" + group.getName(), group.getName()),
                                          } );
            }
        }
        return disconnected;
    }
}
