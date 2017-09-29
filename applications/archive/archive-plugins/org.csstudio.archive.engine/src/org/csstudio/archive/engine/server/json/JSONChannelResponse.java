/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractChannelResponse;

/** Provide JSON with detail for one channel.
 *  @author Dominic Oram
 */
@SuppressWarnings("nls")
public class JSONChannelResponse extends AbstractChannelResponse
{
    public JSONChannelResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        getParams(req, resp);

        final JSONRoot json = new JSONRoot(resp);

        json.writeObjectEntry(Messages.HTTP_ChannelInfo, JSONHelper.createChannelObject(channel));

        JSONObject groups = new JSONObject();

        for (int i=0; i<channel.getGroupCount(); ++i)
        {
            final ArchiveGroup group = channel.getGroup(i);
            groups.writeObjectEntry(group.getName(), group.isEnabled());
        }

        json.writeObjectEntry("Group Membership", groups);

        json.close();
    }
}
