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
	/** Names of preferences */
	final public static String
		PREV_INDIVIDUAL = "run_individual_scripts",
		PREF_SCRIPTS = "scripts";
	
	/** @return <code>true</code> when scripts should run for each PV */
	public static boolean getRunIndividualScripts()
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getBoolean(Activator.ID, PREV_INDIVIDUAL, true, null);
	}
	
	/** Get {@link ScriptInfo} entries from preferences
	 *  @return ScriptInfo array
	 *  @throws Exception on error
	 */
	public static ScriptInfo[] getCommandInfos() throws Exception
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String script_list = prefs.getString(Activator.ID, PREF_SCRIPTS, "", null);
        return decode(script_list);
	}
	
	/** @param infos Array of {@link ScriptInfo}
	 *  @return Script infos encoded into one string, suitable for storing as preference
	 */
	public static String encode(final ScriptInfo[] infos)
	{
		final StringBuilder buf = new StringBuilder();
		for (int i=0; i<infos.length; ++i)
		{
			if (i > 0)
				buf.append(',');
			buf.append('"').append(infos[i].getDescription()).append('"')
			   .append('|')
			   .append('"').append(infos[i].getScript()).append('"');
		}
		return buf.toString();
	}
	
	/** @param script_list Encoded script infos, as read from preferences
	 *  @return ScriptInfo array
	 *  @throws Exception on error
	 */
	public static ScriptInfo[] decode(final String script_list) throws Exception
	{
        // Split  description1|command1,description2|command2  at ","
        final String[] desc_scripts = StringSplitter.splitIgnoreInQuotes(script_list, ',', false);
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
