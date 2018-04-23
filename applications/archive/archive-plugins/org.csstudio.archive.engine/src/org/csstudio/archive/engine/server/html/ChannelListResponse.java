/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.html;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveChannel;
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractResponse;

/** Provide web page with list of channels (by pattern).
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelListResponse extends AbstractResponse
{
    public ChannelListResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {   // Locate the group
        final String name = req.getParameter("name");
        if (name == null)
        {
            resp.sendError(400, "Missing 'name' parameter for channel name pattern");
            return;
        }
        final Pattern pattern = Pattern.compile(name);

        // HTML table similar to group's list of channels
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Channels for pattern '" + name + "'");

        html.openTable(1, new String[]
        {
            Messages.HTTP_Channel,
            Messages.HTTP_Group,
            Messages.HTTP_Connected,
            Messages.HTTP_InternalState,
            Messages.HTTP_Mechanism,
            Messages.HTTP_Enabled,
            Messages.HTTP_CurrentValue,
            Messages.HTTP_LastArchivedValue,
        });

        for (int i=0; i<model.getChannelCount(); ++i)
        {
            final ArchiveChannel channel = model.getChannel(i);
            // Filter by channel name pattern
            if (!pattern.matcher(channel.getName()).matches())
                continue;
            final StringBuilder groups = new StringBuilder();
            for (int g=0; g<channel.getGroupCount(); ++g)
            {
                if (g > 0)
                    groups.append(", ");
                final ArchiveGroup group = channel.getGroup(i);
                groups.append(HTMLWriter.makeLink(
                            "group?name=" + group.getName(), group.getName()));
            }
            html.tableLine(new String[]
            {
                HTMLWriter.makeLink("channel?name=" + channel.getName(),
                        channel.getName()),
                groups.toString(),
                channel.isConnected()
                    ? Messages.HTTP_Connected
                    : HTMLWriter.makeRedText(Messages.HTTP_Disconnected),
                channel.getInternalState(),
                channel.getMechanism(),
                channel.isEnabled()
                    ? Messages.HTTP_Enabled
                    : HTMLWriter.makeRedText(Messages.HTTP_Disabled),
                channel.getCurrentValueAsString(),
                channel.getLastArchivedValueAsString(),
            });
        }
        html.closeTable();
        html.close();
    }
}
