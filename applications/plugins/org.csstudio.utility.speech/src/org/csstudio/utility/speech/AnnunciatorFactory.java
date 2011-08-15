/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Annunciator factory
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AnnunciatorFactory
{
    /** Get Annunciator
     *  Preferences determine which one it will be
     *  @return Annunciator
     *  @throws Exception on error
     */
    public static Annunciator getAnnunciator() throws Exception
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return new FreeTTS_JSAPI_Annunciator();
        
        final String type = prefs.getString(Plugin.ID, "annunciator", "JSAPI", null);
        
        if ("JSAPI".equalsIgnoreCase(type))
            return new FreeTTS_JSAPI_Annunciator();
        if ("FreeTTS".equalsIgnoreCase(type))
            return new FreeTTSAnnunciator();
        if ("UDP".equalsIgnoreCase(type))
        {
        	final String host = prefs.getString(Plugin.ID, "host", "", null);
        	final int port = prefs.getInt(Plugin.ID, "port", 6543, null);
        	return new UDPAnnunciator(host, port);
        }

        return new ExternalAnnunciator();
    }
}
