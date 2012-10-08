/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

/** Provide web page with list of channels (by pattern).
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class ImportResultResponse extends AbstractChannelResponse {


    private static final String URL_IMPORT_RESULT = "/importresult";

    private static final long serialVersionUID = 1L;

    private static List<EpicsChannelName> _configureChannelsFromFile;

    ImportResultResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
            // HTML table similar to group's list of channels
            final HTMLWriter html = new HTMLWriter(resp, "Channel import result");

            createChannelListTable(html);
            html.close();

    }

    private void createChannelListTable(@Nonnull final HTMLWriter html) {
        html.openTable(1, new String[] {
            Messages.HTTP_CHANNEL,
            Messages.HTTP_STARTED,
            Messages.HTTP_CONNECTED,
            Messages.HTTP_CURRENT_VALUE,
        });
        ArchiveChannelBuffer<?, ?> channel;

        for (final EpicsChannelName channelName : _configureChannelsFromFile) {
            channel = getModel().getChannel(channelName.toString());
            if (channel != null) {
            html.tableLine(new String[]{
                                        ShowChannelResponse.linkTo(channelName.toString()),
                                        channel.isStarted() ? Messages.HTTP_YES :
                                                              HTMLWriter.makeRedText(Messages.HTTP_NO),
                                        channel.isConnected() ? Messages.HTTP_YES :
                                                                HTMLWriter.makeRedText(Messages.HTTP_NO),
                                        getValueAsString(channel.getMostRecentSample()),
                                       });
            } else {
                html.tableLine(new String[]{
                        HTMLWriter.makeRedText(channelName.toString()),
                        "",
                          "",
                              ""});

            }
        }
        html.closeTable();
    }

    public static void setResult(final List<EpicsChannelName> configureChannelsFromFile) {
        _configureChannelsFromFile = configureChannelsFromFile;
    }

    @Nonnull
    public static String baseUrl() {
        return URL_IMPORT_RESULT;
    }
}
