/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets;

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
	
	public final static String PROHIBIT_ADVANCED_GRAPHICS = "org.csstudio.swt.widget.prohibit_advanced_graphics";
	
	public final static String URL_FILE_LOAD_TIMEOUT = "org.csstudio.swt.widget.url_file_load_timeout";

	
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
        return use_advanced_graphics;
    }
    
    public static int getURLFileLoadTimeout(){
    	 final IPreferencesService prefs = Platform.getPreferencesService();
         if (prefs == null)
        	 return 5000;
         else
        	 return prefs.getInt(
            		 Activator.PLUGIN_ID, "url_file_load_timeout", 5000, null); //$NON-NLS-1$
    }
}
