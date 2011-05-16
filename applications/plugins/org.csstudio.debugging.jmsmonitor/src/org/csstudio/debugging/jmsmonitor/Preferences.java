/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences for JMS Monitor
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class Preferences
{
    public static final String JMS_URL = "jms_url";
    public static final String JMS_USER = "jms_user";
    public static final String JMS_PASSWORD = "jms_password";

    /** @return URL of JMS server or <code>null</code> */
    public static String getJMS_URL()
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        return preferences.getString(Activator.ID, JMS_URL, null, null);
    }

    public static String getJMS_User()
    {
       return getSecureString(JMS_USER);
    }

    public static String getJMS_Password()
    {
        return getSecureString(JMS_PASSWORD);
    }
    
    private static String getSecureString(final String setting) {
    	String value = SecureStorage.retrieveSecureStorage(Activator.ID, setting);        	
        return value;
    }
    
}
