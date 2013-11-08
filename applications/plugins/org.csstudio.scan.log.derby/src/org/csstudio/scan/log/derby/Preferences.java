/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
	/** @return Derby database directory */
    public static String getDatabaseDirectory()
	{
    	String dir = "/tmp/scan_log_db";
    	final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs != null)
			dir = prefs.getString(Activator.ID, "database_directory", dir, null);
		return dir;
	}

	/** @return Derby network server port */
    public static int getServerPort()
	{
    	int port = 1527;
    	final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs != null)
			port = prefs.getInt(Activator.ID, "server_port", port, null);
		return port;
	}
}
