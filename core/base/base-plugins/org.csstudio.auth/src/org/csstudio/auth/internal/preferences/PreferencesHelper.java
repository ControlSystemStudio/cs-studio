/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.auth.internal.preferences;

import org.csstudio.auth.internal.AuthActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preference settings.
 * 
 *  See preferences.ini for details on the available settings
 *  @author Xihui Chen
 */
public class PreferencesHelper
{
	public enum SecureStorageLocation {
		INSTALL_LOCATION,
		CONFIGURATION_LOCATION;
	}
	
	public final static String SECURE_STORAGE_LOCATION = "secure_storage_location"; //$NON-NLS-1$
     private static SecureStorageLocation secureStorageLocation;

    static
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            secureStorageLocation = SecureStorageLocation.CONFIGURATION_LOCATION;
        else{
            secureStorageLocation = SecureStorageLocation.valueOf(prefs.getString(
            		AuthActivator.ID, SECURE_STORAGE_LOCATION,
            		SecureStorageLocation.CONFIGURATION_LOCATION.name(), null));
        }
    }

    public static SecureStorageLocation getSecureStorageLocation()
    {
    	return secureStorageLocation;
    }
}
