/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph;

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
    // useAdvancedGraphics() is called from many drawing operations, so
    // only determine it once
    private static boolean use_advanced_graphics;

    static
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            use_advanced_graphics = true;
        else
            use_advanced_graphics = prefs.getBoolean(Activator.PLUGIN_ID, "use_advanced_graphics", true, null);
    }

    public static boolean useAdvancedGraphics()
    {
    	if(use_advanced_graphics){
    		String value = System.getProperty("prohibit_advanced_graphics"); //$NON-NLS-1$
    		if(value == null || !value.equals("true")) //$NON-NLS-1$
    			return true;
    		return  false;
    	}
        return false;
    }
}
