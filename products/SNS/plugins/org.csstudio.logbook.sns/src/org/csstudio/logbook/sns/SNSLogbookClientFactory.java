/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientFactory;

/** Implementation of the {@link LogbookClientFactory}.
 *  Plugin.xml declares this class as the logbook factory extension point.
 *  @author Kay Kasemir
 */
public class SNSLogbookClientFactory implements LogbookClientFactory
{
    @Override
    public LogbookClient getClient() throws Exception
    {
        return getClient(Preferences.getLogListUser(), Preferences.getLogListPassword());
    }

    @Override
    public LogbookClient getClient(final String username, final String password)
            throws Exception
    {
        return new SNSLogbookClient(Preferences.getURL(), username, password);
    }
}
