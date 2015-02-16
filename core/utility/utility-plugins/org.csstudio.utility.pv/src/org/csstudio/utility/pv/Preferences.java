/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preferences for Utility.PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Preference ID of the default PV type */
    final public static String DEFAULT_TYPE = "default_type";
    
    /** @return Default PV type from preferences */
    public static String getDefaultType()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Activator.ID, DEFAULT_TYPE, "ca", null);
    }
}
