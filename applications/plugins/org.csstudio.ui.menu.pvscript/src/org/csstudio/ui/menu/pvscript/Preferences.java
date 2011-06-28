/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.csstudio.java.string.StringSplitter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read and decode preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
	/** Get {@link ScriptInfo} entries from preferences
	 *  @return ScriptInfo array
	 *  @throws Exception on error
	 */
	public static ScriptInfo[] getCommandInfos() throws Exception
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String script_list = prefs.getString(Activator.ID, "scripts", "", null);
        
        // Split  description1|command1,description2|command2  at ","
        final String[] desc_scripts = StringSplitter.splitIgnoreInQuotes(script_list, ',', true);
        final ScriptInfo[] infos = new ScriptInfo[desc_scripts.length];
        for (int i=0; i<infos.length; ++i)
        {
        	// Split description1|command1 at "|"
            final String[] desc_script = StringSplitter.splitIgnoreInQuotes(desc_scripts[i], '|', true);
        	if (desc_script.length != 2)
        		throw new Exception("Error in preference " + Activator.ID + "/scripts");
        	infos[i] = new ScriptInfo(desc_script[0], desc_script[1]);
        }
		return infos;
	}
}
