/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server.app;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Scan server preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
	/** @return Path to the default device context initialization file */
	public static String getDeviceConfigPath()
	{
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getString(Activator.ID, "device_config", "examples/devices.xml", null);
	}
}
