/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb.httpd;

import org.csstudio.jms2rdb.Application;

/** Servlet to 'stop' the JMS Log Tool.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StopServlet extends AbstractServlet
{
	private static final long serialVersionUID = 1L;

    private transient Application application;

    /** Initialize
     *  @param application Application to stop when servlet runs
     */
    public StopServlet(final Application application)
    {
        this.application = application;
    }

    /** {@inheritDoc} */
    @Override
    protected void fillBody(final HTMLWriter html)
    {
        html.h1("<h1>Stopping...</h1>\n");
        application.stop();
    }
}
