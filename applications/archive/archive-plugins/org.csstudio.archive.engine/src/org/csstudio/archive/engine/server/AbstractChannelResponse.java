/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.ArchiveChannel;
import org.csstudio.archive.engine.model.EngineModel;

/** Provide web page with detail for one channel.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class AbstractChannelResponse extends AbstractResponse
{
    protected String channel_name;
    protected ArchiveChannel channel;

    public AbstractChannelResponse(final EngineModel model)
    {
        super(model);
    }

    protected void getParams(final HttpServletRequest req,
            final HttpServletResponse resp) throws Exception {
        channel_name = req.getParameter("name");
        if (channel_name == null)
        {
            resp.sendError(400, "Missing channel name");
            return;
        }
        channel = model.getChannel(channel_name);
        if (channel == null)
        {
            resp.sendError(400, "Unknown channel " + channel_name);
            return;
        }
    }
}
