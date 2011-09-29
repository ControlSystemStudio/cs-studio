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
class ChannelListResponse extends AbstractResponse {
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ChannelListResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final String name = req.getParameter("name");
        if (name == null) {
            resp.sendError(400, "Missing 'name' parameter for channel name pattern");
            return;
        }
        final Pattern pattern = Pattern.compile(name);

        // HTML table similar to group's list of channels
        final HTMLWriter html = new HTMLWriter(resp, "Archive Channels for pattern '" + name + "'");

        createChannelListTable(pattern, html);
        html.close();
    }

    private void createChannelListTable(@Nonnull final Pattern pattern,
                                        @Nonnull final HTMLWriter html) {
        html.openTable(1, new String[] {
            Messages.HTTP_CHANNEL,
            Messages.HTTP_YES,
            Messages.HTTP_INTERNAL_STATE,
            //Messages.HTTP_Mechanism,
            //Messages.HTTP_Enabled,
            Messages.HTTP_CURRENT_VALUE,
            Messages.HTTP_LAST_ARCHIVED_VALUE,
        });

        for (final ArchiveChannelBuffer<?, ?> channel : getModel().getChannels()) {
            // Filter by channel name pattern
            if (!pattern.matcher(channel.getName()).matches()) {
                continue;
            }
//            final List<String> groupNamesWithLinks = new ArrayList<String>();
//            for (final ArchiveGroup group : channel.getGroups()) {
//                groupNamesWithLinks.add(HTMLWriter.makeLink("group?name=" + group.getName(), group.getName()));
//            }
            html.tableLine(new String[]{
                                        HTMLWriter.makeLink("channel?name=" + channel.getName(), channel.getName()),
                                        //Joiner.on(", ").join(groupNamesWithLinks),
                                        channel.isConnected() ? Messages.HTTP_YES :
                                                                HTMLWriter.makeRedText(Messages.HTTP_NO),
                                                                channel.getInternalState(),
                                            //channel.getMechanism(),
                                            //                                       channel.isEnabled() ? Messages.HTTP_Enabled :
                                            //                                                             HTMLWriter.makeRedText(Messages.HTTP_Disabled),
                                        getValueAsString(channel.getMostRecentSample()),
                                        getValueAsString(channel.getLastArchivedSample()),
                                       });
        }
        html.closeTable();
    }
}
