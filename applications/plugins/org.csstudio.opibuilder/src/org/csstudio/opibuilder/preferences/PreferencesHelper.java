/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.preferences;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.java.string.StringUtil;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**This is the central place for preference related operations.
 * @author Xihui Chen
 *
 */
public class PreferencesHelper {

	public enum ConsolePopupLevel {
		NO_POP,
		ONLY_INFO,
		ALL;
	}

	public static final String COLOR_FILE = "color_file"; //$NON-NLS-1$
	public static final String FONT_FILE = "font_file"; //$NON-NLS-1$
	public static final String RUN_MACROS= "macros"; //$NON-NLS-1$
	public static final String AUTOSAVE= "auto_save"; //$NON-NLS-1$
	public static final String OPI_GUI_REFRESH_CYCLE = "opi_gui_refresh_cycle"; //$NON-NLS-1$
	public static final String NO_EDIT = "no_edit"; //$NON-NLS-1$
	public static final String TOP_OPIS = "top_opis"; //$NON-NLS-1$
	public static final String DISABLE_ADVANCED_GRAPHICS = "disable_advanced_graphics"; //$NON-NLS-1$
	public static final String POPUP_CONSOLE = "popup_console"; //$NON-NLS-1$
	public static final String PROBE_OPI = "probe_opi"; //$NON-NLS-1$
	public static final String SCHEMA_OPI = "schema_opi"; //$NON-NLS-1$
	public static final String PYTHON_PATH = "python_path"; //$NON-NLS-1$
	public static final String DISPLAY_SYSTEM_OUTPUT = "display_system_output"; //$NON-NLS-1$
	public static final String SHOW_COMPACT_MODE_DIALOG = "show_compact_mode_dialog";
	public static final String SHOW_FULLSCREEN_DIALOG = "show_fullscreen_dialog";
	public static final String START_WINDOW_IN_COMPACT_MODE = "start_window_in_compact_mode";
	public static final String URL_FILE_LOADING_TIMEOUT = "url_file_loading_timeout";
	

	private static final char ROW_SEPARATOR = '|'; 
	private static final char ITEM_SEPARATOR = ','; 
	private static final char MACRO_SEPARATOR = '='; 


	 /** @param preferenceName Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String preferenceName)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(OPIBuilderPlugin.PLUGIN_ID, preferenceName, null, null);
    }


    /**Get the color file path from preference store.
     * @return the color file path. null if not specified.
     */
    public static IPath getColorFilePath(){
    	String colorFilePath = getString(COLOR_FILE);
    	if(colorFilePath != null){
    		if(ResourceUtil.isURL(colorFilePath))
    			return new URLPath(colorFilePath);
    		else
    			return new Path(colorFilePath);
    	}
    	return null;
    }


    /**Get the font file path from preference store.
     * @return the color file path. null if not specified.
     */
    public static IPath getFontFilePath(){
    	String fontFilePath = getString(FONT_FILE);
    	if(fontFilePath != null){
    		if(ResourceUtil.isURL(fontFilePath))
    			return new URLPath(fontFilePath);
    		else
    			return new Path(fontFilePath);
    	}
    	return null;
    }


    /**Get the probe OPI path from preference store.
     * @return the probe OPI path. null if not specified.
     */
    public static IPath getProbeOPIPath(){
    	String probeOPIPath = getString(PROBE_OPI);
    	if(probeOPIPath != null){
    		if(ResourceUtil.isURL(probeOPIPath))
    			return new URLPath(probeOPIPath);
    		else
    			return new Path(probeOPIPath);
    	}
    	return null;
    }
    
    /**Get the schema OPI path from preference store.
     * @return the schema OPI path. null if not specified.
     */
    public static IPath getSchemaOPIPath(){
    	String schemaOPIPath = getString(SCHEMA_OPI);
    	if(schemaOPIPath != null){
    		if(ResourceUtil.isURL(schemaOPIPath))
    			return new URLPath(schemaOPIPath);
    		else
    			return new Path(schemaOPIPath);
    	}
    	return null;
    }

