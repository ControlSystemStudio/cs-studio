/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rack.sns;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

@SuppressWarnings("nls")
public class Preferences
{
    final public static String DEFAULT_URL = "jdbc:oracle:thin:@//myhost:1521/orcl";

    public static String getURL()
    {

        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return DEFAULT_URL;
        return service.getString(Activator.ID, "rdb_url", DEFAULT_URL, null);
    }

    public static String getUser()
    {
        return "sns_reports";
    }

    public static String getPassword()
    {
        return "sns";
    }
}
