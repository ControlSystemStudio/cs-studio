/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.rap.core.preferences;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.java.string.StringSplitter;
import org.csstudio.rap.core.RAPCorePlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * This is the central place for preference related operations.
 * 
 * @author Xihui Chen
 * 
 */
public class PreferenceHelper {

	private static final String DEFAULT_LOGIN_MODULE_EXT_ID = "org.csstudio.rap.core.defaultLoginModule"; //$NON-NLS-1$

	final public static String LOGIN_MODULE_EXTENSION_ID = "login_module_extension_id"; //$NON-NLS-1$

	final public static String LOGIN_MODULE_OPTIONS = "login_module_options"; //$NON-NLS-1$

	private static final char ROW_SEPARATOR = ',';  //$NON-NLS-1$
	private static final char ITEM_SEPARATOR = '='; //$NON-NLS-1$
	
	/**
	 * @param preferenceName
	 *            Preference identifier
	 * @return String from preference system, or <code>null</code>
	 */
	protected static String getString(final String preferenceName,
			final String defaultValue) {
		final IPreferencesService service = Platform.getPreferencesService();
		return service.getString(RAPCorePlugin.PLUGIN_ID, preferenceName,
				defaultValue, null);
	}

	/**
	 * @return extension id of the login module.
	 */
	public static String getLoginModuleExtensionId() {
		return getString(LOGIN_MODULE_EXTENSION_ID, DEFAULT_LOGIN_MODULE_EXT_ID);
	}
	
    /**Get login module options from preference store.
     * @return the options map. Empty if failed to get macros from preference store.
     */
    public static LinkedHashMap<String, String> getLoginModuleOptions(){
    	String optionsString = getString(LOGIN_MODULE_OPTIONS, null);
    	if(optionsString != null){
    		try {
    			LinkedHashMap<String, String> optionsMap = new LinkedHashMap<String, String>();
				List<String[]> items = decodeStringTable(optionsString);
				for(String[] item : items){
					if(item.length == 2)
						optionsMap.put(item[0], item[1]);
				}
				return optionsMap;

			} catch (Exception e) {
                RAPCorePlugin.getLogger().log(Level.WARNING, "login module options error", e); //$NON-NLS-1$
				return new LinkedHashMap<String, String>();
			}
    	}
    	return new LinkedHashMap<String, String>();

    }
    
	public static List<String[]> decodeStringTable(final String flattedString) throws Exception{
		final List<String[]> result = new ArrayList<String[]>();
		final String[] rows = StringSplitter.splitIgnoreInQuotes(flattedString, ROW_SEPARATOR, false);
		for(String rowString : rows){
		    // Skip empty rowString, don't split it into String[1] { "" }
		    if (rowString.length() <= 0)
		        continue;
			final String[] items = StringSplitter.splitIgnoreInQuotes(rowString, ITEM_SEPARATOR, true);
			result.add(items);
		}
		return result;
	}

}
