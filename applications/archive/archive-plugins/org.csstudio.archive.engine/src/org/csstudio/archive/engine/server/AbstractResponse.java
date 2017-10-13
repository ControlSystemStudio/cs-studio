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

import org.csstudio.archive.engine.model.EngineModel;

/** Helper for creating web pages with consistent look (header, footer, ...)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class AbstractResponse
{
    /** Model from which to serve info */
    final protected EngineModel model;

    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected AbstractResponse(final EngineModel model)
    {
        this.model = model;
    }

    /** Derived class must implement this to provide page content.
     *  <p>
     *  Call <code>startHTML</code> once, then other print methods.
     *  @param req The request
     *  @param resp The response
     */
    abstract public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp)
       throws Exception;
}