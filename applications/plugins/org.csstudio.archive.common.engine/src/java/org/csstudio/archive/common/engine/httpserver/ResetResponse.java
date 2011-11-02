/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.EngineModel;

/**
 * Provide web page to reset engine statistics.
 *  @author Kay Kasemir
 */
class ResetResponse extends AbstractResponse {

    private static final String URL_BASE_PAGE = "/reset";

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    ResetResponse(@Nonnull final EngineModel model,
                  @Nonnull final String adminParamKey,
                  @Nonnull final String admingParamValue) {
        super(model, adminParamKey, admingParamValue);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Reset");

        html.text("Engine statistics are reset");
        getModel().resetStats();

        html.close();
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }
}
