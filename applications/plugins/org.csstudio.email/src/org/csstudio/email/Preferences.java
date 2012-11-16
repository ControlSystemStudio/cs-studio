/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preference settings.
 *
 *  See preferences.ini for details on the available settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String SMTP_HOST = "smtp_host";
    final public static String SMTP_SENDER = "smtp_sender";

    /** @return SMTP URL */
    public static String getSMTP_Host()
    {
        String host = "localhost";
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            host = service.getString(Activator.ID, SMTP_HOST, host, null);
        return host;
    }
    
    /** @return SMTP URL */
    public static String getSMTP_Sender()
    {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service != null)
			return service.getString(Activator.ID, SMTP_SENDER, null, null);
		return null;
    }
}
