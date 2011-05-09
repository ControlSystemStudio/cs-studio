/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.platform.logging.CentralLogger;

/** Helper for creating web pages with consistent look (header, footer, ...)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract class AbstractResponse extends HttpServlet {
    /** Required by Serializable */
    private static final long serialVersionUID = 1L;
    /** Model from which to serve info */
    private final EngineModel _model;


    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected AbstractResponse(@Nonnull final EngineModel model) {
        this._model = model;
    }

    @Nonnull
    protected EngineModel getModel() {
        return _model;
    }

    /** {@inheritDoc} */
    @Override
    protected void doGet(@Nonnull final HttpServletRequest req,
                         @Nonnull final HttpServletResponse resp)
                         throws ServletException, IOException {
        try {
            fillResponse(req, resp);
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (resp.isCommitted()) {
                CentralLogger.getInstance().getLogger(this).warn("HTTP Server exception", ex);
                return;
            }
            resp.sendError(400, "HTTP Server exception" + ex.getMessage());
        }
    }

    /** Derived class must implement this to provide page content.
     *  <p>
     *  Call <code>startHTML</code> once, then other print methods.
     *  @param req The request
     *  @param resp The response
     */
    protected abstract void fillResponse(@Nonnull final HttpServletRequest req,
                                         @Nonnull final HttpServletResponse resp)
       throws Exception;

    @Nonnull
    protected String getValueAsString(@CheckForNull final ISystemVariable<?> var) {
        if (var == null) {
            return "null";
        }
        return var.getData().toString();
    }

    @Nonnull
    protected String limitLength(@Nonnull final String valueAsString, final int maxValueDisplay) {
        return valueAsString.substring(0, Math.min(valueAsString.length(), maxValueDisplay));
    }

}
