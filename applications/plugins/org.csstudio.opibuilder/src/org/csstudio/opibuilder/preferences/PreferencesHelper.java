package org.csstudio.opibuilder.preferences;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**This is the central place for preference related operations.
 * @author Xihui Chen
 *
 */
public class PreferencesHelper {
	public static final String COLOR_FILE = "color_file"; //$NON-NLS-1$
	public static final String FONT_FILE = "font_file"; //$NON-NLS-1$
	public static final String RUN_MACROS= "macros"; //$NON-NLS-1$
	public static final String AUTOSAVE= "auto_save"; //$NON-NLS-1$
	public static final String OPI_GUI_REFRESH_CYCLE = "opi_gui_refresh_cycle"; //$NON-NLS-1$
	public static final String NO_EDIT = "no_edit"; //$NON-NLS-1$
	public static final String TOP_OPIS = "top_opis"; //$NON-NLS-1$
	public static final String DISABLE_ADVANCED_GRAPHICS = "disable_advanced_graphics"; //$NON-NLS-1$
		
	private static final char ROW_SEPARATOR = '|'; //$NON-NLS-1$
	private static final char ITEM_SEPARATOR = ','; //$NON-NLS-1$
	private static final char MACRO_SEPARATOR = '='; //$NON-NLS-1$
	
	
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
    	if(getString(COLOR_FILE) != null)
    		return new Path(getString(COLOR_FILE));
    	return null;
    }
	
    
    /**Get the font file path from preference store.
     * @return the color file path. null if not specified.
     */
    public static IPath getFontFilePath(){
    	if(getString(FONT_FILE) != null)
    		return new Path(getString(FONT_FILE));
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
				CentralLogger.getInstance().error("OPIBuilder.Preference Helper", e); //$NON-NLS-1$
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
					if(!items[i].contains("://") && items[i].contains(":/")) //$NON-NLS-1$
						urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
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
    
	
	
}
