/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/** Helper for creating JSON for a servlet response.
 *  @author Dominic Oram
 */

public class JSONWriter extends JSONObject {
    boolean isFirstItem = true;

    /**
     * Creates a JSON Object that will write to a HttpServletResponse.
     * @param resp The response to write to.
     * @throws IOException if an i/o exception occurred from the HttpServletResponse
     */
    public JSONWriter(final HttpServletResponse resp)
        throws IOException
    {
        super(resp.getWriter());
        resp.setContentType("application/json");
    }
}
