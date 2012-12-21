/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server.httpd;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/** Servlet Helper
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ServletHelper
{
    /** Dump request headers 
     *  @param request
     */
    @SuppressWarnings("unchecked")
    public static void dumpHeaders(final HttpServletRequest request)
    {
        // TODO Get scan XML from request
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements())
        {
            String header = headers.nextElement();
            System.out.println(header + " = " + request.getHeader(header));
        }
    }
}
