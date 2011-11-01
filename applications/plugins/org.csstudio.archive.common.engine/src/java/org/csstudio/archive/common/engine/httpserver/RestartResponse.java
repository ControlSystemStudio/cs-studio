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
import org.csstudio.archive.common.engine.service.IServiceProvider;

/** Provide web page for engine restart request.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class RestartResponse extends AbstractResponse {

    private static final String URL_BASE_PAGE = "/restart";

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    RestartResponse(@Nonnull final EngineModel model,
                    @Nonnull final IServiceProvider provider) {
        super(model, provider);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Restart");

        html.text("Engine will restart....");
        getModel().requestRestart();

        html.close();
    }


    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }
}
