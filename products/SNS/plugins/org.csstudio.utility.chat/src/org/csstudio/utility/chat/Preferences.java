/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Chat preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
	final public static String GROUP = "group";
	final public static String CHAT_SERVER = "chat_server";

	public static String getChatServer()
    {
		return getString(CHAT_SERVER, "localhost");
    }

    public static String getGroup()
    {
		return getString(GROUP, "css@conference.localhost");
    }
    
    private static String getString(final String pref, final String default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
        	return default_value;
        return prefs.getString(Activator.ID, pref, default_value, null);
    }
}
