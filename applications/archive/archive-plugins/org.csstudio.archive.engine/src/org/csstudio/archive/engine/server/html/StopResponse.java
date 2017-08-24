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

import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractResponse;

/** Provide web page for engine shutdown request.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StopResponse extends AbstractResponse
{
    public StopResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Shutdown");

        html.text("Engine will shut down....");
        model.requestStop();

        html.close();
    }
}
