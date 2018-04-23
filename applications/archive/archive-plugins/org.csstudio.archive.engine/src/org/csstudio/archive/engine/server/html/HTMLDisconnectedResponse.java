/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveChannel;
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractResponse;

/** Provide web page with list of disconnected channels in HTML
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HTMLDisconnectedResponse extends AbstractResponse
{
    public HTMLDisconnectedResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_DisconnectedTitle);
        html.openTable(1, new String[] { "#", Messages.HTTP_Channel, Messages.HTTP_Group });

        final int group_count = model.getGroupCount();
        int disconnected = 0;
        for (int i=0; i<group_count; ++i)
        {
            final ArchiveGroup group = model.getGroup(i);
            final int channel_count = group.getChannelCount();
            for (int j=0; j<channel_count; ++j)
            {
                final ArchiveChannel channel = group.getChannel(j);
                if (channel.isConnected())
                    continue;
                ++disconnected;
                html.tableLine(new String[]
                {
                    Integer.toString(disconnected),
                    HTMLWriter.makeLink("channel?name=" + channel.getName(), channel.getName()),
                    HTMLWriter.makeLink("group?name=" + group.getName(), group.getName()),
                } );
            }
        }
        html.closeTable();

        if (disconnected == 0)
            html.h2("All channels are connected");

        html.close();
    }
}
