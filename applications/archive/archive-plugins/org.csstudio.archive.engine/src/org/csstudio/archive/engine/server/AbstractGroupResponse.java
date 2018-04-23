/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.EngineModel;

/**
 * Provide web page with detail for one group.
 * @author Dominic Oram
 */
public abstract class AbstractGroupResponse extends AbstractResponse {

    protected String group_name;
    protected ArchiveGroup group;

    public AbstractGroupResponse(final EngineModel model)
    {
        super(model);
    }

    protected void getParams(final HttpServletRequest req,
            final HttpServletResponse resp) throws Exception {

        // Locate the group
        group_name = req.getParameter("name");
        if (group_name == null)
        {
            resp.sendError(400, "Missing group name");
            return;
        }
        group = model.getGroup(group_name);
        if (group == null)
        {
            resp.sendError(400, "Unknown group " + group_name);
            return;
        }
    }

}
