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

import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
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
	public static final String SHOW_COMPACT_MODE_DIALOG = "show_compact_mode_dialog";//$NON-NLS-1$
	public static final String SHOW_FULLSCREEN_DIALOG = "show_fullscreen_dialog";//$NON-NLS-1$
	public static final String SHOW_OPI_RUNTIME_PERSPECTIVE_DIALOG = "show_opi_runtime_perspective_dialog";//$NON-NLS-1$
	public static final String START_WINDOW_IN_COMPACT_MODE = "start_window_in_compact_mode";//$NON-NLS-1$
	public static final String URL_FILE_LOADING_TIMEOUT = "url_file_loading_timeout";//$NON-NLS-1$
	public static final String OPI_SEARCH_PATH="opi_search_path"; //$NON-NLS-1$
	//The widgets that are hidden from palette.
	public static final String HIDDEN_WIDGETS="hidden_widgets"; //$NON-NLS-1$
	
//WebOPI preferences
	
	public static final String OPI_REPOSITORY = "opi_repository"; //$NON-NLS-1$
	public static final String STARTUP_OPI = "startup_opi"; //$NON-NLS-1$
	public static final String MOBILE_STARTUP_OPI = "mobile_startup_opi"; //$NON-NLS-1$
	
	private static final char ROW_SEPARATOR = '|'; 
	private static final char ITEM_SEPARATOR = ','; 
	private static final char MACRO_SEPARATOR = '='; 
	


	 /** @param preferenceName Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    protected static String getString(final String preferenceName)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(OPIBuilderPlugin.PLUGIN_ID, preferenceName, null, null);
    }


    /**Get the color file path from preference store.
     * @return the color file path. null if not specified.
     */
    public static IPath getColorFilePath(){
    	String colorFilePath = getString(COLOR_FILE);
    	if(colorFilePath == null || colorFilePath.trim().isEmpty())
    		return null;
    	return getAbsolutePathOnRepo(colorFilePath);
    }


    /**Get the font file path from preference store.
     * @return the color file path. null if not specified.
     */
    public static IPath getFontFilePath(){
    	String fontFilePath = getString(FONT_FILE);
    	if(fontFilePath == null || fontFilePath.trim().isEmpty())
    		return null;
    	return getAbsolutePathOnRepo(fontFilePath);
    }


    /**Get the probe OPI path from preference store.
     * @return the probe OPI path. null if not specified.
     */
    public static IPath getProbeOPIPath(){
    	String probeOPIPath = getString(PROBE_OPI);
     	if(probeOPIPath == null || probeOPIPath.trim().isEmpty())
    		return null;
    	return getExistFileInRepoAndSearchPath(probeOPIPath);
    }
    
    /**Get the schema OPI path from preference store.
     * @return the schema OPI path. null if not specified.
     */
    public static IPath getSchemaOPIPath(){
    	String schemaOPIPath = getString(SCHEMA_OPI);
    	if(schemaOPIPath == null || schemaOPIPath.trim().isEmpty())
    		return null;
    	return getExistFileInRepoAndSearchPath(schemaOPIPath);
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
    	String[] rows = StringSplitter.splitIgnoreInQuotes(rawString, ROW_SEPARATOR, false);
		for(String rowString : rows){
			String[] items = StringSplitter.splitIgnoreInQuotes(rowString, ITEM_SEPARATOR, true);
			IPath path = null;
			MacrosInput macrosInput = new MacrosInput(new LinkedHashMap<String, String>(), true);
			for(int i= 0; i<items.length; i++){
				if(i == 0){
					String urlString = items[i];
					path = getExistFileInRepoAndSearchPath(urlString);
				}
				else{
					String[] macro = StringSplitter.splitIgnoreInQuotes(items[i], MACRO_SEPARATOR, true);
					if(macro.length == 2)
						macrosInput.getMacrosMap().put(macro[0], macro[1]);
				}
			}
			if(path != null)
				result.put(path, macrosInput);
		}
		return result;
    }
    
    /**
     * @return the OPI search paths preference. null if no such preference was set.
     * @throws Exception
     */
    public static IPath[] getOPISearchPaths() throws Exception{
    	String rawString = getString(OPI_SEARCH_PATH);
    	if(rawString == null || rawString.trim().isEmpty())
    		return null;
    	String[] rows = StringSplitter.splitIgnoreInQuotes(rawString, ROW_SEPARATOR, true);
    	IPath[] result = new IPath[rows.length];
    	int i=0;
    	for(String row:rows){
    		result[i++]=ResourceUtil.getPathFromString(row);
    	}
    	return result;
    }
    
    /**
     * @return typeId of widgets that should be hidden from palette.
     * @throws Exception
     */
    public static String[] getHiddenWidgets(){
    	String rawString = getString(HIDDEN_WIDGETS);
    	if(rawString == null || rawString.trim().isEmpty())
    		return null;
    	try {
			return StringSplitter.splitIgnoreInQuotes(rawString, ROW_SEPARATOR, true);
		} catch (Exception e) {
			ErrorHandlerUtil.handleError("Failed to parse hidden_widgets preference", e);
			return null;
		}
    	
    }
    
    /**Get python path preferences.
     * @return the python path, null if this preference is not setted.
     * @throws Exception
     */
    public static String getPythonPath() throws Exception {
    	String rawString = getString(PYTHON_PATH);
    	if(rawString == null || rawString.isEmpty())
    		return null;
    	String[] rawPaths = StringSplitter.splitIgnoreInQuotes(rawString, ROW_SEPARATOR, true);
    	StringBuilder sb = new StringBuilder();
    	for(String rawPath : rawPaths){
    		IPath path = new Path(rawPath);
    		IPath location = ResourceUtil.workspacePathToSysPath(path);
    		if(location != null){
    			sb.append(location.toOSString());
    		}else{
    			sb.append(rawPath);
    		}    		
			sb.append(System.getProperty("path.separator"));	//$NON-NLS-1$	
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
    
    public static boolean isShowOpiRuntimePerspectiveDialog(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, SHOW_OPI_RUNTIME_PERSPECTIVE_DIALOG, true, null);
    }
    
    public static void setShowOpiRuntimePerspectiveDialog(boolean show){
    	putBoolean(SHOW_OPI_RUNTIME_PERSPECTIVE_DIALOG, show);
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
    
    /**
     * @return the absolute path of the startup opi. null if not configured.
     */
    public static IPath getStartupOPI(){
    	String startOPI = getString(STARTUP_OPI);
    	if(startOPI == null || startOPI.trim().isEmpty())
    		return null;
    	return getExistFileInRepoAndSearchPath(startOPI);
    }
    
    /**
     * @return the absolute path of the startup opi. null if not configured.
     */
    public static IPath getMobileStartupOPI(){
    	String startOPI = getString(MOBILE_STARTUP_OPI);
    	if(startOPI == null || startOPI.trim().isEmpty())
    		return null;
    	return getExistFileInRepoAndSearchPath(startOPI);
    }

    public static IPath getOPIRepository(){
    	String opiRepo = getString(OPI_REPOSITORY);
    	if(opiRepo == null || opiRepo.trim().isEmpty())
    		return null;
    	return ResourceUtil.getPathFromString(opiRepo);       
    }  
   

    /**Return the absolute path based on OPI Repository.
	 * @param pathString
	 * @return
	 */
	protected static IPath getAbsolutePathOnRepo(String pathString) {
		IPath opiPath = ResourceUtil.getPathFromString(pathString);
		if(opiPath == null)
			return null;
    	if(opiPath.isAbsolute())
    		return opiPath;
    	IPath repoPath = getOPIRepository();
    	if(repoPath != null)
    		opiPath = repoPath.append(opiPath);
    	return opiPath;
	}
	
	protected static IPath getExistFileInRepoAndSearchPath(String pathString){
		IPath originPath = ResourceUtil.getPathFromString(pathString);
		IPath opiPath = originPath;
		if(opiPath == null)
			return null;
    	if(opiPath.isAbsolute())
    		return opiPath;
    	IPath repoPath = getOPIRepository();
    	if(repoPath != null){
    		opiPath = repoPath.append(originPath);
    		if(ResourceUtil.isExsitingFile(opiPath, true))
    			return opiPath;
    	}
    	IPath sPath = ResourceUtil.getFileOnSearchPath(originPath, true);
    	if(sPath == null)
    		return opiPath;
    	return sPath;		
	}

}
