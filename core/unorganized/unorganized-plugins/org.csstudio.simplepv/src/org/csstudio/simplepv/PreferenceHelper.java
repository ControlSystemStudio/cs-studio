/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**The preference helper for simplepv.
 * @author Xihui Chen
 *
 */
public class PreferenceHelper {

    final public static String DEFAULT_PVFACTORY="default_pvfactory"; //$NON-NLS-1$


     /** @param preferenceName Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String preferenceName)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(SimplePVPlugin.PLUGIN_ID, preferenceName, null, null);
    }

    public static String getDefaultPVFactoryID(){
        return getString(DEFAULT_PVFACTORY);
    }

}
