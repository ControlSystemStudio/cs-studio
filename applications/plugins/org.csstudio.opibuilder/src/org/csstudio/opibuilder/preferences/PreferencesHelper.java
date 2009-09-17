package org.csstudio.opibuilder.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**This is the central place for preference related operations.
 * @author Xihui Chen
 *
 */
public class PreferencesHelper {
	public static final String OPI_COLOR_FILE = "opi_color_file"; //$NON-NLS-1$
	public static final String OPI_FONT_FILE = "opi_font_file"; //$NON-NLS-1$
	public static final String OPI_RUN_MACROS= "opi_run_macros"; //$NON-NLS-1$
	public static final String OPI_AUTOSAVE= "opi_autosave"; //$NON-NLS-1$

	
	 /** @param preferenceName Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String preferenceName)
    {
        final IPreferencesService service = Platform.getPreferencesService();        
        return service.getString(OPIBuilderPlugin.PLUGIN_ID, preferenceName, null, null);
    }
    
    
    /**Get the color file path from preference stroe.
     * @return the color file path. null if not specified.
     */
    public static IPath getColorFilePath(){
    	if(getString(OPI_COLOR_FILE) != null)
    		return new Path(getString(OPI_COLOR_FILE));
    	return null;
    }
	
    
    public static boolean isAutoSaveBeforeRunning(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return service.getBoolean(OPIBuilderPlugin.PLUGIN_ID, OPI_AUTOSAVE, false, null);
    }
    
    /**Get the macros map from preference store.
     * @return the macros map. null if failed to get macros from preference store.
     */
    public static Map<String, String> getMacros(){
    	if(getString(OPI_RUN_MACROS) != null){
    		try {
    			Map<String, String> macros = new HashMap<String, String>();
				List<String[]> items = StringTableFieldEditor.decodeStringTable(getString(OPI_RUN_MACROS));
				for(String[] item : items){
					if(item.length == 2)
						macros.put(item[0], item[1]);
				}
				return macros;
				
			} catch (Exception e) {
				CentralLogger.getInstance().error("OPIBuilder.Preference Helper", e); //$NON-NLS-1$
				return new HashMap<String, String>();
			}    		
    	}
    	return new HashMap<String, String>();
    	
    }
	
	
}