    public static boolean isAutoSaveBeforeRunning(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, AUTOSAVE, false, null);
    }

    public static boolean isNoEdit(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, NO_EDIT, false, null);
    }

    public static ConsolePopupLevel getConsolePopupLevel(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	String popupLevelString = service.getString(
    			OPIBuilderPlugin.PLUGIN_ID, POPUP_CONSOLE, ConsolePopupLevel.ALL.toString(), null);
    	return ConsolePopupLevel.valueOf(popupLevelString);
    }


    public static boolean isAdvancedGraphicsDisabled(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, DISABLE_ADVANCED_GRAPHICS, false, null);
    }

    public static Integer getGUIRefreshCycle(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getInt(OPIBuilderPlugin.PLUGIN_ID, OPI_GUI_REFRESH_CYCLE, 100, null);
    }

    /**Get the macros map from preference store.
     * @return the macros map. null if failed to get macros from preference store.
     */
    public static LinkedHashMap<String, String> getMacros(){
    	if(getString(RUN_MACROS) != null){
    		try {
    			LinkedHashMap<String, String> macros = new LinkedHashMap<String, String>();
				List<String[]> items = StringTableFieldEditor.decodeStringTable(getString(RUN_MACROS));
				for(String[] item : items){
					if(item.length == 2)
						macros.put(item[0], item[1]);
				}
				return macros;

			} catch (Exception e) {
                OPIBuilderPlugin.getLogger().log(Level.WARNING, "Macro error", e); //$NON-NLS-1$
				return new LinkedHashMap<String, String>();
			}
    	}
    	return new LinkedHashMap<String, String>();

    }

    public static Map<IPath, MacrosInput> getTopOPIs() throws Exception{
    	String rawString = getString(TOP_OPIS);
    	if(rawString == null)
    		return null;
    	Map<IPath, MacrosInput> result = new LinkedHashMap<IPath, MacrosInput>();
    	String[] rows = StringUtil.splitIgnoreInQuotes(rawString, ROW_SEPARATOR, false);
		for(String rowString : rows){
			String[] items = StringUtil.splitIgnoreInQuotes(rowString, ITEM_SEPARATOR, true);
			IPath path = null;
			MacrosInput macrosInput = new MacrosInput(new LinkedHashMap<String, String>(), true);
			for(int i= 0; i<items.length; i++){
				if(i == 0){
					String urlString = items[i];
					if(ResourceUtil.isURL(items[i])) 
						path = new URLPath(items[i]);
					else
						path = new Path(urlString);
				}
				else{
					String[] macro = StringUtil.splitIgnoreInQuotes(items[i], MACRO_SEPARATOR, true);
					if(macro.length == 2)
						macrosInput.getMacrosMap().put(macro[0], macro[1]);
				}
			}
			if(path != null)
				result.put(path, macrosInput);
		}
		return result;

    }
    
    /**Get python path preferences.
     * @return the python path, null if this preference is not setted.
     * @throws Exception
     */
    public static String getPythonPath() throws Exception {
    	String rawString = getString(PYTHON_PATH);
    	if(rawString == null || rawString.isEmpty())
    		return null;
    	String[] rawPaths = StringUtil.splitIgnoreInQuotes(rawString, ROW_SEPARATOR, true);
    	StringBuilder sb = new StringBuilder();
    	for(String rawPath : rawPaths){
    		IPath path = new Path(rawPath);
    		IPath location = ResourceUtil.workspacePathToSysPath(path);
    		if(location != null){
    			sb.append(location.toOSString());
    		}else{
    			sb.append(rawPath);
    		}    		
			sb.append(System.getProperty("path.separator"));			
    	}
    	sb.deleteCharAt(sb.length()-1);
    	return sb.toString();
    	
    }
    
    public static boolean isDisplaySystemOutput(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, DISPLAY_SYSTEM_OUTPUT, false, null);
    }

    public static boolean isShowCompactModeDialog(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, SHOW_COMPACT_MODE_DIALOG, true, null);
    }
    
    public static void setShowCompactModeDialog(boolean show){    	
    	putBoolean(SHOW_COMPACT_MODE_DIALOG, show);    	
    }   
    
    public static boolean isShowFullScreenDialog(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, SHOW_FULLSCREEN_DIALOG, true, null);
    }
    
    public static void setShowFullScreenDialog(boolean show){
    	putBoolean(SHOW_FULLSCREEN_DIALOG, show);
    }
    
     public static boolean isStartWindowInCompactMode(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, START_WINDOW_IN_COMPACT_MODE, false, null);
    }
     
    private static void putBoolean(String name, boolean value){
    	IEclipsePreferences prefs = new InstanceScope().getNode(OPIBuilderPlugin.PLUGIN_ID);
    	prefs.putBoolean(name, value);
    	try {
			prefs.flush();
		} catch (BackingStoreException e) {
			OPIBuilderPlugin.getLogger().log(Level.SEVERE, "Failed to store preferences.", e);
		}	
    }
    
    public static int getURLFileLoadingTimeout(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getInt(OPIBuilderPlugin.PLUGIN_ID, URL_FILE_LOADING_TIMEOUT, 8000, null);
    }


}
