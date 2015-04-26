/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.ui;

import org.csstudio.opibuilder.converter.EDM2OPIConverterPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**This is the central place for preference related operations.
 * @author Xihui Chen
 *
 */
public class PreferencesHelper {
	public static final String EDM_COLORLIST_FILE = "edm_colorlist_file"; //$NON-NLS-1$
	public static final String OUTPUT_OPICOLOR_FILE = "output_opicolor_file"; //$NON-NLS-1$
	public static final String OUTPUT_OPIS_FOLDER = "output_opis_folder"; //$NON-NLS-1$
	public static final String FAIL_FAST = "robust_parsing"; //$NON-NLS-1$
	public static final String OPEN_OPIS = "open_opis"; //$NON-NLS-1$
	 /** @param preferenceName Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String preferenceName)
    {
        final IPreferencesService service = Platform.getPreferencesService();        
        return service.getString(EDM2OPIConverterPlugin.PLUGIN_ID, preferenceName, null, null);
    }
    
    
    /**Get the EDM colors.list file path from preference store.
     * @return the colors.list file path. null if not specified.
     */
    public static IPath getEDMColorListFilePath(){
    	if(getString(EDM_COLORLIST_FILE) != null)
    		return new Path(getString(EDM_COLORLIST_FILE));
    	return null;
    }
	
    /**Get the output opi color definition file path from preference store.
     * @return the opi color definition file path. null if not specified.
     */
    public static IPath getOutputOPIColorFilePath(){
    	if(getString(OUTPUT_OPICOLOR_FILE) != null)
    		return new Path(getString(OUTPUT_OPICOLOR_FILE));
    	return null;
    }   
    
    public static boolean isRobustParsing(){
    	final IPreferencesService service = Platform.getPreferencesService();
    	return !service.getBoolean(EDM2OPIConverterPlugin.PLUGIN_ID, FAIL_FAST, false, null);
    }
    	
}
